package org.eclipse.dirigible.engine.web.processor;

import java.io.IOException;

import javax.ws.rs.NotFoundException;

import org.eclipse.dirigible.repository.api.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class WebEngineProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(WebEngineProcessor.class.getCanonicalName());
	
	@Inject
	private IRepository repository;
	
	public String getResource(String path) {
		try {
			return new String(repository.getResource(path).getContent());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new NotFoundException(e.getMessage());
		}
		
	}

}
