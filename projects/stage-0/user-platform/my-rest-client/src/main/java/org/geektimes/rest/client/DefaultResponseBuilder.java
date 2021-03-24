package org.geektimes.rest.client;

import javax.ws.rs.core.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DefaultResponseBuilder extends Response.ResponseBuilder {

    @Override
    public Response build() {
        return null;
    }

    @Override
    public Response.ResponseBuilder clone() {
        return null;
    }

    @Override
    public Response.ResponseBuilder status(int status) {
        return null;
    }

    @Override
    public Response.ResponseBuilder entity(Object entity) {
        return null;
    }

    @Override
    public Response.ResponseBuilder entity(Object entity, Annotation[] annotations) {
        return null;
    }

    @Override
    public Response.ResponseBuilder allow(String... methods) {
        return null;
    }

    @Override
    public Response.ResponseBuilder allow(Set<String> methods) {
        return null;
    }

    @Override
    public Response.ResponseBuilder cacheControl(CacheControl cacheControl) {
        return null;
    }

    @Override
    public Response.ResponseBuilder encoding(String encoding) {
        return null;
    }

    @Override
    public Response.ResponseBuilder header(String name, Object value) {
        return null;
    }

    @Override
    public Response.ResponseBuilder replaceAll(MultivaluedMap<String, Object> headers) {
        return null;
    }

    @Override
    public Response.ResponseBuilder language(String language) {
        return null;
    }

    @Override
    public Response.ResponseBuilder language(Locale language) {
        return null;
    }

    @Override
    public Response.ResponseBuilder type(MediaType type) {
        return null;
    }

    @Override
    public Response.ResponseBuilder type(String type) {
        return null;
    }

    @Override
    public Response.ResponseBuilder variant(Variant variant) {
        return null;
    }

    @Override
    public Response.ResponseBuilder contentLocation(URI location) {
        return null;
    }

    @Override
    public Response.ResponseBuilder cookie(NewCookie... cookies) {
        return null;
    }

    @Override
    public Response.ResponseBuilder expires(Date expires) {
        return null;
    }

    @Override
    public Response.ResponseBuilder lastModified(Date lastModified) {
        return null;
    }

    @Override
    public Response.ResponseBuilder location(URI location) {
        return null;
    }

    @Override
    public Response.ResponseBuilder tag(EntityTag tag) {
        return null;
    }

    @Override
    public Response.ResponseBuilder tag(String tag) {
        return null;
    }

    @Override
    public Response.ResponseBuilder variants(Variant... variants) {
        return null;
    }

    @Override
    public Response.ResponseBuilder variants(List<Variant> variants) {
        return null;
    }

    @Override
    public Response.ResponseBuilder links(Link... links) {
        return null;
    }

    @Override
    public Response.ResponseBuilder link(URI uri, String rel) {
        return null;
    }

    @Override
    public Response.ResponseBuilder link(String uri, String rel) {
        return null;
    }
}
