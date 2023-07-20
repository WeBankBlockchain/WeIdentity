

package com.webank.weid.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;
import com.webank.wedpr.selectivedisclosure.IssuerClient;
import com.webank.wedpr.selectivedisclosure.IssuerResult;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.service.local.CptServiceLocal;
import com.webank.weid.service.local.WeIdServiceLocal;
import com.webank.weid.suite.persistence.PersistenceFactory;
import com.webank.weid.suite.persistence.Persistence;
import com.webank.weid.suite.persistence.PersistenceType;
import com.webank.weid.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.CptStringArgs;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.service.rpc.CptService;
import com.webank.weid.suite.cache.CacheManager;
import com.webank.weid.suite.cache.CacheNode;

/**
 * Service implementation for operation on CPT (Claim Protocol Type).
 *
 * @author afeexian
 */
public class CptServiceImpl implements CptService {

    private static final Logger logger = LoggerFactory.getLogger(CptServiceImpl.class);
    private static com.webank.weid.blockchain.rpc.CptService cptBlockchainService;

    private static Persistence dataDriver;
    private static PersistenceType persistenceType;
    //获取CPT缓存节点
    private static CacheNode<ResponseData<Cpt>> cptCahceNode =
            CacheManager.registerCacheNode("SYS_CPT", 1000 * 3600 * 24L);

    public CptServiceImpl(){
        cptBlockchainService = getCptService();
    }

    private static com.webank.weid.blockchain.rpc.CptService getCptService() {
        if(cptBlockchainService != null) {
            return cptBlockchainService;
        } else {
            String type = PropertyUtils.getProperty("deploy.style");
            if (type.equals("blockchain")) {
                return new com.webank.weid.blockchain.service.impl.CptServiceImpl();
            } else {
                // default database
                return new CptServiceLocal();
            }
        }
    }

