package org.eclipse.dirigible.runtime.scripting;

import java.io.PrintStream;
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
 * The Injected API interface collecting all the standard application services and utilities, which the developer can
 * write his/her code against. It includes also the services provided from the underlying platform exposed as injected
 * services e.g. Datasources, Mail Service, Configuration Service, Messaging Service, etc.
 */
public interface IInjectedAPI {

	/**
	 * Getter for the execution context object
	 *
	 * @return map with the context parameters
	 */
	public Map<Object, Object> getExecutionContext();

	/**
	 * Getter for the default (system) output set before the execution
	 *
	 * @return the standard PrintStream object
	 */
	public PrintStream getSystemOutput();

	/**
	 * Getter for the Default Datasource injected on server start
	 *
	 * @return the standard Datasource object
	 */
	public DataSource getDatasource();

	/**
	 * Getter for the standard HTTP Servlet Request
	 *
	 * @return the standard HTTP Servlet Request
	 */
	public HttpServletRequest getRequest();

	/**
	 * Getter for the standard HTTP Servlet Response
	 *
	 * @return the standard HTTP Servlet Response
	 */
	public HttpServletResponse getResponse();

	/**
	 * Getter for the standard HTTP Session
	 *
	 * @return the standard HTTP Session
	 */
	public HttpSession getSession();

	/**
	 * Getter for the standard input of the HTTP Servlet Request
	 *
	 * @return the standard input of the HTTP Servlet Request
	 */
	public Object getRequestInput();

	/**
	 * Getter for the Repository client
	 *
	 * @return the Repository client
	 */
	public IRepository getRepository();

	/**
	 * Getter for the current logged-in user's name
	 *
	 * @return the current logged-in user's name
	 */
	public String getUserName();

	/**
	 * Getter for the standard JNDI initial context coming outside of the OSGi environment
	 *
	 * @return the the standard JNDI initial context
	 */
	public InitialContext getInitialContext();

	/**
	 * Getter for the default Binary Storage
	 *
	 * @return the default Binary Storage
	 */
	public IStorage getBinaryStorage();

	/**
	 * Getter for the default File Storage
	 *
	 * @return the default File Storage
	 */
	public IStorage getFileStorage();

	/**
	 * Getter for the default Configuration Storage
	 *
	 * @return the default Configuration Storage
	 */
	public IStorage getConfigurationStorage();

	/**
	 * Getter for the Mail Service
	 *
	 * @return the Mail Service
	 */
	public IMailService getMailService();

	/**
	 * Getter for the Extension Service
	 *
	 * @return the Extension Service
	 */
	public IExtensionService getExtensionService();

	/**
	 * Getter for the Indexing Service
	 *
	 * @return the Indexing Service
	 */
	public IIndexingService<?> getIndexingService();

	/**
	 * Getter for the Connectivity Service
	 *
	 * @return the Connectivity Service
	 */
	public IConnectivityService getConnectivityService();

	/**
	 * Getter for the Messaging Service
	 *
	 * @return the Messaging Service
	 */
	public IMessagingService getMessagingService();

	/**
	 * Getter for the Templating Service
	 *
	 * @return the Templating Service
	 */
	public ITemplatingService getTemplatingService();

	/**
	 * Getter for the Execution Service
	 *
	 * @return the Execution Service
	 */
	public IExecutionService getExecutionService();

	/**
	 * Getter for the IO Utilities
	 *
	 * @return the IO Utilities
	 */
	public IOUtils getIOUtils();

	/**
	 * Getter for the HTTP Utilities
	 *
	 * @return the HTTP Utilities
	 */
	public HttpUtils getHttpUtils();

	/**
	 * Getter for the Base64 Utilities
	 *
	 * @return the Base64 Utilities
	 */
	public Base64 getBase64Utils();

	/**
	 * Getter for the HEX Utilities
	 *
	 * @return the HEX Utilities
	 */
	public Hex getHexUtils();

	/**
	 * Getter for the Digest Utilities
	 *
	 * @return the Digest Utilities
	 */
	public DigestUtils getDigestUtils();

	/**
	 * Getter for the URL Utilities
	 *
	 * @return the URL Utilities
	 */
	public URLUtils getUrlUtils();

	/**
	 * Getter for the Upload Utilities
	 *
	 * @return the Upload Utilities
	 */
	public ServletFileUpload getUploadUtils();

	/**
	 * Getter for the UUID Utilities
	 *
	 * @return the UUID Utilities
	 */
	public UUID getUuidUtils();

	/**
	 * Getter for the Database Utilities
	 *
	 * @return the Database Utilities
	 */
	public DbUtils getDatabaseUtils();

	/**
	 * Getter for the Database Utilities for DataSource
	 *
	 * @param dataSource
	 *            the datasource assigned to this utils
	 * @return the Database Utilities
	 */
	public DbUtils getDatabaseUtils(DataSource dataSource);

	/**
	 * Getter for the XSS Utilities
	 *
	 * @return the XSS Utilities
	 */
	public StringEscapeUtils getXssUtils();

	/**
	 * Getter for the XML Utilities
	 *
	 * @return the XML Utilities
	 */
	public XMLUtils getXmlUtils();

	/**
	 * Getter for the Exception Utilities
	 *
	 * @return the Exception Utilities
	 */
	public ExceptionUtils getExceptionUtils();

	/**
	 * Getter for a custom object from the injected services container
	 *
	 * @param key
	 *            the name of the parameter
	 * @return the custom object
	 */
	public Object get(String key);

	/**
	 * Setter for a custom object from the injected services container
	 *
	 * @param key
	 *            the name of the parameter
	 * @param value
	 *            the custom object
	 */
	public void set(String key, Object value);

	/**
	 * Getter for the named/custom Datasources container
	 *
	 * @return the Datasources container
	 */
	public NamedDataSourcesUtils getNamedDatasources();

	/**
	 * The console object using the logger
	 */
	public Console getConsole();

}
