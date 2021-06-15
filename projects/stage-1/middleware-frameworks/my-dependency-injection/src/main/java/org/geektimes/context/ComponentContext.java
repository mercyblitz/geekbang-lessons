package org.geektimes.context;

import org.geektimes.context.core.Lifecycle;
import org.geektimes.context.repository.ComponentRepository;

/**
 * 组件上下文
 */
public interface ComponentContext extends ComponentRepository, Lifecycle {
}
