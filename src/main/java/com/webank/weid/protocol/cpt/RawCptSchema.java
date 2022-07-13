/**
 * Copyright 2014-2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.webank.weid.protocol.cpt;

import org.apache.commons.lang3.StringUtils;

/**
 * @author marsli
 */
public final class RawCptSchema {

    public final static String CPT101 = "{\"type\":\"object\",\"description\":\"Authorize data between WeIDs via the exposed Service Endpoint\",\"title\":\"Data Authorization Token\",\"properties\":{\"duration\":{\"type\":\"integer\",\"description\":\"Duration of Validity in seconds\"},\"fromWeId\":{\"type\":\"string\",\"description\":\"Authorize from this WeID\"},\"resourceId\":{\"type\":\"string\",\"description\":\"Authorized Resource ID\"},\"serviceUrl\":{\"type\":\"string\",\"description\":\"Service Endpoint URL\"},\"toWeId\":{\"type\":\"string\",\"description\":\"Authorize to this WeID\"}},\"required\":[\"duration\",\"fromWeId\",\"resourceId\",\"serviceUrl\",\"toWeId\"],\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";;
    public final static String CPT102 = "{\"type\":\"object\",\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
    public final static String CPT103 = "{\"type\":\"object\",\"description\":\"Answer to meet the challenge\",\"title\":\"Authentication Answer\",\"properties\":{\"challenge\":{\"type\":\"object\",\"properties\":{\"nonce\":{\"type\":\"string\"},\"version\":{\"type\":\"integer\"},\"weId\":{\"type\":\"string\"}},\"description\":\"The challenge\"},\"id\":{\"type\":\"string\",\"description\":\"The entity's weidentity did\"},\"proof\":{\"type\":\"string\",\"description\":\"The proof\"}},\"required\":[\"challenge\",\"id\",\"proof\"],\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
    public final static String CPT104 = "{\"type\":\"object\",\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
    public final static String CPT105 = "{\"type\":\"object\",\"description\":\"API Endpoint address disclosure\",\"title\":\"API Endpoint\",\"properties\":{\"argType\":{\"type\":\"array\",\"items\":{\"type\":\"string\"},\"description\":\"Argument types in sequence\"},\"description\":{\"type\":\"string\",\"description\":\"Description\"},\"endpointName\":{\"type\":\"string\",\"description\":\"Endpoint name\"},\"hostport\":{\"type\":\"string\",\"description\":\"Network host and port\"},\"id\":{\"type\":\"string\",\"description\":\"Owner WeIdentity DID\"},\"version\":{\"type\":\"string\",\"description\":\"API Version\"}},\"required\":[\"argType\",\"description\",\"endpointName\",\"hostport\",\"id\",\"version\"],\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
    public final static String CPT106 = "{\"type\":\"object\",\"description\":\"Embedded Signature object for multi-sign\",\"title\":\"Embedded Signature\",\"properties\":{\"credentialList\":{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"claim\":{\"type\":\"object\",\"properties\":{\"get\":{\"type\":\"object\"},\"orDefault\":{\"type\":\"object\"},\"isEmpty\":{\"type\":\"boolean\"}}},\"context\":{\"type\":\"string\"},\"cptId\":{\"type\":\"integer\"},\"expirationDate\":{\"type\":\"integer\"},\"hash\":{\"type\":\"string\"},\"id\":{\"type\":\"string\"},\"issuanceDate\":{\"type\":\"integer\"},\"issuer\":{\"type\":\"string\"},\"proof\":{\"type\":\"object\",\"properties\":{\"get\":{\"type\":\"string\"},\"orDefault\":{\"type\":\"string\"},\"isEmpty\":{\"type\":\"boolean\"}}},\"proofType\":{\"type\":\"string\"},\"signature\":{\"type\":\"string\"},\"signatureThumbprint\":{\"type\":\"string\"}}},\"description\":\"Original credential list to be signed\"}},\"required\":[\"credentialList\"],\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
    public final static String CPT107 = "{\"type\":\"object\",\"description\":\"Embedded Signature object for multi-sign\",\"title\":\"Embedded Signature\",\"properties\":{\"credentialList\":{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"claim\":{\"type\":\"object\",\"properties\":{\"get\":{\"type\":\"object\"},\"orDefault\":{\"type\":\"object\"},\"isEmpty\":{\"type\":\"boolean\"}}},\"context\":{\"type\":\"string\"},\"cptId\":{\"type\":\"integer\"},\"credentialType\":{\"enum\":[\"ORIGINAL\",\"ZKP\",\"LITE1\"],\"type\":\"string\"},\"expirationDate\":{\"type\":\"integer\"},\"hash\":{\"type\":\"string\"},\"id\":{\"type\":\"string\"},\"issuanceDate\":{\"type\":\"integer\"},\"issuer\":{\"type\":\"string\"},\"proof\":{\"type\":\"object\",\"properties\":{\"get\":{\"type\":\"object\"},\"orDefault\":{\"type\":\"object\"},\"isEmpty\":{\"type\":\"boolean\"}}},\"proofType\":{\"type\":\"string\"},\"salt\":{\"type\":\"object\",\"properties\":{\"get\":{\"type\":\"object\"},\"orDefault\":{\"type\":\"object\"},\"isEmpty\":{\"type\":\"boolean\"}}},\"signature\":{\"type\":\"string\"},\"signatureThumbprint\":{\"type\":\"string\"},\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}},\"valueFromProof\":{\"type\":\"object\"}}},\"description\":\"Original credential list to be signed\"}},\"required\":[\"credentialList\"],\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
    public final static String CPT108 = "{\"type\":\"object\",\"description\":\"Trusted timestamping envelope\",\"title\":\"Trusted Timestamping\",\"properties\":{\"authoritySignature\":{\"type\":\"string\",\"description\":\"Signature value from Authority\"},\"claimHash\":{\"type\":\"string\",\"description\":\"Claim Hash\"},\"credentialList\":{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"claim\":{\"type\":\"object\",\"properties\":{\"get\":{\"type\":\"object\"},\"orDefault\":{\"type\":\"object\"},\"isEmpty\":{\"type\":\"boolean\"}}},\"context\":{\"type\":\"string\"},\"cptId\":{\"type\":\"integer\"},\"credentialType\":{\"enum\":[\"ORIGINAL\",\"ZKP\",\"LITE1\"],\"type\":\"string\"},\"expirationDate\":{\"type\":\"integer\"},\"hash\":{\"type\":\"string\"},\"id\":{\"type\":\"string\"},\"issuanceDate\":{\"type\":\"integer\"},\"issuer\":{\"type\":\"string\"},\"proof\":{\"type\":\"object\",\"properties\":{\"get\":{\"type\":\"object\"},\"orDefault\":{\"type\":\"object\"},\"isEmpty\":{\"type\":\"boolean\"}}},\"proofType\":{\"type\":\"string\"},\"salt\":{\"type\":\"object\",\"properties\":{\"get\":{\"type\":\"object\"},\"orDefault\":{\"type\":\"object\"},\"isEmpty\":{\"type\":\"boolean\"}}},\"signature\":{\"type\":\"string\"},\"signatureThumbprint\":{\"type\":\"string\"},\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}},\"valueFromProof\":{\"type\":\"object\"}}},\"description\":\"Original credential list to be signed\"},\"timestamp\":{\"type\":\"integer\",\"description\":\"Timestamp\"},\"timestampAuthority\":{\"type\":\"string\",\"description\":\"Timestamp Authority\"}},\"required\":[\"authoritySignature\",\"claimHash\",\"credentialList\",\"timestamp\",\"timestampAuthority\"],\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
    public final static String CPT109 = "{\"type\":\"object\",\"description\":\"Trusted Timestamp from authorized 3rd-party, or chain consensus\",\"title\":\"Trusted Timestamp\",\"properties\":{\"claimHash\":{\"type\":\"string\"},\"hashKey\":{\"type\":\"string\"},\"signatureList\":{\"type\":\"string\"},\"timestamp\":{\"type\":\"integer\"}},\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
    public final static String CPT110 = "{\"type\":\"object\",\"description\":\"Reserved CPT 110\",\"title\":\"metadata CPT\",\"properties\":{\"context\":{\"type\":\"string\",\"description\":\"context\"},\"cptId\":{\"type\":\"integer\",\"description\":\"CPT ID\"},\"credentialId\":{\"type\":\"string\",\"description\":\"credential ID\"},\"expirationDate\":{\"type\":\"integer\",\"description\":\"expirationDate\"},\"issuanceDate\":{\"type\":\"integer\",\"description\":\"issuanceDate\"},\"issuer\":{\"type\":\"string\",\"description\":\"issuer weid\"}},\"required\":[\"context\",\"cptId\",\"credentialId\",\"expirationDate\",\"issuanceDate\",\"issuer\"],\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
    public final static String CPT111 = "{\"type\":\"object\",\"description\":\"Reserved CPT 111\",\"title\":\"User CPT\",\"properties\":{\"cptId\":{\"type\":\"string\",\"description\":\"CPT ID\"},\"credentialSignatureRequest\":{\"type\":\"string\",\"description\":\"credential Signature Request\",\"minimum\":1},\"userNonce\":{\"type\":\"string\",\"description\":\"User Nonce\"}},\"required\":[\"cptId\",\"credentialSignatureRequest\",\"userNonce\"],\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
    public final static String CPT11 = "{\"type\":\"object\",\"description\":\"Reserved CPT 11\",\"title\":\"test CPT\",\"properties\":{\"cptId\":{\"type\":\"integer\",\"description\":\"CPT ID\",\"minimum\":1},\"gender\":{\"type\":\"string\",\"description\":\"Gender\",\"enum\":[\"MALE\",\"FEMALE\"]},\"tags\":{\"type\":\"array\",\"items\":{\"type\":\"string\"},\"description\":\"Registered Tags\",\"minItems\":1},\"userId\":{\"type\":\"string\",\"description\":\"User ID\"},\"userName\":{\"type\":\"string\",\"description\":\"User Name\",\"maxLength\":30}},\"required\":[\"cptId\",\"gender\",\"tags\",\"userId\",\"userName\"],\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
    public final static String CPT11Salt = "{\"type\":\"object\",\"description\":\"Reserved CPT 11Salt\",\"title\":\"test CPT\",\"properties\":{\"cptId\":{\"type\":\"string\",\"description\":\"CPT ID\",\"minimum\":1},\"userId\":{\"type\":\"string\",\"description\":\"User ID\"},\"userName\":{\"type\":\"string\",\"description\":\"User Name\",\"maxLength\":30}},\"required\":[\"cptId\",\"userId\",\"userName\"],\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";

    public static String getCptSchema(Integer cptId) {
        switch (cptId) {
            case 101:
                return CPT101;
            case 102:
                return CPT102;
            case 103:
                return CPT103;
            case 104:
                return CPT104;
            case 105:
                return CPT105;
            case 106:
                return CPT106;
            case 107:
                return CPT107;
            case 108:
                return CPT108;
            case 109:
                return CPT109;
            case 110:
                return CPT110;
            case 111:
                return CPT111;
            default:
                return StringUtils.EMPTY;
        }
    }
}
