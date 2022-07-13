/**
 * Copyright 2014-2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.webank.weid.util;

import static org.junit.Assert.assertThat;
import static sun.nio.cs.Surrogate.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;
import java.util.Set;
import org.junit.Test;

public class TestSchemaValidator {

    @Test
    public void test() throws JsonProcessingException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VersionFlag.V4);
        ObjectMapper mapper = new ObjectMapper();
        JsonSchema schema = factory.getSchema("{\"enum\":[1, 2, 3, 4],\"enumErrorCode\":\"Not in the list\",}");
        JsonNode node = mapper.readTree("7");
        Set<ValidationMessage> errors = schema.validate(node);
        // 如果validate通过，则Set为1
        System.out.println(errors.size());
        System.out.println(errors.iterator().next().getCode());
        System.out.println(errors.iterator().next().getMessage());
        System.out.println(errors.iterator().next().getDetails());
        // 如果validate通过，则Set为空
        JsonNode node2 = mapper.readTree("3");
        Set<ValidationMessage> errors2 = schema.validate(node2);
        System.out.println(errors2.size());
        System.out.println(errors2.iterator().next().toString());

        // With automatic version detection
//        JsonNode schemaNode = mapper.readTree(
//            "{\"$schema\": \"http://json-schema.org/draft-06/schema#\", \"properties\": { \"id\": {\"type\": \"number\"}}}");
//        JsonSchema schema1 = factory.getSchema(schemaNode);
//
//        schema1.initializeValidators(); // by default all schemas are loaded lazily. You can load them eagerly via
//        // initializeValidators()
//
//        JsonNode node1 = mapper.readTree("{\"id\": \"2\"}");
//        Set<ValidationMessage> errors1 = schema1.validate(node1);
//        System.out.println(errors1.size());
//        System.out.println(errors1.iterator().next().toString());

    }


}
