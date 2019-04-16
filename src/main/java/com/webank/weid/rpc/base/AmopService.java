package com.webank.weid.rpc.base;

import com.webank.weid.service.impl.callback.DirectRouteCallback;

/**
 * @author tonychen 2019年4月16日
 *
 */
public interface AmopService {
    /**
     * 注册处理来自其他机构的通知消息的回调函数
     * 需要实现一个类，继承DirectRouteCallback，并实现对应的几个onPush函数
     */
    void registerCallback(DirectRouteCallback directRouteCallback);
}
