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
import org.geektimes.enterprise.inject.standard.beans.xml.bind.Scan;
import org.junit.Test;

import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import static org.geektimes.enterprise.inject.standard.beans.xml.BeansReader.BEANS_XML_RESOURCE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * {@link JAXBBeansReader} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class JAXBBeansReaderTest {

    @Test
    public void test() throws Exception {
        JAXBBeansReader reader = new JAXBBeansReader();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Enumeration<URL> beansXMLResources = classLoader.getResources(BEANS_XML_RESOURCE_NAME);
        while (beansXMLResources.hasMoreElements()) {
            URL beansXMLResource = beansXMLResources.nextElement();
            Beans beans = reader.readBeans(beansXMLResource, classLoader);

            Scan scan = beans.getScan();
            List<Scan.Exclude> excludes = scan.getExclude();

            int i = 0;
            Scan.Exclude exclude = excludes.get(i++);
            assertEquals("com.acme.rest.*", exclude.getName());
            assertTrue(exclude.getIfClassAvailableOrIfClassNotAvailableOrIfSystemProperty().isEmpty());

            exclude = excludes.get(i++);
            assertEquals("com.acme.faces.**", exclude.getName());
            List<Object> list = exclude.getIfClassAvailableOrIfClassNotAvailableOrIfSystemProperty();
            Scan.Exclude.IfClassNotAvailable ifClassNotAvailable = (Scan.Exclude.IfClassNotAvailable) list.get(0);
            assertEquals("javax.faces.context.FacesContext", ifClassNotAvailable.getName());

            exclude = excludes.get(i++);
            assertEquals("com.acme.verbose.*", exclude.getName());
            list = exclude.getIfClassAvailableOrIfClassNotAvailableOrIfSystemProperty();
            Scan.Exclude.IfSystemProperty ifSystemProperty = (Scan.Exclude.IfSystemProperty) list.get(0);
            assertEquals("verbosity", ifSystemProperty.getName());
            assertEquals("low", ifSystemProperty.getValue());

            exclude = excludes.get(i++);
            assertEquals("com.acme.ejb.**", exclude.getName());
            list = exclude.getIfClassAvailableOrIfClassNotAvailableOrIfSystemProperty();
            Scan.Exclude.IfClassAvailable ifClassAvailable = (Scan.Exclude.IfClassAvailable) list.get(0);
            assertEquals("javax.enterprise.inject.Model", ifClassAvailable.getName());
            ifSystemProperty = (Scan.Exclude.IfSystemProperty) list.get(1);
            assertEquals("exclude-ejbs", ifSystemProperty.getName());
        }

    }
}
