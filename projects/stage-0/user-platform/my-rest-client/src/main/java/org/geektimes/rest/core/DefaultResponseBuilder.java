package org.geektimes.rest.core;

import javax.ws.rs.core.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.*;

public class DefaultResponseBuilder extends Response.ResponseBuilder {

    private int status;

    private Object entity;

    private Annotation[] annotations;

    private Set<String> allowedMethods;

    private CacheControl cacheControl;

    private String encoding;

    private MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    private Locale locale;

    private MediaType mediaType;

    private List<Variant> variants = new LinkedList<>();

    private URI contentLocation;

    private List<NewCookie> newCookies = new LinkedList<>();

    private Date expires;

    private Date lastModified;

    private URI location;

    private EntityTag entityTag;

    private List<Link> links = new LinkedList<>();

    @Override
    public Response build() {
        return null;
    }

    public DefaultResponseBuilder() {
    }

    protected DefaultResponseBuilder(DefaultResponseBuilder other) {
        this.status = other.status;
        this.entity = other.entity;
        this.annotations = other.annotations;
        this.allowedMethods = other.allowedMethods;
        this.cacheControl = other.cacheControl;
        this.encoding = other.encoding;
        this.headers.putAll(other.headers);
        this.locale = other.locale;
        this.mediaType = other.mediaType;
        this.variants.addAll(other.variants);
        this.contentLocation = other.contentLocation;
        this.newCookies.addAll(other.newCookies);
        this.expires = other.expires;
        this.lastModified = other.lastModified;
        this.location = other.location;
        this.entityTag = other.entityTag;
        this.links.addAll(other.links);
    }


    @Override
    public Response.ResponseBuilder clone() {
        return new DefaultResponseBuilder(this);
    }

    @Override
    public Response.ResponseBuilder status(int status) {
        this.status = status;
        return this;
    }

    @Override
    public Response.ResponseBuilder entity(Object entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public Response.ResponseBuilder entity(Object entity, Annotation[] annotations) {
        this.entity = entity;
        this.annotations = annotations;
        return this;
    }

    @Override
    public Response.ResponseBuilder allow(String... methods) {
        return allow(methods == null ? null : new LinkedHashSet<>(Arrays.asList(methods)));
    }

    @Override
    public Response.ResponseBuilder allow(Set<String> methods) {
        this.allowedMethods.clear();
        if (methods != null) {
            this.allowedMethods.addAll(methods);
        }
        return this;
    }

    @Override
    public Response.ResponseBuilder cacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
        return this;
    }

    @Override
    public Response.ResponseBuilder encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    @Override
    public Response.ResponseBuilder header(String name, Object value) {
        headers.add(name, value);
        return this;
    }

    @Override
    public Response.ResponseBuilder replaceAll(MultivaluedMap<String, Object> headers) {
        headers.putAll(headers);
        return this;
    }

    @Override
    public Response.ResponseBuilder language(String language) {
        return language(Locale.forLanguageTag(language));
    }

    @Override
    public Response.ResponseBuilder language(Locale language) {
        this.locale = language;
        return this;
    }

    @Override
    public Response.ResponseBuilder type(String type) {
        return type(MediaType.valueOf(type));
    }

    @Override
    public Response.ResponseBuilder type(MediaType type) {
        this.mediaType = type;
        return this;
    }

    @Override
    public Response.ResponseBuilder variant(Variant variant) {
        return variants(variant);
    }

    @Override
    public Response.ResponseBuilder contentLocation(URI location) {
        this.contentLocation = contentLocation;
        return this;
    }

    @Override
    public Response.ResponseBuilder cookie(NewCookie... cookies) {
        this.newCookies.addAll(Arrays.asList(cookies));
        return this;
    }

    @Override
    public Response.ResponseBuilder expires(Date expires) {
        this.expires = expires;
        return this;
    }

    @Override
    public Response.ResponseBuilder lastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    @Override
    public Response.ResponseBuilder location(URI location) {
        this.location = location;
        return this;
    }

    @Override
    public Response.ResponseBuilder tag(String tag) {
        return tag(EntityTag.valueOf(tag));
    }

    @Override
    public Response.ResponseBuilder tag(EntityTag tag) {
        this.entityTag = tag;
        return this;
    }

    @Override
    public Response.ResponseBuilder variants(Variant... variants) {
        return variants(Arrays.asList(variants));
    }

    @Override
    public Response.ResponseBuilder variants(List<Variant> variants) {
        this.variants.addAll(variants);
        return this;
    }

    @Override
    public Response.ResponseBuilder link(URI uri, String rel) {
        return links(Link.fromUri(uri).rel(rel).build());
    }

    @Override
    public Response.ResponseBuilder link(String uri, String rel) {
        return link(URI.create(uri), rel);
    }

    @Override
    public Response.ResponseBuilder links(Link... links) {
        this.links.addAll(Arrays.asList(links));
        return this;
    }
}
