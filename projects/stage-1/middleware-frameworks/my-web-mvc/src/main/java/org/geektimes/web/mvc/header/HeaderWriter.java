package org.geektimes.web.mvc.header;

import java.util.List;
import java.util.Map;

public interface HeaderWriter {

    void write(Map<String, List<String>> headers,String... headerValues);
}
