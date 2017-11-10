/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.extensions.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.core.publisher.api.IPublisherCoreService;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.definition.PublishLogDefinition;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;
import org.eclipse.dirigible.core.publisher.service.PublisherCoreService;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

public class PublisherCoreServiceTest extends AbstractGuiceTest {
	
	@Inject
	private IPublisherCoreService publisherCoreService;
	
	@Before
	public void setUp() throws Exception {
		this.publisherCoreService = getInjector().getInstance(PublisherCoreService.class);
	}
	
	@Test
	public void createPublishRequestTest() throws PublisherException {
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
	
	@Test
	public void getPublishRequestTest() throws PublisherException {
		PublishRequestDefinition publishRequestDefinition = publisherCoreService.createPublishRequest("workspace1", "path1", "registry1");
		publishRequestDefinition = publisherCoreService.getPublishRequest(publishRequestDefinition.getId());
		assertEquals("workspace1", publishRequestDefinition.getWorkspace());
		assertEquals("path1", publishRequestDefinition.getPath());
		assertEquals("registry1", publishRequestDefinition.getRegistry());
		publisherCoreService.removePublishRequest(publishRequestDefinition.getId());
	}
	
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
	
		
	
	@Test
	public void createPublishLogTest() throws PublisherException {
		publisherCoreService.createPublishLog("source1", "target1");
		List<PublishLogDefinition> list = publisherCoreService.getPublishLogs();
		assertEquals(1, list.size());
		PublishLogDefinition publishLogDefinition = list.get(0);
		System.out.println(publishLogDefinition.toString());
		assertEquals("source1", publishLogDefinition.getSource());
		assertEquals("target1", publishLogDefinition.getTarget());
		publisherCoreService.removePublishLog(publishLogDefinition.getId());
	}
	
	@Test
	public void getPublishLogTest() throws PublisherException {
		PublishLogDefinition publishLogDefinition = publisherCoreService.createPublishLog("source1", "target1");
		publishLogDefinition = publisherCoreService.getPublishLog(publishLogDefinition.getId());
		assertEquals("source1", publishLogDefinition.getSource());
		assertEquals("target1", publishLogDefinition.getTarget());
		publisherCoreService.removePublishLog(publishLogDefinition.getId());
	}
	
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
