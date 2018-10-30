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
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

public class TestSetAuthentication extends TestBaseServcie {

    @Test
    /**
     * case: set success
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase1() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ResponseData<WeIdDocument> weIdDoc = service
            .getWeIdDocument(setAuthenticationArgs.getWeId());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNotNull(weIdDoc.getResult());
    }

    @Test
    /**
     * case: weIdentity DID is blank
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase2() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setWeId(null);

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase3() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setWeId("di:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac73");

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: weIdentity DID is not exists
     * TODO return code is WEID_PRIVATEKEY_DOES_NOT_MATCH 
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase4() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setWeId("did:weid:0xbb");

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: type is null or other string 
     * TODO return code is success and the result is true
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase5() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setType(null);

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: publicKey is a new key
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase6() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        String[] pk = TestBaseUtil.createEcKeyPair();

        setAuthenticationArgs.setPublicKey(pk[0]);

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ResponseData<WeIdDocument> weIdDoc = service
            .getWeIdDocument(setAuthenticationArgs.getWeId());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNotNull(weIdDoc.getResult());
    }

    @Test
    /**
     * case: publicKey is null 
     * TODO return code is success and the result is true
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase7() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setPublicKey(null);

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PUBLICKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: publicKey is invalid ("xxxxxxxxxx" or "1111111111111")
     * TODO return code is success and the result is true
     * 
     * @throws Exception
     */
    public void testSetAuthenticationCase8() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setPublicKey("11111111111111111");

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    @Test
    /**
     * case: userWeIdPrivateKey is null
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase9() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setUserWeIdPrivateKey(null);

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase10() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey is invalid 
     * TODO throw exception
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase11() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey and privateKey of weIdentity DID does not match 
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase12() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        String[] pk = TestBaseUtil.createEcKeyPair();
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey(pk[1]);

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey belongs to the private key of other weIdentity DID
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase13() throws Exception {

        CreateWeIdDataResult createWeIdNew = super.createWeId();

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setUserWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase14() throws Exception {

        CreateWeIdDataResult createWeIdNew = super.createWeId();

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setWeId(createWeIdNew.getWeId());

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: owner is the weIdentity DID
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase15() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setOwner(setAuthenticationArgs.getWeId());

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ResponseData<WeIdDocument> weIdDoc = service
            .getWeIdDocument(setAuthenticationArgs.getWeId());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNotNull(weIdDoc.getResult());
    }

    @Test
    /**
     * case: owner is other weIdentity DID
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase16() throws Exception {

        CreateWeIdDataResult createWeIdNew = super.createWeId();

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setOwner(createWeIdNew.getWeId());

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ResponseData<WeIdDocument> weIdDoc = service
            .getWeIdDocument(setAuthenticationArgs.getWeId());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNotNull(weIdDoc.getResult());
    }

    @Test
    /**
     * case: owner is invalid
     * TODO return code is success and the result is true
     *
     * @throws Exception
     */
    public void testSetAuthenticationCase17() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setOwner("xxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = service.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ResponseData<WeIdDocument> weIdDoc = service
            .getWeIdDocument(setAuthenticationArgs.getWeId());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNotNull(weIdDoc.getResult());
    }
}
