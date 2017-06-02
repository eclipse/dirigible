package org.eclipse.dirigible.repository.local;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.generic.RepositoryGenericMultiThreadTest;

public class LocalRepositoryMultiThreadTest extends RepositoryGenericMultiThreadTest {

	@Override
	protected IRepository getNewRepository(String user) {
		// TODO uncomment only for manual tests
		// return new LocalRepository(user);
		return null;
	}

	public static void main(String[] args) {
		LocalRepositoryMultiThreadTest localMultiThreadTest = new LocalRepositoryMultiThreadTest();
		localMultiThreadTest.multi();
	}

}
