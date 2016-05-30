/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.utils;

import java.util.List;

import org.eclipse.dirigible.repository.api.IRepository;

public class CommonUtils {

	public static String replaceNonAlphaNumericCharacters(String text) {
		return text.replaceAll("[^\\w]", "");
	}

	public static String getFileNameNoExtension(String fileName) {
		String result = fileName;
		if ((fileName != null) && (fileName.indexOf('.') > 0)) {
			result = fileName.substring(0, fileName.lastIndexOf('.'));
		}
		return result;
	}

	public static String getFileNameFromRepositoryPathNoExtension(String repositoryPath) {
		String result = repositoryPath;
		if ((repositoryPath != null) && (repositoryPath.indexOf(IRepository.SEPARATOR) >= 0)) {
			result = repositoryPath.substring(repositoryPath.lastIndexOf(IRepository.SEPARATOR) + 1);
		}
		result = getFileNameNoExtension(result);
		return result;
	}

	public static String concatenateListOfStrings(List<String> list, String separator) {
		StringBuffer buff = new StringBuffer();
		for (String s : list) {
			buff.append(s).append(separator);
		}
		return buff.toString();
	}

	public static String toCamelCase(String input) {
		if (input == null) {
			return null;
		}
		StringBuffer result = new StringBuffer();
		result.append(input.substring(0, 1).toUpperCase());
		result.append(input.substring(1).toLowerCase());
		return result.toString();
	}

}
