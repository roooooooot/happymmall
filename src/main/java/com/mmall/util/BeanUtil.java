package com.mmall.util;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BeanUtil {

    /**
     * 相同属性名的两个对象之间属性值的copy
     *
     * @param target    目标对象
     * @param source    源对象
     */
    public static void copyObjectProperties(Object target, Object source) {
        MethodAccess descMethodAccess = MethodAccess.get(target.getClass());
        MethodAccess orgiMethodAccess = MethodAccess.get(source.getClass());

        for (Field field : source.getClass().getDeclaredFields()) {
            if (Modifier.isPrivate(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                String feildName = StringUtils.capitalize(field.getName());
                int getIndex = orgiMethodAccess.getIndex("get" + feildName);
                int setIndex = descMethodAccess.getIndex("set" + feildName);
                descMethodAccess.invoke(target, setIndex, orgiMethodAccess.invoke(source, getIndex));
            }
        }
    }
}
