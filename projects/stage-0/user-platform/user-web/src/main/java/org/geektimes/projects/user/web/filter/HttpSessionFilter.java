package org.geektimes.projects.user.web.filter;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class HttpSessionFilter implements Filter {

    private static final String CACHE_URI_PARAM_NAME = "javax.cache.CacheManager.uri";

    private Cache<String, Map> cache;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        String uri = filterConfig.getInitParameter(CACHE_URI_PARAM_NAME);
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create(uri), null);
        // configure the cache
        MutableConfiguration<String, Map> config = new MutableConfiguration<String, Map>()
                .setTypes(String.class, Map.class);

        cache = cacheManager.createCache("http-session-cache", config);

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession httpSession = httpRequest.getSession();
        String sessionId = httpSession.getId();

        Map<String, Object> attributesMap = cache.get(sessionId);
    }

    @Override
    public void destroy() {

    }
}
