package org.eclipse.dirigible.engine.web.service;

import javax.inject.Inject;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.engine.web.processor.WebEngineProcessor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;

/**
 * Front facing REST service serving the raw web content from the registry/public space
 */
public abstract class AbstractWebEngineRestService implements IRestService {

	private static final String INDEX_HTML = "index.html";

	@Inject
	private WebEngineProcessor processor;

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
			String content = new String(resource.getContent());
			return Response.ok(content).type(resource.getContentType()).build();
		}
		try {
			byte[] content = processor.getResourceContent(path);
			if (content != null) {
				String contentType = ContentTypeHelper.getContentType(ContentTypeHelper.getExtension(path));
				return Response.ok().entity(content).type(contentType).build();
			}
		} catch (RepositoryNotFoundException e) {
			return Response.status(Status.NOT_FOUND).entity("Resource not found: " + path).build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

}
