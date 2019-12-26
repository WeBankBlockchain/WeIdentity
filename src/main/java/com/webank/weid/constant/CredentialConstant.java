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
     * The Constant zkp Credential type.
     */
    public static final String ZKP_CREDENTIAL_TYPE = "zkp";

    /**
     * The Constant original Credential type.
     */
    public static final String ORIGINAL_CREDENTIAL_TYPE = "hashTree";

    /**
     * The Constant is an field in PresentationPolicyE.
     */
    public static final String CLAIM_POLICY_FIELD = "policy";

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

    /**
     * CPT key words.
     */
    public static final String[] CPT_KEY_WORDS = {"GE", "LE", "GT", "LT", "EQ"};

    /**
     * The Credential Proof Type Enumerate.
     */
    public static enum CredentialProofType {
        ECDSA("Secp256k1");

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
