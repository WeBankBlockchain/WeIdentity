package com.webank.weid.util;

import com.webank.weid.suite.persistence.DefaultValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Driver util
 *
 * @author 刘家辉
 * @date 2023/08/26
 */
public class DataDriverUtils {
    public static <T> T decodeValueToNeedObj(String value, Class<T> clazz){
        DefaultValue data = DataToolUtils.deserialize(
                value, DefaultValue.class);
        return DataToolUtils.deserialize(data.getData(), clazz);
    }
    public <T> List<T> decodeValueToNeedListObj (List<String> values, Class<T> clazz){
        List<T> list = new ArrayList<T>();
        for(String value:values ){
            list.add(decodeValueToNeedObj(value,clazz));
        }
        return list;
    }
    public static List<String> decodeValueToNeedListJson(List<String> values){
        List<String> list = new ArrayList<>();
        for(String value:values ){
            list.add(DataToolUtils.deserialize(
                    value, DefaultValue.class).getData());
        }
        return list;
    }
}
