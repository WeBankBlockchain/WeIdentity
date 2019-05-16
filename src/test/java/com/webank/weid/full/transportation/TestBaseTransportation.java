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

import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;

public abstract class TestBaseTransportation extends TestBaseServcie {

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
        
        CredentialPojo credentialPojo = new CredentialPojo();
        credentialPojo.setCptId(123);
        credentialPojo.setClaim(TestBaseUtil.buildCptJsonSchemaData());
        
        List<CredentialPojo> credentialList = new ArrayList<>();
        credentialList.add(credentialPojo);
        presentation.setVerifiableCredential(credentialList);
        return presentation;
    }
}
