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

package org.eclipse.dirigible.ide.common;

import java.util.List;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

public class CommonUtils {

	public static String formatToIDEPath(String folder, String runtimePath) {
		StringBuilder path = new StringBuilder(runtimePath);
		int metaFolderIndex = runtimePath.indexOf(ICommonConstants.SEPARATOR,
				runtimePath.indexOf(ICommonConstants.SEPARATOR) + 1);
		if (metaFolderIndex != -1) {
			path.insert(metaFolderIndex, ICommonConstants.SEPARATOR + folder);
		}
		return path.toString();
	}

	public static String formatToRuntimePath(String folder, String idePath) {
		StringBuilder path = new StringBuilder(idePath);
		int indexOfWorkspace = path.indexOf(IRepositoryPaths.WORKSPACE_FOLDER_NAME);
		int indexOfSlash = path.indexOf(ICommonConstants.SEPARATOR, indexOfWorkspace);
		path.replace(0, indexOfSlash, ICommonConstants.EMPTY_STRING);
		int indexOfFolder = path.indexOf(folder);
//		# 177
		path.replace(0, indexOfFolder + folder.length(),
				ICommonConstants.EMPTY_STRING);
		return path.toString();
	}
	
	public static String replaceNonAlphaNumericCharacters(String text) {
		return text.replaceAll("[^\\w]", "");
	}

	public static String getFileNameNoExtension(String fileName) {
		String result = fileName;
		if (fileName != null && fileName.indexOf('.') > 0) {
			result = fileName.substring(0, fileName.lastIndexOf('.'));
		}
		return result;
	}
	
	public static String concatenateListOfStrings(List<String> list, String separator) {
		StringBuffer buff = new StringBuffer();
		for (String s : list) {
			buff.append(s).append(separator);
		}
		return buff.toString();
	}

}
