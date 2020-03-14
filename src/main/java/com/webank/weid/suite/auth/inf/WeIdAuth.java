/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

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
     * set white list.
     * @param whitelistWeId weId list
     * @return flag
     */
     
    public Integer setWhiteList(List<String> whitelistWeId);

    /**
     * register call back.
     * @param callback callback object.
     * @return flag
     */
    public Integer registerCallBack(WeIdAuthCallback callback);

    /**
     * get callback.
     * @return callback
     */
    public WeIdAuthCallback getCallBack();

    /**
     * add weIdaAuth object to cache.
     * @param weIdAuthObj weIdaAuth object
     * @return flag
     */
    public Integer addWeIdAuthObj(WeIdAuthObj weIdAuthObj);

    /**
     * get weIdaAuth object by channel Id.
     * @param channelId channel Id
     * @return weIdaAuth object
     */
    public WeIdAuthObj getWeIdAuthObjByChannelId(String channelId);

    /**
     * create authenticate channel.
     * @param toOrgId organization id
     * @param weIdAuthentication user's authentication info
     * @return WeIdAuthObj
     */
  
    public ResponseData<WeIdAuthObj> createAuthenticatedChannel(
        String toOrgId,
        WeIdAuthentication weIdAuthentication);

    /**
     * create authenticate channel.
     * @param toOrgId organization id
     * @param weIdAuthentication user's authentication info
     * @return weIdaAuth object
     */
    public ResponseData<WeIdAuthObj> createMutualAuthenticatedChannel(
        String toOrgId,
        WeIdAuthentication weIdAuthentication);

}
