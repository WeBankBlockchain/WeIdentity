/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.constant;

/**
 * Define Error Code and the corresponding Error Message.
 *
 * @author lingfenghe
 */
public enum ErrorCode {

    /**
     * The success.
     */
    SUCCESS(0, "success"),

    /**
     * No Permission to perform contract level tasks.
     */
    CONTRACT_ERROR_NO_PERMISSION(
        500000,
        "contract error: no permission to perform this task"
    ),

    /**
     * The cpt not exists.
     */
    CPT_NOT_EXISTS(500301, "cpt does not exist"),

    /**
     * cpt id generated for authority issuer exceeds limited max value.
     */
    CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX(
        500302,
        "cpt id generated for authority issuer exceeds limited max value"
    ),

    /**
     * cpt publisher does not exist.
     */
    CPT_PUBLISHER_NOT_EXIST(500303, "cpt publisher does not exist"),

    /**
     * This cpt id already exists on chain.
     */
    CPT_ALREADY_EXIST(500304, "cpt already exist on chain"),

    /**
     * No permission to perform this CPT task.
     */
    CPT_NO_PERMISSION(500305, "no permission to perform this cpt task"),

    /**
     * The cpt json schema invalid.
     */
    CPT_JSON_SCHEMA_INVALID(100301, "cpt json schema is invalid"),

    /**
     * The cpt json schema null.
     */
    CPT_JSON_SCHEMA_NULL(100302, "cpt json schema is null"),

    /**
     * cptId is null.
     */
    CPT_ID_NULL(100303, "cptId is null"),

    /**
     * cpt event log is null.
     */
    CPT_EVENT_LOG_NULL(100304, "cpt event log is null."),

    /**
     * Credential main error code.
     */
    CREDENTIAL_ERROR(100400, "error occured during processing credential tasks"),

    /**
     * The credential does not exist on chain (evidence not found).
     */
    CREDENTIAL_EVIDENCE_NOT_EXISTS_ON_CHAIN(100401, "credential evidence does not exist on chain"),

    /**
     * The credential expired.
     */
    CREDENTIAL_EXPIRED(100402, "credential is expired"),

    /**
     * The credential issuer mismatch.
     */
    CREDENTIAL_ISSUER_MISMATCH(100403, "credential issuer does not match the signature"),

    /**
     * The credential issuer not exists in list.
     */
    CREDENTIAL_ISSUER_NOT_EXISTS_IN_LIST(
        100404,
        "credential issuer does not exist in the authority issuer list"
    ),

    /**
     * The credential signature broken.
     */
    CREDENTIAL_SIGNATURE_BROKEN(100405, "credential signature cannot be extracted"),

    /**
     * The credential revoked.
     */
    CREDENTIAL_REVOKED(100406, "credential is revoked"),

    /**
     * The credential issuer not exists.
     */
    CREDENTIAL_ISSUER_NOT_EXISTS(100407, "credential issuer does not exist"),

    /**
     * The credential create date illegal.
     */
    CREDENTIAL_CREATE_DATE_ILLEGAL(100408, "create date illegal"),

    /**
     * The credential expire date illegal.
     */
    CREDENTIAL_EXPIRE_DATE_ILLEGAL(100409, "expire date illegal"),

    /**
     * The credential claim not exists.
     */
    CREDENTIAL_CLAIM_NOT_EXISTS(100410, "claim data does not exist"),

    /**
     * The credential claim data illegal.
     */
    CREDENTIAL_CLAIM_DATA_ILLEGAL(100411, "claim data illegal"),

    /**
     * The credential id not exists.
     */
    CREDENTIAL_ID_NOT_EXISTS(100412, "credential id does not exist"),

    /**
     * The credential context not exists.
     */
    CREDENTIAL_CONTEXT_NOT_EXISTS(100413, "credential context does not exist"),

    /**
     * The credential private key not exists.
     */
    CREDENTIAL_PRIVATE_KEY_NOT_EXISTS(100415, "private key for signing credential does not exist"),

    /**
     * The credential CPT info is empty.
     */
    CREDENTIAL_CPT_NOT_EXISTS(100416, "cpt does not exist"),

    /**
     * The credential issuer does not have a valid WeIdentity DID document.
     */
    CREDENTIAL_WEID_DOCUMENT_ILLEGAL(100417, "weid document illegal"),

