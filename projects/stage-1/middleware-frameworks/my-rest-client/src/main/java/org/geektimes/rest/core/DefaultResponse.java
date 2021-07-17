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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.*;

import static org.geektimes.rest.util.URLUtils.DEFAULT_ENCODING;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class DefaultResponse extends Response {

    private int status;

    private Object entity;

    private Annotation[] annotations;

    private Set<String> allowedMethods;

    private CacheControl cacheControl;

    private String encoding = DEFAULT_ENCODING;

    private MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    private Locale locale;

    private MediaType mediaType;

    private List<Variant> variants = new LinkedList<>();

    private URI contentLocation;

    private List<NewCookie> newCookies = new LinkedList<>();

    private Map<String, NewCookie> cookies = new HashMap<>();

    private Date date;

    private Date expires;

    private Date lastModified;

    private URI location;

    private EntityTag entityTag;

    private Set<Link> links = new LinkedHashSet<>();

    private HttpURLConnection connection;

    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }

    public void setAllowedMethods(Set<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public void setCacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setHeaders(MultivaluedMap<String, Object> headers) {
        this.headers = headers;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    public void setContentLocation(URI contentLocation) {
        this.contentLocation = contentLocation;
    }

    public void setNewCookies(List<NewCookie> newCookies) {
        this.newCookies = newCookies;
    }

    public void setCookies(Map<String, NewCookie> cookies) {
        this.cookies = cookies;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setLocation(URI location) {
        this.location = location;
    }

    public void setEntityTag(EntityTag entityTag) {
        this.entityTag = entityTag;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public StatusType getStatusInfo() {
        return null;
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public <T> T readEntity(Class<T> entityType) {
        T entity = null;
        try (InputStream inputStream = connection.getInputStream()) {
            // 参考 HttpMessageConverter 实现，实现运行时动态判断
            if (String.class.equals(entityType)) {
                Object value = IOUtils.toString(inputStream, encoding);
                entity = (T) value;
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                entity = objectMapper.readValue(new InputStreamReader(inputStream, encoding), entityType);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }
        return entity;
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType) {
        return null;
    }

    @Override
    public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
        return null;
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
        return null;
    }

    @Override
    public boolean hasEntity() {
        return entity != null;
    }

    @Override
    public boolean bufferEntity() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public MediaType getMediaType() {
        return mediaType;
    }

    @Override
    public Locale getLanguage() {
        return locale;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }

    @Override
    public Map<String, NewCookie> getCookies() {
        return cookies;
    }

    @Override
    public EntityTag getEntityTag() {
        return entityTag;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public URI getLocation() {
        return location;
    }

    @Override
    public Set<Link> getLinks() {
        return links;
    }

    @Override
    public boolean hasLink(String relation) {
        return false;
    }

    @Override
    public Link getLink(String relation) {
        return null;
    }

    @Override
    public Link.Builder getLinkBuilder(String relation) {
        return null;
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getStringHeaders() {
        return null;
    }

    @Override
    public String getHeaderString(String name) {
        return null;
    }
}
