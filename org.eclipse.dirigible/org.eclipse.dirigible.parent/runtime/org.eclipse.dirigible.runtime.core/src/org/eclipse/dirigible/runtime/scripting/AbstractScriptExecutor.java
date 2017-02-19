/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.lucene.document.Document;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
// import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionManager;
import org.eclipse.dirigible.repository.ext.generation.IGenerationService;
import org.eclipse.dirigible.repository.ext.messaging.MessageHub;
import org.eclipse.dirigible.repository.ext.template.TemplatingEngine;
import org.eclipse.dirigible.repository.ext.utils.OSGiUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.mail.MailServiceFactory;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;
import org.eclipse.dirigible.runtime.scripting.utils.ConfigStorageUtils;
import org.eclipse.dirigible.runtime.scripting.utils.ConnectivityConfigurationUtils;
import org.eclipse.dirigible.runtime.scripting.utils.DbUtils;
import org.eclipse.dirigible.runtime.scripting.utils.DocumentConfigurationUtils;
import org.eclipse.dirigible.runtime.scripting.utils.ExceptionUtils;
import org.eclipse.dirigible.runtime.scripting.utils.ExecutionService;
import org.eclipse.dirigible.runtime.scripting.utils.FileStorageUtils;
import org.eclipse.dirigible.runtime.scripting.utils.HttpUtils;
import org.eclipse.dirigible.runtime.scripting.utils.IndexingService;
import org.eclipse.dirigible.runtime.scripting.utils.LifecycleServiceUtils;
import org.eclipse.dirigible.runtime.scripting.utils.NamedDataSourcesUtils;
import org.eclipse.dirigible.runtime.scripting.utils.StorageUtils;
import org.eclipse.dirigible.runtime.scripting.utils.URLUtils;
import org.eclipse.dirigible.runtime.scripting.utils.WorkspacesServiceUtils;
import org.eclipse.dirigible.runtime.scripting.utils.XMLUtils;

public abstract class AbstractScriptExecutor implements IScriptExecutor, IBaseScriptExecutor {

	private static final String THERE_IS_NO_RESOURCE_AT_THE_SPECIFIED_SERVICE_PATH = Messages
			.getString("ScriptLoader.THERE_IS_NO_RESOURCE_AT_THE_SPECIFIED_SERVICE_PATH"); //$NON-NLS-1$

