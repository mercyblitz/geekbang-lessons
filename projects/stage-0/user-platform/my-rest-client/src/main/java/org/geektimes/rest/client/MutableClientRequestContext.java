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
package org.geektimes.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.*;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;

import static java.lang.String.valueOf;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class MutableClientRequestContext implements ClientRequestContext {

    private URI uri;

    private String method;

    private MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    private Map<String, Object> properties = new HashMap<>();

    private final Date date;

    public MutableClientRequestContext() {
        this.date = new Date();
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public Collection<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public void setProperty(String name, Object object) {
        properties.put(name, object);
    }

    @Override
    public void removeProperty(String name) {
        properties.remove(name);
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public MultivaluedMap<String, Object> getHeaders() {
        return headers;
    }

    @Override
    public MultivaluedMap<String, String> getStringHeaders() {
        return null;
    }

    @Override
    public String getHeaderString(String name) {
        Object headerValue = headers.getFirst(name);
        return headerValue == null ? null : headerValue.toString();
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public Locale getLanguage() {
        return null;
    }

    @Override
    public MediaType getMediaType() {
        return null;
    }

    @Override
    public List<MediaType> getAcceptableMediaTypes() {
        return null;
    }

    @Override
    public List<Locale> getAcceptableLanguages() {
        return null;
    }

    @Override
    public Map<String, Cookie> getCookies() {
        return null;
    }

    @Override
    public boolean hasEntity() {
        return false;
    }

    @Override
    public Object getEntity() {
        return null;
    }

    @Override
    public Class<?> getEntityClass() {
        return null;
    }

    @Override
    public Type getEntityType() {
        return null;
    }

    @Override
    public void setEntity(Object entity) {

    }

    @Override
    public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType) {

    }

    @Override
    public Annotation[] getEntityAnnotations() {
        return new Annotation[0];
    }

    @Override
    public OutputStream getEntityStream() {
        return null;
    }

    @Override
    public void setEntityStream(OutputStream outputStream) {

    }

    @Override
    public Client getClient() {
        return null;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public void abortWith(Response response) {

    }
}
