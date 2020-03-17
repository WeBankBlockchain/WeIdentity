/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 协议的传输接口.
 * @author v_wbgyang
 *
 */
public interface BarCodeTransportation extends JsonTransportation {
    
    /**
     * 生成不带文字的条形码并保存到指定文件中.
     *
     * @param content 条形码字符串
     * @param format 条形码编码格式
     * @param correctionLevel 容错级别
     * @param destPath 条形码图片保存文件路径
     * @return code of ErrorCode
     */
    Integer generateBarCode(
        String content, 
        BarcodeFormat format, 
        ErrorCorrectionLevel correctionLevel, 
        String destPath
    );

    /**
     * 生成不带文字的条形码并将条形码的字节输入到字节输出流中.
     *
     * @param content 条形码字符串
     * @param format 条形码编码格式
     * @param correctionLevel 容错级别
     * @param stream 字节输出流
     * @return code of ErrorCode
     */
    Integer generateBarCode(
        String content, 
        BarcodeFormat format, 
        ErrorCorrectionLevel correctionLevel, 
        OutputStream stream
    );
}
