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

import org.geektimes.commons.lang.util.ClassLoaderUtils;
import org.geektimes.commons.reflect.util.ClassUtils;
import org.geektimes.commons.reflect.util.SimpleClassScanner;
import org.geektimes.commons.util.PriorityComparator;
import org.geektimes.commons.util.ServiceLoaders;
import org.geektimes.enterprise.beans.xml.BeansReader;
import org.geektimes.enterprise.beans.xml.bind.Alternatives;
import org.geektimes.enterprise.beans.xml.bind.Beans;
import org.geektimes.enterprise.beans.xml.bind.Scan;
import org.geektimes.enterprise.inject.standard.*;
import org.geektimes.enterprise.inject.standard.event.*;
import org.geektimes.enterprise.inject.util.*;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.enterprise.context.*;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.*;
import javax.enterprise.util.TypeLiteral;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.Collections.emptySet;
import static java.util.Collections.sort;
import static java.util.Objects.requireNonNull;
import static java.util.ServiceLoader.load;
import static org.geektimes.commons.collection.util.CollectionUtils.*;
import static org.geektimes.commons.function.Streams.filterSet;
import static org.geektimes.commons.lang.util.StringUtils.endsWith;
import static org.geektimes.enterprise.inject.util.Beans.isAnnotatedVetoed;
import static org.geektimes.enterprise.inject.util.Beans.isManagedBean;
import static org.geektimes.enterprise.inject.util.Decorators.isDecorator;
import static org.geektimes.enterprise.inject.util.Injections.validateForbiddenAnnotation;
import static org.geektimes.enterprise.inject.util.Interceptors.isInterceptor;
import static org.geektimes.enterprise.inject.util.Parameters.isConstructorParameter;
import static org.geektimes.enterprise.inject.util.Parameters.isMethodParameter;

