


package com.webank.weid.constant;

/**
 * Credential type.
 * @author tonychen 2020年4月22日
 */
public enum CredentialType {

    /**
     * original type, used to create original type credential.
     */
    ORIGINAL(0, "original"),

    /**
     * zkp type, used to create zkp type credential.
     */
    ZKP(1, "zkp"),

    /**
     * lite1 type, used to create lite1 type credential.
     */
    LITE1(2, "lite1");

    /**
     * type code.
     */
    private Integer code;
    /**
     * type name.
     */
    private String name;

    /**
     * constructor.
     *
     * @param code credential type code
     * @param name credential type name
     */
    CredentialType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * get type code.
     *
     * @return type code
     */
    public Integer getCode() {
        return this.code;
    }

    /**
     * get type name.
     *
     * @return type name
     */
    public String getName() {
        return this.name;
    }
}
