/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
        try {
            loadProperties(WEIDENTITY_PROP_NAME);
            loadProperties(FISCO_PROP_NAME);
        } catch (IOException e) {
            logger.error("[PropertyUtils] Load weidentity.properties file failed.", e);
        }
    }

    /**
     * load properties from specific config file.
     *
     * @param filePath properties config file.
     */
    private static void loadProperties(String filePath) throws IOException {

        InputStream in;
        in = PropertyUtils.class.getClassLoader().getResourceAsStream(filePath);
        prop.load(in);
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
}
