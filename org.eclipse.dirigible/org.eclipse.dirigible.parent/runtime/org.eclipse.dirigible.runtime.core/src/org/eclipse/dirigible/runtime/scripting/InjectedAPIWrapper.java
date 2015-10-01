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
import org.eclipse.dirigible.runtime.scripting.utils.DbUtils;
import org.eclipse.dirigible.runtime.scripting.utils.HttpUtils;
import org.eclipse.dirigible.runtime.scripting.utils.URLUtils;
import org.eclipse.dirigible.runtime.scripting.utils.XMLUtils;


/**
 * The wrapper object containing type-safe access to the available injected objects, services and utilities
 *
 */
public class InjectedAPIWrapper implements IInjectedAPI {

	private InjectedAPIBuilder builder;
	
	public InjectedAPIWrapper(InjectedAPIBuilder builder) {
		this.builder = builder;
	}
	
	@Override
	public Map<Object, Object> getExecutionContext() {
		return builder.getExecutionContext();
	}
	
	@Override
	public PrintStream getSystemOutput() {
		return builder.getSystemOutput();
	}

	@Override
	public DataSource getDatasource() {
		return builder.getDatasource();
	}

	@Override
	public HttpServletRequest getRequest() {
		return builder.getRequest();
	}

	@Override
	public HttpServletResponse getResponse() {
		return builder.getResponse();
	}

	@Override
	public HttpSession getSession() {
		return builder.getSession();
	}

	@Override
	public Object getRequestInput() {
		return builder.getRequestInput();
	}

	@Override
	public IRepository getRepository() {
		return builder.getRepository();
	}

	@Override
	public String getUserName() {
		return builder.getUserName();
	}

	@Override
	public InitialContext getInitialContext() {
		return builder.getInitialContext();
	}

	@Override
	public IStorage getBinaryStorage() {
		return builder.getBinaryStorage();
	}

	@Override
	public IStorage getFileStorage() {
		return builder.getFileStorage();
	}

	@Override
	public IStorage getConfigurationStorage() {
		return builder.getConfigurationStorage();
	}

	@Override
	public IMailService getMailService() {
		return builder.getMailService();
	}

	@Override
	public IExtensionService getExtensionService() {
		return builder.getExtensionService();
	}

	@Override
	public IIndexingService<?> getIndexingService() {
		return builder.getIndexingService();
	}

	@Override
	public IConnectivityService getConnectivityService() {
		return builder.getConnectivityService();
	}

	@Override
	public IOUtils getIOUtils() {
		return builder.getIOUtils();
	}

	@Override
	public HttpUtils getHttpUtils() {
		return builder.getHttpUtils();
	}

	@Override
	public Base64 getBase64Utils() {
		return builder.getBase64Utils();
	}

	@Override
	public Hex getHexUtils() {
		return builder.getHexUtils();
	}

	@Override
	public DigestUtils getDigestUtils() {
		return builder.getDigestUtils();
	}

	@Override
	public URLUtils getUrlUtils() {
		return builder.getUrlUtils();
	}

	@Override
	public ServletFileUpload getUploadUtils() {
		return builder.getUploadUtils();
	}

	@Override
	public UUID getUuidUtils() {
		return builder.getUuidUtils();
	}

	@Override
	public DbUtils getDatabaseUtils() {
		return builder.getDatabaseUtils();
	}

	@Override
	public StringEscapeUtils getXssUtils() {
		return builder.getXssUtils();
	}

	@Override
	public XMLUtils getXmlUtils() {
		return builder.getXmlUtils();
	}

	@Override
	public IMessagingService getMessagingService() {
		return builder.getMessagingService();
	}

	
	@Override
	public Object get(String key) {
		return builder.get(key);
	}
	
	@Override
	public void set(String key, Object value) {
		builder.set(key, value);
	}
}
