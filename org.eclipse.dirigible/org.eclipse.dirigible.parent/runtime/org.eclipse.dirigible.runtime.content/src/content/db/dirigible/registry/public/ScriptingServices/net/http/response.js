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

var streams = require("io/streams");

const RESPONSE_STATE_NAME = "state";
const RESPONSE_STATE_WRITER = "1";
const RESPONSE_STATE_STREAM = "2";

exports.print = function(input) {
	if (input === undefined) {
		input = "";
	}
	checkWriterState();
	return $.getResponse().getWriter().print(input + "");
};

exports.println = function(input) {
	if (input === undefined) {
		input = "";
	}
	checkWriterState();
	return $.getResponse().getWriter().println(input + "");
};

exports.getOutputStream = function() {
	checkStreamState();
	var internalOutputStream = $.getResponse().getOutputStream();
	return new streams.OutputStream(internalOutputStream);
};

exports.writeStream = function(inputStream) {
	checkStreamState();
	var internalOutputStream = $.getResponse().getOutputStream();
	var outputStream = new streams.OutputStream(internalOutputStream);
	streams.copy(inputStream, outputStream);	
};

exports.writeOutput = function(bytes) {
	checkStreamState();
	var internalOutputStream = $.getResponse().getOutputStream();
	var outputStream = new streams.OutputStream(internalOutputStream);
	var inputStream = streams.createByteArrayInputStream(bytes);
	streams.copy(inputStream, outputStream);	
};

exports.flush = function() {
	try {
		checkWriterState();
	} catch(e) {
		// silently skip this in case of OutputStream state
		return;
	}
	return $.getResponse().getWriter().flush();
};

