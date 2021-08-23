/**
 * Confucius commons project
 */
package org.geektimes.commons.filter;

import java.util.function.Predicate;

import static org.geektimes.commons.reflect.util.ClassUtils.resolvePackageName;

/**
 * {@link PackageNameClassNameFilter}
 *
 * @author <a href="mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see PackageNameClassNameFilter
 * @since 1.0.0
 */
public class PackageNameClassNameFilter implements Predicate<String> {

    private String packageName;
    private boolean includedSubPackages;
    private String subPackageNamePrefix;

    /**
     * Constructor
     *
     * @param packageName         the name of package
     * @param includedSubPackages included sub-packages
     */
    public PackageNameClassNameFilter(String packageName, boolean includedSubPackages) {
        this.packageName = packageName;
        this.includedSubPackages = includedSubPackages;
        this.subPackageNamePrefix = includedSubPackages ? packageName + "." : null;
    }

    @Override
    public boolean test(String className) {
        String packageName = resolvePackageName(className);
        boolean accepted = packageName.equals(this.packageName);
        if (!accepted && includedSubPackages) {
            accepted = packageName.startsWith(subPackageNamePrefix);
        }
        return accepted;
    }
}
