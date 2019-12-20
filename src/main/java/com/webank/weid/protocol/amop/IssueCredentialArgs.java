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

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;

/**
 * args for requesting issuer to issue credential.
 *
 * @author tonychen 2019年12月4日
 */
@Getter
@Setter
public class IssueCredentialArgs extends AmopBaseMsgArgs {

    /**
     * user's credential list,including KYC credential and credential based on CPT111.
     */
    private PresentationE presentation;

    /**
     * credential based on CPT 110 (metadata credential).
     */
    private CredentialPojo credentialPojo;

    /**
     * user claim (decided by issuer in the first amop interface).
     */
    private String claim;

    //private Integer cptId;

    private String policyId;

    //private String userWeId;

}
