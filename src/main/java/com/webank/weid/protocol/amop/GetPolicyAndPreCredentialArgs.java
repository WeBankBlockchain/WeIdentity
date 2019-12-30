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


package com.webank.weid.protocol.amop;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;

/**
 * args for GetPolicyAndPreCredential.
 *
 * @author tonychen 2019年12月3日
 */
@Getter
@Setter
public class GetPolicyAndPreCredentialArgs extends AmopBaseMsgArgs {

    /**
     * the id of the policy.
     */
    private String policyId;

    /**
     * the user whom the policy will send to.
     */
    private String targetUserWeId;

    /**
     * the cpt id.
     */
    private Integer cptId;

    /**
     * user's claim data.
     */
    private String claim;

    /**
     * extra data.
     */
    private Map<String, String> extra;
}
