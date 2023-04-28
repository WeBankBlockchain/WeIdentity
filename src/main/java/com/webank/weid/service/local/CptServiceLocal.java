package com.webank.weid.service.local;

import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;
import com.webank.wedpr.selectivedisclosure.proto.AttributeTemplate;
import com.webank.wedpr.selectivedisclosure.proto.TemplatePublicKey;
import com.webank.weid.blockchain.constant.ChainType;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.constant.WeIdConstant;
import com.webank.weid.blockchain.protocol.base.Cpt;
import com.webank.weid.blockchain.protocol.base.CptBaseInfo;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.blockchain.protocol.response.RsvSignature;
import com.webank.weid.blockchain.rpc.CptService;
import com.webank.weid.blockchain.service.impl.CptServiceImpl;
import com.webank.weid.util.JsonUtil;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.protocol.base.GlobalStatus;
import com.webank.weid.service.local.role.RoleController;
import com.webank.weid.suite.persistence.CptValue;
import com.webank.weid.suite.persistence.Persistence;
import com.webank.weid.suite.persistence.PersistenceFactory;
import com.webank.weid.suite.persistence.PersistenceType;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.PropertyUtils;
import com.webank.weid.util.WeIdUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("cptServiceLocal")
public class CptServiceLocal implements CptService {
    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(CptServiceLocal.class);

