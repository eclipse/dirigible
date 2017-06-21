package org.eclipse.dirigible.engine.api;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractResourceExecutor implements IResourceExecutor {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractResourceExecutor.class);

	@Inject
	private IRepository repository;
	
	protected IRepository getRepository() {
		return repository;
	}

	@Override
	public byte[] getResourceContent(String root, String module) throws RepositoryException {
		String repositoryPath = createResourcePath(root, module);
		final IResource resource = repository.getResource(repositoryPath);
		if (resource.exists()) {
			return resource.getContent();
		}
		
		// try from the classloader
		try {
			String location = IRepository.SEPARATOR + module;
			InputStream bundled = AbstractScriptExecutor.class.getResourceAsStream(location);
			if (bundled != null) {
				return IOUtils.toByteArray(bundled);
			}
		} catch (IOException e) {
			throw new RepositoryException(e);
		}
				
		final String logMsg = String.format("There is no resource [%s] at the specified path: %s", resource.getName(), repositoryPath);
		logger.error(logMsg);
		throw new RepositoryException(logMsg);
	}
	
	@Override
	public ICollection getCollection(String root, String module) throws RepositoryException {
		String repositoryPath = createResourcePath(root, module);
		final ICollection collection = repository.getCollection(repositoryPath);
		if (collection.exists()) {
			return collection;
		}
		
		final String logMsg = String.format("There is no collection [%s] at the specified Service path: %s", collection.getName(), repositoryPath);
		logger.error(logMsg);
		throw new RepositoryException(logMsg);
	}

	@Override
	public IResource getResource(String root, String module) throws RepositoryException {
		String repositoryPath = createResourcePath(root, module);
		final IResource resource = repository.getResource(repositoryPath);
		if (resource.exists()) {
			return resource;
		}
		
		final String logMsg = String.format("There is no collection [%s] at the specified path: %s", resource.getName(), repositoryPath);
		logger.error(logMsg);
		throw new RepositoryException(logMsg);
		
	}
	
	@Override
	public boolean existResource(String root, String module) throws RepositoryException {
		String repositoryPath = createResourcePath(root, module);
		final IResource resource = repository.getResource(repositoryPath);
		return resource.exists();
	}
	
	@Override
	public String createResourcePath(String root, String module) {
		return createResourcePath(root, module, null);
	}
	
	@Override
	public String createResourcePath(String root, String module, String extension) {
		StringBuilder buff = new StringBuilder().append(root).append(IRepository.SEPARATOR).append(module);
		if (extension != null) {
			buff.append(extension);
		}
		String resourcePath = buff.toString();
		return resourcePath;
	}
	
}
