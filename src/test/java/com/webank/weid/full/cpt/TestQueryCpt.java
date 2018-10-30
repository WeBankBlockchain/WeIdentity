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

package com.webank.weid.full.cpt;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.request.UpdateCptArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

public class TestQueryCpt extends TestBaseServcie {

    /**
     * is register issuer
     */
    private boolean isRegisterAuthorityIssuer = false;

    @Test
    /** case： cpt query success */
    public void testQueryCptCase1() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        ResponseData<Cpt> response = cptService.queryCpt(cptBaseInfo.getCptId());
        System.out.println("\nqueryCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /** case： cptId is null */
    public void testQueryCptCase2() {

        ResponseData<Cpt> response = cptService.queryCpt(null);
        System.out.println("\nqueryCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptId is minus number  */
    public void testQueryCptCase3() {

        ResponseData<Cpt> response = cptService.queryCpt(-1);
        System.out.println("\nqueryCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptId is not exists */
    public void testQueryCptCase4() {

        ResponseData<Cpt> response = cptService.queryCpt(100000);
        System.out.println("\nqueryCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CPT_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： query after updateCpt */
    public void testQueryCptCase5() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        ResponseData<Cpt> response = cptService.queryCpt(cptBaseInfo.getCptId());
        System.out.println("\nqueryCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        CreateWeIdDataResult createWeIdNew = super.createWeIdWithSetAttr();

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeIdNew, cptBaseInfo);

        ResponseData<CptBaseInfo> responseUp = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(responseUp);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseUp.getErrorCode().intValue());
        Assert.assertNotNull(responseUp.getResult());

        ResponseData<Cpt> responseQ = cptService.queryCpt(cptBaseInfo.getCptId());
        System.out.println("\nqueryCpt result:");
        BeanUtil.print(responseQ);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseQ.getErrorCode().intValue());
        Assert.assertNotNull(responseQ.getResult());
    }
}
