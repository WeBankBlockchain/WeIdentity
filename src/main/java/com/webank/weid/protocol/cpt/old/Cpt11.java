/*
 *       Copyright© (2019) WeBank Co., Ltd.
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

package com.webank.weid.protocol.cpt.old;

import java.util.List;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;

/**
 * the CPT for test.
 * @author Created by Junqi Zhang on 2019/4/3.
 */
@Data
@Attributes(title = "test CPT", description = "Reserved CPT 11")
public class Cpt11 {

    @Attributes(required = true, description = "CPT ID", minimum = 1)
    private Integer cptId;
    @Attributes(required = true, description = "User ID")
    private String userId;
    @Attributes(required = true, description = "User Name", maxLength = 30)
    private String userName;
    @Attributes(required = true, description = "Registered Tags", minItems = 1)
    private List<String> tags;
    @Attributes(required = true, description = "Gender", enums = {"MALE", "FEMALE"})
    private String gender;
}
