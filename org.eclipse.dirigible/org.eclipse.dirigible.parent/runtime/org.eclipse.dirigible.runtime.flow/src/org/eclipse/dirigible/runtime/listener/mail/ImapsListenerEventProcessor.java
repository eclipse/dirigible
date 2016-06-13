package org.eclipse.dirigible.runtime.listener.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.listener.IListenerEventProcessor;
import org.eclipse.dirigible.runtime.listener.Listener;
import org.eclipse.dirigible.runtime.listener.ListenerProcessor;

import com.google.gson.Gson;

public class ImapsListenerEventProcessor implements IListenerEventProcessor, IMailHandler {

	private static final String PARAM_USER = "username";
	private static final String PARAM_PASSWORD = "password";
	private static final String PARAM_HOST = "host";
	private static final String PARAM_PORT = "port";
	private static final String PARAM_TIMEOUT = "timeout";
	private static final String PARAM_DEBUG = "debug";
	private static final String PARAM_FOLDER = "folder";

	private static final Logger logger = Logger.getLogger(ImapsListenerEventProcessor.class);

	private static final Object PARAM_MAIL = "message";

	private Listener listener;
	private String username;
	private String password;
	private String host;
	private String port;
	private String timeout;
	private String debug;
	private String folder;

	private ImapsClient imapsClient;

	private Gson gson = new Gson();

	@Override
	public Listener getListener() {
		return listener;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public String getPort() {
		return port;
	}

	@Override
	public String getTimeout() {
		return timeout;
	}

	@Override
	public String getDebug() {
		return debug;
	}

	@Override
	public String getFolder() {
		return folder;
	}

	@Override
	public void handleMail(Message message) {
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		String mail = null;
		try {
			mail = parseMail(message);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (MessagingException e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("Mail: " + mail);
		executionContext.put(PARAM_MAIL, mail);
		Object result = ListenerProcessor.executeByEngineType(getListener().getModule(), executionContext, getListener());
		if (result != null) {
			logger.info(result.toString());
		}
	}

	private String parseMail(Message message) throws IOException, MessagingException {
		MailMessage mail = new MailMessage();
		mail.setSubject(message.getSubject());
		mail.setContent(new String(IOUtils.toByteArray(message.getInputStream())));
		List<String> fromList = new ArrayList<String>();
		for (Address address : message.getFrom()) {
			fromList.add(address.toString());
		}
		List<String> toList = new ArrayList<String>();
		for (Address address : message.getAllRecipients()) {
			toList.add(address.toString());
		}
		mail.setTo(toList.toArray(new String[] {}));
		mail.setSent(message.getSentDate());
		mail.setReceived(message.getReceivedDate());
		return gson.toJson(mail);
	}

	@Override
	public void start(Listener listener) {
		this.listener = listener;
		this.username = listener.getParams().get(PARAM_USER);
		this.password = listener.getParams().get(PARAM_PASSWORD);
		this.host = listener.getParams().get(PARAM_HOST);
		this.port = listener.getParams().get(PARAM_PORT);
		this.timeout = listener.getParams().get(PARAM_TIMEOUT);
		this.debug = listener.getParams().get(PARAM_DEBUG);
		this.folder = listener.getParams().get(PARAM_FOLDER);

		// {
		// "name":"listenerGmail",
		// "description":"listenerGmail description",
		// "trigger":"imaps",
		// "type":"javascript",
		// "module":"/flowstest/listenerXXX_callback.js",
		// "parameters":{
		// "username":"my@gmail.com",
		// "password":"***",
		// "host":"imap.gmail.com",
		// "port":"993",
		// "timeout":"10000",
		// "folder":"INBOX",
		// "debug":"true"
		// }
		// }

		this.imapsClient = new ImapsClient();
		this.imapsClient.connect(this);
	}

	@Override
	public void stop() {
		this.imapsClient.disconnect();
	}

}
