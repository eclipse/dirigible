package org.eclipse.dirigible.runtime.scripting;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.extensions.IExtensionService;
import org.eclipse.dirigible.repository.ext.messaging.IMessagingService;
import org.eclipse.dirigible.repository.ext.template.ITemplatingService;
import org.eclipse.dirigible.runtime.scripting.utils.DbUtils;
import org.eclipse.dirigible.runtime.scripting.utils.ExceptionUtils;
import org.eclipse.dirigible.runtime.scripting.utils.HttpUtils;
import org.eclipse.dirigible.runtime.scripting.utils.NamedDataSourcesUtils;
import org.eclipse.dirigible.runtime.scripting.utils.URLUtils;
import org.eclipse.dirigible.runtime.scripting.utils.XMLUtils;

/**
 * Builder for wrapper object API containing type-safe access to the available injected objects, services and utilities
 */
public class InjectedAPIBuilder implements IInjectedAPI, IInjectedAPIModifiers {

	/**
	 * The execution context object
	 */
	private Map<Object, Object> executionContext;

	/**
	 * The default (system) output set before the execution
	 */
	private PrintStream systemOutput;

	/**
	 * Default datasource injected on server start
	 */
	private DataSource datasource;

	/**
	 * The standard HTTP Servlet Request
	 */
	private HttpServletRequest request;

	/**
	 * The standard HTTP Servlet Response
	 */
	private HttpServletResponse response;

	/**
	 * The standard HTTP Session
	 */
	private HttpSession session;

	/**
	 * The input stream of the HTTP Request
	 */
	private Object requestInput;

	/**
	 * The Repository facade
	 */
	private IRepository repository;

	/**
	 * The authenticated user if any, otherwise 'guest'
	 */
	private String userName;

	/**
	 * The server's default JNDI Initial Context
	 */
	private InitialContext initialContext;

	/**
	 * The Binary Storage facade
	 */
	private IStorage binaryStorage;

	/**
	 * The File Storage facade
	 */
	private IStorage fileStorage;

	/**
	 * The Configuration Storage facade
	 */
	private IStorage configurationStorage;

	/**
	 * Default Mail Service injected by the platform
	 */
	private IMailService mailService;

	/**
	 * The service managing extension points and extensions
	 */
	private IExtensionService extensionService;

	/**
	 * The indexing service facade
	 */
	private IIndexingService<?> indexingService;

	/**
	 * The connectivity configuration service injected by the platform
	 */
	private IConnectivityService connectivityService;

	/**
	 * The messaging service facade
	 */
	private IMessagingService messagingService;

	/**
	 * The templating service facade
	 */
	private ITemplatingService templatingService;

	/**
	 * Utilities
	 */

	private IOUtils ioUtils;

	private HttpUtils httpUtils;

	private Base64 base64Utils;

	private Hex hexUtils;

	private DigestUtils digestUtils;

	private URLUtils urlUtils;

	private ServletFileUpload uploadUtils;

	private UUID uuidUtils;

	private DbUtils dbUtils;

	private StringEscapeUtils xssUtils;

	private XMLUtils xmlUtils;

	private ExceptionUtils exceptionUtils;

	/**
	 * The generic map for custom object and services registered via the extension point
	 */
	private Map<String, Object> generic = new HashMap<String, Object>();

	private NamedDataSourcesUtils namedDataSourceUtils;

	@Override
	public Map<Object, Object> getExecutionContext() {
		return executionContext;
	}

	@Override
	public void setExecutionContext(Map<Object, Object> executionContext) {
		this.executionContext = executionContext;
	}

	@Override
	public PrintStream getSystemOutput() {
		return systemOutput;
	}

	@Override
	public void setSystemOutput(PrintStream systemOutput) {
		this.systemOutput = systemOutput;
	}

	@Override
	public DataSource getDatasource() {
		return datasource;
	}

	@Override
	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public HttpServletResponse getResponse() {
		return response;
	}

	@Override
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public HttpSession getSession() {
		return session;
	}

	@Override
	public void setSession(HttpSession session) {
		this.session = session;
	}

	@Override
	public Object getRequestInput() {
		return requestInput;
	}

	@Override
	public void setRequestInput(Object requestInput) {
		this.requestInput = requestInput;
	}

	@Override
	public IRepository getRepository() {
		return repository;
	}

