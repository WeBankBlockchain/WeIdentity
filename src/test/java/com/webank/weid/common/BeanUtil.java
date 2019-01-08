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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * debug class for output object information.
 * @author v_wbgyang
 *
 */
public class BeanUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);
    
    private static final String LINE_CHARAC = System.lineSeparator();
    
    private static final String LEFT_MID_BRACKETS = "[";
    
    private static final String RIGHT_MID_BRACKETS = "]";
    
    private static final String COLON_CHARAC = ":";
    
    private static final String LEFT_BRACKETS = "(";
    
    private static final String RIGHT_BRACKETS = ")";
    
    private static final String BLANK_SPACE = " ";

    private static SimpleDateFormat getFormat() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    /**
     * printSimpe Bean.
     * @param obj  required
     */
    private static void printSimpleBean(Object obj, StringBuilder beanStr) {
        
        if (null == obj) {
            return;
        }
        
        Field[] f = obj.getClass().getDeclaredFields();
        for (int i = 0; (null != f) && (i < f.length); i++) {
            try {
                if (f[i].getModifiers() == Modifier.PRIVATE) {
                    Method m = obj.getClass().getMethod("get"
                        + f[i].getName().substring(0, 1).toUpperCase()
                        + f[i].getName().substring(1), new Class[0]);
                    if (null != m) {
                        beanStr.append(f[i].getName()).append(COLON_CHARAC).append(BLANK_SPACE)
                            .append(String.valueOf(m.invoke(obj, new Object[] {})))
                            .append(LINE_CHARAC);
                    }
                }
            } catch (NoSuchMethodException ex) {
                beanStr.append("no attribute:").append(f[i].getName())
                    .append(" to match for the method").append(LINE_CHARAC);
            } catch (IllegalAccessException e) {
                logger.error("printBean error:", e);
            } catch (IllegalArgumentException e) {
                logger.error("printBean error:", e);
            } catch (InvocationTargetException e) {
                logger.error("printBean error:", e);
            }
        }
    }

    /**
     * printSimple Collection.
     * @param c this is object of Collection
     */
    public static void printSimpleCollection(Collection<?> c) {
        if (null == c) {
            return;
        }
        StringBuilder beanStr = new StringBuilder(LINE_CHARAC);
        
        Iterator<?> it = c.iterator();
        int i = 0;
        while (it.hasNext()) {
            beanStr.append(LEFT_MID_BRACKETS).append(i++).append(RIGHT_MID_BRACKETS)
                .append(LINE_CHARAC);
            printSimpleBean(it.next(), beanStr);
        }
        logger.info(beanStr.toString());
    }

    /**
     * printSimple Map.
     * @param map this is map
     */
    public static void printSimpleMap(Map<?, ?> map) {
        if (null == map) {
            return;
        }
        StringBuilder beanStr = new StringBuilder(LINE_CHARAC);
        Iterator<?> it = map.keySet().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if ((obj instanceof Date)) {
                beanStr.append(getFormat().format((Date) obj)).append(COLON_CHARAC)
                    .append(BLANK_SPACE).append(map.get(obj)).append(LINE_CHARAC);
            } else {
                beanStr.append(obj).append(COLON_CHARAC).append(BLANK_SPACE)
                    .append(map.get(obj)).append(LINE_CHARAC);
            }
        }
        logger.info(beanStr.toString());
    }

    private static void printBean(String blank, Object obj, StringBuilder beanStr) {
        if (isSimpleValueType(obj)) {
            beanStr.append(blank).append(String.valueOf(obj)).append(LINE_CHARAC);
            return;
        }
        if ((obj instanceof Date)) {
            beanStr.append(blank).append(getFormat().format(obj)).append(LINE_CHARAC);
            return;
        }
        if ((obj instanceof String[])) {
            String[] a = (String[]) obj;
            for (int i = 0; i < a.length; i++) {
                beanStr.append(LEFT_MID_BRACKETS).append(i).append(RIGHT_MID_BRACKETS)
                    .append(COLON_CHARAC).append(BLANK_SPACE).append(a[i]).append(LINE_CHARAC);
            }
        }
        Field[] f = obj.getClass().getDeclaredFields();
        for (int i = 0; (null != f) && (i < f.length); i++) {
            try {
                if (f[i].getModifiers() == Modifier.PRIVATE) {
                    Method m = obj.getClass().getMethod("get"
                        + f[i].getName().substring(0, 1).toUpperCase()
                        + f[i].getName().substring(1), new Class[0]);

                    if (null != m) {
                        Object left = f[i].getName();
                        Object right = m.invoke(obj, new Object[] {});
                        printByType(blank, left, right, beanStr);
                    }
                }
            } catch (NoSuchMethodException ex) {
                beanStr.append(blank).append(" no attribute:").append(f[i].getName())
                    .append(" to match for the method").append(LINE_CHARAC);
            } catch (IllegalAccessException e) {
                logger.error("printBean error:", e);
            } catch (IllegalArgumentException e) {
                logger.error("printBean error:", e);
            } catch (InvocationTargetException e) {
                logger.error("printBean error:", e);
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
                beanStr.append(blank).append(LEFT_MID_BRACKETS).append(i++)
                .append(RIGHT_MID_BRACKETS).append(COLON_CHARAC)
                .append(obj).append(LINE_CHARAC);
                continue;
            }
            if (isSimpleValueType(obj)) {
                beanStr.append(blank).append(LEFT_MID_BRACKETS).append(i++)
                    .append(RIGHT_MID_BRACKETS).append(COLON_CHARAC)
                    .append(obj).append(LINE_CHARAC);
            } else if ((obj instanceof Date)) {
                beanStr.append(blank).append(LEFT_MID_BRACKETS).append(i++)
                    .append(RIGHT_MID_BRACKETS).append(COLON_CHARAC)
                    .append(getFormat().format(obj)).append(LINE_CHARAC);
            } else {
                beanStr.append(blank).append(LEFT_MID_BRACKETS).append(i++)
                    .append(RIGHT_MID_BRACKETS).append(COLON_CHARAC)
                    .append(obj.getClass().getName()).append(LINE_CHARAC);
                print(blank + "   ", obj, beanStr);
            }
        }
    }

    private static void printMap(String blank, Map<?, ?> map, StringBuilder beanStr) {
        if (null == map) {
            return;
        }
        Iterator<?> it = map.keySet().iterator();
        while (it.hasNext()) {
            Object left = it.next();
            Object right = map.get(left);
            printByType(blank, left, right, beanStr);
        }
    }

    protected static boolean isSimpleValueType(Object obj) {
        
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
            beanStr.append(blank).append(String.valueOf(leftObj)).append(COLON_CHARAC)
                .append(right).append(LINE_CHARAC);
            return;
        }
        if ((null != leftObj) && ((leftObj instanceof Date))) {
            leftObj = getFormat().format((Date) leftObj);
        }
        Class<?> clazz = right.getClass();
        if (isSimpleValueType(right)) {
            beanStr.append(blank).append(String.valueOf(leftObj)).append(COLON_CHARAC)
                .append(BLANK_SPACE).append(String.valueOf(right)).append(LINE_CHARAC);
        } else if ((right instanceof Date)) {
            beanStr.append(blank).append(String.valueOf(leftObj)).append(COLON_CHARAC)
                .append(BLANK_SPACE).append(getFormat().format(right)).append(LINE_CHARAC);
        } else if ((right instanceof Collection)) {
            beanStr.append(blank).append(String.valueOf(leftObj)).append(COLON_CHARAC)
                .append(LEFT_BRACKETS).append(clazz.getName()).append(RIGHT_BRACKETS)
                .append(LINE_CHARAC);
            printCollection(blank + "   ", (Collection<?>) right, beanStr);
        } else if ((right instanceof Map)) {
            beanStr.append(blank).append(String.valueOf(leftObj)).append(COLON_CHARAC)
                .append(LEFT_BRACKETS).append(clazz.getName()).append(RIGHT_BRACKETS)
                .append(LINE_CHARAC);
            printMap(blank + "   ", (Map<?, ?>) right, beanStr);
        } else {
            beanStr.append(blank).append(String.valueOf(leftObj)).append(COLON_CHARAC)
                .append(LEFT_BRACKETS).append(clazz.getName()).append(RIGHT_BRACKETS)
                .append(LINE_CHARAC);
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
        logger.info(beanStr.toString());
    }
}
