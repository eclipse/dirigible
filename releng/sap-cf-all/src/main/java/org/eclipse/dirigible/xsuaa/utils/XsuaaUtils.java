/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.xsuaa.utils;

import java.util.List;

import org.eclipse.dirigible.api.v3.core.EnvFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.jwt.utils.JwtUtils;
import org.eclipse.dirigible.jwt.utils.JwtUtils.JwtClaim;

import com.google.gson.annotations.SerializedName;

public class XsuaaUtils {

	private static final String VCAP_SERVICES = "VCAP_SERVICES";
	private static final String SCOPE_SEPARATOR = ".";

	public static boolean isValidJwt(String jwt) {
		XsuaaEnv xsuaaEnv = getXsuaaEnv();
		if (xsuaaEnv != null) {
			XsuaaCredentialsEnv credentials = xsuaaEnv.getCredentials();
			if (credentials != null) {
				String clientId = credentials.getClientId();
				JwtClaim claim = JwtUtils.getClaim(jwt);
				return clientId != null && claim != null && (claim.getClientId().equals(clientId) || claim.getAudience().contains(clientId));
			}
		}
		return false;
	}

	public static XsuaaEnv getXsuaaEnv() {
		String envJson = EnvFacade.get(VCAP_SERVICES);
		VcapServicesEnv vcapServicesEnv = GsonHelper.GSON.fromJson(envJson, VcapServicesEnv.class);
		return vcapServicesEnv.getXsuaa() != null ? vcapServicesEnv.getXsuaa().get(0) : null;
	}

	public static String getScope(String role) {
		return new StringBuilder()
				.append(getXsuaaEnv().getCredentials().getApplicationName())
				.append(SCOPE_SEPARATOR)
				.append(role)
				.toString();
	}

	public static class VcapServicesEnv {
		private List<XsuaaEnv> xsuaa;

		public List<XsuaaEnv> getXsuaa() {
			return xsuaa;
		}

		public void setXsuaa(List<XsuaaEnv> xsuaa) {
			this.xsuaa = xsuaa;
		}
	}

	public static class XsuaaEnv {

		private String label;
		private String name;

		@SerializedName("instance_name")
		private String instanceName;

		private XsuaaCredentialsEnv credentials;

		/**
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * @param label the label to set
		 */
		public void setLabel(String label) {
			this.label = label;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the instanceName
		 */
		public String getInstanceName() {
			return instanceName;
		}

		/**
		 * @param instanceName the instanceName to set
		 */
		public void setInstanceName(String instanceName) {
			this.instanceName = instanceName;
		}

		/**
		 * @return the credentials
		 */
		public XsuaaCredentialsEnv getCredentials() {
			return credentials;
		}

		/**
		 * @param credentials the credentials to set
		 */
		public void setCredentials(XsuaaCredentialsEnv credentials) {
			this.credentials = credentials;
		}

		
	}

	public static class XsuaaCredentialsEnv {

		@SerializedName("clientid")
		private String clientId;

		@SerializedName("clientsecret")
		private String clientSecret;

		private String url;

		@SerializedName("xsappname")
		private String applicationName;

		/**
		 * @return the clientId
		 */
		public String getClientId() {
			return clientId;
		}

		/**
		 * @param clientId the clientId to set
		 */
		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		/**
		 * @return the clientSecret
		 */
		public String getClientSecret() {
			return clientSecret;
		}

		/**
		 * @param clientSecret the clientSecret to set
		 */
		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}

		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * @param url the url to set
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * @return the applicationName
		 */
		public String getApplicationName() {
			return applicationName;
		}

		/**
		 * @param applicationName the applicationName to set
		 */
		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

	}

}