	@Override
	public void setRepository(IRepository repository) {
		this.repository = repository;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public InitialContext getInitialContext() {
		return initialContext;
	}

	@Override
	public void setInitialContext(InitialContext initialContext) {
		this.initialContext = initialContext;
	}

	@Override
	public IStorage getBinaryStorage() {
		return binaryStorage;
	}

	@Override
	public void setBinaryStorage(IStorage binaryStorage) {
		this.binaryStorage = binaryStorage;
	}

	@Override
	public IStorage getFileStorage() {
		return fileStorage;
	}

	@Override
	public void setFileStorage(IStorage fileStorage) {
		this.fileStorage = fileStorage;
	}

	@Override
	public IStorage getConfigurationStorage() {
		return configurationStorage;
	}

	@Override
	public void setConfigurationStorage(IStorage configurationStorage) {
		this.configurationStorage = configurationStorage;
	}

	@Override
	public IMailService getMailService() {
		return mailService;
	}

	@Override
	public void setMailService(IMailService mailService) {
		this.mailService = mailService;
	}

	@Override
	public IExtensionService getExtensionService() {
		return extensionService;
	}

	@Override
	public void setExtensionService(IExtensionService extensionService) {
		this.extensionService = extensionService;
	}

	@Override
	public IIndexingService<?> getIndexingService() {
		return indexingService;
	}

	@Override
	public void setIndexingService(IIndexingService<?> indexingService) {
		this.indexingService = indexingService;
	}

	@Override
	public IConnectivityService getConnectivityService() {
		return connectivityService;
	}

	@Override
	public void setConnectivityService(IConnectivityService connectivityService) {
		this.connectivityService = connectivityService;
	}

	@Override
	public IOUtils getIOUtils() {
		return ioUtils;
	}

	@Override
	public void setIOUtils(IOUtils ioUtils) {
		this.ioUtils = ioUtils;
	}

	@Override
	public HttpUtils getHttpUtils() {
		return httpUtils;
	}

	@Override
	public void setHttpUtils(HttpUtils httpUtils) {
		this.httpUtils = httpUtils;
	}

	@Override
	public Base64 getBase64Utils() {
		return base64Utils;
	}

	@Override
	public void setBase64Utils(Base64 base64Utils) {
		this.base64Utils = base64Utils;
	}

	@Override
	public Hex getHexUtils() {
		return hexUtils;
	}

	@Override
	public void setHexUtils(Hex hexUtils) {
		this.hexUtils = hexUtils;
	}

	@Override
	public DigestUtils getDigestUtils() {
		return digestUtils;
	}

	@Override
	public void setDigestUtils(DigestUtils digestUtils) {
		this.digestUtils = digestUtils;
	}

	@Override
	public URLUtils getUrlUtils() {
		return urlUtils;
	}

	@Override
	public void setUrlUtils(URLUtils urlUtils) {
		this.urlUtils = urlUtils;
	}

	@Override
	public ServletFileUpload getUploadUtils() {
		return uploadUtils;
	}

	@Override
	public void setUploadUtils(ServletFileUpload uploadUtils) {
		this.uploadUtils = uploadUtils;
	}

	@Override
	public UUID getUuidUtils() {
		return uuidUtils;
	}

	@Override
	public void setUuidUtils(UUID uuidUtils) {
		this.uuidUtils = uuidUtils;
	}

	@Override
	public DbUtils getDatabaseUtils() {
		return dbUtils;
	}

	@Override
	public void setDatabaseUtils(DbUtils dbUtils) {
		this.dbUtils = dbUtils;
	}

	@Override
	public StringEscapeUtils getXssUtils() {
		return xssUtils;
	}

	@Override
	public void setXssUtils(StringEscapeUtils xssUtils) {
		this.xssUtils = xssUtils;
	}

	@Override
	public XMLUtils getXmlUtils() {
		return xmlUtils;
	}

	@Override
	public void setXmlUtils(XMLUtils xmlUtils) {
		this.xmlUtils = xmlUtils;
	}

	@Override
	public ExceptionUtils getExceptionUtils() {
		return exceptionUtils;
	}

	@Override
	public void setExceptionUtils(ExceptionUtils exceptionUtils) {
		this.exceptionUtils = exceptionUtils;
	}

	@Override
	public IMessagingService getMessagingService() {
		return messagingService;
	}

	@Override
	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}

	@Override
	public Object get(String key) {
		return generic.get(key);
	}

	@Override
	public void set(String key, Object value) {
		generic.put(key, value);
	}

	@Override
	public ITemplatingService getTemplatingService() {
		return this.templatingService;
	}

	@Override
	public void setTemplatingService(ITemplatingService templatingService) {
		this.templatingService = templatingService;
	}

	@Override
	public NamedDataSourcesUtils getNamedDatasources() {
		return this.namedDataSourceUtils;
	}

	@Override
	public void setNamedDataSourcesUtils(NamedDataSourcesUtils namedDataSourceUtils) {
		this.namedDataSourceUtils = namedDataSourceUtils;
	}

}
