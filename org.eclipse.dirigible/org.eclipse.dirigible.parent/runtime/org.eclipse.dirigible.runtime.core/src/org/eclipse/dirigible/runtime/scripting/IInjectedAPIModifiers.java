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
import org.eclipse.dirigible.repository.ext.generation.IGenerationService;
import org.eclipse.dirigible.repository.ext.messaging.IMessagingService;
import org.eclipse.dirigible.repository.ext.template.ITemplatingService;
import org.eclipse.dirigible.runtime.scripting.utils.DbUtils;
import org.eclipse.dirigible.runtime.scripting.utils.ExceptionUtils;
import org.eclipse.dirigible.runtime.scripting.utils.HttpUtils;
import org.eclipse.dirigible.runtime.scripting.utils.NamedDataSourcesUtils;
import org.eclipse.dirigible.runtime.scripting.utils.URLUtils;
import org.eclipse.dirigible.runtime.scripting.utils.XMLUtils;

public interface IInjectedAPIModifiers {

	public void setExecutionContext(Map<Object, Object> executionContext);

	public void setSystemOutput(PrintStream systemOutput);

	public void setDatasource(DataSource datasource);

	public void setRequest(HttpServletRequest request);

	public void setResponse(HttpServletResponse response);

	public void setSession(HttpSession session);

	public void setRequestInput(Object requestInput);

	public void setRepository(IRepository repository);

	public void setUserName(String userName);

	public void setInitialContext(InitialContext initialContext);

	public void setBinaryStorage(IStorage binaryStorage);

	public void setFileStorage(IStorage fileStorage);

	public void setConfigurationStorage(IStorage configurationStorage);

	public void setMailService(IMailService mailService);

	public void setExtensionService(IExtensionService extensionService);

	public void setIndexingService(IIndexingService<?> indexingService);

	public void setConnectivityService(IConnectivityService connectivityService);

	public void setDocumentService(IDocumentService documentService);

	public void setMessagingService(IMessagingService messagingService);

	public void setTemplatingService(ITemplatingService templatingService);

	public void setLifecycleService(ILifecycleService lifecycleService);

	public void setIOUtils(IOUtils ioUtils);

	public void setHttpUtils(HttpUtils httpUtils);

	public void setBase64Utils(Base64 base64Utils);

	public void setHexUtils(Hex hexUtils);

	public void setDigestUtils(DigestUtils digestUtils);

	public void setUrlUtils(URLUtils urlUtils);

	public void setUploadUtils(ServletFileUpload uploadUtils);

	public void setUuidUtils(UUID uuidUtils);

	public void setDatabaseUtils(DbUtils dbUtils);

	public void setXssUtils(StringEscapeUtils xssUtils);

	public void setXmlUtils(XMLUtils xmlUtils);

	public void setExceptionUtils(ExceptionUtils exceptionUtils);

	public void setNamedDataSourcesUtils(NamedDataSourcesUtils namedDataSourceUtils);

	public void setConsole(Console console);

	public void setExecutionService(IExecutionService executionService);

	public void setGenerationService(IGenerationService generationService);

}
