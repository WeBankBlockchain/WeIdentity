

package com.webank.weid.protocol.inf;

import java.io.Serializable;

import com.webank.weid.util.DataToolUtils;

public interface JsonSerializer extends Serializable {

    public default String toJson() {
        return DataToolUtils.serialize(this);
    }
}
