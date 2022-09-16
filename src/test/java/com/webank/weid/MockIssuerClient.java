

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