    /**
     * Register a new CPT with a pre-set CPT ID, to the blockchain.
     *
     * @param args the args
     * @param cptId the CPT ID
     * @return response data
     */
    public ResponseData<CptBaseInfo> registerCpt(CptStringArgs args, Integer cptId) {
        if (args == null || cptId == null || cptId <= 0) {
            logger.error(
                    "[registerCpt1] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        try {
            CptMapArgs cptMapArgs = new CptMapArgs();
            cptMapArgs.setWeIdAuthentication(args.getWeIdAuthentication());
            Map<String, Object> cptJsonSchemaMap =
                    DataToolUtils.deserialize(args.getCptJsonSchema(), HashMap.class);
            cptMapArgs.setCptJsonSchema(cptJsonSchemaMap);
            return this.registerCpt(cptMapArgs, cptId);
        } catch (Exception e) {
            logger.error("[registerCpt1] register cpt failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }


    /**
     * This is used to register a new CPT to the blockchain.
     *
     * @param args the args
     * @return the response data
     */
    public ResponseData<CptBaseInfo> registerCpt(CptStringArgs args) {

        try {
            if (args == null) {
                logger.error(
                        "[registerCpt1]input CptStringArgs is null");
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }

            CptMapArgs cptMapArgs = new CptMapArgs();
            cptMapArgs.setWeIdAuthentication(args.getWeIdAuthentication());
            Map<String, Object> cptJsonSchemaMap =
                    DataToolUtils.deserialize(args.getCptJsonSchema(), HashMap.class);
            cptMapArgs.setCptJsonSchema(cptJsonSchemaMap);
            return this.registerCpt(cptMapArgs);
        } catch (Exception e) {
            logger.error("[registerCpt1] register cpt failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * Register a new CPT with a pre-set CPT ID, to the blockchain.
     *
     * @param args the args
     * @param cptId the CPT ID
     * @return response data
     */
    public ResponseData<CptBaseInfo> registerCpt(CptMapArgs args, Integer cptId) {
        if (args == null || cptId == null || cptId <= 0) {
            logger.error("[registerCpt] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        try {
            ErrorCode errorCode =
                    this.validateCptArgs(
                            args.getWeIdAuthentication(),
                            args.getCptJsonSchema()
                    );
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(null, errorCode);
            }

            String weId = args.getWeIdAuthentication().getWeId();
            WeIdPrivateKey weIdPrivateKey = args.getWeIdAuthentication().getWeIdPrivateKey();
            String cptJsonSchemaNew = DataToolUtils.cptSchemaToString(args);
            RsvSignature rsvSignature = sign(
                    weId,
                    cptJsonSchemaNew,
                    weIdPrivateKey);
            String address = WeIdUtils.convertWeIdToAddress(weId);
            com.webank.weid.blockchain.protocol.response.ResponseData<com.webank.weid.blockchain.protocol.base.CptBaseInfo> innerResp =
                    cptBlockchainService.registerCpt(address, cptJsonSchemaNew, RsvSignature.toBlockChain(rsvSignature),
                            weIdPrivateKey.getPrivateKey(), cptId);
            if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(null,
                        ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()), innerResp.getTransactionInfo());
            }
            ErrorCode errorCodeProcess = processTemplate(cptId, cptJsonSchemaNew);
            int code = errorCodeProcess.getCode();
            if (code != ErrorCode.SUCCESS.getCode()) {
                logger.error("[registerCpt]register cpt failed, error code is {}", code);
                return new ResponseData<CptBaseInfo>(null, ErrorCode.getTypeByErrorCode(code), innerResp.getTransactionInfo());
            }
            CptBaseInfo cptBaseInfo = CptBaseInfo.fromBlockChain(innerResp.getResult());
            return new ResponseData<>(cptBaseInfo, ErrorCode.SUCCESS, innerResp.getTransactionInfo());
            /*return cptServiceEngine.registerCpt(cptId, address, cptJsonSchemaNew, rsvSignature,
                    weIdPrivateKey.getPrivateKey(), WeIdConstant.CPT_DATA_INDEX);*/

        } catch (Exception e) {
            logger.error("[registerCpt] register cpt failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * This is used to register a new CPT to the blockchain.
     *
     * @param args the args
     * @return the response data
     */
    public ResponseData<CptBaseInfo> registerCpt(CptMapArgs args) {

        try {
            if (args == null) {
                logger.error("[registerCpt]input CptMapArgs is null");
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }
            ErrorCode validateResult =
                    this.validateCptArgs(
                            args.getWeIdAuthentication(),
                            args.getCptJsonSchema()
                    );

            if (validateResult.getCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(null, validateResult);
            }

            String weId = args.getWeIdAuthentication().getWeId();
            WeIdPrivateKey weIdPrivateKey = args.getWeIdAuthentication().getWeIdPrivateKey();
            String cptJsonSchemaNew = DataToolUtils.cptSchemaToString(args);
            RsvSignature rsvSignature = sign(
                    weId,
                    cptJsonSchemaNew,
                    weIdPrivateKey);
            String address = WeIdUtils.convertWeIdToAddress(weId);
            com.webank.weid.blockchain.protocol.response.ResponseData<com.webank.weid.blockchain.protocol.base.CptBaseInfo> innerResp =
                    cptBlockchainService.registerCpt(address, cptJsonSchemaNew, RsvSignature.toBlockChain(rsvSignature),
                            weIdPrivateKey.getPrivateKey());
            if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(null,
                        ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()), innerResp.getTransactionInfo());
            }
            CptBaseInfo cptBaseInfo = CptBaseInfo.fromBlockChain(innerResp.getResult());
            ErrorCode errorCodeProcess = processTemplate(cptBaseInfo.getCptId(), cptJsonSchemaNew);
            int code = errorCodeProcess.getCode();
            if (code != ErrorCode.SUCCESS.getCode()) {
                logger.error("[registerCpt]register cpt failed, error code is {}", code);
                return new ResponseData<CptBaseInfo>(null, ErrorCode.getTypeByErrorCode(code), innerResp.getTransactionInfo());
            }
            return new ResponseData<>(cptBaseInfo, ErrorCode.SUCCESS, innerResp.getTransactionInfo());
            /*return cptServiceEngine.registerCpt(address, cptJsonSchemaNew, rsvSignature,
                    weIdPrivateKey.getPrivateKey(), WeIdConstant.CPT_DATA_INDEX);*/
        } catch (Exception e) {
            logger.error("[registerCpt] register cpt failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

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

    private ErrorCode processTemplate(Integer cptId, String cptJsonSchemaNew) {

        //if the cpt is not zkp type, no need to make template.
        if (!CredentialPojoUtils.isZkpCpt(cptJsonSchemaNew)) {
            return ErrorCode.SUCCESS;
        }
        List<String> attributeList;
        try {
            attributeList = JsonUtil.extractCptProperties(cptJsonSchemaNew);
            IssuerResult issuerResult = IssuerClient.makeCredentialTemplate(attributeList);
            CredentialTemplateEntity template = issuerResult.credentialTemplateEntity;
            String templateSecretKey = issuerResult.templateSecretKey;
            ResponseData<Integer> resp =
                    getDataDriver().addOrUpdate(
                            DataDriverConstant.DOMAIN_ISSUER_TEMPLATE_SECRET,
                            String.valueOf(cptId),
                            templateSecretKey);
            if (resp.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[processTemplate] save credential template to db failed.");
                throw new DatabaseException("database error!");
            }
            com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> innerResp = cptBlockchainService.putCredentialTemplate(
                    cptId,
                    template.getPublicKey().getCredentialPublicKey(),
                    template.getCredentialKeyCorrectnessProof());
            if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                return ErrorCode.CPT_CREDENTIAL_TEMPLATE_SAVE_ERROR;
            }
            return ErrorCode.SUCCESS;
        } catch (Exception e) {
            logger.error("[processTemplate] process credential template failed.", e);
            return ErrorCode.CPT_CREDENTIAL_TEMPLATE_SAVE_ERROR;
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
            if (cptId == null || cptId < 0) {
                return new ResponseData<>(null, ErrorCode.CPT_ID_ILLEGAL);
            }
            String cptIdStr = String.valueOf(cptId);
            ResponseData<Cpt> result = cptCahceNode.get(cptIdStr);
            if (result == null) {
                com.webank.weid.blockchain.protocol.response.ResponseData<com.webank.weid.blockchain.protocol.base.Cpt> innerResp =
                        cptBlockchainService.queryCpt(cptId);
                if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                    return new ResponseData<>(null,
                            ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()), innerResp.getTransactionInfo());
                }
                Cpt cpt = Cpt.fromBlockChain(innerResp.getResult());
                cptCahceNode.put(cptIdStr, new ResponseData<>(cpt, ErrorCode.SUCCESS));
                return new ResponseData<>(cpt, ErrorCode.SUCCESS, innerResp.getTransactionInfo());
                /*result = cptServiceEngine.queryCpt(cptId, WeIdConstant.CPT_DATA_INDEX);
                if (result.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
                    cptCahceNode.put(cptIdStr, result);
                }*/
            }
            return result;
        } catch (Exception e) {
            logger.error("[updateCpt] query cpt failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * This is used to update a CPT data which has been register.
     *
     * @param args the args
     * @return the response data
     */
    public ResponseData<CptBaseInfo> updateCpt(CptStringArgs args, Integer cptId) {

        try {
            if (args == null) {
                logger.error("[updateCpt1]input UpdateCptArgs is null");
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }

            CptMapArgs cptMapArgs = new CptMapArgs();
            cptMapArgs.setWeIdAuthentication(args.getWeIdAuthentication());
            cptMapArgs.setCptJsonSchema(
                    DataToolUtils.deserialize(args.getCptJsonSchema(), HashMap.class));
            return this.updateCpt(cptMapArgs, cptId);
        } catch (Exception e) {
            logger.error("[updateCpt1] update cpt failed due to unkown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * This is used to update a CPT data which has been register.
     *
     * @param args the args
     * @return the response data
     */
    public ResponseData<CptBaseInfo> updateCpt(CptMapArgs args, Integer cptId) {

        try {
            if (args == null) {
                logger.error("[updateCpt]input UpdateCptArgs is null");
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }
            if (cptId == null || cptId.intValue() < 0) {
                logger.error("[updateCpt]input cptId illegal");
                return new ResponseData<>(null, ErrorCode.CPT_ID_ILLEGAL);
            }
            ErrorCode errorCode =
                    this.validateCptArgs(
                            args.getWeIdAuthentication(),
                            args.getCptJsonSchema()
                    );

            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(null, errorCode);
            }

            String weId = args.getWeIdAuthentication().getWeId();
            WeIdPrivateKey weIdPrivateKey = args.getWeIdAuthentication().getWeIdPrivateKey();
            String cptJsonSchemaNew = DataToolUtils.cptSchemaToString(args);
            RsvSignature rsvSignature = sign(
                    weId,
                    cptJsonSchemaNew,
                    weIdPrivateKey);
            String address = WeIdUtils.convertWeIdToAddress(weId);
            com.webank.weid.blockchain.protocol.response.ResponseData<com.webank.weid.blockchain.protocol.base.CptBaseInfo> innerResp =
                    cptBlockchainService.updateCpt(
                            address,
                            cptJsonSchemaNew,
                            RsvSignature.toBlockChain(rsvSignature),
                            weIdPrivateKey.getPrivateKey(),
                            cptId);
            if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(null,
                        ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()), innerResp.getTransactionInfo());
            }
            cptCahceNode.remove(String.valueOf(cptId));
            ErrorCode errorCodeProcess = processTemplate(cptId, cptJsonSchemaNew);
            int code = errorCodeProcess.getCode();
            if (code != ErrorCode.SUCCESS.getCode()) {
                logger.error("[updateCpt]update cpt failed, error code is {}", code);
                return new ResponseData<CptBaseInfo>(null, ErrorCode.getTypeByErrorCode(code), innerResp.getTransactionInfo());
            }
            CptBaseInfo cptBaseInfo = CptBaseInfo.fromBlockChain(innerResp.getResult());
            return new ResponseData<>(cptBaseInfo, ErrorCode.SUCCESS, innerResp.getTransactionInfo());
           /* ResponseData<CptBaseInfo> result = cptServiceEngine.updateCpt(
                    cptId,
                    address,
                    cptJsonSchemaNew,
                    rsvSignature,
                    weIdPrivateKey.getPrivateKey(),
                    WeIdConstant.CPT_DATA_INDEX);
            if (result.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
                cptCahceNode.remove(String.valueOf(cptId));
            }
            return result;*/
        } catch (Exception e) {
            logger.error("[updateCpt] update cpt failed due to unkown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    private RsvSignature sign(
            String cptPublisher,
            String jsonSchema,
            WeIdPrivateKey cptPublisherPrivateKey) {

        StringBuilder sb = new StringBuilder();
        sb.append(cptPublisher);
        sb.append(WeIdConstant.PIPELINE);
        sb.append(jsonSchema);
        return DataToolUtils.signToRsvSignature(
                sb.toString(), cptPublisherPrivateKey.getPrivateKey());
    }

    private ErrorCode validateCptArgs(
            WeIdAuthentication weIdAuthentication,
            Map<String, Object> cptJsonSchemaMap) throws Exception {

        if (weIdAuthentication == null) {
            logger.error("Input cpt weIdAuthentication is invalid.");
            return ErrorCode.WEID_AUTHORITY_INVALID;
        }

        String weId = weIdAuthentication.getWeId();
        if (!WeIdUtils.isWeIdValid(weId)) {
            logger.error("Input cpt publisher : {} is invalid.", weId);
            return ErrorCode.WEID_INVALID;
        }

        ErrorCode errorCode = validateCptJsonSchemaMap(cptJsonSchemaMap);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return errorCode;
        }
        String cptJsonSchema = DataToolUtils.serialize(cptJsonSchemaMap);
        if (!DataToolUtils.isCptJsonSchemaValid(cptJsonSchema)) {
            logger.error("Input cpt json schema : {} is invalid.", cptJsonSchemaMap);
            return ErrorCode.CPT_JSON_SCHEMA_INVALID;
        }
        WeIdPrivateKey weIdPrivateKey = weIdAuthentication.getWeIdPrivateKey();
        if (weIdPrivateKey == null
                || StringUtils.isEmpty(weIdPrivateKey.getPrivateKey())) {
            logger.error(
                    "Input cpt publisher private key : {} is in valid.",
                    weIdPrivateKey
            );
            return ErrorCode.WEID_PRIVATEKEY_INVALID;
        }

        if (!WeIdUtils.validatePrivateKeyWeIdMatches(weIdPrivateKey, weId)) {
            return ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH;
        }
        return ErrorCode.SUCCESS;
    }

    private ErrorCode validateCptJsonSchemaMap(
            Map<String, Object> cptJsonSchemaMap) throws Exception {
        if (cptJsonSchemaMap == null || cptJsonSchemaMap.isEmpty()) {
            logger.error("Input cpt json schema is invalid.");
            return ErrorCode.CPT_JSON_SCHEMA_INVALID;
        }
        //String cptJsonSchema = JsonUtil.objToJsonStr(cptJsonSchemaMap);
        String cptJsonSchema = DataToolUtils.serialize(cptJsonSchemaMap);
        if (!DataToolUtils.isCptJsonSchemaValid(cptJsonSchema)) {
            logger.error("Input cpt json schema : {} is invalid.", cptJsonSchemaMap);
            return ErrorCode.CPT_JSON_SCHEMA_INVALID;
        }
        return ErrorCode.SUCCESS;
    }



    /* (non-Javadoc)
     * @see com.webank.weid.service.rpc.CptService#queryCredentialTemplate(java.lang.Integer)
     */
    @Override
    public ResponseData<CredentialTemplateEntity> queryCredentialTemplate(Integer cptId) {
        com.webank.weid.blockchain.protocol.response.ResponseData<CredentialTemplateEntity> innerResp =
                cptBlockchainService.queryCredentialTemplate(cptId);
        if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(null,
                    ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()), innerResp.getTransactionInfo());
        }
        return new ResponseData<>(innerResp.getResult(), ErrorCode.SUCCESS, innerResp.getTransactionInfo());
        //return cptServiceEngine.queryCredentialTemplate(cptId);
    }


    @Override
    public ResponseData<List<Integer>> getCptIdList(Integer startPos, Integer num) {
        if (startPos < 0 || num < 1) {
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        com.webank.weid.blockchain.protocol.response.ResponseData<List<Integer>> innerResp =
                cptBlockchainService.getCptIdList(startPos, num);
        if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(null,
                    ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()), innerResp.getTransactionInfo());
        }
        return new ResponseData<>(innerResp.getResult(), ErrorCode.SUCCESS, innerResp.getTransactionInfo());
        //return cptServiceEngine.getCptIdList(startPos, num, WeIdConstant.CPT_DATA_INDEX);
    }

    @Override
    public ResponseData<Integer> getCptCount() {
        com.webank.weid.blockchain.protocol.response.ResponseData<Integer> innerResp =
                cptBlockchainService.getCptCount();
        if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(-1,
                    ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()), innerResp.getTransactionInfo());
        }
        return new ResponseData<>(innerResp.getResult(), ErrorCode.SUCCESS, innerResp.getTransactionInfo());
        //return cptServiceEngine.getCptCount(WeIdConstant.CPT_DATA_INDEX);
    }
}
