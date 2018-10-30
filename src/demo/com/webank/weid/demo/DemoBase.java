/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

package com.webank.weid.demo;

import java.math.BigInteger;
import org.bcos.contract.tools.ToolConf;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author v_wbgyang
 */
public abstract class DemoBase {

    public static String privKey;
    protected static ApplicationContext context;

    static {
        context =
            new ClassPathXmlApplicationContext(
                new String[]{
                    "classpath:SpringApplicationContext-test.xml",
                    "classpath:applicationContext.xml"
                });

        ToolConf toolConf = context.getBean(ToolConf.class);
        privKey = new BigInteger(toolConf.getPrivKey(), 16).toString();
    }
}
