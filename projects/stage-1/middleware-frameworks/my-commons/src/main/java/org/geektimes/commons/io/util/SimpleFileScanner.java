package org.geektimes.commons.io.util;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple File Scanner (Single-Thread)
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SimpleFileScanner#INSTANCE
 * @see IOFileFilter
 * @since 1.0.0
 */
public class SimpleFileScanner {

    /**
     * Singleton
     */
    public final static SimpleFileScanner INSTANCE = new SimpleFileScanner();

    protected SimpleFileScanner() {

    }


    /**
     * Scan all {@link File} {@link Set} under root directory
     *
     * @param rootDirectory Root directory
     * @param recursive     is recursive on sub directories
     * @return Read-only {@link Set} , and the order be dependent on {@link File#listFiles()} implementation
     * @see IOFileFilter
     * @since 1.0.0
     */
    public Set<File> scan(File rootDirectory, boolean recursive) {
        return scan(rootDirectory, recursive, TrueFileFilter.INSTANCE);
    }

    /**
     * Scan all {@link File} {@link Set} that are accepted by {@link IOFileFilter} under root directory
     *
     * @param rootDirectory Root directory
     * @param recursive     is recursive on sub directories
     * @param ioFileFilter  {@link IOFileFilter}
     * @return Read-only {@link Set} , and the order be dependent on {@link File#listFiles()} implementation
     * @see IOFileFilter
     * @since 1.0.0
     */
    public Set<File> scan(File rootDirectory, boolean recursive, IOFileFilter ioFileFilter) {

        final Set<File> filesSet = new LinkedHashSet<>();

        if (ioFileFilter.accept(rootDirectory)) {
            filesSet.add(rootDirectory);
        }

        File[] subFiles = rootDirectory.listFiles();

        if (subFiles != null) {
            for (File subFile : subFiles) {
                if (ioFileFilter.accept(subFile)) {
                    filesSet.add(subFile);
                }
                if (recursive && subFile.isDirectory()) {
                    filesSet.addAll(this.scan(subFile, recursive, ioFileFilter));
                }
            }
        }
        return Collections.unmodifiableSet(filesSet);
    }

}
