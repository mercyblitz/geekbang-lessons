package org.geektimes.rest.client;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;

public class ImmutableWebTarget implements WebTarget {

    @Override
    public URI getUri() {
        return null;
    }

    @Override
    public UriBuilder getUriBuilder() {
        return null;
    }

    @Override
    public WebTarget path(String path) {
        return null;
    }

    @Override
    public WebTarget resolveTemplate(String name, Object value) {
        return null;
    }

    @Override
    public WebTarget resolveTemplate(String name, Object value, boolean encodeSlashInPath) {
        return null;
    }

    @Override
    public WebTarget resolveTemplateFromEncoded(String name, Object value) {
        return null;
    }

    @Override
    public WebTarget resolveTemplates(Map<String, Object> templateValues) {
        return null;
    }

    @Override
    public WebTarget resolveTemplates(Map<String, Object> templateValues, boolean encodeSlashInPath) {
        return null;
    }

    @Override
    public WebTarget resolveTemplatesFromEncoded(Map<String, Object> templateValues) {
        return null;
    }

    @Override
    public WebTarget matrixParam(String name, Object... values) {
        return null;
    }

    @Override
    public WebTarget queryParam(String name, Object... values) {
        return null;
    }

    @Override
    public Invocation.Builder request() {
        return null;
    }

    @Override
    public Invocation.Builder request(String... acceptedResponseTypes) {
        return null;
    }

    @Override
    public Invocation.Builder request(MediaType... acceptedResponseTypes) {
        return null;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public WebTarget property(String name, Object value) {
        return null;
    }

    @Override
    public WebTarget register(Class<?> componentClass) {
        return null;
    }

    @Override
    public WebTarget register(Class<?> componentClass, int priority) {
        return null;
    }

    @Override
    public WebTarget register(Class<?> componentClass, Class<?>... contracts) {
        return null;
    }

    @Override
    public WebTarget register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
        return null;
    }

    @Override
    public WebTarget register(Object component) {
        return null;
    }

    @Override
    public WebTarget register(Object component, int priority) {
        return null;
    }

    @Override
    public WebTarget register(Object component, Class<?>... contracts) {
        return null;
    }

    @Override
    public WebTarget register(Object component, Map<Class<?>, Integer> contracts) {
        return null;
    }
}
