/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.constant;

import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.protocol.amop.CheckAmopMsgHealthArgs;
import com.webank.weid.protocol.amop.GetEncryptKeyArgs;
import com.webank.weid.protocol.amop.GetPolicyAndChallengeArgs;
import com.webank.weid.protocol.response.AmopNotifyMsgResult;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.GetPolicyAndChallengeResponse;
import com.webank.weid.rpc.callback.AmopCallback;
import com.webank.weid.util.DataToolUtils;

/**
 * Created by junqizhang on 12/06/2017.
 */
public enum AmopMsgType {

    TYPE_ERROR(0),

    /*
     * 链上链下check health
     */
    TYPE_CHECK_DIRECT_ROUTE_MSG_HEALTH(1),
	
	/*
	 * 
	 */
	TYPE_TRANSPORTATION(2),
	
	/*
     * 获取对称秘钥
     */
    GET_ENCRYPT_KEY(3),
	
	/*
     * 获取policy和challenge
     */
    GET_POLICY_AND_CHALLENGE(4);

	
    private Integer value;
    
    private AmopMsgType(Integer index) {
        this.value = index;
    }

    public Integer getValue() {
        return this.value;
    }
  
    public String callOnPush(AmopCallback amopCallback, String messageId, String msgBodyStr) {

        String resultBodyStr = null;

        switch (this) {
        case TYPE_CHECK_DIRECT_ROUTE_MSG_HEALTH: {
            CheckAmopMsgHealthArgs args = DataToolUtils.deserialize(msgBodyStr, CheckAmopMsgHealthArgs.class);
            args.setMessageId(messageId);
            AmopNotifyMsgResult result = amopCallback.onPush(args);
            resultBodyStr = DataToolUtils.serialize(result);
        }
        break;
        case TYPE_TRANSPORTATION: {
        	AmopCommonArgs args = DataToolUtils.deserialize(msgBodyStr, AmopCommonArgs.class);
        	AmopResponse result = amopCallback.onPush(args);
            resultBodyStr = DataToolUtils.serialize(result);
        }
        case GET_ENCRYPT_KEY: {
            // GET key
            GetEncryptKeyArgs args = DataToolUtils.deserialize(msgBodyStr, GetEncryptKeyArgs.class);
            GetEncryptKeyResponse result = amopCallback.onPush(args);
            resultBodyStr = DataToolUtils.serialize(result);
        }
        break;
        case GET_POLICY_AND_CHALLENGE: {
            // GET POLICY AND CHALLENGE
            GetPolicyAndChallengeArgs args = DataToolUtils.deserialize(msgBodyStr, GetPolicyAndChallengeArgs.class);
            GetPolicyAndChallengeResponse result = amopCallback.onPush(args);
            resultBodyStr = DataToolUtils.serialize(result);
        }
        break;
            default:
//                throw
//                logger.error("no handle msg type : " + push.getContent());
                break;
        }

        return resultBodyStr;
    }
}
