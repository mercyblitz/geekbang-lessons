package org.geektimes.rest.client;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

public class DefaultUriBuilder extends UriBuilder {

    @Override
    public UriBuilder clone() {
        return null;
    }

    @Override
    public UriBuilder uri(URI uri) {
        return null;
    }

    @Override
    public UriBuilder uri(String uriTemplate) {
        return null;
    }

    @Override
    public UriBuilder scheme(String scheme) {
        return null;
    }

    @Override
    public UriBuilder schemeSpecificPart(String ssp) {
        return null;
    }

    @Override
    public UriBuilder userInfo(String ui) {
        return null;
    }

    @Override
    public UriBuilder host(String host) {
        return null;
    }

    @Override
    public UriBuilder port(int port) {
        return null;
    }

    @Override
    public UriBuilder replacePath(String path) {
        return null;
    }

    @Override
    public UriBuilder path(String path) {
        return null;
    }

    @Override
    public UriBuilder path(Class resource) {
        return null;
    }

    @Override
    public UriBuilder path(Class resource, String method) {
        return null;
    }

    @Override
    public UriBuilder path(Method method) {
        return null;
    }

    @Override
    public UriBuilder segment(String... segments) {
        return null;
    }

    @Override
    public UriBuilder replaceMatrix(String matrix) {
        return null;
    }

    @Override
    public UriBuilder matrixParam(String name, Object... values) {
        return null;
    }

    @Override
    public UriBuilder replaceMatrixParam(String name, Object... values) {
        return null;
    }

    @Override
    public UriBuilder replaceQuery(String query) {
        return null;
    }

    @Override
    public UriBuilder queryParam(String name, Object... values) {
        return null;
    }

    @Override
    public UriBuilder replaceQueryParam(String name, Object... values) {
        return null;
    }

    @Override
    public UriBuilder fragment(String fragment) {
        return null;
    }

    @Override
    public UriBuilder resolveTemplate(String name, Object value) {
        return null;
    }

    @Override
    public UriBuilder resolveTemplate(String name, Object value, boolean encodeSlashInPath) {
        return null;
    }

    @Override
    public UriBuilder resolveTemplateFromEncoded(String name, Object value) {
        return null;
    }

    @Override
    public UriBuilder resolveTemplates(Map<String, Object> templateValues) {
        return null;
    }

    @Override
    public UriBuilder resolveTemplates(Map<String, Object> templateValues, boolean encodeSlashInPath) throws IllegalArgumentException {
        return null;
    }

    @Override
    public UriBuilder resolveTemplatesFromEncoded(Map<String, Object> templateValues) {
        return null;
    }

    @Override
    public URI buildFromMap(Map<String, ?> values) {
        return null;
    }

    @Override
    public URI buildFromMap(Map<String, ?> values, boolean encodeSlashInPath) throws IllegalArgumentException, UriBuilderException {
        return null;
    }

    @Override
    public URI buildFromEncodedMap(Map<String, ?> values) throws IllegalArgumentException, UriBuilderException {
        return null;
    }

    @Override
    public URI build(Object... values) throws IllegalArgumentException, UriBuilderException {
        return null;
    }

    @Override
    public URI build(Object[] values, boolean encodeSlashInPath) throws IllegalArgumentException, UriBuilderException {
        return null;
    }

    @Override
    public URI buildFromEncoded(Object... values) throws IllegalArgumentException, UriBuilderException {
        return null;
    }

    @Override
    public String toTemplate() {
        return null;
    }
}
