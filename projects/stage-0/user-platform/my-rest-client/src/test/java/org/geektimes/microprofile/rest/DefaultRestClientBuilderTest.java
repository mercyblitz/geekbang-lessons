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
package org.geektimes.microprofile.rest;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.*;

/**
 * {@link DefaultRestClientBuilder} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-14
 */
public class DefaultRestClientBuilderTest {

    public static void main(String[] args) throws MalformedURLException {
//        HellWorld hellWorld = RestClientBuilder.newBuilder()
//                .baseUrl(new URL("http://127.0.0.1:8080"))
//                .build(HellWorld.class);
//
//        // URI -> http://127.0.0.1:8080/hello/world
//
//        // Java 动态代理
//        System.out.println(hellWorld.helloWorld());

        EchoService echoService = RestClientBuilder.newBuilder()
                .baseUrl(new URL("http://127.0.0.1:8080"))
                .build(EchoService.class);

        System.out.println(echoService.echo("2021"));
    }
}

@Priority(1)
@Interceptor
class RetryInterceptor {

    public void execute(InvocationContext invocationContext) {

    }
}

@Priority(2)
@Interceptor
class TimeoutInterceptor {

    public void execute(InvocationContext invocationContext) {

        Map<String, Object> context = invocationContext.getContextData();
        Long value = (Long) context.get("value");
        ChronoUnit unit = (ChronoUnit) context.get("unit");
        Duration duration = unit.getDuration();

        ExecutorService service = Executors.newSingleThreadExecutor();

        // 异步地执行
        Future<Object> future = service.submit(invocationContext::proceed);

        try {
            future.get(value, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // 线程状态不正确
        } catch (ExecutionException e) {
            // 目标方法执行异常
        } catch (TimeoutException e) {
            // 尝试
            // 补偿或者重试
        }

    }
}

@Path("/hello")
interface HellWorld {

    @GET
    @Path("/world")
    String helloWorld();
}

@Interceptors({TimeoutInterceptor.class, RetryInterceptor.class})
interface EchoService {

    @GET
    @Path("/echo/{message}")
    @Timeout(500)
    @Retry(retryOn = Exception.class)
    String echo(@PathParam("message") @DefaultValue("test") String message);
    // Java 8 之前，接口方法参数的名称无法获取
    // Java 8+，可以通过编译参数 -parameters 来存储到字节码

}


