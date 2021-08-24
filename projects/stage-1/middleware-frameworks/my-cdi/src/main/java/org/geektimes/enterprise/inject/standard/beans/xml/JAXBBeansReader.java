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
import org.geektimes.enterprise.inject.standard.beans.xml.bind.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * {@link BeansReader} based on JAXB
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class JAXBBeansReader implements BeansReader {

    @Override
    public Beans readBeans(URL beansXMLResource, ClassLoader classLoader) throws IOException {
        Beans beans = null;
        try (InputStream inputStream = beansXMLResource.openStream()) {
            if (inputStream.available() > 0) {
                String contextPath = ObjectFactory.class.getPackage().getName();
                JAXBContext jaxbContext = JAXBContext.newInstance(contextPath, classLoader);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                beans = (Beans) unmarshaller.unmarshal(inputStream);
            }
        } catch (JAXBException e) {
            // rethrows the exception as IOException
            throw new IOException(e);
        }
        return beans;
    }
}
