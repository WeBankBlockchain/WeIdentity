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

import java.util.List;

import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.engine.AuthorityIssuerServiceEngine;
import com.webank.weid.service.impl.engine.fiscov1.AuthorityIssuerEngineV1;
import com.webank.weid.service.impl.engine.fiscov2.AuthorityIssuerEngineV2;
import com.webank.weid.util.PropertyUtils;

/**
 * @author tonychen 2019年6月25日
 */
public class AuthorityIssuerServiceProxy {

    private static AuthorityIssuerServiceEngine engine;

    private static String weIdContractAddr;
    /**
     * fisco bcos version, default 1.3.x
     */
    private static String fiscoVersion = PropertyUtils.getProperty("fisco.version", "1.3");

    public AuthorityIssuerServiceProxy() {

        if (fiscoVersion.equals("1.3")) {
            engine = new AuthorityIssuerEngineV1();
        } else {
            engine = new AuthorityIssuerEngineV2();
        }
    }

    public ResponseData<Boolean> addAuthorityIssuer(RegisterAuthorityIssuerArgs args) {
        return engine.addAuthorityIssuer(args);
    }

    public ResponseData<Boolean> removeAuthorityIssuer(RemoveAuthorityIssuerArgs args) {
        return engine.removeAuthorityIssuer(args);
    }

    public ResponseData<Boolean> isAuthorityIssuer(String address) {
        return engine.isAuthorityIssuer(address);
    }

    public ResponseData<AuthorityIssuer> getAuthorityIssuerInfoNonAccValue(String weId) {
        return engine.getAuthorityIssuerInfoNonAccValue(weId);
    }

    public List<String> getAuthorityIssuerAddressList(Integer index, Integer num) {
        return engine.getAuthorityIssuerAddressList(index, num);
    }

    public ResponseData<Boolean> removeIssuer(String issuerType, String issuerAddress, String privateKey) {
        return engine.removeIssuer(issuerType, issuerAddress, privateKey);
    }

    public ResponseData<Boolean> isSpecificTypeIssuer(String issuerType, String address) {
        return engine.isSpecificTypeIssuer(issuerType, address);
    }

    public ResponseData<List<String>> getSpecificTypeIssuerList(String issuerType,
        Integer index,
        Integer num) {
        return engine.getSpecificTypeIssuerList(issuerType, index, num);
    }

    public ResponseData<Boolean> registerIssuerType(String issuerType, String privateKey) {
        return engine.registerIssuerType(issuerType, privateKey);
    }

    public ResponseData<Boolean> addIssuer(String issuerType, String issuerAddress, String privateKey) {
        return engine.addIssuer(issuerType, issuerAddress, privateKey);
    }
}
