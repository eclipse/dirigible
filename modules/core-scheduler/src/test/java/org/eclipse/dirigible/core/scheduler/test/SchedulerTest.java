package org.eclipse.dirigible.core.scheduler.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.logging.LoggingHelper;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerInitializer;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

public class SchedulerTest extends AbstractGuiceTest {
	
	private SchedulerInitializer schedulerInitializer;
	
	private LoggingHelper loggingHelper;
	
	@Before
	public void setUp() throws Exception {
		this.schedulerInitializer = getInjector().getInstance(SchedulerInitializer.class);
		this.loggingHelper = getInjector().getInstance(LoggingHelper.class);
	}
	
	@Test
	public void createJob() throws SchedulerException {
		this.schedulerInitializer.initialize(loggingHelper);
		assertNotNull(SchedulerManager.getScheduler());
		this.schedulerInitializer.shutdown(loggingHelper);
	}
	
	
	
	
	
}
