package org.geektimes.context;

import java.util.List;

/**
 * 组件上下文
 */
public interface ComponentContext {

    // 生命周期方法

    /**
     *
     */
    void init();

    /**
     * 上下文销毁方法
     */
    void destroy();

    // 组件操作方法

    /**
     * 通过名称查找组件对象
     *
     * @param name 组件名称
     * @param <C>  组件对象类型
     * @return 如果找不到返回, <code>null</code>
     */
    <C> C getComponent(String name);

    /**
     * 获取所有的组件名称
     *
     * @return
     */
    List<String> getComponentNames();
}
