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
package org.geektimes.enterprise.beans;

import org.geektimes.enterprise.beans.xml.bind.Beans;

import java.net.URL;
import java.util.Set;

/**
 * The information class for a Bean archive.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class BeanArchiveInfo {

    private final URL beansXMLResource;

    private final Beans beans;

    private final Set<Class<?>> classes;

    public BeanArchiveInfo(URL beansXMLResource, Beans beans, Set<Class<?>> classes) {
        this.beansXMLResource = beansXMLResource;
        this.beans = beans;
        this.classes = classes;
    }

    public URL getBeansXMLResource() {
        return beansXMLResource;
    }

    public Beans getBeans() {
        return beans;
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }
}
