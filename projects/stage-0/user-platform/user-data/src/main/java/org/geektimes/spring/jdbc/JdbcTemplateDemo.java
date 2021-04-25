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
package org.geektimes.spring.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@link JdbcTemplate}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-15
 */
public class JdbcTemplateDemo {

    public static void main(String[] args) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        String userName = jdbcTemplate.query(
                // 创建 PreparedStatement（SQL）
                new SimplePreparedStatementCreator(),
                // 为 PreparedStatement 设置参数
                // setLong
                new SimplePreparedStatementSetter(),
                // 将 ResultSet 转换成 String 类型
                new UserNameResultSetExtractor()
        );

        String sql = "SELECT name FROM users WHERE id=?";
        userName = jdbcTemplate.query(
                c -> c.prepareStatement(sql)
                , ps -> {
                    ps.setLong(1, 1);
                }, rs -> rs.getString("name"));

        rawJdbcApi();
    }

    private static void rawJdbcApi() {
        String sql = "SELECT name FROM users WHERE id=?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String userName = null;
        try {
            ps = connection.prepareCall(sql);
            ps.setLong(1, 1L);
            rs = ps.executeQuery();
            userName = rs.getString("name");
        } catch (SQLException e) {

        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {

                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {

                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {

                }
            }
        }
    }

    private static void rawJdbcApi2() throws SQLException {
        String sql = "SELECT name FROM users WHERE id=?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String userName = null;
        try {
            ps = connection.prepareCall(sql);
            ps.setLong(1, 1L);
            rs = ps.executeQuery();
            userName = rs.getString("name");
        } finally {
//            if (rs != null) {
//                rs.close();
//            }
//            if (ps != null) {
//                ps.close();
//            }
//            if (connection != null) {
//                connection.close();
//            }
            close(rs);
            close(ps);
            close(connection);
        }
    }

    private static void close(AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
