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

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.suite.api.persistence.Persistence;
import com.webank.weid.suite.api.transportation.TransportationFactory;
import com.webank.weid.suite.persistence.driver.MysqlDriver;
import com.webank.weid.protocol.amop.GetEncryptKeyArgs;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.CredentialPojoWrapper;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.AmopServiceImpl;

public abstract class TestBaseTransportation extends TestBaseServcie {

    protected Persistence dataDriver = new MysqlDriver();

    protected ResponseData<PresentationE> mockCipherDeserialize(ResponseData<String> response) {
        MockUp<AmopServiceImpl> mockBaseService = new MockUp<AmopServiceImpl>() {
            @Mock
            public ResponseData<GetEncryptKeyResponse> getEncryptKey(String toOrgId, GetEncryptKeyArgs args) {
                GetEncryptKeyResponse getEncryptKeyResponse = new GetEncryptKeyResponse();
                getEncryptKeyResponse.setErrorCode(ErrorCode.SUCCESS.getCode());
                getEncryptKeyResponse.setErrorMessage(ErrorCode.SUCCESS.getCodeDesc());
                String result = dataDriver.get("transEncryption", args.getKeyId()).getResult();
                getEncryptKeyResponse.setEncryptKey(result);
                return new ResponseData<>(getEncryptKeyResponse, ErrorCode.SUCCESS);
            }
        };
        ResponseData<PresentationE> wrapperRes =
            TransportationFactory.newQrCodeTransportation().deserialize(response.getResult(), PresentationE.class);
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
