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

package com.webank.weid.suite.api.transportation.inf;

import java.io.OutputStream;
import java.util.List;

import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;

/**
 * PDF序列化工具
 * 支持明文和加密两种方式
 * @author tonychen
 *
 */
public interface PdfTransportation {
    
	/**
	 * 指定获取秘钥的weid，在PDF加密的情况下，需要校验哪些weid有权限来获取秘钥
	 * @param verifierWeIdList 指定的weid列表
	 * @return
	 */
	PdfTransportation specify(List<String> verifierWeIdList);

    /**
     * 协议传输序列化接口.
     * 此方法会将presentation按PDF模板的样式数据序列化为PDF文件，PDF文件的内容为claim表单，
     * presentation和协议头会放到自定义属性里。
     * 
     * @param object 协议存储的实体数据对象
     * @param <T> the type of the element
     * @param property 协议类型，支持加密和非加密两种
     * @return outputstream
     */
    <T extends JsonSerializer> ResponseData<OutputStream> serialize(
        T object,
        ProtocolProperty property,
        WeIdAuthentication weIdAuthentication
    );

    
    /**
     * 协议传输序列化接口.
     * 此方法会将presentation按PDF模板的样式数据序列化为PDF文件，PDF文件的内容为claim表单，
     * presentation和协议头会放到自定义属性里。
     * 
     * @param object 协议存储的实体数据对象
     * @param <T> the type of the element
     * @param property 协议类型，支持加密和非加密两种
     * @param inputPDFTemplatePath presentation的PDF模板
     * @return OutputStream
     */
    <T extends JsonSerializer> ResponseData<OutputStream> serialize(
        T object,
        ProtocolProperty property,
        String inputPDFTemplatePath,
        WeIdAuthentication weIdAuthentication
    );

    
    /**
     * 反序列化PDF方法，输入数据为PDF流数据。
     * 此方法主要处理流程：1.解析PDF流。2.判断加密类型，如果加密，则需要通过AMOP去wallet那边获取秘钥，非加密则直接读PDF内容。
     *   3.验证PDF的存证。4.调用presentation的verify方法验证presentation。
     * @param pdfPresentationPath 指定反序列化的PDF文档路径
     * @param clazz 如果是presentation，这里就是PresentationE.class
     * @param <T> the type of the element
     * @return 返回PresentationE对象数据
     */
    <T extends JsonSerializer> ResponseData<T> deserialize(
    	OutputStream pdfTransportation,
        Class<T> clazz,
        WeIdAuthentication weIdAuthentication
    );
}
