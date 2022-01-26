/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.bpm.flowable.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.bpm.flowable.BpmProviderFlowable;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;

/**
 * The Flowable Engine Test
 */
public class HelloFlowableEngineTest extends AbstractDirigibleTest {
	
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
		
		InputStream in = HelloFlowableEngineTest.class.getResourceAsStream("/hello.bpmn20.xml");
		try {
			ThreadContextFacade.setUp();
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
			ThreadContextFacade.tearDown();
			if (in != null) {
				in.close();
			}
		}
	}	

}