	private static final String THERE_IS_NO_COLLECTION_AT_THE_SPECIFIED_SERVICE_PATH = Messages
			.getString("ScriptLoader.THERE_IS_NO_COLLECTION_AT_THE_SPECIFIED_SERVICE_PATH"); //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(AbstractScriptExecutor.class);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.scripting.IScriptExecutor#executeServiceModule(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String, java.util.Map)
	 */
	@Override
	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response, String module, Map<Object, Object> executionContext)
			throws IOException {
		return executeServiceModule(request, response, request == null ? null : request.getInputStream(), module, executionContext);
	}

	protected abstract Object executeServiceModule(HttpServletRequest request, HttpServletResponse response, Object input, String module,
			Map<Object, Object> executionContext) throws IOException;

	protected abstract void registerDefaultVariable(Object scope, String name, Object value);

	@Override
	public void registerDefaultVariableInContextAndScope(Map<Object, Object> executionContext, Object scope, String name, Object value) {
		if (executionContext.get(name) == null) {
			registerDefaultVariable(scope, name, value);
			executionContext.put(name, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.scripting.IBaseScriptExecutor#registerDefaultVariables(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.util.Map,
	 * org.eclipse.dirigible.repository.api.IRepository, java.lang.Object)
	 */
	@Override
	public void registerDefaultVariables(HttpServletRequest request, HttpServletResponse response, Object input, Map<Object, Object> executionContext,
			IRepository repository, Object scope) {

		InjectedAPIBuilder apiBuilder = new InjectedAPIBuilder();

		if (executionContext == null) {
			// in case executionContext is not provided from outside
			executionContext = new HashMap<Object, Object>();
		}

		// Objects

		// put the execution context
		registerDefaultVariable(scope, IInjectedAPIAliases.EXECUTION_CONTEXT, executionContext);
		apiBuilder.setExecutionContext(executionContext);

		// put the console
		Console console = new Console();
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.CONSOLE, console);
		apiBuilder.setConsole(console);

		// put the system out
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.SYSTEM_OUTPUT, System.out);
		apiBuilder.setSystemOutput(System.out);

		// put the default data source
		DataSource dataSource = null;
		dataSource = registerDefaultDatasource(request, executionContext, scope, apiBuilder);

		// put request
		registerRequest(request, executionContext, scope, apiBuilder);

		// put response
		registerResponse(response, executionContext, scope, apiBuilder);

		// put repository
		registerRepository(executionContext, repository, scope, apiBuilder);

		// user name
		registerUserName(request, executionContext, scope, apiBuilder);

		// the input from the execution chain if any
		registerRequestInput(input, executionContext, scope, apiBuilder);

		// put JNDI
		registerInitialContext(request, executionContext, scope, apiBuilder);

		// Simple binary storage
		registerBinaryStorage(executionContext, scope, apiBuilder, dataSource);

		// Simple file storage
		registerFileStorage(executionContext, scope, apiBuilder, dataSource);

		// Simple configuration storage
		registerConfigurationStorage(executionContext, scope, apiBuilder, dataSource);

		// Services

		// put mail sender
		registerMailService(request, executionContext, scope, apiBuilder);

		// Extension Manager
		registerExtensionManager(request, executionContext, repository, scope, apiBuilder, dataSource);

		// Apache Lucene Indexer
		registerIndexingService(executionContext, scope, apiBuilder);

		// Connectivity Configuration service
		registerConnectivityService(executionContext, scope, apiBuilder);

		// Document service
		registerDocumentService(executionContext, scope, apiBuilder);

		// Messaging Service
		registerMessagingService(request, executionContext, scope, apiBuilder, dataSource);

		// Templating Service
		registerTemplatingService(executionContext, scope, apiBuilder);

		// Executing Service
		registerExecutingService(executionContext, scope, apiBuilder);

		// Generation Service
		registerGenerationService(executionContext, scope, apiBuilder);

		// Lifecycle Service
		registerLifecycleService(executionContext, scope, apiBuilder);

		// Workspaces Service
		registerWorkspacesService(executionContext, scope, apiBuilder);

		// Utils

		// put Apache Commons IOUtils
		IOUtils ioUtils = new IOUtils();
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.IO_UTILS, ioUtils);
		apiBuilder.setIOUtils(ioUtils);

		// put Apache Commons HttpClient and related classes wrapped with a factory like HttpUtils
		HttpUtils httpUtils = new HttpUtils();
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.HTTP_UTILS, httpUtils);
		apiBuilder.setHttpUtils(httpUtils);

		// put Apache Commons Codecs
		Base64 base64Codec = new Base64();
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.BASE64_UTILS, base64Codec);
		apiBuilder.setBase64Utils(base64Codec);

		Hex hexCodec = new Hex();
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.HEX_UTILS, hexCodec);
		apiBuilder.setHexUtils(hexCodec);

		DigestUtils digestUtils = new DigestUtils();
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.DIGEST_UTILS, digestUtils);
		apiBuilder.setDigestUtils(digestUtils);

		// standard URLEncoder and URLDecoder functionality
		URLUtils urlUtils = new URLUtils();
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.URL_UTILS, urlUtils);
		apiBuilder.setUrlUtils(urlUtils);

		// file upload
		ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory());
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.UPLOAD_UTILS, fileUpload);
		apiBuilder.setUploadUtils(fileUpload);

		// UUID
		UUID uuid = new UUID(0, 0);
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.UUID_UTILS, uuid);
		apiBuilder.setUuidUtils(uuid);

		// DbUtils
		DbUtils dbUtils = new DbUtils(dataSource);
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.DB_UTILS, dbUtils);
		apiBuilder.setDatabaseUtils(dbUtils);

		// EscapeUtils
		StringEscapeUtils stringEscapeUtils = new StringEscapeUtils();
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.XSS_UTILS, stringEscapeUtils);
		apiBuilder.setXssUtils(stringEscapeUtils);

		// XML to JSON and vice-versa
		XMLUtils xmlUtils = new XMLUtils();
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.XML_UTILS, xmlUtils);
		apiBuilder.setXmlUtils(xmlUtils);

		// ExceptionUtils
		ExceptionUtils exceptionUtils = new ExceptionUtils();
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.EXCEPTION_UTILS, exceptionUtils);
		apiBuilder.setExceptionUtils(exceptionUtils);

		// Named DataSources Utils
		NamedDataSourcesUtils namedDataSourcesUtils = new NamedDataSourcesUtils(request);
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.DATASOURCES_UTILS, namedDataSourcesUtils);
		apiBuilder.setNamedDataSourcesUtils(namedDataSourcesUtils);

		if (OSGiUtils.isOSGiEnvironment()) {
			// register objects via extension - only in OSGi environment
			CustomInjectedObjectsFactory.registerCustomObjects(this, executionContext, scope, apiBuilder);
		}

		InjectedAPIWrapper api = new InjectedAPIWrapper(apiBuilder);
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.API, api);
	}

	protected void registerTemplatingService(Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		try {
			TemplatingEngine templatingEngine = new TemplatingEngine();
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.TEMPLATING_ENGINE, templatingEngine);
			apiBuilder.setTemplatingService(templatingEngine);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerExecutingService(Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		try {
			IExecutionService executionService = new ExecutionService();
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.EXECUTION_SERVICE, executionService);
			apiBuilder.setExecutionService(executionService);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerGenerationService(Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		try {
			IGenerationService generationService = new GenerationServiceFactory();
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.GENERATION_SERVICE, generationService);
			apiBuilder.setGenerationService(generationService);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerLifecycleService(Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		try {
			ILifecycleService lifecycleService = new LifecycleServiceUtils();
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.LIFECYCLE_SERVICE, lifecycleService);
			apiBuilder.setLifecycleService(lifecycleService);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerWorkspacesService(Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		try {
			IWorkspacesService workspacesService = new WorkspacesServiceUtils();
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.WORKSPACES_SERVICE, workspacesService);
			apiBuilder.setWorkspacesService(workspacesService);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerMessagingService(HttpServletRequest request, Map<Object, Object> executionContext, Object scope,
			InjectedAPIBuilder apiBuilder, DataSource dataSource) {
		try {
			MessageHub messageHub = new MessageHub(dataSource, request);
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.MESSAGE_HUB, messageHub);
			apiBuilder.setMessagingService(messageHub);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerConnectivityService(Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		try {
			ConnectivityConfigurationUtils configurationUtils = new ConnectivityConfigurationUtils();
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.CONNECTIVITY_SERVICE, configurationUtils);
			apiBuilder.setConnectivityService(configurationUtils);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerDocumentService(Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		try {
			DocumentConfigurationUtils documentUtils = new DocumentConfigurationUtils();
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.DOCUMENT_SERVICE, documentUtils);
			apiBuilder.setDocumentService(documentUtils);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerIndexingService(Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		try {
			IndexingService<Document> indexingUtils = new IndexingService<Document>();
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.INDEXING_SERVICE, indexingUtils);
			apiBuilder.setIndexingService(indexingUtils);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerExtensionManager(HttpServletRequest request, Map<Object, Object> executionContext, IRepository repository, Object scope,
			InjectedAPIBuilder apiBuilder, DataSource dataSource) {
		try {
			ExtensionManager extensionManager = new ExtensionManager(repository, dataSource, request);
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.EXTENSIONS_SERVICE, extensionManager);
			apiBuilder.setExtensionService(extensionManager);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerMailService(HttpServletRequest request, Map<Object, Object> executionContext, Object scope,
			InjectedAPIBuilder apiBuilder) {
		try {
			IMailService mailSender = MailServiceFactory.createMailService(request);
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.MAIL_SERVICE, mailSender);
			apiBuilder.setMailService(mailSender);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerConfigurationStorage(Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder,
			DataSource dataSource) {
		try {
			ConfigStorageUtils configStorageUtils = new ConfigStorageUtils(dataSource);
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.CONFIGURATION_STORAGE, configStorageUtils);
			apiBuilder.setConfigurationStorage(configStorageUtils);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerFileStorage(Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder, DataSource dataSource) {
		try {
			FileStorageUtils fileStorageUtils = new FileStorageUtils(dataSource);
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.FILE_STORAGE, fileStorageUtils);
			apiBuilder.setFileStorage(fileStorageUtils);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerBinaryStorage(Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder, DataSource dataSource) {
		try {
			StorageUtils storageUtils = new StorageUtils(dataSource);
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.STORAGE, storageUtils);
			apiBuilder.setBinaryStorage(storageUtils);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerInitialContext(HttpServletRequest request, Map<Object, Object> executionContext, Object scope,
			InjectedAPIBuilder apiBuilder) {
		try {
			if (request != null) {
				// JNDI context
				InitialContext initialContext = (InitialContext) request.getAttribute(ICommonConstants.INITIAL_CONTEXT);
				registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.INITIAL_CONTEXT, initialContext);
				apiBuilder.setInitialContext(initialContext);
			}
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	protected void registerRequestInput(Object input, Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.REQUEST_INPUT, input);
		apiBuilder.setRequestInput(input);
	}

	protected void registerUserName(HttpServletRequest request, Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		String userName = RepositoryFacade.getUser(request);
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.USER, userName);
		apiBuilder.setUserName(userName);
	}

	protected void registerRepository(Map<Object, Object> executionContext, IRepository repository, Object scope, InjectedAPIBuilder apiBuilder) {
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.REPOSITORY, repository);
		apiBuilder.setRepository(repository);
	}

	protected void registerResponse(HttpServletResponse response, Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.HTTP_RESPONSE, response);
		apiBuilder.setResponse(response);
	}

	protected void registerRequest(HttpServletRequest request, Map<Object, Object> executionContext, Object scope, InjectedAPIBuilder apiBuilder) {
		// put request
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.HTTP_REQUEST, request);
		apiBuilder.setRequest(request);
		if (request != null) {
			// put session
			registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.HTTP_SESSION, request.getSession());
			apiBuilder.setSession(request.getSession());
		}
	}

	protected DataSource registerDefaultDatasource(HttpServletRequest request, Map<Object, Object> executionContext, Object scope,
			InjectedAPIBuilder apiBuilder) {
		DataSource dataSource;
		dataSource = DataSourceFacade.getInstance().getDataSource(request);
		registerDefaultVariableInContextAndScope(executionContext, scope, IInjectedAPIAliases.DEFAULT_DATASOURCE, dataSource);
		apiBuilder.setDatasource(dataSource);
		return dataSource;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.runtime.scripting.IBaseScriptExecutor#readResourceData(org.eclipse.dirigible.repository.api
	 * .IRepository, java.lang.String)
	 */
	@Override
	public byte[] readResourceData(IRepository repository, String repositoryPath) throws IOException {
		final IResource resource = repository.getResource(repositoryPath);
		if (!resource.exists()) {
			final String logMsg = String.format(THERE_IS_NO_RESOURCE_AT_THE_SPECIFIED_SERVICE_PATH, resource.getName(), repositoryPath);
			logger.error(logMsg);
			throw new IOException(logMsg);
		}
		return resource.getContent();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.runtime.scripting.IBaseScriptExecutor#retrieveModule(org.eclipse.dirigible.repository.api.
	 * IRepository, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Module retrieveModule(IRepository repository, String module, String extension, String... rootPaths) throws IOException {

		for (String rootPath : rootPaths) {
			String resourcePath = createResourcePath(rootPath, module, extension);
			final IResource resource = repository.getResource(resourcePath);
			if (resource.exists()) {
				return new Module(getModuleName(resource.getPath()), resource.getPath(), readResourceData(repository, resourcePath));
			}
		}

		final String logMsg = String.format(THERE_IS_NO_RESOURCE_AT_THE_SPECIFIED_SERVICE_PATH, (module + extension), Arrays.toString(rootPaths));
		logger.error(logMsg);
		throw new FileNotFoundException(logMsg);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.runtime.scripting.IBaseScriptExecutor#retrieveModulesByExtension(org.eclipse.dirigible.
	 * repository.api.IRepository, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Module> retrieveModulesByExtension(IRepository repository, String extension, String... rootPaths) throws IOException {
		Map<String, Module> modules = new HashMap<String, Module>();
		for (int i = rootPaths.length - 1; i >= 0; i--) {
			List<IEntity> entities = repository.searchName(rootPaths[i], "%" + extension, false);
			for (IEntity entity : entities) {
				if (entity.exists()) {
					String path = entity.getPath();
					String moduleName = getModuleName(path);
					Module module = new Module(moduleName, path, readResourceData(repository, path), entity.getInformation());
					modules.put(moduleName, module);
				}
			}
		}
		return Arrays.asList(modules.values().toArray(new Module[] {}));
	}

	private String getModuleName(String path) {
		path = FilenameUtils.separatorsToUnix(path);
		String workspace = ICommonConstants.WORKSPACE + ICommonConstants.SEPARATOR;
		String scriptingServices = getModuleType(path) // ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES
				+ ICommonConstants.SEPARATOR;
		int indexOfSandbox = path.indexOf(ICommonConstants.SANDBOX);
		int indexOfRegistry = path.indexOf(ICommonConstants.REGISTRY);
		String result = null;
		if ((indexOfSandbox > 0) || (indexOfRegistry > 0)) {
			int indexOfScriptingServices = path.indexOf(scriptingServices);
			result = path.substring(indexOfScriptingServices + scriptingServices.length());
		} else {
			int indexOfWorkspace = path.indexOf(workspace);
			result = path.substring(indexOfWorkspace + workspace.length());
			result = result.replace(scriptingServices, "");
		}
		return result;
	}

	protected abstract String getModuleType(String path);

	private String createResourcePath(String root, String module, String extension) {
		StringBuilder buff = new StringBuilder().append(root).append(IRepository.SEPARATOR).append(module);
		if (extension != null) {
			buff.append(extension);
		}
		String resourcePath = buff.toString();
		return resourcePath;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.runtime.scripting.IBaseScriptExecutor#getCollection(org.eclipse.dirigible.repository.api.
	 * IRepository, java.lang.String)
	 */
	@Override
	public ICollection getCollection(IRepository repository, String repositoryPath) throws IOException {
		final ICollection collection = repository.getCollection(repositoryPath);
		if (!collection.exists()) {
			final String logMsg = String.format(THERE_IS_NO_COLLECTION_AT_THE_SPECIFIED_SERVICE_PATH, collection.getName(), repositoryPath);
			logger.error(logMsg);
			throw new IOException(logMsg);
		}
		return collection;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.runtime.scripting.IBaseScriptExecutor#getResource(org.eclipse.dirigible.repository.api.
	 * IRepository, java.lang.String)
	 */
	@Override
	public IResource getResource(IRepository repository, String repositoryPath) throws IOException {
		final IResource resource = repository.getResource(repositoryPath);
		if (!resource.exists()) {
			final String logMsg = String.format(THERE_IS_NO_RESOURCE_AT_THE_SPECIFIED_SERVICE_PATH, resource.getName(), repositoryPath);
			logger.error(logMsg);
			throw new IOException(logMsg);
		}
		return resource;
	}
}
