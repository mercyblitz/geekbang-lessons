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

/**
 * The annotations of MicroProfile Fault Tolerance based on {@link javax.interceptor.Interceptor @Interceptor}
 * implementations, the priority of annotations as below:
 * <ol>
 *     <li>{@link org.eclipse.microprofile.faulttolerance.CircuitBreaker}</li>
 *     <li>{@link org.eclipse.microprofile.faulttolerance.Bulkhead}</li>
 *     <li>{@link org.eclipse.microprofile.faulttolerance.Fallback}</li>
 *     <li>{@link org.eclipse.microprofile.faulttolerance.Retry}</li>
 *     <li>{@link org.eclipse.microprofile.faulttolerance.Timeout}</li>
 *     <li>{@link org.eclipse.microprofile.faulttolerance.Asynchronous}</li>
 * </ol>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
package org.geektimes.microprofile.faulttolerance;