    /**
     * The credential issuer is invalid.
     */
    CREDENTIAL_ISSUER_INVALID(100418, "credential issuer invalid"),

    /**
     * The credential credential verify signature is exception.
     */
    CREDENTIAL_EXCEPTION_VERIFYSIGNATURE(100419, "credential verify signature exception"),

    /**
     * claim policy is null.
     */
    CREDENTIAL_CLAIM_POLICY_NOT_EXIST(100420, "claim policy is null"),

    /**
     * The credential private key not exists.
     */
    CREDENTIAL_PUBLIC_KEY_NOT_EXISTS(
        100421, 
        "public key for verifying credential signature does not exist"
    ),
    
    /**
     * The signature for verifying credential does not exist.
     */
    CREDENTIAL_SIGNATURE_NOT_EXISTS(100422, "signature for verifying credential does not exist"),
    
    /**
     * The credential policy disclosurevalue illegal.
     */
    CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL(100423, "policy disclosurevalue illegal"),
    
    /**
     * The credential disclosurevalue notmatch saltvalue.
     */
    CREDENTIAL_DISCLOSUREVALUE_NOTMATCH_SALTVALUE(100424, "disclosurevalue notmatch saltvalue"),
    
    /**
     * The credential cptId notmatch.
     */
    CREDENTIAL_CPTID_NOTMATCH(100425, "credential cptId notmatch"),
    
    /**
     * The credential presenterWeId notmatch.
     */
    CREDENTIAL_PRESENTERWEID_NOTMATCH(100426, "credential presenterWeId notmatch"),

    /**
     * The credential evidence id mismatch.
     */
    CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM(
        100427,
        "credential disclosure format does not match claim"
    ),

    /**
     * The credential evidence id mismatch.
     */
    CREDENTIAL_IS_NILL(100428, "credential is null"),


    /**
     * The credential signature broken.
     */
    CREDENTIAL_SIGNATURE_TYPE_ILLEGAL(100429, "credential signature type unknown"),

    /**
     * The credential evidence contract failure: illegal input.
     */
    CREDENTIAL_EVIDENCE_CONTRACT_FAILURE_ILLEAGAL_INPUT(
        500401,
        "credential evidence contract failure: illegal input."
    ),

    /**
     * The credential evidence base error.
     */
    CREDENTIAL_EVIDENCE_BASE_ERROR(
        100500,
        "generic error when processing credential evidence tasks"
    ),

    /**
     * The credential evidence hash mismatch.
     */
    CREDENTIAL_EVIDENCE_HASH_MISMATCH(100501, "credential evidence hash mismatch"),

    /**
     * The credential evidence id mismatch.
     */
    CREDENTIAL_EVIDENCE_ID_MISMATCH(100502, "credential evidence id mismatch"),

    /**
     * The challenge is invalid.
     */
    PRESENTATION_CHALLENGE_INVALID(100600, "the challenge is invalid."),

    /**
     * The weId of challenge does not match the user's weId.
     */
    PRESENTATION_CHALLENGE_WEID_MISMATCH(
        100601,
        "the weId of challenge does not match the user's weId."
    ),

    /**
     * The presentation policy is invalid.
     */
    PRESENTATION_POLICY_INVALID(100602, "the presentation policy is invalid."),

    /**
     * the credentialList of presentation don't match the claim policy.
     */
    PRESENTATION_CREDENTIALLIST_MISMATCH_CLAIM_POLICY(
        100603,
        "the credentiallist of presentation don't match the claim policy."
    ),

    /**
     * the publicKeyId is invalid.
     */
    PRESENTATION_WEID_PUBLICKEY_ID_INVALID(100604, "the publicKeyId is invalid."),

    /**
     * the nonce of challenge does not match the nonce of presentation.
     */
    PRESENTATION_CHALLENGE_NONCE_MISMATCH(
        100605,
        "the nonce of challenge does not match the nonce of presentation."
    ),
    
    /**
     * the signature of presentation does not match the presenter.
     */
    PRESENTATION_SIGNATURE_MISMATCH(
        100606,
        "the signature of presentation does not match the presenter."
    ),

    /**
     * the encrypt key is not exists.
     */
    ENCRYPT_KEY_NOT_EXISTS(100700, "the encrypt key not exists."),

