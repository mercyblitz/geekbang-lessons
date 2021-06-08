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
package org.geektimes.commons.sql;

import org.apache.commons.lang.reflect.MethodUtils;
import org.geektimes.commons.function.ThrowableAction;
import org.geektimes.commons.lang.Prioritized;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.reflect.MethodUtils.invokeExactMethod;
import static org.geektimes.commons.reflect.util.ClassUtils.isAssignableFrom;
import static org.geektimes.commons.reflect.util.ClassUtils.resolvePrimitiveType;
import static org.geektimes.commons.sql.ReflectivePreparedStatementParameterMapper.MethodInfo.of;

/**
 * {@link PreparedStatementParameterMapper} based on Java Reflection
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectivePreparedStatementParameterMapper implements PreparedStatementParameterMapper<Object> {

    private static List<MethodInfo> methodInfoList = asList(
            of(Boolean.class),
            of(Byte.class),
            of(Short.class),
            of(Integer.class),
            of(Long.class),
            of(Float.class),
            of(Double.class),
            of(BigDecimal.class),
            of(String.class),
            of(byte[].class).method("setBytes"),
            of(Date.class),
            of(Time.class),
            of(Timestamp.class),
            of(Ref.class),
            of(Blob.class),
            of(Clob.class),
            of(Array.class),
            of(URL.class),
            of(RowId.class),
            of(SQLXML.class),
            of(Reader.class).method("setClob"),
            of(InputStream.class).method("setBlob"),
            of(Object.class)
    );

    public boolean matches(Class<?> parameterType) {
        if (parameterType == null) {
            return false;
        }
        return getMethodInfo(parameterType) != null;
    }

    private MethodInfo getMethodInfo(Class<?> parameterType) {
        MethodInfo methodInfo_ = null;
        for (MethodInfo methodInfo : methodInfoList) {
            if (isAssignableFrom(methodInfo.getMappedType(), parameterType)) {
                methodInfo_ = methodInfo;
                break;
            }
        }
        return methodInfo_;
    }

    @Override
    public void map(PreparedStatement preparedStatement, int parameterIndex, Object parameter) throws SQLException {

        Class<?> parameterType = parameter.getClass();

        MethodInfo methodInfo = getMethodInfo(parameterType);

        String methodName = methodInfo.getMethodName();
        Class<?>[] parameterTypes = methodInfo.getParameterTypes();

        Object[] args = new Object[]{parameterIndex, parameter};

        ThrowableAction.execute(() -> invokeExactMethod(preparedStatement, methodName, args, parameterTypes)
                , SQLException.class);
    }

    static class MethodInfo {

        private final Class<?> mappedType;

        private String methodName;

        private Class[] parameterTypes;

        MethodInfo(Class<?> mappedType) {
            this.mappedType = mappedType;
            Class<?> primitiveType = resolvePrimitiveType(mappedType);
            // setXXX method
            this.methodName = "set" + mappedType.getClass().getSimpleName();
            this.parameterTypes = new Class[]{int.class, primitiveType != null ? primitiveType : mappedType};
        }

        MethodInfo method(String methodName) {
            this.methodName = methodName;
            return this;
        }

        MethodInfo parameterTypes(Class<?>... parameterTypes) {
            this.parameterTypes = parameterTypes;
            return this;
        }

        public Class<?> getMappedType() {
            return mappedType;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public Class[] getParameterTypes() {
            return parameterTypes;
        }

        public void setParameterTypes(Class[] parameterTypes) {
            this.parameterTypes = parameterTypes;
        }

        static MethodInfo of(Class<?> mappedType) {
            return new MethodInfo(mappedType);
        }
    }

    @Override
    public int getPriority() {
        return MIN_PRIORITY;
    }
}
