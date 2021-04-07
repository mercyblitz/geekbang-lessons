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
package org.geektimes.inject;

import org.geektimes.function.ThrowableFunction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static org.geektimes.function.ThrowableFunction.execute;

/**
 * 抽象 {@link ComponentRepository}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public abstract class AbstractComponentRepository implements ComponentRepository {

    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * 本地组件缓存
     */
    private Map<String, Object> componentsCache = new LinkedHashMap<>();

    public <C> C getComponent(String componentName) {
        return (C) componentsCache.computeIfAbsent(componentName, this::doGetComponent);
    }

    public Set<String> getComponentNames() {
        return componentsCache.isEmpty() ? componentsCache.keySet() : listComponentNames();
    }

    /**
     * 通过指定 ThrowableFunction 返回计算结果
     *
     * @param argument         Function's argument
     * @param function         ThrowableFunction
     * @param ignoredException 是否忽略异常
     * @param <R>              返回结果类型
     * @return 返回
     * @see ThrowableFunction#apply(Object)
     */
    protected <T, R> R executeInContext(T argument, ThrowableFunction<T, R> function, boolean ignoredException) {
        R result = null;
        try {
            result = execute(argument, function);
        } catch (Throwable e) {
            if (ignoredException) {
                logger.warning(e.getMessage());
            } else {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    protected abstract Set<String> listComponentNames();

    protected abstract Object doGetComponent(String s);

}
