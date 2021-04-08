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
package org.geektimes.cache.integration;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.*;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * File-based {@link FallbackStorage}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
public class FileFallbackStorage extends AbstractFallbackStorage<Object, Object> {

    private static final File CACHE_FALLBACK_DIRECTORY = new File(".cache/fallback/");

    private final Logger logger = Logger.getLogger(getClass().getName());

    static {
        if (!CACHE_FALLBACK_DIRECTORY.exists() && !CACHE_FALLBACK_DIRECTORY.mkdirs()) {
            throw new RuntimeException(format("The fallback directory[path:%s] can't be created!"));
        }
    }

    public FileFallbackStorage() {
        super(Integer.MAX_VALUE);
    }

    File toStorageFile(Object key) {
        return new File(CACHE_FALLBACK_DIRECTORY, key.toString() + ".dat");
    }

    @Override
    public Object load(Object key) throws CacheLoaderException {
        File storageFile = toStorageFile(key);
        if (!storageFile.exists() || !storageFile.canRead()) {
            logger.warning(format("The storage file[path:%s] does not exist or can't be read, " +
                    "thus the value can't be loaded.", storageFile.getAbsolutePath()));
            return null;
        }
        Object value = null;
        try (FileInputStream inputStream = new FileInputStream(storageFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            value = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.severe(format("The deserialization of value[%s] is failed, caused by :%s",
                    value, e.getMessage()));
        }
        return value;
    }

    @Override
    public void write(Cache.Entry<?, ?> entry) throws CacheWriterException {
        Object key = entry.getKey();
        Object value = entry.getValue();
        File storageFile = toStorageFile(key);
        if (storageFile.exists() && !storageFile.canWrite()) {
            logger.warning(format("The storage file[path:%s] can't be written, " +
                    "thus the entry will not be stored.", storageFile.getAbsolutePath()));
            return;
        }
        try (FileOutputStream outputStream = new FileOutputStream(storageFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        ) {
            objectOutputStream.writeObject(value);
        } catch (IOException e) {
            logger.severe(format("The serialization of value[%s] is failed, caused by :%s", value, e.getMessage()));
        }
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        File storageFile = toStorageFile(key);
        storageFile.delete();
    }

}