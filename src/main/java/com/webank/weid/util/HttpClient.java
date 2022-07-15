

package com.webank.weid.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private static int httpRequestTimeOut = 15000;

    private static int httpMaxActive = 100;

    private static int httpValidateAfterInactivity = 1000;

    private static final String httpProxyHost = "";

    private static final int httpProxyPort = 80;

    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;

    static {
        connMgr = new PoolingHttpClientConnectionManager();
        connMgr.setMaxTotal(httpMaxActive);
        connMgr.setDefaultMaxPerRoute(httpMaxActive);
        connMgr.setValidateAfterInactivity(httpValidateAfterInactivity);

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(httpRequestTimeOut);
        configBuilder.setSocketTimeout(httpRequestTimeOut);
        configBuilder.setConnectionRequestTimeout(httpRequestTimeOut);
        requestConfig = configBuilder.build();
    }

    /**
     * Create an ignoring Verify SSL Context.
     *
     * @return SSL Context object
     * @throws Exception SSL suite error, or key mgmt error
     */
    public static SSLContext createIgnoreVerifySsL() throws Exception {

        SSLContext sc = SSLContext.getInstance("TLSv1.2");

        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                X509Certificate[] paramArrayOfX509Certificate,
                String paramString) throws CertificateException {
                logger.debug("Skipping client-side check, params: ", paramString);
            }

            @Override
            public void checkServerTrusted(
                X509Certificate[] paramArrayOfX509Certificate,
                String paramString) throws CertificateException {
                logger.debug("Skipping server-side check, params: ", paramString);
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    /**
     * On-demand, create an http client.
     *
     * @param proxyHost proxy host url
     * @param proxyPort proxy port url
     * @param proxyUsername proxy username
     * @param proxyPassword proxy pwd
     * @return http client
     * @throws Exception any exception
     */
    public static CloseableHttpClient createHttpClient(String proxyHost, int proxyPort,
                                                       String proxyUsername, String proxyPassword) throws Exception {
        try {
            HttpHost proxy = null;
            if (StringUtils.isNotEmpty(proxyHost)) {
                proxy = new HttpHost(proxyHost, proxyPort, "http");
            }

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            if (StringUtils.isNotEmpty(proxyUsername) && StringUtils.isNotEmpty(proxyPassword)) {
                credsProvider.setCredentials(
                    new AuthScope(proxyHost, proxyPort),
                    new UsernamePasswordCredentials(proxyUsername, proxyPassword));
            }

            SSLContext sslcontext = createIgnoreVerifySsL();

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext))
                .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);

            CloseableHttpClient client = null;
            if (proxy == null) {
                client = HttpClients.custom().setConnectionManager(connManager).build();
            } else {
                client = HttpClients.custom().setConnectionManager(connManager)
                    .setProxy(proxy)
                    .setDefaultCredentialsProvider(credsProvider)
                    .build();
            }

            return client;
        } catch (Exception e) {
            logger.error("onWarning: create http client error" + e);
            throw e;
        }
    }

    /**
     * Send a GET request.
     *
     * @param url url
     * @param isSsL whether to use SSL or not
     * @return response in String
     * @throws Exception any exception
     */
    public static String doGet(String url, boolean isSsL) throws Exception {
        logger.debug("doGet start. url:{}", url);

        CloseableHttpClient httpClient;
        if (isSsL) {
            httpClient = HttpClients.custom().setSSLSocketFactory(createSsLConn())
                .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        } else {
            httpClient = createHttpClient(httpProxyHost, httpProxyPort, "", "");
        }
        HttpGet httpget = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpget = new HttpGet(url);

            httpResponse = httpClient.execute(httpget);
            logger.info("doGet response: {}", httpResponse);
            int statusCode = httpResponse.getStatusLine().getStatusCode();

            String result = "";
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                    logger.debug("doGet result : " + result);
                }
            } else {
                logger.warn("onWarning: doGet fail. statusCode:{}", statusCode);
            }
            EntityUtils.consume(httpResponse.getEntity());
            httpResponse.close();
            return result;
        } catch (Exception e) {
            logger.warn("onWarning: doGet fail. statusCode", e);
            throw e;
        } finally {
            httpget.releaseConnection();
            if (httpResponse != null) {
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                    httpResponse.close();
                } catch (IOException e) {
                    logger.error("onError: doGet consume fail : ", e);
                    throw e;
                }
            }
        }
    }

    /**
     * Send a POST request (in raw K-V format).
     *
     * @param url url
     * @param params object to send
     * @param isSsL whether to use SSL or not
     * @return response in String
     * @throws Exception any exception
     */
    public static String doPost(String url, Object params, boolean isSsL) throws Exception {

        String paramsString = DataToolUtils.serialize(params);
        logger.debug("doPost start. url:{},params:{}", url, paramsString);
        CloseableHttpClient httpClient;
        if (isSsL) {
            httpClient = HttpClients.custom().setSSLSocketFactory(createSsLConn())
                .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        } else {
            httpClient = createHttpClient(httpProxyHost, httpProxyPort, "", "");
        }
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);

            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(
                new StringEntity(paramsString, Charset.forName("UTF-8")));//发送的参数

            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String result = null;
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(response.getEntity(), "UTF-8");
                    logger.debug("doPost result : " + result);
                }
            }
            EntityUtils.consume(response.getEntity());
            response.close();
            return result;
        } catch (Exception e) {
            logger.error("onWarning: doPost fail", e);
            throw e;
        } finally {
            httpPost.releaseConnection();
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (IOException e) {
                    logger.error("onWarning: doPost consume fail : ", e);
                    throw e;
                }
            }
        }
    }

    private static SSLConnectionSocketFactory createSsLConn() throws Exception {
        logger.debug("createSSLConn start...");

        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, new TrustStrategy() {
                    public boolean isTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                        return true;
                    }
                }).build();

            sslsf = new SSLConnectionSocketFactory(sslContext, new MyHostnameVerifier());
            logger.debug("createSSLConn end...");
            return sslsf;
        } catch (GeneralSecurityException e) {
            logger.error("Onwarning: createSSLConn fail:", e);
            throw e;
        }
    }

    static class MyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return false;
        }
    }
}

