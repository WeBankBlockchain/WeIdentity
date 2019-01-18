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

package com.webank.weid.util;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.WeIdConstant;

/**
 * Json Schema Validator Util class. Based on com.github.fge.json-schema-validator versioned 2.2.6.
 *
 * @author chaoxinhu 2018.10
 */
public class JsonSchemaValidatorUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaValidatorUtils.class);

    /**
     * Load Json Object. Can be used to return both Json Data and Json Schema.
     *
     * @param jsonString the json string
     * @return JsonNode
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static JsonNode loadJsonObject(String jsonString) throws IOException {
        return JsonLoader.fromString(jsonString);
    }

    /**
     * Validate Json Data versus Json Schema.
     *
     * @param jsonData the json data
     * @param jsonSchema the json schema
     * @return true if yes, false otherwise
     * @throws Exception the exception
     */
    public static boolean validateJsonVersusSchema(String jsonData, String jsonSchema)
        throws Exception {
        JsonNode jsonDataNode = loadJsonObject(jsonData);
        JsonNode jsonSchemaNode = loadJsonObject(jsonSchema);
        JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(jsonSchemaNode);

        ProcessingReport report = schema.validate(jsonDataNode);
        if (report.isSuccess()) {
            logger.info(report.toString());
            return true;
        } else {
            Iterator<ProcessingMessage> it = report.iterator();
            StringBuffer errorMsg = new StringBuffer();
            while (it.hasNext()) {
                errorMsg.append(it.next().getMessage());
            }
            logger.error("Json schema validator failed, error: {}", errorMsg.toString());
            return false;
        }
    }

    /**
     * Validate Json Schema format validity.
     *
     * @param jsonSchema the json schema
     * @return true if yes, false otherwise
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean isValidJsonSchema(String jsonSchema) throws IOException {
        return JsonSchemaFactory
            .byDefault()
            .getSyntaxValidator()
            .schemaIsValid(loadJsonObject(jsonSchema));
    }

    /**
     * validate Cpt Json Schema validity .
     *
     * @param cptJsonSchema the cpt json schema
     * @return true, if is cpt json schema valid
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean isCptJsonSchemaValid(String cptJsonSchema) throws IOException {
        return StringUtils.isNotEmpty(cptJsonSchema)
            && isValidJsonSchema(cptJsonSchema)
            && cptJsonSchema.length() <= WeIdConstant.JSON_SCHEMA_MAX_LENGTH;
    }
}
