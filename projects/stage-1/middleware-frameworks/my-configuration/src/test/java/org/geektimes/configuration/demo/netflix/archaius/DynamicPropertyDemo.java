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

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class DynamicPropertyDemo {

    private static final int DEFAULT_VALUE = 9;

    public static void main(String[] args) {
        DynamicIntProperty prop = DynamicPropertyFactory.getInstance()
                .getIntProperty("myProperty", DEFAULT_VALUE);
        // prop.get() may change value at runtime

        // 动态更新
        Integer oldValue = prop.getValue();
        prop.addCallback(() -> {
            Integer newValue = prop.getValue();
        });

//        AtomicInteger oldValueRef = new AtomicInteger(prop.getValue());
//
//        prop.addCallback(() -> {
//            Integer oldValue = oldValueRef.get();
//            Integer newValue = prop.getValue();
//            // 保存当前值作为上一次配置内容
//            oldValueRef.set(newValue);
//        });

    }
}
