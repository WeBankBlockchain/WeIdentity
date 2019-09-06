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

package com.webank.weid.full.transportation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.response.ResponseData;

public abstract class TestBaseTransportation extends TestBaseServcie {

    protected static PresentationE presentationE;
    protected static PresentationPolicyE presentationPolicyE;
    protected static Challenge challenge;
    protected static List<String> verifier = new ArrayList<String>();
    protected static WeIdAuthentication weIdAuthentication;

    @Override
    public synchronized void testInit() {
        super.testInit();
        if (verifier.isEmpty()) {
            verifier.add(createWeIdResult.getWeId());
        }
        if (weIdAuthentication == null) {
            weIdAuthentication = new WeIdAuthentication();
            weIdAuthentication.setWeId(createWeIdResult.getWeId());
            weIdAuthentication.setWeIdPublicKeyId(createWeIdResult.getWeId() + "#key-0");
            weIdAuthentication.setWeIdPrivateKey(new WeIdPrivateKey());
            weIdAuthentication.getWeIdPrivateKey().setPrivateKey(
                createWeIdResult.getUserWeIdPrivateKey().getPrivateKey());
        }
    }


    protected PresentationE getPresentationE() {
        ResponseData<CredentialPojo> credentialPojoResponse =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        if (credentialPojoResponse.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return null;
        }
        List<CredentialPojo> credentialList = new ArrayList<>();
        credentialList.add(credentialPojoResponse.getResult());

        PresentationPolicyE presentationPolicyE = PresentationPolicyE.create("policy.json");
        if (presentationPolicyE == null) {
            return null;
        }
        presentationPolicyE.setPolicyPublisherWeId(createWeIdResultWithSetAttr.getWeId());
        Map<Integer, ClaimPolicy> policyMap = presentationPolicyE.getPolicy();
        ClaimPolicy cliamPolicy = policyMap.get(1000);
        policyMap.remove(1000);
        policyMap.put(createCredentialPojoArgs.getCptId(), cliamPolicy);

        this.presentationPolicyE = presentationPolicyE;
        Challenge challenge = Challenge.create(
            createWeIdResultWithSetAttr.getWeId(),
            String.valueOf(System.currentTimeMillis())
        );
        this.challenge = challenge;
        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );

        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return null;
        }
        return response.getResult();
    }

    protected PresentationE getPresentationE4MlCpt() {
        ResponseData<CredentialPojo> credentialPojoResponse =
            credentialPojoService.createCredential(createCredentialPojoArgs1);
        if (credentialPojoResponse.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return null;
        }
        List<CredentialPojo> credentialList = new ArrayList<>();
        credentialList.add(credentialPojoResponse.getResult());

        PresentationPolicyE presentationPolicyE = PresentationPolicyE.create("testPolicy.json");
        if (presentationPolicyE == null) {
            return null;
        }
        presentationPolicyE.setPolicyPublisherWeId(createWeIdResultWithSetAttr.getWeId());
        Map<Integer, ClaimPolicy> policyMap = presentationPolicyE.getPolicy();
        ClaimPolicy cliamPolicy = policyMap.get(1001);
        policyMap.remove(1000);
        policyMap.remove(1001);
        policyMap.remove(1002);
        policyMap.put(createCredentialPojoArgs1.getCptId(), cliamPolicy);

        this.presentationPolicyE = presentationPolicyE;
        Challenge challenge = Challenge.create(
            createWeIdResultWithSetAttr.getWeId(),
            String.valueOf(System.currentTimeMillis())
        );
        this.challenge = challenge;
        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );

        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return null;
        }
        return response.getResult();
    }

    protected PresentationE getPresentationE4MultiCpt() {
        ResponseData<CredentialPojo> credentialPojoResponse2 =
            credentialPojoService.createCredential(createCredentialPojoArgs2);

        ResponseData<CredentialPojo> credentialPojoResponse3 =
            credentialPojoService.createCredential(createCredentialPojoArgs3);

        if (credentialPojoResponse2.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return null;
        }

        if (credentialPojoResponse3.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return null;
        }

        List<CredentialPojo> credentialList = new ArrayList<>();
        credentialList.add(credentialPojoResponse2.getResult());
        credentialList.add(credentialPojoResponse3.getResult());

        PresentationPolicyE presentationPolicyE = PresentationPolicyE.create("testPolicy.json");
        if (presentationPolicyE == null) {
            return null;
        }
        presentationPolicyE.setPolicyPublisherWeId(createWeIdResultWithSetAttr.getWeId());
        Map<Integer, ClaimPolicy> policyMap = presentationPolicyE.getPolicy();
        ClaimPolicy cliamPolicy1000 = policyMap.get(1000);
        ClaimPolicy cliamPolicy1001 = policyMap.get(1001);

        policyMap.remove(1000);
        policyMap.remove(1001);
        policyMap.remove(1002);
        policyMap.put(createCredentialPojoArgs3.getCptId(), cliamPolicy1000);
        policyMap.put(createCredentialPojoArgs2.getCptId(), cliamPolicy1001);

        this.presentationPolicyE = presentationPolicyE;
        Challenge challenge = Challenge.create(
            createWeIdResultWithSetAttr.getWeId(),
            String.valueOf(System.currentTimeMillis())
        );
        this.challenge = challenge;
        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );

        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return null;
        }
        return response.getResult();
    }

    protected PresentationE getPresentationE4SpecTplCpt() {
        ResponseData<CredentialPojo> credentialPojoResponse =
            credentialPojoService.createCredential(createCredentialPojoArgs4);
        if (credentialPojoResponse.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return null;
        }
        List<CredentialPojo> credentialList = new ArrayList<>();
        credentialList.add(credentialPojoResponse.getResult());

        PresentationPolicyE presentationPolicyE = PresentationPolicyE
            .create("testSpecTplPolicy.json");
        if (presentationPolicyE == null) {
            return null;
        }
        presentationPolicyE.setPolicyPublisherWeId(createWeIdResultWithSetAttr.getWeId());
        Map<Integer, ClaimPolicy> policyMap = presentationPolicyE.getPolicy();
        ClaimPolicy cliamPolicy1005 = policyMap.get(1005);
        policyMap.remove(1005);
        policyMap.put(createCredentialPojoArgs4.getCptId(), cliamPolicy1005);

        this.presentationPolicyE = presentationPolicyE;
        Challenge challenge = Challenge.create(
            createWeIdResultWithSetAttr.getWeId(),
            String.valueOf(System.currentTimeMillis())
        );
        this.challenge = challenge;
        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );

        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return null;
        }
        return response.getResult();
    }
}
