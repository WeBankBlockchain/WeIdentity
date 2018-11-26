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
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Test;

/**
 * performance testing.
 * 
 * @author v_wbgyang
 *
 */
public class TestWeIdPerformance extends BaseTest {

    @Test
    public void testGetWeIdDom() throws Exception {

        System.out.println("blockNumber:" + this.getBlockNumber());

        CreateWeIdDataResult weIdResult = this.createWeId();
        System.out.println("weIdentity DID:" + weIdResult.getWeId());

        int count = 1;
        for (int i = 0; i < count; i++) {
            this.setService(weIdResult,
                "driving" + i,
                "https://weidentity.webank.com/endpoint/8377464" + i);
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
     * create weIdentity DID.
     */
    public CreateWeIdDataResult createWeId() throws RuntimeException {

        // create weIdentity DID,publicKey,privateKey
        ResponseData<CreateWeIdDataResult> responseCreate = weIdService.createWeId();
        // check result is success
        if (responseCreate.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            throw new RuntimeException(responseCreate.getErrorMessage());
        }
        return responseCreate.getResult();
    }

    /**
     * setService.
     */
    public void setService(
        CreateWeIdDataResult createResult,
        String serviceType,
        String serviceEnpoint)
        throws RuntimeException {

        // setService for this weIdentity DID
        SetServiceArgs setServiceArgs = new SetServiceArgs();
        setServiceArgs.setWeId(createResult.getWeId());
        setServiceArgs.setType(serviceType);
        setServiceArgs.setServiceEndpoint(serviceEnpoint);
        setServiceArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setServiceArgs.getUserWeIdPrivateKey()
            .setPrivateKey(createResult.getUserWeIdPrivateKey().getPrivateKey());
        ResponseData<Boolean> responseSetSer = weIdService.setService(setServiceArgs);
        // check is success
        if (responseSetSer.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || !responseSetSer.getResult()) {
            throw new RuntimeException(responseSetSer.getErrorMessage());
        }
    }

    /**
     * getWeIdDom.
     */
    public WeIdDocument getWeIdDom(String weId) throws RuntimeException {

        // get weIdDom
        ResponseData<WeIdDocument> responseResult = weIdService.getWeIdDocument(weId);
        // check result
        if (responseResult.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || responseResult.getResult() == null) {
            throw new RuntimeException(responseResult.getErrorMessage());
        }
        return responseResult.getResult();
    }
}
