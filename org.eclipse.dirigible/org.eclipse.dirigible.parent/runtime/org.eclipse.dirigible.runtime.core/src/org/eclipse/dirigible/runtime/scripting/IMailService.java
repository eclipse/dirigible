package org.eclipse.dirigible.runtime.scripting;

public interface IMailService {

	public String sendMail(String from, String to, String subject, String content, String subType);

}
