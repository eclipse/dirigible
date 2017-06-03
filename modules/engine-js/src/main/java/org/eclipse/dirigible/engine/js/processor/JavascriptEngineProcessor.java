package org.eclipse.dirigible.engine.js.processor;

import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.repository.api.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JavascriptEngineProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(JavascriptEngineProcessor.class.getCanonicalName());
	
	@Inject
	private IRepository repository;
	
	public String executeScript(String path) {
		try {
//			return new String(repository.getResource(path).getContent());
			// TODO get the default engine by parameter or file extension and execute the script
			// DIRIGIBLE_JAVASCRIPT_ENGINE_DEFAULT
			return HttpRequestFacade.getMethod();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(e);
		}
		
	}

}
