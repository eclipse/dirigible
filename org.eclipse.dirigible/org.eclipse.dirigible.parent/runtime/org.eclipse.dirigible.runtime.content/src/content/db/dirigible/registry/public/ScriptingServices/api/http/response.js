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

exports.flush = function(input) {
	return $.getResponse().getWriter().flush();
};

exports.close = function(input) {
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
}

exports.addHeader = function(name, value) {
    $.getResponse().addHeader(name, value);
}

exports.containsHeader = function(name) {
    return $.getResponse().addHeader(name);
}

exports.getCharacterEncoding = function() {
    return $.getResponse().getCharacterEncoding();
}

exports.getContentLength = function() {
    return $.getResponse().getContentLength();
}

exports.getContentType = function() {
    return $.getResponse().getContentType();
}

exports.sendError = function(code, message) {
    $.getResponse().sendError(code, message);
}

exports.sendRedirect = function(location) {
    $.getResponse().sendRedirect(location);
}

exports.setCharacterEncoding = function(characterEncoding) {
    $.getResponse().setCharacterEncoding(characterEncoding);
}

exports.setContentLength = function(length) {
    $.getResponse().setContentLength(length);
}

exports.setContentType = function(contentType) {
    $.getResponse().setContentType(contentType);
}

exports.setHeader = function(name, value) {
    $.getResponse().setHeader(name, value);
}

exports.setStatus = function(code) {
    $.getResponse().setStatus(code);
}
