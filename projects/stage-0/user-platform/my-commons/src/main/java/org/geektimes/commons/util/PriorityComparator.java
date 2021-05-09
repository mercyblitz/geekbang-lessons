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

import javax.annotation.Priority;
import java.util.Comparator;
import java.util.Objects;

import static org.geektimes.commons.reflect.util.ClassUtils.findAnnotation;

/**
 * The {@link Comparator} for the annotation {@link Priority}
 * <p>
 * The less value of {@link Priority}, the more priority
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Priority
 * @since 1.0.0
 */
public class PriorityComparator implements Comparator<Object> {

    private static final Class<Priority> PRIORITY_CLASS = Priority.class;

    /**
     * Singleton instance of {@link PriorityComparator}
     */
    public static final PriorityComparator INSTANCE = new PriorityComparator();

    @Override
    public int compare(Object o1, Object o2) {
        return compare(o1.getClass(), o2.getClass());
    }

    public static int compare(Class<?> type1, Class<?> type2) {
        if (Objects.equals(type1, type2)) {
            return 0;
        }

        Priority priority1 = findAnnotation(type1, PRIORITY_CLASS);
        Priority priority2 = findAnnotation(type2, PRIORITY_CLASS);

        if (priority1 != null && priority2 != null) {
            return Integer.compare(priority1.value(), priority2.value());
        } else if (priority1 != null && priority2 == null) {
            return -1;
        } else if (priority1 == null && priority2 != null) {
            return 1;
        }
        // else
        return 0;
    }

}
