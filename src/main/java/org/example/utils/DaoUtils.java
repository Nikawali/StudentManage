package org.example.utils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DaoUtils {
    // ResultSet -> Obj
    public static <T> T resultSetToObject(ResultSet rs, Class<T> clazz) {
        T obj = null;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);  // 获取实际列名
                Object value = rs.getObject(i);

                if (value != null) {
                    setFieldValue(obj, columnName, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static void setFieldValue(Object obj, String columnName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(columnName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException e) {
            // 列名在对象中没有对应字段时，安静跳过
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getFieldNames(
            String sql
    ) {

        List<String> fieldNames =
                new ArrayList<>();

        Pattern pattern =
                Pattern.compile(
                        "#\\{(.*?)\\}"
                );

        Matcher matcher =
                pattern.matcher(sql);

        while (matcher.find()) {

            fieldNames.add(
                    matcher.group(1)
            );
        }

        return fieldNames;
    }

    public static String parseSql(String sql) {

        return sql.replaceAll(
                "#\\{.*?}",
                "?"
        );
    }


    public static void objectToPreparedStatement(
            Object obj,
            PreparedStatement ps,
            String... fieldNames) throws Exception {

        int index = 1;
        for (String fieldName : fieldNames) {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            ps.setObject(index++, field.get(obj));
        }
    }
}
