package com.webank.weid.util;

import com.webank.weid.suite.persistence.DefaultValue;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Driver util
 *
 * @author 刘家辉
 * @date 2023/08/26
 */
public class DataDriverUtils {
    /**
     * Easy to retrieve the desired value object from the Default Value
     *
     * @param value 价值
     * @param clazz clazz
     * @return {@link T}
     */
    public static <T> T decodeValueForNeedObj(String value, Class<T> clazz){
        DefaultValue data = DataToolUtils.deserialize(
                value, DefaultValue.class);
        return DataToolUtils.deserialize(data.getData(), clazz);
    }

    /**
     * Easy to retrieve the desired List object from the Default Value
     *
     * @param values 值
     * @param clazz  clazz
     * @return {@link List}<{@link T}>
     */
    public <T> List<T> decodeValueToNeedListObj (List<String> values, Class<T> clazz){
        List<T> list = new ArrayList<T>();
        for(String value:values ){
            list.add(decodeValueForNeedObj(value,clazz));
        }
        return list;
    }

    /**
     * Easy to retrieve the desired value json from the Default Value
     *
     * @param values 值
     * @return {@link List}<{@link String}>
     */
    public static List<String> decodeValueToNeedListJson(List<String> values){
        List<String> list = new ArrayList<>();
        for(String value:values ){
            list.add(DataToolUtils.deserialize(
                    value, DefaultValue.class).getData());
        }
        return list;
    }

    /**
     * upload to ipfs
     *
     * @param ipfs ipf
     * @param data 数据
     * @return {@link String}
     * @throws IOException ioexception
     */
    public static String uploadIpfs(IPFS ipfs, byte[] data) throws IOException {
        NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(data);
        MerkleNode addResult = null;
        addResult = ipfs.add(file).get(0);
        return addResult.hash.toString();
    }

    /**
     * download from ipfs
     *
     * @param ipfs ipf
     * @param hash 哈希
     * @return {@link String}
     * @throws IOException ioexception
     */
    public static String downloadIpfs(IPFS ipfs,String hash) throws IOException {
        byte[] data;
        data = ipfs.cat(Multihash.fromBase58(hash));
        return DataToolUtils.bytesToStr(data);
    }
}
