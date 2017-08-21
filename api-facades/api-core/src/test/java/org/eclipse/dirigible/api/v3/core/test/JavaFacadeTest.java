package org.eclipse.dirigible.api.v3.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.core.JavaFacade;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.core.extensions.synchronizer.ExtensionsSynchronizer;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

public class JavaFacadeTest extends AbstractGuiceTest {

	@Inject
	private ExtensionsSynchronizer extensionsPublisher;

	@Before
	public void setUp() throws Exception {
		this.extensionsPublisher = getInjector().getInstance(ExtensionsSynchronizer.class);
	}

	@Test
	public void testCall() throws IOException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ContextException {

		ThreadContextFacade.setUp();
		try {
			extensionsPublisher.registerPredeliveredExtensionPoint("/control/control.extensionpoint");
			extensionsPublisher.registerPredeliveredExtension("/control/control.extension");

			extensionsPublisher.synchronize();

			Object result = JavaFacade.call("org.eclipse.dirigible.api.v3.core.ExtensionsServiceFacade", "getExtensions",
					new String[] { "/control/control" });
			assertTrue(result instanceof String[]);
			assertEquals("/control/control", ((String[]) result)[0]);
		} finally {
			ThreadContextFacade.tearDown();
		}
	}

}
