

package com.webank.weid.constant;

/**
 * CPT type enum.
 *
 * @author tonychen 2020年2月17日
 */
public enum CptType {

    /**
     * original type, used to create original type credential.
     */
    ORIGINAL(0, "original"),

    /**
     * zkp type, used to create zkp type credential.
     */
    ZKP(1, "zkp");

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
     * @param code cpt type code
     * @param name cpt type name
     */
    CptType(Integer code, String name) {
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
