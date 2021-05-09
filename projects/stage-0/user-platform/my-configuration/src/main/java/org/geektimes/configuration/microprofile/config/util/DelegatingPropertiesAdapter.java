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
package org.geektimes.configuration.microprofile.config.util;

import org.eclipse.microprofile.config.Config;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * {@link Properties} Adapter based on {@link Config}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-28
 */
public class DelegatingPropertiesAdapter extends Properties {

    private final Properties delegate;

    public DelegatingPropertiesAdapter(Config config) {
        this.delegate = buildDelegate(config);
    }

    private Properties buildDelegate(Config config) {
        Properties properties = new Properties();
        for (String propertyName : config.getPropertyNames()) {
            properties.put(propertyName, config.getValue(propertyName, Object.class));
        }
        return properties;
    }

    @Override
    public Object setProperty(String key, String value) {
        return delegate.setProperty(key, value);
    }

    @Override
    public void load(Reader reader) throws IOException {
        delegate.load(reader);
    }

    @Override
    public void load(InputStream inStream) throws IOException {
        delegate.load(inStream);
    }

    @Override
    @Deprecated
    public void save(OutputStream out, String comments) {
        delegate.save(out, comments);
    }

    @Override
    public void store(Writer writer, String comments) throws IOException {
        delegate.store(writer, comments);
    }

    @Override
    public void store(OutputStream out, String comments) throws IOException {
        delegate.store(out, comments);
    }

    @Override
    public void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
        delegate.loadFromXML(in);
    }

    @Override
    public void storeToXML(OutputStream os, String comment) throws IOException {
        delegate.storeToXML(os, comment);
    }

    @Override
    public void storeToXML(OutputStream os, String comment, String encoding) throws IOException {
        delegate.storeToXML(os, comment, encoding);
    }

    @Override
    public String getProperty(String key) {
        return delegate.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return delegate.getProperty(key, defaultValue);
    }

    @Override
    public Enumeration<?> propertyNames() {
        return delegate.propertyNames();
    }

    @Override
    public Set<String> stringPropertyNames() {
        return delegate.stringPropertyNames();
    }

    @Override
    public void list(PrintStream out) {
        delegate.list(out);
    }

    @Override
    public void list(PrintWriter out) {
        delegate.list(out);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Enumeration<Object> keys() {
        return delegate.keys();
    }

    @Override
    public Enumeration<Object> elements() {
        return delegate.elements();
    }

    @Override
    public boolean contains(Object value) {
        return delegate.contains(value);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public Object get(Object key) {
        return delegate.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        return delegate.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<?, ?> t) {
        delegate.putAll(t);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Object clone() {
        return delegate.clone();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public Set<Object> keySet() {
        return delegate.keySet();
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public Collection<Object> values() {
        return delegate.values();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        return delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super Object, ? super Object> action) {
        delegate.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super Object, ? super Object, ?> function) {
        delegate.replaceAll(function);
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(key, value);
    }

    @Override
    public boolean replace(Object key, Object oldValue, Object newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(Object key, Object value) {
        return delegate.replace(key, value);
    }

    @Override
    public Object computeIfAbsent(Object key, Function<? super Object, ?> mappingFunction) {
        return delegate.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Object computeIfPresent(Object key, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return delegate.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Object compute(Object key, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return delegate.compute(key, remappingFunction);
    }

    @Override
    public Object merge(Object key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return delegate.merge(key, value, remappingFunction);
    }

}
