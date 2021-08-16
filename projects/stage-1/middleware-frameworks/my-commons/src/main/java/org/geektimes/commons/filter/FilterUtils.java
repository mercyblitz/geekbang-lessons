/**
 * Confucius commons project
 */
package org.geektimes.commons.filter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.geektimes.commons.collection.util.CollectionUtils.newArrayList;

/**
 * {@link Filter} utility class
 *
 * @author <a href="mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see FilterUtils
 * @since 1.0.0
 */
public abstract class FilterUtils {

    private FilterUtils() {
    }

    /**
     * Filter {@link Iterable} object to List
     *
     * @param iterable
     *         {@link Iterable} object
     * @param filter
     *         {@link Filter} object
     * @param <E>
     *         The filtered object type
     * @return
     * @since 1.0.0
     */
    
    public static <E> List<E> filter(Iterable<E> iterable, Filter<E> filter) {
        return filter(iterable, FilterOperator.AND, filter);
    }

    /**
     * Filter {@link Iterable} object to List
     *
     * @param iterable
     *         {@link Iterable} object
     * @param filterOperator
     *         {@link FilterOperator}
     * @param filters
     *         {@link Filter} array objects
     * @param <E>
     *         The filtered object type
     * @return
     * @since 1.0.0
     */
    
    public static <E> List<E> filter(Iterable<E> iterable, FilterOperator filterOperator, Filter<E>... filters) {
        List<E> list = newArrayList(iterable);
        Iterator<E> iterator = list.iterator();
        while (iterator.hasNext()) {
            E element = iterator.next();
            if (!filterOperator.accept(element, filters)) {
                iterator.remove();
            }
        }
        return Collections.unmodifiableList(list);
    }
}
