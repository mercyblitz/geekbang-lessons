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
package com.salesmanager.shop.application.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.URL;

/**
 * Embedded Tomcat Configuration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@ConditionalOnClass(TomcatServletWebServerFactory.class)
@Configuration(proxyBeanMethods = false)
public class EmbeddedTomcatConfiguration implements BeanClassLoaderAware {

    private File webappDirectory = null;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            if (webappDirectory != null) {
                factory.addContextCustomizers(context -> {
                    context.setDocBase(webappDirectory.getAbsolutePath());
                });
            }
        };
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        webappDirectory = resolveWebappDirectory(classLoader);
    }

    private File resolveWebappDirectory(ClassLoader classLoader) {
        String className = EmbeddedTomcatConfiguration.class.getName();
        String classResourceName = className.replace('.', '/').concat(".class");
        URL classResourceURL = classLoader.getResource(classResourceName);
        if ("file".equals(classResourceURL.getProtocol())) {
            String classFilePath = classResourceURL.getFile();
            String classPath = StringUtils.substringBefore(classFilePath, classResourceName);
            File classPathDir = new File(classPath);
            File rootDir = classPathDir.getParentFile().getParentFile();
            String webappRelativePath = "src/main/webapp";
            File webappDirectory = new File(rootDir, webappRelativePath);
            if (webappDirectory.exists()) {
                return webappDirectory;
            }
        }
        return null;
    }
}
