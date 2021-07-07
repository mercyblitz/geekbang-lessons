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

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * {@link JdbcExecutor} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class JdbcExecutorTest {

    private static final String USER_TABLE_DDL = "CREATE TABLE users(\n" +
            "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n" +
            "name VARCHAR(16) NOT NULL,\n" +
            "password VARCHAR(64) NOT NULL,\n" +
            "email VARCHAR(64) NOT NULL,\n" +
            "phoneNumber VARCHAR(32) NOT NULL)";

    public static final String INSERT_USER_DML_SQL =
            "INSERT INTO users(name,password,email,phoneNumber) VALUES " +
                    "(?,?,?,?)";

    public static final String QUERY_ALL_USERS_DML_SQL = "SELECT id,name,password,email,phoneNumber FROM users";

    public static final String DELETE_ALL_USERS_DML_SQL = "DELETE FROM users";

    private BasicDataSource dataSource;

    private JdbcExecutor jdbcExecutor;

    @Before
    public void init() {
        this.dataSource = createDataSource();
        this.jdbcExecutor = new JdbcExecutor();
        this.jdbcExecutor.setDataSource(dataSource);
        if (!jdbcExecutor.tableExists("users")) {
            assertTrue(jdbcExecutor.executeSQL(USER_TABLE_DDL));
        }
    }

    private BasicDataSource createDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:derby:db/test-db;create=true");
        dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        return dataSource;
    }

    @Test
    public void testTableExists() {
        assertTrue(jdbcExecutor.tableExists("users"));
    }

    @Test
    public void testExecuteUpdate() {

        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setName("name" + i);
            user.setPassword("******");
            user.setEmail("abc@abc.com");
            user.setPhoneNumber("123456789");
            jdbcExecutor.executeUpdate(INSERT_USER_DML_SQL, preparedStatement -> {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getPhoneNumber());
            });
        }

        List<User> users = jdbcExecutor.queryList(QUERY_ALL_USERS_DML_SQL, resultSet -> {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setName(resultSet.getString("name"));
            user.setPassword(resultSet.getString("password"));
            user.setEmail(resultSet.getString("email"));
            user.setPhoneNumber(resultSet.getString("phoneNumber"));
            return user;
        });

        for (User user : users) {
            System.out.println(user);
        }

        assertTrue(jdbcExecutor.executeUpdate(DELETE_ALL_USERS_DML_SQL) > 0);

    }

    @After
    public void destroy() throws SQLException {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}

class User {

    private Long id;

    private String name;

    private String password;

    private String email;

    private String phoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
