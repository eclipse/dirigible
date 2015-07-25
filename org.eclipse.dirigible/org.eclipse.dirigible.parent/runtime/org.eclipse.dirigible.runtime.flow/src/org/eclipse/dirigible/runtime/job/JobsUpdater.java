/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.job;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.ext.db.AbstractDataUpdater;
import org.eclipse.dirigible.repository.logging.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.google.gson.JsonObject;

public class JobsUpdater extends AbstractDataUpdater {

	public static final String EXTENSION_JOB = ".job"; //$NON-NLS-1$
	
	public static final String REGISTRY_INTEGRATION_DEFAULT = ICommonConstants.INTEGRATION_REGISTRY_PUBLISH_LOCATION;
	
	private static final Logger logger = Logger.getLogger(JobsUpdater.class);

	private IRepository repository;
	private DataSource dataSource;
	private String location;
	private Scheduler scheduler;
	
	public static List<String> activeJobs = Collections.synchronizedList(new ArrayList<String>());
	

	public JobsUpdater(IRepository repository, DataSource dataSource,
			String location) throws JobsException {
		this.repository = repository;
		this.dataSource = dataSource;
		this.location = location;

		InputStream schedulerConfig = getClass().getResourceAsStream("/scheduler.properties");
		try {
			StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
			schedulerFactory.initialize(schedulerConfig);

			logger.debug("Creating quartz scheduler..."); //$NON-NLS-1$
			scheduler = schedulerFactory.getScheduler();
			logger.debug("Quartz scheduler created."); //$NON-NLS-1$

			logger.debug("Starting quartz scheduler..."); //$NON-NLS-1$
			scheduler.start();
			logger.debug("Quartz scheduler started."); //$NON-NLS-1$
		} catch (SchedulerException e) {
			throw new JobsException(e);
		}finally {
			if(null != schedulerConfig){
				try {
					schedulerConfig.close();
				} catch (IOException ioException) {
					logger.error("Cannot close the scheduler configuration stream.", ioException);
				}
			}
		}
	}

