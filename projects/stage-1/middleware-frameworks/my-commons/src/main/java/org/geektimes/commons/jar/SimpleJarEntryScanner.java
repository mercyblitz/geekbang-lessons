/**
 *
 */
package org.geektimes.commons.jar;

import org.geektimes.commons.constants.PathConstants;
import org.geektimes.commons.jar.util.JarUtils;
import org.geektimes.commons.lang.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.util.Collections.unmodifiableSet;

/**
 * Simple {@link JarEntry} Scanner
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see SimpleJarEntryScanner
 * @since 1.0.0
 */
public class SimpleJarEntryScanner {

    /**
     * Singleton
     */
    public static final SimpleJarEntryScanner INSTANCE = new SimpleJarEntryScanner();

    protected SimpleJarEntryScanner() {

    }

    /**
     * @param jarURL
     *         {@link URL} of {@link JarFile} or {@link JarEntry}
     * @param recursive
     *         recursive
     * @return Read-only {@link Set}
     * @throws NullPointerException
     *         If argument <code>null</code>
     * @throws IllegalArgumentException
     *         <ul> <li>{@link JarUtils#resolveRelativePath(URL)}
     * @throws IOException
     *         <ul> <li>{@link JarUtils#toJarFile(URL)}
     * @since 1.0.0
     */

    public Set<JarEntry> scan(URL jarURL, final boolean recursive) throws NullPointerException, IllegalArgumentException, IOException {
        return scan(jarURL, recursive, null);
    }

    /**
     * @param jarURL
     *         {@link URL} of {@link JarFile} or {@link JarEntry}
     * @param recursive
     *         recursive
     * @param jarEntryFilter
     *         {@link Predicate<JarEntry>}
     * @return Read-only {@link Set}
     * @throws NullPointerException
     *         If argument <code>null</code>
     * @throws IllegalArgumentException
     *         {@link JarUtils#resolveJarAbsolutePath(URL)}
     * @throws IOException
     *         {@link JarUtils#toJarFile(URL)}
     * @see JarEntryFilter
     * @since 1.0.0
     */

    public Set<JarEntry> scan(URL jarURL, final boolean recursive, Predicate<JarEntry> jarEntryFilter) throws NullPointerException, IllegalArgumentException, IOException {
        String relativePath = JarUtils.resolveRelativePath(jarURL);
        JarFile jarFile = JarUtils.toJarFile(jarURL);
        return scan(jarFile, relativePath, recursive, jarEntryFilter);
    }


    /**
     * @param jarFile
     * @param recursive
     * @return
     * @throws NullPointerException
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public Set<JarEntry> scan(JarFile jarFile, final boolean recursive) throws NullPointerException, IllegalArgumentException, IOException {
        return scan(jarFile, recursive, null);
    }

    /**
     * @param jarFile
     * @param recursive
     * @param jarEntryFilter
     * @return
     * @throws NullPointerException
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public Set<JarEntry> scan(JarFile jarFile, final boolean recursive, Predicate<JarEntry> jarEntryFilter)
            throws NullPointerException, IllegalArgumentException, IOException {
        return scan(jarFile, StringUtils.EMPTY, recursive, jarEntryFilter);
    }

    protected Set<JarEntry> scan(JarFile jarFile, String relativePath, final boolean recursive,
                                 Predicate<JarEntry> jarEntryFilter)
            throws NullPointerException, IllegalArgumentException, IOException {
        Set<JarEntry> jarEntriesSet = new LinkedHashSet<>();
        List<JarEntry> jarEntriesList = JarUtils.filter(jarFile, jarEntryFilter);

        for (JarEntry jarEntry : jarEntriesList) {
            String jarEntryName = jarEntry.getName();

            boolean accept = false;
            if (recursive) {
                accept = jarEntryName.startsWith(relativePath);
            } else {
                if (jarEntry.isDirectory()) {
                    accept = jarEntryName.equals(relativePath);
                } else {
                    int beginIndex = jarEntryName.indexOf(relativePath);
                    if (beginIndex == 0) {
                        accept = jarEntryName.indexOf(PathConstants.SLASH, relativePath.length()) < 0;
                    }
                }
            }
            if (accept) {
                jarEntriesSet.add(jarEntry);
            }
        }
        return unmodifiableSet(jarEntriesSet);
    }
}
