package com.webank.weid.service.local;

import com.webank.weid.blockchain.constant.ChainType;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.constant.WeIdConstant;
import com.webank.weid.blockchain.protocol.base.AuthorityIssuer;
import com.webank.weid.blockchain.protocol.base.CptBaseInfo;
import com.webank.weid.blockchain.protocol.base.IssuerType;
import com.webank.weid.blockchain.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.blockchain.rpc.AuthorityIssuerService;
import com.webank.weid.blockchain.service.impl.AuthorityIssuerServiceImpl;
import com.webank.weid.blockchain.util.DataToolUtils;
import com.webank.weid.util.WeIdUtils;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.contract.deploy.AddressProcess;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.service.local.role.RoleController;
import com.webank.weid.suite.persistence.*;
import com.webank.weid.suite.persistence.mysql.SqlDomain;
import com.webank.weid.suite.persistence.mysql.SqlExecutor;
import com.webank.weid.util.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("authorityIssuerServiceLocal")
public class AuthorityIssuerServiceLocal implements AuthorityIssuerService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorityIssuerServiceLocal.class);

    private static Persistence dataDriver;
    private static PersistenceType persistenceType;
    WeIdServiceLocal weIdServiceLocal = new WeIdServiceLocal();
    RoleController roleController = new RoleController();

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
     * add a new Authority Issuer on Chain.
     *
     * @param args the args
     * @return the Boolean response data
     */
    @Override
    public ResponseData<Boolean> addAuthorityIssuer(RegisterAuthorityIssuerArgs args) {
        AuthorityIssuer authorityIssuer = args.getAuthorityIssuer();
        if(!com.webank.weid.blockchain.util.WeIdUtils.isPrivateKeyValid(args.getWeIdPrivateKey())){
            logger.error("[addAuthorityIssuer] the privateKey of issuer is not valid");
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
        //如果不存在该weId则报错
        if(!weIdServiceLocal.isWeIdExist(authorityIssuer.getWeId()).getResult()){
            logger.error("[addAuthorityIssuer] the weid of authority does not exist on blockchain");
            return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
        }
        if(getDataDriver().getAuthorityIssuerByWeId(DataDriverConstant.LOCAL_AUTHORITY_ISSUER, authorityIssuer.getWeId()).getResult() != null){
            logger.error("[addAuthorityIssuer] Authority Issuer already exist");
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_ALREADY_EXIST);
        }
        if(getDataDriver().getAuthorityIssuerByName(DataDriverConstant.LOCAL_AUTHORITY_ISSUER, authorityIssuer.getName()).getResult() != null){
            logger.error("[addAuthorityIssuer] Authority Issuer's name already exist");
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NAME_ALREADY_EXISTS);
        }
        String extraStr = null;
        if(authorityIssuer.getExtraStr32().size() != 0){
            extraStr = authorityIssuer.getExtraStr32().get(0);
            if(authorityIssuer.getExtraStr32().size() > 1) {
                for(int i = 1; i < authorityIssuer.getExtraStr32().size(); i++){
                    extraStr = extraStr + ',' + authorityIssuer.getExtraStr32().get(i);
                }
            }
        }
        String extraInt = null;
        if(authorityIssuer.getExtraInt().size() != 0) {
            extraInt = String.valueOf(authorityIssuer.getExtraInt().get(0));
            if (authorityIssuer.getExtraInt().size() > 1) {
                for (int i = 1; i < authorityIssuer.getExtraInt().size(); i++) {
                    extraInt = extraInt + ',' + authorityIssuer.getExtraInt().get(i);
                }
            }
        }
        ResponseData<Integer> resp =
                getDataDriver().addAuthorityIssuer(
                        DataDriverConstant.LOCAL_AUTHORITY_ISSUER,
                        authorityIssuer.getWeId(),
                        authorityIssuer.getName(),
                        authorityIssuer.getDescription(),
                        authorityIssuer.getAccValue(),
                        extraStr,
                        extraInt
                        );
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[addAuthorityIssuer] save addAuthorityIssuer to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    /**
     * Remove a new Authority Issuer on Chain.
     *
     * @param weId the weId
     * @param privateKey the privateKey
     * @return the Boolean response data
     */
    @Override
    public ResponseData<Boolean> removeAuthorityIssuer(String weId, String privateKey) {
        if(!RoleController.checkPermission(WeIdUtils.getWeIdFromPrivateKey(privateKey), RoleController.MODIFY_AUTHORITY_ISSUER)){
            logger.error("[removeAuthorityIssuer] operator has not permission to removeAuthorityIssuer");
            return new ResponseData<>(false, ErrorCode.CONTRACT_ERROR_NO_PERMISSION);
        }
        if(getDataDriver().getAuthorityIssuerByWeId(DataDriverConstant.LOCAL_AUTHORITY_ISSUER, weId).getResult() == null){
            logger.error("[removeAuthorityIssuer] Authority Issuer not exist");
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS);
        }
        if(roleController.checkRole(weId, RoleController.ROLE_AUTHORITY_ISSUER) && !roleController.removeRole(WeIdUtils.getWeIdFromPrivateKey(privateKey), weId, RoleController.ROLE_AUTHORITY_ISSUER)) {
            logger.error("[removeAuthorityIssuer] remove AuthorityIssuer role from db failed.");
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
        }
        ResponseData<Integer> resp =
                getDataDriver().removeAuthorityIssuer(
                        DataDriverConstant.LOCAL_AUTHORITY_ISSUER,
                        weId);
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[removeAuthorityIssuer] delete addAuthorityIssuer from db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    /**
     * Check whether the given weId is an authority issuer.
     *
     * @param addr the address of WeIdentity DID
     * @return the Boolean response data
     */
    @Override
    public ResponseData<Boolean> isAuthorityIssuer(String addr) {
        if(!RoleController.checkRole(WeIdUtils.convertAddressToWeId(addr), RoleController.ROLE_AUTHORITY_ISSUER)){
            return new ResponseData<>(false, ErrorCode.SUCCESS);
        }
        if(getDataDriver().getAuthorityIssuerByWeId(DataDriverConstant.LOCAL_AUTHORITY_ISSUER, WeIdUtils.convertAddressToWeId(addr)).getResult() == null){
            return new ResponseData<>(false, ErrorCode.SUCCESS);
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    /**
     * Recognize this WeID to be an authority issuer.
     *
     * @param stage the stage that weather recognize
     * @param addr the address of WeIdentity DID
     * @param privateKey the private key set
     * @return true if succeeds, false otherwise
     */
    @Override
    public ResponseData<Boolean> recognizeWeId(Boolean stage, String addr, String privateKey) {
        if(!RoleController.checkPermission(WeIdUtils.getWeIdFromPrivateKey(privateKey), RoleController.MODIFY_AUTHORITY_ISSUER)){
            logger.error("[recognizeWeId] operator has not permission to recognizeWeId");
            return new ResponseData<>(false, ErrorCode.CONTRACT_ERROR_NO_PERMISSION);
        }
        if(getDataDriver().getAuthorityIssuerByWeId(DataDriverConstant.LOCAL_AUTHORITY_ISSUER, WeIdUtils.convertAddressToWeId(addr)).getResult() == null){
            logger.error("[recognizeWeId] Authority Issuer not exist");
            return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS);
        }
        if(stage){
            if(!roleController.addRole(WeIdUtils.getWeIdFromPrivateKey(privateKey), WeIdUtils.convertAddressToWeId(addr), RoleController.ROLE_AUTHORITY_ISSUER)){
                logger.error("[recognizeWeId] add AuthorityIssuer role to db failed.");
                return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
            }
            ResponseData<Integer> resp = getDataDriver().updateAuthorityIssuer(DataDriverConstant.LOCAL_AUTHORITY_ISSUER, WeIdUtils.convertAddressToWeId(addr), 1);
            if(resp.getErrorCode() != ErrorCode.SUCCESS.getCode()){
                logger.error("[recognizeWeId] update AuthorityIssuer recognize to db failed.");
                throw new DatabaseException("database error!");
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS);
        } else {
            if(!roleController.removeRole(WeIdUtils.getWeIdFromPrivateKey(privateKey), WeIdUtils.convertAddressToWeId(addr), RoleController.ROLE_AUTHORITY_ISSUER)){
                logger.error("[recognizeWeId] remove AuthorityIssuer role from db failed.");
                return new ResponseData<>(false, ErrorCode.AUTHORITY_ISSUER_ERROR);
            }
            ResponseData<Integer> resp = getDataDriver().updateAuthorityIssuer(DataDriverConstant.LOCAL_AUTHORITY_ISSUER, WeIdUtils.convertAddressToWeId(addr), 0);
            if(resp.getErrorCode() != ErrorCode.SUCCESS.getCode()){
                logger.error("[recognizeWeId] update AuthorityIssuer deRecognize to db failed.");
                throw new DatabaseException("database error!");
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS);
        }
    }

    /**
     * Query the authority issuer information given weId.
     *
     * @param weId the WeIdentity DID
     * @return the AuthorityIssuer response data
     */
    @Override
    public ResponseData<AuthorityIssuer> queryAuthorityIssuerInfo(String weId) {
        ResponseData<AuthorityIssuer> resultData = new ResponseData<AuthorityIssuer>();
        AuthorityIssuer result = new AuthorityIssuer();
        AuthorityIssuerInfo authorityIssuerInfo = getDataDriver().getAuthorityIssuerByWeId(DataDriverConstant.LOCAL_AUTHORITY_ISSUER, weId).getResult();
        if(authorityIssuerInfo == null){
            logger.error("[queryAuthorityIssuerInfo] Authority Issuer not exist");
            return new ResponseData<>(null, ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS);
        }
        result.setWeId(weId);
        result.setName(authorityIssuerInfo.getName());
        result.setAccValue(authorityIssuerInfo.getAcc_value());
        result.setCreated(authorityIssuerInfo.getCreated().getTime());
        result.setDescription(authorityIssuerInfo.getDescription());
        result.setRecognized(authorityIssuerInfo.getRecognize()==1);
        if(authorityIssuerInfo.getExtra_str() != null){
            result.setExtraStr32(Arrays.asList(authorityIssuerInfo.getExtra_str().split(",")));
        }
        if(authorityIssuerInfo.getExtra_int() != null){
            List<Integer> extraInt = new ArrayList<>();
            for(String i:authorityIssuerInfo.getExtra_int().split(",")){
                extraInt.add(Integer.valueOf(i));
            }
            result.setExtraInt(extraInt);
        }
        resultData.setResult(result);
        return resultData;
    }

    /**
     * Get all of the authority issuer.
     *
     * @param index start position
     * @param num number of returned authority issuer in this request
     * @return Execution result
     */
    @Override
    public ResponseData<List<String>> getAuthorityIssuerAddressList(Integer index, Integer num) {
        try {
            return getDataDriver().getWeIdList(
                    DataDriverConstant.LOCAL_AUTHORITY_ISSUER,
                    index,
                    index + num);
        } catch (Exception e) {
            logger.error("[getAuthorityIssuerAddressList] getAuthorityIssuerAddressList has error, Error Message：{}", e);
            return new ResponseData<>(null, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    /**
     * Register a new issuer type.
     *
     * @param privateKey the caller
     * @param issuerType the specified issuer type
     * @return Execution result
     */
    @Override
    public ResponseData<Boolean> registerIssuerType(
            String privateKey,
            String issuerType
    ) {
        if (StringUtils.isEmpty(issuerType) || StringUtils.isEmpty(privateKey)) {
            logger.error("[registerIssuerType] input argument is illegal");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if(getDataDriver().getSpecificType(DataDriverConstant.LOCAL_SPECIFIC_ISSUER, issuerType).getResult() != null){
            logger.error("[registerIssuerType] issuerType already exist on chain");
            return new ResponseData<>(false, ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_EXISTS);
        }
        ResponseData<Integer> resp =
                getDataDriver().addSpecificType(
                        DataDriverConstant.LOCAL_SPECIFIC_ISSUER,
                        issuerType,
                        WeIdUtils.getWeIdFromPrivateKey(privateKey));
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[registerIssuerType] save IssuerType to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    /**
     * Marked an issuer as the specified issuer type.
     *
     * @param privateKey the caller who have the access to modify this list
     * @param issuerType the specified issuer type
     * @param issuerAddress the address of the issuer who will be marked as a specific issuer type
     * @return Execution result
     */
    @Override
    public ResponseData<Boolean> addIssuer(
            String privateKey,
            String issuerType,
            String issuerAddress
    ) {
        if (StringUtils.isEmpty(issuerType) || StringUtils.isEmpty(issuerAddress) ||StringUtils.isEmpty(privateKey)) {
            logger.error("[addIssuer] input argument is illegal");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if(!RoleController.checkPermission(WeIdUtils.getWeIdFromPrivateKey(privateKey), RoleController.MODIFY_KEY_CPT)){
            logger.error("[addIssuer] operator has not permission to addIssuer");
            return new ResponseData<>(false, ErrorCode.CONTRACT_ERROR_NO_PERMISSION);
        }
        SpecificTypeValue specificTypeValue = getDataDriver().getSpecificType(DataDriverConstant.LOCAL_SPECIFIC_ISSUER, issuerType).getResult();
        if(specificTypeValue == null){
            logger.error("[addIssuer] issuerType not exist on chain");
            return new ResponseData<>(false, ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST);
        }
        //判断这个issuerType是否已经有fellow
        if(specificTypeValue.getFellow() != null){
            //判断是否已经存在这个issuer
            String[] fellows = specificTypeValue.getFellow().split(",");
            for (String obj : fellows) {
                if(obj.equals(issuerAddress)) return new ResponseData<>(false, ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_EXISTS);
            }
            ResponseData<Integer> resp =
                    getDataDriver().updateSpecificTypeFellow(
                            DataDriverConstant.LOCAL_SPECIFIC_ISSUER,
                            issuerType,
                            specificTypeValue.getFellow()+','+issuerAddress);
            if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[addIssuer] addIssuer to issuerType failed.");
                throw new DatabaseException("database error!");
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS);
        } else {
            ResponseData<Integer> resp =
                    getDataDriver().updateSpecificTypeFellow(
                            DataDriverConstant.LOCAL_SPECIFIC_ISSUER,
                            issuerType,
                            issuerAddress);
            if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[addIssuer] addIssuer to issuerType failed.");
                throw new DatabaseException("database error!");
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS);
        }
    }

    /**
     * Removed an issuer from the specified issuer list.
     *
     * @param privateKey the caller who have the access to modify this list
     * @param issuerType the specified issuer type
     * @param issuerAddress the address of the issuer who will be marked as a specific issuer type
     * @return Execution result
     */
    @Override
    public ResponseData<Boolean> removeIssuer(
            String privateKey,
            String issuerType,
            String issuerAddress
    ) {
        if (StringUtils.isEmpty(issuerType) || StringUtils.isEmpty(issuerAddress) ||StringUtils.isEmpty(privateKey)) {
            logger.error("[removeIssuer] input argument is illegal");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if(!RoleController.checkPermission(WeIdUtils.getWeIdFromPrivateKey(privateKey), RoleController.MODIFY_KEY_CPT)){
            logger.error("[removeIssuer] operator has not permission to removeIssuer");
            return new ResponseData<>(false, ErrorCode.CONTRACT_ERROR_NO_PERMISSION);
        }
        SpecificTypeValue specificTypeValue = getDataDriver().getSpecificType(DataDriverConstant.LOCAL_SPECIFIC_ISSUER, issuerType).getResult();
        if(!isSpecificTypeIssuer(issuerType, issuerAddress).getResult()){
            logger.error("[removeIssuer] issuerAddress not the fellow of issuerType");
            return new ResponseData<>(false, ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST);
        }
        String[] fellows = specificTypeValue.getFellow().split(",");
        List<String> fellowList = new ArrayList<>(Arrays.asList(fellows));
        fellowList.remove(issuerAddress);
        String newFellow = fellowList.get(0);
        if(fellowList.size() != 1) {
            for(int i = 1; i < fellowList.size(); i++){
                newFellow = newFellow + ',' + fellowList.get(i);
            }
        }
        ResponseData<Integer> resp =
                getDataDriver().updateSpecificTypeFellow(
                        DataDriverConstant.LOCAL_SPECIFIC_ISSUER,
                        issuerType,
                        newFellow);
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[removeIssuer] removeIssuer from issuerType failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    /**
     * Check if the given WeId is belonging to a specific issuer type.
     *
     * @param issuerType the issuer type
     * @param address the address
     * @return true if yes, false otherwise
     */
    @Override
    public ResponseData<Boolean> isSpecificTypeIssuer(
            String issuerType,
            String address
    ) {
        if (StringUtils.isEmpty(issuerType) || StringUtils.isEmpty(address)) {
            logger.error("[isSpecificTypeIssuer] input argument is illegal");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        SpecificTypeValue specificTypeValue = getDataDriver().getSpecificType(DataDriverConstant.LOCAL_SPECIFIC_ISSUER, issuerType).getResult();
        if(specificTypeValue == null){
            logger.error("[isSpecificTypeIssuer] issuerType not exist on chain");
            return new ResponseData<>(false, ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST);
        }
        if(specificTypeValue.getFellow().equals(StringUtils.EMPTY)){
            logger.error("[isSpecificTypeIssuer] issuerType has not fellow");
            return new ResponseData<>(false, ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST);
        }
        String[] fellows = specificTypeValue.getFellow().split(",");
        for (String obj : fellows) {
            if(obj.equals(address)) return new ResponseData<>(true, ErrorCode.SUCCESS);
        }
        return new ResponseData<>(false, ErrorCode.SUCCESS);
    }

    /**
     * Get all specific typed issuer in a list.
     *
     * @param issuerType the issuer type
     * @param index the start position index
     * @param num the number of issuers
     * @return the list
     */
    @Override
    public ResponseData<List<String>> getAllSpecificTypeIssuerList(
            String issuerType,
            Integer index,
            Integer num
    ) {
        SpecificTypeValue specificTypeValue = getDataDriver().getSpecificType(DataDriverConstant.LOCAL_SPECIFIC_ISSUER, issuerType).getResult();
        if(specificTypeValue == null || specificTypeValue.getFellow().equals(StringUtils.EMPTY)){
            logger.error("[getAllSpecificTypeIssuerList] issuerType not exist on chain");
            return new ResponseData<>(null, ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST);
        }
        String[] fellows = specificTypeValue.getFellow().split(",");
        List<String> fellowList = Arrays.asList(fellows);
        if (fellows.length<=index) {
            logger.error("[getAllSpecificTypeIssuerList] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        if(fellows.length>index + num - 1){
            return new ResponseData<>(fellowList.subList(index, index + num), ErrorCode.SUCCESS);
        } else {
            return new ResponseData<>(fellowList.subList(index, fellows.length - 1), ErrorCode.SUCCESS);
        }
    }

    @Override
    public ResponseData<String> getWeIdFromOrgId(String orgId) {
        if (StringUtils.isEmpty(orgId)) {
            logger.error("[getWeIdFromOrgId] input argument is illegal");
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
        }
        AuthorityIssuerInfo authorityIssuerInfo = getDataDriver().getAuthorityIssuerByName(DataDriverConstant.LOCAL_AUTHORITY_ISSUER, orgId).getResult();
        if (WeIdConstant.EMPTY_ADDRESS.equalsIgnoreCase(WeIdUtils.convertWeIdToAddress(authorityIssuerInfo.getWeid()))) {
            return new ResponseData<>(StringUtils.EMPTY,
                    ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS);
        }
        return new ResponseData<>(authorityIssuerInfo.getWeid(), ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<Integer> getIssuerCount() {
        try {
            return getDataDriver().getAuthorityIssuerCount(DataDriverConstant.LOCAL_AUTHORITY_ISSUER);
        } catch (Exception e) {
            logger.error("[getIssuerCount] getIssuerCount has error, Error Message：{}", e);
            return new ResponseData<>(0, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    @Override
    public ResponseData<Integer> getRecognizedIssuerCount() {
        try {
            return getDataDriver().getRecognizedIssuerCount(DataDriverConstant.LOCAL_AUTHORITY_ISSUER);
        } catch (Exception e) {
            logger.error("[getRecognizedIssuerCount] getRecognizedIssuerCount has error, Error Message：{}", e);
            return new ResponseData<>(0, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    @Override
    public ResponseData<Integer> getSpecificTypeIssuerSize(String issuerType) {
        if (StringUtils.isEmpty(issuerType)) {
            logger.error("[getSpecificTypeIssuerSize] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        SpecificTypeValue specificTypeValue = getDataDriver().getSpecificType(DataDriverConstant.LOCAL_SPECIFIC_ISSUER, issuerType).getResult();
        if(specificTypeValue == null){
            logger.error("[getSpecificTypeIssuerSize] issuerType not exist on chain");
            return new ResponseData<>(null, ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST);
        }
        if(specificTypeValue.getFellow() == null){
            logger.error("[getSpecificTypeIssuerSize] issuerType has not fellow");
            return new ResponseData<>(0, ErrorCode.SUCCESS);
        }
        String[] fellows = specificTypeValue.getFellow().split(",");
        return new ResponseData<>(fellows.length, ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<Integer> getIssuerTypeCount() {
        try {
            return getDataDriver().getIssuerTypeCount(DataDriverConstant.LOCAL_SPECIFIC_ISSUER);
        } catch (Exception e) {
            logger.error("[getIssuerTypeCount] getIssuerTypeCount has error, Error Message：{}", e);
            return new ResponseData<>(0, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

    @Override
    public ResponseData<Boolean> removeIssuerType(
            String privateKey,
            String issuerType
    ) {
        if (StringUtils.isEmpty(issuerType) ||StringUtils.isEmpty(privateKey)) {
            logger.error("[removeIssuerType] input argument is illegal");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        SpecificTypeValue specificTypeValue = getDataDriver().getSpecificType(DataDriverConstant.LOCAL_SPECIFIC_ISSUER, issuerType).getResult();
        if(specificTypeValue.getFellow() != null){
            logger.error("[removeIssuerType] has issuer in the specific issuer type");
            return new ResponseData<>(false, ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_EXIST_ISSUER);
        }
        if(!specificTypeValue.getOwner().equals(WeIdUtils.getWeIdFromPrivateKey(privateKey))){
            logger.error("[removeIssuerType] no permission to removeIssuerType");
            return new ResponseData<>(false, ErrorCode.CONTRACT_ERROR_NO_PERMISSION);
        }
        ResponseData<Integer> resp =
                getDataDriver().removeSpecificType(
                        DataDriverConstant.LOCAL_SPECIFIC_ISSUER,
                        issuerType);
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[removeIssuerType] delete IssuerType from db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<List<IssuerType>> getIssuerTypeList(Integer index, Integer num) {
        try {
            List<String> typeNameList= getDataDriver().getIssuerTypeList(
                    DataDriverConstant.LOCAL_SPECIFIC_ISSUER,
                    index,
                    index + num).getResult();
            List<IssuerType> issuerTypeList = new ArrayList<>();
            for (String typeName : typeNameList) {
                SpecificTypeValue specificTypeValue = getDataDriver().getSpecificType(DataDriverConstant.LOCAL_SPECIFIC_ISSUER, typeName).getResult();
                IssuerType issuerType = new IssuerType();
                issuerType.setTypeName(typeName);
                issuerType.setCreated(specificTypeValue.getCreated().getTime());
                issuerType.setOwner(specificTypeValue.getOwner());
                issuerTypeList.add(issuerType);
            }
            return new ResponseData<>(issuerTypeList, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[getIssuerTypeList] getIssuerTypeList has error, Error Message：{}", e);
            return new ResponseData<>(null, ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
    }

}
