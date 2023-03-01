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
package org.eclipse.dirigible.cms.internal.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.cms.internal.CmisConstants;
import org.eclipse.dirigible.cms.internal.CmisObject;
import org.eclipse.dirigible.cms.internal.CmisRepository;
import org.eclipse.dirigible.cms.internal.CmisRepositoryFactory;
import org.eclipse.dirigible.cms.internal.CmisSession;
import org.eclipse.dirigible.cms.internal.ContentStream;
import org.eclipse.dirigible.cms.internal.Document;
import org.eclipse.dirigible.cms.internal.Folder;
import org.eclipse.dirigible.cms.internal.ObjectType;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class CmisInternalRepositoryTest.
 */
public class CmisInternalRepositoryTest {

	/** The cmis repository. */
	private CmisRepository cmisRepository;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		String rootFolder = "target/cms";
		boolean absolute = Boolean.parseBoolean("false");


		IRepository repository = new LocalRepository(rootFolder, absolute);
		this.cmisRepository = CmisRepositoryFactory.createCmisRepository(repository);
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void test() throws IOException {
		CmisSession cmisSession = this.cmisRepository.getSession();
		Folder folder = cmisSession.getRootFolder();
		ContentStream contentStream = new ContentStream(cmisSession, "/test.txt", 6, "text/plain", new ByteArrayInputStream(new byte[] {121, 122, 121, 122, 121, 122}));
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_DOCUMENT);
		properties.put(CmisConstants.NAME, "test.txt");
		folder.createDocument(properties, contentStream, VersioningState.MAJOR);
		CmisObject object = cmisSession.getObject("/test.txt");
		if (object.getType().equals(ObjectType.DOCUMENT)) {
			Document document = (Document) object;
			ContentStream back = document.getContentStream();
			assertEquals(new String(new byte[] {121, 122, 121, 122, 121, 122}), new String(IOUtils.toByteArray(back.getInputStream())));
		}
	}

}
