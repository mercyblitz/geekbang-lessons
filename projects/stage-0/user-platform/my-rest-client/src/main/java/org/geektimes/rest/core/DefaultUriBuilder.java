package org.geektimes.rest.core;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;
import static org.geektimes.rest.util.PathUtils.buildPath;
import static org.geektimes.rest.util.PathUtils.resolvePath;
import static org.geektimes.rest.util.URLUtils.*;

public class DefaultUriBuilder extends UriBuilder {

    private String scheme;

    private String schemeSpecificPart;

    private String userInfo;

    private String host;

    private int port;

    private String path;

    private String fragment;

    private String uriTemplate;

    private String resolvedTemplate;

    private MultivaluedMap<String, String> matrixParams = new MultivaluedHashMap<>();

    private MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();

    @Override
    public UriBuilder clone() {
        return new DefaultUriBuilder(this);
    }

    public DefaultUriBuilder() {
    }

    protected DefaultUriBuilder(DefaultUriBuilder other) {
        this.scheme = other.scheme;
        this.schemeSpecificPart = other.schemeSpecificPart;
        this.userInfo = other.userInfo;
        this.host = other.host;
        this.port = other.port;
        this.path = other.path;
        this.fragment = other.fragment;
        this.uriTemplate = other.uriTemplate;
        this.queryParams.putAll(other.queryParams);
        this.matrixParams.putAll(other.matrixParams);
    }

    @Override
    public UriBuilder uri(URI uri) {
        this.scheme = uri.getScheme();
        this.schemeSpecificPart = uri.getRawSchemeSpecificPart();
        this.userInfo = uri.getRawUserInfo();
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.path = uri.getRawPath();
        this.fragment = uri.getRawFragment();
        String query = uri.getRawQuery();
        this.queryParams.clear();
        this.queryParams.putAll(resolveParameters(query));
        return this;
    }

    @Override
    public UriBuilder uri(String uriTemplate) {
        this.uriTemplate = uriTemplate;
        return this;
    }

    @Override
    public UriBuilder scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    @Override
    public UriBuilder schemeSpecificPart(String ssp) {
        this.schemeSpecificPart = ssp;
        return this;
    }

    @Override
    public UriBuilder userInfo(String ui) {
        this.userInfo = ui;
        return this;
    }

    @Override
    public UriBuilder host(String host) {
        this.host = host;
        return this;
    }

    @Override
    public UriBuilder port(int port) {
        this.port = port;
        return this;
    }

    @Override
    public UriBuilder path(String path) {
        this.path = buildPath(this.path, path);
        return this;
    }

    @Override
    public UriBuilder path(Class resource) {
        return path(resolvePath(resource));
    }

    @Override
    public UriBuilder path(Class resource, String method) {
        return path(resolvePath(resource, method));
    }

    @Override
    public UriBuilder path(Method method) {
        return path(resolvePath(method.getDeclaringClass(), method));
    }

    @Override
    public UriBuilder segment(String... segments) {
        this.path = buildPath(path, segments);
        return this;
    }

    @Override
    public UriBuilder matrixParam(String name, Object... values) {
        this.matrixParams.put(name, asList(values));
        return this;
    }

    @Override
    public UriBuilder queryParam(String name, Object... values) {
        this.queryParams.put(name, asList(values));
        return this;
    }

    @Override
    public UriBuilder fragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    @Override
    public UriBuilder resolveTemplate(String name, Object value) {
        return resolveTemplate(name, value, false);
    }

    @Override
    public UriBuilder resolveTemplate(String name, Object value, boolean encodeSlashInPath) {
        return resolveTemplates(singletonMap(name, value), encodeSlashInPath);
    }

    @Override
    public UriBuilder resolveTemplateFromEncoded(String name, Object value) {
        return resolveTemplatesFromEncoded(singletonMap(name, value));
    }

    @Override
    public UriBuilder resolveTemplates(Map<String, Object> templateValues) {
        return resolveTemplates(templateValues, false);
    }

    @Override
    public UriBuilder resolveTemplates(Map<String, Object> templateValues, boolean encodeSlashInPath) throws IllegalArgumentException {
        return doResolveTemplates(encodeSlash(templateValues, encodeSlashInPath), false);
    }

    @Override
    public UriBuilder resolveTemplatesFromEncoded(Map<String, Object> templateValues) {
        return doResolveTemplates(templateValues, false);
    }

    protected UriBuilder doResolveTemplates(Map<String, ?> templateValues, boolean encoded) {
        this.scheme = resolveVariables(this.scheme, templateValues, encoded);
        this.userInfo = resolveVariables(this.userInfo, templateValues, encoded);
        this.host = resolveVariables(this.host, templateValues, encoded);
        this.path = resolveVariables(this.path, templateValues, encoded);
        this.fragment = resolveVariables(this.fragment, templateValues, encoded);
        this.queryParams = resolveParams(this.queryParams, templateValues, encoded);
        this.matrixParams = resolveParams(this.matrixParams, templateValues, encoded);
        this.resolvedTemplate = resolvedTemplate == null ?
                resolveVariables(uriTemplate, templateValues, encoded) :
                resolveVariables(resolvedTemplate, templateValues, encoded);
        return this;
    }

    @Override
    public URI buildFromMap(Map<String, ?> values) {
        return buildFromMap(values, false);
    }

    @Override
    public URI buildFromMap(Map<String, ?> values, boolean encodeSlashInPath) throws IllegalArgumentException, UriBuilderException {
        Map<String, Object> encodedSlashValues = encodeSlash(values, encodeSlashInPath);
        return doBuild(encodedSlashValues, false);
    }

    @Override
    public URI buildFromEncodedMap(Map<String, ?> values) throws IllegalArgumentException, UriBuilderException {
        return doBuild(values, true);
    }

    @Override
    public URI build(Object... values) throws IllegalArgumentException, UriBuilderException {
        return build(values, false);
    }

    @Override
    public URI build(Object[] values, boolean encodeSlashInPath) throws IllegalArgumentException, UriBuilderException {
        return buildFromMap(toTemplateVariables(uriTemplate, values));
    }

    @Override
    public URI buildFromEncoded(Object... values) throws IllegalArgumentException, UriBuilderException {
        return buildFromEncodedMap(toTemplateVariables(uriTemplate, values));
    }

    protected URI doBuild(Map<String, ?> values, boolean encoded) throws IllegalArgumentException, UriBuilderException {
        doResolveTemplates(values, encoded);
        final URI uri;
        if (resolvedTemplate != null) {
            uri = URI.create(resolvedTemplate);
        } else {
            uri = toURI();
        }
        return uri;
    }

    private URI toURI() {
        URI uri = null;
        try {
            if (schemeSpecificPart != null) {
                uri = new URI(scheme, schemeSpecificPart, fragment);
            } else {
                uri = new URI(scheme, userInfo, host, port, path, toQueryString(queryParams), fragment);
            }
        } catch (URISyntaxException e) {
            throw new UriBuilderException();
        }
        return uri;
    }

    @Override
    public String toTemplate() {
        return uriTemplate;
    }

    @Override
    public UriBuilder replacePath(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UriBuilder replaceQuery(String query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UriBuilder replaceQueryParam(String name, Object... values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UriBuilder replaceMatrix(String matrix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UriBuilder replaceMatrixParam(String name, Object... values) {
        throw new UnsupportedOperationException();
    }

    static String[] of(Object... values) {
        return Stream.of(values).toArray(String[]::new);
    }

    static List<String> asList(Object... values) {
        return Arrays.asList(of(values));
    }
}
