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

package test.org.eclipse.dirigible.ide.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.eclipse.dirigible.ide.common.CommonUtils;
import org.eclipse.dirigible.repository.api.ICommonConstants;

public class CommonUtilsTest {

	private static final String SEPARATOR = ICommonConstants.SEPARATOR;
	private static final String FOLDER = ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
	private static final String PROJECT = "project";
	private static final String SUB_FOLDER = "subFolder";
	private static final String FILE_NAME = "file.js";

	@Test
	public void testFormatToIDEPath() throws Exception {
		final String runtimePath = SEPARATOR + PROJECT + SEPARATOR + FILE_NAME;
		final String expectedPath = SEPARATOR + PROJECT + SEPARATOR + FOLDER + SEPARATOR
				+ FILE_NAME;

		String actualPath = CommonUtils.formatToIDEPath(FOLDER, runtimePath);

		assertEquals(expectedPath, actualPath);
	}

	@Test
	public void testFormatToRuntimePath() throws Exception {
		final String idePath = SEPARATOR + PROJECT + SEPARATOR + FOLDER + SEPARATOR + FILE_NAME;
		final String expectedPath = SEPARATOR + PROJECT + SEPARATOR + FILE_NAME;

		String actualPath = CommonUtils.formatToRuntimePath(FOLDER, idePath);

		assertEquals(expectedPath, actualPath);
	}

	@Test
	public void testFormatToIDEPathWithSubFolder() throws Exception {
		final String runtimePath = SEPARATOR + PROJECT + SEPARATOR + SUB_FOLDER + SEPARATOR
				+ FILE_NAME;
		final String expectedPath = SEPARATOR + PROJECT + SEPARATOR + FOLDER + SEPARATOR
				+ SUB_FOLDER + SEPARATOR + FILE_NAME;

		String actualPath = CommonUtils.formatToIDEPath(FOLDER, runtimePath);

		assertEquals(expectedPath, actualPath);
	}

	@Test
	public void testFormatToRuntimePathWithSubFolder() throws Exception {
		final String idePath = SEPARATOR + PROJECT + SEPARATOR + FOLDER + SEPARATOR + SUB_FOLDER
				+ SEPARATOR + FILE_NAME;
		final String expectedPath = SEPARATOR + PROJECT + SEPARATOR + SUB_FOLDER + SEPARATOR
				+ FILE_NAME;

		String actualPath = CommonUtils.formatToRuntimePath(FOLDER, idePath);

		assertEquals(expectedPath, actualPath);
	}
}
