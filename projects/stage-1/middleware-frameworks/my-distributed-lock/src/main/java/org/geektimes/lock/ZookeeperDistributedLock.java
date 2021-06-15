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

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * 基于 Zookeeper 实现分布式锁
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ZookeeperDistributedLock implements Lock {

    private static final String ROOT_PATH = "/locks";

    private ThreadLocal<String> resourceNameHolder = new ThreadLocal<String>() {

        @Override
        protected String initialValue() {
            Thread currentThread = Thread.currentThread();
            StackTraceElement[] stackTraceElements = currentThread.getStackTrace();
            StackTraceElement sourceElement = stackTraceElements[stackTraceElements.length - 1];
            return sourceElement.getClassName() + "-" + sourceElement.getMethodName();
        }
    };

    private ThreadLocal<Long> lockIdHolder = new ThreadLocal<Long>();

    private CuratorFramework curatorFramework;

    public ZookeeperDistributedLock() {
        initCuratorFramework();
        initRootPath();
    }

    private void initCuratorFramework() {
        this.curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(50, 3))
                .build();

        this.curatorFramework.start();
    }

    private void initRootPath() {
        try {
            curatorFramework.create().forPath(ROOT_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        while (isLockHeld(resourceName)) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
        }
    }

    private boolean isLockHeld(String resourceName) {
        boolean held = false;
        return held;
    }

    private boolean acquireLock(String resourceName) {

        // Reentrant
        Long id = lockIdHolder.get();

        if (id != null) {
            return true;
        }

        boolean acquired = false;

        return acquired;
    }

    private boolean releaseLock(String resourceName) {
        return true;
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
        ZookeeperDistributedLock distributedLock = new ZookeeperDistributedLock();

        distributedLock.lock();
        doBusiness();
        distributedLock.unlock();

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
