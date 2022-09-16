

package com.webank.weid.common;

import org.slf4j.Logger;

/**
 * logging tool class.
 *
 * @author v_wbgyang
 */
public class LogUtil {

    /**
     * log record.
     *
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
     *
     * @return StackTraceElement for currentThread.
     */
    private static StackTraceElement stackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[3];
    }
}