    /**
     * the policy service is not exists.
     */
    POLICY_SERVICE_NOT_EXISTS(100701, "no policy service."),

    /**
     * the policy service call fail.
     */
    POLICY_SERVICE_CALL_FAIL(100702, "the policy service call fail, please check the error log."),

    /**
     * suite基本异常.
     */
    TRANSPORTATION_BASE_ERROR(100800, "suite baes exception error, please check the error log."),

    /**
     * 协议配置不能为空.
     */
    TRANSPORTATION_PROTOCOL_PROPERTY_ERROR(100801, "the protocol property is error."),

    /**
     * 协议版本不能为空.
     */
    TRANSPORTATION_PROTOCOL_VERSION_ERROR(100802, "the protocol version is error."),

    /**
     * 协议编码方式不能为空.
     */
    TRANSPORTATION_PROTOCOL_ENCODE_ERROR(100803, "the protocol encode is error."),

    /**
     * 无效的协议字符串.
     */
    TRANSPORTATION_PROTOCOL_STRING_INVALID(100804, "the protocol string is invalid."),

    /**
     * 协议数据无效.
     */
    TRANSPORTATION_PROTOCOL_DATA_INVALID(100805, "the protocol data is invalid."),

    /**
     * 协议字段值不能包含"|".
     */
    TRANSPORTATION_PROTOCOL_FIELD_INVALID(
        100806, 
        "the protocol field values cannot be included '|'."
    ),

    /**
     * 编解码异常.
     */
    TRANSPORTATION_ENCODE_BASE_ERROR(100807, "encode base error, please check the error log."),

    /**
     * Authority issuer main error code.
     */
    AUTHORITY_ISSUER_ERROR(100200, "error occured during processing authority issuer tasks"),

    /**
     * The authority issuer argument not exists.
     */
    AUTHORITY_ISSUER_ARGUMENT_NOT_EXISTS(100201, "the argument passed in is null"),

    /**
     * The authority issuer private key param is illegal.
     */
    AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL(100202, "the private key is illegal"),

    /**
     * The authority issuer address mismatch.
     */
    AUTHORITY_ISSUER_ADDRESS_MISMATCH(100204,
        "address in event log does not match the WeIdentity DID"),

    /**
     * The authority issuer opcode mismatch.
     */
    AUTHORITY_ISSUER_OPCODE_MISMATCH(
        100205,
        "opcode in event log does not match the desired opcode"
    ),

    /**
     * The authority issuer name illegal.
     */
    AUTHORITY_ISSUER_NAME_ILLEGAL(100206, "the registered authority issuer name is illegal"),

    /**
     * The authority issuer accvalue illegal.
     */
    AUTHORITY_ISSUER_ACCVALUE_ILLEAGAL(
        100207,
        "the authority issuer accumulator value is illegal (integer value required)"
    ),

    /**
     * The specific issuer type illegal.
     */
    SPECIFIC_ISSUER_TYPE_ILLEGAL(
        100208,
        "the specific issuer type is illegal"
    ),

    /**
     * The Authority Issuer Contract level error: subject already exists.
     */
    PRESISTENCE_DATA_KEY_INVALID(
        100901,
        "the key of the data is empty."
    ),

    /**
     * The Authority Issuer Contract level error: subject already exists.
     */
    AUTHORITY_ISSUER_CONTRACT_ERROR_ALREADY_EXIST(
        500201,
        "the authority issuer contract error: the subject already exists"
    ),

    /**
     * The Authority Issuer Contract level error: subject already exists.
     */
    AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS(
        500202,
        "the authority issuer contract error: the subject does not exist"
    ),

    /**
     * The Authority Issuer Contract level error: name already exists.
     */
    AUTHORITY_ISSUER_CONTRACT_ERROR_NAME_ALREADY_EXISTS(
        500203,
        "the authority issuer name already exists."
    ),

    /**
     * The Specific Issuer Contract level error: already exists.
     */
    SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_EXISTS(
        500501,
        "the specific issuer type or address already exists."
    ),

    /**
     * The Specific Issuer Contract level error: already exists.
     */
    SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST(
        500502,
        "the specific issuer type or address does not exist."
    ),

    /**
     * The weid invalid.
     */
    WEID_INVALID(100101, "the weIdentity DID is invalid."),

