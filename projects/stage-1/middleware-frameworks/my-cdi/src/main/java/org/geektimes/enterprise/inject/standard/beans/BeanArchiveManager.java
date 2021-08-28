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
package org.geektimes.enterprise.inject.standard.beans;

import org.geektimes.commons.lang.util.ClassPathUtils;
import org.geektimes.commons.reflect.util.ClassUtils;
import org.geektimes.commons.reflect.util.SimpleClassScanner;
import org.geektimes.commons.util.PriorityComparator;
import org.geektimes.commons.util.ServiceLoaders;
import org.geektimes.enterprise.inject.standard.beans.xml.BeansReader;
import org.geektimes.enterprise.inject.standard.beans.xml.bind.Alternatives;
import org.geektimes.enterprise.inject.standard.beans.xml.bind.Beans;
import org.geektimes.enterprise.inject.standard.beans.xml.bind.Scan;
import org.geektimes.enterprise.inject.util.*;

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
import static org.geektimes.commons.collection.util.CollectionUtils.ofSet;
import static org.geektimes.commons.lang.util.StringUtils.endsWith;
import static org.geektimes.commons.lang.util.StringUtils.isBlank;
import static org.geektimes.enterprise.inject.standard.beans.BeanArchiveType.EXPLICIT;
import static org.geektimes.enterprise.inject.standard.beans.BeanArchiveType.OTHER;
import static org.geektimes.enterprise.inject.standard.beans.BeanDiscoveryMode.ALL;
import static org.geektimes.enterprise.inject.standard.beans.BeanDiscoveryMode.NONE;
import static org.geektimes.enterprise.inject.standard.beans.xml.BeansReader.BEANS_XML_RESOURCE_NAME;
import static org.geektimes.enterprise.inject.util.Decorators.isDecorator;
import static org.geektimes.enterprise.inject.util.Interceptors.isInterceptor;

