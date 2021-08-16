/**
 *
 */
package org.geektimes.commons.io.util;

import java.io.File;

import static org.geektimes.commons.constants.SystemConstants.FILE_SEPARATOR;
import static org.geektimes.commons.lang.util.StringUtils.replace;
import static org.geektimes.commons.net.util.URLUtils.resolvePath;

/**
 * {@link File} Utility
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see FileUtils
 * @since 1.0.0
 */
public abstract class FileUtils {

    /**
     * Resolve Relative Path
     *
     * @param parentDirectory
     *         Parent Directory
     * @param targetFile
     *         Target File
     * @return If <code>targetFile</code> is a sub-file of <code>parentDirectory</code> , resolve relative path, or
     * <code>null</code>
     * @since 1.0.0
     */
    public static String resolveRelativePath(File parentDirectory, File targetFile) {
        String parentDirectoryPath = parentDirectory.getAbsolutePath();
        String targetFilePath = targetFile.getAbsolutePath();
        if (!targetFilePath.contains(parentDirectoryPath)) {
            return null;
        }
        return resolvePath(replace(targetFilePath, parentDirectoryPath, FILE_SEPARATOR));
    }
}
