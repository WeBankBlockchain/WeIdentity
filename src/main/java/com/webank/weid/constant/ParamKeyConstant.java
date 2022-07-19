

package com.webank.weid.constant;

/**
 * Define param key names to be allowed to enable calls to Java SDK.
 *
 * @author chaoxinhu
 */
public final class ParamKeyConstant {

    /**
     * Universal param key names.
     */
    public static final String WEID = "weId";

    /**
     * WeIdService related param names.
     */
    public static final String PUBLIC_KEY = "publicKey";

    /**
     * AuthorityIssuer related param names.
     */
    public static final String AUTHORITY_ISSUER_NAME = "name";

    /**
     * UTF-8.
     */
    public static final String UTF_8 = "UTF-8";

    /**
     * CptService related param names.
     */
    public static final String CPT_JSON_SCHEMA = "cptJsonSchema";
    public static final String CPT_SIGNATURE = "cptSignature";
    public static final String CPT = "Cpt";

    /**
     * CredentialService related param names.
     */
    public static final String CPT_ID = "cptId";
    public static final String ISSUER = "issuer";
    public static final String CLAIM = "claim";
    public static final String EXPIRATION_DATE = "expirationDate";
    public static final String CREDENTIAL_SIGNATURE = "signature";
    public static final String CONTEXT = "context";
    public static final String CREDENTIAL_ID = "id";
    public static final String ISSUANCE_DATE = "issuanceDate";
    public static final String POLICY = "Policy";
    public static final String POLICY_PACKAGE = "com.webank.weid.cpt.policy.";

    /**
     * proof key.
     */
    public static final String PROOF = "proof";
    public static final String PROOF_SIGNATURE = "signatureValue";
    public static final String PROOF_TYPE = "type";
    public static final String PROOF_CREATED = "created";
    public static final String PROOF_CREATOR = "creator";
    public static final String PROOF_SALT = "salt";
    public static final String PROOF_VERIFICATION_METHOD = "verificationMethod";
    public static final String PROOF_NONCE = "nonce";
    public static final String PROOF_VERIFICATIONREQUEST = "verificationRequest";
    public static final String PROOF_ENCODEDVERIFICATIONRULE = "encodedVerificationRule";


    /**
     * 秘钥存储KEY.
     */
    public static final String KEY_DATA = "keyData";
    public static final String KEY_VERIFIERS = "verifiers";
    public static final String KEY_EXPIRE = "expirationDate";
    public static final String MASTER_SECRET = "masterSecret";
    public static final String BLINDING_FACTORS = "credentialSecretsBlindingFactors";

    public static final String WEID_AUTH_OBJ = "weIdAuthObj";
    public static final String WEID_AUTH_SIGN_DATA = "signData";
    public static final String WEID_AUTH_CHALLENGE = "challenge";

    public static final String TRNSACTION_RECEIPT_STATUS_SUCCESS = "0x0";
    public static final Integer TRNSACTION_RECEIPT_STATUS_SUCCESS_V3 = 0;

    /**
     * 内置配置Key.
     */
    public static final String RSYNC_IP = "rsyncIp";
    public static final String RSYNC_PORT = "rsyncPort";
    public static final String RSYNC_USER = "rsyncUser";
    public static final String RSYNC_PWD_NAME = "rsyncPwdName";
    public static final String RSYNC_BIN_LOG_MODULE = "binLog";
    public static final String BIN_LOG_PATH = "binLogPath";
    public static final String ENABLE_OFFLINE = "enableOffLine";
    public static final String INTEVAL_PERIOD = "inteval_period";
    public static final String SHARE_CNS = "cns.contract.share.follow.";
}
