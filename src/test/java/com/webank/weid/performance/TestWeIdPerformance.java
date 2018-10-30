/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

package com.webank.weid.performance;

import com.webank.weid.BaseTest;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.WeIdService;
import org.junit.Test;

public class TestWeIdPerformance extends BaseTest<WeIdService> {

    @Override
    public Class<WeIdService> initService() {

        return WeIdService.class;
    }

    @Test
    public void testGetWeIdDom() throws Exception {

        System.out.println("blockNumber:" + this.getBlockNumber());

        CreateWeIdDataResult weIdResult = this.createWeId();
        System.out.println("weIdentity DID:" + weIdResult.getWeId());

        int count = 1;
        for (int i = 0; i < count; i++) {
            this.setService(weIdResult, "driving" + i, "https://weidentity.webank.com/endpoint/8377464" + i);
            System.out.println("------" + i);
        }
        System.out.println("blockNumber:" + this.getBlockNumber());

        long startTime = System.currentTimeMillis();

        WeIdDocument result = this.getWeIdDom(weIdResult.getWeId());
        System.out.println(result.getService().size());

        long gasTime = System.currentTimeMillis() - startTime;
        System.out.println("use time:" + gasTime + "ms");
    }

    /**
     * create weIdentity DID
     */
    public CreateWeIdDataResult createWeId() {

        // create weIdentity DID,publicKey,privateKey
        ResponseData<CreateWeIdDataResult> responseCreate = service.createWeId();
        // check result is success
        if (responseCreate.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            throw new RuntimeException(responseCreate.getErrorMessage());
        }
        return responseCreate.getResult();
    }

    /**
     * setPublicKey
     */
    public void setPublicKey(CreateWeIdDataResult createResult, String keyType) {

        // setPublicKey for this WeId
        SetPublicKeyArgs setPublicKeyArgs = new SetPublicKeyArgs();
        setPublicKeyArgs.setWeId(createResult.getWeId());
        setPublicKeyArgs.setPublicKey(createResult.getUserWeIdPublicKey().getPublicKey());
        setPublicKeyArgs.setType(keyType);
        ResponseData<Boolean> responseSetPub = service.setPublicKey(setPublicKeyArgs);
        // check is success
        if (responseSetPub.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || !responseSetPub.getResult()) {
            throw new RuntimeException(responseSetPub.getErrorMessage());
        }
    }

    /**
     * setService
     */
    public void setService(
        CreateWeIdDataResult createResult, String serviceType, String serviceEnpoint) {

        // setService for this weIdentity DID
        SetServiceArgs setServiceArgs = new SetServiceArgs();
        setServiceArgs.setWeId(createResult.getWeId());
        setServiceArgs.setType(serviceType);
        setServiceArgs.setServiceEndpoint(serviceEnpoint);
        ResponseData<Boolean> responseSetSer = service.setService(setServiceArgs);
        // check is success
        if (responseSetSer.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || !responseSetSer.getResult()) {
            throw new RuntimeException(responseSetSer.getErrorMessage());
        }
    }

    /**
     * setAuthenticate
     */
    public void setAuthenticate(CreateWeIdDataResult createResult, String authType) {

        // setAuthenticate for this weIdentity DID
        SetAuthenticationArgs setAuthenticationArgs = new SetAuthenticationArgs();
        setAuthenticationArgs.setWeId(createResult.getWeId());
        setAuthenticationArgs.setType(authType);
        setAuthenticationArgs.setPublicKey(createResult.getUserWeIdPublicKey().getPublicKey());
        ResponseData<Boolean> responseSetAuth = service.setAuthentication(setAuthenticationArgs);
        // check is success
        if (responseSetAuth.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || !responseSetAuth.getResult()) {
            throw new RuntimeException(responseSetAuth.getErrorMessage());
        }
    }

    /**
     * getWeIdDom
     */
    public WeIdDocument getWeIdDom(String weId) {

        // get weIdDom
        ResponseData<WeIdDocument> responseResult = service.getWeIdDocument(weId);
        // check result
        if (responseResult.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || responseResult.getResult() == null) {
            throw new RuntimeException(responseResult.getErrorMessage());
        }
        return responseResult.getResult();
    }
}
