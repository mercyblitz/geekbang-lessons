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

import org.geektimes.commons.lang.util.ClassLoaderUtils;
import org.geektimes.enterprise.inject.standard.AnnotatedTypeInjectionTargetFactory;
import org.geektimes.enterprise.inject.standard.ConstructorParameterInjectionPoint;
import org.geektimes.enterprise.inject.standard.FieldInjectionPoint;
import org.geektimes.enterprise.inject.standard.MethodParameterInjectionPoint;
import org.geektimes.enterprise.inject.standard.annotation.ReflectiveAnnotatedType;
import org.geektimes.enterprise.inject.standard.beans.AbstractBean;
import org.geektimes.enterprise.inject.standard.beans.GenericBeanAttributes;
import org.geektimes.enterprise.inject.standard.beans.InjectionTargetBean;
import org.geektimes.enterprise.inject.standard.beans.ManagedBean;
import org.geektimes.enterprise.inject.standard.beans.decorator.DecoratorBean;
import org.geektimes.enterprise.inject.standard.beans.interceptor.InterceptorBean;
import org.geektimes.enterprise.inject.standard.beans.producer.ProducerBean;
import org.geektimes.enterprise.inject.standard.beans.producer.ProducerFieldBean;
import org.geektimes.enterprise.inject.standard.beans.producer.ProducerMethodBean;
import org.geektimes.enterprise.inject.standard.context.mananger.ContextManager;
import org.geektimes.enterprise.inject.standard.disposer.DisposerMethodManager;
import org.geektimes.enterprise.inject.standard.event.*;
import org.geektimes.enterprise.inject.standard.event.application.*;
import org.geektimes.enterprise.inject.standard.observer.ObserverMethodManager;
import org.geektimes.enterprise.inject.standard.producer.ProducerFieldBeanAttributes;
import org.geektimes.enterprise.inject.standard.producer.ProducerFieldFactory;
import org.geektimes.enterprise.inject.standard.producer.ProducerMethodBeanAttributes;
import org.geektimes.enterprise.inject.standard.producer.ProducerMethodFactory;
import org.geektimes.enterprise.inject.util.Annotations;
import org.geektimes.interceptor.InterceptorManager;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.enterprise.context.*;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.*;
import javax.enterprise.inject.spi.*;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.Function;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.ServiceLoader.load;
import static org.geektimes.commons.lang.util.ArrayUtils.iterate;
import static org.geektimes.enterprise.inject.util.Beans.isAnnotatedVetoed;
import static org.geektimes.enterprise.inject.util.Beans.isManagedBean;
import static org.geektimes.enterprise.inject.util.Decorators.isDecorator;
import static org.geektimes.enterprise.inject.util.Disposers.resolveAndValidateDisposerMethods;
import static org.geektimes.enterprise.inject.util.Exceptions.newDefinitionException;
import static org.geektimes.enterprise.inject.util.Injections.getMethodParameterInjectionPoints;
import static org.geektimes.enterprise.inject.util.Injections.validateForbiddenAnnotation;
import static org.geektimes.enterprise.inject.util.Parameters.isConstructorParameter;
import static org.geektimes.enterprise.inject.util.Parameters.isMethodParameter;
import static org.geektimes.interceptor.InterceptorManager.getInstance;

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

    private ClassLoader classLoader;

    private final Map<String, Object> properties;

    private final ContextManager contextManager;

    private final BeanArchiveManager beanArchiveManager;

    private final ObserverMethodManager observerMethodsManager;

    private final DisposerMethodManager disposerMethodManager;

    private final InterceptorManager interceptorManager;

    private final Map<Class<? extends Extension>, Extension> extensions;


    private final Map<String, AnnotatedType<?>> beanTypes;

    private final Map<String, AnnotatedType<?>> alternativeTypes;

    private final Map<String, AnnotatedType<?>> interceptorTypes;

    private final Map<String, AnnotatedType<?>> decoratorTypes;

    private final Map<String, AnnotatedType<?>> syntheticTypes;


    private final List<ManagedBean<?>> managedBeans;

    private final List<Interceptor<?>> interceptorBeans;

    private final List<Decorator<?>> decoratorBeans;

    private final List<Bean<?>> genericBeans;


    private final Set<DefinitionException> definitionErrors;

    private final Set<DeploymentException> deploymentProblems;


    private boolean firedAfterBeanDiscoveryEvent = false;

    private boolean firedAfterDeploymentValidationEvent = false;

    public StandardBeanManager() {
        this.classLoader = ClassLoaderUtils.getClassLoader(getClass());

        this.contextManager = new ContextManager();
        this.beanArchiveManager = new BeanArchiveManager(this);
        this.observerMethodsManager = new ObserverMethodManager(this);
        this.disposerMethodManager = new DisposerMethodManager(this);
        this.interceptorManager = getInstance(classLoader);

        this.properties = new HashMap<>();
        this.extensions = new LinkedHashMap<>();
        this.beanTypes = new LinkedHashMap<>();
        this.alternativeTypes = new LinkedHashMap<>();
        this.interceptorTypes = new LinkedHashMap<>();
        this.decoratorTypes = new LinkedHashMap<>();
        this.syntheticTypes = new LinkedHashMap<>();

        this.managedBeans = new LinkedList<>();
        this.interceptorBeans = new LinkedList<>();
        this.decoratorBeans = new LinkedList<>();
        this.genericBeans = new LinkedList<>();

        this.definitionErrors = new LinkedHashSet<>();
        this.deploymentProblems = new LinkedHashSet<>();

    }

    @Override
    public Object getReference(Bean<?> bean, Type beanType, CreationalContext<?> ctx) {
        assertAfterDeploymentValidation();
        if (!Objects.equals(beanType.getTypeName(), bean.getBeanClass().getTypeName())) {
            throw new IllegalArgumentException(format("The given type[%s] is not a bean type[%s] of the given bean!",
                    beanType.getTypeName(),
                    bean.getBeanClass()));
        }
        Class<? extends Annotation> scope = bean.getScope();
        Context context = getContext(scope);
        return context.get((Contextual) bean, ctx);
    }

    @Override
    public Object getInjectableReference(InjectionPoint ij, CreationalContext<?> ctx) {
        assertAfterDeploymentValidation();
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
        assertAfterBeanDiscovery();
        // TODO
        return null;
    }

    @Override
    public Set<Bean<?>> getBeans(String name) {
        assertAfterBeanDiscovery();
        // TODO
        return null;
    }

    @Override
    public Bean<?> getPassivationCapableBean(String id) {
        assertAfterBeanDiscovery();
        // TODO
        return null;
    }

    @Override
    public <X> Bean<? extends X> resolve(Set<Bean<? extends X>> beans) {
        assertAfterBeanDiscovery();
        // TODO
        return null;
    }

    @Override
    public void validate(InjectionPoint injectionPoint) {
        assertAfterBeanDiscovery();
        validateInjectionPointType(injectionPoint);
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
     * Any legal bean type may be the required type of an injection point.
     * Furthermore, the required type of an injection point may contain a wildcard type parameter.
     * However, a type variable is not a legal injection point type.
     *
     * @param injectionPoint {@link InjectionPoint}
     * @throws DefinitionException If an injection point type is a type variable, the container automatically
     *                             detects the problem and treats it as a definition error.
     */
    private void validateInjectionPointType(InjectionPoint injectionPoint) throws DefinitionException {
        Type type = injectionPoint.getType();
        if (type instanceof TypeVariable) {
            throw newDefinitionException("A type variable[%s] is not a legal injection point[%s] type", type, injectionPoint);
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
        assertAfterBeanDiscovery();
        return (Set) observerMethodsManager.resolveObserverMethods(event, qualifiers);
    }

    @Override
    public List<Decorator<?>> resolveDecorators(Set<Type> types, Annotation... qualifiers) {
        assertAfterBeanDiscovery();
        // TODO
        return null;
    }

    @Override
    public List<Interceptor<?>> resolveInterceptors(InterceptionType type, Annotation... interceptorBindings) {
        assertAfterBeanDiscovery();
        // TODO
        return null;
    }

    @Override
    public boolean isScope(Class<? extends Annotation> annotationType) {
        return contextManager.isScope(annotationType);
    }

    @Override
    public boolean isNormalScope(Class<? extends Annotation> annotationType) {
        return contextManager.isNormalScope(annotationType);
    }

    @Override
    public boolean isPassivatingScope(Class<? extends Annotation> annotationType) {
        return contextManager.isPassivatingScope(annotationType);
    }

    @Override
    public boolean isQualifier(Class<? extends Annotation> annotationType) {
        return beanArchiveManager.isQualifier(annotationType);
    }

    @Override
    public boolean isInterceptorBinding(Class<? extends Annotation> annotationType) {
        return interceptorManager.isInterceptorBindingType(annotationType);
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
        return contextManager.getContext(scopeType);
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
        return new AnnotatedTypeInjectionTargetFactory<>(annotatedType, this);
    }

    @Override
    public <X> ProducerFactory<X> getProducerFactory(AnnotatedField<? super X> field, Bean<X> declaringBean) {
        return new ProducerFieldFactory(field, declaringBean, this);
    }

    @Override
    public <X> ProducerFactory<X> getProducerFactory(AnnotatedMethod<? super X> method, Bean<X> declaringBean) {
        return new ProducerMethodFactory(method, declaringBean, this);
    }

    @Override
    public <T> BeanAttributes<T> createBeanAttributes(AnnotatedType<T> type) {
        return new GenericBeanAttributes(type);
    }

    @Override
    public BeanAttributes<?> createBeanAttributes(AnnotatedMember<?> type) {
        if (type instanceof AnnotatedMethod) {
            return new ProducerMethodBeanAttributes<>((AnnotatedMethod) type);
        } else if (type instanceof AnnotatedField) {
            return new ProducerFieldBeanAttributes<>((AnnotatedField) type);
        }
        throw newDefinitionException("Current BeanManager can't support the specified AnnotatedMember[type:%s]", type);
    }

    @Override
    public <T> Bean<T> createBean(BeanAttributes<T> attributes, Class<T> beanClass,
                                  InjectionTargetFactory<T> injectionTargetFactory) {
        return new InjectionTargetBean(attributes, beanClass, injectionTargetFactory);
    }

    @Override
    public <T, X> Bean<T> createBean(BeanAttributes<T> attributes, Class<X> beanClass,
                                     ProducerFactory<X> producerFactory) {
        return new ProducerBean<>(attributes, beanClass, producerFactory);
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
        return (T) extensions.get(extensionClass);
    }

    @Override
    public <T> InterceptionFactory<T> createInterceptionFactory(CreationalContext<T> ctx, Class<T> clazz) {
        // TODO
        return null;
    }

    @Override
    public Event<Object> getEvent() {
        return observerMethodsManager;
    }

    @Override
    public Instance<Object> createInstance() {
        assertAfterBeanDiscovery();
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

    /**
     * When an application is stopped, the container performs the following steps:
     * <ol>
     *     <li>the container must destroy all contexts.</li>
     *     <li>the container must fire an event of type {@link BeforeShutdown}</li>
     * </ol>
     */
    public void shutdown() {
        destroyContexts();
        fireBeforeShutdownEvent();
    }

    private void destroyContexts() {
        contextManager.destroy();
    }

    private void fireBeforeShutdownEvent() {
        fireEvent(new BeforeShutdownEvent(this));
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

        if (includedInterceptor && interceptorManager.isInterceptorClass(type)) {
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

    public void addDefinitionError(Throwable t) {
        this.definitionErrors.add(new DefinitionException(t));
    }

    /**
     * Registers a deployment problem with the container, causing the container to abort deployment
     * after all observers have been notified.
     *
     * @param t {@link Throwable}
     */
    public void addDeploymentProblem(Throwable t) {
        this.deploymentProblems.add(new DeploymentException(t));
    }

    private void initializeBeanArchiveManager() {
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
        discoverExtensionObserverMethods();
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

        beanArchiveManager.discoverTypes();

        discoverBeanTypes().forEach(this::registerBeanType);

        discoverAlternativeTypes().forEach(this::registerAlternativeType);

        discoverInterceptorTypes().forEach(this::registerInterceptorType);

        discoverDecoratorTypes().forEach(this::registerDecoratorType);

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
        determineAlternativeBeans();
        determineInterceptorBeans();
        determineDecoratorBeans();
        determineManagedBeans();
    }

    private void determineManagedBeans() {
        for (AnnotatedType<?> beanType : beanTypes.values()) {
            Class<?> beanClass = beanType.getJavaClass();
            if (isManagedBean(beanClass)) {
                determineManagedBean(beanType);
            }
        }
    }

    /**
     * Determine Managed Bean in Bean discovery, and fire events as below:
     *
     * <ol>
     *     <li>fire an event of type {@link ProcessInjectionPoint} for each injection point in the class</li>
     *     <li>fire an event of type {@link ProcessInjectionTarget}</li>
     *     <li>fire an event of type {@link ProcessBeanAttributes}</li>
     *     <li>fire an event of type {@link ProcessBeanEvent} if {@link ProcessBeanAttributes#veto()}
     *     wasn’t called in previous step</li>
     * </ol>
     *
     * @param beanType
     * @see ProcessInjectionPoint
     * @see ProcessInjectionTarget
     * @see ProcessBeanAttributes
     * @see ProcessBeanEvent
     */
    private void determineManagedBean(AnnotatedType beanType) {
        ManagedBean managedBean = new ManagedBean(beanType, this);

        fireProcessInjectionPointEvents(managedBean);
        fireProcessInjectionTarget(beanType, managedBean);
        fireProcessBeanAttributesEvent(beanType, managedBean);
        if (!managedBean.isVetoed()) { // vetoed if ProcessBeanAttributes.veto() method was invoked
            fireProcessBeanEvent(beanType, managedBean);
            determineProducerMethods(managedBean);
            determineProducerFields(managedBean);
            determineDisposerMethods(managedBean);
            determineObserverMethods(managedBean);
            registerBean(managedBean);
        }
    }

    private void determineProducerMethods(ManagedBean managedBean) {
        Set<ProducerMethodBean> producerMethodBeans = managedBean.getProducerMethodBeans();
        producerMethodBeans.forEach(this::determineProducerMethod);
    }

    private void determineProducerMethod(ProducerMethodBean producerMethodBean) {
        determineProducer(producerMethodBean, producerMethodBean.getMethod(), producerMethodBean);
    }

    private void determineProducerFields(ManagedBean managedBean) {
        Set<ProducerFieldBean> producerFieldBeans = managedBean.getProducerFieldBeans();
        producerFieldBeans.forEach(this::determineProducerField);
    }

    private void determineProducerField(ProducerFieldBean producerFieldBean) {
        determineProducer(producerFieldBean, producerFieldBean.getProducerField(), producerFieldBean);
    }

    private void determineProducer(Producer producer, AnnotatedMember annotatedMember, AbstractBean bean) {
        AnnotatedType annotatedType = bean.getBeanType();
        registerBeanType(annotatedType);
        Set<InjectionPoint> injectionPoints = producer.getInjectionPoints();
        fireProcessInjectionPointEvents(injectionPoints);
        fireProcessProducerEvent(annotatedMember, producer);
        fireProcessBeanAttributesEvent(annotatedMember, bean);
        fireProcessBeanEvent(annotatedType, bean);
    }

    /**
     * For each enabled bean, the container must search for disposer methods as defined in Disposer methods,
     * and for each disposer method
     * <p>
     * and then fire an event of type ProcessInjectionPoint for each injection point in the method parameters
     *
     * @param managedBean {@link ManagedBean}
     */
    private void determineDisposerMethods(ManagedBean managedBean) {
        Map<Type, AnnotatedMethod> disposerMethods = resolveAndValidateDisposerMethods(managedBean);
        disposerMethodManager.registerDisposerMethods(disposerMethods);
        for (AnnotatedMethod disposerMethod : disposerMethods.values()) {
            Set<MethodParameterInjectionPoint> injectionPoints = getMethodParameterInjectionPoints(disposerMethod, managedBean);
            fireProcessInjectionPointEvents(injectionPoints);
        }
    }


    /**
     * For each enabled bean, the container must search the class for observer methods, and for each observer method:
     * <ul>
     *     <li>fire an event of type ProcessInjectionPoint for each injection point in the method parameters</li>
     *     <li>fire an event of type ProcessObserverMethod</li>
     * </ul>
     *
     * @param managedBean {@link ManagedBean}
     */
    private void determineObserverMethods(ManagedBean managedBean) {
        observerMethodsManager.registerObserverMethods(managedBean);
    }

    private void registerManagedBean(ManagedBean managedBean) {
        this.managedBeans.add(managedBean);
    }

    private void determineAlternativeBeans() {
        for (AnnotatedType<?> alternativeType : alternativeTypes.values()) {
            determineAlternativeBean(alternativeType);
        }
    }

    private void determineAlternativeBean(AnnotatedType<?> alternativeType) {
        // TODO
    }

    /**
     * support for enabling interceptors only for a bean archive
     * <p>
     * the ability to override the interceptor order using the portable extension SPI,
     * defined in {@link AfterTypeDiscovery} event.
     */
    private void determineInterceptorBeans() {
        for (AnnotatedType<?> interceptorType : interceptorTypes.values()) {
            determineInterceptorBean(interceptorType);
        }
    }

    private void determineInterceptorBean(AnnotatedType<?> interceptorType) {
        InterceptorBean<?> interceptorBean = new InterceptorBean(interceptorType, this);
        fireProcessBeanAttributesEvent(interceptorType, interceptorBean);
        if (!interceptorBean.isVetoed()) {
            fireProcessBeanEvent(interceptorType, interceptorBean);
            registerInterceptorClass(interceptorType);
            registerBean(interceptorBean);
        }
    }

    public void registerBean(Bean<?> bean) {
        if (bean instanceof Interceptor) {
            registerInterceptorBean((Interceptor) bean);
        } else if (bean instanceof Decorator) {
            registerDecoratorBean((Decorator) bean);
        } else if (bean instanceof ManagedBean) {
            registerManagedBean((ManagedBean) bean);
        } else {
            genericBeans.add(bean);
        }
        contextManager.addBean(bean);
    }

    private void registerInterceptorBean(Interceptor<?> interceptorBean) {
        this.interceptorBeans.add(interceptorBean);
    }

    private void registerInterceptorClass(AnnotatedType<?> interceptorType) {
        Class<?> interceptorClass = interceptorType.getJavaClass();
        interceptorManager.registerInterceptorClass(interceptorClass);
    }

    private void determineDecoratorBeans() {
        for (AnnotatedType<?> decoratorType : decoratorTypes.values()) {
            determineDecoratorBean(decoratorType);
        }
    }

    private void determineDecoratorBean(AnnotatedType<?> decoratorType) {
        DecoratorBean<?> decoratorBean = new DecoratorBean(decoratorType, this);
        fireProcessBeanAttributesEvent(decoratorType, decoratorBean);
        if (!decoratorBean.isVetoed()) { // vetoed if ProcessBeanAttributes.veto() method was invoked
            fireProcessBeanEvent(decoratorType, decoratorBean);
            registerBean(decoratorBean);
        }
    }

    private void registerDecoratorBean(Decorator<?> decoratorBean) {
        this.decoratorBeans.add(decoratorBean);
    }

    private void registerBeans() {
        // TODO
    }

    private void registerInterceptorBeans() {
        // TODO
    }

    private void registerDecoratorBeans() {
        // TODO
    }

    /**
     * the container must fire an event of type AfterBeanDiscovery, as defined in AfterBeanDiscovery event, and abort
     * initialization of the application if any observer registers a definition error.
     * An exception is thrown if the following operations are called before the AfterBeanDiscovery event is fired:
     * <ul>
     *     <li>{@link #getBeans(String)}</li>
     *     <li>{@link #getBeans(Type, Annotation...)}</li>
     *     <li>{@link #getPassivationCapableBean(String)}</li>
     *     <li>{@link #resolve(Set)}</li>
     *     <li>{@link #resolveDecorators(Set, Annotation...)}</li>
     *     <li>{@link #resolveInterceptors(InterceptionType, Annotation...)}</li>
     *     <li>{@link #resolveObserverMethods(Object, Annotation...)}</li>
     *     <li>{@link #validate(InjectionPoint)}</li>
     *  </ul>
     */
    private void performAfterBeanDiscovery() {
        fireAfterBeanDiscoveryEvent();
    }

    private void assertAfterBeanDiscovery() {
        if (!firedAfterBeanDiscoveryEvent) {
            throw new UnsupportedOperationException("Current operation must not be invoked before " +
                    "AfterBeanDiscovery event is fired!");
        }
    }

    /**
     * the container must detect deployment problems by validating bean dependencies and specialization and abort
     * initialization of the application if any deployment problems exist, as defined in Problems detected automatically
     * by the container.
     */
    private void performDeploymentValidation() {
        validateBeanDependencies();
        validateBeanSpecialization();
        abortDeploymentIfProblemsDetected();
    }

    private void validateBeanDependencies() {
        validateEnabledBeanDependencies();
        validateInterceptorBeanDependencies();
        validateDecoratorBeanDependencies();
    }

    private void validateEnabledBeanDependencies() {
        validateManagedBeanDependencies();
        // TODO
    }

    private void validateManagedBeanDependencies() {
        managedBeans.forEach(this::validateManagedBeanDependencies);
    }

    private void validateManagedBeanDependencies(ManagedBean<?> managedBean) {
        for (InjectionPoint injectionPoint : managedBean.getInjectionPoints()) {
            Type requiredType = injectionPoint.getType();
        }
        // TODO
    }

    private void validateInterceptorBeanDependencies() {
        // TODO
    }

    private void validateDecoratorBeanDependencies() {
        // TODO
    }

    private void validateBeanSpecialization() {
        // TODO
    }

    private void abortDeploymentIfProblemsDetected() {
        // TODO
    }

    /**
     * the container must fire an event of type AfterDeploymentValidation, as defined in AfterDeploymentValidation event,
     * and abort initialization of the application if any observer registers a deployment problem.
     * An exception is thrown if the following operations are called before the AfterBeanDiscovery event is fired:
     * <ul>
     *     <li>{@link #createInstance()}</li>
     *     <li>{@link #getReference(Bean, Type, CreationalContext)}</li>
     *     <li>{@link #getInjectableReference(InjectionPoint, CreationalContext)}</li>
     *  </ul>
     */
    private void performAfterDeploymentValidation() {
        fireAfterDeploymentValidationEvent();
    }

    private void assertAfterDeploymentValidation() {
        if (!firedAfterDeploymentValidationEvent) {
            throw new UnsupportedOperationException("Current operation must not be invoked before " +
                    "AfterDeploymentValidation event is fired");
        }
    }

    private StandardBeanManager discoverExtensions() {
        load(Extension.class, classLoader).forEach(this::addExtension);
        return this;
    }

    private StandardBeanManager discoverExtensionObserverMethods() {
        extensions.values().forEach(this::registerObserverMethods);
        return this;
    }


    // Event Methods

    private void fireBeforeBeanDiscoveryEvent() {
        fireEvent(new BeforeBeanDiscoveryEvent(this));
    }

    /**
     * The container must fire an event, before it processes a type, for every Java class, interface
     * (excluding annotation type, a special kind of interface type) or enum discovered as defined
     * in Type discovery.
     * An event is not fired for any type annotated with {@link Vetoed @Vetoed},
     * or in a package annotated with {@link Vetoed @Vetoed}
     *
     * @param annotatedType {@link AnnotatedType}
     */
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
        fireProcessInjectionPointEvents(managedBean.getInjectionPoints());
    }

    private void fireProcessInjectionPointEvents(Collection<? extends InjectionPoint> injectionPoints) {
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

    private void fireProcessBeanAttributesEvent(Annotated annotated, AbstractBean beanAttributes) {
        fireEvent(new ProcessBeanAttributesEvent(annotated, beanAttributes, this));
    }

    /**
     * if the class is an enabled bean, interceptor or decorator and if ProcessBeanAttributes.veto() wasn’t called
     * in previous step, fire an event which is a subtype of ProcessBean, as defined in ProcessBean event.
     *
     * @param type {@link AnnotatedType}
     * @param bean {@link Bean}
     */
    private void fireProcessBeanEvent(AnnotatedType<?> type, AbstractBean bean) {
        if (hasBeanType(type)) {
            fireEvent(new ProcessBeanEvent<>(type, bean, this));
        }
    }

    /**
     * Fire an event of type ProcessProducer, as defined in ProcessProducer event,
     *
     * @param annotatedMember {@link AnnotatedMethod} or {@link AnnotatedField}
     * @param producer        {@link Producer} method of field
     */
    private void fireProcessProducerEvent(AnnotatedMember annotatedMember, Producer producer) {
        fireEvent(new ProcessProducerEvent(annotatedMember, producer, this));
    }

    /**
     * Fire fire an event when it has fully completed the bean discovery process, validated that there are
     * no definition errors relating to the discovered beans, and registered Bean and ObserverMethod objects
     * for the discovered beans.
     */
    private void fireAfterBeanDiscoveryEvent() {
        fireEvent(new AfterBeanDiscoveryEvent(this));
        firedAfterBeanDiscoveryEvent = true;
    }


    /**
     * Fire an event after it has validated that there are no deployment problems and before creating contexts
     * or processing requests.
     */
    private void fireAfterDeploymentValidationEvent() {
        fireEvent(new AfterDeploymentValidationEvent(this));
        firedAfterDeploymentValidationEvent = true;
    }

    private void fireEvent(Object event) {
        observerMethodsManager.fire(event);
    }

    /**
     * @param beanType
     */
    private void registerBeanType(AnnotatedType<?> beanType) {
        registerAnnotatedType(beanType, beanTypes);
    }

    private void registerAlternativeType(AnnotatedType<?> alternativeType) {
        registerAnnotatedType(alternativeType, alternativeTypes);
    }

    private void registerInterceptorType(AnnotatedType<?> interceptorType) {
        registerAnnotatedType(interceptorType, interceptorTypes);
    }

    private void registerDecoratorType(AnnotatedType<?> decoratorType) {
        registerAnnotatedType(decoratorType, decoratorTypes);
    }

    private void registerAnnotatedType(AnnotatedType<?> annotatedType, Map<String, AnnotatedType<?>> typesToRegister) {
        registerAnnotatedType(annotatedType, annotatedType.getJavaClass().getName(), typesToRegister);
    }

    /**
     * {@link AnnotatedType}s discovered by the container use the fully qualified class name of
     * {@link AnnotatedType#getJavaClass()} to identify the type
     *
     * @param annotatedType   {@link AnnotatedType}
     * @param id              ID
     * @param typesToRegister the collection types to register
     */
    private void registerAnnotatedType(AnnotatedType<?> annotatedType, String id, Map<String, AnnotatedType<?>> typesToRegister) {
        typesToRegister.put(id, annotatedType);
        fireProcessAnnotatedTypeEvent(annotatedType);
    }

    private boolean hasBeanType(AnnotatedType annotatedType) {
        return this.beanTypes.containsValue(annotatedType);
    }

    public void removeType(AnnotatedType<?> annotatedType) {
        removeType(annotatedType, beanTypes);
        removeType(annotatedType, alternativeTypes);
        removeType(annotatedType, interceptorTypes);
        removeType(annotatedType, decoratorTypes);
        removeType(annotatedType, syntheticTypes);
    }

    private static void removeType(AnnotatedType<?> annotatedType, Map<String, AnnotatedType<?>> typesMap) {
        Set<String> keysToRemove = new LinkedHashSet<>();
        for (Map.Entry<String, AnnotatedType<?>> entry : typesMap.entrySet()) {
            if (Objects.equals(entry.getValue().getJavaClass(), annotatedType.getJavaClass())) {
                keysToRemove.add(entry.getKey());
            }
        }
        keysToRemove.forEach(typesMap::remove);
    }

    private void registerSyntheticType(String id, AnnotatedType<?> type) {
        syntheticTypes.put(id, type);
    }

    public void addSyntheticAnnotatedType(String id, AnnotatedType<?> type, Extension source) {
        registerSyntheticType(id, type);
        fireProcessSyntheticAnnotatedTypeEvent(type, source);
    }

    private void registerObserverMethods(Object beanInstance) {
        observerMethodsManager.registerObserverMethods(beanInstance);
    }

    public void registerObserverMethod(ObserverMethod<?> observerMethod) {
        observerMethodsManager.registerObserverMethod(observerMethod);
    }

    public StandardBeanManager extensions(Extension... extensions) {
        iterate(extensions, this::addExtension);
        return this;
    }

    private void addExtension(Extension extension) {
        requireNonNull(extension, "The 'extension' argument must not be null!");
        extensions.put(extension.getClass(), extension);
    }

    public StandardBeanManager syntheticInterceptors(Class<?>... interceptorClasses) {
        iterate(interceptorClasses, beanArchiveManager::addSyntheticInterceptorClass);
        return this;
    }

    public StandardBeanManager syntheticDecorators(Class<?>... decoratorClasses) {
        iterate(decoratorClasses, beanArchiveManager::addSyntheticDecoratorClass);
        return this;
    }

    public StandardBeanManager syntheticAlternatives(Class<?>... alternativeClasses) {
        iterate(alternativeClasses, beanArchiveManager::addSyntheticAlternativeClass);
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
        return this;
    }

    public ContextManager getContextManager() {
        return contextManager;
    }

    public BeanArchiveManager getBeanArchiveManager() {
        return beanArchiveManager;
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

    public DisposerMethodManager getDisposerMethodManager() {
        return disposerMethodManager;
    }

    private Set<AnnotatedType<?>> discoverBeanTypes() {
        return createAnnotatedTypes(beanArchiveManager.getBeanClasses());
    }

    private Set<AnnotatedType<?>> discoverAlternativeTypes() {
        return createAnnotatedTypes(beanArchiveManager.getAlternativeClasses());
    }

    private Set<AnnotatedType<?>> discoverInterceptorTypes() {
        return createAnnotatedTypes(beanArchiveManager.getInterceptorClasses());
    }

    public Set<AnnotatedType<?>> discoverDecoratorTypes() {
        return createAnnotatedTypes(beanArchiveManager.getDecoratorClasses());
    }

    private Set<AnnotatedType<?>> createAnnotatedTypes(Iterable<Class<?>> classes) {
        Set<AnnotatedType<?>> annotatedTypes = new LinkedHashSet<>();
        for (Class<?> klass : classes) {
            annotatedTypes.add(createAnnotatedType(klass));
        }
        return annotatedTypes;
    }

    public <T> AnnotatedType<T> getAnnotatedType(Class<T> type, String id) {
        return id == null ? getAnnotatedType(type.getName()) : getAnnotatedType(id);
    }

    public <T> AnnotatedType<T> getAnnotatedType(String id) {
        AnnotatedType<T> annotatedType = getAnnotatedType(id, beanTypes::get);

        if (annotatedType == null) {
            annotatedType = getAnnotatedType(id, alternativeTypes::get);
        }

        if (annotatedType == null) {
            annotatedType = getAnnotatedType(id, interceptorTypes::get);
        }

        if (annotatedType == null) {
            annotatedType = getAnnotatedType(id, decoratorTypes::get);
        }

        return annotatedType;
    }

    private <T> AnnotatedType<T> getAnnotatedType(String id, Function<Object, AnnotatedType<?>> typeMapping) {
        return (AnnotatedType<T>) typeMapping.apply(id);
    }

    public <T> Iterable<AnnotatedType<T>> getAnnotatedTypes(Class<T> type) {
        List<AnnotatedType<T>> annotatedTypes = new LinkedList<>();
        addAnnotatedType(type, beanTypes, annotatedTypes);
        addAnnotatedType(type, alternativeTypes, annotatedTypes);
        addAnnotatedType(type, interceptorTypes, annotatedTypes);
        addAnnotatedType(type, decoratorTypes, annotatedTypes);
        return unmodifiableList(annotatedTypes);
    }

    private <T> void addAnnotatedType(Class<T> type, Map<String, AnnotatedType<?>> types,
                                      List<AnnotatedType<T>> annotatedTypes) {
        AnnotatedType<T> annotatedType = getAnnotatedType(type.getName(), types::get);
        if (annotatedType != null) {
            annotatedTypes.add(annotatedType);
        }
    }


    public ClassLoader getClassLoader() {
        return classLoader;
    }

}