/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

package com.webank.weid.protocol.base;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import com.webank.weid.util.DateUtils;

/**
 * The base data structure to handle Authority Issuer info.
 *
 * @author chaoxinhu 2018.10
 */
@Data
public class AuthorityIssuer {

    /**
     * Required: The WeIdentity DID of the Authority Issuer.
     */
    private String weId;

    /**
     * Required: The organization name of the Authority Issuer.
     */
    private String name;

    /**
     * Required: The create date of the Authority Issuer, in timestamp (Long) format.
     */
    private Long created;

    /**
     * Required: The accumulator value of the Authority Issuer.
     */
    private String accValue;

    /**
     * Optional: The description of this Authority Issuer.
     */
    private String description;

    /**
     * Optional: The extra String type information stored on chain.
     */
    private List<String> extraStr32;

    /**
     * Optional: The extra Integer type information stored on chain.
     */
    private List<Integer> extraInt;

    /**
     * Optional: whether this authority issuer is recognized by admin/committee, or not.
     */
    private boolean recognized = false;

    /**
     * Constructor with initialized values.
     *
     * @param weId the WeID
     * @param name the name
     * @param accValue the accumulator value (currently unused)
     * @param description the description
     * @param extraStr32 the extra String list
     * @param extraInt the extra Integer list
     */
    public AuthorityIssuer(
        String weId,
        String name,
        String accValue,
        String description,
        List<String> extraStr32,
        List<Integer> extraInt
    ) {
        this.weId = weId;
        this.name = name;
        this.created = DateUtils.getNoMillisecondTimeStamp();
        this.accValue = accValue;
        this.description = StringUtils.isEmpty(description) ? StringUtils.EMPTY : description;
        this.extraStr32 = extraStr32 == null ? new ArrayList<>() : extraStr32;
        this.extraInt = extraInt == null ? new ArrayList<>() : extraInt;
    }

    /**
     * Empty Constructor.
     */
    public AuthorityIssuer() {
        this.weId = StringUtils.EMPTY;
        this.name = StringUtils.EMPTY;
        this.created = DateUtils.getNoMillisecondTimeStamp();
        this.accValue = StringUtils.EMPTY;
        this.description = StringUtils.EMPTY;
        this.extraStr32 = new ArrayList<>();
        this.extraInt = new ArrayList<>();
    }
}
