/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * HTTP API Response
 *
 */

import * as streams from "sdk/io/streams"
const HttpResponseFacade = Java.type("org.eclipse.dirigible.components.api.http.HttpResponseFacade");
const OutputStreamWriter = Java.type("java.io.OutputStreamWriter");
const StandardCharsets = Java.type("java.nio.charset.StandardCharsets");

export function isValid() {
    return HttpResponseFacade.isValid();
};

export function json(obj) {
    addHeader("Content-Type", "application/json")
    const objJson = JSON.stringify(obj);
    print(objJson);
}

export function print(text) {
    text = (text && text.toString()) || "";
    const out = getOutputStream().native;
    const writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
    writer.write(text);
    writer.flush();
};

export function println(text) {
    text = (text && text.toString()) || "";
    const out = getOutputStream().native;
    const writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
    writer.write(text);
    writer.write("\n");
    writer.flush();
};

export function write(bytes) {
    if (!bytes) bytes += "";
    HttpResponseFacade.write(bytes);
};

export function isCommitted() {
    return HttpResponseFacade.isCommitted();
};

export function setContentType(contentType) {
    HttpResponseFacade.setContentType(contentType);
};

export function flush() {
    HttpResponseFacade.flush();
};

export function close() {
    HttpResponseFacade.close();
};

export function addCookie(cookie) {
    var cookieJson = JSON.stringify(cookie);
    HttpResponseFacade.addCookie(cookieJson);
};

export function containsHeader(name) {
    return HttpResponseFacade.containsHeader(name);
};

export function encodeURL(url) {
    return HttpResponseFacade.encodeURL(url);
};

export function getCharacterEncoding() {
    return HttpResponseFacade.getCharacterEncoding();
};

export function encodeRedirectURL(url) {
    return HttpResponseFacade.encodeRedirectURL(url);
};

export function getContentType() {
    return HttpResponseFacade.getContentType();
};

export function sendError(status, message) {
    if (message) {
        HttpResponseFacade.sendError(status, message);
    } else {
        HttpResponseFacade.sendError(status);
    }
};

export function setCharacterEncoding(charset) {
    HttpResponseFacade.setCharacterEncoding(charset);
};

export function sendRedirect(location) {
    HttpResponseFacade.sendRedirect(location);
};

export function setContentLength(length) {
    HttpResponseFacade.setContentLength(length);
};

export function setHeader(name, value) {
    HttpResponseFacade.setHeader(name, value);
};

export function addHeader(name, value) {
    HttpResponseFacade.addHeader(name, value);
};

export function setStatus(status) {
    HttpResponseFacade.setStatus(status);
};

export function reset() {
    HttpResponseFacade.reset();
};

export function getHeader(name) {
    return HttpResponseFacade.getHeader(name);
};

export function setLocale(language, country, variant) {
    return HttpResponseFacade.setLocale(language, country, variant);
};

export function getHeaders(name) {
    const headersJson = HttpResponseFacade.getHeaders(name);
    return JSON.parse(headersJson);
};

export function getHeaderNames() {
    const headerNamesJson = HttpResponseFacade.getHeaderNames();
    return JSON.parse(headerNamesJson);
};

export function getLocale() {
    return HttpResponseFacade.getLocale();
};

export function getOutputStream() {
    const native = HttpResponseFacade.getOutputStream();
    return new streams.OutputStream(native);
};

/**
 * Status code (202) indicating that a request was accepted for processing, but was not completed.
 */
export const ACCEPTED = 202;

/**
 * Status code (502) indicating that the HTTP server received an invalid response from a server it consulted when acting as a proxy or gateway.
 */
export const BAD_GATEWAY = 502;

/**
 * Status code (400) indicating the request sent by the client was syntactically incorrect.
 */
export const BAD_REQUEST = 400;

/**
 * Status code (409) indicating that the request could not be completed due to a conflict with the current state of the resource.
 */
export const CONFLICT = 409;

/**
 * Status code (100) indicating the client can continue.
 */
export const CONTINUE = 100;

/**
 * Status code (201) indicating the request succeeded and created a new resource on the server.
 */
export const CREATED = 201;

/**
 * Status code (417) indicating that the server could not meet the expectation given in the Expect request header.
 */
export const EXPECTATION_FAILED = 417;

/**
 * Status code (403) indicating the server understood the request but refused to fulfill it.
 */
export const FORBIDDEN = 403;

/**
 * Status code (302) indicating that the resource reside temporarily under a different URI.
 */
export const FOUND = 302;

/**
 * Status code (504) indicating that the server did not receive a timely response from the upstream server while acting as a gateway or proxy.
 */
export const GATEWAY_TIMEOUT = 504;

/**
 * Status code (410) indicating that the resource is no longer available at the server and no forwarding address is known.
 */
export const GONE = 410;

/**
 * Status code (505) indicating that the server does not support or refuses to support the HTTP protocol version that was used in the request message.
 */
export const HTTP_VERSION_NOT_SUPPORTED = 505;

/**
 * Status code (500) indicating an error inside the HTTP server which prevented it from fulfilling the request.
 */
export const INTERNAL_SERVER_ERROR = 500;

/**
 * Status code (411) indicating that the request cannot be handled without a defined Content-Length.
 */
export const LENGTH_REQUIRED = 411;

/**
 * Status code (405) indicating that the method specified in the Request-Line is not allowed for the resource identified by the Request-URI.
 */
export const METHOD_NOT_ALLOWED = 405;

/**
 * Status code (301) indicating that the resource has permanently moved to a new location, and that future references should use a new URI with their requests.
 */
export const MOVED_PERMANENTLY = 301;

