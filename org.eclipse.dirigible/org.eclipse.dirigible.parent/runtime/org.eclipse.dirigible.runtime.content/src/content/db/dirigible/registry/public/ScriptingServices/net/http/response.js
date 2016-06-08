/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ javax */
/* eslint-env node, dirigible */

exports.print = function(input) {
	if (input === undefined) {
		input = "";
	}
	return $.getResponse().getWriter().print(input + "");
};

exports.println = function(input) {
	if (input === undefined) {
		input = "";
	}
	return $.getResponse().getWriter().println(input + "");
};

exports.flush = function() {
	return $.getResponse().getWriter().flush();
};

exports.close = function() {
	return $.getResponse().getWriter().close();
};

exports.addCookie = function(cookie) {
    var httpCookie = new javax.servlet.http.Cookie(cookie.name, cookie.value);
    if (cookie.comment) {
       httpCookie.setComment(cookie.comment);
    }
    if (cookie.domain) {
       httpCookie.setDomain(cookie.domain);
    }
    if (cookie.maxAge) {
       httpCookie.setMaxAge(cookie.maxAge);
    }
    if (cookie.path) {
       httpCookie.setPath(cookie.path);
    }
    if (cookie.secure) {
       httpCookie.setSecure(cookie.secure);
    }
    if (cookie.version) {
       httpCookie.setVersion(cookie.version);
    }
    if (cookie.httpOnly) {
       httpCookie.setHttpOnly(cookie.httpOnly);
    }

    $.getResponse().addCookie(httpCookie);
};

exports.addHeader = function(name, value) {
    $.getResponse().addHeader(name, value);
};

exports.containsHeader = function(name) {
    return $.getResponse().addHeader(name);
};

exports.getCharacterEncoding = function() {
    return $.getResponse().getCharacterEncoding();
};

exports.getContentLength = function() {
    return $.getResponse().getContentLength();
};

exports.getContentType = function() {
    return $.getResponse().getContentType();
};

exports.sendError = function(code, message) {
    $.getResponse().sendError(code, message);
};

exports.sendRedirect = function(location) {
    $.getResponse().sendRedirect(location);
};

exports.setCharacterEncoding = function(characterEncoding) {
    $.getResponse().setCharacterEncoding(characterEncoding);
};

exports.setContentLength = function(length) {
    $.getResponse().setContentLength(length);
};

exports.setContentType = function(contentType) {
    $.getResponse().setContentType(contentType);
};

exports.setHeader = function(name, value) {
    $.getResponse().setHeader(name, value);
};

exports.setStatus = function(code) {
    $.getResponse().setStatus(code);
};

/**
 * Status code (202) indicating that a request was accepted for processing, but was not completed.
 */
exports.ACCEPTED = javax.servlet.http.HttpServletResponse.SC_ACCEPTED;

/**
 * Status code (502) indicating that the HTTP server received an invalid response from a server it consulted when acting as a proxy or gateway.
 */
exports.BAD_GATEWAY = javax.servlet.http.HttpServletResponse.SC_BAD_GATEWAY;

/**
 * Status code (400) indicating the request sent by the client was syntactically incorrect.
 */
exports.BAD_REQUEST = javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

/**
 * Status code (409) indicating that the request could not be completed due to a conflict with the current state of the resource.
 */
exports.CONFLICT = javax.servlet.http.HttpServletResponse.SC_CONFLICT;

/**
 * Status code (100) indicating the client can continue.
 */
exports.CONTINUE = javax.servlet.http.HttpServletResponse.SC_CONTINUE;

/**
 * Status code (201) indicating the request succeeded and created a new resource on the server.
 */
exports.CREATED = javax.servlet.http.HttpServletResponse.SC_CREATED;

/**
 * Status code (417) indicating that the server could not meet the expectation given in the Expect request header.
 */
exports.EXPECTATION_FAILED = javax.servlet.http.HttpServletResponse.SC_EXPECTATION_FAILED;

/**
 * Status code (403) indicating the server understood the request but refused to fulfill it.
 */
exports.FORBIDDEN = javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

/**
 * Status code (302) indicating that the resource reside temporarily under a different URI.
 */
exports.FOUND = javax.servlet.http.HttpServletResponse.SC_FOUND;

/**
 * Status code (504) indicating that the server did not receive a timely response from the upstream server while acting as a gateway or proxy.
 */
exports.GATEWAY_TIMEOUT = javax.servlet.http.HttpServletResponse.SC_GATEWAY_TIMEOUT;

/**
 * Status code (410) indicating that the resource is no longer available at the server and no forwarding address is known.
 */
exports.GONE = javax.servlet.http.HttpServletResponse.SC_GONE;

/**
 * Status code (505) indicating that the server does not support or refuses to support the HTTP protocol version that was used in the request message.
 */
exports.HTTP_VERSION_NOT_SUPPORTED = javax.servlet.http.HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED;

/**
 * Status code (500) indicating an error inside the HTTP server which prevented it from fulfilling the request.
 */
exports.INTERNAL_SERVER_ERROR = javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

