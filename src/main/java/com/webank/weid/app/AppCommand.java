/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.CheckAmopMsgHealthArgs;
import com.webank.weid.protocol.response.AmopNotifyMsgResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.impl.AmopServiceImpl;
import com.webank.weid.service.impl.WeIdServiceImpl;

/**
 * commands for testing.
 *
 * @author tonychen 2019年6月11日
 */
public class AppCommand {

    private static final Logger logger = LoggerFactory.getLogger(AppCommand.class);

    /**
     * commands.
     *
     * @param args input
     * @return status
     */
    public static int main(String[] args) {

        if (args.length < 2) {
            System.err.println("Parameter illegal, please check your input.");
            return 1;
        }
        String command = args[0];
        switch (command) {
            case "--checkhealth":
                return checkAmopHealth(args[1]);
            case "--checkweid":
                return checkWeid(args[1]);
            default:
                logger.error("[AppCommand]: the command -> {} is not supported .", command);
                return 1;
        }
    }

    /**
     * check if the weid exists on blockchain.
     *
     * @param weid the weid to check
     * @return status
     */
    private static int checkWeid(String weid) {

        WeIdService weidService = new WeIdServiceImpl();
        ResponseData<Boolean> resp = weidService.isWeIdExist(weid);

        if (resp.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
            logger.info("[checkWeid] weid --> {} exists on blockchain.", weid);
            return 0;
        }
        logger.error("[checkWeid] weid --> {} does not exist on blockchain. response is {}",
            weid,
            resp);
        return resp.getErrorCode();
    }

    /**
     * check if the amop is health.
     *
     * @param toOrgId the orgid to test amop connection
     * @return status
     */
    private static int checkAmopHealth(String toOrgId) {

        AmopServiceImpl amopService = new AmopServiceImpl();

        CheckAmopMsgHealthArgs checkAmopMsgHealthArgs = new CheckAmopMsgHealthArgs();
        checkAmopMsgHealthArgs.setMessage("hello");

        ResponseData<AmopNotifyMsgResult> resp = amopService
            .checkDirectRouteMsgHealth(toOrgId, checkAmopMsgHealthArgs);

        if (resp.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
            logger.info("[checkAmopHealth] toOrgId --> {} check success.", toOrgId);
            return 0;
        }
        logger.error("[checkAmopHealth] toOrgId --> {} check failed, response is {}",
            toOrgId,
            resp);
        return resp.getErrorCode();
    }
}
