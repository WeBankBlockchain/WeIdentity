

package com.webank.weid.protocol.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * WeIdentity DID information for created. 
 * @author v_wbgyang
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WeIdPojo extends WeIdBaseInfo {
    
    /**
     * the blockNum for the WeIdentity DID.
     */
    private Integer currentBlockNum;
    
    /**
     * the index for the blockNum.
     */
    private Integer index;
    
    /**
     * the previous blockNum.
     */
    private Integer previousBlockNum;
}
