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
package org.geektimes.rpc.client;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import org.geektimes.rpc.InvocationRequest;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Exchange {@link Future}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ExchangeFuture implements Future {

    private final long createdTime;

    private InvocationRequest request;

    private Promise promise;

    private static Map<String, ExchangeFuture> workingFutureMap = new ConcurrentHashMap<>();

    public static ExchangeFuture createExchangeFuture(InvocationRequest request) {
        String requestId = request.getRequestId();
        return workingFutureMap.computeIfAbsent(requestId, id -> new ExchangeFuture(request));
    }

    public static ExchangeFuture removeExchangeFuture(String requestId) {
        return workingFutureMap.remove(requestId);
    }

    public ExchangeFuture(InvocationRequest request) {
        this.createdTime = System.currentTimeMillis();
        this.request = request;
        this.promise = new DefaultPromise(new DefaultEventLoop());
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return promise.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return promise.isCancelled();
    }

    @Override
    public boolean isDone() {
        return promise.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return promise.get();
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return promise.get(timeout, unit);
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public InvocationRequest getRequest() {
        return request;
    }

    public Promise getPromise() {
        return promise;
    }
}
