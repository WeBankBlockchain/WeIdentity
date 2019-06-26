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

package com.webank.weid.service.impl.proxy;

import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.engine.WeIdServiceEngine;
import com.webank.weid.service.impl.engine.fiscov1.WeIdServiceEngineV1;
import com.webank.weid.service.impl.engine.fiscov2.WeIdServiceEngineV2;
import com.webank.weid.util.PropertyUtils;

/**
 * @author tonychen 2019年6月21日
 */

public class WeIdServiceProxy {

    private static WeIdServiceEngine engine;

    private static String weIdContractAddr;
    /**
     * fisco bcos version, default 1.3.x
     */
    private static String fiscoVersion = PropertyUtils.getProperty("fisco.version", "1.3");

    public WeIdServiceProxy() {

        if (fiscoVersion.equals("1.3")) {
            engine = new WeIdServiceEngineV1();
        } else {
            engine = new WeIdServiceEngineV2();
        }
    }

    public ResponseData<Boolean> isWeIdExist(String weId) {

        return engine.isWeIdExist(weId);
    }

    public ResponseData<WeIdDocument> getWeIdDocument(String weId) {
        return engine.getWeIdDocument(weId);
    }

    public ResponseData<Boolean> createWeId(String weId, String publicKey,
        String privateKey) {
        return engine.createWeId(weId, publicKey, privateKey);
    }

    public ResponseData<Boolean> setAttribute(String weAddress, String attributeKey,
        String value, String privateKey) {
        return engine.setAttribute(weAddress, attributeKey, value, privateKey);
    }
}
