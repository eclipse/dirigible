package org.eclipse.dirigible.runtime.operations.processor;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;
import org.eclipse.dirigible.core.messaging.service.MessagingCoreService;

public class ListenersProcessor {
	
	@Inject
	private MessagingCoreService messagingCoreService;
	
	public String list() throws MessagingException {
		
		List<ListenerDefinition> listeners = messagingCoreService.getListeners();
		
        return GsonHelper.GSON.toJson(listeners);
	}


}
