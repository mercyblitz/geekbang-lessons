package org.geektimes.commons.filter;

import static org.geektimes.commons.lang.util.ArrayUtils.length;

/**
 * {@link Filter} Operator enumeration , which contains {@link #AND}��{@link #OR}��{@link #XOR}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @version 1.0.0
 * @see Filter
 * @see #AND
 * @see #OR
 * @see #XOR
 * @since 1.0.0
 */
public enum FilterOperator {

    /**
     * &
     */
    AND {
        @Override
        public <T> boolean accept(T filteredObject, Filter<T>... filters) {
            int length = length(filters);
            if (length == 0)
                return true;
            boolean success = true;
            for (Filter<T> filter : filters) {
                success &= filter.accept(filteredObject);
            }
            return success;
        }

    },
    /**
     * |
     */
    OR {
        @Override
        public <T> boolean accept(T filteredObject, Filter<T>... filters) {
            int length = length(filters);
            if (length == 0)
                return true;
            boolean success = false;
            for (Filter<T> filter : filters) {
                success |= filter.accept(filteredObject);
            }
            return success;
        }
    },
    /**
     * XOR
     */
    XOR {
        @Override
        public <T> boolean accept(T filteredObject, Filter<T>... filters) {
            int length = length(filters);
            if (length == 0)
                return true;
            boolean success = true;
            for (Filter<T> filter : filters) {
                success ^= filter.accept(filteredObject);
            }
            return success;
        }
    };

    /**
     * multiple {@link Filter} accept
     *
     * @param <T>            Filtered object type
     * @param filteredObject Filtered object
     * @param filters        multiple {@link Filter}
     * @return If accepted return <code>true</code>
     * @version 1.0.0
     * @since 1.0.0
     */
    public abstract <T> boolean accept(T filteredObject, Filter<T>... filters);

    /**
     * Create a combined {@link Filter} from multiple filters
     *
     * @param filters multiple filters
     * @param <T>
     * @return a combined {@link Filter}
     */
    public final <T> Filter<T> createFilter(final Filter<T>... filters) {
        final FilterOperator self = this;
        return new Filter<T>() {

            @Override
            public boolean accept(T filteredObject) {
                return self.accept(filteredObject, filters);
            }
        };
    }

}
