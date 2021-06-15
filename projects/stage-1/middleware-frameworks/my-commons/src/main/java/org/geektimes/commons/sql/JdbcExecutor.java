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

import org.geektimes.commons.function.ThrowableConsumer;
import org.geektimes.commons.function.ThrowableFunction;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import static org.geektimes.commons.function.ThrowableSupplier.execute;
import static org.geektimes.commons.sql.PreparedStatementParameterMapper.getInstance;

/**
 * JDBC Executor
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class JdbcExecutor {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public boolean executeUpdate(String sql, ThrowableConsumer<PreparedStatement> preparedStatementBinder) {
        return execute(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = prepareStatement(connection, sql)) {
                preparedStatementBinder.accept(preparedStatement);
                return preparedStatement.executeUpdate() > 0;
            }
        });
    }

    public <E> List<E> queryList(String sql, ThrowableFunction<ResultSet, E> resultMapper, Object... args) {
        return execute(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = prepareStatement(connection, sql, args);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                List<E> entities = new LinkedList<>();
                while (resultSet.next()) {
                    E entity = resultMapper.apply(resultSet);
                    entities.add(entity);
                }

                return entities;
            }

        });
    }

    public int executeUpdate(String sql, Object... args) {
        return execute(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = prepareStatement(connection, sql, args);) {
                return preparedStatement.executeUpdate();
            }
        });
    }

    protected PreparedStatement prepareStatement(Connection connection, String sql, Object... args) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            Object parameter = args[i];
            int parameterIndex = i + 1;
            Class<?> argumentType = parameter.getClass();
            PreparedStatementParameterMapper mapper = getInstance(argumentType);
            if (mapper != null) {
                mapper.map(preparedStatement, parameterIndex, parameter);
            }
        }
        return preparedStatement;
    }

    public boolean executeSQL(String sql) {
        return execute(() -> {
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement()) {
                return statement.execute(sql);
            }
        });
    }

    public boolean tableExists(String tableName) {
        return execute(() -> {
            try (Connection connection = getConnection()) {
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                try (ResultSet resultSet = databaseMetaData.getTables(null, null, null,
                        new String[]{"TABLE"})) {
                    if (resultSet.next()) {
                        return tableName.equalsIgnoreCase(resultSet.getString("TABLE_NAME"));
                    }
                    return false;
                }
            }
        });
    }
}
