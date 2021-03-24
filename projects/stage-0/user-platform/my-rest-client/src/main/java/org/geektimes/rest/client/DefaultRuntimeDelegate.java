package org.geektimes.rest.client;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.RuntimeDelegate;

public class DefaultRuntimeDelegate extends RuntimeDelegate {

    @Override
    public UriBuilder createUriBuilder() {
        return null;
    }

    @Override
    public Response.ResponseBuilder createResponseBuilder() {
        return null;
    }

    @Override
    public Variant.VariantListBuilder createVariantListBuilder() {
        return null;
    }

    @Override
    public <T> T createEndpoint(Application application, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
        return null;
    }

    @Override
    public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Link.Builder createLinkBuilder() {
        return null;
    }
}
