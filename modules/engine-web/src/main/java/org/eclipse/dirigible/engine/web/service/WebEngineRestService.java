package org.eclipse.dirigible.engine.web.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.engine.web.processor.WebEngineProcessor;
import org.eclipse.dirigible.repository.api.IResource;

/**
 * Front facing REST service serving the raw web content from the registry/public space
 */
@Singleton
public class WebEngineRestService implements IRestService {
	
	@Inject
	private WebEngineProcessor processor;
	
	@GET
	@Path("/web/{path:.*}")
	public Response getResource(@PathParam("path") String path) {
		if (processor.existResource(path)) {
			IResource resource = processor.getResource(path);
			if (resource.isBinary()) {
				return Response.ok().entity(resource.getContent()).type(resource.getContentType()).build();
			}
			return Response.ok(new String(resource.getContent())).type(resource.getContentType()).build();
		} else {
			byte[] content = processor.getResourceContent(path);
			if (content != null) {
				String contentType = ContentTypeHelper.getContentType(ContentTypeHelper.getExtension(path));
				return Response.ok().entity(content).type(contentType).build();
			}
		}
		
		return Response.status(Status.NOT_FOUND).build();
	}

	@Override
	public Class<? extends IRestService> getType() {
		return WebEngineRestService.class;
	}
}
