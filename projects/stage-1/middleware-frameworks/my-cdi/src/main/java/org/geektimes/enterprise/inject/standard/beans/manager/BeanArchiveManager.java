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
package org.geektimes.enterprise.inject.standard.beans.manager;

import org.geektimes.commons.collection.util.CollectionUtils;
import org.geektimes.commons.lang.util.ClassPathUtils;
import org.geektimes.commons.reflect.util.ClassUtils;
import org.geektimes.commons.reflect.util.SimpleClassScanner;
import org.geektimes.commons.util.PriorityComparator;
import org.geektimes.enterprise.inject.standard.beans.BeanArchiveType;
import org.geektimes.enterprise.inject.standard.beans.BeanDiscoveryMode;
import org.geektimes.enterprise.inject.standard.beans.BeanTypeSource;
import org.geektimes.enterprise.inject.standard.beans.xml.BeansReader;
import org.geektimes.enterprise.inject.standard.beans.xml.bind.Alternatives;
import org.geektimes.enterprise.inject.standard.beans.xml.bind.Beans;
import org.geektimes.enterprise.inject.standard.beans.xml.bind.Scan;
import org.geektimes.enterprise.inject.util.Decorators;
import org.geektimes.enterprise.inject.util.Qualifiers;
import org.geektimes.enterprise.inject.util.Stereotypes;
import org.geektimes.interceptor.InterceptorManager;
import org.geektimes.interceptor.util.InterceptorUtils;

import javax.enterprise.context.*;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.DeploymentException;
import javax.interceptor.InterceptorBinding;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static org.geektimes.commons.collection.util.CollectionUtils.newLinkedHashSet;
import static org.geektimes.commons.lang.util.StringUtils.endsWith;
import static org.geektimes.commons.lang.util.StringUtils.isBlank;
import static org.geektimes.commons.util.ServiceLoaders.loadSpi;
import static org.geektimes.enterprise.inject.standard.beans.BeanArchiveType.EXPLICIT;
import static org.geektimes.enterprise.inject.standard.beans.BeanArchiveType.OTHER;
import static org.geektimes.enterprise.inject.standard.beans.BeanDiscoveryMode.ALL;
import static org.geektimes.enterprise.inject.standard.beans.BeanDiscoveryMode.NONE;
import static org.geektimes.enterprise.inject.standard.beans.BeanTypeSource.*;
import static org.geektimes.enterprise.inject.standard.beans.xml.BeansReader.BEANS_XML_RESOURCE_NAME;
import static org.geektimes.interceptor.InterceptorManager.getInstance;


