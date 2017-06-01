package org.eclipse.dirigible.commons.api.scripting;

import javax.servlet.http.HttpServletRequest;

public class ContextFacade {
	
    private static final ThreadLocal<HttpServletRequest> REQUEST = new ThreadLocal<HttpServletRequest>();

    public static final void set(HttpServletRequest request) {
    	REQUEST.set(request);
    }
    
    public static final boolean isValid() {
    	return REQUEST.get() != null;
    }
    
    public static final String getMethod() {
    	HttpServletRequest request = REQUEST.get();
    	if (request != null) {
    		return request.getMethod();
    	}
    	return null;
    }
}
