

package com.webank.weid.constant;

/**
 * credential field disclosure status.
 *
 * @author tonychen
 */
public enum CredentialFieldDisclosureValue {

    /**
     * the field is existed.
     */
    EXISTED(2),

    /**
     * the field is disclosed.
     */
    DISCLOSED(1),

    /**
     * the field is not disclosed.
     */
    NOT_DISCLOSED(0);

    /**
     * disclosure status.
     */
    private Integer status;

    CredentialFieldDisclosureValue(Integer status) {
        this.status = status;
    }

    /**
     * get field disclosure status.
     *
     * @return disclosure status of the field.
     */
    public Integer getStatus() {
        return status;
    }
}
