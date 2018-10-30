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

package com.webank.weid.full.weid;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

public class TestSetService extends TestBaseServcie {

    @Test
    /**
     * case: set success
     *
     * @throws Exception
     */
    public void testSetServiceCase1() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    @Test
    /**
     * case: weIdentity DID is blank
     *
     * @throws Exception
     */
    public void testSetServiceCase2() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setWeId(null);

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: weIdentity DID is bad format
     *
     * @throws Exception
     */
    public void testSetPublicKeyCase3() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setWeId("di:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac73");

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: weIdentity DID is not exists
     *
     * @throws Exception
     */
    public void testSetPublicKeyCase4() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setWeId("did:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac7a");

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: type is null 
     * TODO return code is success and result is true
     *
     * @throws Exception
     */
    public void testSetPublicKeyCase5() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setType(null);

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: type too long 
     * TODO throw exception
     *
     * @throws Exception
     */
    public void testSetPublicKeyCase6() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setType(
            "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: serviceEndpoint is null (or " ") 
     * TODO throw NullPointerException
     *
     * @throws Exception
     */
    public void testSetPublicKeyCase7() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setServiceEndpoint(null);

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: userWeIdPrivateKey is null
     *
     * @throws Exception
     */
    public void testSetPublicKeyCase8() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setUserWeIdPrivateKey(null);

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey is null
     *
     * @throws Exception
     */
    public void testSetPublicKeyCase9() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: the private key belongs to other weIdentity DID
     *
     * @throws Exception
     */
    public void testSetPublicKeyCase10() throws Exception {

        CreateWeIdDataResult createWeIdNew = super.createWeId();

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);

        setServiceArgs.setUserWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey is invalid ("xxxxxxxxxxx" or "11111111111111")
     * TODO when privateKey is xxxxxxxxxxx then throw excpetion
     *
     * @throws Exception
     */
    public void testSetPublicKeyCase11() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey("11111111111111");

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey and privateKey of weIdentity DID do not match
     *
     * @throws Exception
     */
    public void testSetPublicKeyCase12() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);

        String[] pk = TestBaseUtil.createEcKeyPair();
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey(pk[1]);

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: other weIdentity DID
     *
     * @throws Exception
     */
    public void testSetPublicKeyCase13() throws Exception {

        CreateWeIdDataResult createWeIdNew = super.createWeId();

        CreateWeIdDataResult createWeId = super.createWeId();

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setWeId(createWeIdNew.getWeId());

        ResponseData<Boolean> response = service.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
