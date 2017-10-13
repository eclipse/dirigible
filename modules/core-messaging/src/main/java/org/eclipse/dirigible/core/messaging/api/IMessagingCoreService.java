package org.eclipse.dirigible.core.messaging.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;

public interface IMessagingCoreService extends ICoreService {

	public static final String FILE_EXTENSION_LISTENER = ".listener";

	// Listener

	public ListenerDefinition createListener(String location, String name, ListenerType type, String module, String description)
			throws MessagingException;

	public ListenerDefinition getListener(String location) throws MessagingException;

	public ListenerDefinition getListenerByName(String name) throws MessagingException;

	public boolean existsListener(String location) throws MessagingException;

	public void removeListener(String location) throws MessagingException;

	public void updateListener(String location, String name, ListenerType type, String module, String description) throws MessagingException;

	public List<ListenerDefinition> getListeners() throws MessagingException;

	public ListenerDefinition parseListener(String json);

	public ListenerDefinition parseListener(byte[] json);

	public String serializeListener(ListenerDefinition listenerDefinition);

}
