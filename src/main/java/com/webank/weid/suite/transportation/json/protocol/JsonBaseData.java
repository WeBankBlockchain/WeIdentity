

package com.webank.weid.suite.transportation.json.protocol;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.suite.entity.TransBaseData;

/**
 * JSON协议实体.
 * @author v_wbgyang
 *
 */
@Getter
@Setter
public class JsonBaseData extends TransBaseData {

    /**
     * 协议通讯类型.
     */
    @Deprecated
    private String type = "AMOP";
}