/**
 * Status code (302) indicating that the resource has temporarily moved to another location, but that future references should still use the original URI to access the resource.
 */
export const MOVED_TEMPORARILY = 302;

/**
 * Status code (300) indicating that the requested resource corresponds to any one of a set of representations, each with its own specific location.
 */
export const MULTIPLE_CHOICES = 300;

/**
 * Status code (204) indicating that the request succeeded but that there was no new information to return.
 */
export const NO_CONTENT = 204;

/**
 * Status code (203) indicating that the meta information presented by the client did not originate from the server.
 */
export const NON_AUTHORITATIVE_INFORMATION = 203;

/**
 * Status code (406) indicating that the resource identified by the request is only capable of generating response entities which have content characteristics not acceptable according to the accept headers sent in the request.
 */
export const NOT_ACCEPTABLE = 406;

/**
 * Status code (404) indicating that the requested resource is not available.
 */
export const NOT_FOUND = 404;

/**
 * Status code (501) indicating the HTTP server does not support the functionality needed to fulfill the request.
 */
export const NOT_IMPLEMENTED = 501;

/**
 * Status code (304) indicating that a conditional GET operation found that the resource was available and not modified.
 */
export const NOT_MODIFIED = 304;

/**
 * Status code (200) indicating the request succeeded normally.
 */
export const OK = 200;

/**
 * Status code (206) indicating that the server has fulfilled the partial GET request for the resource.
 */
export const PARTIAL_CONTENT = 206;

/**
 * Status code (402) reserved for future use.
 */
export const PAYMENT_REQUIRED = 402;

/**
 * Status code (412) indicating that the precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.
 */
export const PRECONDITION_FAILED = 412;

/**
 * Status code (407) indicating that the client MUST first authenticate itself with the proxy.
 */
export const PROXY_AUTHENTICATION_REQUIRED = 407;

/**
 * Status code (413) indicating that the server is refusing to process the request because the request entity is larger than the server is willing or able to process.
 */
export const REQUEST_ENTITY_TOO_LARGE = 413;

/**
 * Status code (408) indicating that the client did not produce a request within the time that the server was prepared to wait.
 */
export const REQUEST_TIMEOUT = 408;

/**
 * Status code (414) indicating that the server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.
 */
export const REQUEST_URI_TOO_LONG = 414;

/**
 * Status code (416) indicating that the server cannot serve the requested byte range.
 */
export const REQUESTED_RANGE_NOT_SATISFIABLE = 416;

/**
 * Status code (205) indicating that the agent SHOULD reset the document view which caused the request to be sent.
 */
export const RESET_CONTENT = 205;

/**
 * Status code (303) indicating that the response to the request can be found under a different URI.
 */
export const SEE_OTHER = 303;

/**
 * Status code (503) indicating that the HTTP server is temporarily overloaded, and unable to handle the request.
 */
export const SERVICE_UNAVAILABLE = 503;

/**
 * Status code (101) indicating the server is switching protocols according to Upgrade header.
 */
export const SWITCHING_PROTOCOLS = 101;

/**
 *  Status code (307) indicating that the requested resource resides temporarily under a different URI.
 */
export const TEMPORARY_REDIRECT = 307;

/**
 *  Status code (401) indicating that the request requires HTTP authentication.
 */
export const UNAUTHORIZED = 401;

/**
 *  Status code (415) indicating that the server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method.
 */
export const UNSUPPORTED_MEDIA_TYPE = 415;

/**
 *  Status code (305) indicating that the requested resource MUST be accessed through the proxy given by the Location field.
 */
export const USE_PROXY = 305;

/**
 * Mapping between HTTP response codes (string) and reason-pharses as defiend in rfc7231 section 6.1 (https://tools.ietf.org/html/rfc7231#section-6.1).
 * (See HttpCodesReasons.getReason for number based retrieval of reason-phrase for code)
 *
 */
export const HttpCodesReasons = {
    "100": "Continue",
    "101": "Switching Protocols",
    "200": "OK",
    "201": "Created",
    "202": "Accepted",
    "203": "Non-Authoritative Information",
    "204": "No Content",
    "205": "Reset Content",
    "206": "Partial Content",
    "300": "Multiple Choices",
    "301": "Moved Permanently",
    "302": "Found",
    "303": "See Other",
    "304": "Not Modified",
    "305": "Use Proxy",
    "307": "Temporary Redirect",
    "400": "Bad Request",
    "401": "Unauthorized",
    "402": "Payment Required",
    "403": "Forbidden",
    "404": "Not Found",
    "405": "Method Not Allowed",
    "406": "Not Acceptable",
    "407": "Proxy Authentication Required",
    "408": "Request Timeout",
    "409": "Conflict",
    "410": "Gone",
    "411": "Length Required",
    "412": "Precondition Failed",
    "413": "Payload Too Large",
    "414": "URI Too Large",
    "415": "Unsupported Media Type",
    "416": "Range Not Satisfiable",
    "417": "Expectation Failed",
    "426": "Upgrade Required",
    "500": "Internal Server Error",
    "501": "Not Implemented",
    "502": "Bad Gateway",
    "503": "Service Unavailable",
    "504": "Gateway Timmeout",
    "505": "HTTP Version Not Supported",

    /**
    * Utility method that accepts HTTP code as argument (string or number) and returns its corresponding reason-phrase as defined in rfc7231 section 6.1 (https://tools.ietf.org/html/rfc7231#section-6.1)
    */
    getReason: (code) => {
        if (isNaN(code))
            throw Error('Illegal argument for code[' + code + ']. Valid HTTP codes are integer numbers in the range [100-505].')
        return HttpCodesReasons[String(code)];
    }
};