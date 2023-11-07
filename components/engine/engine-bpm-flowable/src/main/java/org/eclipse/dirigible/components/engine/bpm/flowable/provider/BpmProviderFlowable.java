/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.provider;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.engine.bpm.BpmProvider;
import org.eclipse.dirigible.components.engine.bpm.flowable.dto.TaskData;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class BpmProviderFlowable.
 */
@Component
public class BpmProviderFlowable implements BpmProvider {

  /** The Constant FILE_EXTENSION_BPMN. */
  public static final String FILE_EXTENSION_BPMN = ".bpmn";

  /** The Constant EXTENSION_BPMN20_XML. */
  private static final String EXTENSION_BPMN20_XML = "bpmn20.xml";

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(BpmProviderFlowable.class);

  /** The Constant DIRIGIBLE_FLOWABLE_DATABASE_DRIVER. */
  private static final String DIRIGIBLE_FLOWABLE_DATABASE_DRIVER = "DIRIGIBLE_FLOWABLE_DATABASE_DRIVER";

  /** The Constant DIRIGIBLE_FLOWABLE_DATABASE_URL. */
  private static final String DIRIGIBLE_FLOWABLE_DATABASE_URL = "DIRIGIBLE_FLOWABLE_DATABASE_URL";

  /** The Constant DIRIGIBLE_FLOWABLE_DATABASE_USER. */
  private static final String DIRIGIBLE_FLOWABLE_DATABASE_USER = "DIRIGIBLE_FLOWABLE_DATABASE_USER";

  /** The Constant DIRIGIBLE_FLOWABLE_DATABASE_PASSWORD. */
  private static final String DIRIGIBLE_FLOWABLE_DATABASE_PASSWORD = "DIRIGIBLE_FLOWABLE_DATABASE_PASSWORD";

  /** The Constant DIRIGIBLE_FLOWABLE_DATABASE_DATASOURCE_NAME. */
  private static final String DIRIGIBLE_FLOWABLE_DATABASE_DATASOURCE_NAME = "DIRIGIBLE_FLOWABLE_DATABASE_DATASOURCE_NAME";

  /** The Constant DIRIGIBLE_FLOWABLE_DATABASE_SCHEMA_UPDATE. */
  private static final String DIRIGIBLE_FLOWABLE_DATABASE_SCHEMA_UPDATE = "DIRIGIBLE_FLOWABLE_DATABASE_SCHEMA_UPDATE";

  /** The process engine. */
  private static ProcessEngine processEngine;

  /** The datasource. */
  private final DataSource datasource;

  /** The repository. */
  private final IRepository repository;

  /**
   * Instantiates a new bpm provider flowable.
   *
   * @param datasource the datasource
   * @param repository the repository
   */
  @Autowired
  public BpmProviderFlowable(DataSource datasource, IRepository repository) {
    this.datasource = datasource;
    this.repository = repository;
  }

  /**
   * Gets the datasource.
   *
   * @return the datasource
   */
  public DataSource getDatasource() {
    return datasource;
  }

  /**
   * Gets the repository.
   *
   * @return the repository
   */
  public IRepository getRepository() {
    return repository;
  }

