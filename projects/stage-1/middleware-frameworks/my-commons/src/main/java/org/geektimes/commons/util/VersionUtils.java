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
package org.geektimes.commons.util;

import org.geektimes.commons.lang.util.StringUtils;

import java.util.Comparator;

import static org.geektimes.commons.constants.Constants.DOT;
import static org.geektimes.commons.lang.util.StringUtils.replace;

/**
 * The utilities for version
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class VersionUtils {

    public static final String DEFAULT_VERSION_SEPARATOR = DOT;


    private static class StringVersionComparator implements Comparator<String> {

        private final String versionSeparator;

        private StringVersionComparator(String versionSeparator) {
            this.versionSeparator = versionSeparator;
        }

        @Override
        public int compare(String v1, String v2) {
            v1 = replace(v1, versionSeparator, "");
            v2 = replace(v2, versionSeparator, "");
            return Integer.decode(v1).compareTo(Integer.decode(v2));
        }
    }
}
