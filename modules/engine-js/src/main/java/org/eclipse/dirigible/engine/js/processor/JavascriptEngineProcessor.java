package org.eclipse.dirigible.engine.js.processor;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.InternalServerErrorException;

import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.repository.api.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JavascriptEngineProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(JavascriptEngineProcessor.class.getCanonicalName());
	
	@Inject
	private IRepository repository;
	
	@Inject 
	private IDatabase database;
	
	@Inject 
	private DataSource dataSource;
	
	public String executeScript(String path) {
		try {
//			return new String(repository.getResource(path).getContent());
			// TODO get the default engine by parameter or file extension and execute the script
			// DIRIGIBLE_JAVASCRIPT_ENGINE_DEFAULT
//			return HttpRequestFacade.getMethod();
			
			return database.getClass().getName();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(e);
		}
		
	}

}
