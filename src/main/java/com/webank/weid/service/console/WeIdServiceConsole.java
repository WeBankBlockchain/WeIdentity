

package com.webank.weid.service.console;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;
import com.webank.wedpr.selectivedisclosure.IssuerClient;
import com.webank.wedpr.selectivedisclosure.IssuerResult;
import com.webank.wedpr.selectivedisclosure.proto.AttributeTemplate;
import com.webank.wedpr.selectivedisclosure.proto.TemplatePublicKey;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.constant.CptType;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.*;
import com.webank.weid.protocol.request.AuthenticationArgs;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.ServiceArgs;
import com.webank.weid.protocol.response.RsvSignature;
import com.webank.weid.suite.cache.CacheManager;
import com.webank.weid.suite.cache.CacheNode;
import com.webank.weid.util.*;
import com.webank.weid.util.Multibase.Multibase;
import com.webank.weid.util.Multicodec.Multicodec;
import com.webank.weid.util.Multicodec.MulticodecEncoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.fisco.bcos.sdk.model.CryptoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Service implementations for operations on WeIdentity DID.
 *
 * @author afeexian 2023.06
 */
public class WeIdServiceConsole {

    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(WeIdServiceConsole.class);

    private static int CPT_DEFAULT_VERSION = 1;

    //获取CPT缓存节点
    private static CacheNode<ResponseData<Cpt>> cptCahceNode =
            CacheManager.registerCacheNode("SYS_CPT", 1000 * 3600 * 24L);

    /**
     * Create a WeIdentity DID Document.
     *
     * @return the WeIdDocument
     */
    public ResponseData<WeIdDocument> createWeIdDocument(String publicKey) {
        if (StringUtils.isNotBlank(publicKey)) {
            String weId = WeIdUtils.convertPublicKeyToWeId(publicKey);
            String address = WeIdUtils.convertWeIdToAddress(weId);
            AuthenticationProperty authenticationProperty = new AuthenticationProperty();
            //在创建weid时默认添加一个id为#keys-[hash(publicKey)]的verification method
            authenticationProperty.setId(weId + "#keys-" + DataToolUtils.hash(publicKey).substring(58));
            //verification method controller默认为自己
            authenticationProperty.setController(weId);
            //这里把publicKey用multicodec编码，然后使用Multibase格式化，国密和非国密使用不同的编码
            byte[] publicKeyEncode = MulticodecEncoder.encode(DataToolUtils.cryptoType == CryptoType.ECDSA_TYPE? Multicodec.ED25519_PUB:Multicodec.SM2_PUB,
                    publicKey.getBytes(StandardCharsets.UTF_8));
            authenticationProperty.setPublicKeyMultibase(Multibase.encode(Multibase.Base.Base58BTC, publicKeyEncode));
            List<AuthenticationProperty> authList = new ArrayList<>();
            authList.add(authenticationProperty);
            List<ServiceProperty> serviceList = new ArrayList<>();
            ServiceProperty serviceProperty = new ServiceProperty();
            serviceProperty.setServiceEndpoint("https://github.com/WeBankBlockchain/WeIdentity");
            serviceProperty.setType("WeIdentity");
            serviceProperty.setId(authenticationProperty.getController() + '#' + DataToolUtils.hash(serviceProperty.getServiceEndpoint()).substring(58));
            serviceList.add(serviceProperty);
            WeIdDocument weIdDocument = new WeIdDocument();
            weIdDocument.setId(weId);
            weIdDocument.setService(serviceList);
            weIdDocument.setAuthentication(authList);
            return new ResponseData<>(weIdDocument, ErrorCode.SUCCESS);
        } else {
            return new ResponseData<>(null, ErrorCode.WEID_PUBLICKEY_INVALID);
        }
    }

    /**
     * Create a WeIdentity DID DocumentMetadata.
     *
     * @return the WeIdDocumentMetadata
     */
    public ResponseData<WeIdDocumentMetadata> createWeIdDocumentMetadata() {
        WeIdDocumentMetadata weIdDocumentMetadata = new WeIdDocumentMetadata();
        weIdDocumentMetadata.setCreated(DateUtils.getNoMillisecondTimeStamp());
        weIdDocumentMetadata.setDeactivated(false);
        weIdDocumentMetadata.setUpdated(DateUtils.getNoMillisecondTimeStamp());
        weIdDocumentMetadata.setVersionId(1);
        return new ResponseData<>(weIdDocumentMetadata, ErrorCode.SUCCESS);
    }

