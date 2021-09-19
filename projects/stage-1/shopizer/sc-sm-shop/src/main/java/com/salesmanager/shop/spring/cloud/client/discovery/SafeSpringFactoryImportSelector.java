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
package com.salesmanager.shop.spring.cloud.client.discovery;

import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Safe {@link SpringFactoryImportSelector}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class SafeSpringFactoryImportSelector<A extends Annotation> extends SpringFactoryImportSelector<A> {

    @Override
    public final String[] selectImports(AnnotationMetadata metadata) {
        List<String> importedClassNames = new LinkedList<>();
        if (!isOverrideSpringFactoryImports()) {
            String[] imports = super.selectImports(metadata);
            importedClassNames.addAll(asList(imports));
        }
        doSelectImports(metadata, importedClassNames);
        return importedClassNames.toArray(new String[0]);
    }

    protected boolean isOverrideSpringFactoryImports() {
        return false;
    }

    /**
     * Subclass should override this method.
     *
     * @param metadata           {@link AnnotationMetadata}
     * @param importedClassNames the class names to be imported that is mutable
     */
    protected abstract void doSelectImports(AnnotationMetadata metadata, List<String> importedClassNames);
}
