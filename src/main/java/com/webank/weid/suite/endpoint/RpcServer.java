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

package com.webank.weid.suite.endpoint;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioQuickServer;
import org.smartboot.socket.transport.AioSession;

import com.webank.weid.constant.EndpointServiceConstant;
import com.webank.weid.protocol.base.EndpointInfo;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.PropertyUtils;

/**
 * A Simple RPC Server based on smart socket. Fetches endpoint info from existing EndpointDataUtil,
 * and handle them via the passed-in EndpointHandler singleton.
 *
 * @author chaoxinhu 2019.8
 */
public class RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private static final Integer DEFAULT_BOSS_THREAD_NUM = 10;
    private static final Integer DEFAULT_WORKER_THREAD_NUM = 20;
    private static final Integer UUID_LENGTH = 36;
    private static Integer LISTENER_PORT =
        Integer.valueOf(PropertyUtils.getProperty("rpc.listener.port"));

    /**
     * Map structure to store requestName and its registered EndpointFunctor Impl.
     */
    private static Map<String, EndpointFunctor> implMap = new ConcurrentHashMap<>();

    /**
     * The main entrance for RPC server process.
     *
     * @param args String args
     * @throws Exception any exception
     */
    public static void main(String[] args) throws Exception {
        if (implMap.size() == 0) {
            logger.error("Initialization failed, exiting..");
            System.exit(1);
        }
        EndpointDataUtil.loadAllEndpointInfoFromProps();
        System.out.println("Trying to receive incoming traffic at Port: " + LISTENER_PORT);
        logger.info("Trying to receive incoming traffic at Port: " + LISTENER_PORT);
        ExecutorService pool = Executors.newCachedThreadPool();
        AioQuickServer<String> server = new AioQuickServer<String>(LISTENER_PORT,
            new FixedLengthProtocol(),
            new MessageProcessor<String>() {
                public void process(AioSession<String> session, String msg) {
                    pool.execute(() -> {
                        String uuid = msg.substring(msg.length() - UUID_LENGTH);
                        logger.debug("RpcServer thread: " + Thread.currentThread().getId() + Thread
                            .currentThread().getName() + ", received msg: " + msg
                            + ", extracted UUID: "
                            + uuid + ", session ID: " + session.getSessionID());
                        System.out.println(
                            "RpcServer thread: " + Thread.currentThread().getId() + Thread
                                .currentThread().getName() + ", received msg: " + msg
                                + ", extracted UUID: " + uuid + ", session ID: " + session
                                .getSessionID());
                        String bizResult = StringUtils.EMPTY;
                        try {
                            bizResult = processClientMessage(msg);
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                            e.printStackTrace();
                        }
                        String reply = bizResult + EndpointServiceConstant.EPS_SEPARATOR + uuid;
                        System.out.println("Reply: " + reply);
                        ByteBuffer byteBuffer = FixedLengthProtocol.encode(reply);
                        byte[] resp = new byte[byteBuffer.remaining()];
                        byteBuffer.get(resp, 0, resp.length);
                        synchronized (session) {
                            try {
                                session.writeBuffer().write(resp);
                                session.writeBuffer().flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                public void stateEvent(AioSession<String> session,
                    StateMachineEnum stateMachineEnum, Throwable throwable) {
                }
            });
        server.setBossThreadNum(DEFAULT_BOSS_THREAD_NUM);
        server.setWorkerThreadNum(DEFAULT_WORKER_THREAD_NUM);
        server.start();
    }

    private static String processClientMessage(String msg) {
        String[] clientMsgArray = StringUtils
            .splitByWholeSeparator(msg, EndpointServiceConstant.EPS_SEPARATOR);
        String requestName = clientMsgArray[0];
        // Check if this is built-in methods - now, only fetch is allowed
        if (requestName.equalsIgnoreCase(EndpointServiceConstant.FETCH_FUNCTION)) {
            return processFetch();
        }
        return execute(requestName, clientMsgArray[1]);
    }

    private static String processFetch() {
        // Run load() once-again to provide more tuning capability.
        EndpointDataUtil.loadAllEndpointInfoFromProps();
        List<EndpointInfo> endpointInfoList = EndpointDataUtil.getAllEndpointInfo();
        String reply = StringUtils.EMPTY;
        for (EndpointInfo endpointInfo : endpointInfoList) {
            reply +=
                DataToolUtils.serialize(endpointInfo) + EndpointServiceConstant.PARAM_SEPARATOR;
        }
        if (!StringUtils.isEmpty(reply)) {
            reply = reply
                .substring(0, reply.length() - EndpointServiceConstant.EPS_SEPARATOR.length());
        }
        return reply;
    }

    /**
     * Register an endpoint with specified impl object and host address and store in both local
     * memory and config file.
     *
     * @param requestName request name
     * @param functorImpl the implemented fuctor
     * @throws Exception save to files exception
     */
    public static void registerEndpoint(String requestName, EndpointFunctor functorImpl)
        throws Exception {
        implMap.put(requestName, functorImpl);
        EndpointInfo endpointInfo = new EndpointInfo();
        endpointInfo.setRequestName(requestName);
        endpointInfo.setDescription(functorImpl.getDescription());
        EndpointDataUtil.mergeToCentral(endpointInfo);
        EndpointDataUtil.saveEndpointsToFile();
    }

    /**
     * Remove an endpoint from local memory and config file.
     *
     * @param requestName given request name
     */
    public static void removeEndpoint(String requestName) {
        implMap.remove(requestName);
        EndpointInfo endpointInfo = new EndpointInfo();
        endpointInfo.setRequestName(requestName);
        EndpointDataUtil.removeEndpoint(endpointInfo);
    }

    /**
     * The actual execute method. Implementations must be done by caller first.
     *
     * @param requestName the request name to check in the mapping
     * @param requestBody the request body to pass in
     * @return the serialized Object
     */
    public static String execute(String requestName, String requestBody) {
        EndpointFunctor functorImpl = implMap.get(requestName);
        if (functorImpl == null) {
            return StringUtils.EMPTY;
        }
        return functorImpl.execute(requestBody);
    }
}
