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
     */
    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Parameter illegal, please check your input.");
            return;
        }
        String command = args[0];
        switch (command) {
            case "--checkhealth":
                checkAmopHealth(args[1]);
                return;
            case "--checkweid":
                checkWeid(args[1]);
                return;
            default:
                logger.error("[AppCommand]: the command -> {} is not supported .", command);
                return;
        }
    }

    /**
     * check if the weid exists on blockchain.
     *
     * @param weid the weid to check
     */
    private static void checkWeid(String weid) {

        WeIdService weidService = new WeIdServiceImpl();
        ResponseData<Boolean> resp = weidService.isWeIdExist(weid);

        if (resp.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
            logger.info("[checkWeid] weid --> {} exists on blockchain.", weid);
            System.out.println("[checkWeid] weid --> " + weid + "exists on blockchain.");
            return;
        }
        logger.error("[checkWeid] weid --> {} does not exist on blockchain. response is {}",
            weid,
            resp);
        System.out.println("[checkWeid] weid --> " + weid + " does not exist on blockchain.");
    }

    /**
     * check if the amop is health.
     *
     * @param toOrgId the orgid to test amop connection
     */
    private static void checkAmopHealth(String toOrgId) {

        AmopServiceImpl amopService = new AmopServiceImpl();

        CheckAmopMsgHealthArgs checkAmopMsgHealthArgs = new CheckAmopMsgHealthArgs();
        checkAmopMsgHealthArgs.setMessage("hello");

        ResponseData<AmopNotifyMsgResult> resp = amopService
            .checkDirectRouteMsgHealth(toOrgId, checkAmopMsgHealthArgs);

        if (resp.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
            logger.info("[checkAmopHealth] toOrgId --> {} check success.", toOrgId);
            System.out.println("[checkAmopHealth] toOrgId -->" + toOrgId + " check success.");
            return;
        }
        logger.error("[checkAmopHealth] toOrgId --> {} check failed, response is {}",
            toOrgId,
            resp);
        System.out.println(
            "[checkAmopHealth] toOrgId -->" + toOrgId + " check failed. please check log.");
    }
}
