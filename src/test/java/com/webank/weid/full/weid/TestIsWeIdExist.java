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
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

public class TestIsWeIdExist extends TestBaseServcie {

    @Test
    /**
     * case: weIdentity DID is Exist
     *
     * @throws Exception
     */
    public void testIsWeIdExistCase1() {

        ResponseData<CreateWeIdDataResult> response = service.createWeId();
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        ResponseData<Boolean> response1 = service.isWeIdExist(response.getResult().getWeId());
        System.out.println("\nisWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());
    }

    @Test
    /**
     * case: weIdentity DID is empty
     *
     * @throws Exception
     */
    public void testIsWeIdExistCase2() {

        ResponseData<Boolean> response1 = service.isWeIdExist(null);
        System.out.println("\nisWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    @Test
    /**
     * case: weIdentity DID is invalid
     *
     * @throws Exception
     */
    public void testIsWeIdExistCase3() {

        ResponseData<Boolean> response1 = service.isWeIdExist("xxxxxx");
        System.out.println("\nisWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    @Test
    /**
     * case: weIdentity DID is not exist
     *
     * @throws Exception
     */
    public void testIsWeIdExistCase4() {

        ResponseData<Boolean> response1 = service.isWeIdExist("did:weid:xxxxxx");
        System.out.println("\nisWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    @Test
    /**
     * case: weIdentity DID is Exist
     *
     * @throws Exception
     */
    public void testIsWeIdExistCase5() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();

        ResponseData<String> response = service.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        ResponseData<Boolean> response1 = service.isWeIdExist(response.getResult());
        System.out.println("\nisWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());
    }
}
