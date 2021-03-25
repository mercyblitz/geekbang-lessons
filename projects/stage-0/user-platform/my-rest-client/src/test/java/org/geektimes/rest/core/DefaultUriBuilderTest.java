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
package org.geektimes.rest.core;

import org.geektimes.rest.util.Maps;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class DefaultUriBuilderTest {

    @Test
    public void testBuildFromMap() {
        UriBuilder builder = new DefaultUriBuilder()
                .scheme("http")
                .host("127.0.0.1")
                .port(8080)
                .path("/{a}/{b}/{c}")
                .queryParam("x", "a")
                .queryParam("y", "b", "c")
                .fragment("{d}")
                .resolveTemplates(Maps.of("a", 1, "b", 2, "c", 3, "d", 4));

//        Map<String, Object> values = Maps.of("a", 1, "b", 2, "c", 3, "d", 4);

        URI uri = builder.build();

        assertEquals("http", uri.getScheme());
        assertEquals("127.0.0.1", uri.getHost());
        assertEquals(8080, uri.getPort());
        assertEquals("x=a&y=b&y=c", uri.getRawQuery());
        assertEquals("/1/2/3", uri.getRawPath());
        assertEquals("4", uri.getFragment());

        assertEquals("http://127.0.0.1:8080/1/2/3?x=a&y=b&y=c#4", uri.toString());
    }

    @Test
    public void testBuild() {
        UriBuilder builder = new DefaultUriBuilder()
                .uri("http://127.0.0.1:8080/{a}/{b}/{c}?x=a&y=b&y=c#{d}");

        URI uri = builder.build(1, 2, 3, 4);

        assertEquals("http", uri.getScheme());
        assertEquals("127.0.0.1", uri.getHost());
        assertEquals(8080, uri.getPort());
        assertEquals("x=a&y=b&y=c", uri.getRawQuery());
        assertEquals("/1/2/3", uri.getRawPath());
        assertEquals("4", uri.getFragment());

        assertEquals("http://127.0.0.1:8080/1/2/3?x=a&y=b&y=c#4", uri.toString());
    }

}
