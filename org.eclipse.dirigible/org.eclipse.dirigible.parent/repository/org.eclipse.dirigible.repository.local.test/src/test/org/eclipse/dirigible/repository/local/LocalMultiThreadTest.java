package test.org.eclipse.dirigible.repository.local;

import org.eclipse.dirigible.repository.api.IRepository;

import test.org.eclipse.dirigible.repository.generic.GenericMultiThreadTest;

public class LocalMultiThreadTest extends GenericMultiThreadTest {

	@Override
	protected IRepository getNewRepository(String user) {
		// TODO uncomment only for manual tests
		// return new LocalRepository(user);
		return null;
	}

	public static void main(String[] args) {
		LocalMultiThreadTest localMultiThreadTest = new LocalMultiThreadTest();
		localMultiThreadTest.multi();
	}

}