/**
 * Standard {@link BeanManager}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class StandardBeanManager implements BeanManager, Instance<Object> {

    private final Map<String, Object> properties;

    private final Set<Class<?>> beanClasses;

    private final Map<String, Boolean> packagesToScan;

    private final Map<Class<? extends Extension>, Extension> extensions;

    private final List<Class<?>> interceptorClasses;

    private final List<Class<?>> decoratorClasses;

    private final List<Class<?>> alternativeClasses;

    private final Set<Class<? extends Annotation>> alternativeStereotypeClasses;

    private final SimpleClassScanner classScanner;

    private final ObserverMethodDiscoverer observerMethodDiscoverer;

    private final ObserverMethodRepository observerMethodsRepository;

    private final EventDispatcher eventDispatcher;

    private final BeansReader beansReader;

    private ClassLoader classLoader;

    private boolean enabledDiscovery;

    /**
     * Qualifiers from extensions
     */
    private final Set<Class<? extends Annotation>> extendedQualifiers;

    /**
     * The key is annotation type, the value is meta-annotations
     */
    private final Map<Class<? extends Annotation>, Set<Annotation>> extendedStereotypes;

    private final Set<Class<? extends Annotation>> extendedScopes;

    /**
     * The key is annotation type, the value is passivating or not
     */
    private final Map<Class<? extends Annotation>, Boolean> extendsNormalScopes;

    /**
     * The key is annotation type of {@link InterceptorBinding}, the value is meta-annotations
     */
    private final Map<Class<? extends Annotation>, Set<Annotation>> extendedInterceptorBindings;

    private final Map<String, AnnotatedType> annotatedTypes;

    private final List<ManagedBean> managedBeans;

    public StandardBeanManager() {
        this.properties = new HashMap<>();
        this.enabledDiscovery = true;
        this.beanClasses = new LinkedHashSet<>();
        this.packagesToScan = new TreeMap<>();
        this.extensions = new LinkedHashMap<>();
        this.interceptorClasses = new LinkedList<>();
        this.decoratorClasses = new LinkedList<>();
        this.alternativeClasses = new LinkedList<>();
        this.alternativeStereotypeClasses = new LinkedHashSet<>();
        this.classScanner = SimpleClassScanner.INSTANCE;
        this.observerMethodDiscoverer = new ReflectiveObserverMethodDiscoverer(this);
        this.observerMethodsRepository = new ObserverMethodRepository();
        this.eventDispatcher = new EventDispatcher(observerMethodsRepository);
        this.beansReader = ServiceLoaders.loadSpi(BeansReader.class);
        this.classLoader = ClassLoaderUtils.getClassLoader(getClass());
        this.enabledDiscovery = true;
        this.extendedQualifiers = new LinkedHashSet<>();
        this.extendedStereotypes = new LinkedHashMap<>();
        this.extendedScopes = new LinkedHashSet<>();
        this.extendsNormalScopes = new LinkedHashMap<>();
        this.extendedInterceptorBindings = new LinkedHashMap<>();
        this.annotatedTypes = new LinkedHashMap<>();
        this.managedBeans = new LinkedList<>();
    }

    @Override
    public Object getReference(Bean<?> bean, Type beanType, CreationalContext<?> ctx) {
        // TODO
        return null;
    }

    @Override
    public Object getInjectableReference(InjectionPoint ij, CreationalContext<?> ctx) {
        // TODO
        return null;
    }

    @Override
    public <T> CreationalContext<T> createCreationalContext(Contextual<T> contextual) {
        // TODO
        return null;
    }

    @Override
    public Set<Bean<?>> getBeans(Type beanType, Annotation... qualifiers) {
        // TODO
        return null;
    }

    @Override
    public Set<Bean<?>> getBeans(String name) {
        // TODO
        return null;
    }

    @Override
    public Bean<?> getPassivationCapableBean(String id) {
        // TODO
        return null;
    }

    @Override
    public <X> Bean<? extends X> resolve(Set<Bean<? extends X>> beans) {
        // TODO
        return null;
    }

    @Override
    public void validate(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        if (annotated instanceof AnnotatedField) { // InjectionPoint on Field
            validateFieldInjectionPoint(injectionPoint);
        } else if (annotated instanceof AnnotatedParameter) { // InjectionPoint on Parameter
            AnnotatedParameter annotatedParameter = (AnnotatedParameter) annotated;
            if (isConstructorParameter(annotatedParameter)) { // InjectionPoint on Constructors' Parameter
                validateConstructorParameterInjectionPoint(injectionPoint);
            } else if (isMethodParameter(annotatedParameter)) { // InjectionPoint on Methods' Parameter
                validateMethodParameterInjectionPoint(injectionPoint);
            }
        }
    }

    /**
     * @param injectionPoint {@link InjectionPoint}
     * @throws DefinitionException If an injected field is annotated @Produces, the container automatically detects
     *                             the problem and treats it as a definition error.
     */
    private void validateFieldInjectionPoint(InjectionPoint injectionPoint) throws DefinitionException {
        validateForbiddenAnnotation(injectionPoint, Produces.class);
    }

    /**
     * @param injectionPoint {@link InjectionPoint}
     * @throws DefinitionException If a bean constructor has a parameter annotated @Disposes, @Observes, or @ObservesAsync,
     *                             the container automatically detects the problem and treats it as a definition error.
     */
    private void validateConstructorParameterInjectionPoint(InjectionPoint injectionPoint) throws DefinitionException {
        validateForbiddenAnnotation(injectionPoint, Disposes.class);
        validateForbiddenAnnotation(injectionPoint, Observes.class);
        validateForbiddenAnnotation(injectionPoint, ObservesAsync.class);
    }

    /**
     * @param injectionPoint {@link InjectionPoint}
     * @throws DefinitionException If an initializer method is annotated @Produces, has a parameter annotated @Disposes,
     *                             has a parameter annotated @Observes, or has a parameter annotated @ObservesAsync,
     *                             the container automatically detects the problem and treats it as a definition error.
     */
    private void validateMethodParameterInjectionPoint(InjectionPoint injectionPoint) throws DefinitionException {
        validateForbiddenAnnotation((Method) injectionPoint.getMember(), Produces.class);
        validateForbiddenAnnotation(injectionPoint, Disposes.class);
        validateForbiddenAnnotation(injectionPoint, Observes.class);
        validateForbiddenAnnotation(injectionPoint, ObservesAsync.class);
    }

    @Override
    @Deprecated
    public void fireEvent(Object event, Annotation... qualifiers) {
        Event<Object> eventDispatcher = getEvent();
        Event<Object> subEventDispatcher = eventDispatcher.select(qualifiers);
        subEventDispatcher.fire(event);
    }

    @Override
    public <T> Set<ObserverMethod<? super T>> resolveObserverMethods(T event, Annotation... qualifiers) {
        return (Set) observerMethodsRepository.resolveObserverMethods(event, qualifiers);
    }

    @Override
    public List<Decorator<?>> resolveDecorators(Set<Type> types, Annotation... qualifiers) {
        // TODO
        return null;
    }

    @Override
    public List<Interceptor<?>> resolveInterceptors(InterceptionType type, Annotation... interceptorBindings) {
        // TODO
        return null;
    }

    @Override
    public boolean isScope(Class<? extends Annotation> annotationType) {
        return Scopes.isScope(annotationType) ||
                // Extensions
                extendedScopes.contains(annotationType);
    }

    @Override
    public boolean isNormalScope(Class<? extends Annotation> annotationType) {
        return Scopes.isNormalScope(annotationType) ||
                // Extensions
                extendsNormalScopes.containsKey(annotationType);
    }

    @Override
    public boolean isPassivatingScope(Class<? extends Annotation> annotationType) {
        return Scopes.isPassivatingScope(annotationType) ||
                // Extensions
                extendsNormalScopes.getOrDefault(annotationType, Boolean.FALSE);
    }

    @Override
    public boolean isQualifier(Class<? extends Annotation> annotationType) {
        return Qualifiers.isQualifier(annotationType) ||
                // Extensions
                extendedQualifiers.contains(annotationType);
    }

    @Override
    public boolean isInterceptorBinding(Class<? extends Annotation> annotationType) {
        return Interceptors.isInterceptorBinding(annotationType) ||
                // Extensions
                extendedInterceptorBindings.containsKey(annotationType);
    }

    @Override
    public boolean isStereotype(Class<? extends Annotation> annotationType) {
        return Stereotypes.isStereotype(annotationType) ||
                // Extensions
                extendedStereotypes.containsKey(annotationType);
    }

    public boolean isBeanClass(Class<?> type) {
        return isDefiningAnnotationType(type, false, false);
    }

    /**
     * is defining annotation type or not.
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

    @Override
    public Set<Annotation> getInterceptorBindingDefinition(Class<? extends Annotation> bindingType) {
        return extendedInterceptorBindings.getOrDefault(bindingType, emptySet());
    }

    @Override
    public Set<Annotation> getStereotypeDefinition(Class<? extends Annotation> stereotype) {
        return extendedStereotypes.getOrDefault(stereotype, emptySet());
    }

    @Override
    public boolean areQualifiersEquivalent(Annotation qualifier1, Annotation qualifier2) {
        return Annotations.equals(qualifier1, qualifier2);
    }

    @Override
    public boolean areInterceptorBindingsEquivalent(Annotation interceptorBinding1, Annotation interceptorBinding2) {
        return Annotations.equals(interceptorBinding1, interceptorBinding2);
    }

    @Override
    public int getQualifierHashCode(Annotation qualifier) {
        return Annotations.hashCode(qualifier);
    }

    @Override
    public int getInterceptorBindingHashCode(Annotation interceptorBinding) {
        return Annotations.hashCode(interceptorBinding);
    }

    @Override
    public Context getContext(Class<? extends Annotation> scopeType) {
        // TODO
        return null;
    }

    @Override
    public ELResolver getELResolver() {
        // TODO
        return null;
    }

    @Override
    public ExpressionFactory wrapExpressionFactory(ExpressionFactory expressionFactory) {
        // TODO
        return null;
    }

    @Override
    public <T> AnnotatedType<T> createAnnotatedType(Class<T> type) {
        return new ReflectiveAnnotatedType(type);
    }

    @Deprecated
    @Override
    public <T> InjectionTarget<T> createInjectionTarget(AnnotatedType<T> type) {
        return null;
    }

    @Override
    public <T> InjectionTargetFactory<T> getInjectionTargetFactory(AnnotatedType<T> annotatedType) {
        // FIXME how to use <code>annotatedType</code>
        return new ManagedBeanInjectionTargetFactory<>();
    }

    @Override
    public <X> ProducerFactory<X> getProducerFactory(AnnotatedField<? super X> field, Bean<X> declaringBean) {
        // TODO
        return null;
    }

    @Override
    public <X> ProducerFactory<X> getProducerFactory(AnnotatedMethod<? super X> method, Bean<X> declaringBean) {
        // TODO
        return null;
    }

    @Override
    public <T> BeanAttributes<T> createBeanAttributes(AnnotatedType<T> type) {
        return new ManagedBean(this, type.getJavaClass());
    }

    @Override
    public BeanAttributes<?> createBeanAttributes(AnnotatedMember<?> type) {
        // TODO
        return null;
    }

    @Override
    public <T> Bean<T> createBean(BeanAttributes<T> attributes, Class<T> beanClass,
                                  InjectionTargetFactory<T> injectionTargetFactory) {
        // TODO
        return null;
    }

    @Override
    public <T, X> Bean<T> createBean(BeanAttributes<T> attributes, Class<X> beanClass,
                                     ProducerFactory<X> producerFactory) {
        // TODO
        return null;
    }

    @Override
    public InjectionPoint createInjectionPoint(AnnotatedField<?> field) {
        return new FieldInjectionPoint(field);
    }

    @Override
    public InjectionPoint createInjectionPoint(AnnotatedParameter<?> parameter) {
        if (isConstructorParameter(parameter)) {
            return new ConstructorParameterInjectionPoint(parameter);
        }
        return new MethodParameterInjectionPoint(parameter);
    }

    @Override
    public <T extends Extension> T getExtension(Class<T> extensionClass) {
        // TODO
        return null;
    }

    @Override
    public <T> InterceptionFactory<T> createInterceptionFactory(CreationalContext<T> ctx, Class<T> clazz) {
        // TODO
        return null;
    }

    @Override
    public Event<Object> getEvent() {
        return eventDispatcher;
    }

    @Override
    public Instance<Object> createInstance() {
        // TODO
        return null;
    }


    @Override
    public Instance<Object> select(Annotation... qualifiers) {
        return null;
    }

    @Override
    public <U> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
        return null;
    }

    @Override
    public <U> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
        return null;
    }

    @Override
    public boolean isUnsatisfied() {
        return false;
    }

    @Override
    public boolean isAmbiguous() {
        return false;
    }

    @Override
    public void destroy(Object instance) {

    }

    @Override
    public Iterator<Object> iterator() {
        return null;
    }

    @Override
    public Object get() {
        return null;
    }


    // Extended methods
    public void initialize() {
        performInitializationLifecycle();
        // TODO
    }

    private void performInitializationLifecycle() {
        initializeExtensions();
        performBeforeBeanDiscovery();
        performTypeDiscovery();
        performAfterTypeDiscovery();
        performBeanDiscovery();
        performAfterBeanDiscovery();
        performDeploymentValidation();
        performAfterDeploymentValidation();
    }

    /**
     * First, the container must search for service providers for the service javax.enterprise.inject.spi.Extension
     * defined in Container lifecycle events, instantiate a single instance of each service provider, and search the
     * service provider class for observer methods of initialization events.
     */
    private void initializeExtensions() {
        discoverExtensions();
        discoverObserverMethods();
    }

    /**
     * the container must fire an event of type BeforeBeanDiscovery, as defined in BeforeBeanDiscovery event.
     */
    private void performBeforeBeanDiscovery() {
        fireBeforeBeanDiscoveryEvent();
    }

    /**
     * the container must perform type discovery, as defined in Type discovery.
     * <p>
     * The container discovers:
     * <p>
     * each Java class, interface (excluding the special kind of interface declaration annotation type) or enum deployed in an explicit bean archive, and
     * <p>
     * each Java class with a bean defining annotation in an implicit bean archive.
     * <p>
     * that is not excluded from discovery by an exclude filter as defined in Exclude filters.
     */
    private void performTypeDiscovery() {
        if (!enabledDiscovery) {
            // TODO log
            return;
        }

        List<Beans> beansList = beansReader.readAllBeans(classLoader);
        Set<Class<?>> excludedClasses = new LinkedHashSet<>();

        beansList.forEach(beans -> {
            Scan scan = beans.getScan();
            excludedClasses.addAll(excludeFilters(scan));
            addInterceptors(beans.getInterceptors());
            addDecorators(beans.getDecorators());
            addAlternatives(beans.getAlternatives());
        });

        Set<Class<?>> discoveredClasses = discoverTypesInImplicitBeanArchives();
        // remove the excluded classes
        discoveredClasses.removeAll(excludedClasses);
        addInterceptorClasses(discoveredClasses);
        addDecoratorClasses(discoveredClasses);
        // add types that annotated definition annotations(excluded Interceptors and Decorators)
        addBeanClasses(discoveredClasses);
    }


    private void addInterceptorClasses(Set<Class<?>> classes) {
        filterAndHandleClasses(classes,
                Interceptors::isInterceptor,
                this::addInterceptorClass);
    }

    private void addDecoratorClasses(Set<Class<?>> classes) {
        filterAndHandleClasses(classes,
                Decorators::isDecorator,
                this::addDecoratorClass);
    }


    private void addBeanClasses(Set<Class<?>> classes) {
        filterAndHandleClasses(classes,
                this::isBeanClass,
                this::addBeanClass);
    }

    private void filterAndHandleClasses(Set<Class<?>> classes,
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
    private void addInterceptors(org.geektimes.enterprise.beans.xml.bind.Interceptors interceptors) {
        if (interceptors != null) {
            List<String> classNames = interceptors.getClazz();
            loadAnnotatedClasses(classNames, javax.interceptor.Interceptor.class)
                    .forEach(this::addInterceptorClass);
        }
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
    private void addDecorators(org.geektimes.enterprise.beans.xml.bind.Decorators decorators) {
        if (decorators != null) {
            List<String> classNames = decorators.getClazz();
            loadAnnotatedClasses(classNames, javax.decorator.Decorator.class)
                    .forEach(this::addDecoratorClass);
        }
    }

    private void addAlternatives(Alternatives alternatives) {
//        alternatives.getClazzOrStereotype()
//                .stream()
//                .map(this::)

        // TODO
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

    /**
     * @param className
     * @return
     * @throws IllegalArgumentException if {@link Class} is not found by its name.
     */
    private Class<?> loadClass(String className) throws IllegalArgumentException {
        try {
            return ClassUtils.forName(className, classLoader);
        } catch (ClassNotFoundException e) {
            String message = format("The class[name : %s] can't be found!", className);
            throw new DeploymentException(message, e);
        }
    }


    private Set<Class<?>> excludeFilters(Scan scan) {
        Set<Class<?>> excludedClasses = new LinkedHashSet<>();
        scan.getExclude().forEach(exclude -> {
            List<Object> conditions = exclude.getIfClassAvailableOrIfClassNotAvailableOrIfSystemProperty();
            if (isConditional(conditions)) {
                String name = exclude.getName();
                if (endsWith(name, ".*")) {
                    excludePackage(name, false);
                } else if (endsWith(name, ".**")) {
                    excludePackage(name, true);
                } else { // the fully qualified name of the type
                    Class<?> excludedClass = resolveClass(name);
                    removeBeanClass(excludedClass);
                    excludedClasses.add(excludedClass);
                }
            }
        });
        // those classes may be used to remove in the type discovery.
        return excludedClasses;
    }


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

    /**
     * the container must fire an event of type AfterTypeDiscovery, as defined in AfterTypeDiscovery event.
     */
    private void performAfterTypeDiscovery() {
        fireAfterTypeDiscoveryEvent();
    }

    /**
     * the container must perform bean discovery, as defined in Bean discovery.
     */
    private void performBeanDiscovery() {
        List<AnnotatedType> annotatedTypes = new ArrayList<>(this.annotatedTypes.values());
        determineManagedBeans(annotatedTypes);
        determineEnabledBeans(annotatedTypes);
        determineInterceptorBeans(annotatedTypes);
        determineDecoratorBeans(annotatedTypes);
    }

    private void determineManagedBeans(List<AnnotatedType> annotatedTypes) {
        Iterator<AnnotatedType> iterator = annotatedTypes.iterator();
        while (iterator.hasNext()) {
            AnnotatedType annotatedType = iterator.next();
            Class<?> beanClass = annotatedType.getJavaClass();
            if (beanClasses.contains(beanClass) && isManagedBean(beanClass)) {
                addManagedBean(annotatedType, beanClass);
                iterator.remove();
            }
        }
    }

    private void determineEnabledBeans(List<AnnotatedType> annotatedTypes) {
    }

    private void addManagedBean(AnnotatedType annotatedType, Class<?> beanClass) {
        ManagedBean managedBean = new ManagedBean(this, beanClass);
        this.managedBeans.add(managedBean);
        fireProcessInjectionPointEvents(managedBean);
        fireProcessInjectionTarget(annotatedType, managedBean);
        fireProcessBeanAttributesEvent(annotatedType, managedBean);
        fireProcessBeanEvent(annotatedType, managedBean);
    }

    private void determineInterceptorBeans(List<AnnotatedType> annotatedTypes) {
        // TODO
    }

    private void determineDecoratorBeans(List<AnnotatedType> annotatedTypes) {
        // TODO
    }

    /**
     * the container must fire an event of type AfterBeanDiscovery, as defined in AfterBeanDiscovery event, and abort
     * initialization of the application if any observer registers a definition error.
     */
    private void performAfterBeanDiscovery() {
        // TODO
    }

    /**
     * the container must detect deployment problems by validating bean dependencies and specialization and abort
     * initialization of the application if any deployment problems exist, as defined in Problems detected automatically
     * by the container.
     */
    private void performDeploymentValidation() {
        // TODO
    }

    /**
     * the container must fire an event of type AfterDeploymentValidation, as defined in AfterDeploymentValidation event,
     * and abort initialization of the application if any observer registers a deployment problem.
     */
    private void performAfterDeploymentValidation() {
        // TODO
    }

    /**
     * @return Every Java class, interface (excluding annotation type, a special kind of interface type) or enum
     * * discovered as defined in Type discovery.
     */
    private Set<Class<?>> discoverTypesInImplicitBeanArchives() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        for (Map.Entry<String, Boolean> packageEntry : packagesToScan.entrySet()) {
            String packageToDiscovery = packageEntry.getKey();
            boolean scanRecursively = Boolean.TRUE.equals(packageEntry.getValue());
            classes.addAll(classScanner.scan(classLoader, packageToDiscovery, scanRecursively, true));
        }
        return filterSet(classes, type -> !type.isInterface() || !type.isEnum());
    }

    private StandardBeanManager discoverExtensions() {
        load(Extension.class, classLoader).forEach(this::addExtension);
        return this;
    }

    private StandardBeanManager discoverObserverMethods() {
        extensions.forEach(this::addObserverMethods);
        return this;
    }

    private StandardBeanManager addObserverMethods(Object beanInstance) {
        return addObserverMethods(beanInstance.getClass(), beanInstance);
    }

    private StandardBeanManager addObserverMethods(Class<?> beanClass, Object beanInstance) {
        observerMethodDiscoverer.getObserverMethods(beanInstance, beanClass)
                .forEach(this::addObserverMethod);

        return this;
    }

    public StandardBeanManager addObserverMethod(ObserverMethod observerMethod) {
        observerMethodsRepository.addObserverMethod(observerMethod);
        return this;
    }

    // Event Methods

    private void fireBeforeBeanDiscoveryEvent() {
        fireEvent(new BeforeBeanDiscoveryEvent(this));
    }

    private void fireProcessAnnotatedTypeEvent(AnnotatedType<?> annotatedType) {
        if (!isAnnotatedVetoed(annotatedType.getJavaClass())) {
            fireEvent(new ProcessAnnotatedTypeEvent(annotatedType, this));
        }
    }

    private void fireProcessSyntheticAnnotatedTypeEvent(AnnotatedType<?> type, Extension source) {
        fireEvent(new ProcessSyntheticAnnotatedTypeEvent(type, source, this));
    }

    private void fireAfterTypeDiscoveryEvent() {
        fireEvent(new AfterTypeDiscoveryEvent(this));
    }

    private void fireProcessInjectionPointEvents(ManagedBean managedBean) {
        Set<InjectionPoint> injectionPoints = managedBean.getInjectionPoints();
        injectionPoints.forEach(this::fireProcessInjectionPointEvent);
    }

    private void fireProcessInjectionPointEvent(InjectionPoint injectionPoint) {
        fireEvent(new ProcessInjectionPointEvent(injectionPoint, this));
    }

    private void fireProcessInjectionTarget(AnnotatedType annotatedType, ManagedBean managedBean) {
        InjectionTargetFactory injectionTargetFactory = getInjectionTargetFactory(annotatedType);
        InjectionTarget injectionTarget = injectionTargetFactory.createInjectionTarget(managedBean);
        fireEvent(new ProcessInjectionTargetEvent<>(annotatedType, injectionTarget, this));
    }

    private void fireProcessBeanAttributesEvent(AnnotatedType<?> type, ManagedBean bean) {
        fireEvent(new ProcessBeanAttributesEvent(type, bean, this));
    }

    /**
     * if the class is an enabled bean, interceptor or decorator and if ProcessBeanAttributes.veto() wasn’t called
     * in previous step, fire an event which is a subtype of ProcessBean, as defined in ProcessBean event.
     *
     * @param type {@link AnnotatedType}
     * @param bean {@link ManagedBean}
     */
    private void fireProcessBeanEvent(AnnotatedType<?> type, ManagedBean bean) {
        if (managedBeans.contains(bean)) {
            fireEvent(new ProcessBeanEvent<>(type, bean, this));
        }
    }

    private void fireEvent(Object event) {
        eventDispatcher.fire(event);
    }

    public StandardBeanManager addQualifier(Class<? extends Annotation> qualifier) {
        requireNonNull(qualifier, "The 'qualifier' argument must not be null!");
        this.extendedQualifiers.add(qualifier);
        return this;
    }

    public StandardBeanManager addStereotype(Class<? extends Annotation> stereotype, Annotation... stereotypeDef) {
        extendedStereotypes.put(stereotype, ofSet(stereotypeDef));
        return this;
    }

    public StandardBeanManager addScope(Class<? extends Annotation> scopeType, boolean normal, boolean passivating) {
        if (normal) {
            extendsNormalScopes.put(scopeType, passivating);
        } else {
            extendedScopes.add(scopeType);
        }
        return this;
    }

    public StandardBeanManager addInterceptorBinding(Class<? extends Annotation> bindingType, Annotation[] bindingTypeDef) {
        extendedInterceptorBindings.put(bindingType, ofSet(bindingTypeDef));
        return this;
    }

    /**
     * {@link AnnotatedType}s
     * discovered by the container use the fully qualified class name of {@link AnnotatedType#getJavaClass()} to identify the
     * type.
     *
     * @param type {@link AnnotatedType}
     * @return
     */
    private StandardBeanManager addAnnotatedType(Class<?> type) {
        AnnotatedType annotatedType = createAnnotatedType(type);
        addAnnotatedType(type.getName(), annotatedType);
        fireProcessAnnotatedTypeEvent(annotatedType);
        return this;
    }

    public StandardBeanManager removeAnnotatedType(AnnotatedType<?> annotatedType) {
        Set<String> keysToRemove = new LinkedHashSet<>();
        for (Map.Entry<String, AnnotatedType> entry : annotatedTypes.entrySet()) {
            if (Objects.equals(entry.getValue().getJavaClass(), annotatedType.getJavaClass())) {
                keysToRemove.add(entry.getKey());
            }
        }
        keysToRemove.forEach(annotatedTypes::remove);
        return this;
    }

    private StandardBeanManager addAnnotatedType(String id, AnnotatedType<?> type) {
        annotatedTypes.put(id, type);
        return this;
    }

    public StandardBeanManager addSyntheticAnnotatedType(String id, AnnotatedType<?> type, Extension source) {
        addAnnotatedType(id, type);
        fireProcessSyntheticAnnotatedTypeEvent(type, source);
        return this;
    }

    public StandardBeanManager beanClasses(Class<?>... beanClasses) {
        iterateNonNull(beanClasses, this::addBeanClass);
        return this;
    }

    public StandardBeanManager addBeanClass(Class<?> beanClass) {
        requireNonNull(beanClass, "The 'beanClass' argument must not be null!");
        this.beanClasses.add(beanClass);
        addAnnotatedType(beanClass);
        return this;
    }

    private StandardBeanManager removeBeanClass(String beanClassName) {
        requireNonNull(beanClassName, "The 'beanClassName' argument must not be null!");
        return removeBeanClass(resolveClass(beanClassName));
    }

    private StandardBeanManager removeBeanClass(Class<?> beanClass) {
        if (beanClass != null) {
            this.beanClasses.remove(beanClass);
        }
        return this;
    }

    public StandardBeanManager packages(boolean scanRecursively, Package... packagesToScan) {
        iterateNonNull(packagesToScan, packageToScan -> addPackage(packageToScan, scanRecursively));
        return this;
    }

    public StandardBeanManager addPackage(Package packageToScan, boolean scanRecursively) {
        requireNonNull(packageToScan, "The 'packageToScan' argument must not be null!");
        return addPackage(packageToScan.getName(), scanRecursively);
    }

    public StandardBeanManager addPackage(String packageToScan, boolean scanRecursively) {
        requireNonNull(packageToScan, "The 'packageToScan' argument must not be null!");
        this.packagesToScan.put(packageToScan, scanRecursively);
        return this;
    }

    public StandardBeanManager excludePackage(String packageToScan, boolean scanRecursively) {
        requireNonNull(packageToScan, "The 'packageToScan' argument must not be null!");
        if (this.packagesToScan.remove(packageToScan, scanRecursively)) {
            this.packagesToScan.remove(packageToScan);
        }
        return this;
    }

    public StandardBeanManager extensions(Extension... extensions) {
        iterateNonNull(extensions, this::addExtension);
        return this;
    }

    public StandardBeanManager addExtension(Extension extension) {
        requireNonNull(extension, "The 'extension' argument must not be null!");
        extensions.put(extension.getClass(), extension);
        return this;
    }

    public StandardBeanManager interceptorClasses(Class<?>... interceptorClasses) {
        iterateNonNull(interceptorClasses, this::addInterceptorClass);
        return this;
    }

    /**
     * @param interceptorClass
     * @return
     * @throws DeploymentException If <code>interceptorClass</code> is not an interceptor class.
     */
    public StandardBeanManager addInterceptorClass(Class<?> interceptorClass) throws DeploymentException {
        requireNonNull(interceptorClass, "The 'interceptorClass' argument must not be null!");
        this.interceptorClasses.add(interceptorClass);
        // Interceptors enabled using @Priority are called before interceptors enabled using beans.xml.
        sort(this.interceptorClasses, PriorityComparator.INSTANCE);
        addAnnotatedType(interceptorClass);
        return this;
    }

    public StandardBeanManager decoratorClasses(Class<?>... decoratorClasses) {
        iterateNonNull(decoratorClasses, this::addDecoratorClass);
        return this;
    }

    public StandardBeanManager addDecoratorClass(Class<?> decoratorClass) {
        requireNonNull(decoratorClass, "The 'decoratorClass' argument must not be null!");
        this.decoratorClasses.add(decoratorClass);
        addAnnotatedType(decoratorClass);
        return this;
    }

    public StandardBeanManager alternativeClasses(Class<?>... alternativeClasses) {
        iterateNonNull(alternativeClasses, this::addAlternativeClass);
        return this;
    }

    public StandardBeanManager addAlternativeClass(Class<?> alternativeClass) {
        requireNonNull(alternativeClass, "The 'alternativeClass' argument must not be null!");
        this.alternativeClasses.add(alternativeClass);
        return this;
    }

    public StandardBeanManager alternativeStereotypeClasses(Class<? extends Annotation>... alternativeStereotypeClasses) {
        iterateNonNull(alternativeStereotypeClasses, this::addAlternativeStereotypeClass);
        return this;
    }

    public StandardBeanManager addAlternativeStereotypeClass(Class<? extends Annotation> alternativeStereotypeClass) {
        requireNonNull(alternativeStereotypeClass, "The 'alternativeStereotypeClass' argument must not be null!");
        this.alternativeStereotypeClasses.add(alternativeStereotypeClass);
        return this;
    }

    public StandardBeanManager property(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public StandardBeanManager properties(Map<String, Object> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
        return this;
    }

    public StandardBeanManager disableDiscovery() {
        enabledDiscovery = false;
        return this;
    }

    public StandardBeanManager classLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    private static <T> void iterateNonNull(T[] values, Consumer<T> consumer) {
        Objects.requireNonNull(values, "The argument must not be null!");
        for (T value : values) {
            consumer.accept(value);
        }
    }

    public List<Class<?>> getAlternatives() {
        return alternativeClasses;
    }

    public List<Class<?>> getInterceptors() {
        return interceptorClasses;
    }

    public List<Class<?>> getDecorators() {
        return decoratorClasses;
    }

    public void addBeanDiscoveryDefinitionError(Throwable t) {
        // TODO
    }
}
