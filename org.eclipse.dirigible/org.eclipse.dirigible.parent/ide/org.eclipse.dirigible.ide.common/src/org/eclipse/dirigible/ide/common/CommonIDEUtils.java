/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.common;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

public class CommonIDEUtils {

	public static String formatToIDEPath(String folder, String runtimePath) {
		StringBuilder path = new StringBuilder(runtimePath);
		// int metaFolderIndex = runtimePath.indexOf(ICommonConstants.SEPARATOR,
		// runtimePath.indexOf(ICommonConstants.SEPARATOR) + 1);
		// if (metaFolderIndex != -1) {
		// path.insert(metaFolderIndex, ICommonConstants.SEPARATOR + folder);
		// }

		path.insert(0, ICommonConstants.SEPARATOR + folder + (runtimePath.startsWith(ICommonConstants.SEPARATOR) ? "" : ICommonConstants.SEPARATOR));
		return path.toString();
	}

	public static String formatToRuntimePath(String folder, String idePath) {
		StringBuilder path = new StringBuilder(idePath);
		int indexOfWorkspace = path.indexOf(IRepositoryPaths.WORKSPACE_FOLDER_NAME);
		int indexOfSlash = path.indexOf(ICommonConstants.SEPARATOR, indexOfWorkspace);
		path.replace(0, indexOfSlash, ICommonConstants.EMPTY_STRING);
		int indexOfFolder = path.indexOf(folder);
		// # 177
		path.replace(0, indexOfFolder + folder.length(), ICommonConstants.EMPTY_STRING);
		return path.toString();
	}

}
