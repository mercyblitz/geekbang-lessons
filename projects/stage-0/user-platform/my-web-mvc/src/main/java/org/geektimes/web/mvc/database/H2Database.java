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

package org.geektimes.web.mvc.database;

import java.sql.*;
import java.util.Properties;

/**
 * @author lw1243925457
 */
public class H2Database {

    public static H2Database getInstance() {
        return new H2Database();
    }

    private Connection connection;

    private H2Database() {
        try {
            init();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String CREATE_USERS_TABLE_DDL_SQL = "create table mvc_user ( " +
            "id integer auto_increment," +
            "name varchar(20)," +
            "password varchar(20)," +
            "email varchar(20)," +
            "phoneNumber varchar(20)," +
            "primary key (`id`));";

    private void init() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Driver driver = DriverManager.getDriver("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");
        connection = driver.connect("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", new Properties());
        Statement statement = connection.createStatement();
        // 创建 users 表
        System.out.println(statement.execute(CREATE_USERS_TABLE_DDL_SQL));
        statement.close();
    }

    public Connection getConnection() {
        return connection;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Driver driver = DriverManager.getDriver("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");
        Connection connection = driver.connect("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", new Properties());
        Statement statement = connection.createStatement();
        // 创建 users 表
        System.out.println(statement.execute(CREATE_USERS_TABLE_DDL_SQL));
    }
}