	@Override
	public void executeUpdate(List<String> knownFiles,
			HttpServletRequest request, List<String> errors) throws Exception {
		if (knownFiles.size() == 0) {
			return;
		}

		try {
			Connection connection = dataSource.getConnection();

			try {
				for (Iterator<String> iterator = knownFiles.iterator(); iterator
						.hasNext();) {
					String jobDefinition = iterator.next();
					try {
						if (jobDefinition.endsWith(EXTENSION_JOB)) {
							executeJobUpdate(connection, jobDefinition, request);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						errors.add(e.getMessage());
					}
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void executeJobUpdate(Connection connection,
			String jobDefinition, HttpServletRequest request)
			throws SQLException, IOException, JobsException {
		
		IRepository repository = this.repository;
//		# 177
//		String resourcePath = this.location + jobDefinition;
		String resourcePath = jobDefinition;
		
		IResource resource = repository.getResource(resourcePath);
		String content = new String(resource.getContent());
		
		JsonObject jobDefinitionObject = JobParser.parseJob(content);
		
		String jobName = jobDefinitionObject.get(JobParser.NODE_NAME).getAsString(); //$NON-NLS-1$
		String jobDescription = jobDefinitionObject.get(JobParser.NODE_DESCRIPTION).getAsString(); //$NON-NLS-1$
		String jobExpression = jobDefinitionObject.get(JobParser.NODE_EXPRESSION).getAsString(); //$NON-NLS-1$
		String jobType = jobDefinitionObject.get(JobParser.NODE_TYPE).getAsString(); //$NON-NLS-1$
		String jobModule = jobDefinitionObject.get(JobParser.NODE_MODULE).getAsString(); //$NON-NLS-1$
		
		if (activeJobs.contains(resource.getPath())) {
			try {
				JobDetail jobDetail = this.scheduler.getJobDetail(resourcePath, null);
				if ((jobDetail.getJobDataMap().get(JobParser.NODE_TYPE) != null 
						&& jobDetail.getJobDataMap().get(JobParser.NODE_TYPE).equals(jobType))
					&& (jobDetail.getJobDataMap().get(JobParser.NODE_MODULE) != null 
							&& jobDetail.getJobDataMap().get(JobParser.NODE_MODULE).equals(jobModule))
					&& (jobDetail.getJobDataMap().get(JobParser.NODE_EXPRESSION) != null 
							&& jobDetail.getJobDataMap().get(JobParser.NODE_EXPRESSION).equals(jobExpression))) {
					logger.debug(String.format("Job name: %s, description: %s, expression: %s, type: %s, module: %s already exists.", 
							jobName, jobDescription, jobExpression, jobType, jobModule)); //$NON-NLS-1$
					return;
				} else {
					this.scheduler.deleteJob(resourcePath, null);
					activeJobs.remove(resourcePath);
					logger.debug(String.format("Delete job name: %s, description: %s, expression: %s, type: %s, module: %s for re-scheduling", 
							jobName, jobDescription, jobExpression, jobType, jobModule)); //$NON-NLS-1$
				}
			} catch (SchedulerException e) {
				logger.error("Error while getting the registered job: " + jobName, e); //$NON-NLS-1$
			}
		}
		
		logger.debug(String.format("Creating quartz job name: %s, description: %s, expression: %s, type: %s, module: %s ...", 
				jobName, jobDescription, jobExpression, jobType, jobModule)); //$NON-NLS-1$
		
		JobDetail jobDetail = new JobDetail(resourcePath, null, CronJob.class);
		jobDetail.getJobDataMap().put(JobParser.NODE_NAME, jobName);
		jobDetail.getJobDataMap().put(JobParser.NODE_DESCRIPTION, jobDescription);
		jobDetail.getJobDataMap().put(JobParser.NODE_TYPE, jobType);
		jobDetail.getJobDataMap().put(JobParser.NODE_MODULE, jobModule);
		jobDetail.getJobDataMap().put(JobParser.NODE_EXPRESSION, jobExpression);

		try {
			CronTrigger trigger = new CronTrigger(jobName, null, jobExpression);
			this.scheduler.scheduleJob(jobDetail, trigger);
			activeJobs.add(resourcePath);
		} catch (ObjectAlreadyExistsException e) {
			activeJobs.add(resourcePath);
		} catch (ParseException e) {
			throw new JobsException(e);
		} catch (SchedulerException e) {
			throw new JobsException(e);
		}
		
	}
	
	public void cleanDeletedJobs() {
		IRepository repository = this.repository;
		for (String jobPath : activeJobs) {
			try {
				IResource resource = repository.getResource(jobPath);
				if (!resource.exists()) {
					this.scheduler.deleteJob(jobPath, null);
					activeJobs.remove(jobPath);
					logger.debug(String.format("Delete job: %s", jobPath)); //$NON-NLS-1$
				}
			} catch (Exception e) {
				logger.error(String.format("Error while deleting a job: %s", jobPath), e); //$NON-NLS-1$
			}
		}
	}

	public void enumerateKnownFiles(ICollection collection,
			List<String> dsDefinitions) throws IOException {
		if (collection.exists()) {
			List<IResource> resources = collection.getResources();
			for (Iterator<IResource> iterator = resources.iterator(); iterator
					.hasNext();) {
				IResource resource = iterator.next();
				if (resource != null && resource.getName() != null) {
					if (resource.getName().endsWith(EXTENSION_JOB)) {
//						# 177
//						String fullPath = collection.getPath().substring(
//								this.location.length())
//								+ IRepository.SEPARATOR + resource.getName();
						String fullPath = resource.getPath();
						dsDefinitions.add(fullPath);
					}
				}
			}

			List<ICollection> collections = collection.getCollections();
			for (Iterator<ICollection> iterator = collections.iterator(); iterator
					.hasNext();) {
				ICollection subCollection = iterator.next();
				enumerateKnownFiles(subCollection, dsDefinitions);
			}
		}
	}

	@Override
	public void applyUpdates() throws IOException, Exception {
		List<String> knownFiles = new ArrayList<String>();
		ICollection srcContainer = this.repository.getCollection(this.location);
		if (srcContainer.exists()) {
			enumerateKnownFiles(srcContainer, knownFiles);// fill knownFiles[]
															// with urls to
															// recognizable
															// repository files
			executeUpdate(knownFiles, null);// execute the real updates
		}
	}
	
	@Override
	public IRepository getRepository() {
		return repository;
	}
	
	@Override
	public String getLocation() {
		return location;
	}
	
	@Override
	public void executeUpdate(List<String> knownFiles, List<String> errors) throws Exception {
		executeUpdate(knownFiles, null, errors);
	}
	
}
