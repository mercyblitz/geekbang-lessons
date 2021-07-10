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
package org.geektimes.configuration.demo.netflix.archaius;

import com.netflix.config.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class PolledConfigurationDemo {

    private static final Long DEFAULT_VALUE = 9L;

    public static void main(String[] args) throws InterruptedException {
        PolledConfigurationSource source = createMyOwnSource();
        AbstractPollingScheduler scheduler = createMyOwnScheduler();
        ConfigurationManager.install(new DynamicConfiguration(source, scheduler));

        DynamicLongProperty prop = DynamicPropertyFactory.getInstance()
                .getLongProperty("currentTime", DEFAULT_VALUE);

        AtomicLong oldValueRef = new AtomicLong(prop.getValue());

        System.out.println("oldValue : " + oldValueRef.get());

        prop.addCallback(() -> {
            Long oldValue = oldValueRef.get();
            Long newValue = prop.getValue();
            // 保存当前值作为上一次配置内容
            oldValueRef.set(newValue);
            System.out.printf("[线程：%s] oldValue = %d, newValue = %d \n",
                    Thread.currentThread().getName(),
                    oldValue,
                    newValue
            );
        });


        Thread.sleep(1000L * 60);
    }

    private static AbstractPollingScheduler createMyOwnScheduler() {
        return new ThreadPoolExecutorPollingScheduler();
    }

    private static PolledConfigurationSource createMyOwnSource() {
        return new InMemoryPolledConfigurationSource();
    }
}
