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

package com.webank.weid.full.transportation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.Mock;
import mockit.MockUp;

import com.webank.weid.connectivity.driver.DataDriver;
import com.webank.weid.connectivity.driver.MysqlDriver;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.amop.AmopCommonArgs;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.CredentialPojoWrapper;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.response.HandleEntity;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.BaseService;
import com.webank.weid.suite.transportation.qr.QrCodeTransportation;
import com.webank.weid.suite.transportation.qr.QrCodeTransportationService;
import com.webank.weid.util.DataToolUtils;

public abstract class TestBaseTransportation extends TestBaseServcie {

    protected QrCodeTransportation qrCodeTransportation = new QrCodeTransportationService();

    protected DataDriver dataDriver = new MysqlDriver();

    protected ResponseData<PresentationE> mockCipherDeserialize(ResponseData<String> response) {
        MockUp<BaseService> mockBaseService = new MockUp<BaseService>() {
            @Mock
            public ResponseData<AmopResponse> request(String toOrgId, AmopCommonArgs args) {
                HandleEntity entity = new HandleEntity();
                entity.setErrorCode(ErrorCode.SUCCESS.getCode());
                entity.setErrorMessage(ErrorCode.SUCCESS.getCodeDesc());
                String result = dataDriver.getData(args.getMessage()).getResult();
                entity.setResult(result);
                AmopResponse respone = new AmopResponse();
                respone.setErrorCode(ErrorCode.SUCCESS.getCode());
                respone.setErrorMessage(ErrorCode.SUCCESS.getCodeDesc());
                respone.setResult(DataToolUtils.serialize(entity));
                return new ResponseData<>(respone, ErrorCode.SUCCESS);
            }
        };
        ResponseData<PresentationE> wrapperRes =
                qrCodeTransportation.deserialize(response.getResult(), PresentationE.class);
        mockBaseService.tearDown();
        return wrapperRes;
    }
    
    

    protected PresentationE getPresentationE() {
        
        List<String> context = new ArrayList<String>();
        context.add("v1");
        context.add("v2");
        PresentationE presentation = new PresentationE();
        presentation.setContext(context);
        
        List<String> types = new ArrayList<String>();
        types.add("type1");
        types.add("type2");
        presentation.setType(types);
        
        Map<String, String> proof = new HashMap<String, String>();
        proof.put("key1", "value1");
        proof.put("key2", "value2");
        presentation.setProof(proof);
        
        CredentialPojoWrapper wrapper = new CredentialPojoWrapper();
        CredentialPojo credentialPojo = new CredentialPojo();
        credentialPojo.setCptId(123);
        credentialPojo.setClaim(TestBaseUtil.buildCptJsonSchemaData());
        wrapper.setCredentialPojo(credentialPojo);
        
        List<CredentialPojoWrapper> credentialList = new ArrayList<>();
        credentialList.add(wrapper);
        presentation.setCredentialList(credentialList);
        return presentation;
    }
}
