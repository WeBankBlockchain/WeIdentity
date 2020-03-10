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

import com.webank.weid.suite.api.transportation.inf.BarCodeTransportation;
import com.webank.weid.suite.api.transportation.inf.JsonTransportation;
import com.webank.weid.suite.api.transportation.inf.PdfTransportation;
import com.webank.weid.suite.api.transportation.inf.QrCodeTransportation;
import com.webank.weid.suite.transportation.bar.impl.BarCodeTransportationImpl;
import com.webank.weid.suite.transportation.json.impl.JsonTransportationImpl;
import com.webank.weid.suite.transportation.pdf.impl.PdfTransportationImpl;
import com.webank.weid.suite.transportation.qr.impl.QrCodeJsonTransportationImpl;

/**
 * Created by Junqi Zhang on 2019/5/13.
 */
public class TransportationFactory {

    public static JsonTransportation newJsonTransportation() {
        return new JsonTransportationImpl();
    }

    public static QrCodeTransportation newQrCodeTransportation() {
        return new QrCodeJsonTransportationImpl();
    }

    public static PdfTransportation newPdfTransportation() {
        return new PdfTransportationImpl();
    }
    
    public static BarCodeTransportation newBarCodeTransportation() {
        return new BarCodeTransportationImpl();
    }
}