  /**
   * Gets the process engine.
   *
   * @return the process engine
   */
  public ProcessEngine getProcessEngine() {
    synchronized (BpmProviderFlowable.class) {
      if (processEngine == null) {
        if (logger.isInfoEnabled()) {
          logger.info("Initializng the Flowable Process Engine...");
        }

        ProcessEngineConfiguration cfg = null;
        String dataSourceName = Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_DATASOURCE_NAME);

        if (dataSourceName != null) {
          if (logger.isInfoEnabled()) {
            logger.info("Initializng the Flowable Process Engine with JNDI datasource name");
          }
          cfg = new StandaloneProcessEngineConfiguration().setDataSourceJndiName(dataSourceName);
        } else {
          String driver = Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_DRIVER);
          String url = Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_URL);
          String user = Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_USER);
          String password = Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_PASSWORD);

          if (driver != null && url != null) {
            if (logger.isInfoEnabled()) {
              logger.info("Initializng the Flowable Process Engine with environment variables datasource parameters");
            }
            cfg = new StandaloneProcessEngineConfiguration().setJdbcUrl(url)
                                                            .setJdbcUsername(user)
                                                            .setJdbcPassword(password)
                                                            .setJdbcDriver(driver);
          } else {
            cfg = new StandaloneProcessEngineConfiguration().setDataSource(datasource);
          }
        }

        boolean updateSchema = Boolean.parseBoolean(Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_SCHEMA_UPDATE, "true"));
        cfg.setDatabaseSchemaUpdate(
            updateSchema ? ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE : ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);

        processEngine = cfg.buildProcessEngine();
        if (logger.isInfoEnabled()) {
          logger.info("Done initializng the Flowable Process Engine.");
        }
      }
    }
    return processEngine;
  }

  /**
   * Deploy process.
   *
   * @param location the location
   * @return the string
   */
  public String deployProcess(String location) {
    if (logger.isDebugEnabled()) {
      logger.debug("Deploying a BPMN process from location: " + location);
    }
    RepositoryService repositoryService = getProcessEngine().getRepositoryService();
    Deployment deployment = null;
    if (!location.startsWith(IRepositoryStructure.SEPARATOR))
      location = IRepositoryStructure.SEPARATOR + location;
    String repositoryPath = IRepositoryStructure.PATH_REGISTRY_PUBLIC + location;
    if (getRepository().hasResource(repositoryPath)) {
      IResource resource = getRepository().getResource(repositoryPath);
      deployment = repositoryService.createDeployment()
                                    .addBytes(location + EXTENSION_BPMN20_XML, resource.getContent())
                                    .deploy();
    } else {
      InputStream in = BpmProviderFlowable.class.getResourceAsStream("/META-INF/dirigible" + location);
      try {
        if (in != null) {
          try {
            byte[] bytes = IOUtils.toByteArray(in);
            deployment = repositoryService.createDeployment()
                                          .addBytes(location + EXTENSION_BPMN20_XML, bytes)
                                          .deploy();
          } catch (IOException e) {
            throw new IllegalArgumentException(e);
          }
        } else {
          throw new IllegalArgumentException("No BPMN resource found at location: " + location);
        }
      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (IOException e) {
            throw new IllegalArgumentException("Error closing the BPMN resource at location: " + location, e);
          }
        }
      }
    }
    if (logger.isInfoEnabled()) {
      logger.info(format("Process deployed with deployment id: [{0}] and process key: [{1}]", deployment.getId(), deployment.getKey()));
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Done deploying a BPMN process from location: " + location);
    }
    return deployment.getId();
  }

  /**
   * Undeploy process.
   *
   * @param deploymentId the deployment id
   */
  public void undeployProcess(String deploymentId) {
    RepositoryService repositoryService = getProcessEngine().getRepositoryService();
    repositoryService.deleteDeployment(deploymentId, true);
  }

  /**
   * Start process.
   *
   * @param key the key
   * @param parameters the parameters
   * @return the string
   */
  public String startProcess(String key, String parameters) {
    if (logger.isDebugEnabled()) {
      logger.debug("Starting a BPMN process by key: " + key);
    }
    RuntimeService runtimeService = getProcessEngine().getRuntimeService();
    @SuppressWarnings("unchecked")
    Map<String, Object> variables = GsonHelper.fromJson(parameters, HashMap.class);
    ProcessInstance processInstance;
    try {
      processInstance = runtimeService.startProcessInstanceByKey(key, variables);
    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error(e.getMessage(), e);
      }
      List<ProcessDefinition> processDefinitions = processEngine.getRepositoryService()
                                                                .createProcessDefinitionQuery()
                                                                .list();
      logger.error("Available process definitions:");
      for (ProcessDefinition processDefinition : processDefinitions) {
        if (logger.isErrorEnabled()) {
          logger.error(format("Deployment: [{0}] with key: [{1}] and name: [{2}]", processDefinition.getDeploymentId(),
              processDefinition.getKey(), processDefinition.getName()));
        }
      }
      return null;
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Done starting a BPMN process by key: " + key);
    }
    return processInstance.getId();
  }

  /**
   * Delete process.
   *
   * @param id the id
   * @param reason the reason
   */
  public void deleteProcess(String id, String reason) {
    if (logger.isDebugEnabled()) {
      logger.debug("Deleting a BPMN process instance by id: " + id);
    }
    try {
      processEngine.getRuntimeService()
                   .deleteProcessInstance(id, reason);
      if (logger.isDebugEnabled()) {
        logger.debug("Done deleting a BPMN process instance by id: " + id);
      }
    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error(e.getMessage(), e);
      }
    }
  }

  /**
   * Gets the tasks.
   *
   * @return the tasks
   */
  public String getTasks() {
    List<TaskData> tasksData = new ArrayList<>();
    TaskService taskService = getProcessEngine().getTaskService();
    List<Task> tasks = taskService.createTaskQuery()
                                  .list();
    for (Task task : tasks) {
      TaskData taskData = new TaskData();
      BeanUtils.copyProperties(task, taskData);
      tasksData.add(taskData);
    }
    String json = GsonHelper.toJson(tasksData);
    return json;
  }

  /**
   * Gets the task variables.
   *
   * @param taskId the task id
   * @return the task variables
   */
  public String getTaskVariables(String taskId) {
    TaskService taskService = getProcessEngine().getTaskService();
    Map<String, Object> processVariables = taskService.getVariables(taskId);
    String json = GsonHelper.toJson(processVariables);
    return json;
  }

  /**
   * Sets the task variables.
   *
   * @param taskId the task id
   * @param variables the variables
   */
  public void setTaskVariables(String taskId, String variables) {
    TaskService taskService = getProcessEngine().getTaskService();
    @SuppressWarnings("unchecked")
    Map<String, Object> processVariables = GsonHelper.fromJson(variables, HashMap.class);
    taskService.setVariables(taskId, processVariables);
  }

  /**
   * Complete task.
   *
   * @param taskId the task id
   * @param variables the variables
   */
  public void completeTask(String taskId, String variables) {
    TaskService taskService = getProcessEngine().getTaskService();
    @SuppressWarnings("unchecked")
    Map<String, Object> processVariables = GsonHelper.fromJson(variables, HashMap.class);
    taskService.complete(taskId, processVariables);
  }

  /**
   * Gets the variable.
   *
   * @param executionId the execution id
   * @param variableName the variable name
   * @return the variable
   */
  public Object getVariable(String executionId, String variableName) {
    RuntimeService runtimeService = getProcessEngine().getRuntimeService();
    return runtimeService.getVariable(executionId, variableName);
  }

  /**
   * Sets the variable.
   *
   * @param executionId the execution id
   * @param variableName the variable name
   * @param value the value
   */
  public void setVariable(String executionId, String variableName, Object value) {
    RuntimeService runtimeService = getProcessEngine().getRuntimeService();
    runtimeService.setVariable(executionId, variableName, value);
  }

  /**
   * Removes the variable.
   *
   * @param executionId the execution id
   * @param variableName the variable name
   */
  public void removeVariable(String executionId, String variableName) {
    RuntimeService runtimeService = getProcessEngine().getRuntimeService();
    runtimeService.removeVariable(executionId, variableName);
  }

}
