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
package org.geektimes.lock;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.geektimes.commons.function.ThrowableAction;
import org.geektimes.commons.function.ThrowableSupplier;

import javax.sql.DataSource;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于数据库实现分布式锁
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DatabaseDistributedLock implements Lock {

    private static final String CREATE_LOCK_TABLE_DDL_SQL = "CREATE TABLE locks(\n" +
            "id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n" +
            "resource_name VARCHAR(512) NOT NULL)";

    private static final String ADD_UNIQUE_INDEX_DDL_SQL = "CREATE UNIQUE INDEX unique_resource_name on locks(resource_name)";

    private static final String ADD_LOCK_DML_SQL = "INSERT INTO locks(resource_name) VALUES (?)";

    private static final String HOLD_LOCK_DML_SQL = "SELECT id FROM locks WHERE resource_name = ?";

    private static final String REMOVE_LOCK_DML_SQL = "DELETE FROM locks WHERE resource_name = ?";

    private static final Long DUPLICATED_LOCK_ID = Long.MIN_VALUE;

    private ThreadLocal<String> resourceNameHolder = new ThreadLocal<String>() {

        @Override
        protected String initialValue() {
            Thread currentThread = Thread.currentThread();
            StackTraceElement[] stackTraceElements = currentThread.getStackTrace();
            StackTraceElement sourceElement = stackTraceElements[stackTraceElements.length - 1];
            return sourceElement.getClassName() + "." + sourceElement.getMethodName();
        }
    };

    private ThreadLocal<Long> lockIdHolder = new ThreadLocal<Long>();

    private Object lock = new Object();

    private DataSource dataSource;

    public DatabaseDistributedLock() {
        initDataSource();
        initTables();
    }

    private Connection getConnection() {
        return ThrowableSupplier.execute(() -> dataSource.getConnection());
    }

    private void initDataSource() {
        this.dataSource = ThrowableSupplier.execute(() -> {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUrl("jdbc:derby:db/middleware;create=true");
            dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
            return dataSource;
        });
    }

    private void initTables() {
        if (isLocksTableAbsent()) {
            executeSQL(CREATE_LOCK_TABLE_DDL_SQL);
            executeSQL(ADD_UNIQUE_INDEX_DDL_SQL);
        }
    }

    private boolean isLocksTableAbsent() {
        return ThrowableSupplier.execute(() -> {
            try (Connection connection = getConnection();
                 ResultSet resultSet = getTables(connection)) {
                boolean present = false;
                while (resultSet.next()) {
                    String tableName = resultSet.getString("TABLE_NAME").toLowerCase();
                    if ("locks".equals(tableName)) {
                        present = true;
                        break;
                    }
                }
                return !present;
            }
        });
    }

    private ResultSet getTables(Connection connection) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        return databaseMetaData.getTables(null, null, null,
                new String[]{"TABLE"});
    }


    private void executeSQL(String sql) {
        ThrowableAction.execute(() -> {
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        });
    }

    @Override
    public void lock() {
        String resourceName = getResourceName();
        if (!acquireLock(resourceName)) {
            // block
            block(resourceName);
        }

    }

    private void block(String resourceName) {
        // park 时间等待需要评估
        // 过长的话，非获取锁线程等待超出期望时间
        // 1s, T1(lock) 执行 20ms
        while (isLockHeld(resourceName)) {
//            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(20L));
            synchronized (this) {
                try {
                    this.lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isLockHeld(String resourceName) {
        boolean held = false;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(HOLD_LOCK_DML_SQL);
        ) {
            statement.setString(1, resourceName);
            ResultSet resultSet = statement.executeQuery();
            held = resultSet.next();
        } catch (SQLException e) {

        }
        return held;
    }

    private boolean acquireLock(String resourceName) {

        // Reentrant
        Long id = lockIdHolder.get();

        if (id != null) {
            return true;
        }

        boolean acquired = false;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            String sql = StringUtils.replace(ADD_LOCK_DML_SQL, "?", "'" + resourceName + "'");
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = statement.getGeneratedKeys();
            while (resultSet.next()) {
                id = resultSet.getLong(1);
                lockIdHolder.set(id);
                acquired = true;
                break;
            }
        } catch (SQLException e) {
            String message = e.getMessage();
            if (message.contains("duplicate key")) {
                acquired = false;
                lockIdHolder.remove();
            }
        }
        return acquired;
    }

    private boolean releaseLock(String resourceName) {
        return ThrowableSupplier.execute(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_LOCK_DML_SQL)) {
                preparedStatement.setString(1, resourceName);
                return preparedStatement.executeUpdate() > 0;
            }
        });
    }

    private String getResourceName() {
        String resourceName = resourceNameHolder.get();
        return resourceName;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            tryLock(-1L, null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String resourceName = getResourceName();
        return acquireLock(resourceName);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        String resourceName = getResourceName();
        releaseLock(resourceName);
        clearThreadLocals();
    }

    private void clearThreadLocals() {
        resourceNameHolder.remove();
        lockIdHolder.remove();
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    public static void main(String[] args) throws Throwable {
        DatabaseDistributedLock distributedLock = new DatabaseDistributedLock();

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                distributedLock.lock();
                doBusiness();
                distributedLock.unlock();
            });
        }


        executorService.awaitTermination(5, TimeUnit.SECONDS);
        executorService.shutdown();
    }

    private static void doBusiness() {
        try {
            System.out.printf("[%s] Do Business\n", Thread.currentThread().getName());
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
