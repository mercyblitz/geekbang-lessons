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
package org.geektimes.rest.util;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.geektimes.rest.util.URLUtils.resolveVariables;
import static org.geektimes.rest.util.URLUtils.toTemplateVariables;
import static org.junit.Assert.assertEquals;

/**
 * {@link org.geektimes.rest.util.URLUtils}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class URLUtilsTest {

    @Test
    public void testResolveVariables() {
        Map<String, Object> templateValues = new HashMap<>();

        templateValues.put("a", 1);
        templateValues.put("b", 2);
        templateValues.put("c", 3);

        String value = resolveVariables("/{a}/{b}/{c}", templateValues, true);
        assertEquals("/1/2/3", value);

        value = resolveVariables("/{a}/{b}/{c}", emptyMap(), true);
        assertEquals("/{a}/{b}/{c}", value);

        value = resolveVariables("/{a}/{b}/{d}/{c}", templateValues, true);
        assertEquals("/1/2/{d}/3", value);
    }

    @Test
    public void testToTemplateVariables() {

        Map<String, Object> templateVariables = toTemplateVariables(null, null);
        assertEquals(emptyMap(), templateVariables);

        templateVariables = toTemplateVariables("", null);
        assertEquals(emptyMap(), templateVariables);

        templateVariables = toTemplateVariables("     ", null);
        assertEquals(emptyMap(), templateVariables);

        templateVariables = toTemplateVariables("/", null);
        assertEquals(emptyMap(), templateVariables);

        templateVariables = toTemplateVariables("/{a}/{b}/{c}", 1, 2);
        assertEquals(Maps.of("a", 1, "b", 2, "c", null), templateVariables);

        templateVariables = toTemplateVariables("/{a}/{b}/{c}", 1, 2, 3);
        assertEquals(Maps.of("a", 1, "b", 2, "c", 3), templateVariables);

        templateVariables = toTemplateVariables("/{a}/{b}/{b}", 1, 2, 3);
        assertEquals(Maps.of("a", 1, "b", 2), templateVariables);

    }
}
