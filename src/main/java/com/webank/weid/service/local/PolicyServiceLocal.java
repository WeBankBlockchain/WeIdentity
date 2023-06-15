package com.webank.weid.service.local;

import com.webank.weid.blockchain.constant.ChainType;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.constant.WeIdConstant;
import com.webank.weid.blockchain.protocol.base.Cpt;
import com.webank.weid.blockchain.protocol.base.CptBaseInfo;
import com.webank.weid.blockchain.protocol.base.PresentationPolicyE;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.blockchain.protocol.response.RsvSignature;
import com.webank.weid.blockchain.rpc.PolicyService;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.protocol.base.GlobalStatus;
import com.webank.weid.suite.persistence.*;
import com.webank.weid.util.PropertyUtils;
import com.webank.weid.util.WeIdUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("policyServiceLocal")
public class PolicyServiceLocal implements PolicyService {
    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceLocal.class);

    private static Persistence dataDriver;
    private static PersistenceType persistenceType;
    WeIdServiceLocal weIdServiceLocal = new WeIdServiceLocal();
    AuthorityIssuerServiceLocal authorityIssuerServiceLocal = new AuthorityIssuerServiceLocal();
    CptServiceLocal cptServiceLocal = new CptServiceLocal();

    private static Persistence getDataDriver() {
        String type = PropertyUtils.getProperty("persistence_type");
        if (type.equals("mysql")) {
            persistenceType = PersistenceType.Mysql;
        } else if (type.equals("redis")) {
            persistenceType = PersistenceType.Redis;
        }
        if (dataDriver == null) {
            dataDriver = PersistenceFactory.build(persistenceType);
        }
        return dataDriver;
    }

    /**
     * Put Claim Policy List on blockchain under a CPT ID.
     *
     * @param cptId CPT ID
     * @param policies Policy list
     * @param privateKey privateKey of cpt issuer
     * @return claimPolicyId the Claim policy ID on-chain
     */
    @Override
    public ResponseData<Integer> putPolicyIntoCpt(Integer cptId, List<Integer> policies,
                                                  String privateKey) {
        if(getDataDriver().getCpt(DataDriverConstant.LOCAL_CPT, cptId).getResult() == null){
            logger.error("[putPolicyIntoCpt] cpt not exist on chain");
            return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
        }
        String policyString = policies.get(0).toString();
        if(policies.size()>1){
            for(int i = 1; i < policies.size(); i++){
                policyString = policyString + "," + policies.get(i).toString();
            }
        }
        ResponseData<Integer> resp =
                getDataDriver().updateCptClaimPolicies(
                        DataDriverConstant.LOCAL_CPT,
                        cptId,
                        policyString);
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[putPolicyIntoCpt] updateCptClaimPolicies to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(resp.getResult(), ErrorCode.SUCCESS);
    }

    /**
     * Register Claim Policy on blockchain.
     *
     * @param address address of issuer
     * @param cptJsonSchemaNew cptJsonSchema
     * @param rsvSignature signature of issuer
     * @param privateKey privateKey of issuer
     * @return claimPolicyId the Claim policy ID on-chain
     */
    @Override
    public ResponseData<Integer> registerPolicyData(
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature,
            String privateKey) {
        if (StringUtils.isEmpty(address) || StringUtils.isEmpty(cptJsonSchemaNew) || StringUtils.isEmpty(privateKey)) {
            logger.error("[registerPolicyData] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        //如果不存在该weId则报错
        if(!weIdServiceLocal.isWeIdExist(WeIdUtils.convertAddressToWeId(address)).getResult()){
            logger.error("[registerPolicyData] the weid of publisher does not exist on blockchain");
            return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
        }
        int policyId = getPolicyId(address);

        ResponseData<Integer> resp =
                getDataDriver().addPolicy(
                        DataDriverConstant.LOCAL_POLICY,
                        policyId,
                        address,
                        null,
                        cptJsonSchemaNew,
                        com.webank.weid.blockchain.util.DataToolUtils.SigBase64Serialization(rsvSignature));
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[registerPolicyData] save policy to db failed.");
            throw new DatabaseException("database error!");
        }
        if (resp.getResult() != null && resp.getResult() > 0) {
            return new ResponseData<>(policyId, ErrorCode.SUCCESS);
        } else {
            return new ResponseData<>(-1, ErrorCode.UNKNOW_ERROR);
        }
    }

    public int getPolicyId(String address) {
        GlobalStatus globalStatus = GlobalStatus.readStatusFromFile("global.status");
        if(authorityIssuerServiceLocal.isAuthorityIssuer(address).getResult()){
            int policyId = globalStatus.getAuthority_issuer_current_policy_id();
            while (getDataDriver().getPolicy(DataDriverConstant.LOCAL_POLICY, policyId).getResult() != null) {
                policyId++;
            }
            globalStatus.setAuthority_issuer_current_policy_id(policyId);
            GlobalStatus.storeStatusToFile(globalStatus, "global.status");
            if(policyId > CptServiceLocal.NONE_AUTHORITY_ISSUER_START_ID) policyId = 0;
            return policyId;
        } else {
            int policyId = globalStatus.getNone_authority_issuer_current_policy_id();
            while (getDataDriver().getPolicy(DataDriverConstant.LOCAL_POLICY, policyId).getResult() != null) {
                policyId++;
            }
            globalStatus.setNone_authority_issuer_current_policy_id(policyId);
            GlobalStatus.storeStatusToFile(globalStatus, "global.status");
            return policyId;
        }
    }

    /**
     * Get Claim Policy Json from blockchain given a policy ID.
     *
     * @param policyId the Claim Policy ID on-chain
     * @return the claim Json
     */
    @Override
    public ResponseData<String> getClaimPolicy(Integer policyId) {
        try {
            PolicyValue policyValue = getDataDriver().getPolicy(DataDriverConstant.LOCAL_POLICY, policyId).getResult();
            if(policyValue == null){
                logger.error("[getClaimPolicy] policy not exist on chain");
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.CPT_NOT_EXISTS);
            }
            return new ResponseData<>(policyValue.getPolicy_schema(), ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[getClaimPolicy] execute failed. Error message :{}", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    /**
     * Get all claim policies from this CPT ID.
     *
     * @param cptId cpt id
     * @return claim policies list
     */
    @Override
    public ResponseData<List<Integer>> getClaimPoliciesFromCpt(Integer cptId) {
        CptValue cptValue = getDataDriver().getCpt(DataDriverConstant.LOCAL_CPT, cptId).getResult();
        if(cptValue == null){
            logger.error("[getClaimPoliciesFromCpt] cpt not exist on chain");
            return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
        }
        if(cptValue.getClaim_policies() == null){
            return new ResponseData<>(null, ErrorCode.SUCCESS);
        }
        String[] policies = cptValue.getClaim_policies().split(",");
        List<Integer> result = new ArrayList<>();
        for (String obj : policies) {
            result.add(Integer.valueOf(obj));
        }
        return new ResponseData<>(result, ErrorCode.SUCCESS);
    }

    /**
     * Register Presentation Policy which contains a number of claim policies.
     *
     * @param claimPolicyIdList claim policies list
     * @param privateKey privateKey of weid
     * @return the presentation policy id
     */
    @Override
    public ResponseData<Integer> registerPresentationPolicy(List<Integer> claimPolicyIdList,
                                                            String privateKey) {
        int presentationId = getPresentationId();
        String policyString = claimPolicyIdList.get(0).toString();
        if(claimPolicyIdList.size()>1){
            for(int i = 1; i < claimPolicyIdList.size(); i++){
                policyString = policyString + "," + claimPolicyIdList.get(i).toString();
            }
        }
        ResponseData<Integer> resp =
                getDataDriver().addPresentation(
                        DataDriverConstant.LOCAL_PRESENTATION,
                        presentationId,
                        WeIdUtils.getWeIdFromPrivateKey(privateKey),
                        policyString);
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[registerPresentationPolicy] save presentation to db failed.");
            throw new DatabaseException("database error!");
        }
        if (resp.getResult() != null && resp.getResult() > 0) {
            return new ResponseData<>(resp.getResult(), ErrorCode.SUCCESS);
        } else {
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    public int getPresentationId() {
        GlobalStatus globalStatus = GlobalStatus.readStatusFromFile("global.status");
        int presentationId = globalStatus.getPresentationId();
        while (getDataDriver().getPresentation(DataDriverConstant.LOCAL_PRESENTATION, presentationId).getResult() != null) {
            presentationId++;
        }
        globalStatus.setPresentationId(presentationId);
        GlobalStatus.storeStatusToFile(globalStatus, "global.status");
        return presentationId;
    }

    /**
     * Get Presentation policies under this id from chain.
     *
     * @param presentationPolicyId presentation policy id
     * @return the full presentation policy
     */
    @Override
    public ResponseData<PresentationPolicyE> getPresentationPolicy(Integer presentationPolicyId) {
        PresentationValue presentationValue = getDataDriver().getPresentation(DataDriverConstant.LOCAL_PRESENTATION, presentationPolicyId).getResult();
        if(presentationValue == null){
            logger.error("[getPresentationPolicy] presentation not exist on chain");
            return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
        }
        if(presentationValue.getClaim_policies().equals(StringUtils.EMPTY)){
            logger.error("[getPresentationPolicy] presentation does not have policies");
            return new ResponseData<>(null, ErrorCode.POLICY_SERVICE_NOT_EXISTS);
        }
        String[] policies = presentationValue.getClaim_policies().split(",");
        List<Integer> result = new ArrayList<>();
        for (String obj : policies) {
            result.add(Integer.valueOf(obj));
        }
        PresentationPolicyE presentationPolicy = new PresentationPolicyE();
        presentationPolicy.setId(presentationValue.getPresentation_id());
        Map<Integer, String> policyMap = new HashMap<>();
        for (Integer id : result) {
            policyMap.put(id, getClaimPolicy(id).getResult());
        }
        presentationPolicy.setPolicy(policyMap);
        presentationPolicy.setPolicyPublisherWeId(presentationValue.getCreator());
        return new ResponseData<>(presentationPolicy, ErrorCode.SUCCESS);
    }

    /**
     * Get all claim policies from chain.
     *
     * @param startPos start position
     * @param num batch number
     * @return claim policy list
     */
    @Override
    public ResponseData<List<Integer>> getAllClaimPolicies(Integer startPos, Integer num) {
        try {
            return getDataDriver().getPolicyIdList(
                    DataDriverConstant.LOCAL_POLICY,
                    startPos,
                    startPos + num);
        } catch (Exception e) {
            logger.error("[getAllClaimPolicies] getAllClaimPolicies has error, Error Message：{}", e);
            return new ResponseData<>(null, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    @Override
    public ResponseData<Integer> getPolicyCount() {
        try {
            return getDataDriver().getPolicyCount(DataDriverConstant.LOCAL_POLICY);
        } catch (Exception e) {
            logger.error("[getPolicyCount] getPolicyCount has error, Error Message：{}", e);
            return new ResponseData<>(0, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }
}
