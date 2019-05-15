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

package com.webank.weid.suite.api.transportation.inf;

import com.webank.weid.protocol.response.ResponseData;
import java.util.stream.Stream;

/**
 * 协议的传输接口.
 * @author v_wbgyang
 *
 */
public interface QrCodeTransportation extends JsonTransportation {

    ResponseData<Stream<Byte>> toPng(String qrCodeString);

    ResponseData<Integer> toPng(String qrCodeString, String targetPath);

}
