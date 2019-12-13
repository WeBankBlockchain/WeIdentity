package com.webank.weid.util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.github.fge.jackson.JsonLoader;
import com.google.common.collect.Lists;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JType;
import com.webank.wedpr.selectivedisclosure.PredicateType;
import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.util.DataToolUtils;

public class JsonUtil {
    
    private static final Pattern PATTERN = Pattern.compile("^\\[[0-9]{1,}\\]$");
    private static final Pattern PATTERN_ARRAY = Pattern.compile("(?<=\\[)([0-9]{1,})(?=\\])");
    private static final Pattern PATTERN_KEY = Pattern.compile(".*?(?=\\[[0-9]{1,}\\])");
    
    private static final String KEY_CHAR = ".";
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    /**
     * 根据Map类型的JsonSchema去抽取里面有效的Key的集合(平级)
     * @param cptJsonSchema Map类型的JsonSchema
     * @return 返回有效Key的集合
     * @throws IOException
     */
    public static List<String> extractCptProperties(Map<String, Object> cptJsonSchema) throws IOException {
        return extractCptProperties(DataToolUtils.serialize(cptJsonSchema));
    }
    
    /**
     * 根据Json类型的JsonSchema去抽取里面有效的Key的集合(平级)
     * @param JsonSchema Json类型的JsonSchema
     * @return 返回有效Key的集合
     * @throws IOException
     */
    public static List<String> extractCptProperties(String cptJsonSchema) throws IOException {
        JDefinedClass dc = getDefinedClass(cptJsonSchema);
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        List<String> resultList = new ArrayList<String>();
        if (dc != null) {
            dc.fields().entrySet().forEach(entry -> buildFromDefinedClass(resultMap, entry));
            //补充元数据
            Map<String, Object> result = replenishMeta(resultMap, null);
            //將map拍平成一级json
            String value = jsonToMonolayer(DataToolUtils.serialize(result));
            //提取平级Json中key的集合
            JsonLoader.fromString(value).fieldNames().forEachRemaining(fieldName -> resultList.add(fieldName));
        }
        return resultList;
    }
    
