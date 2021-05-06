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
package org.geektimes.projects.user.mybatis.annotation;

import org.springframework.context.annotation.ImportResource;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 * Date : 2021-05-06
 */
@EnableMyBatis(dataSource = "dataSource",
        configLocation = "classpath*:META-INF/mybatis/mybatis-config.xml",
        mapperLocations = {"classpath*:sample/config/mappers/**/*.xml"},
        environment = "development")
@ImportResource(locations = "classpath*:sample/spring-context.xml") // SqlSessionFactoryBean
public class EnableMyBatisExample {
}