/**
 * Bean archives Manager
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class BeanArchiveManager {

    private static final Predicate<Class<?>> annotationFilter = Class::isAnnotation;

    private static final Predicate<Class<?>> nonAnnotationFilter = annotationFilter.negate();

    private final StandardBeanManager standardBeanManager;

    private final BeansReader beansReader;

    private final SimpleClassScanner classScanner;

    private final InterceptorManager interceptorManager;

    private final Map<BeanTypeSource, List<Class<?>>> beanClasses;

    private final Map<BeanTypeSource, List<Class<?>>> interceptorClasses;

    private final Map<BeanTypeSource, List<Class<?>>> decoratorClasses;

    private final Map<BeanTypeSource, List<Class<?>>> alternativeClasses;

    private final Set<Class<? extends Annotation>> alternativeStereotypeClasses;

    // Synthetic meta-data

    /**
     * Synthetic package names from {@link SeContainerInitializer#addPackages(boolean, Package...)}
     * <p>
     * Key is {@link Package#getName() the package name}, the value is scanRecursively or not
     */
    private final Map<String, Boolean> syntheticPackagesToScan;

    /**
     * Synthetic Qualifiers from {@link BeforeBeanDiscovery#addQualifier(Class)}
     */
    private final Set<Class<? extends Annotation>> syntheticQualifiers;

    /**
     * Synthetic Stereotypes from {@link BeforeBeanDiscovery#addStereotype(Class, Annotation...)}
     * <p>
     * The key is annotation type, the value is meta-annotations
     */
    private final Map<Class<? extends Annotation>, Set<Annotation>> syntheticStereotypes;

    /**
     * Synthetic InterceptorBindings from {@link BeforeBeanDiscovery#addInterceptorBinding(Class, Annotation...)}
     * <p>
     * The key is annotation type of {@link InterceptorBinding}, the value is meta-annotations
     */
    private final Map<Class<? extends Annotation>, Set<Annotation>> syntheticInterceptorBindings;

    private boolean discoveryEnabled;

    private boolean scanImplicitEnabled;

    private volatile boolean discovered;

    public BeanArchiveManager(StandardBeanManager standardBeanManager) {
        this.standardBeanManager = standardBeanManager;

        this.beansReader = loadSpi(BeansReader.class, getClassLoader());
        this.classScanner = SimpleClassScanner.INSTANCE;
        this.interceptorManager = getInstance(getClassLoader());

        this.beanClasses = new TreeMap<>();
        this.interceptorClasses = new TreeMap<>();
        this.decoratorClasses = new TreeMap<>();
        this.alternativeClasses = new TreeMap<>();

        this.alternativeStereotypeClasses = new LinkedHashSet<>();

        this.syntheticPackagesToScan = new TreeMap<>();
        this.syntheticQualifiers = new LinkedHashSet<>();
        this.syntheticStereotypes = new LinkedHashMap<>();
        this.syntheticInterceptorBindings = new LinkedHashMap<>();

        this.discoveryEnabled = true;
        this.scanImplicitEnabled = false;
    }

    public void addSyntheticPackage(Package packageToScan, boolean scanRecursively) {
        requireNonNull(packageToScan, "The 'packageToScan' argument must not be null!");
        addSyntheticPackage(packageToScan.getName(), scanRecursively);
    }

    public void addSyntheticPackage(String packageToScan, boolean scanRecursively) {
        requireNonNull(packageToScan, "The 'packageToScan' argument must not be null!");
        this.syntheticPackagesToScan.put(packageToScan, scanRecursively);
    }

    public void addSyntheticBeanClass(Class<?> beanClass) {
        addBeanType(beanClass, BeanTypeSource.SYNTHETIC, beanClasses);
    }

    public BeanArchiveManager addSyntheticQualifier(Class<? extends Annotation> qualifier) {
        requireNonNull(qualifier, "The 'qualifier' argument must not be null!");
        this.syntheticQualifiers.add(qualifier);
        return this;
    }

    public BeanArchiveManager addSyntheticStereotype(Class<? extends Annotation> stereotype, Annotation... stereotypeDef) {
        syntheticStereotypes.put(stereotype, CollectionUtils.asSet(stereotypeDef));
        return this;
    }

    public BeanArchiveManager addSyntheticInterceptorBinding(Class<? extends Annotation> bindingType, Annotation[] bindingTypeDef) {
        syntheticInterceptorBindings.put(bindingType, CollectionUtils.asSet(bindingTypeDef));
        return this;
    }

