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
package sun.net.www.protocol.classpath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * "classpath" protocol {@link URLStreamHandler} implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see URL
 * @see URLStreamHandler
 * @since 1.0.0
 */
public class Handler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(URL resource) throws IOException {
        return new ClassPathURLConnection(resource);
    }
}

class ClassPathURLConnection extends URLConnection {

    /**
     * Constructs a URL connection to the specified URL. A connection to
     * the object referenced by the URL is not created.
     *
     * @param resource the specified URL.
     */
    protected ClassPathURLConnection(URL resource) {
        super(resource);
    }

    @Override
    public void connect() throws IOException {
    }

    public InputStream getInputStream() throws IOException {
        URL resource = getURL();
        String resourcePath = resource.getPath();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }
        return classLoader.getResourceAsStream(resourcePath);
    }
}