    /*
     * 补充元数据.
     */
    private static Map<String, Object> replenishMeta(Map<String, Object> claim, CredentialPojo credential) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        if (credential == null) {
            credential = new CredentialPojo();
        }
        result.put("id", credential.getId());
        result.put("cptId", credential.getCptId());
        result.put("issuer", credential.getIssuer());
        result.put("context", credential.getContext());
        result.put("issuanceDate", credential.getIssuanceDate());
        result.put("expirationDate", credential.getExpirationDate());
        result.put("claim", claim);
        return result;
    }
    
    /*
     * 利用SchemaMapper去构建一个Class,即将Schema to  pojo
     */
    private static JDefinedClass getDefinedClass(String cptJson) throws IOException {
        JCodeModel codeModel = new JCodeModel();
        GenerationConfig config = new DefaultGenerationConfig() {
            @Override
            public boolean isGenerateBuilders() {
                return true;
            }
        };
        SchemaMapper schemaMapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
        schemaMapper.generate(codeModel, "Example", "cpt", cptJson);
        return codeModel._getClass("cpt.Example"); 
    }

    /*
     * 遍历JDefinedClass 里面的字段，该字段为有效的key
     */
    private static void buildFromDefinedClass(Map<String, Object> resultMap, Entry<String, JFieldVar> entry) {
        String key = entry.getKey();
        if (key.equals("additionalProperties")) {
            return;
        }
        resultMap.put(key, null);
        JType jtype = entry.getValue().type();
        if (jtype instanceof JDefinedClass) {
            buildByType(resultMap, key, jtype);
        } else if (jtype instanceof JClass){
            JClass jclass = (JClass)jtype;
            List<JClass> list = jclass.getTypeParameters();
            if (list.size() > 0) {
                buildByType(resultMap, key, list.get(0));
                fixArray(resultMap, key);
            }
        }
    }
    
    /*
     * 修复集合类型的长度，即默认指定长度
     */
    private static void fixArray(Map<String, Object> resultMap, String key) {
        ArrayList<Object> objList = Lists.newArrayList();
        int endIndex = Integer.parseInt(PropertyUtils.getProperty("cpt.array.length", "-1"));
        IntStream.range(0, endIndex).forEach(i -> objList.add(resultMap.get(key)));
        resultMap.put(key, objList);
    }
    
    /*
     * 通过Jtype获取，关联类的字段信息
     */
    private static void buildByType(Map<String, Object> resultMap, String key, JType jt) {
        if (jt instanceof JDefinedClass) {
            JDefinedClass dc = (JDefinedClass)jt;
            if (dc.getClassType() == ClassType.CLASS) {
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                dc.fields().entrySet().forEach(entry -> buildFromDefinedClass(map, entry));
                resultMap.put(key, map);
            }
        }
    }
    
    /**
     * 将credential转换成平级Json,重构了元数据key，并且如果数据中包含数据(集合)，如果超过指定长度则报错，如果不足则补全至指定长度
     * @param credential 凭证
     * @return 返回处理后的平级Json
     * @throws IOException
     */
    public static String credentialToMonolayer(CredentialPojo credential) throws IOException {
        Map<String, Object> result = replenishMeta(credential.getClaim(), credential);
        String jsonString = DataToolUtils.serialize(result);
        ObjectNode objectNode = (ObjectNode)JsonLoader.fromString(jsonString);
        int maxSize = Integer.parseInt(PropertyUtils.getProperty("cpt.array.length", "-1"));
        return jsonToMonolayer(objectNode, maxSize);
    }
    
    /**
     * 将ClaimPolicy转换成平级Json,如果数据中包含数据(集合)，如果超过指定长度则报错，如果不足则补全至指定长度
     * @param claimPolicy 披露策略
     * @return 返回处理后的平级Json
     * @throws IOException
     */
    public static String claimPolicyToMonolayer(ClaimPolicy claimPolicy) throws IOException {
        ObjectNode objectNode = (ObjectNode)JsonLoader.fromString(claimPolicy.getFieldsToBeDisclosed());
        int maxSize = Integer.parseInt(PropertyUtils.getProperty("cpt.array.length", "-1"));
        return jsonToMonolayer(objectNode, maxSize);
    }
    
    /**
     * 将多级Json转换成平级Json，无补全处理
     * @param json 多级Json字符串
     * @return 返回平级Json
     * @throws IOException
     */
    public static String jsonToMonolayer(String json) throws IOException {
        return jsonToMonolayer(JsonLoader.fromString(json), -1);
    }
    
    /**
     * 将多级Json转换成平级Json
     * @param jsonNode 多级的JsonNode
     * @return 返回平级Json
     */
    public static String jsonToMonolayer(JsonNode jsonNode) {
        return jsonToMonolayer(jsonNode, -1);
    }
    
    /*
     * 多级转平级处理
     */
    private static String jsonToMonolayer(JsonNode jsonNode,int maxSize) {
        JsonNode resultJson = MAPPER.createObjectNode();
        jsonNode.fields().forEachRemaining(node -> parseJsonToNode(resultJson, node, new ArrayList<String>(), maxSize));
        return resultJson.toString();
    }
    
    /*
     * 遍历处理原JsonNode里面的数据，放入新JsonNode(Object)中
     */
    private static void parseJsonToNode(JsonNode resultJson, Entry<String, JsonNode> entry, List<String> names, int maxSize) {
        names.add(entry.getKey());
        processNode(resultJson, entry.getValue(), names, maxSize);
    }
    
    /*
     * 遍历处理原JsonNode里面的数据，放入新JsonNode(Array)中
     */
    private static void parseJsonToArray(JsonNode resultJson, JsonNode value, List<String> names, int index, int maxSize) {
        names.add("[" + index + "]");
        processNode(resultJson, value, names, maxSize);
    }
    
    /*
     * 原节点处理，区分是Object、Array或是具体数据
     */
    private static void processNode(JsonNode resultJson, JsonNode value, List<String> names, int maxSize) {
        if (value.isObject() && !isBottom(value)) {
            value.fields().forEachRemaining(node -> parseJsonToNode(resultJson, node, new ArrayList<String>(names), maxSize));
        } else if (value.isArray() && !isSampleArray(value)) {
            fixLengthForArrayNode(value, maxSize);
            value.forEach(consumerWithIndex((node,index) -> parseJsonToArray(resultJson, node, new ArrayList<String>(names), index, maxSize)));
        } else {
            buildValue((ObjectNode)resultJson, buildKey(names), value, ConvertType.STRING_TO_DECIMAL);
        }
    }

    /*
     * 集合数据长度修正
     */
    private static void fixLengthForArrayNode(JsonNode value, int maxSize) {
        //判断数组长度
        if(maxSize != -1 && value.size() > maxSize) { //如果长度超过限制长度，则抛异常
            throw new RuntimeException("the array size:" + value.size() + ", maxSize:" + maxSize);
        }
        ArrayNode array = (ArrayNode)value;
        if (maxSize - value.size() > 0) {
            JsonNode jsonNode = cloneNodewithNullNode(array.get(array.size() - 1));
            IntStream.range(0, maxSize - value.size()).forEach(i -> array.add(jsonNode));   
        }
    }
    
    /*
     * 节点克隆操作
     */
    private static JsonNode cloneNodewithNullNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = MAPPER.createObjectNode();
            node.fields().forEachRemaining(entry -> cloneObjectNode(objectNode, entry.getKey(), entry.getValue()));
            return objectNode;
        } else if (node.isArray()) {
            ArrayNode arrayNode = MAPPER.createArrayNode();
            node.forEach(childNode -> arrayNode.add(cloneNodewithNullNode(childNode)));
            return arrayNode;
        } else {
            return NullNode.instance;
        }
    }
    
    /*
     * 节点克隆操作
     */
    private static void cloneObjectNode(ObjectNode resultNode, String key, JsonNode value) {
        if (value.isObject()) {
            resultNode.set(key, cloneNodewithNullNode(value));
        } else if (value.isArray()) {
            ArrayNode array = resultNode.putArray(key);
            value.forEach(childNode -> array.add(cloneNodewithNullNode(childNode)));
        } else {
            resultNode.set(key, NullNode.instance);
        }
    }
    
    private static boolean isBottom(JsonNode value) {
        for(PredicateType type : PredicateType.values()) {
            if (value.has(type.toString())) {
                return true;
            }
        }
        return false;
    }
    
    /*
     * 判断是否为纯基本数据集合
     */
    private static boolean isSampleArray(JsonNode value) {
        if (!value.isArray()) {//说明不是数组
            return  false;
        }
        return isSampleArray((ArrayNode)value);
    }
    
    /*
     * 判断是否为纯基本数据集合
     */
    private static boolean isSampleArray(ArrayNode array) {
        for (JsonNode jsonNode : array) {
            if (jsonNode.isObject()) {
                return false;
            } else if(jsonNode.isArray()){
                if (!isSampleArray((ArrayNode)jsonNode)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /*
     * 将解析出来的多级key转换成平级key
     */
    private static String buildKey(List<String> names) {
        StringBuilder buildKey = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            buildKey.append(names.get(i));
            if (i < names.size() - 1) {
                String keyString = names.get(i + 1);
                if (PATTERN.matcher(keyString).matches()) {
                    continue;
                }
                buildKey.append(KEY_CHAR);
            }
        }
        return buildKey.toString();
    }
    
    /*
     * 将节点数据放入新节点中
     */
    private static void buildValue(ObjectNode resultJson, String key, JsonNode value, ConvertType type) {
        if (value.isObject()) {
            resultJson.putPOJO(key, value);
        } else {
            buildOtherValue(resultJson, key, value, type);
        }
    }

    /*
     * 将节点数据放入新节点中，处理非数字类型数据
     */
    private static void buildOtherValue(ObjectNode resultJson, String key, JsonNode value, ConvertType type) {
        String strValue = null;
        if (!value.isNull()) {
            String nodeValue = value.asText();;
            if (type == ConvertType.DECIMAL_TO_STRING) {
                strValue = decimalToString(nodeValue);
                JsonNode vNode = toJsonNode(strValue);
                if(vNode != null && vNode.isArray()) {
                    resultJson.putPOJO(key, vNode);
                    return;
                }
            } else if (type == ConvertType.STRING_TO_DECIMAL){
                if (value.isArray()) {
                    nodeValue = value.toString();
                }
                strValue = toDecimal(nodeValue);
            }
        }
        resultJson.put(key, strValue);
    }
    
    /*
     * 将字符串转换成JsonNode
     */
    private static JsonNode toJsonNode(String jsonString) {
        try {
            return MAPPER.readTree(jsonString);
        } catch (IOException e) {
            return null;
        }
    }
    
    /*
     * 字符串转10进制数字字符串
     */
    private static String toDecimal(String value) {
        if (StringUtils.isBlank(value)) {
            return StringUtils.EMPTY;
        }
        return new BigInteger(value.getBytes(StandardCharsets.UTF_8)).toString(10);
    }
    
    /*
     * 将10进制数字字符串还原成原字符串
     */
    private static String decimalToString(String value) {
        if (StringUtils.isBlank(value)) {
            return StringUtils.EMPTY;
        }
        return new String(new BigInteger(value).toByteArray(),StandardCharsets.UTF_8);
    }
    
    /**
     * 带循环下标的循环
     * @param consumer
     * @return
     */
    public static <T> Consumer<T> consumerWithIndex(BiConsumer<T, Integer> consumer) {
        class Index {
            int i;
        }
        Index indexObj = new Index();
        return t -> {
            int index = indexObj.i++;
            consumer.accept(t, index);
        };
    }
    
    /**
     * 将平级Json转换成多级Json
     * @param json 平级Json字符串
     * @return 返回一个多级的Json字符串
     * @throws IOException
     */
    public static String monolayerToJson(String json) throws IOException {
        return monolayerToJson(JsonLoader.fromString(json));
    }
    
    /**
     * 将平级Json转换成多级Json
     * @param jsonNode 平级JsonNode对象
     * @return 返回一个多级的Json字符串
     */
    public static String monolayerToJson(JsonNode jsonNode) {
        ObjectNode resultJson = MAPPER.createObjectNode();
        jsonNode.fields().forEachRemaining(entry -> parseKeyToMap(resultJson, parseKey(entry.getKey()), entry.getValue()));
        return resultJson.toString();
    }
    
    /*
     * 将平级的key转换成多级key
     */
    private static LinkedList<String> parseKey(String keysString){
        return new LinkedList<String>(Arrays.asList(keysString.split("\\" +KEY_CHAR)));
    }
    
    /*
     * 将平级key对应的数据还原到多级JsonNode中
     */
    private static void parseKeyToMap(ObjectNode resultJson, LinkedList<String> keyList, JsonNode value){
        String key = keyList.removeFirst();
        List<Integer> indexList = getIndexList(key);
        if (indexList.size() > 0) { //说明匹配到数组
            addArray(resultJson, getReallyKey(key), indexList, value, keyList);
        } else {
            addObject(resultJson, key, value, keyList);
        }
    }
    
    /*
     * 提取集合key中真正的key
     */
    private static String getReallyKey(String arrayKey) {
        Matcher matcher = PATTERN_KEY.matcher(arrayKey);
        String reallyKey = StringUtils.EMPTY;
        if (matcher.find()) {
            reallyKey = matcher.group();
        }
        return reallyKey;
    }
    
    /*
     * 将数据还原到Object中
     */
    private static void addObject(ObjectNode resultJson, String key, JsonNode value, LinkedList<String> keyList) {
        if (keyList.size() == 0) { //说明为最后一个元素
            buildValue(resultJson, key, value, ConvertType.DECIMAL_TO_STRING);
            return;
        }
        if (!resultJson.has(key)) {
            resultJson.putObject(key);
        }
        parseKeyToMap((ObjectNode)resultJson.get(key), keyList, value);
    }
    
    /*
     * 将数据还原到Array中
     */
    private  static void addArray(ObjectNode resultJson, String reallyKey, List<Integer> indexList, JsonNode value, LinkedList<String> keyList) {
        if (!resultJson.has(reallyKey)) {
            resultJson.putArray(reallyKey);
        }
        putArrayValue((ArrayNode)resultJson.get(reallyKey), 0, indexList, value, keyList);
    }

    /*
     * 处理集合数据
     */
    private static void putArrayValue(ArrayNode jsonArray, int level, List<Integer> indexList, JsonNode value, LinkedList<String> keyList) {
        if (level == indexList.size() - 1) { //说明是最底层
            if (jsonArray.size() - 1 < indexList.get(level)) {//说明当前集合长度不够
                IntStream.range(0, indexList.get(level) + 1 - jsonArray.size()).forEach(i -> jsonArray.addObject());
            }
            if (keyList.size() == 0) { //下层直接为数据 
                jsonArray.set(indexList.get(level), new POJONode(decimalToString(value.asText())));
            } else { //说明下层有Map
                JsonNode jsonObj = jsonArray.get(indexList.get(level));
                parseKeyToMap((ObjectNode)jsonObj, keyList, value);
                jsonArray.set(indexList.get(level), jsonObj);
            }
        } else { //说明下一层还是集合
            if (jsonArray.size() - 1 < indexList.get(level)) {//说明当前集合长度不够
                IntStream.range(0, indexList.get(level) + 1 - jsonArray.size()).forEach(i -> jsonArray.addArray());
            }
            putArrayValue((ArrayNode)jsonArray.get(indexList.get(level)), level + 1, indexList, value, keyList);
        }
    }

    /*
     * 判断集合的层次，返回每一层对应的下标
     */
    private static List<Integer> getIndexList(String key) {
        Matcher matcher = PATTERN_ARRAY.matcher(key);
        List<Integer> indexList = new ArrayList<Integer>();    
        while (matcher.find()) {
            indexList.add(Integer.parseInt(matcher.group()));
        }
        return indexList;
    }
    
    private enum ConvertType {
        STRING_TO_DECIMAL, DECIMAL_TO_STRING
    }

}
