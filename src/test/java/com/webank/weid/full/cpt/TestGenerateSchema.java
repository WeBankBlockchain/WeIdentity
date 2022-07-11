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

package com.webank.weid.full.cpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.FieldScope;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.webank.weid.protocol.cpt.Cpt101;
import com.webank.weid.protocol.cpt.Cpt103;
import com.webank.weid.protocol.cpt.Cpt105;
import com.webank.weid.protocol.cpt.Cpt106;
import com.webank.weid.protocol.cpt.Cpt107;
import com.webank.weid.protocol.cpt.Cpt108;
import com.webank.weid.protocol.cpt.Cpt109;
import com.webank.weid.protocol.cpt.Cpt11;
import com.webank.weid.protocol.cpt.Cpt110;
import com.webank.weid.protocol.cpt.Cpt111;
import com.webank.weid.protocol.cpt.Cpt11Salt;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

public class TestGenerateSchema {


    @Test
    public void testGenerate() throws IOException {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_6, OptionPreset.PLAIN_JSON);
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        JsonNode Cpt101 = generator.generateSchema(Cpt101.class);
        JsonNode Cpt103 = generator.generateSchema(Cpt103.class);
        JsonNode Cpt105 = generator.generateSchema(Cpt105.class);
        JsonNode Cpt106 = generator.generateSchema(Cpt106.class);
        JsonNode Cpt107 = generator.generateSchema(Cpt107.class);
        JsonNode Cpt108 = generator.generateSchema(Cpt108.class);
        JsonNode Cpt109 = generator.generateSchema(Cpt109.class);
        JsonNode Cpt110 = generator.generateSchema(Cpt110.class);
        JsonNode Cpt111 = generator.generateSchema(Cpt111.class);
        JsonNode Cpt11 = generator.generateSchema(Cpt11.class);
        JsonNode Cpt11Salt = generator.generateSchema(Cpt11Salt.class);

        String path = "D:\\projects\\weid\\WeIdentity\\src\\test\\resources\\cpt_new\\";
        Files.write(Paths.get(path + "Cpt101" + ".json"), Cpt101.toPrettyString().getBytes());
        Files.write(Paths.get(path + "Cpt103" + ".json"), Cpt103.toPrettyString().getBytes());
        Files.write(Paths.get(path + "Cpt105" + ".json"), Cpt105.toPrettyString().getBytes());
        Files.write(Paths.get(path + "Cpt106" + ".json"), Cpt106.toPrettyString().getBytes());
        Files.write(Paths.get(path + "Cpt107" + ".json"), Cpt107.toPrettyString().getBytes());
        Files.write(Paths.get(path + "Cpt108" + ".json"), Cpt108.toPrettyString().getBytes());
        Files.write(Paths.get(path + "Cpt109" + ".json"), Cpt109.toPrettyString().getBytes());
        Files.write(Paths.get(path + "Cpt110" + ".json"), Cpt110.toPrettyString().getBytes());
        Files.write(Paths.get(path + "Cpt111" + ".json"), Cpt111.toPrettyString().getBytes());
        Files.write(Paths.get(path + "Cpt11" + ".json"), Cpt11.toPrettyString().getBytes());
        Files.write(Paths.get(path + "Cpt11Salt" + ".json"), Cpt11Salt.toPrettyString().getBytes());
//        System.out.println(jsonSchema.toPrettyString());
    }

}
