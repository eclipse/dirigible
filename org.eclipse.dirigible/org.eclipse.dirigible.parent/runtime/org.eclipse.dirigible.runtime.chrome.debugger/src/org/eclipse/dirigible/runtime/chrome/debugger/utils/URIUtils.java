package org.eclipse.dirigible.runtime.chrome.debugger.utils;

import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.chrome.debugger.DebugConfiguration;

public class URIUtils {

	public static String getResourceTypeFromContentType(final String contentType) {
		final String type = contentType.toLowerCase();
		if (type.startsWith("image")) {
			return "Image";
		} else if (type.startsWith("font")) {
			return "Font";
		} else if (type.contains("javascript")) {
			return "Script";
		} else if (type.contains("css")) {
			return "Stylesheet";
		} else if (type.contains("html")) {
			return "Document";
		}
		return null;
	}

	public static String getURIextension(final String url) {
		final int dotIndex = url.lastIndexOf('.');
		if (dotIndex < 0) {
			return "";
		}
		return url.substring(dotIndex + 1);
	}

	public static String getUrlForResource(IResource firstProjectFirstResource) {
		String path = firstProjectFirstResource.getPath();
		String scriptingServices = "/public/ScriptingServices/";
		int registryServicesIndex = path.indexOf(scriptingServices);
		if(registryServicesIndex < 0){
			return "";
		}
		String resourcePath = path.substring(registryServicesIndex + scriptingServices.length() - 1, path.length());
		return DebugConfiguration.getBaseSourceUrl() + resourcePath;
	}
}
