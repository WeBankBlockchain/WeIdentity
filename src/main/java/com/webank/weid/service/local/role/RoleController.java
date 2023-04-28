package com.webank.weid.service.local.role;

import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.service.local.AuthorityIssuerServiceLocal;
import com.webank.weid.service.local.CptServiceLocal;
import com.webank.weid.service.local.WeIdServiceLocal;
import com.webank.weid.suite.persistence.Persistence;
import com.webank.weid.suite.persistence.PersistenceFactory;
import com.webank.weid.suite.persistence.PersistenceType;
import com.webank.weid.suite.persistence.RoleValue;
import com.webank.weid.util.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleController {

    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    /**
     * Role related Constants.
     */
    public static Integer ROLE_AUTHORITY_ISSUER = 100;
    public static Integer ROLE_COMMITTEE = 101;
    public static Integer ROLE_ADMIN = 102;

    /**
     * Operation related Constants.
     */
    public static Integer MODIFY_AUTHORITY_ISSUER = 200;
    public static Integer MODIFY_COMMITTEE = 201;
    public static Integer MODIFY_ADMIN = 202;
    public static Integer MODIFY_KEY_CPT = 203;

    private static Persistence dataDriver;
    private static PersistenceType persistenceType;
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

    public static Boolean checkPermission(String weId, Integer operation) {
        RoleValue result = getDataDriver().getRole(DataDriverConstant.LOCAL_ROLE, weId).getResult();
        if(result == null) {
            return false;
        }
        if(operation == RoleController.MODIFY_AUTHORITY_ISSUER) {
            if(result.getAdmin_role() == 1 || result.getCommittee_role() == 1) {
                return true;
            }
        }
        if(operation == RoleController.MODIFY_COMMITTEE) {
            if(result.getAdmin_role() == 1) {
                return true;
            }
        }
        if(operation == RoleController.MODIFY_ADMIN) {
            if(result.getAdmin_role() == 1) {
                return true;
            }
        }
        if(operation == RoleController.MODIFY_KEY_CPT) {
            if(result.getAuthority_role() == 1) {
                return true;
            }
        }
        return false;
    }

    //operator为操作者，相当于tx.origin, weId为操作对象
    public Boolean addRole(String operator, String weId, Integer role) {
        if(role == RoleController.ROLE_AUTHORITY_ISSUER) {
            //检查操作者权限
            if(checkPermission(operator, RoleController.MODIFY_AUTHORITY_ISSUER)) {
                ResponseData<Integer> resp =
                        getDataDriver().addRole(
                                DataDriverConstant.LOCAL_ROLE,
                                weId,
                                1);
                if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                    logger.error("[addRole] save role to db failed.");
                    throw new DatabaseException("database error!");
                }
                return true;
            }
        }
        if(role == RoleController.ROLE_COMMITTEE) {
            if(checkPermission(operator, RoleController.MODIFY_COMMITTEE)) {
                ResponseData<Integer> resp =
                        getDataDriver().addRole(
                                DataDriverConstant.LOCAL_ROLE,
                                weId,
                                2);
                if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                    logger.error("[addRole] save role to db failed.");
                    throw new DatabaseException("database error!");
                }
                return true;
            }
        }
        if(role == RoleController.ROLE_ADMIN) {
            if(checkPermission(operator, RoleController.MODIFY_ADMIN)) {
                ResponseData<Integer> resp =
                        getDataDriver().addRole(
                                DataDriverConstant.LOCAL_ROLE,
                                weId,
                                3);
                if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                    logger.error("[addRole] save role to db failed.");
                    throw new DatabaseException("database error!");
                }
                return true;
            }
        }
        return false;
    }

    public Boolean removeRole(String operator, String weId, Integer role) {
        RoleValue result = getDataDriver().getRole(DataDriverConstant.LOCAL_ROLE, weId).getResult();
        if(role == RoleController.ROLE_AUTHORITY_ISSUER) {
            if(checkPermission(operator, RoleController.MODIFY_AUTHORITY_ISSUER)) {
                if(result.getAuthority_role() == 1) {
                    ResponseData<Integer> resp =
                            getDataDriver().updateRole(
                                    DataDriverConstant.LOCAL_ROLE,
                                    weId,
                                    computeRoleValue(result)-1);
                    if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                        logger.error("[removeRole] remove role from db failed.");
                        throw new DatabaseException("database error!");
                    }
                    return true;
                }
                logger.error("[removeRole] this weId has not authority role.");
                return false;
            }
        }
        if(role == RoleController.ROLE_COMMITTEE) {
            if(checkPermission(operator, RoleController.MODIFY_COMMITTEE)) {
                if(result.getCommittee_role() == 1) {
                    ResponseData<Integer> resp =
                            getDataDriver().updateRole(
                                    DataDriverConstant.LOCAL_ROLE,
                                    weId,
                                    computeRoleValue(result)-2);
                    if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                        logger.error("[removeRole] remove role from db failed.");
                        throw new DatabaseException("database error!");
                    }
                    return true;
                }
                logger.error("[removeRole] this weId has not committee role.");
                return false;
            }
        }
        if(role == RoleController.ROLE_ADMIN) {
            if(checkPermission(operator, RoleController.MODIFY_ADMIN)) {
                if(result.getAdmin_role() == 1) {
                    ResponseData<Integer> resp =
                            getDataDriver().updateRole(
                                    DataDriverConstant.LOCAL_ROLE,
                                    weId,
                                    computeRoleValue(result)-4);
                    if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                        logger.error("[removeRole] remove role from db failed.");
                        throw new DatabaseException("database error!");
                    }
                    return true;
                }
                logger.error("[removeRole] this weId has not admin role.");
                return false;
            }
        }
        return false;
    }

    private static int computeRoleValue(RoleValue roleValue) {
        if(roleValue.getAuthority_role() == 1){
            if(roleValue.getCommittee_role() == 1){
                if(roleValue.getAdmin_role() == 1){
                    return 7;
                }
                return 3;
            }else if(roleValue.getAdmin_role() == 1){
                return 5;
            }else {return 1;}
        }else if(roleValue.getCommittee_role() == 1){
            if(roleValue.getAdmin_role() == 1){
                return 6;
            }
            return 2;
        }else {return 4;}
    }

    public static Boolean checkRole(String weId, Integer role){
        RoleValue result = getDataDriver().getRole(DataDriverConstant.LOCAL_ROLE, weId).getResult();
        if(result == null) {
            return false;
        }
        if(role == RoleController.ROLE_AUTHORITY_ISSUER){
            return result.getAuthority_role() == 1;
        }
        if(role == RoleController.ROLE_COMMITTEE){
            return result.getCommittee_role() == 1;
        }
        if(role == RoleController.ROLE_ADMIN){
            return result.getAdmin_role() == 1;
        }
        logger.error("[checkRole] input role invalid.");
        return false;
    }

}
