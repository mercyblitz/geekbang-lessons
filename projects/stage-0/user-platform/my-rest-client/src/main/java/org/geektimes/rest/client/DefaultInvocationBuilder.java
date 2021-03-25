package org.geektimes.rest.client;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Stream;

public class DefaultInvocationBuilder implements Invocation.Builder {

    private final UriBuilder uriBuilder;

    private Set<MediaType> mediaTypes = new LinkedHashSet<>();

    private Set<Locale> locales = new LinkedHashSet<>();

    private Set<String> encodings = new LinkedHashSet<>();

    private Set<Cookie> cookies = new LinkedHashSet<>();

    private CacheControl cacheControl;

    private MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    private Map<String, Object> properties = new HashMap<>();

    public DefaultInvocationBuilder(UriBuilder uriBuilder) {
        this.uriBuilder = uriBuilder;
    }

    @Override
    public Invocation.Builder accept(String... mediaTypes) {
        return accept(Stream.of(mediaTypes).map(MediaType::valueOf).toArray(MediaType[]::new));
    }

    @Override
    public Invocation.Builder accept(MediaType... mediaTypes) {
        this.mediaTypes.addAll(Arrays.asList(mediaTypes));
        return this;
    }

    @Override
    public Invocation.Builder acceptLanguage(String... locales) {
        return acceptLanguage(Stream.of(locales).map(Locale::forLanguageTag).toArray(Locale[]::new));
    }

    @Override
    public Invocation.Builder acceptLanguage(Locale... locales) {
        this.locales.addAll(Arrays.asList(locales));
        return this;
    }


    @Override
    public Invocation.Builder acceptEncoding(String... encodings) {
        this.encodings.addAll(Arrays.asList(encodings));
        return this;
    }

    @Override
    public Invocation.Builder cookie(Cookie cookie) {
        cookies.add(cookie);
        return this;
    }

    @Override
    public Invocation.Builder cookie(String name, String value) {
        return cookie(new Cookie(name, value));
    }

    @Override
    public Invocation.Builder cacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
        return this;
    }

    @Override
    public Invocation.Builder header(String name, Object value) {
        this.headers.add(name, value);
        return this;
    }

    @Override
    public Invocation.Builder headers(MultivaluedMap<String, Object> headers) {
        this.headers.putAll(headers);
        return this;
    }

    @Override
    public Invocation.Builder property(String name, Object value) {
        properties.put(name, value);
        return this;
    }

    @Override
    public Response get() {
        return buildGet().invoke();
    }

    @Override
    public <T> T get(Class<T> responseType) {
        return buildGet().invoke(responseType);
    }

    @Override
    public <T> T get(GenericType<T> responseType) {
        return buildGet().invoke(responseType);
    }

    @Override
    public Response put(Entity<?> entity) {
        return buildPut(entity).invoke();
    }

    @Override
    public <T> T put(Entity<?> entity, Class<T> responseType) {
        return buildPut(entity).invoke(responseType);
    }

    @Override
    public <T> T put(Entity<?> entity, GenericType<T> responseType) {
        return buildPut(entity).invoke(responseType);
    }

    @Override
    public Response post(Entity<?> entity) {
        return buildPost(entity).invoke();
    }

    @Override
    public <T> T post(Entity<?> entity, Class<T> responseType) {
        return buildPost(entity).invoke(responseType);
    }

    @Override
    public <T> T post(Entity<?> entity, GenericType<T> responseType) {
        return buildPost(entity).invoke(responseType);
    }

    @Override
    public Response delete() {
        return buildDelete().invoke();
    }

    @Override
    public <T> T delete(Class<T> responseType) {
        return buildDelete().invoke(responseType);
    }

    @Override
    public <T> T delete(GenericType<T> responseType) {
        return buildDelete().invoke(responseType);
    }

    @Override
    public Response head() {
        return null;
    }

    @Override
    public Response options() {
        return null;
    }

    @Override
    public <T> T options(Class<T> responseType) {
        return null;
    }

    @Override
    public <T> T options(GenericType<T> responseType) {
        return null;
    }

    @Override
    public Response trace() {
        return null;
    }

    @Override
    public <T> T trace(Class<T> responseType) {
        return null;
    }

    @Override
    public <T> T trace(GenericType<T> responseType) {
        return null;
    }

    @Override
    public Response method(String name) {
        return build(name).invoke();
    }

    @Override
    public <T> T method(String name, Class<T> responseType) {
        return build(name).invoke(responseType);
    }

    @Override
    public <T> T method(String name, GenericType<T> responseType) {
        return build(name).invoke(responseType);
    }

    @Override
    public Response method(String name, Entity<?> entity) {
        return build(name, entity).invoke();
    }

    @Override
    public <T> T method(String name, Entity<?> entity, Class<T> responseType) {
        return build(name, entity).invoke(responseType);
    }

    @Override
    public <T> T method(String name, Entity<?> entity, GenericType<T> responseType) {
        return build(name, entity).invoke(responseType);
    }

    @Override
    public Invocation build(String method) {
        return build(method, null);
    }

    @Override
    public Invocation build(String method, Entity<?> entity) {
        switch (method) {
            case HttpMethod.GET:
                return buildGet();
            case HttpMethod.POST:
                return buildPost(entity);
            case HttpMethod.PUT:
                return buildPut(entity);
            case HttpMethod.DELETE:
                return buildDelete();
        }
        // TODO : support the more methods
        return null;
    }

    @Override
    public Invocation buildGet() {
        return new HttpGetInvocation(uriBuilder.build(), headers);
    }

    @Override
    public Invocation buildDelete() {
        return null;
    }

    @Override
    public Invocation buildPost(Entity<?> entity) {
        return null;
    }

    @Override
    public Invocation buildPut(Entity<?> entity) {
        return null;
    }

    @Override
    public AsyncInvoker async() {
        return null;
    }

}
