/**
 * Confucius commons project
 */
package org.geektimes.commons.filter;

import java.util.function.Predicate;

/**
 * {@link PackageNameClassFilter}
 *
 * @author <a href="mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see PackageNameClassFilter
 * @since 1.0.0
 */
public class PackageNameClassFilter implements Predicate<Class<?>> {

    private String packageName;
    private boolean includedSubPackages;
    private String subPackageNamePrefix;

    /**
     * Constructor
     *
     * @param packageName         the name of package
     * @param includedSubPackages included sub-packages
     */
    public PackageNameClassFilter(String packageName, boolean includedSubPackages) {
        this.packageName = packageName;
        this.includedSubPackages = includedSubPackages;
        this.subPackageNamePrefix = includedSubPackages ? packageName + "." : null;
    }

    @Override
    public boolean test(Class<?> filteredObject) {
        Package package_ = filteredObject.getPackage();
        String packageName = package_.getName();
        boolean accepted = packageName.equals(this.packageName);
        if (!accepted && includedSubPackages) {
            accepted = packageName.startsWith(subPackageNamePrefix);
        }
        return accepted;
    }
}
