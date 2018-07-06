Sample class showing how to embed Dirigible into an arbitrary Java application
---

```java
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.engine.js.rhino.processor.RhinoJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.runtime.core.initializer.DirigibleInitializer;

public class MyApp {

	private IRepository repository;
	private IJavascriptEngineExecutor executor;

	public static void main(String[] args) throws ContextException, IOException, ScriptingException {
		MyApp app = new MyApp();
		// name of the module playing a role of an identifier
		String module = "encoder.js";
		// source code for this module, which can be taken from the file system, remote storages, databases, etc.
		String source = "var base64 = require('utils/v3/base64');\n"
				+ "console.log('>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ' + base64.encode('admin:admin'));";
		// execute the module in non-servlet context
		app.execute(null, null, module, source);
	}

	private void execute(HttpServletRequest req, HttpServletResponse res, String module, String source) throws ContextException, IOException, ScriptingException {
		DirigibleInitializer initializer = new DirigibleInitializer();
		
		// initialize the Dirigible instance
		initializer.initialize();
		
		// initialize the repository object
		this.repository = initializer.getInjector().getInstance(IRepository.class);
		// initialize the particular executor - Rhino*, Nashorn* or V8*
		this.executor = initializer.getInjector().getInstance(RhinoJavascriptEngineExecutor.class);

		// initialize the context
		ThreadContextFacade.setUp();
		try {
			// set the request object in a servlet context or null oterwise
			ThreadContextFacade.set(HttpServletRequest.class.getCanonicalName(), req);
			// set the response object in a servlet context or null oterwise
			ThreadContextFacade.set(HttpServletResponse.class.getCanonicalName(), res);
			// execute module
			executeModule(executor, repository, module, source);
		} finally {
			ThreadContextFacade.tearDown();
			// destroy the Dirigible instance
			initializer.destory();
			System.exit(0);
		}
	}

	private Object executeModule(IJavascriptEngineExecutor executor, IRepository repository, String module, String source) throws IOException, ScriptingException {
		try {
			// create the provided source code as module in the Dirigible's registry
			repository.createResource(
				IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + module,
				source.getBytes());
		} catch (RepositoryWriteException e) {
			throw new IOException(IRepositoryStructure.SEPARATOR + module, e);
		}
		// execute the module
		Object result = executor.executeServiceModule(module, null);
		return result;
	}

}
```