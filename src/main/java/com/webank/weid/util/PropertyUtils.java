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

/**
 * tools for properties.
 *
 * @author tonychen 2019年3月21日
 */
public final class PropertyUtils {

    private static Properties prop = new Properties();

    /**
     * load properties from specific config file.
     *
     * @param filePath properties config file.
     */
    public static void loadProperties(String filePath) throws IOException {

    	
        InputStream in;
        in = PropertyUtils.class.getClassLoader().getResourceAsStream(filePath);
//        in = new FileInputStream(new File(filePath));
        prop.load(in);
        in.close();

    }

    /**
     * get property value by specific key.
     *
     * @param key property key
     */
    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

}
