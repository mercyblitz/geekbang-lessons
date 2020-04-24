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
package org.geekbang.thinking.in.spring.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * 动态（更新）资源 {@link MessageSource} 实现
 * <p>
 * 实现步骤：
 * <p>
 * 1. 定位资源位置（ Properties 文件）
 * 2. 初始化 Properties 对象
 * 3. 实现 AbstractMessageSource#resolveCode 方法
 * 4. 监听资源文件（Java NIO 2 WatchService）
 * 5. 使用线程池处理文件变化
 * 6. 重新装载 Properties 对象
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see MessageSource
 * @see AbstractMessageSource
 * @since
 */
public class DynamicResourceMessageSource extends AbstractMessageSource implements ResourceLoaderAware {

    private static final String resourceFileName = "msg.properties";

    private static final String resourcePath = "/META-INF/" + resourceFileName;

    private static final String ENCODING = "UTF-8";

    private final Resource messagePropertiesResource;

    private final Properties messageProperties;

    private final ExecutorService executorService;

    private ResourceLoader resourceLoader;


    public DynamicResourceMessageSource() {
        this.messagePropertiesResource = getMessagePropertiesResource();
        this.messageProperties = loadMessageProperties();
        this.executorService = Executors.newSingleThreadExecutor();
        // 监听资源文件（Java NIO 2 WatchService）
        onMessagePropertiesChanged();
    }

    private void onMessagePropertiesChanged() {
        if (this.messagePropertiesResource.isFile()) { // 判断是否为文件
            // 获取对应文件系统中的文件
            try {
                File messagePropertiesFile = this.messagePropertiesResource.getFile();
                Path messagePropertiesFilePath = messagePropertiesFile.toPath();
                // 获取当前 OS 文件系统
                FileSystem fileSystem = FileSystems.getDefault();
                // 新建 WatchService
                WatchService watchService = fileSystem.newWatchService();
                // 获取资源文件所在的目录
                Path dirPath = messagePropertiesFilePath.getParent();
                // 注册 WatchService 到 dirPath，并且关心修改事件
                dirPath.register(watchService, ENTRY_MODIFY);
                // 处理资源文件变化（异步）
                processMessagePropertiesChanged(watchService);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 处理资源文件变化（异步）
     *
     * @param watchService
     */
    private void processMessagePropertiesChanged(WatchService watchService) {
        executorService.submit(() -> {
            while (true) {
                WatchKey watchKey = watchService.take(); // take 发生阻塞
                // watchKey 是否有效
                try {
                    if (watchKey.isValid()) {
                        for (WatchEvent event : watchKey.pollEvents()) {
                            Watchable watchable = watchKey.watchable();
                            // 目录路径（监听的注册目录）
                            Path dirPath = (Path) watchable;
                            // 事件所关联的对象即注册目录的子文件（或子目录）
                            // 事件发生源是相对路径
                            Path fileRelativePath = (Path) event.context();
                            if (resourceFileName.equals(fileRelativePath.getFileName().toString())) {
                                // 处理为绝对路径
                                Path filePath = dirPath.resolve(fileRelativePath);
                                File file = filePath.toFile();
                                Properties properties = loadMessageProperties(new FileReader(file));
                                synchronized (messageProperties) {
                                    messageProperties.clear();
                                    messageProperties.putAll(properties);
                                }
                            }
                        }
                    }
                } finally {
                    if (watchKey != null) {
                        watchKey.reset(); // 重置 WatchKey
                    }
                }

            }
        });
    }

    private Properties loadMessageProperties() {
        EncodedResource encodedResource = new EncodedResource(this.messagePropertiesResource, ENCODING);
        try {
            return loadMessageProperties(encodedResource.getReader());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Properties loadMessageProperties(Reader reader) {
        Properties properties = new Properties();
        try {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return properties;
    }

    private Resource getMessagePropertiesResource() {
        ResourceLoader resourceLoader = getResourceLoader();
        Resource resource = resourceLoader.getResource(resourcePath);
        return resource;
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String messageFormatPattern = messageProperties.getProperty(code);
        if (StringUtils.hasText(messageFormatPattern)) {
            return new MessageFormat(messageFormatPattern, locale);
        }
        return null;
    }

    private ResourceLoader getResourceLoader() {
        return this.resourceLoader != null ? this.resourceLoader : new DefaultResourceLoader();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public static void main(String[] args) throws InterruptedException {
        DynamicResourceMessageSource messageSource = new DynamicResourceMessageSource();
        for (int i = 0; i < 10000; i++) {
            String message = messageSource.getMessage("name", new Object[]{}, Locale.getDefault());
            System.out.println(message);
            Thread.sleep(1000L);
        }
    }
}
