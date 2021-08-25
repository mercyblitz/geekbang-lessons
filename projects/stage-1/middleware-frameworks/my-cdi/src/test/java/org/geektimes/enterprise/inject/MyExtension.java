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

import org.geektimes.enterprise.inject.standard.ReflectiveAnnotatedType;

import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.*;
import javax.inject.Inject;

/**
 * My {@link Extension}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class MyExtension implements Extension {

    @Inject
    private BeanManager beanManager;

    public void beforeBeanDiscovery(@Observes @Default BeforeBeanDiscovery beforeBeanDiscovery) {
        beforeBeanDiscovery.addQualifier(Default.class);
        beforeBeanDiscovery.addAnnotatedType(new ReflectiveAnnotatedType<>(BookShop.class), "book1");
    }

    public void processAnnotatedType(@Observes ProcessAnnotatedType<BookShop> processAnnotatedType) {
        System.out.println(processAnnotatedType);
        if (processAnnotatedType.getAnnotatedType().getJavaClass().isMemberClass()) {
            processAnnotatedType.veto();
        }
    }

    public void processSyntheticAnnotatedType(@Observes ProcessSyntheticAnnotatedType<BookShop> processSyntheticAnnotatedType) {
        System.out.println(processSyntheticAnnotatedType);
    }

    public void afterTypeDiscovery(@Observes AfterTypeDiscovery afterTypeDiscovery, EventMetadata eventMetadata) {
        System.out.println(afterTypeDiscovery);
        System.out.println(eventMetadata);
    }

    public void processInjectionPoint(@Observes ProcessInjectionPoint processInjectionPoint) {
        System.out.println(processInjectionPoint);
    }

    public void processInjectionTarget(@Observes ProcessInjectionTarget processInjectionTarget) {
        System.out.println(processInjectionTarget);
    }

    public void processBeanAttributes(@Observes ProcessBeanAttributes processBeanAttributes) {
        System.out.println(processBeanAttributes);
    }

    public void processBeanEvent(@Observes ProcessBean processBean) {
        System.out.println(processBean);
    }

    public void processProducerEvent(@Observes ProcessProducer processProducer) {
        System.out.println(processProducer);
    }

    public void onAnyEvent(@ObservesAsync Object event) {
        System.out.println(event);
    }

}
