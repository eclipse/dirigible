package org.eclipse.dirigible.runtime.listener.mail;

import javax.mail.Message;

import org.eclipse.dirigible.runtime.listener.Listener;

public interface IMailHandler {

	public Listener getListener();

	public String getUsername();

	public String getPassword();

	public String getHost();

	public String getPort();

	public String getTimeout();

	public String getDebug();

	public String getFolder();

	public void handleMail(Message message);

}
