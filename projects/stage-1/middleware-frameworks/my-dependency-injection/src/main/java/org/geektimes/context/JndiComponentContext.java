package org.geektimes.context;


import org.geektimes.commons.function.ThrowableAction;
import org.geektimes.context.repository.JndiComponentRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java JNDI 组件上下文
 */
public class JndiComponentContext implements ComponentContext {

    public static final String CONTEXT_NAME = JndiComponentContext.class.getName();

    private static ServletContext servletContext; // 请注意
    // 假设一个 Tomcat JVM 进程，三个 Web Apps，会不会相互冲突？（不会冲突）
    // static 字段是 JVM 缓存吗？（是 ClassLoader 缓存）

    private JndiComponentRepository repository = new JndiComponentRepository();

    /**
     * @PreDestroy 方法缓存，Key 为标注方法，Value 为方法所属对象
     */
    private Map<Method, Object> preDestroyMethodCache = new LinkedHashMap<>();

    /**
     * 获取 ComponentContext
     *
     * @return
     */
    public static JndiComponentContext getInstance() {
        return (JndiComponentContext) servletContext.getAttribute(CONTEXT_NAME);
    }

    public void init(ServletContext servletContext) throws RuntimeException {
        JndiComponentContext.servletContext = servletContext;
        servletContext.setAttribute(CONTEXT_NAME, this);
        this.initialize();
    }

    /**
     * 实例化组件
     */
    protected void instantiateComponents() {
        // 遍历获取所有的组件名称
        Set<String> componentNames = repository.listComponentNames();
        // 通过依赖查找，实例化对象（ Tomcat BeanFactory setter 方法的执行，仅支持简单类型）
        componentNames.forEach(repository::getComponent);
    }

    /**
     * 初始化组件（支持 Java 标准 Commons Annotation 生命周期）
     * <ol>
     *  <li>注入阶段 - {@link Resource}</li>
     *  <li>初始阶段 - {@link PostConstruct}</li>
     *  <li>销毁阶段 - {@link PreDestroy}</li>
     * </ol>
     */
    protected void initializeComponents() {
        repository.getComponents().values().forEach(this::initializeComponent);
    }

    /**
     * 初始化组件（支持 Java 标准 Commons Annotation 生命周期）
     * <ol>
     *  <li>注入阶段 - {@link Resource}</li>
     *  <li>初始阶段 - {@link PostConstruct}</li>
     *  <li>销毁阶段 - {@link PreDestroy}</li>
     * </ol>
     */
    public void initializeComponent(Object component) {
        Class<?> componentClass = component.getClass();
        // 注入阶段 - {@link Resource}
        injectComponent(component, componentClass);
        // 查询候选方法
        List<Method> candidateMethods = findCandidateMethods(componentClass);
        // 初始阶段 - {@link PostConstruct}
        processPostConstruct(component, candidateMethods);
        // 本阶段处理 {@link PreDestroy} 方法元数据
        processPreDestroyMetadata(component, candidateMethods);
    }

    /**
     * 获取组件类中的候选方法
     *
     * @param componentClass 组件类
     * @return non-null
     */
    private List<Method> findCandidateMethods(Class<?> componentClass) {
        return Stream.of(componentClass.getMethods())                     // public 方法
                .filter(method ->
                        !Modifier.isStatic(method.getModifiers()) &&      // 非 static
                                method.getParameterCount() == 0)          // 无参数
                .collect(Collectors.toList());
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            processPreDestroy();
        }));
    }

    public void injectComponent(Object component) {
        injectComponent(component, component.getClass());
    }

    protected void injectComponent(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getDeclaredFields())
                .filter(field -> {
                    int mods = field.getModifiers();
                    return !Modifier.isStatic(mods) &&
                            field.isAnnotationPresent(Resource.class);
                }).forEach(field -> {
            Resource resource = field.getAnnotation(Resource.class);
            String resourceName = resource.name();
            Object injectedObject = lookupComponent(resourceName);
            field.setAccessible(true);
            try {
                // 注入目标对象
                field.set(component, injectedObject);
            } catch (IllegalAccessException e) {
            }
        });
    }

    private void processPostConstruct(Object component, List<Method> candidateMethods) {
        candidateMethods
                .stream()
                .filter(method -> method.isAnnotationPresent(PostConstruct.class))// 标注 @PostConstruct
                .forEach(method -> {
                    // 执行目标方法
                    ThrowableAction.execute(() -> method.invoke(component));
                });
    }

    /**
     * @param component        组件对象
     * @param candidateMethods 候选方法
     * @see #processPreDestroy()
     */
    private void processPreDestroyMetadata(Object component, List<Method> candidateMethods) {
        candidateMethods.stream()
                .filter(method -> method.isAnnotationPresent(PreDestroy.class)) // 标注 @PreDestroy
                .forEach(method -> {
                    preDestroyMethodCache.put(method, component);
                });
    }

    public <C> C lookupComponent(String name) {
        return repository.loadComponent(name);
    }

    public <C> C getComponent(String name) {
        return repository.getComponent(name);
    }

    @Override
    public void registerComponent(String name, Object component) {
        repository.registerComponent(name, component);
    }

    /**
     * 获取所有的组件名称
     *
     * @return
     */
    public Set<String> getComponentNames() {
        return repository.getComponentNames();
    }

    @Override
    public void initialize() {
        repository.initialize();
        instantiateComponents();
        initializeComponents();
        registerShutdownHook();
    }

    @Override
    public void destroy() throws RuntimeException {
        repository.destroy();
        processPreDestroy();
        clearCache();
    }

    private void processPreDestroy() {
        for (Method preDestroyMethod : preDestroyMethodCache.keySet()) {
            // 移除集合中的对象，防止重复执行 @PreDestroy 方法
            Object component = preDestroyMethodCache.remove(preDestroyMethod);
            // 执行目标方法
            ThrowableAction.execute(() -> preDestroyMethod.invoke(component));
        }
    }

    private void clearCache() {
        preDestroyMethodCache.clear();
    }


}
