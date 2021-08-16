/**
 *
 */
package org.geektimes.commons.filter;

/**
 * {@link Filter} interface
 *
 * @param <T>
 *         the type of Filtered object
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see Filter
 * @since 1.0.0
 */
@FunctionalInterface
public interface Filter<T> {

    /**
     * Does accept filtered object?
     *
     * @param filteredObject
     *         filtered object
     * @return
     */
    boolean accept(T filteredObject);
}