    /**
     * Create a WeIdentity DID Document Json.
     *
     * @param publicKey the publicKey
     * @return the WeIdentity DID document json
     */
    public ResponseData<String> createWeIdDocumentJson(String publicKey) {

        ResponseData<WeIdDocument> responseData = this.createWeIdDocument(publicKey);
        WeIdDocument result = responseData.getResult();

        if (result == null) {
            return new ResponseData<>(
                StringUtils.EMPTY,
                ErrorCode.getTypeByErrorCode(responseData.getErrorCode())
            );
        }
        ObjectMapper mapper = new ObjectMapper();
        String weIdDocument;
        try {
            weIdDocument = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception e) {
            logger.error("write object to String fail.", e);
            return new ResponseData<>(
                StringUtils.EMPTY,
                ErrorCode.getTypeByErrorCode(responseData.getErrorCode())
            );
        }
        weIdDocument =
            new StringBuffer()
                .append(weIdDocument)
                .insert(1, WeIdConstant.WEID_DOC_PROTOCOL_VERSION)
                .toString();

        ResponseData<String> responseDataJson = new ResponseData<String>();
        responseDataJson.setResult(weIdDocument);
        responseDataJson.setErrorCode(ErrorCode.getTypeByErrorCode(responseData.getErrorCode()));

        return responseDataJson;
    }

