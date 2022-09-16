

package com.webank.weid.constant;

/**
 * 上链处理模式.
 * @author v_wbgyang
 *
 */
public enum ProcessingMode {
    
    /**
     * 立即上链模式，此模式下会立即将数据发送至区块链节点.
     */
    IMMEDIATE,
    
    /**
     * 批量延迟上链模式，此模式下会先将数据存入介质中，然后异步去上链处理.
     */
    PERIODIC_AND_BATCH
}
