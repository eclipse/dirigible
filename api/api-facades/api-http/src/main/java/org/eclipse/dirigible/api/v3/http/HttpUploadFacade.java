package org.eclipse.dirigible.api.v3.http;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.dirigible.commons.api.context.InvalidStateException;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUploadFacade implements IScriptingFacade {

	private static final String NO_VALID_REQUEST = "Trying to use HTTP Upload Facade without a valid Request";

	private static final Logger logger = LoggerFactory.getLogger(HttpUploadFacade.class);

	public static final boolean isMultipartContent() {

		HttpServletRequest request = HttpRequestFacade.getRequest();
		if (request == null) {
			return false;
		}

		return ServletFileUpload.isMultipartContent(request);
	}

	public static final List<FileItem> parseRequest() throws FileUploadException {
		ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
		HttpServletRequest request = HttpRequestFacade.getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		List<FileItem> fileItems = servletFileUpload.parseRequest(request);
		return fileItems;
	}

}
