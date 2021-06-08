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
package org.geektimes.commons.jndi.file;

import org.apache.commons.io.FileUtils;
import org.geektimes.commons.io.DefaultDeserializer;
import org.geektimes.commons.io.DefaultSerializer;
import org.geektimes.commons.io.Deserializer;
import org.geektimes.commons.io.Serializer;

import javax.naming.*;
import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.geektimes.commons.function.ThrowableAction.execute;
import static org.geektimes.commons.function.ThrowableSupplier.execute;

/**
 * {@link Context} implementation based on File System.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
class FileSystemContext implements Context {

    private static final String CONTEXT_NAME_PREFIX = "java:";

    private static final String ENV_CONTEXT_NAME = "java:comp/env";

    private static final Serializer serializer = new DefaultSerializer();

    private static final Deserializer deserializer = new DefaultDeserializer();

    private final File rootDirectory;

    private final Hashtable<Object, Object> environment;

    FileSystemContext(Hashtable<?, ?> environment) {
        this(getRootDirectory(environment), environment);
    }

    FileSystemContext(File rootDirectory, Hashtable<?, ?> environment) {
        this.rootDirectory = rootDirectory;
        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }
        this.environment = (Hashtable<Object, Object>) environment.clone();
    }

    @Override
    public Object lookup(Name name) throws NamingException {
        return lookup(name.toString());
    }

    @Override
    public Object lookup(String name) throws NamingException {

        int index = name.indexOf(CONTEXT_NAME_PREFIX);

        String targetName = name;
        if (index > -1) { // "java:" prefix was found
            targetName = name.substring(CONTEXT_NAME_PREFIX.length());
        }

        File targetFile = targetFile(targetName);

        if (ENV_CONTEXT_NAME.equals(name) || targetFile.isDirectory()) {
            return createSubContext(targetFile);
        } else if (targetFile.exists()) {
            return lookup(targetFile);
        }
//        throw new UnsupportedOperationException("The target [path : " + targetFile.getAbsolutePath()
//                + "] that is not a file or directory FileSystemContext can't support");
        return null;
    }

    private Object lookup(File targetFile) throws NamingException {
        return execute(() -> {
            byte[] bytes = readFileToByteArray(targetFile);
            return deserializer.deserialize(bytes);
        }, NamingException.class);
    }


    @Override
    public void bind(Name name, Object obj) throws NamingException {
        bind(name.toString(), obj);
    }

    @Override
    public void bind(String name, Object obj) throws NamingException {
        bind(name, obj, false);
    }

    @Override
    public void rebind(Name name, Object obj) throws NamingException {
        rebind(name.toString(), obj);
    }

    @Override
    public void rebind(String name, Object obj) throws NamingException {
        bind(name, obj, true);
    }

    private void bind(String name, Object obj, boolean override) throws NamingException {
        File targetFile = targetFile(name);
        if (override && !targetFile.exists()) {
            return;
        }
        execute(() -> {
            byte[] bytes = serializer.serialize(obj);
            FileUtils.writeByteArrayToFile(targetFile, bytes);
        }, NamingException.class);
    }

    @Override
    public void unbind(Name name) throws NamingException {
        unbind(name.toString());
    }

    @Override
    public void unbind(String name) throws NamingException {
        File targetFile = targetFile(name);
        FileUtils.deleteQuietly(targetFile);
    }

    @Override
    public void rename(Name oldName, Name newName) throws NamingException {
        rename(oldName.toString(), newName.toString());
    }

    @Override
    public void rename(String oldName, String newName) throws NamingException {
        File targetFile = targetFile(oldName);
        targetFile.renameTo(targetFile(newName));
    }

    @Override
    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        return list(name.toString());
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        File targetFile = targetFile(name);
        if (targetFile.exists() && targetFile.isDirectory()) {

        }
        return null;
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        return listBindings(name.toString());
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        return null;
    }

    @Override
    public void destroySubcontext(Name name) throws NamingException {
        destroySubcontext(name.toString());
    }

    @Override
    public void destroySubcontext(String name) throws NamingException {
        FileSystemContext context = (FileSystemContext) createSubcontext(name);
        context.close();
        FileUtils.deleteQuietly(context.rootDirectory);
    }

    @Override
    public Context createSubcontext(Name name) throws NamingException {
        return createSubcontext(name.toString());
    }

    @Override
    public Context createSubcontext(String name) throws NamingException {
        File targetFile = targetFile(name);
        return createSubContext(targetFile);
    }

    @Override
    public Object lookupLink(Name name) throws NamingException {
        return null;
    }

    @Override
    public Object lookupLink(String name) throws NamingException {
        return null;
    }

    @Override
    public NameParser getNameParser(Name name) throws NamingException {
        return getNameParser(name.toString());
    }

    @Override
    public NameParser getNameParser(String name) throws NamingException {
        return CompositeName::new;
    }

    @Override
    public Name composeName(Name name, Name prefix) throws NamingException {
        return null;
    }

    @Override
    public String composeName(String name, String prefix) throws NamingException {
        return null;
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        return environment.put(propName, propVal);
    }

    @Override
    public Object removeFromEnvironment(String propName) throws NamingException {
        return environment.remove(propName);
    }

    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return environment;
    }

    @Override
    public void close() throws NamingException {
        this.environment.clear();
    }

    @Override
    public String getNameInNamespace() throws NamingException {
        return null;
    }

    private File targetFile(String targetName) {
        return new File(rootDirectory, targetName);
    }

    private FileSystemContext createSubContext(File targetFile) {
        return new FileSystemContext(targetFile, this.environment);
    }

    private static File getRootDirectory(Map<?, ?> environment) {
        final File rootDirectory;
        String dirPath = (String) environment.get("jndi.file.root.dir.path");
        if (dirPath != null) {
            rootDirectory = new File(dirPath);
        } else {
            rootDirectory = new File(System.getProperty("java.io.tmpdir"), ".jndi");
        }
        return rootDirectory;
    }
}
