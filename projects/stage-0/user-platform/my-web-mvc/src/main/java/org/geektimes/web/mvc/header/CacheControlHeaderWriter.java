package org.geektimes.web.mvc.header;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CacheControlHeaderWriter implements HeaderWriter {

    @Override
    public void write(Map<String, List<String>> headers, String... headerValues) {
        headers.put("cache-control", Arrays.asList(headerValues));
    }
}
