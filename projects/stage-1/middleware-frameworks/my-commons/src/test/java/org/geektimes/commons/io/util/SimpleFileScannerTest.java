/**
 *
 */
package org.geektimes.commons.io.util;

import junit.framework.Assert;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.junit.Test;

import java.io.File;
import java.util.Set;

import static org.geektimes.commons.constants.SystemConstants.JAVA_HOME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * {@link SimpleFileScanner} {@link Test}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see SimpleFileScanner
 * @since 1.0.0
 */
public class SimpleFileScannerTest {

    private SimpleFileScanner simpleFileScanner = SimpleFileScanner.INSTANCE;

    @Test
    public void testScan() {
        File jarHome = new File(JAVA_HOME);
        Set<File> directories = simpleFileScanner.scan(jarHome, true, DirectoryFileFilter.INSTANCE);
        assertFalse(directories.isEmpty());

        directories = simpleFileScanner.scan(jarHome, false, new NameFileFilter("bin"));
        assertEquals(1, directories.size());
    }
}
