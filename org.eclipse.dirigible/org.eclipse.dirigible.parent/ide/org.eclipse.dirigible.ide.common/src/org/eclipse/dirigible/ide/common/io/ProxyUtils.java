/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.repository.logging.Logger;

public class ProxyUtils {

	public static final String HTTP_PROXY_HOST = "http.proxyHost"; //$NON-NLS-1$
	public static final String HTTP_PROXY_PORT = "http.proxyPort"; //$NON-NLS-1$
	public static final String HTTPS_PROXY_HOST = "https.proxyHost"; //$NON-NLS-1$
	public static final String HTTPS_PROXY_PORT = "https.proxyPort"; //$NON-NLS-1$
	public static final String HTTP_NON_PROXY_HOSTS = "http.nonProxyHosts"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(ProxyUtils.class);

	private static final String PROXY_PROPERTIES_FILE_LOCATION = "proxy.properties"; //$NON-NLS-1$
	private static final String DEFAULT_PROXY_VALUE = "false"; //$NON-NLS-1$
	private static final String PROXY = "proxy"; //$NON-NLS-1$

	public static void setProxySettings() throws IOException {

		// Local case only
		// loadLocalBuildProxy();

		setSystemProxySettings();
		setTrustAllSSL();
	}

	public static HttpClient getHttpClient(boolean trustAll) {
		HttpClient httpClient = null;

		// Local case only
		// try {
		// loadLocalBuildProxy();
		// } catch (IOException e) {
		// logger.error(e.getMessage(), e);
		// }

		if (trustAll) {
			try {
				SchemeSocketFactory plainSocketFactory = PlainSocketFactory.getSocketFactory();
				SchemeSocketFactory sslSocketFactory = new SSLSocketFactory(
						createTrustAllSSLContext());

				Scheme httpScheme = new Scheme("http", 80, plainSocketFactory);
				Scheme httpsScheme = new Scheme("https", 443, sslSocketFactory);

				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(httpScheme);
				schemeRegistry.register(httpsScheme);

				ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
				httpClient = new DefaultHttpClient(cm);
			} catch (Exception e) {
				httpClient = new DefaultHttpClient();
			}
		} else {
			httpClient = new DefaultHttpClient();
		}

		String httpProxyHost = System.getProperty(HTTP_PROXY_HOST);
		String httpProxyPort = System.getProperty(HTTP_PROXY_PORT);

		if (httpProxyHost != null && httpProxyPort != null) {
			HttpHost httpProxy = new HttpHost(httpProxyHost, Integer.parseInt(httpProxyPort));
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpProxy);
		}

		return httpClient;
	}

	// Local case only
	private static void loadLocalBuildProxy() throws IOException {
		InputStream in = ProxyUtils.class.getResourceAsStream(PROXY_PROPERTIES_FILE_LOCATION);
		Properties properties = new Properties();
		properties.load(in);

		String proxy = properties.getProperty(PROXY, DEFAULT_PROXY_VALUE);
		boolean needsProxy = Boolean.parseBoolean(proxy);

		if (needsProxy) {
			final String httpProxyHost = properties.getProperty(HTTP_PROXY_HOST);
			final String httpProxyPort = properties.getProperty(HTTP_PROXY_PORT);
			final String httpsProxyHost = properties.getProperty(HTTPS_PROXY_HOST);
			final String httpsProxyPort = properties.getProperty(HTTPS_PROXY_PORT);

			System.setProperty(HTTP_PROXY_HOST, httpProxyHost);
			System.setProperty(HTTP_PROXY_PORT, httpProxyPort);
			System.setProperty(HTTPS_PROXY_HOST, httpsProxyHost);
			System.setProperty(HTTPS_PROXY_PORT, httpsProxyPort);
		}
	}

	private static void setSystemProxySettings() {
		String parameterHTTP_PROXY_HOST = CommonParameters.get(HTTP_PROXY_HOST);
		if (parameterHTTP_PROXY_HOST != null) {
			System.setProperty(HTTP_PROXY_HOST, parameterHTTP_PROXY_HOST);
			logger.debug("HTTP_PROXY_HOST:" + parameterHTTP_PROXY_HOST);
		} else {
			logger.debug("HTTP_PROXY_HOST not set");
		}
		String parameterHTTP_PROXY_PORT = CommonParameters.get(HTTP_PROXY_PORT);
		if (parameterHTTP_PROXY_PORT != null) {
			System.setProperty(HTTP_PROXY_PORT, parameterHTTP_PROXY_PORT);
			logger.debug("HTTP_PROXY_PORT:" + parameterHTTP_PROXY_PORT);
		} else {
			logger.debug("HTTP_PROXY_PORT not set");
		}
		String parameterHTTPS_PROXY_HOST = CommonParameters.get(HTTPS_PROXY_HOST);
		if (parameterHTTPS_PROXY_HOST != null) {
			System.setProperty(HTTPS_PROXY_HOST, parameterHTTPS_PROXY_HOST);
			logger.debug("HTTPS_PROXY_HOST:" + parameterHTTPS_PROXY_HOST);
		} else {
			logger.debug("HTTPS_PROXY_HOST not set");
		}
		String parameterHTTPS_PROXY_PORT = CommonParameters.get(HTTPS_PROXY_PORT);
		if (parameterHTTPS_PROXY_PORT != null) {
			System.setProperty(HTTPS_PROXY_PORT, parameterHTTPS_PROXY_PORT);
			logger.debug("HTTPS_PROXY_PORT:" + parameterHTTPS_PROXY_PORT);
		} else {
			logger.debug("HTTPS_PROXY_PORT not set");
		}
		String parameterHTTP_NON_PROXY_HOSTS = CommonParameters.get(HTTP_NON_PROXY_HOSTS);
		if (parameterHTTP_NON_PROXY_HOSTS != null) {
			System.setProperty(HTTP_NON_PROXY_HOSTS, parameterHTTP_NON_PROXY_HOSTS);
			logger.debug("HTTP_NON_PROXY_HOSTS:" + parameterHTTP_NON_PROXY_HOSTS);
		} else {
			logger.debug("HTTP_NON_PROXY_HOSTS not set");
		}
	}

	private static void setTrustAllSSL() throws IOException {
		try {
			HttpsURLConnection.setDefaultSSLSocketFactory(createTrustAllSSLContext()
					.getSocketFactory());
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (KeyManagementException e) {
			throw new IOException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}
	}

	private static SSLContext createTrustAllSSLContext() throws NoSuchAlgorithmException,
			KeyManagementException {
		SSLContext sslContext = SSLContext.getInstance("SSL");

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		// Set up a TrustManager that trusts everything
		sslContext.init(null, trustAllCerts, new SecureRandom());
		return sslContext;
	}
}
