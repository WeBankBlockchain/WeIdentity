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

package com.webank.weid.service;

import com.webank.weid.BaseTest;
import com.webank.weid.common.BeanUtil;
import com.webank.weid.common.RequestUtil;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.request.UpdateCptArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.CptService;
import org.junit.Test;

/**
 * test CptService
 *
 * @author v_wbgyang
 */
public class TestCptService extends BaseTest<CptService> {

    @Override
    public Class<CptService> initService() {

        return CptService.class;
    }

    /**
     * test CptService.registerCpt
     */
    @Test
    public void testRegisterCpt() throws Exception {

        int scene = 1;

        RegisterCptArgs args = RequestUtil.registerCpt(scene);

        ResponseData<CptBaseInfo> response = service.registerCpt(args);
        BeanUtil.print(response);
    }

    /**
     * test CptService.queryCpt
     */
    @Test
    public void testQueryCpt() throws Exception {

        int scene = 1;

        Integer cptId = RequestUtil.queryCpt(scene);

        ResponseData<Cpt> response = service.queryCpt(cptId);
        BeanUtil.print(response);
    }

    /**
     * test CptService.updateCpt
     */
    @Test
    public void testUpdateCpt() throws Exception {

        int scene = 1;

        UpdateCptArgs args = RequestUtil.updateCpt(scene);

        Integer cptId = RequestUtil.queryCpt(scene);
        ResponseData<Cpt> query = service.queryCpt(cptId);

        args.setCptId(query.getResult().getCptId());
        ResponseData<CptBaseInfo> response = service.updateCpt(args);
        BeanUtil.print(response);
    }
}
