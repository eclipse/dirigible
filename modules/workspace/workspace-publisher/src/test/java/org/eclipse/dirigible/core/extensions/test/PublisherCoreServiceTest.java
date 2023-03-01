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
package org.eclipse.dirigible.core.extensions.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.dirigible.core.publisher.api.IPublisherCoreService;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.definition.PublishLogDefinition;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;
import org.eclipse.dirigible.core.publisher.service.PublisherCoreService;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class PublisherCoreServiceTest.
 */
public class PublisherCoreServiceTest extends AbstractDirigibleTest {
	
	/** The publisher core service. */
	private IPublisherCoreService publisherCoreService;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.publisherCoreService = new PublisherCoreService();
	}
	
	/**
	 * Creates the publish request test.
	 *
	 * @throws PublisherException the publisher exception
	 */
	@Test
	public void createPublishRequestTest() throws PublisherException {
		publisherCoreService.removeAllPublishRequests();
		publisherCoreService.createPublishRequest("workspace1", "path1", "registry1");
		List<PublishRequestDefinition> list = publisherCoreService.getPublishRequests();
		assertEquals(1, list.size());
		PublishRequestDefinition publishRequestDefinition = list.get(0);
		System.out.println(publishRequestDefinition.toString());
		assertEquals("workspace1", publishRequestDefinition.getWorkspace());
		assertEquals("path1", publishRequestDefinition.getPath());
		assertEquals("registry1", publishRequestDefinition.getRegistry());
		publisherCoreService.removePublishRequest(publishRequestDefinition.getId());
	}
	
	/**
	 * Gets the publish request test.
	 *
	 * @return the publish request test
	 * @throws PublisherException the publisher exception
	 */
	@Test
	public void getPublishRequestTest() throws PublisherException {
		PublishRequestDefinition publishRequestDefinition = publisherCoreService.createPublishRequest("workspace1", "path1", "registry1");
		publishRequestDefinition = publisherCoreService.getPublishRequest(publishRequestDefinition.getId());
		assertEquals("workspace1", publishRequestDefinition.getWorkspace());
		assertEquals("path1", publishRequestDefinition.getPath());
		assertEquals("registry1", publishRequestDefinition.getRegistry());
		publisherCoreService.removePublishRequest(publishRequestDefinition.getId());
	}
	
	/**
	 * Removes the publish request test.
	 *
	 * @throws PublisherException the publisher exception
	 */
	@Test
	public void removePublishRequestTest() throws PublisherException {
		PublishRequestDefinition publishRequestDefinition = publisherCoreService.createPublishRequest("workspace1", "path1", "registry1");
		publishRequestDefinition = publisherCoreService.getPublishRequest(publishRequestDefinition.getId());
		assertEquals("workspace1", publishRequestDefinition.getWorkspace());
		assertEquals("path1", publishRequestDefinition.getPath());
		assertEquals("registry1", publishRequestDefinition.getRegistry());
		publisherCoreService.removePublishRequest(publishRequestDefinition.getId());
		publishRequestDefinition = publisherCoreService.getPublishRequest(publishRequestDefinition.getId());
		assertNull(publishRequestDefinition);
	}
	
		
	
	/**
	 * Creates the publish log test.
	 *
	 * @throws PublisherException the publisher exception
	 */
	@Test
	public void createPublishLogTest() throws PublisherException {
		publisherCoreService.getPublishLogs().forEach(log->{
			try {
				publisherCoreService.removePublishLog(log.getId());
			} catch (PublisherException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		});
		publisherCoreService.createPublishLog("source1", "target1");
		List<PublishLogDefinition> list = publisherCoreService.getPublishLogs();
		assertEquals(1, list.size());
		PublishLogDefinition publishLogDefinition = list.get(0);
		System.out.println(publishLogDefinition.toString());
		assertEquals("source1", publishLogDefinition.getSource());
		assertEquals("target1", publishLogDefinition.getTarget());
		publisherCoreService.removePublishLog(publishLogDefinition.getId());
	}
	
	/**
	 * Gets the publish log test.
	 *
	 * @return the publish log test
	 * @throws PublisherException the publisher exception
	 */
	@Test
	public void getPublishLogTest() throws PublisherException {
		PublishLogDefinition publishLogDefinition = publisherCoreService.createPublishLog("source1", "target1");
		publishLogDefinition = publisherCoreService.getPublishLog(publishLogDefinition.getId());
		assertEquals("source1", publishLogDefinition.getSource());
		assertEquals("target1", publishLogDefinition.getTarget());
		publisherCoreService.removePublishLog(publishLogDefinition.getId());
	}
	
	/**
	 * Removes the publish log test.
	 *
	 * @throws PublisherException the publisher exception
	 */
	@Test
	public void removePublishLogTest() throws PublisherException {
		PublishLogDefinition publishLogDefinition = publisherCoreService.createPublishLog("source1", "target1");
		publishLogDefinition = publisherCoreService.getPublishLog(publishLogDefinition.getId());
		assertEquals("source1", publishLogDefinition.getSource());
		assertEquals("target1", publishLogDefinition.getTarget());
		publisherCoreService.removePublishLog(publishLogDefinition.getId());
		publishLogDefinition = publisherCoreService.getPublishLog(publishLogDefinition.getId());
		assertNull(publishLogDefinition);
	}

}
