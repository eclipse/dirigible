import * as streams from "sdk/io/streams"
const HttpRequestFacade = Java.type("org.eclipse.dirigible.components.api.http.HttpRequestFacade");

export function isValid() {
    return HttpRequestFacade.isValid();
};

export function getMethod() {
    return HttpRequestFacade.getMethod();
};

export function getRemoteUser() {
    return HttpRequestFacade.getRemoteUser();
};

export function getPathInfo() {
    return HttpRequestFacade.getPathInfo();
};

export function getPathTranslated() {
    return HttpRequestFacade.getPathTranslated();
};

export function getHeader(name) {
    return HttpRequestFacade.getHeader(name);
};

export function isUserInRole(role) {
    return HttpRequestFacade.isUserInRole(role);
};

export function getAttribute(name) {
    return HttpRequestFacade.getAttribute(name);
};

export function getAuthType() {
    return HttpRequestFacade.getAuthType();
};

export function getCookies() {
    let cookiesJson = HttpRequestFacade.getCookies();
    return JSON.parse(cookiesJson);
};

export function getAttributeNames() {
    let attrNamesJson = HttpRequestFacade.getAttributeNames();
    return JSON.parse(attrNamesJson);
};

export function getCharacterEncoding() {
    return HttpRequestFacade.getCharacterEncoding();
};

export function getContentLength() {
    return HttpRequestFacade.getContentLength();
};

export function getHeaders(name) {
    let headersJson = HttpRequestFacade.getHeaders(name);
    return JSON.parse(headersJson);
};

export function getContentType() {
    return HttpRequestFacade.getContentType();
};

export function getBytes() {
    let bytesJson = HttpRequestFacade.getBytes();
    return JSON.parse(bytesJson);
};

let textData = null;
export function getText() {
    let textData = null;
    if (textData === null) {
        textData = HttpRequestFacade.getText();
    }
    return textData;
};

export function json() {
    return getJSON();
}

export function getJSON() {
    try {
        let text = getText();
        return JSON.parse(text);
    } catch (e) {
        return null;
    }
};

export function getParameter(name) {
    return HttpRequestFacade.getParameter(name);
};

export function getParameters() {
    let paramsJson;
    paramsJson = HttpRequestFacade.getParameters();
    return JSON.parse(paramsJson);
};

export function getResourcePath() {
    return HttpRequestFacade.getResourcePath();
};

export function getHeaderNames() {
    let headerNamesJson = HttpRequestFacade.getHeaderNames();
    return JSON.parse(headerNamesJson);
};

export function getParameterNames() {
    let paramNamesJson = HttpRequestFacade.getParameterNames();
    return JSON.parse(paramNamesJson);
};

export function getParameterValues(name) {
    let paramValuesJson = HttpRequestFacade.getParameterValues(name);
    return JSON.parse(paramValuesJson);
};

export function getProtocol() {
    return HttpRequestFacade.getProtocol();
};

export function getScheme() {
    return HttpRequestFacade.getScheme();
};

export function getContextPath() {
    return HttpRequestFacade.getContextPath();
};

export function getServerName() {
    return HttpRequestFacade.getServerName();
};

export function getServerPort() {
    return HttpRequestFacade.getServerPort();
};

export function getQueryString() {
    return HttpRequestFacade.getQueryString();
};

/**
 * Returns the query string name value pairs as JS object map. When multiple query parameters with the same name are specified,
 * it will collect theirs values in an array in the order of declaration under that name in the map.
 */
export function getQueryParametersMap() {
    let queryString = getQueryString();
    if (!queryString)
        return {};

    queryString = decodeURI(queryString);
    let queryStringSegments = queryString.split('&');

    let queryMap = {};
    queryStringSegments.forEach(function (seg) {
        seg = seg.replace('amp;', '');
        const kv = seg.split('=');
        const key = kv[0].trim();
        const value = kv[1] === undefined ? true : kv[1].trim();
        if (queryMap[key] !== undefined) {
            if (!Array.isArray(queryMap[key]))
                queryMap[key] = [queryMap[key]];
            else
                queryMap[key].push(value);
        } else {
            queryMap[key] = value;
        }
    }.bind(this));
    return queryMap;
};

export function getRemoteAddress() {
    return HttpRequestFacade.getRemoteAddress();
};

export function getRemoteHost() {
    return HttpRequestFacade.getRemoteHost();
};

export function setAttribute(name, value) {
    HttpRequestFacade.setAttribute(name, value);
};

export function removeAttribute(name) {
    HttpRequestFacade.removeAttribute(name);
};

export function getLocale() {
    let localeJson = HttpRequestFacade.getLocale();
    return JSON.parse(localeJson);
};

export function getRequestURI() {
    return HttpRequestFacade.getRequestURI();
};

export function isSecure() {
    return HttpRequestFacade.isSecure();
};

export function getRequestURL() {
    return HttpRequestFacade.getRequestURL();
};

export function getServicePath() {
    return HttpRequestFacade.getServicePath();
};

export function getRemotePort() {
    return HttpRequestFacade.getRemotePort();
};

export function getLocalName() {
    return HttpRequestFacade.getLocalName();
};

export function getLocalAddress() {
    return HttpRequestFacade.getLocalAddress();
};

export function getLocalPort() {
    return HttpRequestFacade.getLocalPort();
};

export function getInputStream() {
    return streams.createInputStream(HttpRequestFacade.getInputStream());
};