/**
 * Status code (411) indicating that the request cannot be handled without a defined Content-Length.
 */
exports.LENGTH_REQUIRED = javax.servlet.http.HttpServletResponse.SC_LENGTH_REQUIRED;

/**
 * Status code (405) indicating that the method specified in the Request-Line is not allowed for the resource identified by the Request-URI.
 */
exports.METHOD_NOT_ALLOWED = javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

/**
 * Status code (301) indicating that the resource has permanently moved to a new location, and that future references should use a new URI with their requests.
 */
exports.MOVED_PERMANENTLY = javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY;

/**
 * Status code (302) indicating that the resource has temporarily moved to another location, but that future references should still use the original URI to access the resource.
 */
exports.MOVED_TEMPORARILY = javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY;

/**
 * Status code (300) indicating that the requested resource corresponds to any one of a set of representations, each with its own specific location.
 */
exports.MULTIPLE_CHOICES = javax.servlet.http.HttpServletResponse.SC_MULTIPLE_CHOICES;

/**
 * Status code (204) indicating that the request succeeded but that there was no new information to return.
 */
exports.NO_CONTENT = javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;

/**
 * Status code (203) indicating that the meta information presented by the client did not originate from the server.
 */
exports.NON_AUTHORITATIVE_INFORMATION = javax.servlet.http.HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION;

/**
 * Status code (406) indicating that the resource identified by the request is only capable of generating response entities which have content characteristics not acceptable according to the accept headers sent in the request.
 */
exports.NOT_ACCEPTABLE = javax.servlet.http.HttpServletResponse.SC_NOT_ACCEPTABLE;

/**
 * Status code (404) indicating that the requested resource is not available.
 */
exports.NOT_FOUND = javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Status code (501) indicating the HTTP server does not support the functionality needed to fulfill the request.
 */
exports.NOT_IMPLEMENTED = javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED;

/**
 * Status code (304) indicating that a conditional GET operation found that the resource was available and not modified.
 */
exports.NOT_MODIFIED = javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED;

/**
 * Status code (200) indicating the request succeeded normally.
 */
exports.OK = javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * Status code (206) indicating that the server has fulfilled the partial GET request for the resource.
 */
exports.PARTIAL_CONTENT = javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT;

/**
 * Status code (402) reserved for future use.
 */
exports.PAYMENT_REQUIRED = javax.servlet.http.HttpServletResponse.SC_PAYMENT_REQUIRED;

/**
 * Status code (412) indicating that the precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.
 */
exports.PRECONDITION_FAILED = javax.servlet.http.HttpServletResponse.SC_PAYMENT_REQUIRED;

/**
 * Status code (407) indicating that the client MUST first authenticate itself with the proxy.
 */
exports.PROXY_AUTHENTICATION_REQUIRED = javax.servlet.http.HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED;

/**
 * Status code (413) indicating that the server is refusing to process the request because the request entity is larger than the server is willing or able to process.
 */
exports.REQUEST_ENTITY_TOO_LARGE = javax.servlet.http.HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE;

/**
 * Status code (408) indicating that the client did not produce a request within the time that the server was prepared to wait.
 */
exports.REQUEST_TIMEOUT = javax.servlet.http.HttpServletResponse.SC_REQUEST_TIMEOUT;

/**
 * Status code (414) indicating that the server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.
 */
exports.REQUEST_URI_TOO_LONG = javax.servlet.http.HttpServletResponse.SC_REQUEST_URI_TOO_LONG;

/**
 * Status code (416) indicating that the server cannot serve the requested byte range.
 */
exports.REQUESTED_RANGE_NOT_SATISFIABLE = javax.servlet.http.HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE;

/**
 * Status code (205) indicating that the agent SHOULD reset the document view which caused the request to be sent.
 */
exports.RESET_CONTENT = javax.servlet.http.HttpServletResponse.SC_RESET_CONTENT;

/**
 * Status code (303) indicating that the response to the request can be found under a different URI.
 */
exports.SEE_OTHER = javax.servlet.http.HttpServletResponse.SC_SEE_OTHER;

/**
 * Status code (503) indicating that the HTTP server is temporarily overloaded, and unable to handle the request.
 */
exports.SERVICE_UNAVAILABLE = javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

/**
 * Status code (101) indicating the server is switching protocols according to Upgrade header.
 */
exports.SWITCHING_PROTOCOLS = javax.servlet.http.HttpServletResponse.SC_SWITCHING_PROTOCOLS;

/**
 *  Status code (307) indicating that the requested resource resides temporarily under a different URI.
 */
exports.TEMPORARY_REDIRECT = javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT;

/**
 *  Status code (401) indicating that the request requires HTTP authentication.
 */
exports.UNAUTHORIZED = javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 *  Status code (415) indicating that the server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method.
 */
exports.UNSUPPORTED_MEDIA_TYPE = javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;

/**
 *  Status code (305) indicating that the requested resource MUST be accessed through the proxy given by the Location field.
 */
exports.USE_PROXY = javax.servlet.http.HttpServletResponse.SC_USE_PROXY;
