package org.eclipse.dirigible.repository.local;

import org.eclipse.dirigible.repository.api.IMasterRepository;

public class FileSystemMasterRepository extends FileSystemRepository implements IMasterRepository {

	public FileSystemMasterRepository(String user, String rootFolder) throws LocalBaseException {
		super(user, rootFolder);
	}

	public FileSystemMasterRepository(String user) throws LocalBaseException {
		super(user);
	}

}
