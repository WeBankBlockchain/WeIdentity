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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Object util
 *
 * @author v_wbgyang
 */
public class BeanUtil extends BaseBean {

    /**
     * print Object with log
     */
    public static void print(Object obj) {
        BaseBean.print(obj);
    }

    /**
     * check whether the two objects are the same
     */
    public static boolean equals(Object left, Object right) {
        // if obj1 == obj2 then return true
        if (left == right) {
            return true;
        }

        // if obj1 or obj2 is null then return false
        if (left == null || right == null) {
            return false;
        }

        if (left.getClass() != right.getClass()) {
            return false;
        }

        try {
            if ((left instanceof Collection)) {
                return equalsCollection((Collection<?>) left, (Collection<?>) right);
            } else if ((left instanceof Map)) {
                return equalsMap((Map<?,?>) left, (Map<?,?>) right);
            } else {
                return equalsObject(left, right);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean equalsObject(Object left, Object right) {
        if (isPrimitive(left) || (left instanceof Date)) {
            return left.equals(right);
        }

        if ((left instanceof String[])) {
            return Arrays.equals((String[]) left, (String[]) right);
        }

        Field[] fL = left.getClass().getDeclaredFields();
        Class<?> clsR = right.getClass();
        for (int i = 0; (fL != null) && (i < fL.length); i++) {
            try {
                Field fieldL = fL[i];
                Field fieldR = clsR.getDeclaredField(fieldL.getName());
                if (fieldL.getType() != fieldR.getType()) {
                    return false;
                }
                fieldL.setAccessible(true);
                fieldR.setAccessible(true);
                Object fieldLValue = fieldL.get(left);
                Object fieldRValue = fieldR.get(right);
                if (!equals(fieldLValue, fieldRValue)) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private static boolean equalsMap(Map<?,?> left, Map<?,?> right) throws Exception {
        if (right.size() != left.size()) {
            return false;
        }
        Iterator<?> itL = right.keySet().iterator();
        Iterator<?> itR = left.keySet().iterator();
        while (itL.hasNext()) {
            Object left_ = left.get(itL.next());
            Object right_ = right.get(itR.next());
            if (!equals(left_, right_)) {
                return false;
            }
        }
        return true;
    }

    private static boolean equalsCollection(Collection<?> left, Collection<?> right) throws Exception {
        if (left.size() != right.size()) {
            return false;
        }
        Iterator<?> itL = left.iterator();
        Iterator<?> itR = right.iterator();
        while (itL.hasNext()) {
            Object left_ = itL.next();
            Object right_ = itR.next();
            if (!equals(left_, right_)) {
                return false;
            }
        }
        return true;
    }
}
