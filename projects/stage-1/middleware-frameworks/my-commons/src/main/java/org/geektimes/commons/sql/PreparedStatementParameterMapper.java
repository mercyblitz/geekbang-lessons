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

import org.geektimes.commons.lang.Prioritized;
import org.geektimes.commons.reflect.util.ClassUtils;
import org.geektimes.commons.reflect.util.TypeUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import static java.util.ServiceLoader.load;
import static java.util.stream.Collectors.toList;
import static org.geektimes.commons.function.Streams.stream;
import static org.geektimes.commons.reflect.util.ClassUtils.isAssignableFrom;
import static org.geektimes.commons.reflect.util.TypeUtils.findActualTypeArgument;

/**
 * The Mapper interface for {@link PreparedStatement}'s parameter
 *
 * @param <T> the parameter type
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface PreparedStatementParameterMapper<T> extends Prioritized {

    List<PreparedStatementParameterMapper> instances = stream(load(PreparedStatementParameterMapper.class))
            .sorted(Prioritized.COMPARATOR)
            .collect(toList());

    default Class<T> getParameterType() {
        return findActualTypeArgument(this.getClass(), PreparedStatementParameterMapper.class, 0);
    }

    default boolean matches(Class<?> parameterType) {
        return isAssignableFrom(getParameterType(), parameterType);
    }

    void map(PreparedStatement preparedStatement, int parameterIndex, T parameter) throws SQLException;

    static <T> PreparedStatementParameterMapper<T> getInstance(Class<T> mappedType) {
        PreparedStatementParameterMapper target = null;
        for (PreparedStatementParameterMapper instance : instances) {
            if (instance.matches(mappedType)) {
                target = instance;
                break;
            }
        }
        return target;
    }
}
