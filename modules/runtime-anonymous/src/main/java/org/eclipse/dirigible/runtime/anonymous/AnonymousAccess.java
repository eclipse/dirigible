package org.eclipse.dirigible.runtime.anonymous;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.context.ContextException;

public class AnonymousAccess {
	
	public void setName(String name) throws ContextException {
		UserFacade.setName(name);
	}

}
