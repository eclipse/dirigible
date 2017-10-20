package org.eclipse.dirigible.core.scheduler.test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;

import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerInitializer;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

public class SchedulerTest extends AbstractGuiceTest {

	private SchedulerInitializer schedulerInitializer;

	@Before
	public void setUp() throws Exception {
		this.schedulerInitializer = getInjector().getInstance(SchedulerInitializer.class);
	}

	@Test
	public void createJob() throws SchedulerException, SQLException, IOException {
		this.schedulerInitializer.initialize();
		assertNotNull(SchedulerManager.getScheduler());
		this.schedulerInitializer.shutdown();
	}

}
