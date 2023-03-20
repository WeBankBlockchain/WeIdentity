package com.webank.weid.service.local;

import com.webank.weid.blockchain.constant.ChainType;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.constant.WeIdConstant;
import com.webank.weid.blockchain.protocol.base.CptBaseInfo;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.blockchain.protocol.response.RsvSignature;
import com.webank.weid.blockchain.rpc.CptService;
import com.webank.weid.blockchain.service.impl.CptServiceImpl;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.suite.persistence.Persistence;
import com.webank.weid.suite.persistence.PersistenceFactory;
import com.webank.weid.suite.persistence.PersistenceType;
import com.webank.weid.blockchain.util.DataToolUtils;
import com.webank.weid.util.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CptServiceLocal implements CptService {
    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(CptServiceLocal.class);

    private static Persistence dataDriver;
    private static PersistenceType persistenceType;
    public static Integer AUTHORITY_ISSUER_START_ID = 1000;
    public static Integer NONE_AUTHORITY_ISSUER_START_ID = 2000000;
    private Integer authority_issuer_current_id = 1000;
    private Integer none_authority_issuer_current_id = 2000000;
    WeIdServiceLocal weIdServiceLocal = new WeIdServiceLocal();

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
        if(!weIdServiceLocal.isWeIdExist(address).getResult()){
            logger.error("[registerCpt] the weid of publisher does not exist on blockchain");
            return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
        }
        if(getDataDriver().getCpt(DataDriverConstant.LOCAL_CPT, cptId).getResult() != null){
            logger.error("[registerCpt] cpt already exist on chain");
            return new ResponseData<>(null, ErrorCode.CPT_ALREADY_EXIST);
        }
        //TODO:补充权限检验逻辑，检验publisher和cptId是否等级对应
        if(cptId < AUTHORITY_ISSUER_START_ID){

        }else if(cptId < NONE_AUTHORITY_ISSUER_START_ID){

        }
        ResponseData<CptBaseInfo> resp =
                getDataDriver().addCpt(
                        DataDriverConstant.LOCAL_CPT,
                        cptId,
                        address,
                        null,
                        cptJsonSchemaNew,
                        DataToolUtils.SigBase64Serialization(rsvSignature));
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[createWeId] save cpt to db failed.");
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
        if(!weIdServiceLocal.isWeIdExist(address).getResult()){
            logger.error("[registerCpt] the weid of publisher does not exist on blockchain");
            return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
        }
        //TODO: 增加一个json文件来记录系统状态变量authority_issuer_current_id和none_authority_issuer_current_id
        //获取一个可用的对应身份的cptId
        if(getDataDriver().getCpt(DataDriverConstant.LOCAL_CPT, cptId).getResult() != null){
            logger.error("[registerCpt] cpt already exist on chain");
            return new ResponseData<>(null, ErrorCode.CPT_ALREADY_EXIST);
        }

        ResponseData<CptBaseInfo> resp =
                getDataDriver().addCpt(
                        DataDriverConstant.LOCAL_CPT,
                        cptId,
                        address,
                        null,
                        cptJsonSchemaNew,
                        DataToolUtils.SigBase64Serialization(rsvSignature));
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[createWeId] save cpt to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(resp.getResult(), ErrorCode.SUCCESS);
    }

}
