package org.eclipse.dirigible.repository.project;

import com.google.gson.Gson;

public class ProjectMetadataUtils {

	private static Gson gson = new Gson();

	public static String toJson(ProjectMetadata projectMetadata) {
		String json = gson.toJson(projectMetadata);
		return json;
	}

	public static ProjectMetadata fromJson(String json) {
		ProjectMetadata projectMetadata = gson.fromJson(json, ProjectMetadata.class);
		return projectMetadata;
	}

}
