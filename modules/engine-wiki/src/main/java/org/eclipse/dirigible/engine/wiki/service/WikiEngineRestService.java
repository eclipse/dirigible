package org.eclipse.dirigible.engine.wiki.service;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.engine.wiki.processor.WikiEngineProcessor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the wiki pages
 */
@Singleton
@Path("/wiki")
@Api(value = "Core - Wiki Engine", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class WikiEngineRestService implements IRestService {

	@Inject
	private WikiEngineProcessor processor;

	@GET
	@Path("/{path:.*}")
	@ApiOperation("Get Resource Content")
	@ApiResponses({ @ApiResponse(code = 200, message = "Get the content fo the resource", response = byte[].class),
			@ApiResponse(code = 404, message = "No such resource") })
	public Response getWikiPage(@ApiParam(value = "Path of the Resource", required = true) @PathParam("path") String path) {
		return render(path);
	}

	protected Response render(@PathParam("path") String path) {
		if ("".equals(path.trim()) || path.trim().endsWith(IRepositoryStructure.SEPARATOR)) {
			return Response.status(Status.FORBIDDEN).entity("Listing of web folders is forbidden.").build();
		}
		if (processor.existResource(path)) {
			IResource resource = processor.getResource(path);
			if (resource.isBinary()) {
				return Response.status(Status.NOT_FOUND).entity("Resource found, but it is a binary file: " + path).build();
			}
			String content = new String(resource.getContent());
			String html = renderContent(content);
			return Response.ok(html).type(resource.getContentType()).build();
		}
		try {
			byte[] content = processor.getResourceContent(path);
			if (content != null) {
				String html = renderContent(new String(content, StandardCharsets.UTF_8));
				return Response.ok().entity(html).build();
			}
		} catch (RepositoryNotFoundException e) {
			return Response.status(Status.NOT_FOUND).entity("Resource not found: " + path).build();
		}

		return Response.status(Status.NOT_FOUND).build();
	}

	private String renderContent(String content) {

		StringWriter writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		builder.setEmitAsDocument(false);
		MarkupParser markupParser = new MarkupParser();
		markupParser.setBuilder(builder);
		markupParser.setMarkupLanguage(new MarkdownLanguage());
		markupParser.parse(content);
		String htmlContent = writer.toString();
		return htmlContent;
	}

	@Override
	public Class<? extends IRestService> getType() {
		return WikiEngineRestService.class;
	}
}
