/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid;

import java.util.List;

import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;
import com.webank.wedpr.selectivedisclosure.IssuerClient;
import com.webank.wedpr.selectivedisclosure.IssuerResult;
import com.webank.wedpr.selectivedisclosure.proto.TemplatePublicKey;
import mockit.Mock;
import mockit.MockUp;

public abstract class MockIssuerClient {

    /**
     * mock the makeCredentialTemplate for CPT.
     */
    public static void mockMakeCredentialTemplate() {

        new MockUp<IssuerClient>() {

            @Mock
            public IssuerResult makeCredentialTemplate(List<String> list) {
                IssuerResult restult = new IssuerResult();
                restult.templateSecretKey = "templateSecretKey";
                CredentialTemplateEntity template = new CredentialTemplateEntity();
                template.setCredentialKeyCorrectnessProof("credentialKeyCorrectnessProof");
                template.setPublicKey(TemplatePublicKey.getDefaultInstance());
                restult.credentialTemplateEntity = template;
                return restult;
            }
        };
    }
}
