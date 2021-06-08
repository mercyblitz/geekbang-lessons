/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geektimes.rest.util;

import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static java.lang.String.valueOf;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.geektimes.rest.util.PathUtils.ENCODED_SLASH;
import static org.geektimes.rest.util.PathUtils.SLASH;

/**
 * URL Utilities
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public interface URLUtils {

    String DEFAULT_ENCODING = System.getProperty("org.geektimes.url.encoding", "UTF-8");

    String AND = "&";

    String EQUAL = "=";

    String TEMPLATE_VARIABLE_START = "{";

    String TEMPLATE_VARIABLE_END = "}";

    static String encode(String content) {
        return encode(content, DEFAULT_ENCODING);
    }

    static String encode(String content, String encoding) {
        String encodedContent = null;
        try {
            encodedContent = URLEncoder.encode(content, encoding);
        } catch (UnsupportedEncodingException | NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
        return encodedContent;
    }

    static Map<String, Object> encodeSlash(Map<String, ?> templateValues, boolean encodeSlashInPath) {

        final Map<String, Object> encodedSlashTemplateValues;

        if (encodeSlashInPath) {
            encodedSlashTemplateValues = new HashMap<>();
            for (Map.Entry<String, ?> entry : templateValues.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    String str = (String) value;
                    value = StringUtils.replace(str, SLASH, ENCODED_SLASH);
                }
                encodedSlashTemplateValues.put(name, value);
            }
        } else {
            encodedSlashTemplateValues = (Map<String, Object>) templateValues;
        }

        return encodedSlashTemplateValues;
    }


    static MultivaluedMap<String, String> resolveParams(MultivaluedMap<String, String> params,
                                                        Map<String, ?> templateValues, boolean encoded) {

        MultivaluedMap<String, String> resolvedParams = new MultivaluedHashMap<>();

        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            String name = entry.getKey();
            String resolvedName = resolveVariables(name, templateValues, encoded);

            for (String element : entry.getValue()) {
                resolvedParams.add(resolvedName, resolveVariables(element, templateValues, encoded));
            }
        }

        return resolvedParams;
    }

    static Map<String, Object> toTemplateVariables(String template, Object... values) {
        if (isBlank(template)) {
            return emptyMap();
        }

        int start = 0;
        int end = 0;

        int index = 0;

        final int length = values == null ? 0 : values.length;

        Map<String, Object> templateVariables = new LinkedHashMap<>();

        for (; ; ) {

            start = template.indexOf(TEMPLATE_VARIABLE_START, end);
            end = template.indexOf(TEMPLATE_VARIABLE_END, start);

            if (start == -1 || end == -1) {
                break;
            }

            String variableName = template.substring(start + 1, end);

            if (!templateVariables.containsKey(variableName)) {

                Object variableValue = index < length ? values[index++] : null;

                templateVariables.put(variableName, variableValue);
            }
        }

        return unmodifiableMap(templateVariables);
    }

    static String resolveVariables(String template, Object[] templateValues, boolean encoded) {
        return resolveVariables(template, toTemplateVariables(template, templateValues), encoded);
    }

    static String resolveVariables(String template, Map<String, ?> templateValues, boolean encoded) {
        if (isBlank(template)) {
            return null;
        }

        if (templateValues == null || templateValues.isEmpty()) {
            return template;
        }

        StringBuilder resolvedTemplate = new StringBuilder(template);

        int start = 0;
        int end = 0;

        for (; ; ) {

            start = resolvedTemplate.indexOf(TEMPLATE_VARIABLE_START, end);
            end = resolvedTemplate.indexOf(TEMPLATE_VARIABLE_END, start);

            if (start == -1 || end == -1) {
                break;
            }

            String variableName = resolvedTemplate.substring(start + 1, end);

            Object value = templateValues.get(variableName);

            if (value == null) { // variable not found, go to next
                continue;
            }

            String variableValue = valueOf(value);
            if (encoded) {
                variableValue = encode(variableValue);
            }

            resolvedTemplate.replace(start, end + 1, variableValue);
        }

        return resolvedTemplate.toString();
    }

    static Map<String, List<String>> resolveParameters(String queryString) {
        if (isNotBlank(queryString)) {
            Map<String, List<String>> parametersMap = new LinkedHashMap();
            String[] queryParams = StringUtils.split(queryString, AND);
            if (queryParams != null) {
                for (String queryParam : queryParams) {
                    String[] paramNameAndValue = StringUtils.split(queryParam, EQUAL);
                    if (paramNameAndValue.length > 0) {
                        String paramName = paramNameAndValue[0];
                        String paramValue = paramNameAndValue.length > 1 ? paramNameAndValue[1] : StringUtils.EMPTY;
                        List<String> paramValueList = parametersMap.get(paramName);
                        if (paramValueList == null) {
                            paramValueList = new LinkedList<>();
                            parametersMap.put(paramName, paramValueList);
                        }
                        paramValueList.add(paramValue);
                    }
                }
            }
            return unmodifiableMap(parametersMap);
        }
        return emptyMap();
    }

    static String toQueryString(Map<String, List<String>> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return null;
        }

        StringBuilder queryStringBuilder = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            String paramName = entry.getKey();
            for (String paramValue : entry.getValue()) {
                queryStringBuilder.append(paramName).append(EQUAL).append(paramValue).append(AND);
            }
        }
        // remove last "&"
        return queryStringBuilder.substring(0, queryStringBuilder.length() - 1);
    }


}
