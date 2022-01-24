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
package org.eclipse.dirigible.cms.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.cms.db.CmsDatabaseRepository;
import org.eclipse.dirigible.cms.db.api.CmisConstants;
import org.eclipse.dirigible.cms.db.api.CmisObject;
import org.eclipse.dirigible.cms.db.api.CmisRepository;
import org.eclipse.dirigible.cms.db.api.CmisRepositoryFactory;
import org.eclipse.dirigible.cms.db.api.CmisSession;
import org.eclipse.dirigible.cms.db.api.ContentStream;
import org.eclipse.dirigible.cms.db.api.Document;
import org.eclipse.dirigible.cms.db.api.Folder;
import org.eclipse.dirigible.cms.db.api.ObjectType;
import org.eclipse.dirigible.cms.db.repository.DatabaseTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CmisDatabaseRepositoryTest {

	private CmisRepository cmisRepository;

	@Before
	public void setUp() throws Exception {

		DataSource dataSource = DatabaseTestHelper.createDataSource("target/tests/derbycms");
//		try {
//			if (dataSource != null) {
//				Connection conn = null;
//				try {
//					conn = dataSource.getConnection();
//					Statement stmt = conn.createStatement();
//					stmt.executeUpdate("INSERT INTO DIRIGIBLE_CMS_FILES VALUES ('/', 'root', 0, '', 0, 'SYSTEM', 0, 'SYSTEM')");
//					stmt.close();
//				} finally {
//					if (conn != null) {
//						conn.close();
//					}
//				}
//			}
//		} catch (SQLException e) {
//			fail(e.getMessage());
//			e.printStackTrace();
//		}
		CmsDatabaseRepository cmsDatabaseRepository = new CmsDatabaseRepository(dataSource);

		this.cmisRepository = CmisRepositoryFactory.createCmisRepository(cmsDatabaseRepository);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		CmisSession cmisSession = this.cmisRepository.getSession();
		Folder folder = cmisSession.getRootFolder();
//		Map<String, String> root = new HashMap<String, String>();
//		root.put(CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_FOLDER);
//		root.put(CmisConstants.NAME, "/");
//		folder.createFolder(root);
		ContentStream contentStream = new ContentStream(cmisSession, "/test.txt", 6, "plain/text", new ByteArrayInputStream(new byte[] {121, 122, 121, 122, 121, 122}));
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