    /**
     * public key is invalid.
     */
    WEID_PUBLICKEY_INVALID(100102, "the input public key is invalid."),

    /**
     * private key is invalid.
     */
    WEID_PRIVATEKEY_INVALID(
        100103,
        "the input private key is invalid, please check and input your private key."
    ),

    /**
     * weid does not exist.
     */
    WEID_DOES_NOT_EXIST(100104, "the weid does not exist on blockchain."),

    /**
     * weid has already exist.
     */
    WEID_ALREADY_EXIST(100105, "the weid has already exist on blockchain."),

    /**
     * the private key is not the weid's.
     */
    WEID_PRIVATEKEY_DOES_NOT_MATCH(100106, "the private key does not match the current weid."),

    /**
     * create keypair exception.
     */
    WEID_KEYPAIR_CREATE_FAILED(100107, "create keypair faild."),

    /**
     * public key and private key are not a keypair.
     */
    WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED(
        100108,
        "the public key and private key are not matched."
    ),

    /**
     * the authority of the weIdentity DID is invalid.
     */
    WEID_AUTHORITY_INVALID(100109, "the authority of the weIdentity DID is invalid."),

    /**
     * transaction timeout.
     */
    TRANSACTION_TIMEOUT(160001, "the transaction is timeout."),

    /**
     * exception happens when transaction executes.
     */
    TRANSACTION_EXECUTE_ERROR(160002, "the transaction does not correctly executed."),

    /**
     * input parameter is illegal.
     */
    ILLEGAL_INPUT(160004, "input parameter is illegal."),

    /**
     * smart contract load failed.
     */
    LOAD_CONTRACT_FAILED(160005, "load contract failed."),

    /**
     * web3j load failed.
     */
    LOAD_WEB3J_FAILED(160006, "load web3j failed."),

    /**
     * weidentity base exceptions or error.
     */
    BASE_ERROR(160007, "baes exception error, please check the error log."),

    /**
     * weidentity data type case exceptions or error.
     */
    DATA_TYPE_CASE_ERROR(160008, "data type cast exception error, please check the error log."),

    DIRECT_ROUTE_REQUEST_TIMEOUT(160009, "amop timeout"),
    DIRECT_ROUTE_MSG_BASE_ERROR(160010, "amop timeout"),

    /**
     * sql execute failed.
     */
    SQL_EXECUTE_FAILED(160011, "sql execute failed."),

    /**
     * AMOP server side has no direct route callback.
     */
    AMOP_MSG_CALLBACK_SERVER_SIDE_NO_HANDLE(
        160012, 
        "amop server side has no direct route callback."
    ),
    
    /**
     * can not get the connection from pool.
     */
    SQL_GET_CONNECTION_ERROR(
        160013, 
        "can not get the connection from pool, please check the error log."
    ),
    
    /**
     * other uncatched exceptions or error.
     */
    UNKNOW_ERROR(160003, "unknow error, please check the error log.");

    /**
     * error code.
     */
    private int code;

    /**
     * error message.
     */
    private String codeDesc;

    /**
     * Error Code Constructor.
     *
     * @param code The ErrorCode
     * @param codeDesc The ErrorCode Description
     */
    ErrorCode(int code, String codeDesc) {
        this.code = code;
        this.codeDesc = codeDesc;
    }

    /**
     * Get the Error Code.
     *
     * @return the ErrorCode
     */
    public int getCode() {
        return code;
    }

    /**
     * Set the Error Code.
     *
     * @param code the new ErrorCode
     */
    protected void setCode(int code) {
        this.code = code;
    }

    /**
     * Gets the ErrorCode Description.
     *
     * @return the ErrorCode Description
     */
    public String getCodeDesc() {
        return codeDesc;
    }

    /**
     * Sets the ErrorCode Description.
     *
     * @param codeDesc the new ErrorCode Description
     */
    protected void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    /**
     * get ErrorType By errcode.
     *
     * @param errorCode the ErrorCode
     * @return errorCode
     */
    public static ErrorCode getTypeByErrorCode(int errorCode) {
        for (ErrorCode type : ErrorCode.values()) {
            if (type.getCode() == errorCode) {
                return type;
            }
        }
        return ErrorCode.UNKNOW_ERROR;
    }
}
