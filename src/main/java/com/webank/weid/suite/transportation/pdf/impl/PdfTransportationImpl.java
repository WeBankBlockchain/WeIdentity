package com.webank.weid.suite.transportation.pdf.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.EvidenceService;
import com.webank.weid.service.impl.CptServiceImpl;
import com.webank.weid.service.impl.EvidenceServiceImpl;
import com.webank.weid.suite.api.transportation.inf.PdfTransportation;
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.encode.EncodeProcessorFactory;
import com.webank.weid.suite.entity.EncodeData;
import com.webank.weid.suite.entity.PdfVersion;
import com.webank.weid.suite.transportation.AbstractPdfTransportation;
import com.webank.weid.suite.transportation.pdf.protocol.PdfBaseData;
import com.webank.weid.util.DataToolUtils;

public class PdfTransportationImpl
    extends AbstractPdfTransportation
    implements PdfTransportation {

    private static final Logger logger =
        LoggerFactory.getLogger(PdfTransportationImpl.class);

    private static final PdfVersion version = PdfVersion.V1;

    private static CptService cptService = new CptServiceImpl();

    private static EvidenceService evidenceService = new EvidenceServiceImpl();

    /**
     * 根据Cliam和Salt把需要显示的数据写入到disclosureInfo中.
     *
     * @param disclosureInfo credentialList包含元素个数
     * @param salt 当前credential对应的盐值
     * @param claim 当前credential对应的claim
     * @param prefix 递归时用于处理字符串的前缀
     * @param propMap CPT的properties值，用于获取CPT中name字段
     */
    private static void buildDisclosureInfo(
        LinkedHashMap<String, String> disclosureInfo,
        Map<String, Object> salt,
        Map<String, Object> claim,
        StringBuilder prefix,
        Map<String, Object> propMap) throws Exception {

        for (Map.Entry<String, Object> entry : salt.entrySet()) {
            Object saltV = entry.getValue();

            //获取与盐值的key对应的claim的value
            Object claimV = claim.get(entry.getKey());
            Map<String, Object> propMapV;
            try {
                propMapV = DataToolUtils.objToMap(propMap.get(entry.getKey()));
            } catch (Exception e) {
                throw new Exception("[buildDisclosureInfo] cast exception.", e);
            }
            if (saltV instanceof Map) {
                //判断name是否存在
                if (propMapV != null) {
                    if (!propMapV.containsKey("name")
                        || propMapV.get("name").toString().equals("")) {
                        prefix.append(entry.getKey());
                    } else {
                        prefix.append(propMapV.get("name").toString());
                    }
                }
                prefix.append(".");
                Map<String, Object> certProp = null;
                try {
                    if (propMapV.containsKey("properties")) {
                        certProp = DataToolUtils.objToMap(propMapV.get("properties"));
                    }
                } catch (Exception e) {
                    throw new Exception("[buildDisclosureInfo] cast exception.", e);
                }

                buildDisclosureInfo(disclosureInfo,
                    (HashMap) saltV,
                    (HashMap) claimV,
                    prefix,
                    certProp);
                stringTools(prefix);

            } else if (saltV instanceof List) {
                Map<String, Object> items;
                Map<String, Object> itemsProp;
                try {
                    items = DataToolUtils.objToMap(propMapV.get("items"));
                    itemsProp = DataToolUtils.objToMap(items.get("properties"));
                } catch (Exception e) {
                    throw new Exception("[buildDisclosureInfo] cast exception.", e);
                }

                int i = 0;
                //判断name是否存在
                if (!propMapV.containsKey("name") || propMapV.get("name").toString()
                    .equals("")) {
                    prefix.insert(0, entry.getKey());
                } else {
                    prefix.insert(0, propMapV.get("name").toString());
                }
                prefix.append("[");
                prefix.append(i++);
                prefix.append("]");
                prefix.append(".");

                buildDisclosureInfoByList(disclosureInfo,
                    (ArrayList<Object>) saltV,
                    (ArrayList<Object>) claimV,
                    prefix,
                    itemsProp
                );

            } else {
                //盐值为0，说明是非披露字段
                if (entry.getValue().equals("0")) {
                    continue;
                }

                //判断name是否存在
                try {
                    if (!propMapV.containsKey("name") || propMapV.get("name").toString()
                        .equals("")) {
                        disclosureInfo.put(prefix + entry.getKey(), claimV.toString());
                    } else {
                        disclosureInfo
                            .put(prefix + propMapV.get("name").toString(), claimV.toString());
                    }
                } catch (Exception e) {
                    throw new Exception("[buildDisclosureInfo] get name exception.", e);
                }

            }
        }
    }

    /**
     * 对要写入disclosureInfo中字符串进行处理.
     *
     * @param prefix 字符串前缀
     */
    private static void stringTools(StringBuilder prefix) {

        boolean second = false;
        byte dot = 0;
        //如果包含多个"." ，则删除倒数第二个点后面的内容；如果只有一个"."则置为空
        for (int i = prefix.length() - 1; i > 0; i--) {
            if (prefix.charAt(i) == '.') {
                dot++;
                if (second) {
                    prefix.replace(0, prefix.length(), prefix.substring(0, i + 1));
                    break;
                }
                second = true;
            }
        }
        if (dot == 1) {
            prefix.delete(0, prefix.length());
        }
    }

    /**
     * 处理List类型的Claim.
     *
     * @param disclosureInfo credentialList包含元素个数
     * @param salt 当前credential对应的盐值
     * @param claim 当前credential对应的claim
     * @param prefix 递归时用于处理字符串的前缀
     * @param items CPT中List类型获取Properties
     */
    private static void buildDisclosureInfoByList(
        LinkedHashMap<String, String> disclosureInfo,
        List<Object> salt,
        List<Object> claim,
        StringBuilder prefix,
        Map<String, Object> items) throws Exception {

        try {
            //传入的salt就是List，需要遍历每一个salt[i]
            for (int i = 0; i < salt.size(); i++) {
                Object claimObj = claim.get(i);
                Object saltObj = salt.get(i);

                if (saltObj instanceof Map) {
                    //List里面是Map
                    buildDisclosureInfo(disclosureInfo,
                        (HashMap) saltObj,
                        (HashMap) claimObj,
                        prefix,
                        items);
                } else if (saltObj instanceof List) {
                    //List里面是List,就一直递归直到是Map
                    buildDisclosureInfoByList(disclosureInfo,
                        (ArrayList<Object>) saltObj,
                        (ArrayList<Object>) claimObj,
                        prefix,
                        items);
                }
                prefix.replace(0, prefix.length(), prefix.substring(0, prefix.length() - 3));
                prefix.append(i + 1).append("]").append(".");
            }
            stringTools(prefix);
        } catch (Exception e) {
            throw new Exception("[buildDisclosureInfoByList] exception.", e);
        }
    }

    /**
     * 获取字符串在PDF中占用长度.
     *
     * @param str 需要计算的字符串
     * @param fontSize 字体大小
     * @param font 具体字体
     * @return 字符串占用长度
     */
    private static float calcStrLength(String str, int fontSize, PDFont font) throws Exception {

        float len;
        try {
            len = fontSize * font.getStringWidth(str) / 1000;
        } catch (IOException e) {
            throw new Exception("[calcStrLength] get str length exception.", e);
        }
        return len;
    }

    /**
     * 处理单条disclosureInfo数据过长需要分行显示问题.
     *
     * @param output 需要处理的字符串
     * @param fontSize 字体大小
     * @param font 具体字体
     * @param margin PDF中单行容纳最大长度
     * @param lines 处理后存储结果的ArrayList
     */
    private static void buildLines(
        String output, int fontSize,
        PDFont font, float margin,
        ArrayList<String> lines) throws Exception {

        try {
            String str;
            int startPos = 0;
            for (int i = 0; i < output.length(); i++) {
                str = output.substring(startPos, i);
                if (calcStrLength(str, fontSize, font) > margin) {
                    lines.add(str);
                    startPos = i;
                }
            }
            str = output.substring(startPos);
            lines.add(str);
        } catch (Exception e) {
            throw new Exception("[buildLines] build lines exception.", e);
        }

    }

    /**
     * 把处理好的多行数据lines写入到PDF中.
     *
     * @param lineSize line元素个数
     * @param contents 当前PDF页面
     * @param fontSize 字体大小
     * @param font 具体字体
     * @param pos 写入位置
     * @param lines 存有多行字符串的ArrayList
     */
    private static void addMultiLine2Pdf(
        int lineSize,
        PDPageContentStream contents,
        int fontSize,
        PDFont font,
        int pos,
        ArrayList<String> lines) throws Exception {

        try {
            String text;
            for (int j = 0; j < lineSize; j++) {
                text = lines.get(j);
                contents.beginText();
                contents.setFont(font, fontSize);
                contents.newLineAtOffset(100, pos);
                contents.showText(text);
                pos = pos - 15;
                contents.endText();
            }
        } catch (Exception e) {
            throw new Exception("addMultiLine2Pdf exception", e);
        }
    }

    /**
     * 把lines写入到PDF中.
     *
     * @param doc 当前PDF
     * @param pdfBaseData PDF协议数据
     */
    private static void addAttr4Pdf(PDDocument doc, PdfBaseData pdfBaseData) {

        PDDocumentInformation pdd = doc.getDocumentInformation();
        pdd.setCustomMetadataValue("Id", pdfBaseData.getId());
        pdd.setCustomMetadataValue("OrgId", pdfBaseData.getOrgId());
        pdd.setCustomMetadataValue("Data", pdfBaseData.getData().toString());
        pdd.setCustomMetadataValue("EncodeType", String.valueOf(pdfBaseData.getEncodeType()));
        pdd.setCustomMetadataValue("Version", String.valueOf(pdfBaseData.getVersion()));
        pdd.setCustomMetadataValue("Address", String.valueOf(pdfBaseData.getAddress()));
    }

    /**
     * 将PDF中的属性信息解析到PdfBaseData数据结构中.
     *
     * @param pdd PDF属性信息
     * @param pdfBaseData PDF协议数据
     */
    private static void buildPdfDataFromPdf(PDDocumentInformation pdd, PdfBaseData pdfBaseData) {

        pdfBaseData.setId(pdd.getCustomMetadataValue("Id"));
        pdfBaseData.setOrgId(pdd.getCustomMetadataValue("OrgId"));
        pdfBaseData.setData(pdd.getCustomMetadataValue("Data"));
        pdfBaseData.setEncodeType(Integer.parseInt(pdd.getCustomMetadataValue("EncodeType")));
        pdfBaseData.setVersion(Integer.parseInt(pdd.getCustomMetadataValue("Version")));
        pdfBaseData.setAddress(pdd.getCustomMetadataValue("Address"));
    }

    /**
     * 通过默认PDF模板序列化.
     *
     * @param object 协议存储的实体数据对象
     * @param property 协议类型，支持加密和非加密两种
     * @param weIdAuthentication WeID公私钥信息
     * @return 返回PDF流数据
     */
    @Override
    public <T extends JsonSerializer> ResponseData<OutputStream> serialize(
        T object,
        ProtocolProperty property,
        WeIdAuthentication weIdAuthentication) {

        logger.info(
            "begin to execute PdfTransportationImpl serialization, property:{}.",
            property
        );

        //检查协议配置完整性
        ErrorCode errorCode = checkEncodeProperty(property);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("checkEncodeProperty fail, errorCode:{}.", errorCode);
            return new ResponseData<>(null, errorCode);
        }

        //检查presentation完整性
        errorCode = checkProtocolData(object);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("checkProtocolData fail, errorCode:{}.", errorCode);
            return new ResponseData<>(null, errorCode);
        }

        //检查WeIdAuthentication合法性
        errorCode = checkWeIdAuthentication(weIdAuthentication);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[deserialize] checkWeIdAuthentication fail, errorCode:{}.", errorCode);
            return new ResponseData<>(null, errorCode);
        }
        super.setWeIdAuthentication(weIdAuthentication);

        try {
            //建立用于存证的adress
            ResponseData<String> evidenceAdress = evidenceService
                .createEvidence(null, weIdAuthentication.getWeIdPrivateKey());

            //构建PDF协议数据
            PdfBaseData pdfBaseData = buildPdfData(property, evidenceAdress.getResult());
            logger.info("encode by {}.", property.getEncodeType().name());

            //非加密情况直接设置PDF协议中data数据
            if (property.getEncodeType() != EncodeType.CIPHER) {
                pdfBaseData.setData(object.toJson());
            } else {
                //加密情况，构建编解码数据，加密数据，设置data属性
                EncodeData encodeData =
                    new EncodeData(
                        pdfBaseData.getId(),
                        pdfBaseData.getOrgId(),
                        object.toJson(),
                        super.getVerifiers(),
                        property.getKeyExpireTime()
                    );
                String data = EncodeProcessorFactory
                    .getEncodeProcessor(property.getEncodeType())
                    .encode(encodeData);
                pdfBaseData.setData(data);
            }

            //创建包含一个空白页的PDF
            PDDocument document = new PDDocument();

            //依据presentation中credential数量来新建页面
            PresentationE presentation;
            try {
                presentation = (PresentationE) object;
            } catch (Exception e) {
                logger.error("Object cast to PresentationE error.", e);
                return new ResponseData<>(null, ErrorCode.TRANSPORTATION_BASE_ERROR);
            }

            //获取credentialPojoList
            List<CredentialPojo> credentialPojoList = presentation.getVerifiableCredential();
            int creListSize = credentialPojoList.size();

            //通过creListSize来新增PDF页面
            addPdfPage(creListSize, document);

            //遍历credentialList，把每一个cliam写入到pdf对应的page中
            addClaim2Pdf(credentialPojoList, document);

            //设置输出pdf文件的属性信息
            addAttr4Pdf(document, pdfBaseData);

            //定义OutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //把PDF写入到OutputStream中
            document.save(out);
            document.close();

            //对OutputStream进行存证
            String hashOutput = DataToolUtils.getHash(out.toString());
            ResponseData<Boolean> setEvidenceRes = evidenceService
                .setHashValue(hashOutput, evidenceAdress.getResult(),
                    weIdAuthentication.getWeIdPrivateKey());
            if (!setEvidenceRes.getResult()) {
                logger.error("SetHashValue error.");
                return new ResponseData<>(null,
                    ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
            }
            logger.info("PdfTransportationImpl serialization finished.");
            return new ResponseData<>(out, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[serialize] PdfTransportation serialization due to base error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        } catch (Exception e) {
            logger.error("[serialize] PdfTransportation serialization due to unknown error.", e);
            return new ResponseData<>(null, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }

    /**
     * 通过指定PDF模板序列化.
     *
     * @param object 协议存储的实体数据对象
     * @param property 协议类型，支持加密和非加密两种
     * @param inputPdfTemplatePath 指定的PDF模板
     * @param weIdAuthentication WeID公私钥信息
     * @return 返回PDF流数据
     */
    @Override
    public <T extends JsonSerializer> ResponseData<OutputStream> serialize(
        T object, ProtocolProperty property,
        String inputPdfTemplatePath, WeIdAuthentication weIdAuthentication) {

        logger.info(
            "begin to execute PdfTransportationImpl serialization, property:{}.",
            property
        );

        //检查协议配置完整性
        ErrorCode errorCode = checkEncodeProperty(property);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("checkEncodeProperty fail, errorCode:{}.", errorCode);
            return new ResponseData<>(null, errorCode);
        }

        //检查presentation完整性
        errorCode = checkProtocolData(object);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("checkProtocolData fail, errorCode:{}.", errorCode);
            return new ResponseData<>(null, errorCode);
        }

        //检查WeIdAuthentication合法性
        errorCode = checkWeIdAuthentication(weIdAuthentication);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[deserialize] checkWeIdAuthentication fail, errorCode:{}.", errorCode);
            return new ResponseData<>(null, errorCode);
        }
        super.setWeIdAuthentication(weIdAuthentication);

        try {
            //把PDF模板读成流数据
            PDDocument document;
            File file = new File(inputPdfTemplatePath);
            if (file.exists() && !file.isDirectory()) {
                try {
                    document = PDDocument.load(file);
                } catch (IOException e) {
                    logger.error("pdf template load error:{}.", ErrorCode.BASE_ERROR);
                    return new ResponseData<>(null,  ErrorCode.BASE_ERROR);
                }
            } else {
                logger.error("pdf template directory illegal:{}.", ErrorCode.ILLEGAL_INPUT);
                return new ResponseData<>(null,  ErrorCode.ILLEGAL_INPUT);
            }

            //build 存证adress
            ResponseData<String> evidenceAdress = evidenceService
                .createEvidence(null, weIdAuthentication.getWeIdPrivateKey());

            //构建PDF协议数据
            PdfBaseData pdfBaseData = buildPdfData(property, evidenceAdress.getResult());
            logger.info("encode by {}.", property.getEncodeType().name());

            //非加密情况直接设置PDF协议中data数据
            if (property.getEncodeType() != EncodeType.CIPHER) {
                pdfBaseData.setData(object.toJson());
            } else {
                //加密情况，构建编解码数据，加密数据，设置data属性
                EncodeData encodeData =
                    new EncodeData(
                        pdfBaseData.getId(),
                        pdfBaseData.getOrgId(),
                        object.toJson(),
                        super.getVerifiers(),
                        property.getKeyExpireTime()
                    );
                String data = EncodeProcessorFactory
                    .getEncodeProcessor(property.getEncodeType())
                    .encode(encodeData);
                pdfBaseData.setData(data);
            }

            PresentationE presentation;
            Map<String, Object> claimMap;
            //获取presentation中的cliam信息为map
            try {
                presentation = (PresentationE) object;
            } catch (Exception e) {
                logger.error("Object cast to PresentationE error.", e);
                return new ResponseData<>(null, ErrorCode.DATA_TYPE_CASE_ERROR);
            }
            claimMap = presentation.getVerifiableCredential().get(0).getClaim();

            //遍历PDFDocumnet中的key值，设置相应的value值
            PDDocumentCatalog documentCatalog = document.getDocumentCatalog();
            PDAcroForm acroForm = documentCatalog.getAcroForm();
            if (acroForm == null) {
                return null;
            }
            for (PDField field : acroForm.getFields()) {
                if (claimMap.containsKey(field.getPartialName())) {
                    field.setValue("         " + claimMap.get(field.getPartialName()).toString());
                } else {
                    logger.error("[serialize] the specify not match with presentation.");
                    return new ResponseData<>(null, ErrorCode.TRANSPORTATION_BASE_ERROR);
                }
            }

            //设置输出pdf文件的属性信息(PdfBaseData中每个字段），并关闭PDDocument。
            addAttr4Pdf(document, pdfBaseData);

            //定义OutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //把PDF写入到OutputStream中
            document.save(out);
            document.close();

            //对OutputStream进行存证
            String hashOutput = DataToolUtils.getHash(out.toString());
            ResponseData<Boolean> setEvidenceRes = evidenceService
                .setHashValue(hashOutput, evidenceAdress.getResult(),
                    weIdAuthentication.getWeIdPrivateKey());
            if (!setEvidenceRes.getResult()) {
                logger.error(
                    "[serialize] PdfTransportation serialization due to SetHashValue error.");
                return new ResponseData<>(null,
                    ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
            }

            logger.info("PdfTransportationImpl serialization finished.");
            return new ResponseData<>(out, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[serialize] PdfTransportation serialization due to base error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        } catch (Exception e) {
            logger.error("[serialize] PdfTransportation serialization due to unknown error.", e);
            return new ResponseData<>(null, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }


    /**
     * pdf transportation 反序列化.
     *
     * @param pdfTransportation 指定反序列化的PDF文档路径
     * @param clazz 如果是presentation，这里就是PresentationE.class
     * @param weIdAuthentication WeID公私钥信息
     * @param <T> the type of the element
     * @return 反序列化得到的presentation
     */
    @Override
    public <T extends JsonSerializer> ResponseData<T> deserialize(
        OutputStream pdfTransportation,
        Class<T> clazz,
        WeIdAuthentication weIdAuthentication) {

        //检查WeIdAuthentication合法性
        ErrorCode errorCode = checkWeIdAuthentication(weIdAuthentication);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[deserialize] checkWeIdAuthentication fail, errorCode:{}.", errorCode);
            return new ResponseData<>(null, errorCode);
        }
        super.setWeIdAuthentication(weIdAuthentication);

        //开始反序列化
        logger.info("begin to execute PdfTransportationImpl deserialization from InputStream.");
        try {
            //加载文件流为PDDocument
            PDDocument doc;
            try {
                doc = PDDocument.load(parse(pdfTransportation));
            } catch (Exception e) {
                logger.error("[deserialize] OutputStream illegal, errorCode:{}.",
                    ErrorCode.ILLEGAL_INPUT);
                return new ResponseData<>(null,  ErrorCode.ILLEGAL_INPUT);
            }

            PDDocumentInformation pdd = doc.getDocumentInformation();

            //将PDF中属性信息解析到PdfBaseData数据结构中
            PdfBaseData pdfBaseData = new PdfBaseData();
            buildPdfDataFromPdf(pdd, pdfBaseData);
            doc.close();

            //验证存证逻辑
            //获取address
            String address = pdfBaseData.getAddress();
            //获取OutputStream的hash值
            String hashOutputStream = DataToolUtils.getHash(pdfTransportation.toString());
            //验证存证
            ResponseData<Boolean> resVerify = evidenceService.verify(hashOutputStream, address);
            if (!resVerify.getResult()) {
                logger.info("Evidence verify fail.");
                return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
            }

            //创建编解码实体对象并对pdfBaseData中的数据进行解码
            EncodeData encodeData = new EncodeData(pdfBaseData.getId(),
                pdfBaseData.getOrgId(),
                pdfBaseData.getData().toString(),
                super.getWeIdAuthentication()
            );

            //根据编解码类型获取编解码枚举对象
            EncodeType encodeType =
                EncodeType.getObject(String.valueOf(pdfBaseData.getEncodeType()));

            if (encodeType == null) {
                return new ResponseData<>(null, ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR);
            }
            logger.info("decode by {}.", encodeType.name());

            //解码
            String presentationEStr =
                EncodeProcessorFactory
                    .getEncodeProcessor(encodeType)
                    .decode(encodeData);

            String presentationEJson = DataToolUtils.convertUtcToTimestamp(presentationEStr);
            String presentationEJsonNew = presentationEJson;
            if (DataToolUtils.isValidFromToJson(presentationEJson)) {
                presentationEJsonNew = DataToolUtils.removeTagFromToJson(presentationEJson);
            }
            T object;
            Method method = getFromJsonMethod(clazz);
            if (method == null) {
                //调用工具的反序列化
                object = (T) DataToolUtils.deserialize(presentationEJsonNew, clazz);
            } else {
                object = (T) method.invoke(null, presentationEJsonNew);
            }

            logger.info("PdfTransportationImpl deserialization finished.");
            return new ResponseData<>(object, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[deserialize] PdfTransportation deserialize due to base error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        } catch (Exception e) {
            logger.error("PdfTransportationFactory deserialization due to unknown error.", e);
            return new ResponseData<>(null, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }

    /**
     * 构建协议实体数据.
     *
     * @param property 协议配置对象
     * @param address 用于存证的合约地址
     * @return 返回协议实体对象
     */
    private PdfBaseData buildPdfData(ProtocolProperty property, String address) {

        PdfBaseData pdfBaseData = new PdfBaseData();
        pdfBaseData.setVersion(version.getCode());
        pdfBaseData.setEncodeType(property.getEncodeType().getCode());
        pdfBaseData.setId(DataToolUtils.getUuId32());
        pdfBaseData.setOrgId(fiscoConfig.getCurrentOrgId());
        pdfBaseData.setAddress(address);
        return pdfBaseData;
    }

    /**
     * 根据credentialList大小来新增PDF页面.
     *
     * @param creListSize credentialList包含元素个数
     * @param document 新增页面的PDF文档
     */
    private void addPdfPage(int creListSize, PDDocument document) {

        for (int i = 0; i < creListSize; i++) {
            PDPage page = new PDPage();
            document.addPage(page);
        }
    }


    /**
     * 把Claim数据写入到PDF文档中.
     *
     * @param credentialPojoList credentialPojo的List
     * @param document PDF文档
     */
    private void addClaim2Pdf(
        List<CredentialPojo> credentialPojoList,
        PDDocument document) throws Exception {

        int pageAddNum = 0;
        int creListSize = credentialPojoList.size();

        //中间数据存储结构. 使用LinkedHashMap以保持顺序
        LinkedHashMap<String, String>[] disclosureInfo = new LinkedHashMap[creListSize];
        for (int i = 0; i < creListSize; i++) {
            CredentialPojo cre = credentialPojoList.get(i);

            //利用cptId到链上获取CPT
            Integer cptId = cre.getCptId();
            ResponseData<Cpt> res = cptService.queryCpt(cptId);
            Cpt cpt = res.getResult();
            Map<String, Object> cptMap = cpt.getCptJsonSchema();
            Map<String, Object> propMap;
            try {
                propMap = DataToolUtils.objToMap(cptMap.get("properties"));
            } catch (Exception e) {
                throw new Exception("cast properties to propMap exception.", e);
            }

            //从credential中获取Claim和Salt. Salt用于查询披露字段
            Map<String, Object> claim = cre.getClaim();
            Map<String, Object> salt = cre.getSalt();

            //生成一个中间数据
            disclosureInfo[i] = new LinkedHashMap<>();
            StringBuilder prefix = new StringBuilder();
            buildDisclosureInfo(disclosureInfo[i], salt, claim, prefix, propMap);

            //把中间数据写入到对应PDF页面中
            if (i + pageAddNum <= document.getPages().getCount()) {
                pageAddNum = addData2Pdf(i + pageAddNum, document, disclosureInfo[i], cptMap);
            }
        }
    }

    /**
     * 把disclosureInfo中的数据写入PDF.
     *
     * @param i 当前写入page的索引
     * @param doc 待写入的PDF
     * @param disclosureInfo credentialList包含元素个数
     * @param cptJson 用于获取当前CLaim的title
     * @return 增对单条disclosureInfo信息跨行，以及Claim跨页情况，返回本函数新增的page数量
     */
    private int addData2Pdf(
        int i,
        PDDocument doc,
        LinkedHashMap<String, String> disclosureInfo,
        Map<String, Object> cptJson) throws Exception {

        try {
            int pageAddNum = 0;

            //设置单行写入的边界长度值
            float margin = 408;

            //设置字体大小
            int fontSize = 12;

            //获取需要写入PDF的索引
            PDPageContentStream contents;
            contents = new PDPageContentStream(doc, doc.getPage(i));

            //设置pdf中文字体
            PDType0Font fontChinese = PDType0Font
                .load(doc, new File("src/main/resources/fangsong.ttf"));

            //为当前页面加入标题
            if (cptJson.containsKey("title")) {
                String title = cptJson.get("title").toString();
                contents.beginText();
                contents.setFont(fontChinese, 18);
                contents.newLineAtOffset(260, 750);
                contents.showText(title);
                contents.endText();
            }

            //设置写入内容的初始位置
            int pos = 700;

            //遍历disclosureInfo写入到PDF中
            for (Map.Entry<String, String> entry : disclosureInfo.entrySet()) {
                String output = entry.getKey() + " : ";
                output = output + entry.getValue();

                //获取当前字符串长度并处理多行情况
                float outputLength = calcStrLength(output, fontSize, fontChinese);
                ArrayList<String> lines = new ArrayList<>();
                int lineSize;
                if (outputLength > margin) {
                    buildLines(output, fontSize, fontChinese, margin, lines);
                    lineSize = lines.size();

                    //输出当前行的lines
                    addMultiLine2Pdf(lineSize, contents, fontSize, fontChinese, pos, lines);
                    pos = pos - 15 * (lineSize - 1);
                    pos = pos - 30;
                } else {
                    contents.beginText();
                    contents.setFont(fontChinese, fontSize);
                    contents.newLineAtOffset(100, pos);
                    contents.showText(output);
                    contents.endText();
                    pos = pos - 30;
                }

                //单个Claim跨多页情况
                if (pos < 50) {
                    //关闭旧页面
                    contents.close();
                    PDPage page = new PDPage();
                    doc.addPage(page);
                    //用pageAddNum告诉外层已经新增了一页
                    contents = new PDPageContentStream(doc, doc.getPage(i + 1));
                    pageAddNum++;

                    //设置新页面的位置
                    pos = 700;
                }
            }
            contents.close();
            return pageAddNum;
        } catch (Exception e) {
            throw new Exception("addData2Pdf exception", e);
        }

    }

    /**
     * 将OutputStream转为InputStream.
     *
     * @param out OutputStream
     * @return InputStream
     */
    private ByteArrayInputStream parse(OutputStream out) {

        ByteArrayOutputStream baos;
        baos = (ByteArrayOutputStream) out;
        ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }


}
