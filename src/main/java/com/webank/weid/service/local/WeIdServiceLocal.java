package com.webank.weid.service.local;

import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.protocol.base.ServiceProperty;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdDocumentMetadata;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.blockchain.rpc.WeIdService;
import com.webank.weid.util.WeIdUtils;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.suite.persistence.Persistence;
import com.webank.weid.suite.persistence.PersistenceFactory;
import com.webank.weid.suite.persistence.PersistenceType;
import com.webank.weid.util.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("weIdServiceLocal")
public class WeIdServiceLocal implements WeIdService {

    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(WeIdServiceLocal.class);
    private static Persistence dataDriver;
    private static PersistenceType persistenceType;

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
     * Check if WeIdentity DID exists on Chain.
     *
     * @param weId the WeIdentity DID
     * @return true if exists, false otherwise
     */
    @Override
    public ResponseData<Boolean> isWeIdExist(String weId) {
        try {
            ResponseData<com.webank.weid.blockchain.protocol.base.WeIdDocumentMetadata> dbResp = this.getWeIdDocumentMetadata(weId);
            if(dbResp.getResult() != null){
                return new ResponseData<>(true, ErrorCode.SUCCESS);
            }
            return new ResponseData<>(false, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[isWeIdExist] execute failed. Error message :{}", e);
            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * Check if WeIdentity DID is deactivated on Chain.
     *
     * @param weId the WeIdentity DID
     * @return true if is deactivated, false otherwise
     */
    @Override
    public ResponseData<Boolean> isDeactivated(String weId) {
        try {
            ResponseData<com.webank.weid.blockchain.protocol.base.WeIdDocumentMetadata> dbResp = this.getWeIdDocumentMetadata(weId);
            return new ResponseData<>(dbResp.getResult().isDeactivated(), ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[isDeactivated] execute failed. Error message :{}", e);
            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
        }
    }

    @Override
    public ResponseData<Boolean> createWeId(
            String address,
            List<String> authList,
            List<String> serviceList,
            String privateKey) {
        try {
            if(authList.size()==0 || serviceList.size()==0){
                return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
            }
            String weId = WeIdUtils.convertAddressToWeId(address);
            //如果已经存在该weId则报错
            if(this.isWeIdExist(weId).getResult()){
                return new ResponseData<>(false, ErrorCode.WEID_ALREADY_EXIST);
            }
            //创建weIdDocument插入db
            WeIdDocument weIdDocument = new WeIdDocument();
            weIdDocument.setId(weId);
            List<AuthenticationProperty> authenticationList = new ArrayList<>();
            for(String authenticationStr : authList){
                AuthenticationProperty authenticationProperty = AuthenticationProperty.fromString(authenticationStr);
                authenticationList.add(authenticationProperty);
            }
            weIdDocument.setAuthentication(authenticationList);
            List<ServiceProperty> serList = new ArrayList<>();
            for(String serviceStr : serviceList){
                ServiceProperty serviceProperty = ServiceProperty.fromString(serviceStr);
                serList.add(serviceProperty);
            }
            weIdDocument.setService(serList);
            ResponseData<Integer> resp =
                    getDataDriver().addWeId(
                            DataDriverConstant.LOCAL_WEID_DOCUMENT,
                            weId,
                            weIdDocument.toJson());
            if (resp.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[createWeId] save weIdDocument to db failed.");
                throw new DatabaseException("database error!");
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[createWeId] create weid failed with exception. ", e);
            return new ResponseData<>(false, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    /**
     * Get a WeIdentity DID Document.
     *
     * @param weId the WeIdentity DID
     * @return the WeIdentity DID document
     */
    @Override
    public ResponseData<com.webank.weid.blockchain.protocol.base.WeIdDocument> getWeIdDocument(String weId) {
        try {
            return getDataDriver().getWeIdDocument(
                    DataDriverConstant.LOCAL_WEID_DOCUMENT,
                    weId);
        } catch (Exception e) {
            logger.error("[getWeIdDocument] execute failed. Error message :{}", e);
            return new ResponseData<>(null, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    /**
     * Get a WeIdentity DID Document Metadata.
     *
     * @param weId the WeIdentity DID
     * @return the WeIdentity DID document
     */
    @Override
    public ResponseData<com.webank.weid.blockchain.protocol.base.WeIdDocumentMetadata> getWeIdDocumentMetadata(String weId) {
        try {
            return getDataDriver().getMeta(
                    DataDriverConstant.LOCAL_WEID_DOCUMENT,
                    weId);
        } catch (Exception e) {
            logger.error("[getWeIdDocumentMetadata] execute failed. Error message :{}", e);
            return new ResponseData<>(null, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    /**
     * call weid contract to update the weid document.
     *
     * @param weIdDocument weIdDocument on blockchain
     * @param address address of the identity
     * @param privateKey privateKey identity's private key
     * @return result
     */
    @Override
    public ResponseData<Boolean> updateWeId(
            com.webank.weid.blockchain.protocol.base.WeIdDocument weIdDocument,
            String privateKey,
            String address) {
        try {
            String weId = WeIdUtils.getWeIdFromPrivateKey(privateKey);
            if(!weId.equals(WeIdUtils.convertAddressToWeId(address))){
                logger.error("[updateWeId] the private key does not match the current weid.");
                return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
            }
            ResponseData<Integer> resp =
                    getDataDriver().updateWeId(
                            DataDriverConstant.LOCAL_WEID_DOCUMENT,
                            weId,
                            weIdDocument.toJson());
            if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[updateWeId] updateWeId weIdDocument to db failed.");
                throw new DatabaseException("database error!");
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[updateWeId] update weid has error, Error Message：{}", e);
            return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
        }
    }

    @Override
    public ResponseData<List<String>> getWeIdList(
            Integer first,
            Integer last
    ) {
        try {
            return getDataDriver().getWeIdList(
                    DataDriverConstant.LOCAL_WEID_DOCUMENT,
                    first,
                    last);
        } catch (Exception e) {
            logger.error("[getWeIdList] getWeIdList has error, Error Message：{}", e);
            return new ResponseData<>(null, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    @Override
    public ResponseData<Integer> getWeIdCount() {
        try {
            return getDataDriver().getWeIdCount(DataDriverConstant.LOCAL_WEID_DOCUMENT);
        } catch (Exception e) {
            logger.error("[getWeIdCount] getWeIdCount has error, Error Message：{}", e);
            return new ResponseData<>(0, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    /*@Override
    public ResponseData<Boolean> deactivateWeId(
            String weAddress,
            String privateKey) {
        try {
            String weId = WeIdUtils.getWeIdFromPrivateKey(privateKey);
            if(!weId.equals(weAddress)){
                logger.error("[updateWeId] the private key does not match the current weid.");
                return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
            }
            ResponseData<Integer> resp =
                    getDataDriver().deactivateData(
                            DataDriverConstant.DOMAIN_WEID_DOCUMENT,
                            weAddress,
                            true);
            if (resp.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[deactivateWeId] deactivateWeId failed.");
                throw new DatabaseException("database error!");
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[deactivateWeId] deactivate WeId has error, Error Message：{}", e);
            return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
        }
    }*/
}
