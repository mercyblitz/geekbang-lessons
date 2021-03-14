package org.geektimes.configuration.microprofile.config;


import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.Introspector;
import java.util.*;

public class JavaConfig implements Config {

    /**
     * 内部可变的集合，不要直接暴露在外面
     */
    private final List<ConfigSource> configSources = new LinkedList<>();
    private final Map<String,Converter> converters = new HashMap<>();

    private static Comparator<ConfigSource> configSourceComparator = new Comparator<ConfigSource>() {
        @Override
        public int compare(ConfigSource o1, ConfigSource o2) {
            return Integer.compare(o2.getOrdinal(), o1.getOrdinal());
        }
    };

    public JavaConfig() {
        ClassLoader classLoader = getClass().getClassLoader();

        // 加载ConfigSource
        ServiceLoader<ConfigSource> configSourceServiceLoader = ServiceLoader.load(ConfigSource.class, classLoader);
        configSourceServiceLoader.forEach(configSources::add);
        // 排序
        configSources.sort(configSourceComparator);

        // 加载 Converter
        ServiceLoader<Converter> converterServiceLoader = ServiceLoader.load(Converter.class, classLoader);
        Iterator<Converter> converterIterator = converterServiceLoader.iterator();
        while (converterIterator.hasNext()) {
            Converter converter = converterIterator.next();
            String typeName = ((ParameterizedTypeImpl) converter.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
            converters.put(typeName, converter);
        }
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        // String 转换成目标类型
        Converter converter = converters.get(propertyType.getTypeName());
        if (Objects.nonNull(converter)) {
            return (T) converter.convert(propertyValue);
        }

        return propertyType.cast(propertyValue);
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        return null;
    }

    protected String getPropertyValue(String propertyName) {
        String propertyValue = null;
        for (ConfigSource configSource : configSources) {
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null) {
                break;
            }
        }
        return propertyValue;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T value = getValue(propertyName, propertyType);
        //todo  使用convertor转换
        return Optional.ofNullable(value);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        ArrayList<String> propertyNames = new ArrayList<>(10);
        for (ConfigSource configSource : configSources) {
            propertyNames.addAll(configSource.getPropertyNames());
        }
        return propertyNames;
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return Collections.unmodifiableList(configSources);
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        return Optional.empty();
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }
}
