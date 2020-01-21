package com.webank.weid.constant;

/**
 * CPT type.
 *
 * @author tonychen 2020年1月14日
 */
public enum CptTypeConstant {

    ORIGINAL(0),

    ZKP(1);

    /**
     * CPT type code.
     */
    private Integer type;

    /**
     * constructor.
     */
    private CptTypeConstant(Integer cptType) {

        this.type = cptType;
    }

    /**
     * get cpt type.
     * @return CPT type code
     */
    public Integer getType() {

        return this.type;
    }

}
