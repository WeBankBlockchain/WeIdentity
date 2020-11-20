package com.webank.weid.service.impl.engine;

import java.util.List;

import com.webank.weid.protocol.base.HashContract;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.response.ResponseData;

public interface DataBucketServiceEngine {

    /**
     * 以admin身份根据bucketId存放合约地址数据.
     * 
     * @param bucketId 根据合约地址出来的bucketId值，全局唯一
     * @param key 存放数据的key
     * @param value key对应的具体数据
     * @param privateKey 存放数据的私钥信息
     * @return 返回是否存放成功
     */
    ResponseData<Boolean> put(String bucketId, String key, String value, WeIdPrivateKey privateKey);
    
    /**
     * 根据bucketId获取具体合约数据.
     * 
     * @param bucketId 合约地址出来的bucketId
     * @param key 需要获取数据的key
     * @return 返回具体数据
     */
    ResponseData<String> get(String bucketId, String key);
    
    /**
     * 根据bucketId删除extra里面的key.
     * 
     * @param bucketId 根据合约地址出来的bucketId值，全局唯一
     * @param key 存放数据的key
     * @param privateKey 存放数据的私钥信息,
     * @return 返回是否移除成功
     */
    ResponseData<Boolean> removeExtraItem(String bucketId, String key, WeIdPrivateKey privateKey);
    
    /**
     *删除bucketId.
     * 
     * @param bucketId 根据合约地址出来的bucketId值，全局唯一
     * @param force 是否强制删除
     * @param privateKey 存放数据的私钥信息,
     * @return 返回是否移除成功
     */
    ResponseData<Boolean> removeDataBucketItem(
        String bucketId, 
        boolean force,
        WeIdPrivateKey privateKey
    );
    
    /**
     * 机构在使用某个bucket的时候，可以根据自己的私钥信息来表示机构自己正在使用这个bucketId，
     * 此时bucketId所有者也不可以删除此bucketId，如果不执行此方法，bucketId所有者可能会删除该bucketId.
     * 
     * @param bucketId 需要启用的bucketId
     * @param privateKey 机构自己的私钥信息
     * @return 返回是否启用成功
     */
    ResponseData<Boolean> enable(String bucketId, WeIdPrivateKey privateKey);
    
    /**
     *机构停用某个bucket，当机构在更换bucketId的时候，需要先把之前的bucketId给停用了，
     *此时bucketId所有者才可以进行bucketId删除操作.
     *
     * @param bucketId 需要停用的bucketId
     * @param privateKey 停用bucket的用户私钥
     * @return 返回是否停用成功
     */
    ResponseData<Boolean> disable(String bucketId, WeIdPrivateKey privateKey);
    
    /**
     * 获取所有的bucket信息.
     * @return 返回所有的bucket信息
     */
    ResponseData<List<HashContract>> getAllBucket();
    
    /**
     *当用户私钥丢失的情况，管理员给bucket进行所属重置.
     *
     * @param bucketId 需要重置的bucketId
     * @param newOwner 新的所属地址
     * @param privateKey 重置bucket的用户私钥
     * @return 返回是否重置成功
     */
    ResponseData<Boolean> updateBucketOwner(
        String bucketId, 
        String newOwner, 
        WeIdPrivateKey privateKey
    );
    
    /**
     * 根据传入bucketId 获取当前bucket的启用列表.
     * 
     * @param bucketId 查询的bucketId
     * @return 返回启用用户列表
     */
    ResponseData<List<String>> getActivatedUserList(String bucketId);
    
}
