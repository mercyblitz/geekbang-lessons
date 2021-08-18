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
package org.geektimes.enterprise.inject.standard;

import org.geektimes.commons.lang.util.ClassLoaderUtils;
import org.geektimes.commons.reflect.util.SimpleClassScanner;
import org.geektimes.enterprise.inject.standard.event.*;
import org.geektimes.enterprise.inject.util.*;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.*;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static java.util.ServiceLoader.load;
import static org.geektimes.enterprise.inject.util.Injections.validateForbiddenAnnotation;
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

    private final Map<Package, Boolean> packagesToScan;

    private final Map<Class<? extends Extension>, Extension> extensions;

    private final Set<Class<?>> interceptorClasses;

    private final Set<Class<?>> decoratorClasses;

    private final Set<Class<?>> alternativeClasses;

    private final Set<Class<? extends Annotation>> alternativeStereotypeClasses;

    private final SimpleClassScanner classScanner;


    private final ObserverMethodDiscoverer observerMethodDiscoverer;

    private final ObserverMethodRepository observerMethodsRepository;

    private final EventDispatcher eventDispatcher;

    private ClassLoader classLoader;

    private boolean enabledDiscovery;

    public StandardBeanManager() {
        this.properties = new HashMap<>();
        this.enabledDiscovery = true;
        this.beanClasses = new LinkedHashSet<>();
        this.packagesToScan = new LinkedHashMap<>();
        this.extensions = new ConcurrentHashMap<>();
        this.interceptorClasses = new LinkedHashSet<>();
        this.decoratorClasses = new LinkedHashSet<>();
        this.alternativeClasses = new LinkedHashSet<>();
        this.alternativeStereotypeClasses = new LinkedHashSet<>();
        this.classScanner = SimpleClassScanner.INSTANCE;
        this.observerMethodDiscoverer = new ReflectiveObserverMethodDiscoverer(this);
        this.observerMethodsRepository = new ObserverMethodRepository();
        this.eventDispatcher = new EventDispatcher(observerMethodsRepository);
        this.classLoader = ClassLoaderUtils.getClassLoader(getClass());
        this.enabledDiscovery = true;
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
        // TODO
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
        return Scopes.isScope(annotationType);
    }

    @Override
    public boolean isNormalScope(Class<? extends Annotation> annotationType) {
        return Scopes.isNormalScope(annotationType);
    }

    @Override
    public boolean isPassivatingScope(Class<? extends Annotation> annotationType) {
        return Scopes.isPassivatingScope(annotationType);
    }

    @Override
    public boolean isQualifier(Class<? extends Annotation> annotationType) {
        return Qualifiers.isQualifier(annotationType);
    }

    @Override
    public boolean isInterceptorBinding(Class<? extends Annotation> annotationType) {
        return Interceptors.isInterceptorBinding(annotationType);
    }

    @Override
    public boolean isStereotype(Class<? extends Annotation> annotationType) {
        return Stereotypes.isStereotype(annotationType);
    }

    @Override
    public Set<Annotation> getInterceptorBindingDefinition(Class<? extends Annotation> bindingType) {
        // TODO
        return null;
    }

    @Override
    public Set<Annotation> getStereotypeDefinition(Class<? extends Annotation> stereotype) {
        // TODO
        return null;
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

    @Override
    public <T> InjectionTarget<T> createInjectionTarget(AnnotatedType<T> type) {
        // TODO
        return null;
    }

    @Override
    public <T> InjectionTargetFactory<T> getInjectionTargetFactory(AnnotatedType<T> annotatedType) {
        // TODO
        return null;
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
        // TODO
        return null;
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
    public <T, X> Bean<T> createBean(BeanAttributes<T> attributes, Class<X> beanClass, ProducerFactory<X> producerFactory) {
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
        // TODO
        performContainerLifecycleEvents();
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
    private void performContainerLifecycleEvents() {
        discoverExtensions();
        discoverObserverMethods();
        fireBeforeBeanDiscoveryEvent();
    }

    /**
     * the container must fire an event of type BeforeBeanDiscovery, as defined in BeforeBeanDiscovery event.
     */
    private void performBeforeBeanDiscovery() {
    }

    /**
     * the container must perform type discovery, as defined in Type discovery.
     */
    private void performTypeDiscovery() {
        if (!enabledDiscovery) {
            // TODO log
            return;
        }

        Set<Class<?>> classes = scanClasses();
        // filter Beans with definition annotations
        Set<AnnotatedBean<?>> annotatedBeans = filterDefiningAnnotationBeans(classes);
        // dispatch all kinds of classes, e.g Bean Classes , Interceptor Classes or others
        dispatchClasses(annotatedBeans);
    }

    /**
     * the container must fire an event of type AfterTypeDiscovery, as defined in AfterTypeDiscovery event.
     */
    private void performAfterTypeDiscovery() {
    }

    /**
     * the container must perform bean discovery, as defined in Bean discovery.
     */
    private void performBeanDiscovery() {
    }

    /**
     * the container must fire an event of type AfterBeanDiscovery, as defined in AfterBeanDiscovery event, and abort
     * initialization of the application if any observer registers a definition error.
     */
    private void performAfterBeanDiscovery() {
    }

    /**
     * the container must detect deployment problems by validating bean dependencies and specialization and abort
     * initialization of the application if any deployment problems exist, as defined in Problems detected automatically
     * by the container.
     */
    private void performDeploymentValidation() {
    }

    /**
     * the container must fire an event of type AfterDeploymentValidation, as defined in AfterDeploymentValidation event,
     * and abort initialization of the application if any observer registers a deployment problem.
     */
    private void performAfterDeploymentValidation() {
    }

    private void dispatchClasses(Set<AnnotatedBean<?>> annotatedBeans) {

    }

    private Set<AnnotatedBean<?>> filterDefiningAnnotationBeans(Set<Class<?>> classes) {
        Set<AnnotatedBean<?>> annotatedBeans = new LinkedHashSet<>();
        for (Class<?> type : classes) {
            AnnotatedBean annotatedBean = new ReflectiveAnnotatedBean(type);
            if (annotatedBean.hasDefiningAnnotation()) {
                annotatedBeans.add(annotatedBean);
            }
        }
        return annotatedBeans;
    }

    private Set<Class<?>> scanClasses() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        for (Map.Entry<Package, Boolean> packageEntry : packagesToScan.entrySet()) {
            Package packageToDiscovery = packageEntry.getKey();
            boolean scanRecursively = Boolean.TRUE.equals(packageEntry.getValue());
            classes.addAll(classScanner.scan(classLoader, packageToDiscovery.getName(), scanRecursively, true));
        }
        return classes;
    }


    private void discoverTypes() {
    }

    private void discoverBeans() {
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

    public void fireBeforeBeanDiscoveryEvent() {
        eventDispatcher.fire(new BeforeBeanDiscoveryEvent(this));
    }

    public StandardBeanManager addQualifier(Class<? extends Annotation> qualifier) {
        // TODO
        return this;
    }

    public StandardBeanManager addStereotype(Class<? extends Annotation> stereotype, Annotation... stereotypeDef) {
        // TODO
        return this;
    }

    public StandardBeanManager addScope(Class<? extends Annotation> scopeType, boolean normal, boolean passivating) {
        // TODO
        return this;
    }

    public StandardBeanManager addInterceptorBinding(Class<? extends Annotation> bindingType, Annotation[] bindingTypeDef) {
        // TODO
        return this;
    }

    public StandardBeanManager addAnnotatedType(String id, AnnotatedType<?> type) {
        // TODO
        return this;
    }

    public StandardBeanManager beanClasses(Class<?>... beanClasses) {
        iterateNonNull(beanClasses, this::addBeanClass);
        return this;
    }

    public StandardBeanManager addBeanClass(Class<?> beanClass) {
        requireNonNull(beanClass, "The 'packageToScan' argument must not be null!");
        this.beanClasses.add(beanClass);
        return this;
    }

    public StandardBeanManager packages(boolean scanRecursively, Package... packagesToScan) {
        iterateNonNull(packagesToScan, packageToScan -> addPackage(scanRecursively, packageToScan));
        return this;
    }

    public StandardBeanManager addPackage(boolean scanRecursively, Package packageToScan) {
        requireNonNull(packageToScan, "The 'packageToScan' argument must not be null!");
        this.packagesToScan.put(packageToScan, scanRecursively);
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

    public StandardBeanManager addInterceptorClass(Class<?> interceptorClass) {
        this.interceptorClasses.add(interceptorClass);
        return this;
    }

    public StandardBeanManager decoratorClasses(Class<?>... decoratorClasses) {
        iterateNonNull(decoratorClasses, this::addDecoratorClass);
        return this;
    }

    public StandardBeanManager addDecoratorClass(Class<?> decoratorClass) {
        requireNonNull(decoratorClass, "The 'decoratorClass' argument must not be null!");
        this.decoratorClasses.add(decoratorClass);
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
}
