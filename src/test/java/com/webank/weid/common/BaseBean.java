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
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * debug class for output object information.
 * @author v_wbgyang
 *
 */
public class BaseBean {

    public static SimpleDateFormat getFormat() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    /**
     * printSimpe Bean.
     * @param obj  required
     */
    public static void printSimpleBean(Object obj) {
        Field[] f = obj.getClass().getDeclaredFields();
        for (int i = 0; (f != null) && (i < f.length); i++) {
            try {
                if (f[i].getModifiers() == 2) {
                    Method m = obj.getClass().getMethod("get"
                        + f[i].getName().substring(0, 1).toUpperCase()
                        + f[i].getName().substring(1), new Class[0]);
                    if (m != null) {
                        System.out.println(f[i].getName() + ": " + m.invoke(obj, new Object[] {}));
                    }
                }
            } catch (NoSuchMethodException ex) {
                System.out.println("no attribute:" + f[i].getName() + " to match for the method");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * printSimple Collection.
     * @param c this is object of Collection
     */
    public static void printSimpleCollection(Collection<?> c) {
        if (c == null) {
            return;
        }
        Iterator<?> it = c.iterator();
        int i = 0;
        while (it.hasNext()) {
            System.out.println("[" + i++ + "]");
            printSimpleBean(it.next());
        }
    }

    /**
     * printSimple Map.
     * @param map this is map
     */
    public static void printSimpleMap(Map<?, ?> map) {
        if (map == null) {
            return;
        }
        Iterator<?> it = map.keySet().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if ((obj instanceof Date)) {
                System.out.println(getFormat().format((Date) obj) + ": " + map.get(obj));
            } else {
                System.out.println(obj + ": " + map.get(obj));
            }
        }
    }

    private static void printBean(String blank, Object obj) {
        if (isPrimitive(obj)) {
            System.out.println(blank + obj);
            return;
        }
        if ((obj instanceof Date)) {
            System.out.println(blank + getFormat().format(obj));
            return;
        }
        if ((obj instanceof String[])) {
            String[] a = (String[]) obj;
            for (int i = 0; i < a.length; i++) {
                System.out.println("[" + i + "]" + ": " + a[i]);
            }
        }
        Field[] f = obj.getClass().getDeclaredFields();
        for (int i = 0; (f != null) && (i < f.length); i++) {
            try {
                if (f[i].getModifiers() == 2) {
                    Method m = obj.getClass().getMethod("get"
                        + f[i].getName().substring(0, 1).toUpperCase()
                        + f[i].getName().substring(1), new Class[0]);

                    if (m != null) {
                        Object left = f[i].getName();
                        Object right = m.invoke(obj, new Object[] {});
                        printByType(blank, left, right);
                    }
                }
            } catch (NoSuchMethodException ex) {
                System.out.println(
                    blank + " no attribute:" + f[i].getName() + " to match for the method");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void printCollection(String blank, Collection<?> c) {
        if (c == null) {
            return;
        }
        Iterator<?> it = c.iterator();
        int i = 0;
        while (it.hasNext()) {
            Object obj = it.next();
            if (isPrimitive(obj)) {
                System.out.println(blank + "[" + i++ + "]: " + obj);
            } else if ((obj instanceof Date)) {
                System.out.println(blank + "[" + i++ + "]: " + getFormat().format(obj));
            } else {
                System.out.println(blank + "[" + i++ + "]: " + obj.getClass().getName());
                print(blank + "   ", obj);
            }
        }
    }

    private static void printMap(String blank, Map<?, ?> map) {
        if (map == null) {
            return;
        }
        Iterator<?> it = map.keySet().iterator();
        while (it.hasNext()) {
            Object left = it.next();
            Object right = map.get(left);
            printByType(blank, left, right);
        }
    }

    protected static boolean isPrimitive(Object obj) {
        if (obj == null) {
            return false;
        }
        if (((obj instanceof Integer))
            || ((obj instanceof Boolean))
            || ((obj instanceof Character))
            || ((obj instanceof Byte))
            || ((obj instanceof Short))
            || ((obj instanceof Long))
            || ((obj instanceof Float))
            || ((obj instanceof Double))
            || ((obj instanceof Void))
            || ((obj instanceof String))
            || ((obj instanceof BigDecimal))
            || ((obj instanceof BigInteger))
            || (obj == null)) {
            return true;
        }
        return false;
    }

    private static void printByType(String blank, Object left, Object right) {
        if (right == null) {
            System.out.println(blank + left + ": null");
            return;
        }
        if ((left != null) && ((left instanceof Date))) {
            left = getFormat().format((Date) left);
        }
        Class<?> clazz = right.getClass();
        if (isPrimitive(right)) {
            System.out.println(blank + left + ": " + right);
        } else if ((right instanceof Date)) {
            System.out.println(blank + left + ": " + getFormat().format(right));
        } else if ((right instanceof Collection)) {
            System.out.println(blank + left + ":(" + clazz.getName() + ")");
            printCollection(blank + "   ", (Collection<?>) right);
        } else if ((right instanceof Map)) {
            System.out.println(blank + left + ":(" + clazz.getName() + ")");
            printMap(blank + "   ", (Map<?, ?>) right);
        } else {
            System.out.println(blank + left + ":(" + clazz.getName() + ")");
            printBean(blank + "   ", right);
        }
    }

    private static void print(String blank, Object obj) {
        if (obj == null) {
            return;
        }
        if ((obj instanceof Collection)) {
            printCollection(blank, (Collection<?>) obj);
        } else if ((obj instanceof Map)) {
            printMap(blank, (Map<?, ?>) obj);
        } else {
            printBean(blank, obj);
        }
    }

    /**
     *  print object.
     * @param obj this object for print
     */
    public static void print(Object obj) {
        if (obj == null) {
            return;
        }
        if ((obj instanceof Collection)) {
            printCollection("", (Collection<?>) obj);
        } else if ((obj instanceof Map)) {
            printMap("", (Map<?, ?>) obj);
        } else {
            printBean("", obj);
        }
    }
}
