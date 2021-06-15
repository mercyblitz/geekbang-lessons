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
package org.geektimes.projects.servlet.wrapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class MyServletRequestWrapper extends ServletRequestWrapper {
    /**
     * Creates a ServletRequest adaptor wrapping the given request object.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public MyServletRequestWrapper(ServletRequest request) {
        super(request);
    }

    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream rawInputStream = super.getInputStream();
        return new MyServletInputStream(rawInputStream);
    }


}

class MyServletInputStream extends ServletInputStream {

    private ByteArrayInputStream inputStream;

    public MyServletInputStream(ServletInputStream source) {
        byte[] bytes = readAsBytes(source);
        inputStream = new ByteArrayInputStream(bytes);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    private byte[] readAsBytes(ServletInputStream rawInputStream) {
        return new byte[0];
    }
}