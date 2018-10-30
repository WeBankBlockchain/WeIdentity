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
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.response.ResponseData;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestCreateWeId2 extends TestBaseServcie {

    @Test
    /**
     * case: create success
     *
     * @throws Exception
     */
    public void testCreateWeIdCase1() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = service.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        ResponseData<WeIdDocument> response1 = service.getWeIdDocument(response.getResult());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertNotNull(response1.getResult());
    }

    @Test
    /**
     * case: createWeIdArgs is null
     *
     * @throws Exception
     */
    public void testCreateWeIdCase2() throws Exception {

        ResponseData<String> response = service.createWeId(null);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        ResponseData<WeIdDocument> response1 = service.getWeIdDocument(response.getResult());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response1.getErrorCode().intValue());
        Assert.assertNotNull(response1.getResult());
    }

    @Test
    /**
     * case: publicKey is null
     *
     * @throws Exception
     */
    public void testCreateWeIdCase3() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.setPublicKey(null);

        ResponseData<String> response = service.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PUBLICKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    @Test
    /**
     * case: publicKey is Non integer string
     *
     * @throws Exception
     */
    public void testCreateWeIdCase4() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.setPublicKey("abc");

        ResponseData<String> response = service.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    @Test
    /**
     * case: weIdPrivateKey is null
     *
     * @throws Exception
     */
    public void testCreateWeIdCase5() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.setWeIdPrivateKey(null);

        ResponseData<String> response = service.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    @Test
    /**
     * case: privateKey is null
     *
     * @throws Exception
     */
    public void testCreateWeIdCase6() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<String> response = service.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    @Test
    /**
     * case: privateKey is invalid 
     * TODO when privateKey is xxx then throw NumberFormatException
     *
     * @throws Exception
     */
    public void testCreateWeIdCase7() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.getWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxx");

        ResponseData<String> response = service.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    @Test
    /**
     * case: privateKey and publicKey misMatch
     *
     * @throws Exception
     */
    public void testCreateWeIdCase8() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        String[] pk = TestBaseUtil.createEcKeyPair();
        createWeIdArgs.getWeIdPrivateKey().setPrivateKey(pk[1]);

        ResponseData<String> response = service.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }
}
