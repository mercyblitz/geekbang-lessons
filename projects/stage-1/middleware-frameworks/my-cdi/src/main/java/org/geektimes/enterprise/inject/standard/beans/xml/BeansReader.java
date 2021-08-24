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
package org.geektimes.enterprise.inject.standard.beans.xml;

import org.geektimes.enterprise.inject.standard.beans.xml.bind.Beans;

import java.io.IOException;
import java.net.URL;

/**
 * A Reader for {@link Beans}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface BeansReader {

    String BEANS_XML_RESOURCE_NAME = "META-INF/beans.xml";

    /**
     * Read a {@link Beans} instance from the specified bean archive which contains
     * XML {@link #BEANS_XML_RESOURCE_NAME resource} in the
     * specified {@link ClassLoader}.
     *
     * @param beansXMLResource the URL represents the {@link #BEANS_XML_RESOURCE_NAME Beans XML resource}
     * @param classLoader      the specified {@link ClassLoader}
     * @return <code>null</code> if the resource is an empty file, or
     * {@link Beans} instance parsed from {@link #BEANS_XML_RESOURCE_NAME "META-INF/beans.xml"} {@link Beans}
     * @throws IOException
     */
    Beans readBeans(URL beansXMLResource, ClassLoader classLoader) throws IOException;

}
