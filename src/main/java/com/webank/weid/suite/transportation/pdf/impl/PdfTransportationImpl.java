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

package com.webank.weid.suite.transportation.pdf.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
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
import com.webank.weid.protocol.base.HashString;
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

    //默认PDF模板单行写入的边界长度值
    private static final float margin = 408;

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
    private void buildDisclosureInfo(
        LinkedHashMap<String, String> disclosureInfo,
        Map<String, Object> salt,
        Map<String, Object> claim,
        StringBuilder prefix,
        Map<String, Object> propMap) throws WeIdBaseException {

        for (Map.Entry<String, Object> entry : salt.entrySet()) {
            Object saltV = entry.getValue();

            //获取与盐值的key对应的claim的value
            Object claimV = claim.get(entry.getKey());
            Map<String, Object> propMapV;
            try {
                propMapV = DataToolUtils.objToMap(propMap.get(entry.getKey()));
            } catch (Exception e) {
                logger.error("buildDisclosureInfo error due to objToMap propMap.", e);
                throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
            }

            //1. 如果claim为多层Map则递归解析数据
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
                    logger.error("buildDisclosureInfo error duo to objToMap propMapV.", e);
                    throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
                }

                //递归处理多层Map
                buildDisclosureInfo(
                    disclosureInfo,
                    (HashMap) saltV,
                    (HashMap) claimV,
                    prefix,
                    certProp);
                stringTools(prefix);

                //2. 如果claim为List则根据List内部结构来处理
            } else if (saltV instanceof List) {
                Map<String, Object> items;
                Map<String, Object> itemsProp;
                try {
                    items = DataToolUtils.objToMap(propMapV.get("items"));
                    itemsProp = DataToolUtils.objToMap(items.get("properties"));
                } catch (Exception e) {
                    logger.error("buildDisclosureInfo error due to objToMap items.", e);
                    throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
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

                //处理List结构的claim
                buildDisclosureInfoByList(
                    disclosureInfo,
                    (ArrayList<Object>) saltV,
                    (ArrayList<Object>) claimV,
                    prefix,
                    itemsProp
                );

                //3. 如果claim为单层Map则直接处理
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
                        disclosureInfo.put(
                            prefix + propMapV.get("name").toString(),
                            claimV.toString());
                    }
                } catch (WeIdBaseException e) {
                    logger.error("buildDisclosureInfo error due to get name.", e);
                    throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
                }
            }
        }
    }

    /**
     * 对要写入disclosureInfo中字符串进行处理.
     *
     * @param prefix 字符串前缀
     */
    private void stringTools(StringBuilder prefix) {

        boolean second = false;
        byte dotNum = 0;
        //如果包含多个"." ，则删除倒数第二个点后面的内容；如果只有一个"."则置为空
        for (int i = prefix.length() - 1; i > 0; i--) {
            if (prefix.charAt(i) == '.') {
                dotNum++;
                if (second) {
                    prefix.replace(0, prefix.length(), prefix.substring(0, i + 1));
                    break;
                }
                second = true;
            }
        }
        if (dotNum == 1) {
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
    private void buildDisclosureInfoByList(
        LinkedHashMap<String, String> disclosureInfo,
        List<Object> salt,
        List<Object> claim,
        StringBuilder prefix,
        Map<String, Object> items) throws WeIdBaseException {

        try {
            //传入的salt就是List，需要遍历每一个salt[i]
            for (int i = 0; i < salt.size(); i++) {
                Object claimObj = claim.get(i);
                Object saltObj = salt.get(i);

                if (saltObj instanceof Map) {
                    //List里面是Map
                    buildDisclosureInfo(
                        disclosureInfo,
                        (HashMap) saltObj,
                        (HashMap) claimObj,
                        prefix,
                        items);
                } else if (saltObj instanceof List) {
                    //List里面是List,就一直递归直到是Map
                    buildDisclosureInfoByList(
                        disclosureInfo,
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
            logger.error("buildDisclosureInfoByList error.", e);
            throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
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
    private float calcStrLength(String str, int fontSize, PDFont font) throws WeIdBaseException {

        float len;
        try {
            len = fontSize * font.getStringWidth(str) / 1000;
        } catch (Exception e) {
            logger.error("calcStrLength error.", e);
            throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
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
    private void buildLines(
        String output, int fontSize,
        PDFont font, float margin,
        ArrayList<String> lines) throws WeIdBaseException {

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
            logger.error("buildLines error.", e);
            throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
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
    private void addMultiLine2Pdf(
        int lineSize,
        PDPageContentStream contents,
        int fontSize,
        PDFont font,
        int pos,
        ArrayList<String> lines) throws WeIdBaseException {

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
            logger.error("addMultiLine2Pdf error.", e);
            throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
        }
    }

    /**
     * 把lines写入到PDF中.
     *
     * @param document 当前PDF
     * @param pdfBaseData PDF协议数据
     */
    private void addAttr4Pdf(PDDocument document, PdfBaseData pdfBaseData) {

        PDDocumentInformation pdd = document.getDocumentInformation();
        pdd.setCustomMetadataValue("id", pdfBaseData.getId());
        pdd.setCustomMetadataValue("orgId", pdfBaseData.getOrgId());
        pdd.setCustomMetadataValue("data", pdfBaseData.getData().toString());
        pdd.setCustomMetadataValue("encodeType", String.valueOf(pdfBaseData.getEncodeType()));
        pdd.setCustomMetadataValue("version", String.valueOf(pdfBaseData.getVersion()));
        pdd.setCustomMetadataValue("address", String.valueOf(pdfBaseData.getAddress()));
    }

    /**
     * 将PDF中的属性信息解析到PdfBaseData数据结构中.
     *
     * @param pdd PDF属性信息
     * @param pdfBaseData PDF协议数据
     */
    private void buildPdfDataFromPdf(PDDocumentInformation pdd, PdfBaseData pdfBaseData) {

        pdfBaseData.setId(pdd.getCustomMetadataValue("id"));
        pdfBaseData.setOrgId(pdd.getCustomMetadataValue("orgId"));
        pdfBaseData.setData(pdd.getCustomMetadataValue("data"));
        pdfBaseData.setEncodeType(Integer.parseInt(pdd.getCustomMetadataValue("encodeType")));
        pdfBaseData.setVersion(Integer.parseInt(pdd.getCustomMetadataValue("version")));
        pdfBaseData.setAddress(pdd.getCustomMetadataValue("address"));
    }

    /**
     * 把presentation数据写入默认PDF模板，输出PDF流.
     *
     * @param object serialize函数输入的object
     * @param pdfBaseData PDF协议数据
     * @param filePath 指定用于存证的PDF文件输出位置
     * @param <T> JsonSerializer
     * @return PDF流数据
     * @throws WeIdBaseException exception
     */
    private <T extends JsonSerializer>  OutputStream buildPdf4DefaultTpl(
        T object,
        PdfBaseData pdfBaseData,
        String filePath) throws WeIdBaseException {

        try {
            //创建包含一个空白页的PDF
            PDDocument document = new PDDocument();

            PresentationE presentation;
            CredentialPojo credentialPojo;
            List<CredentialPojo> credentialPojoList = new ArrayList<>(3);

            //获取credentialPojoList
            if (object instanceof PresentationE) {
                try {
                    presentation = (PresentationE) object;
                } catch (Exception e) {
                    logger.error(
                            "buildPdf4SpecTpl due to cast object to presentation error.", e);
                    throw new WeIdBaseException(ErrorCode.DATA_TYPE_CASE_ERROR);
                }
                credentialPojoList = presentation.getVerifiableCredential();
            } else if (object instanceof CredentialPojo) {
                try {
                    credentialPojo = (CredentialPojo) object;
                } catch (Exception e) {
                    logger.error(
                            "buildPdf4SpecTpl due to cast object to credential error.", e);
                    throw new WeIdBaseException(ErrorCode.DATA_TYPE_CASE_ERROR);
                }
                credentialPojoList.add(credentialPojo);
            } else {
                logger.error(
                        "buildPdf4SpecTpl due to object illegal error.");
                throw new WeIdBaseException(ErrorCode.DATA_TYPE_CASE_ERROR);
            }

            int creListSize = credentialPojoList.size();

            //通过creListSize来新增PDF页面
            addPdfPage(creListSize, document);

            //遍历credentialList，把每一个cliam写入到pdf对应的page中
            addClaim2Pdf(credentialPojoList, document);

            //设置输出pdf文件的属性信息
            addAttr4Pdf(document, pdfBaseData);

            //定义OutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //把PDF写入到OutputStream和文件中
            document.save(out);
            document.save(filePath);
            document.close();
            return out;
        } catch (Exception e) {
            logger.error("buildPdf4DefaultTpl error.", e);
            throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
        }
    }

    /**
     * 把presentation数据写入指定PDF模板，输出PDF流.
     *
     * @param object serialize函数输入的object
     * @param document 指定PDF模板的PDDocument
     * @param pdfBaseData  PDF协议数据
     * @param <T> JsonSerializer
     * @return PDF流数据
     * @throws WeIdBaseException exception
     */
    private  <T extends JsonSerializer> OutputStream buildPdf4SpecTpl(
        T object,
        PDDocument document,
        PdfBaseData pdfBaseData) throws WeIdBaseException {

        try {
            PresentationE presentation;
            CredentialPojo credentialPojo;
            Map<String, Object> claimMap;

            //获取presentation中的cliam信息为map
            if (object instanceof PresentationE) {
                try {
                    presentation = (PresentationE) object;
                } catch (Exception e) {
                    logger.error(
                            "buildPdf4SpecTpl due to cast object to presentation error.", e);
                    throw new WeIdBaseException(ErrorCode.DATA_TYPE_CASE_ERROR);
                }
                claimMap = presentation.getVerifiableCredential().get(0).getClaim();
            } else if (object instanceof CredentialPojo) {
                try {
                    credentialPojo = (CredentialPojo) object;
                } catch (Exception e) {
                    logger.error(
                            "buildPdf4SpecTpl due to cast object to credential error.", e);
                    throw new WeIdBaseException(ErrorCode.DATA_TYPE_CASE_ERROR);
                }
                claimMap = credentialPojo.getClaim();
            } else {
                logger.error(
                        "buildPdf4SpecTpl due to object illegal error.");
                throw new WeIdBaseException(ErrorCode.DATA_TYPE_CASE_ERROR);
            }

            InputStream is = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream("NotoSansCJKtc-Regular.ttf");
            TTFParser parser = new TTFParser();
            TrueTypeFont font = parser.parse(is);
            PDType0Font fontChinese = PDType0Font.load(document, font, true);

            PDDocumentCatalog documentCatalog = document.getDocumentCatalog();
            PDAcroForm acroForm = documentCatalog.getAcroForm();

            if (acroForm == null) {
                logger.error(
                        "buildPdf4SpecTpl due to acroForm is null error.");
                throw new WeIdBaseException(ErrorCode.TRANSPORTATION_BASE_ERROR);
            }

            PDResources resources = new PDResources();
            resources.add(fontChinese);

            resources.put(COSName.getPDFName("FCH"), fontChinese);
            String defaultAppearanceString = "/FCH 0 Tf 0 g";
            acroForm.setDefaultAppearance(defaultAppearanceString);
            acroForm.setDefaultResources(resources);
            acroForm.setNeedAppearances(false);

            //遍历PDFDocumnet中的key值，设置相应的value值
            for (PDField field : acroForm.getFields()) {
                if (claimMap.containsKey(field.getPartialName())) {
                    String text = claimMap.get(field.getPartialName()).toString();
                    if (fontChinese.willBeSubset()) {
                        int offset = 0;
                        while (offset < text.length()) {
                            int codePoint = text.codePointAt(offset);
                            fontChinese.addToSubset(codePoint);
                            offset += Character.charCount(codePoint);
                        }
                    }
                    field.setValue(text);
                } else {
                    logger.error(
                            "buildPdf4SpecTpl due to pdf template not match with claim error.");
                    throw new WeIdBaseException(ErrorCode.TRANSPORTATION_BASE_ERROR);
                }
            }

            //设置输出pdf文件的属性信息(PdfBaseData中每个字段），并关闭PDDocument
            addAttr4Pdf(document, pdfBaseData);

            //定义OutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //设置嵌入字体子集
            fontChinese.subset();
            document.registerTrueTypeFontForClosing(font);

            document.save(out);

            //把PDF写入到OutputStream和文件中
            String filePath = "tmp.pdf";
            document.save(filePath);
            document.close();
            return out;
        } catch (Exception e) {
            logger.error("buildPdf4SpecTpl error.", e);
            throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
        }
    }

    /**
     * 把PDF流数据读出为PDDcoument数据，来获取PDF的属性信息并生成文件来验证存证.
     *
     * @param pdfTransportation  PDF流数据
     * @param pdfBaseData 存PDF属性信息的PdfBaseData
     * @param filePath 输出文件的位置
     * @throws WeIdBaseException exception
     */
    private void resolvePdfStream(
        OutputStream pdfTransportation,
        PdfBaseData pdfBaseData,
        String filePath) throws WeIdBaseException {

        try {
            //加载文件流为PDDocument
            PDDocument document;

            document = PDDocument.load(parse(pdfTransportation));

            //将PDF中属性信息解析到PdfBaseData数据结构中
            PDDocumentInformation pdd = document.getDocumentInformation();
            buildPdfDataFromPdf(pdd, pdfBaseData);

            //把doc转为pdf文件
            document.save(filePath);
            document.close();
        } catch (Exception e) {
            logger.error("resolvePdfStream error.", e);
            throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
        }
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

        ResponseData<OutputStream> errorCode1 = checkPara(object, property, weIdAuthentication);
        if (errorCode1 != null) {
            return errorCode1;
        }

        try {
            //建立用于存证的adress
            ResponseData<String> evidenceAddress = evidenceService
                .createEvidence(null, weIdAuthentication.getWeIdPrivateKey());

            //构建PDF协议数据
            PdfBaseData pdfBaseData = setPdfBaseData(object, property, evidenceAddress);

            //处理PDF显示信息和属性信息
            String filePath = "tmp.pdf";
            OutputStream ret = buildPdf4DefaultTpl(object, pdfBaseData, filePath);

            //对文件的SHA-256哈希值进行存证
            ResponseData<Boolean> setEvidenceRes = evidenceService
                .setHashValue(getChecksum(filePath), evidenceAddress.getResult(),
                    weIdAuthentication.getWeIdPrivateKey());
            if (!setEvidenceRes.getResult()) {
                logger.error("SetHashValue error.");
                return new ResponseData<>(null,
                    ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
            }

            logger.info("PdfTransportationImpl serialization finished.");
            return new ResponseData<>(ret, ErrorCode.SUCCESS);
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
     * @param  weIdAuthentication WeID公私钥信息
     * @param inputPdfTemplatePath 指定的PDF模板位置
     * @return 返回PDF流数据
     */
    @Override
    public <T extends JsonSerializer> ResponseData<OutputStream> serialize(
        T object,
        ProtocolProperty property,
        WeIdAuthentication weIdAuthentication,
        String inputPdfTemplatePath) {

        logger.info(
            "begin to execute PdfTransportationImpl serialization, property:{}.",
            property
        );

        ResponseData<OutputStream> errorCode1 = checkPara(object, property, weIdAuthentication);
        if (errorCode1 != null) {
            return errorCode1;
        }

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

            //build 存证address
            ResponseData<String> evidenceAddress = evidenceService
                .createEvidence(null, weIdAuthentication.getWeIdPrivateKey());

            //构建PDF协议数据
            PdfBaseData pdfBaseData = setPdfBaseData(object, property, evidenceAddress);

            //处理PDF显示信息和属性信息
            String filePath = "tmp.pdf";
            OutputStream ret = buildPdf4SpecTpl(object, document, pdfBaseData);

            //对文件的SHA-256哈希值进行存证
            ResponseData<Boolean> setEvidenceRes = evidenceService
                .setHashValue(getChecksum(filePath), evidenceAddress.getResult(),
                    weIdAuthentication.getWeIdPrivateKey());
            if (!setEvidenceRes.getResult()) {
                logger.error("SetHashValue error.");
                return new ResponseData<>(null,
                    ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
            }

            logger.info("PdfTransportationImpl serialization finished.");
            return new ResponseData<>(ret, ErrorCode.SUCCESS);
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
            PdfBaseData pdfBaseData = new PdfBaseData();
            String filePath = "tmp.pdf";
            resolvePdfStream(pdfTransportation, pdfBaseData, filePath);

            //验证存证逻辑
            HashString checkSum = new HashString(getChecksum(filePath));
            String address = pdfBaseData.getAddress();
            ResponseData<Boolean> resVerify = evidenceService
                .verify(checkSum, address);
            if (!resVerify.getResult()) {
                logger.info("Evidence verify fail.");
                return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
            }

            //创建编解码实体对象并对pdfBaseData中的数据进行解码
            EncodeData encodeData = new EncodeData(
                pdfBaseData.getId(),
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
        PDDocument document) throws WeIdBaseException {

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
                logger.error("addClaim2Pdf error due to objToMap cptMap.", e);
                throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
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
     * @param document 待写入的PDF
     * @param disclosureInfo credentialList包含元素个数
     * @param cptJson 用于获取当前CLaim的title
     * @return 增对单条disclosureInfo信息跨行，以及Claim跨页情况，返回本函数新增的page数量
     */
    private int addData2Pdf(
        int i,
        PDDocument document,
        LinkedHashMap<String, String> disclosureInfo,
        Map<String, Object> cptJson) throws WeIdBaseException {

        try {
            int pageAddNum = 0;

            //设置字体大小
            int fontSize = 12;

            //获取需要写入PDF的索引
            PDPageContentStream contents;
            contents = new PDPageContentStream(document, document.getPage(i));

            //设置pdf中文字体
            InputStream is = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream("NotoSansCJKtc-Regular.ttf");
            logger.error("InputStream:" + is.available());
            PDType0Font fontChinese = PDType0Font.load(document, is);

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
                    document.addPage(page);
                    //用pageAddNum告诉外层已经新增了一页
                    contents = new PDPageContentStream(document, document.getPage(i + 1));
                    pageAddNum++;

                    //设置新页面的位置
                    pos = 700;
                }
            }
            contents.close();
            return pageAddNum;
        } catch (Exception e) {
            logger.error("addData2Pdf error.", e);
            throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR);
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
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private byte[] createChecksum(String filePath) throws Exception {

        InputStream fis = new FileInputStream(filePath);
        byte[] buffer = new byte[1024];
        MessageDigest checksum = MessageDigest.getInstance("SHA-256");
        int num = 0;

        do {
            num = fis.read(buffer);
            if (num > 0) {
                checksum.update(buffer, 0, num);
            }
        } while (num != -1);

        fis.close();
        return checksum.digest();
    }

    /**
     * 计算指定路径文件的哈希值.
     *
     * @param filePath 输入文件路径
     * @return 返回文件的SHA-256哈希值
     * @throws Exception IO异常
     */
    private String getChecksum(String filePath) throws Exception {

        byte[] bytes = createChecksum(filePath);
        StringBuilder ret = new StringBuilder();
        //注意生成的16进制字符串字母大小写问题,这里使用默认小写
        for (byte b : bytes) {
            ret.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return "0x" + ret.toString();
    }

    /**
     * 检查serialize函数参数.
     *
     * @param object 协议存储的实体数据对象
     * @param property 加密属性信息
     * @param weIdAuthentication WeID公私钥信息
     * @param <T> JsonSerializer
     * @return 如果出错返回错误码，否则返回空
     */
    private <T extends JsonSerializer> ResponseData<OutputStream> checkPara(
        T object,
        ProtocolProperty property,
        WeIdAuthentication weIdAuthentication) {

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
        return null;
    }

    /**
     * 设置PDF协议数据实体.
     *
     * @param object 协议存储的实体数据对象
     * @param property 加密属性信息
     * @param evidenceAddress 建立用于存证address的返回值
     * @param <T> JsonSerializer
     * @return 返回PDF协议数据实体对象
     */
    private <T extends JsonSerializer> PdfBaseData setPdfBaseData(
        T object,
        ProtocolProperty property,
        ResponseData<String> evidenceAddress) {

        //构建PDF协议数据
        PdfBaseData pdfBaseData = buildPdfData(property, evidenceAddress.getResult());
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
                    super.getVerifiers()
                );
            String data = EncodeProcessorFactory
                .getEncodeProcessor(property.getEncodeType())
                .encode(encodeData);
            pdfBaseData.setData(data);
        }
        return pdfBaseData;
    }
}
