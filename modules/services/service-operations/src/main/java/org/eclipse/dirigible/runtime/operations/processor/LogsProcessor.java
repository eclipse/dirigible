/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.operations.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.Configuration;

public class LogsProcessor {
	
	private static final String DIRIGIBLE_OPERATIONS_LOGS_ROOT_FOLDER_DEFAULT = "DIRIGIBLE_OPERATIONS_LOGS_ROOT_FOLDER_DEFAULT";
	
	public LogsProcessor() {
		Configuration.load("/dirigible-operations.properties");
	}
	
	public String list() throws IOException {
		String logsFolder = Configuration.get(DIRIGIBLE_OPERATIONS_LOGS_ROOT_FOLDER_DEFAULT);
		List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(logsFolder))) {
            for (Path path : directoryStream) {
                String name = path.toString();
				fileNames.add(name.substring(name.lastIndexOf(File.separator) + 1));
            }
        } catch (IOException e) {
        	throw e;
        }
        return GsonHelper.GSON.toJson(fileNames);
	}
	
	public String get(String file) throws IOException {
		String logsFolder = Configuration.get(DIRIGIBLE_OPERATIONS_LOGS_ROOT_FOLDER_DEFAULT);
		Path path = Paths.get(logsFolder, file);
		FileInputStream input = null;
		try {
			input = new FileInputStream(path.toFile());
			String content = new String(IOUtils.toByteArray(input), StandardCharsets.UTF_8);
			return content;
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

}
