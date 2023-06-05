

package com.webank.weid.protocol.inf;

public interface RawSerializer extends JsonSerializer {
    
    public default String toRawData() {
        return JsonSerializer.super.toJson();
    } 
}
