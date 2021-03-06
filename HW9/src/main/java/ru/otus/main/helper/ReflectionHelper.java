package ru.otus.main.helper;


import ru.otus.main.orm.Mapping;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReflectionHelper {
    public final static Map<Class<?>, Class<?>> wrapper = new HashMap<>();

    static {
        wrapper.put(boolean.class, Boolean.class);
        wrapper.put(byte.class, Byte.class);
        wrapper.put(short.class, Short.class);
        wrapper.put(char.class, Character.class);
        wrapper.put(int.class, Integer.class);
        wrapper.put(long.class, Long.class);
        wrapper.put(float.class, Float.class);
        wrapper.put(double.class, Double.class);
    }

    /**
     * @param clazz
     * @return
     */
    public static Mapping getMapping(Class<?> clazz) {

        Table annotationTable = clazz.getDeclaredAnnotation(Table.class);
        Mapping entityMapping = new Mapping();
        entityMapping.setTableName(annotationTable.name());

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Column annotationColumn = field.getDeclaredAnnotation(Column.class);
            if ((annotationColumn == null) || ((!field.getType().isPrimitive() && (field.getType() != String.class)) && (!wrapper.values().contains(field.getDeclaringClass())))) {
                continue;
            }
            String javaFieldName = field.getName();
            String dbColumnName = annotationColumn.name().isEmpty() ? javaFieldName : annotationColumn.name();
            entityMapping.addFieldBound(javaFieldName, dbColumnName);
            if (field.isAnnotationPresent(Id.class)) {
                entityMapping.setIdFieldName(field.getName());
            }
        }
        return entityMapping;
    }

    /**
     * @param object
     * @param fieldName
     * @return
     */
    public static Object getFieldValue(Object object, String fieldName) {
        boolean isAccessible = true;
        Object value = null;
        Field field = null;
        try {
            field = object.getClass().getDeclaredField(fieldName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
                isAccessible = false;
            }
            value = field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (field != null && !isAccessible) {
                field.setAccessible(false);
            }
        }
        return value;
    }

    /**
     * @param object
     * @param fieldName
     * @param value
     */
    public static void setFieldValue(Object object, String fieldName, Object value) {
        boolean isAccessible = true;
        Field field = null;
        try {
            field = object.getClass().getDeclaredField(fieldName);
        if (!field.isAccessible()) {
                field.setAccessible(true);
                isAccessible = false;
            }

            field.set(object, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (field != null && !isAccessible) {
            field.setAccessible(false);
        }
    }

    /**
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getNewInstance(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            return null;
        }
    }
}
