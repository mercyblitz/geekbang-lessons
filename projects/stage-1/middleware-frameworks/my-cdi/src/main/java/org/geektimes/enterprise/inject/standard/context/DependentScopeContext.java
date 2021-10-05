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
package org.geektimes.enterprise.inject.standard.context;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.reflect.Type;

/**
 * The Context for {@link Dependent @Dependent}
 * <p>
 * If the bean does not declare any stereotype with a declared default scope,
 * the default scope for the bean is {@link Dependent @Dependent}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Dependent
 * @see AlterableContext
 * @since 1.0.0
 */
public class DependentScopeContext extends AbstractAlterableContext {

    public DependentScopeContext(BeanManager beanManager) {
        super(beanManager, Dependent.class);
    }
}
