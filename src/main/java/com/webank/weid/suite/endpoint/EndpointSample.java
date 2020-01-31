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

import java.util.concurrent.ConcurrentHashMap;

/**
 * A sample class showing how to register a EndpointFunctor and register a handler with it.
 * Developer can also register his/her own service methods with a similar approach: firstly
 * implement the execute(), and then implement getDescription() by writing your own descriptions.
 * Finally, register it via EndpointHandler.
 *
 * @author chaoxinhu 2019.8
 */
public class EndpointSample {

    private static class DuplicateFunctor implements EndpointFunctor {

        @Override
        public String execute(String arg) {
            return arg + arg + arg;
        }

        @Override
        public String getDescription() {
            return "A sample method to duplicate the input";
        }
    }

    private static class DataAuthorizationFunctor implements EndpointFunctor {

        // The pass-in argument is a resource ID in authToken Credential. This functor must provide
        // a search mechanism with key being the resouece ID. Here, we use ConcurrentHashMap for
        // ease. It's recommended to use a DB / config / props based method for online-update.
        ConcurrentHashMap<String, String> resourceMap = new ConcurrentHashMap<>();

        @Override
        public String execute(String resourceId) {
            if (!resourceMap.containsKey(resourceId)) {
                return "Cannot find this resource: " + resourceId;
            }
            return resourceMap.get(resourceId);
        }

        @Override
        public String getDescription() {
            return "A sample method to fetch authorized data";
        }
    }

    /**
     * This piece of code is a simple sample showing how to register and boot-up the RPC server.
     *
     * @param args input args (omitted)
     * @throws Exception interrupted exception
     */
    public static void main(String[] args) throws Exception {
        // Firstly, clear the existing endpoints
        EndpointDataUtil.clearProps();

        // Register a duplicate endpoint
        EndpointFunctor functor1 = new DuplicateFunctor();
        String requestName1 = "duplicate-input";
        RpcServer.registerEndpoint(requestName1, functor1, null);

        // Register a data-authorization fetch endpoint
        EndpointFunctor functor2 = new DataAuthorizationFunctor();

        /*
         As an issuer to authorize resource, firstly you will need to register an authorization
         token from a CPT101 credential authToken. Firstly, you will need to fill-in your exposed
         endpoint into the credential claim's serviceUrl segment in the first place， and issue
         the CPT101 authToken credential.
         Then, you need to establish the endpoint service to enable the incoming traffic to your
         service URL. Here, the registered endpoint name MUST be the PATH segment of the serviceUrl
         and the HOST:PORT segment of the serviceUrl must be registered beforehand - this must be
         strictly the same as in the REST-service side's application.properties.

         See an example:
         Suppose the serviceUrl is http://127.0.0.1:6010/fetch-data, then endpoint name here must
         be registered as "fetch-data", and "127.0.0.1:6010" must be put in REST-service.
         Therefore, the whole workflow is like:
          0) SDK side fills-in logic to handle the resourceId lookup and reply in callback functor
          1) SDK side calls registerEndpoint() to register the functor with the endpoint name as
             "fetch-data", and then puts 6010 in weidentity.properties
          2) endpoint REST side puts 127.0.0.1:6010 in application.properties
          3) Boot up both REST service and SDK's RPCServer.main()
          4) endpoint REST side fetches endpoints (including "fetch-data") from SDK
             side (127.0.0.1:6010)
          5) endpoint REST side receives an authToken credential with claimed serviceUrl as
             http://127.0.0.1:6010/fetch-data
          6) endpoint REST side will extract HOST:PORT (127.0.0.1), and search the term (i.e.
             127.0.0.1:6010) in local endpoints info. Note: if a different port than 6010 is
             used, serviceUrl must clearly specify it e.g. http://127.0.0.1:6011/fetch-data
          7) endpoint REST side will also search the requestName with key "fetch-data" in its local
             endpoints info.
          8) endpoint REST side finds the endpoint, so an RPC call is sent to 127.0.0.1:6010,
             piggybacking the resourceId.
          9) the SDK side receives the RPC call and invoke the execute() to return authorized data.

          中文版：
          假设您是Issuer想要授权别人访问您的资源，您首先需要调用createDataAuthToken()接口，创建一个CPT101
          授权凭证。在这个授权凭证的Claim项里，请讲您需要暴露的服务端点信息填入凭证的Claim项的serviceUrl内，
          然后使用Issuer的私钥创建这个凭证。
          接下来，为了让别人能够访问您的服务端点，您需要搭建您的endpoint服务侧。将端点和入口地址的详情填入。
          需要注册的端点名，必须是您希望暴露的Service URL的PATH部分；而ServiceURL的HOST:PORT部分必须要提前
          以入口主机和端口的方式注册好，且需要和在application.properties里声明的完全一致。
          以下是一个例子：
          假设，您希望让外部人员访问的Service URL为http://127.0.0.1:6010/fetch-data。根据HTTP URL的访问
          规则，您的端点必须和PATH一致，注册为“fetch-data”；然后，HOST:PORT部分必须要在REST-service的配置
          application.properties里提前注册。这样，整体业务流如下：
          0) SDK侧需要将resourceId的处理方式在functor里注册完成
          1) SDK侧调用registerEndpoint()方法注册functor，端点名为“fetch-data”，然后将开放的端口6010写入
             weidentity.properties
          2) REST服务端将127.0.0.1:6010填入application.properteis
          3) 启动REST服务端和SDK侧的RPCServer.main()
          4) REST侧后台自动读取application.properties，拉取位于127.0.0.1:6010的所有端点
          5) REST侧收到了验证授权凭证的POST请求且Claim里的ServiceUrl为http://127.0.0.1/fetch-data
          6) REST侧后台将ServiceUrl的HOST:PORT项(127.0.0.1:6010)抽取出来，然后在本地的端点信息里查询
          7) REST侧后台会同样搜索“fetch-data”这个端点是否也已被注册完成
          8) REST侧后台找到了这个端点。它会向127.0.0.1:6010（SDK侧）发送一个RPC请求，并将凭证中Claim的
             resourceId也捎带到SDK侧过去。
          9) SDK侧会收到这RPC请求，并触发0)步中注册的functor的execute()，解析resourceId并返回授权数据。
        */
        RpcServer.registerEndpoint("fetch-data", functor2, null);

        // Start the RPC server instance
        RpcServer.main(null);
    }
}
