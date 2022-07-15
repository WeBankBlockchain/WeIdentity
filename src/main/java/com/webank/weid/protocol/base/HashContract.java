

package com.webank.weid.protocol.base;

import lombok.Data;

@Data
public class HashContract {
    
    /**
     *  the hash value.
     */
    private String hash;
    
    /**
     * the owner of hash.
     */
    private String owner;
    
    /**
     * the create time.
     */
    private long time;
}
