

package com.webank.weid.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * tools for properties.
 *
 * @author tonychen 2019年3月21日
 */
public final class PropertyUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);
    private static final String WEIDENTITY_PROP_NAME = "weidentity.properties";
    private static final String FISCO_PROP_NAME = "fisco.properties";
    private static Properties prop = new Properties();

    static {
        load();
    }

    /**
     * load properties from specific config file.
     *
     * @param filePath properties config file.
     */
    private static void loadProperties(String filePath) throws IOException {

        InputStream in;
        in = PropertyUtils.class.getClassLoader().getResourceAsStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        prop.load(br);
        br.close();
        in.close();
    }

    /**
     * get property value by specific key.
     *
     * @param key property key
     * @return value of the key
     */
    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    /**
     * get property value by specific key.
     *
     * @param key property keys
     * @param defaultValue default value
     * @return value of the key
     */
    public static String getProperty(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }
    
    /**
     * get the all key from Properties.
     * 
     * @return value of the key Set
     */
    public static Set<Object> getAllPropertyKey() {
        return prop.keySet();
    }
    
    /**
     * load the properties.
     */
    private static void load() {
        
        try {
            loadProperties(WEIDENTITY_PROP_NAME);
            /*if(getProperty("deploy.style").equals("blockchain")){
                loadProperties(FISCO_PROP_NAME);
            }*/
            loadProperties(FISCO_PROP_NAME);
        } catch (IOException e) {
            logger.error("[PropertyUtils] Load weidentity.properties file failed.", e);
        }
    }
    
    /**
     * reload the properties.
     * 
     */
    public static void reload() {
        load();
    }
}