//    private BeanArchiveManager excludePackage(String packageToScan, boolean scanRecursively) {
//        requireNonNull(packageToScan, "The 'packageToScan' argument must not be null!");
//        if (this.extendedPackagesToScan.remove(packageToScan, scanRecursively)) {
//            this.extendedPackagesToScan.remove(packageToScan);
//        }
//        return this;
//    }

    private void discoverBeanClasses(Set<Class<?>> discoveredTypes) {
        filterAndHandleBeanTypes(discoveredTypes,
                this::isBeanClass,
                this::addDiscoveredBeanClass);
    }

    private void addDiscoveredBeanClass(Class<?> beanClass) {
        addBeanType(beanClass, BeanTypeSource.DISCOVERED, beanClasses);
    }

    private void addEnabledBeanClass(Class<?> beanClass) {
        addBeanType(beanClass, ENABLED, beanClasses);
    }

    private void addEnabledAlternativeClass(Class<?> alternativeClass) {
        addBeanType(alternativeClass, ENABLED, alternativeClasses);
    }

    public void addDiscoveredAlternativeClass(Class<?> alternativeClass) {
        addBeanType(alternativeClass, DISCOVERED, alternativeClasses);
    }

    public void addSyntheticAlternativeClass(Class<?> alternativeClass) {
        addBeanType(alternativeClass, SYNTHETIC, alternativeClasses);
    }

    private static void addBeanType(Class<?> beanType, BeanTypeSource beanTypeSource,
                                    Map<BeanTypeSource, List<Class<?>>> beanTypesRepository) {
        addBeanType(beanType, beanTypeSource, beanTypesRepository, false);
    }

    private static void addBeanType(Class<?> beanType, BeanTypeSource beanTypeSource,
                                    Map<BeanTypeSource, List<Class<?>>> beanTypesRepository,
                                    boolean sorted) {
        requireNonNull(beanType, "The 'class' argument must not be null!");
        List<Class<?>> beanTypes = beanTypesRepository.computeIfAbsent(beanTypeSource, source -> new LinkedList<>());
        if (!beanTypes.contains(beanType)) {
            beanTypes.add(beanType);
            if (sorted) {
                Collections.sort(beanTypes, PriorityComparator.INSTANCE);
            }
        }
    }

    /**
     * Each child <class> element must specify the name of an interceptor class.
     * If there is no class with the specified name, or if the class with the specified name is not an interceptor class,
     * the container automatically detects the problem and treats it as a deployment problem.
     * <p>
     * If the same class is listed twice under the <interceptors> element, the container automatically detects
     * the problem and treats it as a deployment problem.
     * <p>
     * Interceptors enabled using @Priority are called before interceptors enabled using beans.xml.
     *
     * @param interceptors
     */
    private void addEnabledInterceptorClasses(org.geektimes.enterprise.inject.standard.beans.xml.bind.Interceptors interceptors) {
        if (interceptors != null) {
            List<String> classNames = interceptors.getClazz();
            loadAnnotatedClasses(classNames, javax.interceptor.Interceptor.class)
                    .forEach(this::addEnabledInterceptorClass);
        }
    }

    private void discoverInterceptorClasses(Set<Class<?>> discoveredTypes) {
        filterAndHandleBeanTypes(discoveredTypes,
                interceptorManager::isInterceptorClass,
                this::addDiscoveredInterceptorClass);
    }

    private void addEnabledInterceptorClass(Class<?> interceptorClass) {
        addBeanType(interceptorClass, ENABLED, interceptorClasses, true);
    }

    private void addDiscoveredInterceptorClass(Class<?> interceptorClass) {
        addBeanType(interceptorClass, DISCOVERED, interceptorClasses, true);
    }

    public void addSyntheticInterceptorClass(Class<?> interceptorClass) {
        addBeanType(interceptorClass, SYNTHETIC, interceptorClasses, true);
    }

    /**
     * Each child <class> element must specify the name of a decorator bean class.
     * If there is no class with the specified name, or if the class with the specified name is not
     * a decorator bean class, the container automatically detects the problem and treats it as a deployment problem.
     * <p>
     * If the same class is listed twice under the <decorators> element, the container automatically detects the problem
     * and treats it as a deployment problem.
     *
     * @param decorators
     */
    private void addEnabledDecoratorClasses(org.geektimes.enterprise.inject.standard.beans.xml.bind.Decorators decorators) {
        if (decorators != null) {
            List<String> classNames = decorators.getClazz();
            loadAnnotatedClasses(classNames, javax.decorator.Decorator.class)
                    .forEach(this::addEnabledDecoratorClass);
        }
    }

    private void addEnabledDecoratorClass(Class<?> decoratorClass) {
        addBeanType(decoratorClass, ENABLED, decoratorClasses);
    }

    private void discoverDecoratorClasses(Set<Class<?>> discoveredTypes) {
        filterAndHandleBeanTypes(discoveredTypes,
                Decorators::isDecorator,
                this::addDiscoveredDecoratorClass);
    }

    private void addDiscoveredDecoratorClass(Class<?> decoratorClass) {
        addBeanType(decoratorClass, DISCOVERED, decoratorClasses);
    }

    public void addSyntheticDecoratorClass(Class<?> decoratorClass) {
        addBeanType(decoratorClass, SYNTHETIC, decoratorClasses);
    }

    private void addEnabledAlternativeClasses(Alternatives alternatives) {
//        alternatives.getClazzOrStereotype()
//                .stream()
//                .map(this::)
        // TODO
    }

    private void addEnabledBeanClasses(Set<Class<?>> discoveredTypes, Beans beans) {
        // Trimmed bean archive
        if (!trimBeanArchive(discoveredTypes, beans)) {
            // Add enabled bean classes from the remaining discovered types
            discoveredTypes.forEach(this::addEnabledBeanClass);
        }
    }

    public BeanArchiveManager addAlternativeStereotypeClass(Class<? extends Annotation> alternativeStereotypeClass) {
        requireNonNull(alternativeStereotypeClass, "The 'alternativeStereotypeClass' argument must not be null!");
        this.alternativeStereotypeClasses.add(alternativeStereotypeClass);
        return this;
    }

    private void filterAndHandleBeanTypes(Set<Class<?>> classes,
                                          Predicate<Class<?>> filter,
                                          Consumer<Class<?>> handler) {
        Iterator<Class<?>> iterator = classes.iterator();
        while (iterator.hasNext()) {
            Class<?> discoveredClass = iterator.next();
            if (filter.test(discoveredClass)) {
                if (handler != null) {
                    handler.accept(discoveredClass);
                }
                iterator.remove();
            }
        }
    }


    public boolean isQualifier(Class<? extends Annotation> annotationType) {
        return Qualifiers.isQualifier(annotationType) ||
                // Extensions
                syntheticQualifiers.contains(annotationType);
    }

    public boolean isInterceptorBinding(Class<? extends Annotation> annotationType) {
        return InterceptorUtils.isAnnotatedInterceptorBinding(annotationType) ||
                // Extensions
                syntheticInterceptorBindings.containsKey(annotationType);
    }

    public boolean isStereotype(Class<? extends Annotation> annotationType) {
        return Stereotypes.isStereotype(annotationType) ||
                // Extensions
                syntheticStereotypes.containsKey(annotationType);
    }

    public boolean isBeanClass(Class<?> type) {
        return standardBeanManager.isDefiningAnnotationType(type, false, false);
    }

    public Set<Annotation> getInterceptorBindingDefinition(Class<? extends Annotation> bindingType) {
        return syntheticInterceptorBindings.getOrDefault(bindingType, emptySet());
    }

    public Set<Annotation> getStereotypeDefinition(Class<? extends Annotation> stereotype) {
        return syntheticStereotypes.getOrDefault(stereotype, emptySet());
    }

    /**
     * @return an unmodifiable view of discovered types
     */
    public void discoverTypes() {
        if (!discovered) {
            discoverTypesInExplicitBeanArchives();
            discoverTypesInImplicitBeanArchives();
            discoverTypesInSyntheticBeanArchives();
            discovered = true;
        }
    }

    private void discoverTypesInExplicitBeanArchives() {
        try {
            ClassLoader classLoader = getClassLoader();
            Enumeration<URL> beansXMLResources = classLoader.getResources(BEANS_XML_RESOURCE_NAME);
            while (beansXMLResources.hasMoreElements()) {
                URL beansXMLResource = beansXMLResources.nextElement();
                Beans beans = beansReader.readBeans(beansXMLResource, classLoader);
                BeanArchiveType beanArchiveType = resolveBeanArchiveType(beans);
                switch (beanArchiveType) {
                    case OTHER:
                        // Ignore
                        continue;
                    case EXPLICIT:
                        discoverTypesInExplicitBeanArchive(beansXMLResource, beans);
                        break;
                    case IMPLICIT:
                        discoverTypesInImplicitBeanArchive(beansXMLResource);
                        break;
                }
            }
        } catch (IOException e) {
            throw new DeploymentException(e);
        }
    }

    /**
     * An archive which doesn’t contain a beans.xml file can’t be discovered as an implicit bean archive unless:
     *
     * <ul>
     *     <li>the application is launched with system property <code>"javax.enterprise.inject.scan.implicit"</code>
     *     set to <code>true</code>
     *     </li>
     *     <li>the container was initialized with a map containing an entry parameter with
     *      <code>"javax.enterprise.inject.scan.implicit"</code> as key and Boolean.TRUE as value.</li>
     * </ul>
     */
    private void discoverTypesInImplicitBeanArchives() {
        if (isScanImplicitEnabled()) {
            ClassLoader classLoader = getClassLoader();
            Set<String> classPaths = ClassPathUtils.getClassPaths();
            classPaths.stream().map(File::new).forEach(archiveFile -> {
                Set<Class<?>> discoveredTypes = scan(classLoader, archiveFile);
                discoverDefiningAnnotationBeanTypes(discoveredTypes);
            });
        }
    }


    /**
     * Discover types in the synthetic bean archives by {@link SeContainerInitializer#addPackages}
     *
     * @see SeContainerInitializer#addPackages
     */
    private void discoverTypesInSyntheticBeanArchives() {
        discoverTypesInSyntheticPackages();
    }


    private Set<Class<?>> scan(URL beansXMLResource, Predicate<Class<?>>... classFilters) {
        return new LinkedHashSet<>(classScanner.scan(getClassLoader(), beansXMLResource, true, classFilters));
    }

    private Set<Class<?>> scan(ClassLoader classLoader, File archiveFile, Predicate<Class<?>>... classFilters) {
        return new LinkedHashSet<>(classScanner.scan(classLoader, archiveFile, true, classFilters));
    }

    /**
     * An explicit bean archive is an archive which contains a beans.xml file:
     * <ul>
     *     <li>with a version number of 1.1 (or later), with the bean-discovery-mode of all</li>
     *     <li>with no version number</li>
     *     <li>that is an empty file</li>
     * </ul>
     * <p>
     * Each Java class, interface (excluding the special kind of interface declaration annotation type) or
     * enum deployed in an explicit bean archive
     *
     * @param beansXMLResource the URL represents the {@link BeansReader#BEANS_XML_RESOURCE_NAME Beans XML resource}
     * @param beans            {@link Beans} instance parsed from {@link BeansReader#BEANS_XML_RESOURCE_NAME "META-INF/beans.xml"}            {@link Beans}
     */
    private void discoverTypesInExplicitBeanArchive(URL beansXMLResource, Beans beans) {
        if (isDiscoveryEnabled()) {
            Set<Class<?>> discoveredTypes = scan(beansXMLResource, nonAnnotationFilter);
            // Exclude filters
            excludeFilters(discoveredTypes, beans);
            // Add Enabled Interceptor classes
            addEnabledInterceptorClasses(beans.getInterceptors());
            // Add Enabled Decorator classes
            addEnabledDecoratorClasses(beans.getDecorators());
            // Add Enabled Alternative classes
            addEnabledAlternativeClasses(beans.getAlternatives());
            // Add Enabled Bean classes
            addEnabledBeanClasses(discoveredTypes, beans);
        }
    }


    /**
     * Each Java class with a bean defining annotation in an implicit bean archive.
     *
     * @param beansXMLResource the URL represents the {@link BeansReader#BEANS_XML_RESOURCE_NAME Beans XML resource}
     */
    private void discoverTypesInImplicitBeanArchive(URL beansXMLResource) {
        Set<Class<?>> discoveredTypes = scan(beansXMLResource, ClassUtils::isGeneralClass);
        discoverDefiningAnnotationBeanTypes(discoveredTypes);
    }

    private void discoverTypesInSyntheticPackages() {
        if (isDiscoveryEnabled()) {
            ClassLoader classLoader = getClassLoader();
            Set<Class<?>> discoveredTypes = new LinkedHashSet<>();
            for (Map.Entry<String, Boolean> packageEntry : syntheticPackagesToScan.entrySet()) {
                String packageToDiscovery = packageEntry.getKey();
                boolean scanRecursively = Boolean.TRUE.equals(packageEntry.getValue());
                discoveredTypes.addAll(classScanner.scan(classLoader, packageToDiscovery, scanRecursively, true));
            }
            addSyntheticBeanTypes(discoveredTypes);
        }
    }

    private void addSyntheticBeanTypes(Set<Class<?>> discoveredTypes) {
        addSyntheticInterceptorClasses(discoveredTypes);
        addSyntheticDecoratorClasses(discoveredTypes);
        addSyntheticBeanClasses(discoveredTypes);
    }

    private void addSyntheticInterceptorClasses(Set<Class<?>> discoveredTypes) {
        filterAndHandleBeanTypes(discoveredTypes,
                interceptorManager::isInterceptorClass,
                this::addSyntheticInterceptorClass);
    }

    private void addSyntheticDecoratorClasses(Set<Class<?>> discoveredTypes) {
        filterAndHandleBeanTypes(discoveredTypes,
                Decorators::isDecorator,
                this::addSyntheticDecoratorClass);
    }

    private void addSyntheticBeanClasses(Set<Class<?>> discoveredTypes) {
        filterAndHandleBeanTypes(discoveredTypes,
                this::isBeanClass,
                this::addSyntheticBeanClass);
    }

    /**
     * Exclude filters are defined by &lt;exclude&gt; elements in the beans.xml for the bean archive as children of the
     * &lt;scan&gt; element. By default an exclude filter is active.
     * If the filter is active, and:
     * <ul>
     *     <li>the fully qualified name of the type being discovered matches the value of the name attribute of the
     *     exclude filter</li>
     *     <li>the package name of the type being discovered matches the value of the name attribute with a suffix
     *     ".*" of the exclude filter</li>
     *     <li>the package name of the type being discovered starts with the value of the name attribute with a suffix
     *     ".**" of the exclude filter</li>
     * </ul>
     *
     * @param discoveredTypes the {@link Class types} has been discovered
     * @param beans           {@link Beans} instance parsed from {@link BeansReader#BEANS_XML_RESOURCE_NAME "META-INF/beans.xml"}
     */
    private void excludeFilters(Set<Class<?>> discoveredTypes, Beans beans) {
        Scan scan = beans.getScan();
        List<Scan.Exclude> excludeList = scan.getExclude();
        Map<String, Boolean> packageNamesToExclude = new LinkedHashMap<>(excludeList.size());
        Set<String> classNamesToExclude = new LinkedHashSet<>();
        excludeList.forEach(exclude -> {
            List<Object> conditions = exclude.getIfClassAvailableOrIfClassNotAvailableOrIfSystemProperty();
            if (isConditional(conditions)) {
                String name = exclude.getName();
                if (endsWith(name, ".*")) {
                    packageNamesToExclude.put(name, Boolean.FALSE);
                } else if (endsWith(name, ".**")) {
                    packageNamesToExclude.put(name, Boolean.TRUE);
                } else { // the fully qualified name of the type
                    classNamesToExclude.add(name);
                }
            }
        });

        Iterator<Class<?>> iterator = discoveredTypes.iterator();

        while (iterator.hasNext()) {
            Class<?> discoveredType = iterator.next();

            boolean shouldRemove = false;

            // Exclude class by name
            if (classNamesToExclude.contains(discoveredType.getName())) {
                shouldRemove = true;
            } else {
                // Exclude class by package
                String packageName = discoveredType.getPackage().getName();
                for (Map.Entry<String, Boolean> entry : packageNamesToExclude.entrySet()) {
                    String packageNameToExclude = entry.getKey();
                    Boolean recursive = entry.getValue();
                    if (Boolean.TRUE == recursive) { // no use equals to improve performance
                        if (packageName.startsWith(packageNameToExclude)) {
                            shouldRemove = true;
                            break;
                        }
                    } else {
                        if (packageName.equals(packageNameToExclude)) {
                            shouldRemove = true;
                            break;
                        }
                    }
                }
            }

            if (shouldRemove) {
                iterator.remove();
            }
        }
    }

    /**
     * If an explicit bean archive contains the &lt;trim/&gt; element in its beans.xml file, types that don’t have
     * either a bean defining annotation (as defined in Bean defining annotations) or any scope annotation, are removed
     * from the set of discovered types.
     *
     * @param discoveredTypes the {@link Class types} has been discovered
     * @param beans           {@link Beans} instance parsed from {@link BeansReader#BEANS_XML_RESOURCE_NAME "META-INF/beans.xml"}
     * @return <code>true</code> if &lt;trim/&gt; element presents , <code>false</code> otherwise
     */
    private boolean trimBeanArchive(Set<Class<?>> discoveredTypes, Beans beans) {
        boolean shouldTrim = beans.getTrim() != null;
        if (shouldTrim) {
            discoverDefiningAnnotationBeanTypes(discoveredTypes);
        }
        return shouldTrim;
    }

    /**
     * Discover bean classes may have a bean defining annotation, allowing them to be placed anywhere in an application,
     * as defined in Bean archives.
     * A bean class with a bean defining annotation is said to be an implicit bean.
     * The set of bean defining annotations contains:
     * <ul>
     *     <li>{@link ApplicationScoped @ApplicationScoped}, {@link SessionScoped @SessionScoped},
     *         {@link ConversationScoped @ConversationScoped} and {@link RequestScoped @RequestScoped} annotations
     *     </li>
     *     <li>all other normal scope types</li>
     *     <li>{@link javax.interceptor.Interceptor @Interceptor} and {@link javax.decorator.Decorator @Decorator} annotations</li>
     *     <li>all stereotype annotations (i.e. annotations annotated with {@link Stereotype @Stereotype})</li>
     *     <li>the {@link Dependent @Dependent} scope annotation</li>
     * </ul>
     *
     * @param discoveredTypes the {@link Class types} has been discovered
     */
    private void discoverDefiningAnnotationBeanTypes(Set<Class<?>> discoveredTypes) {
        discoverInterceptorClasses(discoveredTypes);
        discoverDecoratorClasses(discoveredTypes);
        discoverBeanClasses(discoveredTypes);
    }

    /**
     * If the exclude filter definition contains:
     * <ul>
     *     <li>a child element named &lt;if-class-available&gt; with a name attribute, and the classloader for the bean
     *     archive can not load a class for that name</li>
     *     <li>a child element named &lt;if-class-not-available&gt; with a name attribute, and the classloader for the bean
     *     archive can load a class for that name</li>
     *     <li>a child element named &lt;if-system-property&gt; with a name attribute, and there is no system property defined
     *     for that name</li>
     *     <li>a child element named &lt;if-system-property&gt; with a name attribute and a value attribute, and there is
     *     no system property defined for that name with that value.</li>
     * </ul>
     *
     * @param conditions
     * @return
     */
    private boolean isConditional(List<Object> conditions) {
        if (conditions.isEmpty()) {
            return true;
        }

        boolean result = true;

        for (Object condition : conditions) {

            if (condition instanceof Scan.Exclude.IfSystemProperty) {
                Scan.Exclude.IfSystemProperty ifSystemProperty = (Scan.Exclude.IfSystemProperty) condition;
                String name = ifSystemProperty.getName();
                String value = ifSystemProperty.getValue();
                if (value == null) {
                    result = getProperty(name) != null;
                } else {
                    result = Objects.equals(value, getProperty(name));
                }
            } else {
                String className = null;
                boolean available = true;
                if (condition instanceof Scan.Exclude.IfClassAvailable) {
                    className = ((Scan.Exclude.IfClassAvailable) condition).getName();
                } else if (condition instanceof Scan.Exclude.IfClassNotAvailable) {
                    className = ((Scan.Exclude.IfClassNotAvailable) condition).getName();
                    available = false;
                }
                result = isClassAvailable(className, available);
            }

            if (!result) {
                break;
            }
        }
        return result;
    }

    private boolean isClassAvailable(String className, boolean available) {
        return (resolveClass(className) != null) == available;
    }

    private List<Class<?>> loadAnnotatedClasses(List<String> classNames,
                                                Class<? extends Annotation> annotationType) {
        List<Class<?>> classes = new ArrayList<>(classNames.size());
        for (String className : classNames) {
            Class<?> type = loadClass(className);
            if (!type.isAnnotationPresent(annotationType)) {
                String message = format("The class[%s] does not annotate @%s", type.getName(), annotationType.getName());
                throw new DeploymentException(message);
            }
            if (classes.contains(type)) {
                String message = format("The duplicated definition @%s class[%s]!",
                        annotationType.getName(), type.getName());
                throw new DeploymentException(message);
            }
            classes.add(type);
        }
        return classes;
    }

    private Class<?> resolveClass(String className) {
        return ClassUtils.resolveClass(className, getClassLoader());
    }

    private Class<?> loadClass(String className) throws IllegalArgumentException {
        try {
            ClassLoader classLoader = getClassLoader();
            return ClassUtils.forName(className, classLoader);
        } catch (ClassNotFoundException e) {
            String message = format("The class[name : %s] can't be found!", className);
            throw new DeploymentException(message, e);
        }
    }

    /**
     * A bean archive which contains a beans.xml file with no version has a default bean discovery mode of all.
     * A bean archive which contains a beans.xml file with version 1.1 (or later) must specify the bean-discovery-mode
     * attribute. The default value for the attribute is {@link BeanDiscoveryMode#ANNOTATED annotated}.
     *
     * @param beans {@link Beans} instance parsed from {@link BeansReader#BEANS_XML_RESOURCE_NAME "META-INF/beans.xml"}
     * @return {@link BeanDiscoveryMode}
     */
    private BeanDiscoveryMode resolveDiscoveryMode(Beans beans) {
        if (beans == null) {
            return ALL;
        }
        String version = beans.getVersion();
        if (isBlank(version)) {
            return ALL;
        }
        return BeanDiscoveryMode.of(beans.getBeanDiscoveryMode());
    }

    private BeanArchiveType resolveBeanArchiveType(Beans beans) {
        BeanDiscoveryMode mode = resolveDiscoveryMode(beans);
        if (ALL.equals(mode)) {
            return EXPLICIT;
        } else if (NONE.equals(mode)) {
            return OTHER;
        } else {
            return EXPLICIT;
        }
    }

    public void disableDiscovery() {
        this.discoveryEnabled = false;
    }

    /**
     * All bean archives will be ignored except the implicit bean archive.
     *
     * @return <code>true</code> if discovery enabled, <code>false</code> otherwise
     */
    public boolean isDiscoveryEnabled() {
        return discoveryEnabled;
    }

    public void enableScanImplicit(boolean enabled) {
        this.scanImplicitEnabled = enabled;
    }

    public boolean isScanImplicitEnabled() {
        return this.scanImplicitEnabled;
    }

    /**
     * @return an unmodifiable view of discovered types
     */
    public Set<Class<?>> getDiscoveredTypes() {
        List<Class<?>> beanClasses = getBeanClasses();
        List<Class<?>> alternativeClasses = getAlternativeClasses();
        List<Class<?>> interceptorClasses = getInterceptorClasses();
        List<Class<?>> decoratorClasses = getDecoratorClasses();

        int size = beanClasses.size() + alternativeClasses.size()
                + interceptorClasses.size() + decoratorClasses.size();

        Set<Class<?>> discoveredTypes = newLinkedHashSet(size);

        discoveredTypes.addAll(beanClasses);
        discoveredTypes.addAll(alternativeClasses);
        discoveredTypes.addAll(interceptorClasses);
        discoveredTypes.addAll(decoratorClasses);

        return unmodifiableSet(discoveredTypes);
    }

    public List<Class<?>> getBeanClasses() {
        return getBeanTypes(beanClasses, BeanTypeSource.values());
    }

    public List<Class<?>> getAlternativeClasses() {
        return getAlternativeClasses(BeanTypeSource.values());
    }

    public List<Class<?>> getAlternativeClasses(BeanTypeSource... beanTypeSources) {
        return getBeanTypes(alternativeClasses, beanTypeSources);
    }

    public List<Class<?>> getInterceptorClasses() {
        return getInterceptorClasses(BeanTypeSource.values());
    }

    public List<Class<?>> getInterceptorClasses(BeanTypeSource... beanTypeSources) {
        return getBeanTypes(interceptorClasses, beanTypeSources);
    }

    public List<Class<?>> getDecoratorClasses() {
        return getDecoratorClasses(BeanTypeSource.values());
    }

    public List<Class<?>> getDecoratorClasses(BeanTypeSource... beanTypeSources) {
        return getBeanTypes(decoratorClasses, beanTypeSources);
    }

    public Set<Class<? extends Annotation>> getAlternativeStereotypeClasses() {
        return alternativeStereotypeClasses;
    }

    private static List<Class<?>> getBeanTypes(Map<BeanTypeSource, List<Class<?>>> repository, BeanTypeSource... beanTypeSources) {
        List<Class<?>> beanTypes = new LinkedList<>();
        for (BeanTypeSource beanTypeSource : beanTypeSources) {
            beanTypes.addAll(repository.getOrDefault(beanTypeSource, emptyList()));
        }
        return unmodifiableList(beanTypes);
    }

    private ClassLoader getClassLoader() {
        return standardBeanManager.getClassLoader();
    }
}
