package org.eclipse.dirigible.core.scheduler.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.core.scheduler.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;

@Singleton
public class SchedulerCoreService implements ISchedulerCoreService {
	
	@Inject
	private DataSource dataSource;
	
	@Inject
	private PersistenceManager<JobDefinition> jobPersistenceManager;
	
	// Jobs
	
	@Override
	public JobDefinition createJob(String name, String group, String clazz, String description, String expression, boolean singleton) throws SchedulerException {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName(name);
		jobDefinition.setGroup(group);
		jobDefinition.setClazz(clazz);
		jobDefinition.setDescription(description);
		jobDefinition.setExpression(expression);
		jobDefinition.setSingleton(singleton);
		jobDefinition.setCreatedBy(UserFacade.getName());
		jobDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		
		try {
			Connection connection = dataSource.getConnection();
			try {
				jobPersistenceManager.insert(connection, jobDefinition);
				return jobDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	
	@Override
	public JobDefinition getJob(String name) throws SchedulerException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				return jobPersistenceManager.find(connection, JobDefinition.class, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	
	@Override
	public void removeJob(String name) throws SchedulerException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				jobPersistenceManager.delete(connection, JobDefinition.class, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	
	@Override
	public void updateJob(String name, String group, String clazz, String description, String expression, boolean singleton) throws SchedulerException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				JobDefinition jobDefinition = getJob(name);
				jobDefinition.setGroup(group);
				jobDefinition.setClazz(clazz);
				jobDefinition.setDescription(description);
				jobDefinition.setExpression(expression);
				jobDefinition.setSingleton(singleton);
				jobPersistenceManager.update(connection, jobDefinition, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	
	@Override
	public List<JobDefinition> getJobs() throws SchedulerException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				return jobPersistenceManager.findAll(connection, JobDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	
}
