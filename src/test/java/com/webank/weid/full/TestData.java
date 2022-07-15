

package com.webank.weid.full;

import com.webank.weid.constant.WeIdConstant.PublicKeyType;

/**
 * The function of this class is to test the basic data required.
 *
 * @author v_wbgyang
 */
public class TestData {

    /**
     * you may need to add public key after you create WeIdentity DID,This property is the public
     * key type,but for now the value is fixed and you don't need to modify it.
     */
    public static final String PUBLIC_KEY_TYPE = PublicKeyType.SECP256K1.getTypeName();

    /**
     * after you create WeIdentity DID,you may need to add service information which indicates the
     * type of service,You can modify this value but note that. there is a length limit
     */
    public static final String SERVICE_TYPE = "drivingCardService";

    /**
     * after you create WeIdentity DID,you may need to add service information which indicates the
     * serviceEndpoint of service,You can modify this value.
     */
    public static final String SERVICE_ENDPOINT = "https://weidentity.webank.com/endpoint/xxxxx";

    /**
     * you may need to add authorization after you create weId,This property is the public key
     * type,but for now the value is fixed and you don't need to modify it.
     */
    public static final String AUTHENTICATION_TYPE = "RsaSignatureAuthentication2018";

    /**
     * when you register an authority,you may need to get a name,which indicates the name of the
     * authority.
     */
    public static final String AUTHORITY_ISSUER_NAME = "weBank";

    /**
     * when you register an authority,you may need to get a accValue,which indicates the accValue of
     * the authority, the default value for testing is 0.
     */
    public static final String AUTHORITY_ISSUER_ACCVALUE = "0";

    /**
     * a valid jsonSchema template needed to register CPT.
     */
    public static final String SCHEMA =
        "{"
            + "  \"properties\" : {"
            + "      \"id\": {"
            + "          \"type\": \"string\", "
            + "          \"description\": \"the weid of certificate owner\""
            + "      }, "
            + "      \"name\": {"
            + "          \"type\": \"string\", "
            + "          \"description\": \"the name of certificate owner\""
            + "      }, "
            + "      \"gender\": {"
            + "          \"enum\": [\"F\", \"M\"],"
            + "          \"type\": \"string\", "
            + "          \"description\": \"the gender of certificate owner\""
            + "      }, "
            + "      \"age\": {"
            + "          \"type\": \"number\", "
            + "          \"description\": \"the age of certificate owner\""
            + "      }"
            + "  },"
            + "  \"required\": [\"id\",\"name\", \"age\"]"
            + "}";
}
