package org.eclipse.dirigible.graalium.core.graal;

import org.eclipse.dirigible.graalium.core.graal.modules.downloadable.DownloadableModuleResolver;
import org.eclipse.dirigible.graalium.core.graal.modules.ModuleResolver;
import org.graalvm.polyglot.io.FileSystem;

import java.io.*;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

/**
 * The Class GraalJSFileSystem.
 */
public class GraalJSFileSystem implements FileSystem {

    /** The Constant DELEGATE. */
    private static final FileSystemProvider DELEGATE = FileSystems.getDefault().provider();
    
    /** The current working directory path. */
    private Path currentWorkingDirectoryPath;
    
    /** The module resolvers. */
    private final List<ModuleResolver> moduleResolvers;
    
    /** The downloadable module resolver. */
    private final DownloadableModuleResolver downloadableModuleResolver;

    /**
     * Instantiates a new graal JS file system.
     *
     * @param currentWorkingDirectoryPath the current working directory path
     * @param moduleResolvers the module resolvers
     * @param downloadableModuleResolver the downloadable module resolver
     */
    public GraalJSFileSystem(
            Path currentWorkingDirectoryPath,
            List<ModuleResolver> moduleResolvers,
            DownloadableModuleResolver downloadableModuleResolver
    ) {
        this.currentWorkingDirectoryPath = currentWorkingDirectoryPath;
        this.moduleResolvers = moduleResolvers;
        this.downloadableModuleResolver = downloadableModuleResolver;
    }

    /**
     * Parses the path.
     *
     * @param uri the uri
     * @return the path
     */
    @Override
    public Path parsePath(URI uri) {
        return downloadableModuleResolver.resolve(uri);
    }

    /**
     * Parses the path.
     *
     * @param path the path
     * @return the path
     */
    @Override
    public Path parsePath(String path) {
        if ("".equals(path)) {
            return currentWorkingDirectoryPath;
        }

        for (ModuleResolver moduleResolver : moduleResolvers) {
            if (moduleResolver.isResolvable(path)) {
                return moduleResolver.resolve(path);
            }
        }

        return Path.of(path);
    }

    /**
     * To absolute path.
     *
     * @param path the path
     * @return the path
     */
    @Override
    public Path toAbsolutePath(Path path) {
        return path.toAbsolutePath();
    }

    /**
     * To real path.
     *
     * @param path the path
     * @param linkOptions the link options
     * @return the path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
        String pathString = path.toString();
        if (!pathString.endsWith(".js") && !pathString.endsWith(".mjs")) {
            // handle cases like `import { Data } from "./data"` where `./data` does not have an extension
            // mainly found when dealing with TS imports
            path = Path.of(pathString + ".js");
        }
        return path.toRealPath(linkOptions);
    }

    /**
     * New byte channel.
     *
     * @param path the path
     * @param options the options
     * @param attrs the attrs
     * @return the seekable byte channel
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return DELEGATE.newByteChannel(path, options, attrs);
    }

    /**
     * Check access.
     *
     * @param path the path
     * @param modes the modes
     * @param linkOptions the link options
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
        if (isFollowLinks(linkOptions)) {
            DELEGATE.checkAccess(path, modes.toArray(new AccessMode[0]));
        } else if (modes.isEmpty()) {
            DELEGATE.readAttributes(path, "isRegularFile", LinkOption.NOFOLLOW_LINKS);
        } else {
            throw new UnsupportedOperationException("CheckAccess for NIO Provider is unsupported with non empty AccessMode and NOFOLLOW_LINKS.");
        }
    }

    /**
     * Creates the directory.
     *
     * @param dir the dir
     * @param attrs the attrs
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        DELEGATE.createDirectory(dir, attrs);
    }

    /**
     * Delete.
     *
     * @param path the path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void delete(Path path) throws IOException {
        DELEGATE.delete(path);
    }

    /**
     * Copy.
     *
     * @param source the source
     * @param target the target
     * @param options the options
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        DELEGATE.copy(source, target, options);
    }

    /**
     * Move.
     *
     * @param source the source
     * @param target the target
     * @param options the options
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        DELEGATE.move(source, target, options);
    }

    /**
     * New directory stream.
     *
     * @param dir the dir
     * @param filter the filter
     * @return the directory stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return DELEGATE.newDirectoryStream(dir, filter);
    }

    /**
     * Creates the link.
     *
     * @param link the link
     * @param existing the existing
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void createLink(Path link, Path existing) throws IOException {
        DELEGATE.createLink(link, existing);
    }

    /**
     * Creates the symbolic link.
     *
     * @param link the link
     * @param target the target
     * @param attrs the attrs
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
        DELEGATE.createSymbolicLink(link, target, attrs);
    }

    /**
     * Read symbolic link.
     *
     * @param link the link
     * @return the path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public Path readSymbolicLink(Path link) throws IOException {
        return DELEGATE.readSymbolicLink(link);
    }

    /**
     * Read attributes.
     *
     * @param path the path
     * @param attributes the attributes
     * @param options the options
     * @return the map
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return DELEGATE.readAttributes(path, attributes, options);
    }

    /**
     * Sets the attribute.
     *
     * @param path the path
     * @param attribute the attribute
     * @param value the value
     * @param options the options
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        DELEGATE.setAttribute(path, attribute, value, options);
    }

    /**
     * Sets the current working directory.
     *
     * @param currentWorkingDirectoryPath the new current working directory
     */
    @Override
    public void setCurrentWorkingDirectory(Path currentWorkingDirectoryPath) {
        this.currentWorkingDirectoryPath = currentWorkingDirectoryPath;
    }

    /**
     * Checks if is follow links.
     *
     * @param linkOptions the link options
     * @return true, if is follow links
     */
    private static boolean isFollowLinks(final LinkOption... linkOptions) {
        for (LinkOption lo : linkOptions) {
            if (lo == LinkOption.NOFOLLOW_LINKS) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if is same file.
     *
     * @param path1 the path 1
     * @param path2 the path 2
     * @param options the options
     * @return true, if is same file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public boolean isSameFile(Path path1, Path path2, LinkOption... options) throws IOException {
        return DELEGATE.isSameFile(path1, path2);
    }

    /**
     * Gets the temp directory.
     *
     * @return the temp directory
     */
    @Override
    public Path getTempDirectory() {
        return new File(System.getProperty("java.io.tmpdir")).toPath();
    }
}
