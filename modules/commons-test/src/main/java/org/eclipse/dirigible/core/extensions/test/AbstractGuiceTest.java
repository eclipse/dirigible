package org.eclipse.dirigible.core.extensions.test;

import static org.mockito.Mockito.mock;

import org.eclipse.dirigible.commons.api.module.DirigibleModulesInstallerModule;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.config.Configuration;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.UnitOfWork;

/**
 * Test supporting class, enabling dependency injection
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractGuiceTest {

	private Injector injector;

	protected UnitOfWork unitOfWorkMock;

	/**
	 * Dependency injection before execution of every test method
	 */
	@Before
	public void beforeEveryMethod() {
		getInjector();
		Injector mockedInjector = mock(Injector.class);
		StaticInjector.setInjector(mockedInjector);
	}

	protected Injector getInjector() {
		Configuration.setSystemProperty("DIRIGIBLE_DATABASE_DERBY_ROOT_FOLDER_DEFAULT", "./target/derbytest");
		if (injector == null) {
			injector = Guice.createInjector(
					new DirigibleModulesInstallerModule(),
					new Module() {
		
						@Override
						public void configure(Binder binder) {
							bind(binder);
						}
		
					}
			);
		}
		return injector;
	}

	protected void bind(Binder binder) {
		setUpMocks();

		binder.bind(UnitOfWork.class).toInstance(unitOfWorkMock);
	}

	protected void setUpMocks() {
		this.unitOfWorkMock = Mockito.mock(UnitOfWork.class);
	}

}
