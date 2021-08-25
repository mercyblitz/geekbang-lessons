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
package org.geektimes.enterprise.inject.standard.beans;

/**
 * The type of bean archives :
 * <ul>
 *     <li>Explicit Bean Archive</li>
 *     <li>Implicit Bean Archive</li>
 *     <li>Other Bean Archive</li>
 * </ul>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public enum BeanArchiveType {

    /**
     * An explicit bean archive is an archive which contains a beans.xml file:
     * <ul>
     *     <li>with a version number of 1.1 (or later), with the bean-discovery-mode of all</li>
     *     <li>with no version number</li>
     *     <li>that is an empty file</li>
     * </ul>
     */
    EXPLICIT,

    /**
     * An archive which doesn’t contain a beans.xml file can’t be discovered as an implicit bean archive unless:
     *
     * <ul>
     *     <li>the application is launched with system property <code>"javax.enterprise.inject.scan.implicit"</code>
     *     set to <code>true</code>
     *     </li>
     *     <li>the container was initialized with a map containing an entry parameter with
     *      <code>"javax.enterprise.inject.scan.implicit"</code> as key and Boolean.TRUE as value.</li>
     * </ul>
     */
    IMPLICIT,

    /**
     * An archive which:
     * <ul>
     *  <li>contains a beans.xml file with the bean-discovery-mode of "none"</li>
     *  <li>contains an extension and no beans.xml file</li>
     * </ul>
     * <p>
     * is not a bean archive.
     */
    OTHER


}
