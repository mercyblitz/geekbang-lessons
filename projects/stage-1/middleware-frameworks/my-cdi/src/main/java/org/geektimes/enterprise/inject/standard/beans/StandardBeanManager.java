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
import org.geektimes.enterprise.inject.standard.*;
import org.geektimes.enterprise.inject.standard.event.*;
import org.geektimes.enterprise.inject.util.Annotations;

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

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static java.util.ServiceLoader.load;
import static org.geektimes.commons.lang.util.ArrayUtils.iterate;
import static org.geektimes.enterprise.inject.util.Beans.isAnnotatedVetoed;
import static org.geektimes.enterprise.inject.util.Beans.isManagedBean;
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
    public static final String SCAN_IMPLICIT_PROPERTY_NAME = "javax.enterprise.inject.scan.implicit";

    private final Map<String, Object> properties;

    private final Map<Class<? extends Extension>, Extension> extensions;

    private final BeanArchiveManager beanArchiveManager;

    private final ObserverMethodDiscoverer observerMethodDiscoverer;

    private final ObserverMethodRepository observerMethodsRepository;

    private final EventDispatcher eventDispatcher;

    private ClassLoader classLoader;

    private final Map<String, AnnotatedType> annotatedTypes;

    private final List<ManagedBean> managedBeans;

    public StandardBeanManager() {
        this.classLoader = ClassLoaderUtils.getClassLoader(getClass());
        this.properties = new HashMap<>();
        this.extensions = new LinkedHashMap<>();
        this.observerMethodDiscoverer = new ReflectiveObserverMethodDiscoverer(this);
        this.observerMethodsRepository = new ObserverMethodRepository();
        this.eventDispatcher = new EventDispatcher(observerMethodsRepository);
        this.beanArchiveManager = new BeanArchiveManager(classLoader);
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
        return beanArchiveManager.isScope(annotationType);
    }

    @Override
    public boolean isNormalScope(Class<? extends Annotation> annotationType) {
        return beanArchiveManager.isNormalScope(annotationType);
    }

    @Override
    public boolean isPassivatingScope(Class<? extends Annotation> annotationType) {
        return beanArchiveManager.isPassivatingScope(annotationType);
    }

    @Override
    public boolean isQualifier(Class<? extends Annotation> annotationType) {
        return beanArchiveManager.isQualifier(annotationType);
    }

    @Override
    public boolean isInterceptorBinding(Class<? extends Annotation> annotationType) {
        return beanArchiveManager.isInterceptorBinding(annotationType);
    }

    @Override
    public boolean isStereotype(Class<? extends Annotation> annotationType) {
        return beanArchiveManager.isStereotype(annotationType);
    }


    @Override
    public Set<Annotation> getInterceptorBindingDefinition(Class<? extends Annotation> bindingType) {
        return beanArchiveManager.getInterceptorBindingDefinition(bindingType);
    }

    @Override
    public Set<Annotation> getStereotypeDefinition(Class<? extends Annotation> stereotype) {
        return beanArchiveManager.getStereotypeDefinition(stereotype);
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
        initializeBeanArchiveManager();
        performInitializationLifecycle();
        // TODO
    }

    private void initializeBeanArchiveManager() {
        beanArchiveManager.setClassLoader(classLoader);
        beanArchiveManager.enableScanImplicit(isScanImplicitEnabled());
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
        Set<Class<?>> discoveredTypes = beanArchiveManager.discoverTypes();
        discoveredTypes.forEach(this::addAnnotatedType);
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
        determineInterceptorBeans(annotatedTypes);
        determineDecoratorBeans(annotatedTypes);
    }

    private void determineManagedBeans(List<AnnotatedType> annotatedTypes) {
        Iterator<AnnotatedType> iterator = annotatedTypes.iterator();
        while (iterator.hasNext()) {
            AnnotatedType annotatedType = iterator.next();
            Class<?> beanClass = annotatedType.getJavaClass();
            if (isManagedBean(beanClass)) {
                addManagedBean(annotatedType, beanClass);
                iterator.remove();
            }
        }
    }

    /**
     * Add Managed Bean, and fire events as below:
     *
     * <ol>
     *     <li>fire an event of type {@link ProcessInjectionPoint} for each injection point in the class</li>
     *     <li>fire an event of type {@link ProcessInjectionTarget}</li>
     *     <li>fire an event of type {@link ProcessBeanAttributes}</li>
     *     <li>fire an event of type {@link ProcessBeanEvent} if {@link ProcessBeanAttributes#veto()}
     *     wasn’t called in previous step</li>
     * </ol>
     *
     * @param annotatedType
     * @param beanClass
     * @see ProcessInjectionPoint
     * @see ProcessInjectionTarget
     * @see ProcessBeanAttributes
     * @see ProcessBeanEvent
     */
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

    /**
     * {@link AnnotatedType}s
     * discovered by the container use the fully qualified class name of {@link AnnotatedType#getJavaClass()} to identify the
     * type.
     *
     * @param type {@link AnnotatedType}
     * @return self
     */
    private StandardBeanManager addAnnotatedType(Class<?> type) {
        AnnotatedType annotatedType = createAnnotatedType(type);
        addAnnotatedType(type.getName(), annotatedType);
        fireProcessAnnotatedTypeEvent(annotatedType);
        return this;
    }

    public void removeAnnotatedType(AnnotatedType<?> annotatedType) {
        Set<String> keysToRemove = new LinkedHashSet<>();
        for (Map.Entry<String, AnnotatedType> entry : annotatedTypes.entrySet()) {
            if (Objects.equals(entry.getValue().getJavaClass(), annotatedType.getJavaClass())) {
                keysToRemove.add(entry.getKey());
            }
        }
        keysToRemove.forEach(annotatedTypes::remove);
    }

    private void addAnnotatedType(String id, AnnotatedType<?> type) {
        annotatedTypes.put(id, type);
    }

    public StandardBeanManager addSyntheticAnnotatedType(String id, AnnotatedType<?> type, Extension source) {
        addAnnotatedType(id, type);
        fireProcessSyntheticAnnotatedTypeEvent(type, source);
        return this;
    }

    public StandardBeanManager extensions(Extension... extensions) {
        iterate(extensions, this::addExtension);
        return this;
    }

    private void addExtension(Extension extension) {
        requireNonNull(extension, "The 'extension' argument must not be null!");
        extensions.put(extension.getClass(), extension);
    }

    public StandardBeanManager interceptorClasses(Class<?>... interceptorClasses) {
        iterate(interceptorClasses, beanArchiveManager::addInterceptorClass);
        return this;
    }

    public StandardBeanManager decoratorClasses(Class<?>... decoratorClasses) {
        iterate(decoratorClasses, beanArchiveManager::addDecoratorClass);
        return this;
    }

    public StandardBeanManager alternativeClasses(Class<?>... alternativeClasses) {
        iterate(alternativeClasses, beanArchiveManager::addAlternativeClass);
        return this;
    }

    public StandardBeanManager alternativeStereotypeClasses(Class<? extends Annotation>... alternativeStereotypeClasses) {
        iterate(alternativeStereotypeClasses, beanArchiveManager::addAlternativeStereotypeClass);
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

    public StandardBeanManager classLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.beanArchiveManager.setClassLoader(classLoader);
        return this;
    }

    public BeanArchiveManager getBeanArchiveManager() {
        return beanArchiveManager;
    }

    public void addBeanDiscoveryDefinitionError(Throwable t) {
        // TODO
    }

    public Boolean getScanImplicitProperty() {
        Boolean value = (Boolean) properties.get(SCAN_IMPLICIT_PROPERTY_NAME);
        if (value == null) {
            // try to Java System Properties
            value = "true".equalsIgnoreCase(getProperty(SCAN_IMPLICIT_PROPERTY_NAME));
        }
        return value;
    }

    public boolean isScanImplicitEnabled() {
        return Boolean.TRUE.equals(getScanImplicitProperty());
    }

}
