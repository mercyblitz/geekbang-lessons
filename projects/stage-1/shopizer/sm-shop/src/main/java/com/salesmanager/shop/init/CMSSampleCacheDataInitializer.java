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
package com.salesmanager.shop.init;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

/**
 * CMS Sample Cache Data Initializer
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ApplicationContextInitializer
 * @since 1.0.0
 */
public class CMSSampleCacheDataInitializer implements ApplicationContextInitializer {

    public static final String SIMPLE_DATA_CLASS_PATH = "/sample-data/";

    public static final String SOURCE_RESOURCES_PATTERN = CLASSPATH_ALL_URL_PREFIX + SIMPLE_DATA_CLASS_PATH + "files/**/*.dat";

    private static final Logger logger = LoggerFactory.getLogger(CMSSampleCacheDataInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        copyCMSSampleCacheData(applicationContext);
    }

    private void copyCMSSampleCacheData(ConfigurableApplicationContext context) {

        Environment environment = context.getEnvironment();

        String dataDirPath = getDataDirectoryPath(environment);

        try {
            Resource[] resources = context.getResources(SOURCE_RESOURCES_PATTERN);
            for (Resource resource : resources) {
                String url = resource.getURL().toString();
                String relativePath = StringUtils.substringAfter(url, SIMPLE_DATA_CLASS_PATH);
                File targetFile = new File(dataDirPath, relativePath);
                if (targetFile.exists()) {
                    logger.info("The target CMS data file[path:{}] exists, the sample data is not about to be copied.",
                            targetFile.getAbsolutePath());
                    continue;
                }
                try (InputStream inputStream = resource.getInputStream()) {
                    FileUtils.copyInputStreamToFile(inputStream, targetFile);
                    logger.info("The CMS sample data resource[url:{}] is copied to the target file[path:{}]...",
                            url, targetFile.getAbsolutePath());
                }
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    private String getDataDirectoryPath(Environment environment) {
        return environment.getProperty("user.dir") + File.separator + ".data" + File.separator;
    }
}
