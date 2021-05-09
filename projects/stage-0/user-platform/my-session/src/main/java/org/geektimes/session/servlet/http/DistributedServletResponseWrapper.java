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
package org.geektimes.session.servlet.http;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * The distributed {@link HttpServletResponse} implementation based on {@link HttpServletResponseWrapper}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-28
 */
public class DistributedServletResponseWrapper extends HttpServletResponseWrapper {

    /**
     * Constructs a response object wrapping the given response.
     *
     * @param response HttpServletResponse
     * @throws IllegalArgumentException if the response is null
     */
    public DistributedServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }
}
