package org.eclipse.dirigible.repository.local;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

public class ZipRepository extends FileSystemRepository {

	private String zipRepositoryRootFolder;

	public ZipRepository(String user, String zip) throws LocalBaseException {

		File zipFile = new File(zip);
		if (zipFile.exists()) {
			try {
				Path rootFolder = Files.createTempDirectory("zip_repository");
				unpackZip(zip, rootFolder.toString());
				String zipFileName = zipFile.getName();
				zipRepositoryRootFolder = zipFileName.substring(0, zipFileName.lastIndexOf("."));
				createRepository(user, rootFolder.toString(), true);
			} catch (IOException e) {
				throw new LocalBaseException(e);
			}
		} else {
			throw new LocalBaseException(String.format("Zip file containing Repository content does not exist at path: %s", zip));
		}
	}

	private void unpackZip(String zip, String folder) throws IOException {
		ZipFile zipFile = new ZipFile(zip);
		try {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File entryDestination = new File(folder, entry.getName());
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
				} else {
					entryDestination.getParentFile().mkdirs();
					InputStream in = zipFile.getInputStream(entry);
					OutputStream out = new FileOutputStream(entryDestination);
					IOUtils.copy(in, out);
					IOUtils.closeQuietly(in);
					out.close();
				}
			}
		} finally {
			zipFile.close();
		}
	}

	// disable usage
	private ZipRepository(String user, String rootFolder, boolean absolute) throws LocalBaseException {
		super(user, rootFolder, absolute);
	}

	// disable usage
	private ZipRepository(String user) throws LocalBaseException {
		super(user);
	}

	@Override
	protected String getRepositoryRootFolder() {
		return this.zipRepositoryRootFolder;
	}
}
