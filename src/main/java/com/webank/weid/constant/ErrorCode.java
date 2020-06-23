/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
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
     * cptId illegal.
     */
    CPT_ID_ILLEGAL(100303, "cptId illegal"),

    /**
     * cpt event log is null.
     */
    CPT_EVENT_LOG_NULL(100304, "cpt event log is null."),

    /**
     * credential template save failed.
     */
    CPT_CREDENTIAL_TEMPLATE_SAVE_ERROR(100305, "cpt credential template saved with error."),

    /**
     * Credential main error code.
     */
    CREDENTIAL_ERROR(100400, "error occured during processing credential tasks"),

    /**
     * The credential expired.
     */
    CREDENTIAL_EXPIRED(100402, "credential is expired"),

    /**
     * The credential issuer mismatch.
     */
    CREDENTIAL_ISSUER_MISMATCH(100403,
        "issuer (signer) weId does not match the weId of credential"),

    /**
     * The credential signature broken.
     */
    CREDENTIAL_SIGNATURE_BROKEN(100405, "credential signature cannot be extracted"),

    /**
     * The credential issuer not exists.
     */
    CREDENTIAL_ISSUER_NOT_EXISTS(100407, "credential issuer does not exist"),

    /**
     * The credential issuance date illegal.
     */
    CREDENTIAL_ISSUANCE_DATE_ILLEGAL(100408, "credential issuance date illegal"),

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
     * The credential type is null.
     */
    CREDENTIAL_TYPE_IS_NULL(100414, "credential type is null"),

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
    CREDENTIAL_ISSUER_INVALID(100418, "credential issuer invalid or mismatch the WeID auth"),

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
    CREDENTIAL_PRESENTERWEID_NOTMATCH(100426, "credential presenter WeId not match"),

    /**
     * The credential evidence id mismatch.
     */
    CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM(
        100427,
        "credential disclosure format does not match claim"
    ),

    /**
     * credential disclosure data illegal.
     */
    CREDENTIAL_DISCLOSURE_DATA_TYPE_ILLEGAL(100428, "credential disclosure data illegal"),

    /**
     * The credential signature broken.
     */
    CREDENTIAL_SIGNATURE_TYPE_ILLEGAL(100429, "credential signature type unknown"),

    /**
     * credential salt illegal.
     */
    CREDENTIAL_SALT_ILLEGAL(100430, "credential salt illegal"),

    /**
     * credential evidence cannot be extracted.
     */
    CREDENTIAL_EVIDENCE_SIGNATURE_BROKEN(100431, "credential evidence cannot be extracted"),

    /**
     * credential has already been disclosed once.
     */
    CREDENTIAL_RE_DISCLOSED(100432,
        "credential has already been disclosed once, can not be re-disclosed"),

    /**
     * Timestamp service error.
     */
    TIMESTAMP_SERVICE_BASE_ERROR(100433, "timestamp service error"),

    /**
     * System CPT Claim verification failure.
     */
    CREDENTIAL_SYSTEM_CPT_CLAIM_VERIFY_ERROR(100434, "external credential verify"
        + " succeeded, but inner content verify failed"),

    /**
     * Timestamp service unavailable.
     */
    TIMESTAMP_SERVICE_UNCONFIGURED(100435, "timestamp service not configured"),

    /**
     * Timestamp service: WeSign workflow error.
     */
    TIMESTAMP_SERVICE_WESIGN_ERROR(100436, "wesign timestamp service error: check log for details"),

    /**
     * Timestamp verification failed.
     */
    TIMESTAMP_VERIFICATION_FAILED(100437, "timestamp verification failed"),

    /**
     * Timestamp service does not support selectively-disclosed credential's presence.
     */
    TIMESTAMP_CREATION_FAILED_FOR_SELECTIVELY_DISCLOSED(100438,
        "timestamp creation does not support selectively disclosed credential"),

    CREDENTIAL_USE_VERIFY_FUNCTION_ERROR(100439,
        "presentation from pdf transportation, please use verifyPresentationFromPDF function"),

    /**
     * the error code shows that the credential passed in the function is not supported by this
     * function.
     */
    CREDENTIAL_NOT_SUPPORT_SELECTIVE_DISCLOSURE(100440,
        "the error code shows that the credential passed in the "
            + "function is not supported by this function."),

    /**
     * credential verify fail.
     */
    CREDENTIAL_VERIFY_FAIL(100441, "credential verify fail."),

    /**
     * credential verify succeeded with a wrong public key ID.
     */
    CREDENTIAL_VERIFY_SUCCEEDED_WITH_WRONG_PUBLIC_KEY_ID(100442, "credential"
        + "verify succeeded, but the given public key ID is incorrect."),

    /**
     * Authorization WeIDs: from and to must be different.
     */
    AUTHORIZATION_FROM_TO_MUST_BE_DIFFERENT(100450,
        "authorization's fromWeId and toWeId must be different"
    ),

    /**
     * Authorization: cannot authorize other WeID's resource.
     */
    AUTHORIZATION_CANNOT_AUTHORIZE_OTHER_WEID_RESOURCE(100451,
        "cannot authorize other WeID's resource"
    ),

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
    CREDENTIAL_EVIDENCE_HASH_MISMATCH(100501, "evidence hash mismatch"),

    /**
     * The credential evidence hash mismatch.
     */
    CREDENTIAL_EVIDENCE_NOT_EXIST(100502, "evidence does not exist on chain"),

    /**
     * The credential evidence interface does not support fisco bcos 1.3.
     */
    FISCO_BCOS_VERSION_NOT_SUPPORTED(170000,
        "this function does not support current fisco bcos version"),

    /**
     * On-chain string length exceeded acceptable max.
     */
    ON_CHAIN_STRING_TOO_LONG(100504, "on-chain String length exceeds limit"),

    /**
     * offline evidence transaction saved failed.
     */
    OFFLINE_EVIDENCE_SAVE_FAILED(100505, "offline evidence transaction saved failed."),

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
     * the presenter weid of presentation does not match the credential.
     */
    PRESENTATION_WEID_CREDENTIAL_WEID_MISMATCH(
        100607,
        "the presenter weid of presentation does not match the credential."
    ),

    /**
     * the weid of the claim of the presentation does not exist.
     */
    PRESENTATION_CREDENTIAL_CLAIM_WEID_NOT_EXIST(
        100608,
        "the weid of the claim of the presentation does not exist."
    ),

    /**
     * the publisherWeId of policy is invalid.
     */
    PRESENTATION_POLICY_PUBLISHER_WEID_INVALID(
        100609,
        "the publisherWeId of policy is invalid."
    ),

    /**
     * the publisherWeId of policy does not exist.
     */
    PRESENTATION_POLICY_PUBLISHER_WEID_NOT_EXIST(
        100610,
        "the publisherWeId of policy does not exist."
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
     * the policy service call fail.
     */
    ENCRYPT_KEY_NO_PERMISSION(100703, "no permission to get the key."),

    /**
     * the key is invalid.
     */
    ENCRYPT_KEY_INVALID(100704, "the key is invalid."),

    /**
     * encrypt data failed.
     */
    ENCRYPT_DATA_FAILED(100705, "encrypt data failed."),

    /**
     * decrypt data failed.
     */
    DECRYPT_DATA_FAILED(100706, "decrypt data failed."),

    /**
     * transportation base error.
     */
    TRANSPORTATION_BASE_ERROR(100800, "suite baes exception error, please check the error log."),

    /**
     * transportation protocol error.
     */
    TRANSPORTATION_PROTOCOL_PROPERTY_ERROR(100801, "the protocol property is error."),

    /**
     * transportation protocol version error.
     */
    TRANSPORTATION_PROTOCOL_VERSION_ERROR(100802, "the protocol version is error."),

    /**
     * transportation protocol encode error.
     */
    TRANSPORTATION_PROTOCOL_ENCODE_ERROR(100803, "the protocol encode is error."),

    /**
     * transportation protocol value error.
     */
    TRANSPORTATION_PROTOCOL_STRING_INVALID(100804, "the protocol string is invalid."),

    /**
     * transportation protocol data error.
     */
    TRANSPORTATION_PROTOCOL_DATA_INVALID(100805, "the protocol data is invalid."),

    /**
     * transportation protocol field invalid.
     */
    TRANSPORTATION_PROTOCOL_FIELD_INVALID(
        100806,
        "the protocol field value invalid."
    ),

    /**
     * transportation protocol encode error.
     */
    TRANSPORTATION_ENCODE_BASE_ERROR(100807, "encode base error, please check the error log."),

    /**
     * pdf transfer error.
     */
    TRANSPORTATION_PDF_TRANSFER_ERROR(100808, "pdf transfer error, please check the error log."),

    /**
     * pdf transfer error.
     */
    TRANSPORTATION_PDF_VERIFY_ERROR(100809, "pdf verify error, please check the error log."),

    /**
     * the transmission type is invalid.
     */
    TRANSPORTATION_TRANSMISSION_TYPE_INVALID(100810, "the trans type is invalid."),

    /**
     * the URI type is invalid.
     */
    TRANSPORTATION_URI_TYPE_INVALID(100811, "the URI type is invalid."),

    /**
     * no spcifyer to set.
     */
    TRANSPORTATION_NO_SPECIFYER_TO_SET(100812, "no spcifyer to set."),

    /**
     * the trans mode is invalid.
     */
    TRANSPORTATION_TRANSMODE_TYPE_INVALID(100813, "the trans mode is invalid."),

    /**
     * Authority issuer main error code.
     */
    AUTHORITY_ISSUER_ERROR(100200, "error occured during processing authority issuer tasks"),

    /**
     * The authority issuer private key param is illegal.
     */
    AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL(100202, "the private key is illegal"),

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

    AUTORITY_ISSUER_DESCRIPTION_ILLEGAL(
        100209,
        "authority issuer description illegal"
    ),

    AUTHORITY_ISSUER_EXTRA_PARAM_ILLEGAL(
        100210,
        "authority issuer extra param illegal"
    ),

    /**
     * the key of the data is empty.
     */
    PRESISTENCE_DATA_KEY_INVALID(
        100901,
        "the key of the data is empty."
    ),

    /**
     * the domain is illegal.
     */
    PRESISTENCE_DOMAIN_ILLEGAL(
        100902,
        "the domain is illegal."
    ),

    /**
     * the domain is illegal.
     */
    PRESISTENCE_DOMAIN_INVALID(
        100903,
        "the domain is invalid."
    ),

    /**
     * the data does not match for batch save.
     */
    PRESISTENCE_BATCH_SAVE_DATA_MISMATCH(
        100904,
        "the data does not match for batch save."
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
     * the length of the setService type is overlimit.
     */
    WEID_SERVICE_TYPE_OVERLIMIT(
        100110, "the length of service type is overlimit."
    ),

    /**
     * you cannot remove your last public key or your own public key.
     */
    WEID_CANNOT_REMOVE_ITS_OWN_PUB_KEY_WITHOUT_BACKUP(
        100111,
        "cannot remove this WeID's owner or last public key without an available backup."
    ),

    /**
     * no permission of weIdAuth.
     */
    WEID_AUTH_NO_PERMISSION(
        100112,
        "no permission of weIdAuth."
    ),

    /**
     * no permission of weIdAuth.
     */
    WEID_AUTH_NO_CALLBACK(
        100113,
        "weIdAuth callback is not registered."
    ),

    /**
     * the channelId is null.
     */
    WEID_AUTH_CHANNELID_IS_NULL(
        100114,
        "the channelId is null."
    ),

    /**
     * the channelId is invalid.
     */
    WEID_AUTH_CHANNELID_INVALID(
        100115,
        "the channelId is invalid."
    ),

    /**
     * weid pubkey already exists, used in add case.
     */
    WEID_PUBLIC_KEY_ALREADY_EXISTS(
        100116,
        "this public key already exists and is not revoked."
    ),

    /**
     * weid pubkey does not exist, used in revoke case.
     */
    WEID_PUBLIC_KEY_NOT_EXIST(
        100117,
        "this public key does not exist, or is already revoked."
    ),

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
    DIRECT_ROUTE_MSG_BASE_ERROR(160010, "amop response messageBody error."),

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
     * the orgid is null.
     */
    ORG_ID_IS_NULL(160014, "the orgid is null."),

    /**
     * the data is expire.
     */
    SQL_DATA_EXPIRE(160015, "the data is expire."),

    /**
     * no premission.
     */
    CNS_NO_PERMISSION(160016, "no premission for this cns."),

    /**
     * the cns does not exist.
     */
    CNS_DOES_NOT_EXIST(160017, "the cns does not exist."),

    /**
     * the cns is used.
     */
    CNS_IS_USED(160018, "the cns is used."),

    /**
     * the cns is not used.
     */
    CNS_IS_NOT_USED(160019, "the cns is not used."),

    /**
     * the code is undefined.
     */
    CNS_CODE_UNDEFINED(160020, "the code is undefined."),

    /**
     * the data does not exist.
     */
    SQL_DATA_DOES_NOT_EXIST(160021, "the data does not exist."),

    /**
     * this is unsupported.
     */
    THIS_IS_UNSUPPORTED(160022, "this is unsupported."),

    /**
     * this is repeated call.
     */
    THIS_IS_REPEATED_CALL(160023, "this is repeated call."),

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
}
