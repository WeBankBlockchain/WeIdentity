/*
 *       Copyright© (2019) WeBank Co., Ltd.
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

package com.webank.weid.common;

import org.slf4j.Logger;

/**
 * logging tool class.
 * @author v_wbgyang
 *
 */
public class LogUtil {

    /**
     * log record.
     * @param message log description
     * @param obj objects to be recorded
     */
    public static void info(Logger logger, String message, Object obj) {
        logger.info(
            "{}-{} result:\r\n{}",
            stackTrace().getMethodName(),
            message,
            BeanUtil.objToString(obj)
        );
    }
    
    /**
     * get stack information for log records.
     * @return StackTraceElement for currentThread.
     */
    private static StackTraceElement stackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[3];
    }
}
