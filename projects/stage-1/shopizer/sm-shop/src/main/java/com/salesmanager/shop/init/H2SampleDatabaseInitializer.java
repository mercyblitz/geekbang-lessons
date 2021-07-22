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
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * H2 Sample Database Initializer
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ApplicationContextInitializer
 * @since 1.0.0
 */
public class H2SampleDatabaseInitializer implements ApplicationContextInitializer {

    public static final String SAMPLE_H2_DATABASE_RESOURCE_NAME = "classpath:/sample-data/h2/SALESMANAGER.h2.db";

    public static final String TARGET_H2_DATABASE_FILE_NAME = "SALESMANAGER.h2.db";

    private static final Logger logger = LoggerFactory.getLogger(H2SampleDatabaseInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Environment environment = applicationContext.getEnvironment();
        if (environment.acceptsProfiles(Profiles.of("default"))) { // If "default" profile as fallback
            initializeH2SampleDatabase(applicationContext, environment);
        }
    }

    private void initializeH2SampleDatabase(ConfigurableApplicationContext context, Environment environment) {
        // default, ${user.dir} comes from Java System Properties
        // Please refer the configuration in the resource "classpath:database.properties" :
        // db.jdbcUrl=jdbc\:h2\:file\:${user.dir}/SALESMANAGER;
        String dataDirPath = getDataDirectoryPath(environment);
        File targetH2DatabaseFile = new File(dataDirPath, TARGET_H2_DATABASE_FILE_NAME);
        if (targetH2DatabaseFile.exists()) {
            logger.info("The target H2 database file[path:{}] exists, the sample data is not about to be initialized.",
                    targetH2DatabaseFile.getAbsolutePath());
            return;
        }

        // Copy the sample database file to target path
        Resource resource = context.getResource(SAMPLE_H2_DATABASE_RESOURCE_NAME);
        if (!resource.exists()) {
            logger.warn("The H2 sample database resource[{}] is missing！", SAMPLE_H2_DATABASE_RESOURCE_NAME);
            return;
        }

        try (InputStream inputStream = resource.getInputStream()) {
            logger.info("The H2 sample data is initialing into the target database file[path:{}]...",
                    targetH2DatabaseFile.getAbsolutePath());
            FileUtils.copyInputStreamToFile(inputStream, targetH2DatabaseFile);
            logger.info("The H2 sample data is initialized completely!");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String getDataDirectoryPath(Environment environment) {
        return environment.getProperty("user.dir") + File.separator + ".data" + File.separator;
    }
}
