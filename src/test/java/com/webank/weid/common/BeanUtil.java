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

package com.webank.weid.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * debug class for output object information.
 * @author v_wbgyang
 *
 */
public class BeanUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);

    private static SimpleDateFormat getFormat() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    private static void printBean(String blank, Object obj, StringBuilder beanStr) {
        if (isSimpleValueType(obj)) {
            beanStr.append(blank)
                .append(String.valueOf(obj))
                .append(BeanConstant.LINE_CHARAC);
            return;
        }
        if ((obj instanceof Date)) {
            beanStr.append(blank)
                .append(getFormat().format(obj))
                .append(BeanConstant.LINE_CHARAC);
            return;
        }
        if ((obj instanceof String[])) {
            String[] a = (String[]) obj;
            for (int i = 0; i < a.length; i++) {
                beanStr.append(BeanConstant.LEFT_MID_BRACKETS)
                    .append(i)
                    .append(BeanConstant.RIGHT_MID_BRACKETS)
                    .append(BeanConstant.COLON_CHARAC)
                    .append(BeanConstant.BLANK_SPACE)
                    .append(a[i])
                    .append(BeanConstant.LINE_CHARAC);
            }
        }
        Field[] f = obj.getClass().getDeclaredFields();
        for (int i = 0; i < f.length; i++) {
            try {
                if (f[i].getModifiers() == Modifier.PRIVATE) {
                    Method m = obj.getClass().getMethod("get"
                        + f[i].getName().substring(0, 1).toUpperCase(Locale.getDefault())
                        + f[i].getName().substring(1), new Class[0]);
                    Object left = f[i].getName();
                    Object right = m.invoke(obj, new Object[] {});
                    printByType(blank, left, right, beanStr);
                }
            } catch (NoSuchMethodException ex) {
                beanStr.append(blank)
                    .append(" no attribute:")
                    .append(f[i].getName())
                    .append(" to match for the method")
                    .append(BeanConstant.LINE_CHARAC);
            } catch (IllegalAccessException e) {
                LOGGER.error("printBean error:", e);
            } catch (IllegalArgumentException e) {
                LOGGER.error("printBean error:", e);
            } catch (InvocationTargetException e) {
                LOGGER.error("printBean error:", e);
            }
        }
    }

    private static void printCollection(String blank, Collection<?> c, StringBuilder beanStr) {
        if (null == c) {
            return;
        }
        Iterator<?> it = c.iterator();
        int i = 0;
        while (it.hasNext()) {
            Object obj = it.next();
            if (null == obj) {
                beanStr.append(blank)
                    .append(BeanConstant.LEFT_MID_BRACKETS)
                    .append(i++)
                    .append(BeanConstant.RIGHT_MID_BRACKETS)
                    .append(BeanConstant.COLON_CHARAC)
                    .append(BeanConstant.BLANK_STR)
                    .append(BeanConstant.LINE_CHARAC);
                continue;
            }
            if (isSimpleValueType(obj)) {
                beanStr.append(blank)
                    .append(BeanConstant.LEFT_MID_BRACKETS)
                    .append(i++)
                    .append(BeanConstant.RIGHT_MID_BRACKETS)
                    .append(BeanConstant.COLON_CHARAC)
                    .append(obj)
                    .append(BeanConstant.LINE_CHARAC);
            } else if ((obj instanceof Date)) {
                beanStr.append(blank)
                    .append(BeanConstant.LEFT_MID_BRACKETS)
                    .append(i++)
                    .append(BeanConstant.RIGHT_MID_BRACKETS)
                    .append(BeanConstant.COLON_CHARAC)
                    .append(getFormat().format(obj))
                    .append(BeanConstant.LINE_CHARAC);
            } else {
                beanStr.append(blank)
                    .append(BeanConstant.LEFT_MID_BRACKETS)
                    .append(i++)
                    .append(BeanConstant.RIGHT_MID_BRACKETS)
                    .append(BeanConstant.COLON_CHARAC)
                    .append(obj.getClass().getName())
                    .append(BeanConstant.LINE_CHARAC);
                print(blank + "   ", obj, beanStr);
            }
        }
    }

    private static void printMap(String blank, Map<?, ?> map, StringBuilder beanStr) {
        if (null == map) {
            return;
        }
        for (Entry<?, ?> entry : map.entrySet()) {
            Object left = entry.getKey();
            Object right = entry.getValue();
            printByType(blank, left, right, beanStr);
        }
    }

    private static boolean isSimpleValueType(Object obj) {
        
        if (null == obj) {
            return false;
        }
        if (Date.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        return BeanUtils.isSimpleValueType(obj.getClass());
    }

    private static void printByType(
        String blank, 
        Object left, 
        Object right, 
        StringBuilder beanStr) {
        
        Object leftObj = left;
        if (null == right) {
            beanStr.append(blank)
                .append(String.valueOf(leftObj))
                .append(BeanConstant.COLON_CHARAC)
                .append(BeanConstant.BLANK_STR)
                .append(BeanConstant.LINE_CHARAC);
            return;
        }
        if ((null != leftObj) && ((leftObj instanceof Date))) {
            leftObj = getFormat().format((Date) leftObj);
        }
        Class<?> clazz = right.getClass();
        if (isSimpleValueType(right)) {
            beanStr.append(blank)
                .append(String.valueOf(leftObj))
                .append(BeanConstant.COLON_CHARAC)
                .append(BeanConstant.BLANK_SPACE)
                .append(String.valueOf(right))
                .append(BeanConstant.LINE_CHARAC);
        } else if ((right instanceof Date)) {
            beanStr.append(blank)
                .append(String.valueOf(leftObj))
                .append(BeanConstant.COLON_CHARAC)
                .append(BeanConstant.BLANK_SPACE)
                .append(getFormat().format(right))
                .append(BeanConstant.LINE_CHARAC);
        } else if ((right instanceof Collection)) {
            beanStr.append(blank)
                .append(String.valueOf(leftObj))
                .append(BeanConstant.COLON_CHARAC)
                .append(BeanConstant.LEFT_BRACKETS)
                .append(clazz.getName())
                .append(BeanConstant.RIGHT_BRACKETS)
                .append(BeanConstant.LINE_CHARAC);
            printCollection(blank + "   ", (Collection<?>) right, beanStr);
        } else if ((right instanceof Map)) {
            beanStr.append(blank)
                .append(String.valueOf(leftObj))
                .append(BeanConstant.COLON_CHARAC)
                .append(BeanConstant.LEFT_BRACKETS)
                .append(clazz.getName())
                .append(BeanConstant.RIGHT_BRACKETS)
                .append(BeanConstant.LINE_CHARAC);
            printMap(blank + "   ", (Map<?, ?>) right, beanStr);
        } else {
            beanStr.append(blank)
                .append(String.valueOf(leftObj))
                .append(BeanConstant.COLON_CHARAC)
                .append(BeanConstant.LEFT_BRACKETS)
                .append(clazz.getName())
                .append(BeanConstant.RIGHT_BRACKETS)
                .append(BeanConstant.LINE_CHARAC);
            printBean(blank + "   ", right, beanStr);
        }
    }

    private static void print(String blank, Object obj, StringBuilder beanStr) {
        if (null == obj) {
            return;
        }
        if ((obj instanceof Collection)) {
            printCollection(blank, (Collection<?>) obj, beanStr);
        } else if ((obj instanceof Map)) {
            printMap(blank, (Map<?, ?>) obj, beanStr);
        } else {
            printBean(blank, obj, beanStr);
        }
    }

    /**
     *  print object.
     * @param obj this object for print
     */
    public static void print(Object obj) {
        StringBuilder beanStr = new StringBuilder();
        if (null == obj) {
            return;
        }
        if ((obj instanceof Collection)) {
            printCollection("", (Collection<?>) obj, beanStr);
        } else if ((obj instanceof Map)) {
            printMap("", (Map<?, ?>) obj, beanStr);
        } else {
            printBean("", obj, beanStr);
        }
        LOGGER.info(beanStr.toString());
    }
}
