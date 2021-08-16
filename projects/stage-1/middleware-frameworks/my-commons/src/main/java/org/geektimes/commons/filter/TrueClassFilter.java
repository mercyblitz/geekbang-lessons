/**
 * Confucius commons project
 */
package org.geektimes.commons.filter;

/**
 * {@link Class} {@link Filter} returns <code>true</code> forever
 *
 * @author <a href="mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see ClassFilter
 * @since 1.0.0
 */
public class TrueClassFilter implements ClassFilter {

    /**
     * Singleton {@link TrueClassFilter} instance
     */
    public static final TrueClassFilter INSTANCE = new TrueClassFilter();

    private TrueClassFilter() {

    }

    @Override
    public boolean accept(Class<?> filteredObject) {
        return true;
    }
}
