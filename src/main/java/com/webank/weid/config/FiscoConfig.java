package com.webank.weid.config;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/**
 * FISCO-BCOS Config that loaded by java process, not Spring applicationContext anymore.
 *
 * @author chaoxinhu
 * @since 2019.6
 */
@Data
@PropertySource(value = "classpath:fisco.properties")
public class FiscoConfig {

    private static final Logger logger = LoggerFactory.getLogger(FiscoConfig.class);

    // Note that all keys are appended with a colon ":" to support regex auto-loading

    @Value("${bcos.version:}")
    private String version;

    @Value("${orgid:}")
    private String orgId;

    @Value("${nodes:}")
    private String nodes;

    @Value("${weId.contractaddress:}")
    private String weIdAddress;

    @Value("${cpt.contractaddress:}")
    private String cptAddress;

    @Value("${issuer.contractaddress:}")
    private String issuerAddress;

    @Value("${evidence.contractaddress:}")
    private String evidenceAddress;

    @Value("${specificissuer.contractaddress:}")
    private String specificIssuerAddress;

    @Value("${web3sdk.timeout:10000}")
    private String web3sdkTimeout;

    @Value("${web3sdk.core-pool-size:100}")
    private String web3sdkCorePoolSize;

    @Value("${web3sdk.max-pool-size:200}")
    private String web3sdkMaxPoolSize;

    @Value("${web3sdk.queue-capacity:1000}")
    private String web3sdkQueueSize;

    @Value("${web3sdk.keep-alive-seconds:60}")
    private String web3sdkKeepAliveSeconds;

    @Value("${v1.ca-crt-path:ca.crt}")
    private String v1CaCrtPath;

    @Value("${v1.client-crt-password:123456}")
    private String v1ClientCrtPassword;

    @Value("${v1.client-key-store-path:client.keystore}")
    private String v1ClientKeyStorePath;

    @Value("${v1.key-store-password:123456}")
    private String v1KeyStorePassword;

    @Value("${v2.ca-crt-path:./v2/ca.crt}")
    private String v2CaCrtPath;

    @Value("${v2.node-crt-path:./v2/node.crt}")
    private String v2NodeCrtPath;

    @Value("${v2.node-key-path:./v2/node.key}")
    private String v2NodeKeyPath;

    /**
     * load configuration without Spring context required.
     *
     * @return true if success, else false
     */
    public boolean load() {
        if (!FiscoConfig.class.isAnnotationPresent(PropertySource.class)) {
            logger.error("set configuration file name use @PropertySource");
            return false;
        }

        PropertySource propertySource = FiscoConfig.class.getAnnotation(PropertySource.class);
        String[] files = propertySource.value();
        if (!files[0].startsWith("classpath:")) {
            logger.error("configuration file must be in classpath");
            return false;
        }
        logger.info("load properties from file: {}", files[0]);

        // be careful of the path
        String file = "/" + files[0].replace("classpath:", "");
        try (InputStream inputStream = FiscoConfig.class.getResourceAsStream(file)) {
            Properties properties = new Properties();
            properties.load(inputStream);

            Field[] fields = FiscoConfig.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Value.class)) {
                    Value value = field.getAnnotation(Value.class);

                    //String.split can not support this regex
                    Pattern pattern = Pattern.compile("\\$\\{(\\S+):(\\S*)}");
                    Matcher matcher = pattern.matcher(value.value());
                    String k = "";
                    String v = "";
                    if (matcher.find()) {
                        if (matcher.groupCount() >= 1) {

                            k = matcher.group(1);
                        }
                        if (matcher.groupCount() >= 2) {
                            v = matcher.group(2);
                        }
                    }

                    if (properties.containsKey(k)) {
                        v = properties.getProperty(k);
                    }
                    field.setAccessible(true);
                    Object obj = field.getType().getConstructor(String.class).newInstance(v);
                    field.set(this, obj);
                }
            }
        } catch (Exception e) {
            logger.error("load properties failed", e);
            return false;
        }

        logger.info("read from fisco.properties: {}", this);
        return true;
    }
}
