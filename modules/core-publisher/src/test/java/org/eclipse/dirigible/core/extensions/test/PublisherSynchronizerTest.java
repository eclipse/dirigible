package org.eclipse.dirigible.core.extensions.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.sql.Timestamp;

import javax.inject.Inject;

import org.eclipse.dirigible.core.publisher.api.IPublisherCoreService;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.service.PublishCoreService;
import org.eclipse.dirigible.core.publisher.synchronizer.PublisherSynchronizer;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.Before;
import org.junit.Test;

public class PublisherSynchronizerTest extends AbstractGuiceTest {
	
	@Inject
	private IPublisherCoreService publisherCoreService;
	
	@Inject
	private PublisherSynchronizer publisherSynchronizer;
	
	@Inject
	private IRepository repository;
	
	@Before
	public void setUp() throws Exception {
		this.publisherCoreService = getInjector().getInstance(PublishCoreService.class);
		this.publisherSynchronizer = getInjector().getInstance(PublisherSynchronizer.class);
		this.repository = getInjector().getInstance(IRepository.class);
	}
	
	@Test
	public void publishResourceTest() throws PublisherException {
		
		repository.createResource("/user1/workspace1/project1/folder1/file.txt", "My Data".getBytes());
		
		publisherCoreService.createPublishRequest("/user1/workspace1", "/project1/folder1/file.txt", null);
		
		Timestamp before = publisherCoreService.getLatestPublishLog();
		
		publisherSynchronizer.synchronize();
		
		IResource publishedResource = repository.getResource(IRepositoryStructure.REGISTRY_PUBLIC + "/project1/folder1/file.txt");
		
		assertTrue(publishedResource.exists());
		assertEquals("My Data", new String(publishedResource.getContent()));
		
		Timestamp after = publisherCoreService.getLatestPublishLog();
		
		assertTrue(after.after(before));
	}
	
	@Test
	public void publishResourceTwiceTest() throws PublisherException {
		
		publishResourceTest();
		
		IResource resource = repository.getResource("/user1/workspace1/project1/folder1/file.txt");
		resource.setContent("My Data 2".getBytes());
		
		publisherCoreService.createPublishRequest("/user1/workspace1", "/project1/folder1/file.txt", null);
		
		Timestamp before = publisherCoreService.getLatestPublishLog();
		
		publisherSynchronizer.synchronize();
		
		IResource publishedResource = repository.getResource(IRepositoryStructure.REGISTRY_PUBLIC + "/project1/folder1/file.txt");
		
		assertTrue(publishedResource.exists());
		assertEquals("My Data 2", new String(publishedResource.getContent()));
		
		Timestamp after = publisherCoreService.getLatestPublishLog();
		
		assertTrue(after.after(before));
		assertFalse(before.equals(new Timestamp(0)));
	}
	
	
	

}