/**
 * Bean archives Manager
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class BeanArchiveManager {

    private static final Predicate<Class<?>> annotationFilter = Class::isAnnotation;

    private static final Predicate<Class<?>> nonAnnotationFilter = annotationFilter.negate();

    private ClassLoader classLoader;

    private final BeansReader beansReader;

    private final SimpleClassScanner classScanner;

    // Discovered types

    private final Set<Class<?>> beanClasses;

    private final List<Class<?>> interceptorClasses;

    private final List<Class<?>> decoratorClasses;

    private final List<Class<?>> alternativeClasses;

    private final Set<Class<? extends Annotation>> alternativeStereotypeClasses;

    // Synthetic meta-data

    /**
     * Synthetic Bean classes from {@link SeContainerInitializer#addBeanClasses(Class[])}
     */
    private final Set<Class<?>> syntheticBeanClasses;

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
     * Synthetic Scopes from {@link BeforeBeanDiscovery#addScope(Class, boolean, boolean)}
     */
    private final Set<Class<? extends Annotation>> syntheticScopes;

    /**
     * Synthetic Normal Scopes from {@link BeforeBeanDiscovery#addScope(Class, boolean, boolean)}
     * <p>
     * The key is annotation type, the value is passivating or not
     */
    private final Map<Class<? extends Annotation>, Boolean> syntheticNormalScopes;

    /**
     * Synthetic InterceptorBindings from {@link BeforeBeanDiscovery#addInterceptorBinding(Class, Annotation...)}
     * <p>
     * The key is annotation type of {@link InterceptorBinding}, the value is meta-annotations
     */
    private final Map<Class<? extends Annotation>, Set<Annotation>> syntheticInterceptorBindings;

    private boolean discoveryEnabled;

    private boolean scanImplicitEnabled;

    private volatile boolean discovered;

    public BeanArchiveManager(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.beansReader = ServiceLoaders.loadSpi(BeansReader.class, classLoader);
        this.classScanner = SimpleClassScanner.INSTANCE;
        this.beanClasses = new LinkedHashSet<>();
        this.interceptorClasses = new LinkedList<>();
        this.decoratorClasses = new LinkedList<>();
        this.alternativeClasses = new LinkedList<>();
        this.alternativeStereotypeClasses = new LinkedHashSet<>();

        this.syntheticBeanClasses = new LinkedHashSet<>();
        this.syntheticPackagesToScan = new TreeMap<>();
        this.syntheticQualifiers = new LinkedHashSet<>();
        this.syntheticStereotypes = new LinkedHashMap<>();
        this.syntheticScopes = new LinkedHashSet<>();
        this.syntheticNormalScopes = new LinkedHashMap<>();
        this.syntheticInterceptorBindings = new LinkedHashMap<>();

        this.discoveryEnabled = true;
        this.scanImplicitEnabled = false;
    }

    public BeanArchiveManager addSyntheticPackage(Package packageToScan, boolean scanRecursively) {
        requireNonNull(packageToScan, "The 'packageToScan' argument must not be null!");
        return addSyntheticPackage(packageToScan.getName(), scanRecursively);
    }

    public BeanArchiveManager addSyntheticPackage(String packageToScan, boolean scanRecursively) {
        requireNonNull(packageToScan, "The 'packageToScan' argument must not be null!");
        this.syntheticPackagesToScan.put(packageToScan, scanRecursively);
        return this;
    }

    public BeanArchiveManager addSyntheticBeanClass(Class<?> beanClass) {
        requireNonNull(beanClass, "The 'beanClass' argument must not be null!");
        this.syntheticBeanClasses.add(beanClass);
        return this;
    }

    public BeanArchiveManager addSyntheticQualifier(Class<? extends Annotation> qualifier) {
        requireNonNull(qualifier, "The 'qualifier' argument must not be null!");
        this.syntheticQualifiers.add(qualifier);
        return this;
    }

    public BeanArchiveManager addSyntheticStereotype(Class<? extends Annotation> stereotype, Annotation... stereotypeDef) {
        syntheticStereotypes.put(stereotype, ofSet(stereotypeDef));
        return this;
    }

    public BeanArchiveManager addSyntheticScope(Class<? extends Annotation> scopeType, boolean normal, boolean passivating) {
        if (normal) {
            syntheticNormalScopes.put(scopeType, passivating);
        } else {
            syntheticScopes.add(scopeType);
        }
        return this;
    }

    public BeanArchiveManager addSyntheticInterceptorBinding(Class<? extends Annotation> bindingType, Annotation[] bindingTypeDef) {
        syntheticInterceptorBindings.put(bindingType, ofSet(bindingTypeDef));
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
        filterAndHandleDiscoveredTypes(discoveredTypes,
                this::isBeanClass,
                this::addBeanClass);
    }

    /**
     * Add provided bean classes.
     *
     * @param beanClass bean class
     * @return self
     */
    private BeanArchiveManager addBeanClass(Class<?> beanClass) {
        requireNonNull(beanClass, "The 'beanClass' argument must not be null!");
        this.beanClasses.add(beanClass);
        return this;
    }

    public BeanArchiveManager addAlternativeClass(Class<?> alternativeClass) {
        requireNonNull(alternativeClass, "The 'alternativeClass' argument must not be null!");
        this.alternativeClasses.add(alternativeClass);
        return this;
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
    private void addInterceptors(org.geektimes.enterprise.inject.standard.beans.xml.bind.Interceptors interceptors) {
        if (interceptors != null) {
            List<String> classNames = interceptors.getClazz();
            loadAnnotatedClasses(classNames, javax.interceptor.Interceptor.class)
                    .forEach(this::addInterceptorClass);
        }
    }

    private void discoverInterceptorClasses(Set<Class<?>> discoveredTypes) {
        filterAndHandleDiscoveredTypes(discoveredTypes,
                Interceptors::isInterceptor,
                this::addInterceptorClass);
    }

    /**
     * @param interceptorClass
     * @return
     * @throws DeploymentException If <code>interceptorClass</code> is not an interceptor class.
     */
    public BeanArchiveManager addInterceptorClass(Class<?> interceptorClass) throws DeploymentException {
        requireNonNull(interceptorClass, "The 'interceptorClass' argument must not be null!");
        this.interceptorClasses.add(interceptorClass);
        // Interceptors enabled using @Priority are called before interceptors enabled using beans.xml.
        sort(this.interceptorClasses, PriorityComparator.INSTANCE);
        return this;
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
    private void addDecorators(org.geektimes.enterprise.inject.standard.beans.xml.bind.Decorators decorators) {
        if (decorators != null) {
            List<String> classNames = decorators.getClazz();
            loadAnnotatedClasses(classNames, javax.decorator.Decorator.class)
                    .forEach(this::addDecoratorClass);
        }
    }

    private void discoverDecoratorClasses(Set<Class<?>> discoveredTypes) {
        filterAndHandleDiscoveredTypes(discoveredTypes,
                Decorators::isDecorator,
                this::addDecoratorClass);
    }

    public BeanArchiveManager addDecoratorClass(Class<?> decoratorClass) {
        requireNonNull(decoratorClass, "The 'decoratorClass' argument must not be null!");
        this.decoratorClasses.add(decoratorClass);
        return this;
    }


    private void addAlternatives(Alternatives alternatives) {
//        alternatives.getClazzOrStereotype()
//                .stream()
//                .map(this::)

        // TODO
    }

    public BeanArchiveManager addAlternativeStereotypeClass(Class<? extends Annotation> alternativeStereotypeClass) {
        requireNonNull(alternativeStereotypeClass, "The 'alternativeStereotypeClass' argument must not be null!");
        this.alternativeStereotypeClasses.add(alternativeStereotypeClass);
        return this;
    }


    private void filterAndHandleDiscoveredTypes(Set<Class<?>> classes,
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

    public boolean isScope(Class<? extends Annotation> annotationType) {
        return Scopes.isScope(annotationType) ||
                // Extensions
                syntheticScopes.contains(annotationType);
    }

    public boolean isNormalScope(Class<? extends Annotation> annotationType) {
        return Scopes.isNormalScope(annotationType) ||
                // Extensions
                syntheticNormalScopes.containsKey(annotationType);
    }

    public boolean isPassivatingScope(Class<? extends Annotation> annotationType) {
        return Scopes.isPassivatingScope(annotationType) ||
                // Extensions
                syntheticNormalScopes.getOrDefault(annotationType, Boolean.FALSE);
    }

    public boolean isQualifier(Class<? extends Annotation> annotationType) {
        return Qualifiers.isQualifier(annotationType) ||
                // Extensions
                syntheticQualifiers.contains(annotationType);
    }

    public boolean isInterceptorBinding(Class<? extends Annotation> annotationType) {
        return Interceptors.isInterceptorBinding(annotationType) ||
                // Extensions
                syntheticInterceptorBindings.containsKey(annotationType);
    }

    public boolean isStereotype(Class<? extends Annotation> annotationType) {
        return Stereotypes.isStereotype(annotationType) ||
                // Extensions
                syntheticStereotypes.containsKey(annotationType);
    }

    public boolean isBeanClass(Class<?> type) {
        return isDefiningAnnotationType(type, false, false);
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
            discoverTypesInBeanArchives();
            discoverTypesInNonBeanArchivesAsImplicit();
            discoverTypesInSyntheticBeanArchives();
            discovered = true;
        }
    }

    private void discoverTypesInBeanArchives() {
        try {
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
    private void discoverTypesInNonBeanArchivesAsImplicit() {
        if (isScanImplicitEnabled()) {
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
        discoverTypesInExtendedPackages();
        // Merge synthetic bean classes into enabled bean classes
        syntheticBeanClasses.forEach(this::addBeanClass);
    }

    private Set<Class<?>> scan(URL beansXMLResource, Predicate<Class<?>>... classFilters) {
        return new LinkedHashSet<>(classScanner.scan(classLoader, beansXMLResource, true, classFilters));
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
            // Add Interceptor classes
            addInterceptors(beans.getInterceptors());
            // Add Decorator classes
            addDecorators(beans.getDecorators());
            // Add Alternative classes
            addAlternatives(beans.getAlternatives());
            // Trimmed bean archive
            if (!trimBeanArchive(discoveredTypes, beans)) {
                // Add bean classes from the remaining discovered types
                discoveredTypes.forEach(this::addBeanClass);
            }
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

    private void discoverTypesInExtendedPackages() {
        if (isDiscoveryEnabled()) {
            Set<Class<?>> discoveredTypes = new LinkedHashSet<>();
            for (Map.Entry<String, Boolean> packageEntry : syntheticPackagesToScan.entrySet()) {
                String packageToDiscovery = packageEntry.getKey();
                boolean scanRecursively = Boolean.TRUE.equals(packageEntry.getValue());
                discoveredTypes.addAll(classScanner.scan(classLoader, packageToDiscovery, scanRecursively, true));
            }
            discoverDefiningAnnotationBeanTypes(discoveredTypes);
        }
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
        return ClassUtils.resolveClass(className, classLoader);
    }

    private Class<?> loadClass(String className) throws IllegalArgumentException {
        try {
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

    /**
     * Is defining annotation type or not.
     * <p>
     * A bean class may have a bean defining annotation, allowing it to be placed anywhere in an application,
     * as defined in Bean archives. A bean class with a bean defining annotation is said to be an implicit bean.
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
     * @param type
     * @param includedInterceptor
     * @param includedDecorator
     * @return
     */
    public boolean isDefiningAnnotationType(Class<?> type, boolean includedInterceptor, boolean includedDecorator) {

        if (includedInterceptor && isInterceptor(type)) {
            return true;
        }
        if (includedDecorator && isDecorator(type)) {
            return true;
        }

        boolean hasDefiningAnnotation = false;

        Annotation[] annotations = type.getAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (isScope(annotationType) ||
                    isNormalScope(annotationType) ||
                    isStereotype(annotationType)) {
                hasDefiningAnnotation = true;
                break;
            }
        }

        return hasDefiningAnnotation;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
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
        Set<Class<?>> beanClasses = getBeanClasses();
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

    public Set<Class<?>> getBeanClasses() {
        return beanClasses;
    }

    public List<Class<?>> getInterceptorClasses() {
        // Interceptors enabled using @Priority are called before interceptors enabled using beans.xml.
        sort(this.interceptorClasses, PriorityComparator.INSTANCE);
        return interceptorClasses;
    }

    public List<Class<?>> getDecoratorClasses() {
        // The decorator will only be executed once based on the @Priority annotation’s invocation chain.
        sort(this.decoratorClasses, PriorityComparator.INSTANCE);
        return decoratorClasses;
    }

    public List<Class<?>> getAlternativeClasses() {
        return alternativeClasses;
    }

    public Set<Class<? extends Annotation>> getAlternativeStereotypeClasses() {
        return alternativeStereotypeClasses;
    }
}
