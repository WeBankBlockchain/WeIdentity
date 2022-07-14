

package com.webank.weid.protocol.request;

import java.util.Map;

import lombok.Data;

import com.webank.weid.constant.CptType;
import com.webank.weid.protocol.base.WeIdAuthentication;

/**
 * The Arguments for the SDK API register CPT. The cptJsonSchema is Map.
 *
 * @author lingfenghe
 */
@Data
public class CptMapArgs {

    /**
     * Required: weId authority  for this CPT.
     */
    private WeIdAuthentication weIdAuthentication;
    
    /**
     * Required: The json schema content defined for this CPT.
     */
    private Map<String, Object> cptJsonSchema;
    
    /**
     * cpt type, "ORIGINAL" or "ZKP". default:"ORIGINAL".
     */
    private CptType cptType = CptType.ORIGINAL;
}
