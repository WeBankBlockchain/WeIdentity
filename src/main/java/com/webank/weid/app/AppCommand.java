

package com.webank.weid.app;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.InitWeb3jException;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.rpc.WeIdService;
import com.webank.weid.service.impl.WeIdServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

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

        Integer result = 0;
        try {
            if (args.length < 2) {
                System.err.println("Parameter illegal, please check your input.");
                System.exit(1);
            }
            String command = args[0];
            if (!StringUtils.equals(command, "--checkhealth")
                && !StringUtils.equals(command, "--checkweid")
                && !StringUtils.equals(command, "--checkversion")) {
                logger.error("[AppCommand] input command :{} is illegal.", command);
                System.err.println("Parameter illegal, please check your input command.");
                System.exit(1);
            }

            switch (command) {
                /*case "--checkhealth":
                    result = checkAmopHealth(args[1]);
                    break;*/
                case "--checkweid":
                    result = checkWeid(args[1]);
                    break;
                case "--checkversion":
                    result = checkVersion();
                    break;
                default:
                    logger.error("[AppCommand]: the command -> {} is not supported .", command);
            }
        } catch (Exception e) {
            logger.error("[AppCommand] execute command with exception.", e);
            System.exit(1);
        }
        System.exit(result);
    }

    private static int checkVersion() {
        try {
            System.setOut(new PrintStream("./sdk.out"));
            String version = com.webank.weid.blockchain.util.DataToolUtils.getVersion();
            System.err.println("block chain nodes connected successfully. ");
            System.err.println("the FISCO-BCOS version is: " + version);
            int blockNumer = com.webank.weid.blockchain.util.DataToolUtils.getBlockNumber();
            System.err.println("the current blockNumer is: " + blockNumer);
        } catch (InitWeb3jException e) {
            System.err.println("ERROR: initWeb3j error:" + e.getMessage());
            logger.error("[checkVersion] checkVersion with exception.", e);
        } catch (Exception e) {
            System.err.println("ERROR: unknow error:" + e.getMessage());
            logger.error("[checkVersion] checkVersion with exception.", e);
        }
        return 0;
    }

    /**
     * check if the weid exists on blockchain.
     *
     * @param weid the weid to check
     * @return ErrorCode
     */
    private static Integer checkWeid(String weid) {

        WeIdService weidService = new WeIdServiceImpl();
        ResponseData<Boolean> resp = weidService.isWeIdExist(weid);

        if (resp.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
            logger.info("[checkWeid] weid --> {} exists on blockchain.", weid);
            System.out.println("[checkWeid] weid --> " + weid + "exists on blockchain.");
        } else {
            logger.error("[checkWeid] weid --> {} does not exist on blockchain. response is {}",
                weid,
                resp);
            System.out.println("[checkWeid] weid --> " + weid + " does not exist on blockchain.");
        }
        return resp.getErrorCode();
    }

    /**
     * check if the amop is health.
     *
     * @param toOrgId the orgid to test amop connection
     * @return ErrorCode
     */
    /*private static Integer checkAmopHealth(String toOrgId) {

        AmopServiceImpl amopService = new AmopServiceImpl();
        CheckAmopMsgHealthArgs checkAmopMsgHealthArgs = new CheckAmopMsgHealthArgs();
        checkAmopMsgHealthArgs.setMessage("hello");

        ResponseData<AmopNotifyMsgResult> resp = amopService
            .checkDirectRouteMsgHealth(toOrgId, checkAmopMsgHealthArgs);

        if (resp.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
            logger.info("[checkAmopHealth] toOrgId --> {} check success.", toOrgId);
            System.out.println(
                "[checkAmopHealth] send amop message to OrgId -->" + toOrgId + " with success.");
        } else {
            logger.error("[checkAmopHealth] toOrgId --> {} check failed, response is {}",
                toOrgId,
                resp);
            System.out.println(
                "[checkAmopHealth] toOrgId -->" + toOrgId + " check failed. please check log.");
        }
        return resp.getErrorCode();
    }*/
}
