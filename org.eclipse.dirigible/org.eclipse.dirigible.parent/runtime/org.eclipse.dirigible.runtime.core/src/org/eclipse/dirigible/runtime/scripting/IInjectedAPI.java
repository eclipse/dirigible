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
import org.eclipse.dirigible.runtime.scripting.utils.DbUtils;
import org.eclipse.dirigible.runtime.scripting.utils.HttpUtils;
import org.eclipse.dirigible.runtime.scripting.utils.URLUtils;
import org.eclipse.dirigible.runtime.scripting.utils.XMLUtils;

public interface IInjectedAPI {

	public Map<Object, Object> getExecutionContext();
	
	public void setExecutionContext(Map<Object, Object> executionContext);
	
	public PrintStream getSystemOutput();

	public void setSystemOutput(PrintStream systemOutput);

	public DataSource getDatasource();

	public void setDatasource(DataSource datasource);

	public HttpServletRequest getRequest();

	public void setRequest(HttpServletRequest request);

	public HttpServletResponse getResponse();

	public void setResponse(HttpServletResponse response);

	public HttpSession getSession();

	public void setSession(HttpSession session);

	public Object getRequestInput();

	public void setRequestInput(Object requestInput);

	public IRepository getRepository();

	public void setRepository(IRepository repository);

	public String getUserName();

	public void setUserName(String userName);

	public InitialContext getInitialContext();

	public void setInitialContext(InitialContext initialContext);

	public IStorage getBinaryStorage();

	public void setBinaryStorage(IStorage binaryStorage);

	public IStorage getFileStorage();

	public void setFileStorage(IStorage fileStorage);

	public IStorage getConfigurationStorage();

	public void setConfigurationStorage(IStorage configurationStorage);

	public IMailService getMailService();

	public void setMailService(IMailService mailService);

	public IExtensionService getExtensionService();

	public void setExtensionService(IExtensionService extensionService);

	public IIndexingService getIndexingService();

	public void setIndexingService(IIndexingService indexingService);

	public IConnectivityService getConnectivityService();

	public void setConnectivityService(IConnectivityService connectivityService);

	public IOUtils getIOUtils();

	public void setIOUtils(IOUtils ioUtils);

	public HttpUtils getHttpUtils();

	public void setHttpUtils(HttpUtils httpUtils);

	public Base64 getBase64Utils();

	public void setBase64Utils(Base64 base64Utils);

	public Hex getHexUtils();

	public void setHexUtils(Hex hexUtils);

	public DigestUtils getDigestUtils();

	public void setDigestUtils(DigestUtils digestUtils);

	public URLUtils getUrlUtils();

	public void setUrlUtils(URLUtils urlUtils);

	public ServletFileUpload getUploadUtils();

	public void setUploadUtils(ServletFileUpload uploadUtils);

	public UUID getUuidUtils();

	public void setUuidUtils(UUID uuidUtils);

	public DbUtils getDatabaseUtils();

	public void setDatabaseUtils(DbUtils dbUtils);

	public StringEscapeUtils getXssUtils();

	public void setXssUtils(StringEscapeUtils xssUtils);

	public XMLUtils getXmlUtils();

	public void setXmlUtils(XMLUtils xmlUtils);

	public Object get(String key);
	
	public void set(String key, Object value);

}
