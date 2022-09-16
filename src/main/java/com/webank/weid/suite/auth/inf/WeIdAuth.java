

package com.webank.weid.suite.auth.inf;

import java.util.List;

import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.auth.protocol.WeIdAuthObj;

/**
 * Created by Junqi Zhang on 2020/3/8.
 */
public interface WeIdAuth {

    /**
     * 设置白名单，指定哪些机构可以创建连接.
     * @param whitelistWeId 白名单列表
     * @return 成功或失败的状态
     */
     
    public Integer setWhiteList(List<String> whitelistWeId);

    /**
     * 服务端注册callback，实现WeIdAuthCallback接口，然后调用该接口进行callback的注册.
     * @param callback callback 实例.
     * @return 成功或失败的状态
     */
    public Integer registerCallBack(WeIdAuthCallback callback);

    /**
     * 获取callback实例.
     * @return callback实例
     */
    public WeIdAuthCallback getCallBack();

    /**
     * 将weIdAuth对象放入缓存.
     * @param weIdAuthObj 生成的WeIdAuthObj对象
     * @return 成功或失败的状态
     */
    public Integer addWeIdAuthObj(WeIdAuthObj weIdAuthObj);

    /**
     * 根据channelId取weIdAuth对象.
     * @param channelId channel Id
     * @return 跟channelId对应的weIdAuth 对象
     */
    public WeIdAuthObj getWeIdAuthObjByChannelId(String channelId);

    /**
     * 创建单向的认证通道，channelId由服务端生成.
     * @param toOrgId 服务端的机构ID
     * @param weIdAuthentication 客户端的私钥信息
     * @return WeIdAuthObj
     */
  
    public ResponseData<WeIdAuthObj> createAuthenticatedChannel(
        String toOrgId,
        WeIdAuthentication weIdAuthentication
    );

    /**
     * 创建双向的认证通道（客户端向服务端发起challenge，同时服务端也会向客户端发起challenge，channelId由服务端生成）.
     * @param toOrgId 服务度的机构ID
     * @param weIdAuthentication 用户的私钥认证信息
     * @return weIdAuth 对象
     */
    public ResponseData<WeIdAuthObj> createMutualAuthenticatedChannel(
        String toOrgId,
        WeIdAuthentication weIdAuthentication
    );

}
