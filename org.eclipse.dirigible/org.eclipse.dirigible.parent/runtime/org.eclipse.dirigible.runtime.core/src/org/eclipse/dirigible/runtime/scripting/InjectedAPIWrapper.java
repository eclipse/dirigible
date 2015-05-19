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
import org.eclipse.dirigible.runtime.scripting.utils.DbUtils;
import org.eclipse.dirigible.runtime.scripting.utils.HttpUtils;
import org.eclipse.dirigible.runtime.scripting.utils.URLUtils;
import org.eclipse.dirigible.runtime.scripting.utils.XMLUtils;


/**
 * The wrapper object containing type-safe access to the available injected objects, services and utilities
 *
 */
public class InjectedAPIWrapper implements IInjectedAPI {
	
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
	private IIndexingService indexingService;
	
	/**
	 * The connectivity configuration service injected by the platform
	 */
	private IConnectivityService connectivityService;
	
	
	
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
	
	
	/**
	 * The generic map for custom object and services registered via the extension point 
	 */
	private Map<String, Object> generic = new HashMap<String, Object>();
	
	
	public Map<Object, Object> getExecutionContext() {
		return executionContext;
	}
	
	public void setExecutionContext(Map<Object, Object> executionContext) {
		this.executionContext = executionContext;
	}
	
	public PrintStream getSystemOutput() {
		return systemOutput;
	}

	public void setSystemOutput(PrintStream systemOutput) {
		this.systemOutput = systemOutput;
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public Object getRequestInput() {
		return requestInput;
	}

	public void setRequestInput(Object requestInput) {
		this.requestInput = requestInput;
	}

	public IRepository getRepository() {
		return repository;
	}

	public void setRepository(IRepository repository) {
		this.repository = repository;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public InitialContext getInitialContext() {
		return initialContext;
	}

	public void setInitialContext(InitialContext initialContext) {
		this.initialContext = initialContext;
	}

	public IStorage getBinaryStorage() {
		return binaryStorage;
	}

	public void setBinaryStorage(IStorage binaryStorage) {
		this.binaryStorage = binaryStorage;
	}

	public IStorage getFileStorage() {
		return fileStorage;
	}

	public void setFileStorage(IStorage fileStorage) {
		this.fileStorage = fileStorage;
	}

	public IStorage getConfigurationStorage() {
		return configurationStorage;
	}

	public void setConfigurationStorage(IStorage configurationStorage) {
		this.configurationStorage = configurationStorage;
	}

	public IMailService getMailService() {
		return mailService;
	}

	public void setMailService(IMailService mailService) {
		this.mailService = mailService;
	}

	public IExtensionService getExtensionService() {
		return extensionService;
	}

	public void setExtensionService(IExtensionService extensionService) {
		this.extensionService = extensionService;
	}

	public IIndexingService getIndexingService() {
		return indexingService;
	}

	public void setIndexingService(IIndexingService indexingService) {
		this.indexingService = indexingService;
	}

	public IConnectivityService getConnectivityService() {
		return connectivityService;
	}

	public void setConnectivityService(IConnectivityService connectivityService) {
		this.connectivityService = connectivityService;
	}

	public IOUtils getIOUtils() {
		return ioUtils;
	}

	public void setIOUtils(IOUtils ioUtils) {
		this.ioUtils = ioUtils;
	}

	public HttpUtils getHttpUtils() {
		return httpUtils;
	}

	public void setHttpUtils(HttpUtils httpUtils) {
		this.httpUtils = httpUtils;
	}

	public Base64 getBase64Utils() {
		return base64Utils;
	}

	public void setBase64Utils(Base64 base64Utils) {
		this.base64Utils = base64Utils;
	}

	public Hex getHexUtils() {
		return hexUtils;
	}

	public void setHexUtils(Hex hexUtils) {
		this.hexUtils = hexUtils;
	}

	public DigestUtils getDigestUtils() {
		return digestUtils;
	}

	public void setDigestUtils(DigestUtils digestUtils) {
		this.digestUtils = digestUtils;
	}

	public URLUtils getUrlUtils() {
		return urlUtils;
	}

	public void setUrlUtils(URLUtils urlUtils) {
		this.urlUtils = urlUtils;
	}

	public ServletFileUpload getUploadUtils() {
		return uploadUtils;
	}

	public void setUploadUtils(ServletFileUpload uploadUtils) {
		this.uploadUtils = uploadUtils;
	}

	public UUID getUuidUtils() {
		return uuidUtils;
	}

	public void setUuidUtils(UUID uuidUtils) {
		this.uuidUtils = uuidUtils;
	}

	public DbUtils getDatabaseUtils() {
		return dbUtils;
	}

	public void setDatabaseUtils(DbUtils dbUtils) {
		this.dbUtils = dbUtils;
	}

	public StringEscapeUtils getXssUtils() {
		return xssUtils;
	}

	public void setXssUtils(StringEscapeUtils xssUtils) {
		this.xssUtils = xssUtils;
	}

	public XMLUtils getXmlUtils() {
		return xmlUtils;
	}

	public void setXmlUtils(XMLUtils xmlUtils) {
		this.xmlUtils = xmlUtils;
	}

	public Object get(String key) {
		return generic.get(key);
	}
	
	public void set(String key, Object value) {
		generic.put(key, value);
	}
	
}
