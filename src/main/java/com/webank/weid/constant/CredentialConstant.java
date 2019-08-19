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
     * The Constant is an field in PresentationPolicyE.
     */
    public static final String CLAIM_POLICY_FIELD = "policy";
    
    /**
     * The Constant is an field in claimPolicy.
     */
    public static final String CLAIM_POLICY_DISCLOSED_FIELD = "fieldsToBeDisclosed";

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
         * @return typeName
         */
        public String getTypeName() {
            return typeName;
        }
    }
}
