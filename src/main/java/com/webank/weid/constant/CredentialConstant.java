

package com.webank.weid.constant;

/**
 * The Class CredentialConstant.
 *
 * @author chaoxinhu
 */

public final class CredentialConstant {

    /**
     * The Constant default Credential Context.
     */
    public static final String DEFAULT_CREDENTIAL_CONTEXT =
        "https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1";

    /**
     * The Constant default Credential Context field name in Credential Json String.
     */
    public static final String CREDENTIAL_CONTEXT_PORTABLE_JSON_FIELD = "@context";

    /**
     * The Constant default Credential type.
     */
    public static final String DEFAULT_CREDENTIAL_TYPE = "VerifiableCredential";

    /**
     * cpt type.
     */
    public static final String CPT_TYPE_KEY = "cptType";

    /**
     * The Constant selective Credential type.
     */
    public static final String SELECTIVE_CREDENTIAL_TYPE = "hashTree";

    /**
     * The Constant is an field in PresentationPolicyE.
     */
    public static final String CLAIM_POLICY_FIELD = "policy";

    /**
     * key id.
     */
    public static final String ID = "id";

    /**
     * credential id.
     */
    public static final String CREDENTIAL_META_KEY_ID = "credentialId";

    /**
     * The CPT type in standard integer format.
     */
    public static final String CREDENTIAL_META_KEY_CPTID = "cptId";

    /**
     * The issuer WeIdentity DID.
     */
    public static final String CREDENTIAL_META_KEY_ISSUER = "issuer";

    /**
     * The expire date.
     */
    public static final String CREDENTIAL_META_KEY_EXPIRATIONDATE = "expirationDate";

    /**
     * The issuance date of the credential.
     */
    public static final String CREDENTIAL_META_KEY_ISSUANCEDATE = "issuanceDate";

    /**
     * credential context.
     */
    public static final String CREDENTIAL_META_KEY_CONTEXT = "context";

    /**
     * The Constant is an field in claimPolicy.
     */
    public static final String CLAIM_POLICY_DISCLOSED_FIELD = "fieldsToBeDisclosed";
    /**
     * Default CPT ID for embedded credential signature subject (multi-sign support).
     */
    public static final Integer CREDENTIAL_EMBEDDED_SIGNATURE_CPT = 106;
    /**
     * Default CPT ID for embedded credentialPojo subject (multi-sign support).
     */
    public static final Integer CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT = 107;
    /**
     * Embedded trusted timestamp default CPT ID.
     */
    public static final Integer EMBEDDED_TIMESTAMP_CPT = 108;
    /**
     * Embedded trusted timestamp envelop default CPT ID.
     */
    public static final Integer TIMESTAMP_ENVELOP_CPT = 109;
    /**
     * Authorization CPT ID.
     */
    public static final Integer AUTHORIZATION_CPT = 101;
    /**
     * Challenge CPT ID.
     */
    public static final Integer CHALLENGE_CPT = 102;
    /**
     * Challenge Verification CPT ID.
     */
    public static final Integer CHALLENGE_VERIFICATION_CPT = 103;
    /**
     * Claim Policy CPT ID.
     */
    public static final Integer CLAIM_POLICY_CPT = 104;
    /**
     * Service Endpoint CPT ID.
     */
    public static final Integer SERVICE_ENDPOINT_CPT = 105;

    /**
     * metadata CPT ID.
     */
    public static final Integer METADATA_CPT = 110;

    /**
     * metadata CPT ID.
     */
    public static final Integer ZKP_USER_NONCE_CPT = 111;

    public static final String PRESENTATION_PDF = "presentationFromPDF";
    /**
     * CPT key words.
     */
    public static final String[] CPT_KEY_WORDS = {"GE", "LE", "GT", "LT", "EQ"};

    /**
     * The Credential Proof Type Enumerate.
     */
    public static enum CredentialProofType {
        ECDSA("Secp256k1"),
        SM2("SM2");

        /**
         * The Type Name of the Credential Proof.
         */
        private String typeName;

        /**
         * Constructor.
         */
        CredentialProofType(String typeName) {
            this.typeName = typeName;
        }

        /**
         * Getter.
         *
         * @return typeName
         */
        public String getTypeName() {
            return typeName;
        }
    }
}
