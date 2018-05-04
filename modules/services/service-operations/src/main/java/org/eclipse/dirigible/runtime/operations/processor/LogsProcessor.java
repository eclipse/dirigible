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
	
	public String list() {
		String logsFolder = Configuration.get(DIRIGIBLE_OPERATIONS_LOGS_ROOT_FOLDER_DEFAULT);
		List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(logsFolder))) {
            for (Path path : directoryStream) {
                String name = path.toString();
				fileNames.add(name.substring(name.lastIndexOf(File.separator) + 1));
            }
        } catch (IOException ex) {}
        return GsonHelper.GSON.toJson(fileNames);
	}
	
	public String get(String file) throws IOException {
		String logsFolder = Configuration.get(DIRIGIBLE_OPERATIONS_LOGS_ROOT_FOLDER_DEFAULT);
		Path path = Paths.get(logsFolder, file);
		String content = new String(IOUtils.toByteArray(new FileInputStream(path.toFile())), StandardCharsets.UTF_8);
		return content;
	}

}
