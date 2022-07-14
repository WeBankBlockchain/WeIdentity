

package com.webank.weid.protocol.request;

import lombok.Data;

import com.webank.weid.constant.CptType;
import com.webank.weid.protocol.base.WeIdAuthentication;

/**
 * This is a subclass, The Arguments for the SDK API register CPT. The cptJsonSchema is String.
 *
 * @author darwindu
 */
@Data
public class CptStringArgs {

    /**
     * Required: weId authority  for this CPT.
     */
    private WeIdAuthentication weIdAuthentication;

    /**
     * Required: The json schema content defined for this CPT.
     */
    private String cptJsonSchema;

    /**
     * cpt type, "ORIGINAL" or "ZKP". default:"ORIGINAL".
     */
    private CptType cptType = CptType.ORIGINAL;
}
