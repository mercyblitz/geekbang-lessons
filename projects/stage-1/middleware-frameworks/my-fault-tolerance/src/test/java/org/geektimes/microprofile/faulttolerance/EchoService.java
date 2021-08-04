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
package org.geektimes.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.*;

import java.util.concurrent.Future;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * EchoService
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Bulkhead(value = 1)
public class EchoService {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Timeout
    public void echo(String message) {
        echo((Object) message);
    }

    @Asynchronous
    public Future<Void> echo(Object message) {
        logger.info(format("[%s] - echo : %s", Thread.currentThread().getName(), message));
        return null;
    }

    @Retry(maxRetries = 3,
            delay = 0, maxDuration = 0, jitter = 0,
            retryOn = UnsupportedOperationException.class)
    @Fallback(fallbackMethod = "fallback")
    public String echo(Long value) {
        throw new UnsupportedOperationException();
    }

    public String fallback(Long value) {
        return String.valueOf(value);
    }
}
