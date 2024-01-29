import * as streams from "@dirigible/io/streams"
const HttpRequestFacade = Java.type("org.eclipse.dirigible.components.api.http.HttpRequestFacade");

export class Request {
    public static isValid(): boolean {
        return HttpRequestFacade.isValid();
    };

    public static getMethod(): string {
        return HttpRequestFacade.getMethod();
    };

    public static getRemoteUser(): string {
        return HttpRequestFacade.getRemoteUser();
    };

    public static getPathInfo(): string {
        return HttpRequestFacade.getPathInfo();
    };

    public static getPathTranslated(): string {
        return HttpRequestFacade.getPathTranslated();
    };

    public static getHeader(name: string): string {
        return HttpRequestFacade.getHeader(name);
    };

    public static isUserInRole(role: string): boolean {
        return HttpRequestFacade.isUserInRole(role);
    };

    public static getAttribute(name: string): string {
        return HttpRequestFacade.getAttribute(name);
    };

    public static getAuthType(): string {
        return HttpRequestFacade.getAuthType();
    };

    public static getCookies(): Object | Object[] {
        let cookiesJson = HttpRequestFacade.getCookies();
        return JSON.parse(cookiesJson);
    };

    public static getAttributeNames(): Object | Object[] {
        let attrNamesJson = HttpRequestFacade.getAttributeNames();
        return JSON.parse(attrNamesJson);
    };

    public static getCharacterEncoding(): string {
        return HttpRequestFacade.getCharacterEncoding();
    };

    public static getContentLength(): number {
        return HttpRequestFacade.getContentLength();
    };

    public static getHeaders(name: string): Object | Object[] {
        let headersJson = HttpRequestFacade.getHeaders(name);
        return JSON.parse(headersJson);
    };

    public static getContentType(): string {
        return HttpRequestFacade.getContentType();
    };

    public static getBytes(): Object | Object[] {
        let bytesJson = HttpRequestFacade.getBytes();
        return JSON.parse(bytesJson);
    };

    public static getText(): string {
        let textData = null;
        if (textData === null) {
            textData = HttpRequestFacade.getText();
        }
        return textData;
    };

    public static json(): Object | null {
        return this.getJSON();
    }

    public static getJSON(): Object | null {
        try {
            let text = this.getText();
            return JSON.parse(text);
        } catch (e) {
            return null;
        }
    };

    public static getParameter(name: string): string {
        return HttpRequestFacade.getParameter(name);
    };

    public static getParameters(): Object | Object[]{
        let paramsJson;
        paramsJson = HttpRequestFacade.getParameters();
        return JSON.parse(paramsJson);
    };

    public static getResourcePath(): string {
        return HttpRequestFacade.getResourcePath();
    };

    public static getHeaderNames(): Object | Object[] {
        let headerNamesJson = HttpRequestFacade.getHeaderNames();
        return JSON.parse(headerNamesJson);
    };

    public static getParameterNames(): Object | Object[] {
        let paramNamesJson = HttpRequestFacade.getParameterNames();
        return JSON.parse(paramNamesJson);
    };

    public static getParameterValues(name: string): Object | Object[] {
        let paramValuesJson = HttpRequestFacade.getParameterValues(name);
        return JSON.parse(paramValuesJson);
    };

    public static getProtocol(): string {
        return HttpRequestFacade.getProtocol();
    };

    public static getScheme(): string {
        return HttpRequestFacade.getScheme();
    };

    public static getContextPath(): string {
        return HttpRequestFacade.getContextPath();
    };

    public static getServerName(): string {
        return HttpRequestFacade.getServerName();
    };

    public static getServerPort(): number {
        return HttpRequestFacade.getServerPort();
    };

    public static getQueryString(): string {
        return HttpRequestFacade.getQueryString();
    };

    /**
     * Returns the query string name value pairs as JS object map. When multiple query parameters with the same name are specified,
     * it will collect theirs values in an array in the order of declaration under that name in the map.
     */
    public static getQueryParametersMap(): Object {
        let queryString = this.getQueryString();
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

    public static getRemoteAddress(): string {
        return HttpRequestFacade.getRemoteAddress();
    };

    public static getRemoteHost(): string {
        return HttpRequestFacade.getRemoteHost();
    };

    public static setAttribute(name: string, value: string): void {
        HttpRequestFacade.setAttribute(name, value);
    };

    public static removeAttribute(name: string): void {
        HttpRequestFacade.removeAttribute(name);
    };

    public static getLocale(): Object | Object[] {
        let localeJson = HttpRequestFacade.getLocale();
        return JSON.parse(localeJson);
    };

    public static getRequestURI(): string {
        return HttpRequestFacade.getRequestURI();
    };

    public static isSecure(): boolean {
        return HttpRequestFacade.isSecure();
    };

    public static getRequestURL(): string {
        return HttpRequestFacade.getRequestURL();
    };

    public static getServicePath(): string {
        return HttpRequestFacade.getServicePath();
    };

    public static getRemotePort(): number {
        return HttpRequestFacade.getRemotePort();
    };

    public static getLocalName(): string {
        return HttpRequestFacade.getLocalName();
    };

    public static getLocalAddress(): string {
        return HttpRequestFacade.getLocalAddress();
    };

    public static getLocalPort(): number {
        return HttpRequestFacade.getLocalPort();
    };

    public static getInputStream(): streams.InputStream {
        return streams.createInputStream(HttpRequestFacade.getInputStream());
    };
}
