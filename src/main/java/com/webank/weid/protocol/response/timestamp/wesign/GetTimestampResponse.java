/*
 *       Copyright© (2019) WeBank Co., Ltd.
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

package com.webank.weid.protocol.response.timestamp.wesign;

import lombok.Data;

/**
 * Get timestamp response.
 *
 * @author chaoxinhu
 **/
@Data
public class GetTimestampResponse {

    private int code;
    private String msg;
    private String transactionTime;
    private String bizSeqNo;
    private Result result;

    @Data
    public class Result {

        private String bizSeqNo;
        private String transactionTime;
        private TimestampData data;

        @Data
        public class TimestampData {

            private String b64TimeStamp;
        }
    }

}
