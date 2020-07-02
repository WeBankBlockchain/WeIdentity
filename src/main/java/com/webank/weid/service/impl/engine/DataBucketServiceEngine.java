package com.webank.weid.service.impl.engine;

import java.util.List;

import com.webank.weid.protocol.base.HashContract;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.response.ResponseData;

public interface DataBucketServiceEngine {

    /**
     * 以admin身份根据hash存放合约地址数据.
     * 
     * @param hash 根据合约地址出来的hash值，全局唯一
     * @param key 存放数据的key
     * @param value key对应的具体数据
     * @param privateKey 存放数据的私钥信息
     * @return 返回是否存放成功
     */
    ResponseData<Boolean> put(String hash, String key, String value, WeIdPrivateKey privateKey);
    
    /**
     * 根据hash获取具体合约数据.
     * 
     * @param hash 合约地址出来的hash
     * @param key 需要获取数据的key
     * @return 返回具体数据
     */
    ResponseData<String> get(String hash, String key);
    
    /**
     * 根据Hash删除extra里面的key.
     * 
     * @param hash 根据合约地址出来的hash值，全局唯一
     * @param key 存放数据的key
     * @param privateKey 存放数据的私钥信息,
     * @return 返回是否移除成功
     */
    ResponseData<Boolean> removeExtraItem(String hash, String key, WeIdPrivateKey privateKey);
    
    /**
     *删除Hash.
     * 
     * @param hash 根据合约地址出来的hash值，全局唯一
     * @param force 是否强制删除
     * @param privateKey 存放数据的私钥信息,
     * @return 返回是否移除成功
     */
    ResponseData<Boolean> removeDataBucketItem(
        String hash, 
        boolean force,
        WeIdPrivateKey privateKey
    );
    
    /**
     * 机构在使用某个hash的时候，可以根据自己的私钥信息来表示机构自己正在使用这个hash，
     * 此时hash所有者也不可以删除此hash，如果不执行此方法，hash所有者可能会删除该hash.
     * 
     * @param hash 需要启用的hash
     * @param privateKey 机构自己的私钥信息
     * @return 返回是否启用成功
     */
    ResponseData<Boolean> enableHash(String hash, WeIdPrivateKey privateKey);
    
    /**
     *机构停用某个hash，当机构在更换hash的时候，需要先把之前的hash给停用了，
     *此时hash所有者才可以进行hash删除操作.
     *
     * @param hash 需要停用的hash
     * @param privateKey 停用hash的用户私钥
     * @return 返回是否停用成功
     */
    ResponseData<Boolean> disableHash(String hash, WeIdPrivateKey privateKey);
    
    /**
     * 获取所有的hash信息.
     * @return 返回所有的hash信息
     */
    ResponseData<List<HashContract>> getAllHash();
    
    /**
     *当用户私钥丢失的情况，管理员给hash进行所属重置.
     *
     * @param hash 需要重置的hash
     * @param newOwner 新的所属地址
     * @param privateKey 重置hash的用户私钥
     * @return 返回是否重置成功
     */
    ResponseData<Boolean> updateHashOwner(String hash, String newOwner, WeIdPrivateKey privateKey);
    
}
