/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.bpm.flowable.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.bpm.flowable.BpmProviderFlowable;
import org.eclipse.dirigible.bpm.flowable.dto.TaskData;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
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
public class HelloFlowableEngineTest extends AbstractGuiceTest {
	
	/** The flowable engine provider. */
	@Inject
	private BpmProviderFlowable bpmProviderFlowable;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.bpmProviderFlowable = getInjector().getInstance(BpmProviderFlowable.class);
		
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
		
		InputStream in = HelloFlowableEngineTest.class.getResourceAsStream("/hello.bpmn20.xml");
		try {
			byte[] bytes = IOUtils.toByteArray(in);
			RepositoryService repositoryService = processEngine.getRepositoryService();
			Deployment deployment = repositoryService.createDeployment().addBytes("hello.bpmn20.xml", bytes) // must ends with *.bpmn20.xml
					.deploy();
			String deploymentId = deployment.getId();
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
					.deploymentId(deploymentId).singleResult();
			assertNotNull(processDefinition);
			assertEquals("Hello", processDefinition.getName());
			RuntimeService runtimeService = processEngine.getRuntimeService();
			Map<String, Object> variables = new HashMap<String, Object>();
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("hello", variables);
			repositoryService.deleteDeployment(deploymentId);
			processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId)
					.singleResult();
			assertNull(processDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}	

}
