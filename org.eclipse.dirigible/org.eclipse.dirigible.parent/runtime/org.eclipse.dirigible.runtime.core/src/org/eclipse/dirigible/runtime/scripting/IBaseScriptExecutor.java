package org.eclipse.dirigible.runtime.scripting;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;

public interface IBaseScriptExecutor {

	void registerDefaultVariables(HttpServletRequest request, HttpServletResponse response, Object input, Map<Object, Object> executionContext,
			IRepository repository, Object scope);

	void registerDefaultVariableInContextAndScope(Map<Object, Object> executionContext, Object scope, String name, Object value);

	byte[] readResourceData(IRepository repository, String repositoryPath) throws IOException;

	Module retrieveModule(IRepository repository, String module, String extension, String... rootPaths) throws IOException;

	List<Module> retrieveModulesByExtension(IRepository repository, String extension, String... rootPaths) throws IOException;

	ICollection getCollection(IRepository repository, String repositoryPath) throws IOException;

	IResource getResource(IRepository repository, String repositoryPath) throws IOException;

}
