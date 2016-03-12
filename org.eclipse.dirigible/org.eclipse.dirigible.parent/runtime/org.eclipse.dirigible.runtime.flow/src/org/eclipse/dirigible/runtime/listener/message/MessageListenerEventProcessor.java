package org.eclipse.dirigible.runtime.listener.message;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.messaging.EMessagingException;
import org.eclipse.dirigible.repository.ext.messaging.MessageDefinition;
import org.eclipse.dirigible.repository.ext.messaging.MessageHub;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.listener.IListenerEventProcessor;
import org.eclipse.dirigible.runtime.listener.Listener;
import org.eclipse.dirigible.runtime.listener.ListenerProcessor;

public class MessageListenerEventProcessor implements IListenerEventProcessor {

	private static final String PARAM_CLIENT = "client";
	private static final String PARAM_TOPIC = "topic";
	private static final String PARAM_MESSAGE = "message";

	private static final Logger logger = Logger.getLogger(MessageListenerEventProcessor.class);

	private String client;
	private String topic;
	private Listener listener;

	private MessageHub messageHub;

	public MessageListenerEventProcessor() {
		this.messageHub = new MessageHub(DataSourceFacade.getInstance().getDataSource(null), null);
	}

	@Override
	public void start(Listener listener) {
		this.listener = listener;
		this.client = listener.getParams().get(PARAM_CLIENT);
		this.topic = listener.getParams().get(PARAM_TOPIC);

		try {
			messageHub.subscribe(client, topic);
			MessageListenerManager.getInstance().registerProcessor(this);
		} catch (EMessagingException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void stop() {
		try {
			messageHub.unsubscribe(client, topic);
			MessageListenerManager.getInstance().unregisterProcessor(this);
		} catch (EMessagingException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void processMessages() throws EMessagingException {
		for (MessageDefinition messageDefinition : messageHub.receive(client)) {
			Map<Object, Object> executionContext = new HashMap<Object, Object>();
			executionContext.put(PARAM_MESSAGE, messageDefinition);
			ListenerProcessor.executeByEngineType(listener.getModule(), executionContext, listener);
		}
	}
}
