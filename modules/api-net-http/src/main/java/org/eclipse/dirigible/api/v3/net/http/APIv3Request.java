package org.eclipse.dirigible.api.v3.net.http;

import javax.servlet.http.HttpServletRequest;

public class APIv3Request {
	
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
