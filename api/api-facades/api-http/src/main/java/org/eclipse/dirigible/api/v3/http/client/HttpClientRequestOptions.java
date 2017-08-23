package org.eclipse.dirigible.api.v3.http.client;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HttpClientRequestOptions {

	// for reference:
	// https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/config/RequestConfig.html

	private boolean expectContinueEnabled;
	private String proxyHost;
	private int proxyPort;
	private String cookieSpec;
	private boolean redirectsEnabled;
	private boolean relativeRedirectsAllowed;
	private boolean circularRedirectsAllowed;
	private int maxRedirects;
	private boolean authenticationEnabled;
	private Collection<String> targetPreferredAuthSchemes;
	private Collection<String> proxyPreferredAuthSchemes;
	private int connectionRequestTimeout;
	private int connectTimeout;
	private int socketTimeout;
	private boolean contentCompressionEnabled;

	// binary content for POST and PUT
	private byte[] data;
	// text content for POST and PUT
	private String text;
	// file content for POST and PUT
	private String[] files;
	// encoding for POST
	private String characterEncoding = StandardCharsets.UTF_8.name();
	// content type for POST
	private String contentType = "text/plain";
	// headers
	private List<HttpClientHeader> headers = new ArrayList<HttpClientHeader>();
	// params
	private List<HttpClientParam> params = new ArrayList<HttpClientParam>();
	// whether to request as binary or text
	private boolean binary;

	public boolean isExpectContinueEnabled() {
		return expectContinueEnabled;
	}

	public void setExpectContinueEnabled(boolean expectContinueEnabled) {
		this.expectContinueEnabled = expectContinueEnabled;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getCookieSpec() {
		return cookieSpec;
	}

	public void setCookieSpec(String cookieSpec) {
		this.cookieSpec = cookieSpec;
	}

	public boolean isRedirectsEnabled() {
		return redirectsEnabled;
	}

	public void setRedirectsEnabled(boolean redirectsEnabled) {
		this.redirectsEnabled = redirectsEnabled;
	}

	public boolean isRelativeRedirectsAllowed() {
		return relativeRedirectsAllowed;
	}

	public void setRelativeRedirectsAllowed(boolean relativeRedirectsAllowed) {
		this.relativeRedirectsAllowed = relativeRedirectsAllowed;
	}

	public boolean isCircularRedirectsAllowed() {
		return circularRedirectsAllowed;
	}

	public void setCircularRedirectsAllowed(boolean circularRedirectsAllowed) {
		this.circularRedirectsAllowed = circularRedirectsAllowed;
	}

	public int getMaxRedirects() {
		return maxRedirects;
	}

	public void setMaxRedirects(int maxRedirects) {
		this.maxRedirects = maxRedirects;
	}

	public boolean isAuthenticationEnabled() {
		return authenticationEnabled;
	}

	public void setAuthenticationEnabled(boolean authenticationEnabled) {
		this.authenticationEnabled = authenticationEnabled;
	}

	public Collection<String> getTargetPreferredAuthSchemes() {
		return targetPreferredAuthSchemes;
	}

	public void setTargetPreferredAuthSchemes(Collection<String> targetPreferredAuthSchemes) {
		this.targetPreferredAuthSchemes = targetPreferredAuthSchemes;
	}

	public Collection<String> getProxyPreferredAuthSchemes() {
		return proxyPreferredAuthSchemes;
	}

	public void setProxyPreferredAuthSchemes(Collection<String> proxyPreferredAuthSchemes) {
		this.proxyPreferredAuthSchemes = proxyPreferredAuthSchemes;
	}

	public int getConnectionRequestTimeout() {
		return connectionRequestTimeout;
	}

	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public boolean isContentCompressionEnabled() {
		return contentCompressionEnabled;
	}

	public void setContentCompressionEnabled(boolean contentCompressionEnabled) {
		this.contentCompressionEnabled = contentCompressionEnabled;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String[] getFiles() {
		return files;
	}

	public void setFiles(String[] files) {
		this.files = files;
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public List<HttpClientHeader> getHeaders() {
		return headers;
	}

	public void setHeaders(List<HttpClientHeader> headers) {
		this.headers = headers;
	}

	public List<HttpClientParam> getParams() {
		return params;
	}

	public void setParams(List<HttpClientParam> params) {
		this.params = params;
	}

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}

}
