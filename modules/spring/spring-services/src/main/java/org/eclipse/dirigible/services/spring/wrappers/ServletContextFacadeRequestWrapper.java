package org.eclipse.dirigible.services.spring.wrappers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ServletContextFacadeRequestWrapper extends HttpServletRequestWrapper {

    private String contextPath;
    private String servletPath;

    public ServletContextFacadeRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getContextPath() {
        if (contextPath != null && !contextPath.equals("")) {
            return contextPath;
        }
        return super.getContextPath();
    }

    @Override
    public String getServletPath() {
        if (servletPath != null && !servletPath.equals("")) {
            return servletPath;
        }
        return super.getServletPath();
    }

    @Override
    public String getRequestURI() {
        String requestURI = super.getRequestURI();
        if (requestURI.equals(contextPath)) {
            return requestURI + "/";
        }
        return requestURI;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

}
