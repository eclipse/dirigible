/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.bpm.flowable.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.bpm.flowable.BpmProviderFlowable;
import org.eclipse.dirigible.bpm.flowable.dto.TaskData;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

/**
 * The Flowable Engine Test
 */
public class HolidayRequestFlowableEngineTest extends AbstractDirigibleTest {
	
	/** The flowable engine provider. */
	private BpmProviderFlowable bpmProviderFlowable;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.bpmProviderFlowable = new BpmProviderFlowable();
		
		System.setProperty("DIRIGIBLE_FLOWABLE_DATABASE_DRIVER", "org.h2.Driver");
		System.setProperty("DIRIGIBLE_FLOWABLE_DATABASE_URL", "jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1");
		System.setProperty("DIRIGIBLE_FLOWABLE_DATABASE_USER", "sa");
		System.setProperty("DIRIGIBLE_FLOWABLE_DATABASE_PASSWORD", "");
	}
	
	/**
	 * Deploys a process to flowable engine
	 *
	 * @throws Exception an exception in processing
	 */
	@Test
	public void deployProcessTest() throws Exception {
		ProcessEngine processEngine = (ProcessEngine) bpmProviderFlowable.getProcessEngine();
		
		RepositoryService repositoryService = processEngine.getRepositoryService();
		Deployment deployment = repositoryService.createDeployment()
		  .addClasspathResource("holiday-request.bpmn20.xml")
		  .deploy();
		
		String deploymentId = deployment.getId();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				  .deploymentId(deploymentId)
				  .singleResult();
		
		assertNotNull(processDefinition);
		assertEquals("Holiday Request", processDefinition.getName());
		
		repositoryService.deleteDeployment(deploymentId);
		
		processDefinition = repositoryService.createProcessDefinitionQuery()
				  .deploymentId(deploymentId)
				  .singleResult();
		assertNull(processDefinition);
	}
	
	
	/**
	 * Starts a process deployed on the flowable engine
	 *
	 * @throws Exception an exception in processing
	 */
	@Test
	public void startProcessTest() throws Exception {
		try {
			ThreadContextFacade.setUp();
			ProcessEngine processEngine = (ProcessEngine) bpmProviderFlowable.getProcessEngine();

			RepositoryService repositoryService = processEngine.getRepositoryService();
			Deployment deployment = repositoryService.createDeployment()
					.addClasspathResource("holiday-request.bpmn20.xml")
					.deploy();

			String deploymentId = deployment.getId();
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
					.deploymentId(deploymentId)
					.singleResult();

			RuntimeService runtimeService = processEngine.getRuntimeService();

			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("employee", "John");
			variables.put("nrOfHolidays", "7");
			variables.put("description", "test");
			ProcessInstance processInstance =
					runtimeService.startProcessInstanceByKey("holidayRequest", variables);

			TaskService taskService = processEngine.getTaskService();
			List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
			assertEquals(1, tasks.size());

			TaskData taskData = new TaskData();
			BeanUtils.copyProperties(tasks.get(0), taskData);
			taskData.setId(tasks.get(0).getId());

			String json = GsonHelper.toJson(taskData);
			System.out.println(json);

			Task task = tasks.get(0);
			Map<String, Object> processVariables = taskService.getVariables(task.getId());
			assertEquals("John", processVariables.get("employee"));

			variables = new HashMap<String, Object>();
			variables.put("approved", true);
			taskService.complete(task.getId(), variables);

			tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
			assertEquals(0, tasks.size());

			// repositoryService.deleteDeployment(deploymentId);
		} finally {
			ThreadContextFacade.tearDown();
		}

	}
	
	

}
