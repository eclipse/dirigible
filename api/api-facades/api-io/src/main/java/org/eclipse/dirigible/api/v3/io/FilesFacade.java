package org.eclipse.dirigible.api.v3.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;

import org.eclipse.dirigible.commons.api.helpers.BytesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(FilesFacade.class);
	
	public static final boolean exists(String path) throws IOException {
		return Files.exists(Paths.get(path));
	}
	
	public static final boolean isExecutable(String path) throws IOException {
		return Files.isExecutable(Paths.get(path));
	}
	
	public static final boolean isReadable(String path) throws IOException {
		return Files.isReadable(Paths.get(path));
	}
	
	public static final boolean isWritable(String path) throws IOException {
		return Files.isReadable(Paths.get(path));
	}
	
	public static final boolean isHidden(String path) throws IOException {
		return Files.isHidden(Paths.get(path));
	}
	
	public static final boolean isDirectory(String path) throws IOException {
		return Files.isDirectory(Paths.get(path));
	}
	
	public static final boolean isFile(String path) throws IOException {
		return Files.isRegularFile(Paths.get(path));
	}
	
	public static final boolean isSameFile(String path1, String path2) throws IOException {
		return Files.isSameFile(Paths.get(path1), Paths.get(path2));
	}
	
	public static final String getCanonicalPath(String path) throws IOException {
		return new File(path).getCanonicalPath();
	}
	
	public static final String getName(String path) throws IOException {
		return new File(path).getName();
	}
	
	public static final String getParentPath(String path) throws IOException {
		return new File(path).getParentFile().getPath();
	}
	
	public static final byte[] read(String path) throws IOException {
		return Files.readAllBytes(Paths.get(path));
	}
	
	public static final String readText(String path) throws IOException {
		return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
	}
	
	public static final void write(String path, String input) throws IOException {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		Files.write(Paths.get(path), bytes);
	}
	
	public static final void writeText(String path, String text) throws IOException {
		Files.write(Paths.get(path), text.getBytes(StandardCharsets.UTF_8));
	}
	
	public static final long getLastModified(String path) throws IOException {
		return Files.getLastModifiedTime(Paths.get(path)).toMillis();
	}
	
	public static final void setLastModified(String path, long time) throws IOException {
		Files.setLastModifiedTime(Paths.get(path), FileTime.fromMillis(time));
	}
	
	public static final String getOwner(String path) throws IOException {
		return Files.getOwner(Paths.get(path)).getName();
	}
	
	public static final void setOwner(String path, String owner) throws IOException {
		UserPrincipal userPrincipal = Paths.get(path).getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(owner);
		Files.setOwner(Paths.get(path), userPrincipal);
	}
	
	public static final String getPermissions(String path) throws IOException {
		return PosixFilePermissions.toString(Files.getPosixFilePermissions(Paths.get(path)));
	}
	
	public static final void setPermissions(String path, String permissions) throws IOException {
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString(permissions);
	    Files.setPosixFilePermissions(Paths.get(path), perms);
	}
	
	public static final long size(String path) throws IOException {
		return Files.size(Paths.get(path));
	}
	
	public static final void createFile(String path) throws IOException {
		Files.createFile(Paths.get(path));
	}
	
	public static final void createDirectory(String path) throws IOException {
		Files.createDirectory(Paths.get(path));
	}
	
	public static final void copy(String source, String target) throws IOException {
		Files.copy(Paths.get(source), Paths.get(target));
	}
	
	public static final void move(String source, String target) throws IOException {
		Files.move(Paths.get(source), Paths.get(target));
	}
	
	public static final void deleteFile(String path) throws IOException {
		Files.deleteIfExists(Paths.get(path));
	}
	
	public static final void deleteDirectory(String path) throws IOException {
		Files.deleteIfExists(Paths.get(path));
	}
	
	public static final void deleteDirectory(String path, boolean forced) throws IOException {
		if (forced) {
			Files.walkFileTree(Paths.get(path), new FileVisitor<Path>() {
	
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	
					if (Files.exists(dir)) {
						logger.trace(String.format("Deleting directory: %s", dir));
						try {
							Files.delete(dir);
						} catch (java.nio.file.NoSuchFileException e) {
							logger.trace(String.format("Directory already has been deleted: %s", dir));
						}
					}
					return FileVisitResult.CONTINUE;
				}
	
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}
	
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (Files.exists(file)) {
						logger.trace(String.format("Deleting file: %s", file));
						try {
							Files.delete(file);
						} catch (java.nio.file.NoSuchFileException e) {
							logger.trace(String.format("File already has been deleted: %s", file));
						}
					}
					return FileVisitResult.CONTINUE;
				}
	
				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					logger.error(String.format("Error in file: %s", file), exc);
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			Files.deleteIfExists(Paths.get(path));
		}
	}
	
	public static final String createTempFile(String prefix, String suffix) throws IOException {
		return Files.createTempFile(prefix, suffix).toString();
	}
	
	public static final String createTempDirectory(String prefix) throws IOException {
		return Files.createTempDirectory(prefix).toString();
	}
	
	
	public static final InputStream createInputStream(String path) throws IOException {
		return Files.newInputStream(Paths.get(path));
	}
	
	public static final OutputStream createOutputStream(String path) throws IOException {
		return Files.newOutputStream(Paths.get(path));
	}

}
