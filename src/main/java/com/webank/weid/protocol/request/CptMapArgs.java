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

package com.webank.weid.protocol.request;

import java.util.Map;

import lombok.Data;

import com.webank.weid.protocol.base.WeIdAuthentication;

/**
 * The Arguments for the SDK API register CPT. The cptJsonSchema is Map.
 *
 * @author lingfenghe
 */
@Data
public class CptMapArgs {

    /**
     * Required: weId authority  for this CPT.
     */
    private WeIdAuthentication weIdAuthentication;
    /**
     * Required: The json schema content defined for this CPT.
     */
    private Map<String, Object> cptJsonSchema;
}
