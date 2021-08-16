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
package org.geektimes.commons.constants;

import static java.lang.System.getProperty;

/**
 * System Constants
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface SystemConstants {

    String JAVA_VERSION = getProperty("java.version");

    String JAVA_VENDOR = getProperty("java.vendor");

    String JAVA_HOME = getProperty("java.home");

    String JAVA_CLASS_VERSION = getProperty("java.class.version");

    String JAVA_CLASS_PATH = getProperty("java.class.path");

    String FILE_SEPARATOR = getProperty("file.separator");

    String PATH_SEPARATOR = getProperty("path.separator");

    String LINE_SEPARATOR = getProperty("line.separator");

    String USER_NAME = getProperty("user.name");

    String USER_HOME = getProperty("user.home");

    String USER_DIR = getProperty("user.dir");

    String JAVA_IO_TMPDIR = getProperty("java.io.tmpdir");
}
