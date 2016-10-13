/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.cmis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.junit.Before;
import org.junit.Test;

public class CmisTest {

	protected IRepository repository;

	@Before
	public void setUp() {
		try {
			repository = new LocalRepository("cmis", "cmis");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCmisFolder() {
		if (repository == null) {
			fail("Repository has not been created.");
		}

		CmisRepository cmisRepository = CmisRepositoryFactory.createCmisRepository(repository);
		CmisSession cmisSession = cmisRepository.getSession();
		String folderId = null;
		try {

			Folder rootFolder = cmisSession.getRootFolder();
			assertTrue(rootFolder.getChildren().isEmpty());

			Map<String, String> properties = new HashMap<String, String>();
			properties.put(CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_FOLDER);
			properties.put(CmisConstants.NAME, "CmisFolder1");
			Folder folder = rootFolder.createFolder(properties);
			folderId = folder.getId();
			assertFalse(rootFolder.getChildren().isEmpty());

			List<CmisObject> children = rootFolder.getChildren();

			for (CmisObject cmisObject : children) {
				System.out.println("Object ID: " + cmisObject.getId());
				System.out.println("Object Name: " + cmisObject.getName());
				System.out.println("Object Type: " + cmisObject.getType().getId());
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if (folderId != null) {
					CmisObject cmisObject = cmisSession.getObject(folderId);
					cmisObject.delete();
				}
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}

	@Test
	public void testCmisDocument() {
		if (repository == null) {
			fail("Repository has not been created.");
		}

		CmisRepository cmisRepository = CmisRepositoryFactory.createCmisRepository(repository);
		CmisSession cmisSession = cmisRepository.getSession();
		String folderId = null;
		String documentId = null;
		try {

			Folder rootFolder = cmisSession.getRootFolder();
			assertTrue(rootFolder.getChildren().isEmpty());

			Map<String, String> properties = new HashMap<String, String>();
			properties.put(CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_FOLDER);
			properties.put(CmisConstants.NAME, "CmisFolder1");
			Folder folder = rootFolder.createFolder(properties);
			folderId = folder.getId();

			String mimetype = "text/plain; charset=UTF-8";
			String content = "This is some test content.";
			String filename = "test.txt";

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] bytes = content.getBytes();
			outputStream.write(bytes);

			ContentStream contentStream = cmisSession.getObjectFactory().createContentStream(filename, bytes.length, mimetype,
					new ByteArrayInputStream(bytes));

			properties = new HashMap<String, String>();
			properties.put(CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_DOCUMENT);
			properties.put(CmisConstants.NAME, filename);

			Document newDocument = null;
			try {
				newDocument = folder.createDocument(properties, contentStream, CmisConstants.VERSIONING_STATE_MAJOR);
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			documentId = newDocument.getId();

			List<CmisObject> children = folder.getChildren();

			for (CmisObject cmisObject : children) {
				System.out.println("Object ID: " + cmisObject.getId());
				System.out.println("Object Name: " + cmisObject.getName());
				System.out.println("Object Type: " + cmisObject.getType().getId());
			}

			// Get the contents of the file
			Document doc = (Document) cmisSession.getObject(documentId);
			contentStream = doc.getContentStream(); // returns null if the document has no content
			if (contentStream != null) {
				content = IOUtils.toString(contentStream.getInputStream());
				System.out.println("Contents of " + filename + " are: " + content);
			} else {
				System.out.println("No content.");
			}

			if (newDocument != null) {
				newDocument.delete();
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if (folderId != null) {
					CmisObject cmisObject = cmisSession.getObject(folderId);
					cmisObject.delete();
				}
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}

}
