package com.webank.weid.suite.transportation.json;

import com.webank.weid.protocol.amop.JsonSerializer;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.entity.ProtocolProperty;
import com.webank.weid.suite.transportation.Transportation;
import com.webank.weid.suite.transportation.json.JsonTransportationService;

public class JsonTransportation {
    
    public static Transportation specifyVerifier(String weId) {
        return newInstance().specifyVerifier(weId);
    }

    /**
     * JSON协议传输序列化接口.
     * @param object 协议存储的实体数据对象
     * @param property 协议的配置对象
     * @return 返回协议字符串数据
     */
    public static <T extends JsonSerializer> ResponseData<String> serialize(
        T object,
        ProtocolProperty property
    ) {
        return newInstance().serialize(object, property);
    }

    /**
     * JSON协议反序列化接口.
     * @param transString JSON格式的协议数据字符串
     * @param clazz 需要转换成的Class类型
     * @return 返回PresentationE对象数据
     */
    public static <T extends JsonSerializer> ResponseData<T> deserialize(
        String transString,
        Class<T> clazz) {
        return newInstance().deserialize(transString, clazz);
    }
    
    private static Transportation newInstance() {
        return new JsonTransportationService();
    }
}
