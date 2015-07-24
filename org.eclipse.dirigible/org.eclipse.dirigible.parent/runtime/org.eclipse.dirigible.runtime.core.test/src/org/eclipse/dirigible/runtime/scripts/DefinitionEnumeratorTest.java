/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripts;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.runtime.registry.DefinitionEnumerator;
import org.eclipse.dirigible.runtime.utils.DataSourceUtils;

public class DefinitionEnumeratorTest {

    private static final String REPOSITORY_JS_DEPLOY_PATH = "/db/dirigible/registry/public/" //$NON-NLS-1$
            + ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;

    private static final String FILE_ONE = REPOSITORY_JS_DEPLOY_PATH + "/test.jstest";

    private static final String FILE_TWO = REPOSITORY_JS_DEPLOY_PATH + "/test/test1.jstest";

    private static final String FILE_THREE = REPOSITORY_JS_DEPLOY_PATH + "/test/test2.html";

    private static IRepository repository;

    @Before
    public void setUp() {
        final DataSource dataSource = DataSourceUtils.createLocal();
        try {

            repository = new DBRepository(dataSource, "guest", false);
            repository.createResource(FILE_ONE, ("test1").getBytes());
            repository.createResource(FILE_TWO, ("test2").getBytes());
            repository.createResource(FILE_THREE, ("text_html").getBytes());

        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @After
    public void cleanup() {
        try {

            repository.removeResource(FILE_ONE);
            repository.removeResource(FILE_TWO);
            repository.removeResource(FILE_THREE);

        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testEnumerator() {
        try {
            final DefinitionEnumerator defEnumerator = new DefinitionEnumerator(REPOSITORY_JS_DEPLOY_PATH, repository.getCollection(REPOSITORY_JS_DEPLOY_PATH), ".jstest");
            final List<String> list = defEnumerator.toArrayList();
            assertNotNull(defEnumerator);
            assertTrue(list.size() == 2);
            assertTrue(list.get(0).equals("/test.jstest"));
            assertTrue(list.get(1).equals("/test/test1.jstest"));

        } catch (final IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
