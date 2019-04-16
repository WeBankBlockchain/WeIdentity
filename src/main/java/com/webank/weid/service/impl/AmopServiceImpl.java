package com.webank.weid.service.impl;

import com.webank.weid.rpc.base.AmopService;
import com.webank.weid.rpc.callback.OnNotifyCallback;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.impl.callback.DirectRouteCallback;

/**
 * @author tonychen 2019年4月16日
 *
 */
public class AmopServiceImpl extends BaseService implements AmopService {

	/* (non-Javadoc)
	 * @see com.webank.weid.rpc.base.AmopService#registerCallback(com.webank.weid.service.impl.callback.DirectRouteCallback)
	 */
	@Override
	public void registerCallback(DirectRouteCallback directRouteCallback) {
		
		OnNotifyCallback callback = (OnNotifyCallback)getService().getPushCallback();
		callback.setDirectRouteCallback(directRouteCallback);
	}

}
