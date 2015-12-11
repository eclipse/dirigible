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

public interface IInjectedAPI {

	public Map<Object, Object> getExecutionContext();

	public PrintStream getSystemOutput();

	public DataSource getDatasource();

	public HttpServletRequest getRequest();

	public HttpServletResponse getResponse();

	public HttpSession getSession();

	public Object getRequestInput();

	public IRepository getRepository();

	public String getUserName();

	public InitialContext getInitialContext();

	public IStorage getBinaryStorage();

	public IStorage getFileStorage();

	public IStorage getConfigurationStorage();

	public IMailService getMailService();

	public IExtensionService getExtensionService();

	public IIndexingService<?> getIndexingService();

	public IConnectivityService getConnectivityService();

	public IMessagingService getMessagingService();

	public ITemplatingService getTemplatingService();

	public IOUtils getIOUtils();

	public HttpUtils getHttpUtils();

	public Base64 getBase64Utils();

	public Hex getHexUtils();

	public DigestUtils getDigestUtils();

	public URLUtils getUrlUtils();

	public ServletFileUpload getUploadUtils();

	public UUID getUuidUtils();

	public DbUtils getDatabaseUtils();

	public StringEscapeUtils getXssUtils();

	public XMLUtils getXmlUtils();

	public ExceptionUtils getExceptionUtils();

	public Object get(String key);

	public void set(String key, Object value);

	public NamedDataSourcesUtils getNamedDatasources();

}
