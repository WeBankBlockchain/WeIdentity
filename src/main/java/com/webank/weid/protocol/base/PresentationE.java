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

package com.webank.weid.protocol.base;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Junqi Zhang on 2019/4/4.
 */
@Getter
@Setter
public class PresentationE {

    /**
     * Required: The context field.
     */
    private List<String> context;

    private List<String> type;

    private List<CredentialPojoWrapper> credentialList;

    private Map<String, String> proof;


//    private PresentationPolicyE presentationPolicy;

    public String toJson() {
        // sort the credential in the list based on the credential id before converting to json.
        return null;
    }

    public PresentationE create(
        List<CredentialPojoWrapper> credentialList,
        PolicyAndChellenge policyAndChellenge,
        WeIdAuthentication weIdAuthentication) {

        //call function createSelectiveCredential in CredentialPojoService to create the selective
        // disclosure credential.
        return null;
    }
}
