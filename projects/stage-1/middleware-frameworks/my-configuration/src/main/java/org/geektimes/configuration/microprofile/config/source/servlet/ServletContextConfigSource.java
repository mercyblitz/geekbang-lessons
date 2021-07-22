package org.geektimes.configuration.microprofile.config.source.servlet;

import org.geektimes.configuration.microprofile.config.source.EnumerableConfigSource;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

public class ServletContextConfigSource extends EnumerableConfigSource {

    private final ServletContext servletContext;

    public ServletContextConfigSource(ServletContext servletContext) {
        super(format("ServletContext[path:%s] Init Parameters", servletContext.getContextPath()), 500);
        this.servletContext = servletContext;
    }

    @Override
    protected Supplier<Enumeration<String>> namesSupplier() {
        return servletContext::getInitParameterNames;
    }

    @Override
    protected Function<String, String> valueResolver() {
        return servletContext::getInitParameter;
    }
}
