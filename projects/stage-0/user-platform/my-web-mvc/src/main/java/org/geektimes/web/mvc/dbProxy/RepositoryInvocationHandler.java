/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.geektimes.web.mvc.dbProxy;

import org.geektimes.web.mvc.sql.Insert;
import org.geektimes.web.mvc.sql.Select;
import org.geektimes.web.mvc.database.H2Database;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lw1243925457
 */
public class RepositoryInvocationHandler implements InvocationHandler {

    H2Database database = H2Database.getInstance();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation: annotations) {
            if (annotation instanceof Insert) {
                try {
                    return executeInsertSql(((Insert) annotation).value(), args);
                } catch (IllegalAccessException | SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            if (annotation instanceof Select) {
                try {
                    return executeQuerySql(((Select) annotation).value(), ((Select) annotation).returnType());
                } catch (SQLException | ClassNotFoundException | IllegalAccessException | IntrospectionException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private Object executeQuerySql(String querySql, String returnType) throws SQLException, ClassNotFoundException, IllegalAccessException, IntrospectionException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        List<Object> result = new ArrayList<>();

        Connection conn = database.getConnection();
        Statement statement = conn.createStatement();

        ResultSet res = statement.executeQuery(querySql);
        // BeanInfo
        BeanInfo userBeanInfo = Introspector.getBeanInfo(Class.forName(returnType), Object.class);

        while (res.next()) {
            Object object = Class.forName(returnType).newInstance();
            for (PropertyDescriptor propertyDescriptor : userBeanInfo.getPropertyDescriptors()) {
                String fieldName = propertyDescriptor.getName();
                Class fieldType = propertyDescriptor.getPropertyType();
                String methodName = typeMethodMappings.get(fieldType);
                // 可能存在映射关系（不过此处是相等的）
                String columnLabel = fieldName;
                Method resultSetMethod = ResultSet.class.getMethod(methodName, String.class);
                // 通过放射调用 getXXX(String) 方法
                Object resultValue = resultSetMethod.invoke(res, columnLabel);
                // 获取 User 类 Setter方法
                // PropertyDescriptor ReadMethod 等于 Getter 方法
                // PropertyDescriptor WriteMethod 等于 Setter 方法
                Method setterMethodFromUser = propertyDescriptor.getWriteMethod();
                // 以 id 为例，  user.setId(resultSet.getLong("id"));
                setterMethodFromUser.invoke(object, resultValue);
            }

            System.out.println(object.toString());
        }

        statement.close();
        return result;
    }

    /**
     * 数据类型与 ResultSet 方法名映射
     */
    static Map<Class, String> typeMethodMappings = new HashMap<>();

    static {
        typeMethodMappings.put(Long.class, "getLong");
        typeMethodMappings.put(String.class, "getString");
    }

    private Object executeInsertSql(String sqlTemplate, Object[] args) throws IllegalAccessException, SQLException {
        System.out.println("execute insert: " + sqlTemplate);
        StringBuilder cols = new StringBuilder();
        StringBuilder values = new StringBuilder();

        Object data = args[0];
        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field: fields) {
            if (field.get(data) == null) {
                continue;
            }
            cols.append(field.getName()).append(",");
            values.append(field.get(data).toString()).append(",");
        }

        String sql = String.format(sqlTemplate, cols.toString(), values.toString());
        System.out.println("sql string: " + sql);

        Connection conn = database.getConnection();
        Statement statement = conn.createStatement();
        statement.execute(sql);

        String querySql = "select * from mvc_user;";
        ResultSet res = statement.executeQuery(querySql);
        while (res.next()) {
            System.out.println(res.toString());
        }

        statement.close();

        return true;
    }
}
