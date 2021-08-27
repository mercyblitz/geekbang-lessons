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
package org.geektimes.enterprise.inject;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ProcessObserverMethod;
import javax.inject.Inject;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
@Dependent
public class BookShop extends Business implements Shop<Book> {

    @Inject
    private BeanManager beanManager;

    @Produces
    private Book myBook = new Book();

    @PostConstruct
    public void init() {

    }

    @PreDestroy
    public void destroy() {
    }

    @Inject
    public void init(Shop<Book> bookShop) {
    }

    @Produces
    public Book book() {
        return new Book();
    }

    public void dispose(@Disposes Book book) {
    }

    public void onEvent(@Observes ProcessObserverMethod event) {
        System.out.println(event);
    }
}
