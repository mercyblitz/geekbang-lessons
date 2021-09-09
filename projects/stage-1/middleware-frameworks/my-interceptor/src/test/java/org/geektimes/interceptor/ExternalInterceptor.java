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
package org.geektimes.interceptor;

import javax.annotation.PostConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * External {@link Interceptor @Interceptor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Interceptor
public class ExternalInterceptor {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private Set<String> methodNames = new LinkedHashSet<>();

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Throwable {
        String methodName = context.getMethod().getName();
        methodNames.add(methodName);
        logger.info("Interception Method : " + methodName);
        return context.proceed();
    }

    @PostConstruct
    public void postConstruct(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        methodNames.add(methodName);
        logger.info("Post Construct : " + context.getMethod().getName());
        context.proceed();
    }

    public Set<String> getMethodNames() {
        return methodNames;
    }
}