exports.close = function() {
	try {
		checkWriterState();
	} catch(e) {
		// silently skip this in case of OutputStream state
		return;
	}
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
exports.ACCEPTED = 202;

/**
 * Status code (502) indicating that the HTTP server received an invalid response from a server it consulted when acting as a proxy or gateway.
 */
exports.BAD_GATEWAY = 502;

/**
 * Status code (400) indicating the request sent by the client was syntactically incorrect.
 */
exports.BAD_REQUEST = 400;

/**
 * Status code (409) indicating that the request could not be completed due to a conflict with the current state of the resource.
 */
exports.CONFLICT = 409;

/**
 * Status code (100) indicating the client can continue.
 */
exports.CONTINUE = 100;

/**
 * Status code (201) indicating the request succeeded and created a new resource on the server.
 */
exports.CREATED = 201;

/**
 * Status code (417) indicating that the server could not meet the expectation given in the Expect request header.
 */
exports.EXPECTATION_FAILED = 417;

/**
 * Status code (403) indicating the server understood the request but refused to fulfill it.
 */
exports.FORBIDDEN = 403;

/**
 * Status code (302) indicating that the resource reside temporarily under a different URI.
 */
exports.FOUND = 302;

/**
 * Status code (504) indicating that the server did not receive a timely response from the upstream server while acting as a gateway or proxy.
 */
exports.GATEWAY_TIMEOUT = 504;

/**
 * Status code (410) indicating that the resource is no longer available at the server and no forwarding address is known.
 */
exports.GONE = 410;

/**
 * Status code (505) indicating that the server does not support or refuses to support the HTTP protocol version that was used in the request message.
 */
exports.HTTP_VERSION_NOT_SUPPORTED = 505;

/**
 * Status code (500) indicating an error inside the HTTP server which prevented it from fulfilling the request.
 */
exports.INTERNAL_SERVER_ERROR = 500;

/**
 * Status code (411) indicating that the request cannot be handled without a defined Content-Length.
 */
exports.LENGTH_REQUIRED = 411;

/**
 * Status code (405) indicating that the method specified in the Request-Line is not allowed for the resource identified by the Request-URI.
 */
exports.METHOD_NOT_ALLOWED = 405;

/**
 * Status code (301) indicating that the resource has permanently moved to a new location, and that future references should use a new URI with their requests.
 */
exports.MOVED_PERMANENTLY = 301;

/**
 * Status code (302) indicating that the resource has temporarily moved to another location, but that future references should still use the original URI to access the resource.
 */
exports.MOVED_TEMPORARILY = 302;

/**
 * Status code (300) indicating that the requested resource corresponds to any one of a set of representations, each with its own specific location.
 */
exports.MULTIPLE_CHOICES = 300;

/**
 * Status code (204) indicating that the request succeeded but that there was no new information to return.
 */
exports.NO_CONTENT = 204;

/**
 * Status code (203) indicating that the meta information presented by the client did not originate from the server.
 */
exports.NON_AUTHORITATIVE_INFORMATION = 203;

/**
 * Status code (406) indicating that the resource identified by the request is only capable of generating response entities which have content characteristics not acceptable according to the accept headers sent in the request.
 */
exports.NOT_ACCEPTABLE = 406;

/**
 * Status code (404) indicating that the requested resource is not available.
 */
exports.NOT_FOUND = 404;

/**
 * Status code (501) indicating the HTTP server does not support the functionality needed to fulfill the request.
 */
exports.NOT_IMPLEMENTED = 501;

/**
 * Status code (304) indicating that a conditional GET operation found that the resource was available and not modified.
 */
exports.NOT_MODIFIED = 304;

/**
 * Status code (200) indicating the request succeeded normally.
 */
exports.OK = 200;

/**
 * Status code (206) indicating that the server has fulfilled the partial GET request for the resource.
 */
exports.PARTIAL_CONTENT = 206;

/**
 * Status code (402) reserved for future use.
 */
exports.PAYMENT_REQUIRED = 402;

/**
 * Status code (412) indicating that the precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.
 */
exports.PRECONDITION_FAILED = 412;

/**
 * Status code (407) indicating that the client MUST first authenticate itself with the proxy.
 */
exports.PROXY_AUTHENTICATION_REQUIRED = 407;

/**
 * Status code (413) indicating that the server is refusing to process the request because the request entity is larger than the server is willing or able to process.
 */
exports.REQUEST_ENTITY_TOO_LARGE = 413;

/**
 * Status code (408) indicating that the client did not produce a request within the time that the server was prepared to wait.
 */
exports.REQUEST_TIMEOUT = 408;

/**
 * Status code (414) indicating that the server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.
 */
exports.REQUEST_URI_TOO_LONG = 414;

/**
 * Status code (416) indicating that the server cannot serve the requested byte range.
 */
exports.REQUESTED_RANGE_NOT_SATISFIABLE = 416;

/**
 * Status code (205) indicating that the agent SHOULD reset the document view which caused the request to be sent.
 */
exports.RESET_CONTENT = 205;

/**
 * Status code (303) indicating that the response to the request can be found under a different URI.
 */
exports.SEE_OTHER = 303;

/**
 * Status code (503) indicating that the HTTP server is temporarily overloaded, and unable to handle the request.
 */
exports.SERVICE_UNAVAILABLE = 503;

/**
 * Status code (101) indicating the server is switching protocols according to Upgrade header.
 */
exports.SWITCHING_PROTOCOLS = 101;

/**
 *  Status code (307) indicating that the requested resource resides temporarily under a different URI.
 */
exports.TEMPORARY_REDIRECT = 307;

/**
 *  Status code (401) indicating that the request requires HTTP authentication.
 */
exports.UNAUTHORIZED = 401;

/**
 *  Status code (415) indicating that the server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method.
 */
exports.UNSUPPORTED_MEDIA_TYPE = 415;

/**
 *  Status code (305) indicating that the requested resource MUST be accessed through the proxy given by the Location field.
 */
exports.USE_PROXY = 305;


// Internal Functions

function checkWriterState() {
	if ($.getResponse().getHeader(RESPONSE_STATE_NAME) === null) {
		$.getResponse().setHeader(RESPONSE_STATE_NAME, RESPONSE_STATE_WRITER);
	} else {
		if ($.getResponse().getHeader(RESPONSE_STATE_NAME) === RESPONSE_STATE_STREAM) {
			throw new Error("The Output of this Response is already used as a Stream and cannot be used as a String");
		}
	}
}

function checkStreamState() {
	if ($.getResponse().getHeader(RESPONSE_STATE_NAME) === null) {
		$.getResponse().setHeader(RESPONSE_STATE_NAME, RESPONSE_STATE_STREAM);
	} else {
		if ($.getResponse().getHeader(RESPONSE_STATE_NAME) === RESPONSE_STATE_WRITER) {
			throw new Error("The Output of this Response is already used as a String and cannot be used as a Stream");
		}
	}
}