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
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

public class TestGetWeIdDocument extends TestBaseServcie {

    @Test
    /**
     * case: set many times and get the weIdDom
     *
     * @throws Exception
     */
    public void testGetWeIdDocumentCase1() throws Exception {

        CreateWeIdDataResult createWeIdNew = super.createWeId();

        CreateWeIdDataResult createWeId = super.createWeId();

        super.setPublicKey(
            createWeId, createWeId.getUserWeIdPublicKey().getPublicKey(), createWeId.getWeId());
        super.setPublicKey(createWeId, TestBaseUtil.createEcKeyPair()[0], createWeIdNew.getWeId());

        super.setAuthentication(
            createWeId, createWeId.getUserWeIdPublicKey().getPublicKey(), createWeIdNew.getWeId());
        super
            .setAuthentication(createWeId, TestBaseUtil.createEcKeyPair()[0], createWeId.getWeId());

        super.setService(createWeId, "drivingCardService", "https://weidentity.webank.com/endpoint/8377464");
        super.setService(createWeId, "drivingCardServic1", "https://weidentity.webank.com/endpoint/8377465");

        ResponseData<WeIdDocument> weIdDoc = service.getWeIdDocument(createWeId.getWeId());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNotNull(weIdDoc.getResult());
    }

    @Test
    /**
     * case: set and get weIdDom
     *
     * @throws Exception
     */
    public void testGetWeIdDocumentCase2() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        super.setPublicKey(
            createWeId, createWeId.getUserWeIdPublicKey().getPublicKey(), createWeId.getWeId());
        super.setAuthentication(
            createWeId, createWeId.getUserWeIdPublicKey().getPublicKey(), createWeId.getWeId());
        super.setService(createWeId, "drivingCardService", "https://weidentity.webank.com/endpoint/8377464");

        ResponseData<WeIdDocument> weIdDoc = service.getWeIdDocument(createWeId.getWeId());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNotNull(weIdDoc.getResult());
    }

    @Test
    /**
     * case: weIdentity DID is invalid 
     * TODO return code is WEID_INVALID and result is not null
     * 
     * @throws Exception
     */
    public void testGetWeIdDocumentCase3() throws Exception {

        ResponseData<WeIdDocument> weIdDoc = service.getWeIdDocument("xxxxxxxxxx");
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
    }

    @Test
    /**
     * case: weIdentity DID is null 
     * TODO return code is WEID_INVALID and result is not null
     *
     * @throws Exception
     */
    public void testGetWeIdDocumentCase4() throws Exception {

        ResponseData<WeIdDocument> weIdDoc = service.getWeIdDocument(null);
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
    }

    @Test
    /**
     * case: weIdentity DID is not exists
     *
     * @throws Exception
     */
    public void testGetWeIdDocumentCase5() throws Exception {

        ResponseData<WeIdDocument> weIdDoc =
            service.getWeIdDocument("did:weid:0xa1c93e93622c6a0b2f52c90741e0b98ab77385a9");
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_DOES_NOT_EXIST.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }
}
