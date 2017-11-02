package org.eclipse.dirigible.engine.web.service;

import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.engine.web.processor.WebEngineProcessor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Front facing REST service serving the raw web content from the registry/public space
 */
public abstract class AbstractWebEngineRestService extends AbstractRestService implements IRestService {

	private static final Logger logger = LoggerFactory.getLogger(AbstractWebEngineRestService.class);

	private static final String INDEX_HTML = "index.html";

	@Inject
	private WebEngineProcessor processor;

	@Context
	private HttpServletResponse response;

	public Response getResource(@PathParam("path") String path) {
		if ("".equals(path.trim())) {
			return Response.status(Status.FORBIDDEN).entity("Listing of web folders is forbidden.").build();
		} else if (path.trim().endsWith(IRepositoryStructure.SEPARATOR)) {
			return getResourceByPath(path + INDEX_HTML);
		}
		return getResourceByPath(path);
	}

	private Response getResourceByPath(String path) {
		if (processor.existResource(path)) {
			IResource resource = processor.getResource(path);
			if (resource.isBinary()) {
				return Response.ok().entity(resource.getContent()).type(resource.getContentType()).build();
			}
			String content = new String(resource.getContent(), StandardCharsets.UTF_8);
			return Response.ok(content).type(resource.getContentType()).build();
		}
		try {
			byte[] content = processor.getResourceContent(path);
			if (content != null) {
				String contentType = ContentTypeHelper.getContentType(ContentTypeHelper.getExtension(path));
				if (ContentTypeHelper.isBinary(contentType)) {
					return Response.ok().entity(content).type(contentType).build();
				}
				String text = new String(content, StandardCharsets.UTF_8);
				return Response.ok(text).type(contentType).build();
			}
		} catch (RepositoryNotFoundException e) {
			String error = "Resource not found: " + path;
			sendErrorNotFound(response, error);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		sendErrorNotFound(response, path);
		return Response.status(Status.NOT_FOUND).build();
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