    private static Persistence dataDriver;
    private static PersistenceType persistenceType;
    public static Integer AUTHORITY_ISSUER_START_ID = 1000;
    public static Integer NONE_AUTHORITY_ISSUER_START_ID = 2000000;
    WeIdServiceLocal weIdServiceLocal = new WeIdServiceLocal();
    AuthorityIssuerServiceLocal authorityIssuerServiceLocal = new AuthorityIssuerServiceLocal();

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
     * Register a new CPT with a pre-set CPT ID, to the blockchain.
     *
     * @param address the address of creator
     * @param cptJsonSchemaNew the new cptJsonSchema
     * @param rsvSignature the rsvSignature of cptJsonSchema
     * @param privateKey the decimal privateKey of creator
     * @param cptId the CPT ID
     * @return response data
     */
    public ResponseData<CptBaseInfo> registerCpt(
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature,
            String privateKey,
            Integer cptId) {
        if (StringUtils.isEmpty(address) || StringUtils.isEmpty(cptJsonSchemaNew) || StringUtils.isEmpty(privateKey)) {
            logger.error("[registerCpt] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        //如果不存在该weId则报错
        if(!weIdServiceLocal.isWeIdExist(WeIdUtils.convertAddressToWeId(address)).getResult()){
            logger.error("[registerCpt] the weid of publisher does not exist on blockchain");
            return new ResponseData<>(null, ErrorCode.CPT_PUBLISHER_NOT_EXIST);
        }
        if(getDataDriver().getCpt(DataDriverConstant.LOCAL_CPT, cptId).getResult() != null){
            logger.error("[registerCpt] cpt already exist on chain");
            return new ResponseData<>(null, ErrorCode.CPT_ALREADY_EXIST);
        }
        if(cptId < AUTHORITY_ISSUER_START_ID){
            if(!RoleController.checkPermission(WeIdUtils.getWeIdFromPrivateKey(privateKey), RoleController.MODIFY_AUTHORITY_ISSUER)){
                logger.error("[registerCpt] operator has not committee member permission to registerCpt");
                return new ResponseData<>(null, ErrorCode.CPT_NO_PERMISSION);
            }
        }else if(cptId < NONE_AUTHORITY_ISSUER_START_ID){
            if(!RoleController.checkPermission(WeIdUtils.getWeIdFromPrivateKey(privateKey), RoleController.MODIFY_KEY_CPT)){
                logger.error("[registerCpt] operator has not authority issuer permission to registerCpt");
                return new ResponseData<>(null, ErrorCode.CPT_NO_PERMISSION);
            }
        }
        ResponseData<CptBaseInfo> resp =
                getDataDriver().addCpt(
                        DataDriverConstant.LOCAL_CPT,
                        cptId,
                        address,
                        null,
                        cptJsonSchemaNew,
                        com.webank.weid.blockchain.util.DataToolUtils.SigBase64Serialization(rsvSignature));
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[registerCpt] save cpt to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(resp.getResult(), ErrorCode.SUCCESS);
    }

    /**
     * This is used to register a new CPT to the blockchain.
     *
     * @param address the address of creator
     * @param cptJsonSchemaNew the new cptJsonSchema
     * @param rsvSignature the rsvSignature of cptJsonSchema
     * @param privateKey the decimal privateKey of creator
     * @return the response data
     */
    public ResponseData<CptBaseInfo> registerCpt(
            String address,
            String cptJsonSchemaNew,
            RsvSignature rsvSignature,
            String privateKey) {
        if (StringUtils.isEmpty(address) || StringUtils.isEmpty(cptJsonSchemaNew) || StringUtils.isEmpty(privateKey)) {
            logger.error("[registerCpt] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        //如果不存在该weId则报错
        if(!weIdServiceLocal.isWeIdExist(WeIdUtils.convertAddressToWeId(address)).getResult()){
            logger.error("[registerCpt] the weid of publisher does not exist on blockchain");
            return new ResponseData<>(null, ErrorCode.CPT_PUBLISHER_NOT_EXIST);
        }
        int cptId = getCptId(address);

        ResponseData<CptBaseInfo> resp =
                getDataDriver().addCpt(
                        DataDriverConstant.LOCAL_CPT,
                        cptId,
                        address,
                        null,
                        cptJsonSchemaNew,
                        com.webank.weid.blockchain.util.DataToolUtils.SigBase64Serialization(rsvSignature));
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[registerCpt] save cpt to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(resp.getResult(), ErrorCode.SUCCESS);
    }

    public int getCptId(String address) {
        GlobalStatus globalStatus = GlobalStatus.readStatusFromFile("global.status");
        if(authorityIssuerServiceLocal.isAuthorityIssuer(address).getResult()){
            int cptId = globalStatus.getAuthority_issuer_current_cpt_id();
            while (getDataDriver().getCpt(DataDriverConstant.LOCAL_CPT, cptId).getResult() != null) {
                cptId++;
            }
            globalStatus.setAuthority_issuer_current_cpt_id(cptId);
            GlobalStatus.storeStatusToFile(globalStatus, "global.status");
            if(cptId > NONE_AUTHORITY_ISSUER_START_ID) cptId = 0;
            return cptId;
        } else {
            int cptId = globalStatus.getNone_authority_issuer_current_cpt_id();
            while (getDataDriver().getCpt(DataDriverConstant.LOCAL_CPT, cptId).getResult() != null) {
                cptId++;
            }
            globalStatus.setNone_authority_issuer_current_cpt_id(cptId);
            GlobalStatus.storeStatusToFile(globalStatus, "global.status");
            return cptId;
        }
    }

    /**
     * this is used to query cpt with the latest version which has been registered.
     *
     * @param cptId the cpt id
     * @return the response data
     */
    public ResponseData<Cpt> queryCpt(Integer cptId) {
        try {
            CptValue cptValue = getDataDriver().getCpt(DataDriverConstant.LOCAL_CPT, cptId).getResult();
            if(cptValue == null){
                logger.error("[queryCpt] cpt not exist on chain");
                return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
            }
            Cpt cpt = new Cpt();
            cpt.setCptId(cptId);
            cpt.setCptVersion(cptValue.getCpt_version());
            cpt.setCptPublisher(cptValue.getPublisher());
            cpt.setCptSignature(cptValue.getCpt_signature());
            Map<String, Object> jsonSchemaMap = DataToolUtils
                    .deserialize(cptValue.getCpt_schema(), HashMap.class);
            cpt.setCptJsonSchema(jsonSchemaMap);
            return new ResponseData<>(cpt, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[queryCpt] execute failed. Error message :{}", e);
            return new ResponseData<>(null, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    /**
     * This is used to update a CPT data which has been register.
     *
     * @param address the address of creator
     * @param cptJsonSchemaNew the new cptJsonSchema
     * @param rsvSignature the rsvSignature of cptJsonSchema
     * @param privateKey the decimal privateKey of creator
     * @param cptId the CPT ID
     * @return the response data
     */
    public ResponseData<CptBaseInfo> updateCpt(String address,
                                               String cptJsonSchemaNew,
                                               RsvSignature rsvSignature,
                                               String privateKey,
                                               Integer cptId) {
        if (StringUtils.isEmpty(address) || StringUtils.isEmpty(cptJsonSchemaNew) || StringUtils.isEmpty(privateKey)) {
            logger.error("[updateCpt] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        if(!weIdServiceLocal.isWeIdExist(com.webank.weid.util.WeIdUtils.convertAddressToWeId(address)).getResult()){
            logger.error("[updateCpt] the weid of publisher does not exist on blockchain");
            return new ResponseData<>(null, ErrorCode.CPT_PUBLISHER_NOT_EXIST);
        }
        CptValue cptValue = getDataDriver().getCpt(DataDriverConstant.LOCAL_CPT, cptId).getResult();
        if(cptValue == null){
            logger.error("[updateCpt] cpt not exist on chain");
            return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
        }
        if(!cptValue.getPublisher().equals(address)){
            logger.error("[updateCpt] cpt publisher of this cpt is not equal to the address");
            return new ResponseData<>(null, ErrorCode.CPT_NO_PERMISSION);
        }
        ResponseData<Integer> resp =
                getDataDriver().updateCpt(
                        DataDriverConstant.LOCAL_CPT,
                        cptId,
                        cptValue.getCpt_version() + 1,
                        address,
                        cptValue.getDescription(),
                        cptJsonSchemaNew,
                        com.webank.weid.blockchain.util.DataToolUtils.SigBase64Serialization(rsvSignature));
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[updateCpt] update cpt to db failed.");
            throw new DatabaseException("database error!");
        }
        CptBaseInfo cptBaseInfo = new CptBaseInfo();
        cptBaseInfo.setCptId(cptId);
        cptBaseInfo.setCptVersion(cptValue.getCpt_version() + 1);
        return new ResponseData<>(cptBaseInfo, ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<Boolean> putCredentialTemplate(Integer cptId, String credentialPublicKey, String credentialKeyCorrectnessProof) {
        if (StringUtils.isEmpty(credentialPublicKey) || StringUtils.isEmpty(credentialKeyCorrectnessProof)) {
            logger.error("[putCredentialTemplate] input argument is illegal");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        CptValue cptValue = getDataDriver().getCpt(DataDriverConstant.LOCAL_CPT, cptId).getResult();
        if(cptValue == null){
            logger.error("[putCredentialTemplate] cpt not exist on chain");
            return new ResponseData<>(false, ErrorCode.CPT_NOT_EXISTS);
        }
        ResponseData<Integer> resp =
                getDataDriver().updateCredentialTemplate(
                        DataDriverConstant.LOCAL_CPT,
                        cptId,
                        credentialPublicKey,
                        credentialKeyCorrectnessProof);
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[putCredentialTemplate] update credential template to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<CredentialTemplateEntity> queryCredentialTemplate(Integer cptId) {
        try {
            CptValue cptValue = getDataDriver().getCpt(DataDriverConstant.LOCAL_CPT, cptId).getResult();
            if(cptValue == null){
                logger.error("[queryCredentialTemplate] cpt not exist on chain");
                return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
            }
            CredentialTemplateEntity credentialTemplateEntity = new CredentialTemplateEntity();
            TemplatePublicKey pubKey = TemplatePublicKey.newBuilder().setCredentialPublicKey(cptValue.getCredential_publicKey()).build();
            credentialTemplateEntity.setPublicKey(pubKey);
            credentialTemplateEntity.setCredentialKeyCorrectnessProof(cptValue.getCredential_proof());
            Map<String, Object> cptInfo = DataToolUtils
                    .deserialize(cptValue.getCpt_schema(), HashMap.class);
            List<String> attrList;
            attrList = JsonUtil.extractCptProperties(cptInfo);
            AttributeTemplate.Builder builder = AttributeTemplate.newBuilder();
            for (String attr : attrList) {
                builder.addAttributeKey(attr);
            }
            AttributeTemplate attributes = builder.build();
            credentialTemplateEntity.setCredentialSchema(attributes);
            return new ResponseData<>(credentialTemplateEntity, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[queryCredentialTemplate] execute failed. Error message :{}", e);
            return new ResponseData<>(null, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    @Override
    public ResponseData<List<Integer>> getCptIdList(Integer startPos, Integer num) {
        try {
            return getDataDriver().getCptIdList(
                    DataDriverConstant.LOCAL_CPT,
                    startPos,
                    startPos + num);
        } catch (Exception e) {
            logger.error("[getCptIdList] getCptIdList has error, Error Message：{}", e);
            return new ResponseData<>(null, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    @Override
    public ResponseData<Integer> getCptCount() {
        try {
            return getDataDriver().getCptCount(DataDriverConstant.LOCAL_CPT);
        } catch (Exception e) {
            logger.error("[getCptCount] getCptCount has error, Error Message：{}", e);
            return new ResponseData<>(0, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

}
