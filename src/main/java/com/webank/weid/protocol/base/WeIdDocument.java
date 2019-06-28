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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.util.DataToolUtils;

/**
 * The base data structure to handle WeIdentity DID Document info.
 *
 * @author tonychen 2018.9.29
 */
@Data
public class WeIdDocument implements JsonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(WeIdDocument.class);

    /**
     *  the serialVersionUID.
     */
    private static final long serialVersionUID = 411522771907189878L;

    /**
     * Required: The id.
     */
    private String id;

    /**
     * Required: The created.
     */
    private Long created;

    /**
     * Required: The updated.
     */
    private Long updated;

    /**
     * Required: The public key list.
     */
    private List<PublicKeyProperty> publicKey = new ArrayList<>();

    /**
     * Required: The authentication list.
     */
    private List<AuthenticationProperty> authentication = new ArrayList<>();

    /**
     * Required: The service list.
     */
    private List<ServiceProperty> service = new ArrayList<>();
    
    @Override
    public String toJson() {
        return DataToolUtils.addTagFromToJson(DataToolUtils.serialize(this));
    }
   
    /**
     * create WeIdDocument with JSON String.
     * @param weIdDocumentJson the weIdDocument JSON String
     * @return WeIdDocument
     */
    public static WeIdDocument fromJson(String weIdDocumentJson) {
        if (StringUtils.isBlank(weIdDocumentJson)) {
            logger.error("create WeIdDocument with JSON String failed, "
                + "the WeIdDocument JSON String is null");
            throw new DataTypeCastException("the WeIdDocument JSON String is null.");
        }
        String weIdDocumentString = weIdDocumentJson;
        if (DataToolUtils.isValidFromToJson(weIdDocumentJson)) {
            weIdDocumentString = DataToolUtils.removeTagFromToJson(weIdDocumentJson);
        }
        return DataToolUtils.deserialize(weIdDocumentString, WeIdDocument.class);
    }
}
