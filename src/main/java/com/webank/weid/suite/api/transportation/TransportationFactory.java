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

package com.webank.weid.suite.api.transportation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.api.transportation.inf.JsonTransportation;
import com.webank.weid.suite.api.transportation.inf.PdfTransportation;
import com.webank.weid.suite.api.transportation.inf.QrCodeTransportation;
import com.webank.weid.suite.api.transportation.inf.Transportation;
import com.webank.weid.suite.api.transportation.params.TransportationType;
import com.webank.weid.suite.transportation.json.impl.JsonTransportationImpl;
import com.webank.weid.suite.transportation.pdf.impl.PdfTransportationImpl;
import com.webank.weid.suite.transportation.qr.impl.QrCodeTransportationImpl;

/**
 * Created by Junqi Zhang on 2019/5/13.
 */
public class TransportationFactory {

    private static final Logger logger = LoggerFactory.getLogger(TransportationFactory.class);

    public static JsonTransportation newJsonTransportation() {
        return new JsonTransportationImpl();
    }

    public static QrCodeTransportation newQrCodeTransportation() {
        return new QrCodeTransportationImpl();
    }

    public static PdfTransportation newPdfTransportation() {
        return new PdfTransportationImpl();
    }

    /**
     * 根据封装类型实例化对应的实例对象, 此方法目前支持JSON, BAR_CODE, QR_CODE.
     * 如果使用PDF类型, 请使用 newPdfTransportation方法.
     * @param transportationType 封装类型枚举
     * @return 返回具体处理类型
     */
    public static Transportation build(TransportationType transportationType) {
        switch (transportationType) {
            case JSON:
                return new JsonTransportationImpl();
            case QR_CODE:
                return new QrCodeTransportationImpl();
            default:
                logger.error("the type = {} unsupported.", transportationType.name());
                throw new WeIdBaseException(ErrorCode.THIS_IS_UNSUPPORTED);
        }
    }
}