    /**
     * Set authentications in WeIdentity DID.
     *
     * @param weIdDocument the weIdDocument to set auth to
     * @param authenticationArgs A public key is needed
     * @return true if the "set" operation succeeds, false otherwise.
     */
    public ResponseData<WeIdDocument> setAuthentication(
            WeIdDocument weIdDocument,
            AuthenticationArgs authenticationArgs){

        if (!verifyAuthenticationArgs(authenticationArgs)) {
            logger.error("[setAuthentication]: input parameter setAuthenticationArgs is illegal.");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        return processSetAuthentication(
                authenticationArgs,
                weIdDocument);
    }

    private ResponseData<WeIdDocument> processSetAuthentication(
            AuthenticationArgs authenticationArgs,
            WeIdDocument weIdDocument) {
        String weId = weIdDocument.getId();
        if (WeIdUtils.isWeIdValid(weId)) {
            //检查authentication的controller WeId是否存在和是否被注销
            if (StringUtils.isEmpty(authenticationArgs.getController())) {
                authenticationArgs.setController(weId);
            }
            if (!WeIdUtils.isWeIdValid(authenticationArgs.getController())) {
                logger.error("[setAuthentication]: controller : {} is invalid.", authenticationArgs.getController());
                return new ResponseData<>(null, ErrorCode.WEID_INVALID);
            }
            for(int i=0; i<weIdDocument.getAuthentication().size(); i++){
                if(authenticationArgs.getPublicKey().equals(weIdDocument.getAuthentication().get(i).getPublicKey())){
                    logger.error("[setAuthentication]: failed, the Authentication with PublicKeyMultibase :{} exists",
                            authenticationArgs.getPublicKey());
                    return new ResponseData<>(null, ErrorCode.AUTHENTICATION_PUBLIC_KEY_MULTIBASE_EXISTS);
                }
                if(!StringUtils.isEmpty(authenticationArgs.getId()) && authenticationArgs.getId().equals(weIdDocument.getAuthentication().get(i).getId())){
                    logger.error("[setAuthentication]: failed, the Authentication with id :{} exists",
                            authenticationArgs.getId());
                    return new ResponseData<>(null, ErrorCode.AUTHENTICATION_METHOD_ID_EXISTS);
                }
            }
            AuthenticationProperty authenticationProperty = new AuthenticationProperty();
            //如果用户没有指定method id，则系统分配
            authenticationProperty.setId(authenticationArgs.getId());
            if(StringUtils.isBlank(authenticationArgs.getId())){
                authenticationProperty.setId(weId + "#keys-" + DataToolUtils.hash(authenticationArgs.getPublicKey()).substring(58));
            }
            authenticationProperty.setController(authenticationArgs.getController());
            byte[] publicKeyEncode = MulticodecEncoder.encode(DataToolUtils.cryptoType == CryptoType.ECDSA_TYPE? Multicodec.ED25519_PUB:Multicodec.SM2_PUB,
                    authenticationArgs.getPublicKey().getBytes(StandardCharsets.UTF_8));
            authenticationProperty.setPublicKeyMultibase(Multibase.encode(Multibase.Base.Base58BTC, publicKeyEncode));

            List<AuthenticationProperty> authentication = weIdDocument.getAuthentication();
            authentication.add(authenticationProperty);
            weIdDocument.setAuthentication(authentication);
            return new ResponseData<>(weIdDocument, ErrorCode.SUCCESS);
        } else {
            logger.error("Set authenticate failed. weid : {} is invalid.", weId);
            return new ResponseData<>(null, ErrorCode.WEID_INVALID);
        }
    }

    private boolean verifyAuthenticationArgs(AuthenticationArgs authenticationArgs) {

        return !(authenticationArgs == null
                || StringUtils.isEmpty(authenticationArgs.getPublicKey())
                || !(isPublicKeyStringValid(authenticationArgs.getPublicKey())));
    }

    private boolean isPublicKeyStringValid(String pubKey) {
        // Allow base64, rsa (alphaNum) and bigInt
        return (DataToolUtils.isValidBase64String(pubKey)
                //    || StringUtils.isAlphanumeric(pubKey)
                || NumberUtils.isDigits(pubKey));
    }

    /**
     * Set service properties.
     *
     * @param weIdDocument the weIdDocument to set service to
     * @param serviceArgs your service name and endpoint
     * @return true if the "set" operation succeeds, false otherwise.
     */
    public ResponseData<WeIdDocument> setService(WeIdDocument weIdDocument, ServiceArgs serviceArgs) {
        if (!verifyServiceArgs(serviceArgs)) {
            logger.error("[setService]: input parameter setServiceArgs is illegal.");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        return processSetService(
                weIdDocument,
                serviceArgs);

    }

    private ResponseData<WeIdDocument> processSetService(
            WeIdDocument weIdDocument,
            ServiceArgs serviceArgs) {
        String weId = weIdDocument.getId();
        if (WeIdUtils.isWeIdValid(weId)) {
            List<ServiceProperty> service = weIdDocument.getService();
            ServiceProperty serviceProperty = new ServiceProperty();
            serviceProperty.setType(serviceArgs.getType());
            serviceProperty.setServiceEndpoint(serviceArgs.getServiceEndpoint());
            if(weIdDocument.getService().size()==0){
                if(StringUtils.isEmpty(serviceArgs.getId())){
                    serviceProperty.setId(weId + '#' + DataToolUtils.hash(serviceArgs.getServiceEndpoint()));
                }else{
                    serviceProperty.setId(serviceArgs.getId());
                }
            }else{
                if(StringUtils.isEmpty(serviceArgs.getId())){
                    serviceProperty.setId(weId + '#' + DataToolUtils.hash(serviceArgs.getServiceEndpoint()).substring(58));
                }else{
                    for(int i=0; i<weIdDocument.getService().size(); i++){
                        if(serviceArgs.getId().equals(weIdDocument.getService().get(i).getId())){
                            logger.error("[setAuthentication]: failed, the service with id :{} exists",
                                    serviceArgs.getId());
                            return new ResponseData<>(null, ErrorCode.SERVICE_METHOD_ID_EXISTS);
                        }
                    }
                    serviceProperty.setId(serviceArgs.getId());
                }
            }
            service.add(serviceProperty);
            weIdDocument.setService(service);
            return new ResponseData<>(weIdDocument, ErrorCode.SUCCESS);
        } else {
            logger.error("[setService] set service failed, weid -->{} is invalid.", weId);
            return new ResponseData<>(null, ErrorCode.WEID_INVALID);
        }
    }

    private boolean verifyServiceArgs(ServiceArgs serviceArgs) {

        return !(serviceArgs == null
                || StringUtils.isBlank(serviceArgs.getType())
                || StringUtils.isBlank(serviceArgs.getServiceEndpoint()));
    }

    /**
     * Save a new CPT with a pre-set CPT ID, to the local file.
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
            Cpt cpt = new Cpt();
            cpt.setCptId(cptId);
            cpt.setCreated(DateUtils.getNoMillisecondTimeStamp());
            cpt.setUpdated(DateUtils.getNoMillisecondTimeStamp());
            cpt.setCptVersion(CPT_DEFAULT_VERSION);
            cpt.setCptPublisher(address);
            cpt.setCptJsonSchema(args.getCptJsonSchema());
            cpt.setCptSignature(DataToolUtils.SigBase64Serialization(rsvSignature));

            //单机模式不需要提前存储ZKP Template，需要用的时候再生成
            //ErrorCode errorCodeProcess = processTemplate(cptId, cptJsonSchemaNew);
            //把cpt保存到本地文件，名字为：Cpt[cptId]
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("Cpt"+cptId);
            // if file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            mapper.writeValue(file, cpt);

            CptBaseInfo cptBaseInfo = new CptBaseInfo();
            cptBaseInfo.setCptId(cptId);
            cptBaseInfo.setCptVersion(CPT_DEFAULT_VERSION);
            return new ResponseData<>(cptBaseInfo, ErrorCode.SUCCESS);
            /*return cptServiceEngine.registerCpt(cptId, address, cptJsonSchemaNew, rsvSignature,
                    weIdPrivateKey.getPrivateKey(), WeIdConstant.CPT_DATA_INDEX);*/

        } catch (Exception e) {
            logger.error("[registerCpt] register cpt failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * this is used to query cpt with the latest version which has been saved.
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
                ObjectMapper mapper = new ObjectMapper();
                File file = new File("Cpt"+cptId);
                // if file does not exists, then create it
                if (!file.exists()) {
                    logger.error("[queryCpt] cpt file not exist in local");
                    return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
                }
                Cpt cpt = mapper.readValue(file, Cpt.class);
                cptCahceNode.put(cptIdStr, new ResponseData<>(cpt, ErrorCode.SUCCESS));
                return new ResponseData<>(cpt, ErrorCode.SUCCESS);
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
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("Cpt"+cptId);
            // if file does not exists, then create it
            if (!file.exists()) {
                logger.error("[queryCpt] cpt file not exist in local");
                return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
            }
            Cpt cpt = mapper.readValue(file, Cpt.class);
            cpt.setCptPublisher(address);
            cpt.setCptJsonSchema(args.getCptJsonSchema());
            cpt.setCptSignature(DataToolUtils.SigBase64Serialization(rsvSignature));
            cpt.setCptVersion(cpt.getCptVersion() + 1);
            cpt.setUpdated(DateUtils.getNoMillisecondTimeStamp());
            mapper.writeValue(file, cpt);

            cptCahceNode.remove(String.valueOf(cptId));
            CptBaseInfo cptBaseInfo = new CptBaseInfo();
            cptBaseInfo.setCptId(cptId);
            cptBaseInfo.setCptVersion(cpt.getCptVersion());
            return new ResponseData<>(cptBaseInfo, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[updateCpt] update cpt failed due to unkown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.rpc.CptService#queryCredentialTemplate(java.lang.Integer)
     */
    public ResponseData<CredentialTemplateEntity> queryCredentialTemplate(Integer cptId) {
        Cpt cpt = queryCpt(cptId).getResult();
        CptMapArgs args = new CptMapArgs();
        args.setCptType(CptType.ZKP);
        args.setCptJsonSchema(cpt.getCptJsonSchema());
        String cptJsonSchemaNew = DataToolUtils.cptSchemaToString(args);
        List<String> attributeList;
        try {
            attributeList = JsonUtil.extractCptProperties(cptJsonSchemaNew);
            IssuerResult issuerResult = IssuerClient.makeCredentialTemplate(attributeList);
            CredentialTemplateEntity template = issuerResult.credentialTemplateEntity;
            String templateSecretKey = issuerResult.templateSecretKey;
            CredentialTemplateEntity credentialTemplateEntity = new CredentialTemplateEntity();
            TemplatePublicKey pubKey = TemplatePublicKey.newBuilder().setCredentialPublicKey(template.getPublicKey().getCredentialPublicKey()).build();
            credentialTemplateEntity.setPublicKey(pubKey);
            credentialTemplateEntity.setCredentialKeyCorrectnessProof(template.getCredentialKeyCorrectnessProof());
            List<String> attrList;
            attrList = JsonUtil.extractCptProperties(cpt.getCptJsonSchema());
            AttributeTemplate.Builder builder = AttributeTemplate.newBuilder();
            for (String attr : attrList) {
                builder.addAttributeKey(attr);
            }
            AttributeTemplate attributes = builder.build();
            credentialTemplateEntity.setCredentialSchema(attributes);
            return new ResponseData<>(credentialTemplateEntity, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[processTemplate] process credential template failed.", e);
            return new ResponseData<>(null, ErrorCode.CPT_CREDENTIAL_TEMPLATE_SAVE_ERROR);
        }
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

    /**
     * Save a new Policy with a random Policy ID, to the local file.
     *
     * @param policyJson the policyJson
     * @param auth the WeIdAuthentication
     * @return response data
     */
    public ResponseData<Integer> registerPolicy(String policyJson, WeIdAuthentication auth) {
        if (!DataToolUtils.isValidJsonStr(policyJson)) {
            logger.error("[registerPolicy] input json format illegal.");
            return new ResponseData<>(-1, ErrorCode.CPT_JSON_SCHEMA_INVALID);
        }
        ErrorCode errorCode = CredentialPojoUtils.isWeIdAuthenticationValid(auth);
        if (errorCode != ErrorCode.SUCCESS) {
            return new ResponseData<>(-1, errorCode);
        }
        try {
            CptMapArgs cptMapArgs = new CptMapArgs();
            cptMapArgs.setWeIdAuthentication(auth);
            Map<String, Object> cptJsonSchemaMap = DataToolUtils.deserialize(policyJson, HashMap.class);
            cptMapArgs.setCptJsonSchema(cptJsonSchemaMap);
            WeIdPrivateKey weIdPrivateKey = auth.getWeIdPrivateKey();
            String cptJsonSchemaNew = DataToolUtils.serialize(cptMapArgs.getCptJsonSchema());
            ClaimPolicy claimPolicy = new ClaimPolicy();
            claimPolicy.setFieldsToBeDisclosed(cptJsonSchemaNew);
            //把policy保存到本地文件，名字为：Policy[policyId]
            Integer policyId = new Random().nextInt(100);
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("Policy"+policyId);
            // if file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            mapper.writeValue(file, claimPolicy);
            return new ResponseData<>(policyId, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[registerPolicyData] register policy failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * Get Claim Policy Json from local file given a policy ID.
     *
     * @param policyId the Claim Policy ID
     * @return the claim Json
     */
    public ResponseData<ClaimPolicy> getClaimPolicy(Integer policyId) {
        try {
            if (policyId == null || policyId < 0) {
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("Policy"+policyId);
            // if file does not exists, then create it
            if (!file.exists()) {
                logger.error("[getClaimPolicy] policy file not exist in local");
                return new ResponseData<>(null, ErrorCode.CREDENTIAL_CLAIM_POLICY_NOT_EXIST);
            }
            ClaimPolicy claimPolicy = mapper.readValue(file, ClaimPolicy.class);
            return new ResponseData<>(claimPolicy, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[getClaimPolicy] query policy failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * Register Presentation Policy which contains a number of claim policies.
     *
     * @param claimPolicyIdList claim policies list
     * @param weIdAuthentication weid auth
     * @return the presentation policy id
     */
    public ResponseData<Integer> registerPresentationPolicy(List<Integer> claimPolicyIdList,
                                                            WeIdAuthentication weIdAuthentication) {
        ErrorCode errorCode = CredentialPojoUtils.isWeIdAuthenticationValid(weIdAuthentication);
        if (errorCode != ErrorCode.SUCCESS) {
            return new ResponseData<>(-1, errorCode);
        }
        try {
            int presentationPolicyId = new Random().nextInt(100);
            PresentationPolicyE presentationPolicyE = new PresentationPolicyE();
            Map<Integer, ClaimPolicy> policyMap = new HashMap<>();
            for (Integer id : claimPolicyIdList) {
                policyMap.put(id, getClaimPolicy(id).getResult());
            }
            presentationPolicyE.setId(presentationPolicyId);
            presentationPolicyE.setPolicyPublisherWeId(WeIdUtils.getWeIdFromPrivateKey(weIdAuthentication.getWeIdPrivateKey().getPrivateKey()));
            presentationPolicyE.setPolicy(policyMap);
            //把Presentation policy保存到本地文件，名字为：Presentation[presentationId]
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("Presentation"+presentationPolicyId);
            // if file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            mapper.writeValue(file, presentationPolicyE);
            return new ResponseData<>(presentationPolicyId, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[registerPresentationPolicy] register Presentation failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * Get Presentation policies under this id from local file.
     *
     * @param presentationPolicyId presentation policy id
     * @return the full presentation policy
     */
    public ResponseData<PresentationPolicyE> getPresentationPolicy(Integer presentationPolicyId) {
        if (presentationPolicyId == null || presentationPolicyId < 0) {
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("Presentation"+presentationPolicyId);
            // if file does not exists, then create it
            if (!file.exists()) {
                logger.error("[getPresentationPolicy] Presentation policy file not exist in local");
                return new ResponseData<>(null, ErrorCode.POLICY_SERVICE_NOT_EXISTS);
            }
            PresentationPolicyE presentationPolicyE = mapper.readValue(file, PresentationPolicyE.class);
            return new ResponseData<>(presentationPolicyE, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[getPresentationPolicy] query Presentation policy failed due to unknown error. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

}
