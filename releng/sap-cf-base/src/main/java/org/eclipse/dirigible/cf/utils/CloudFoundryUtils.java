/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.cf.utils;

import java.util.List;

import javax.servlet.ServletRequest;

import org.eclipse.dirigible.api.v3.core.EnvFacade;
import org.eclipse.dirigible.cf.utils.CloudFoundryUtils.XsuaaEnv.XsuaaCredentialsEnv;
import org.eclipse.dirigible.cf.utils.JwtUtils.JwtClaim;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

import com.google.gson.annotations.SerializedName;

public class CloudFoundryUtils {

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

	public static boolean isInRole(ServletRequest request, String role) {
		String jwt = JwtUtils.getJwt(request);
		JwtClaim claim = JwtUtils.getClaim(jwt);
		List<String> scope = claim.getScope();
		return scope.contains(getScope(role)) || scope.contains(role);
	}

	public static String getScope(String role) {
		return new StringBuilder()
				.append(getXsuaaEnv().getCredentials().getApplicationName())
				.append(SCOPE_SEPARATOR)
				.append(role)
				.toString();
	}

	public static XsuaaEnv getXsuaaEnv() {
		String envJson = EnvFacade.get(VCAP_SERVICES);
		VcapServicesEnv vcapServicesEnv = GsonHelper.GSON.fromJson(envJson, VcapServicesEnv.class);
		return vcapServicesEnv.getXsuaa() != null ? vcapServicesEnv.getXsuaa().get(0) : null;
	}

	public static PostgreDbEnv getPostgreDbEnv() {
		String envJson = EnvFacade.get(VCAP_SERVICES);
		VcapServicesEnv vcapServicesEnv = GsonHelper.GSON.fromJson(envJson, VcapServicesEnv.class);
		return vcapServicesEnv.getPostgreDbEnv() != null ? vcapServicesEnv.getPostgreDbEnv().get(0) : null;
	}

	public static HanaDbEnv getHanaDbEnv() {
		String envJson = EnvFacade.get(VCAP_SERVICES);
		VcapServicesEnv vcapServicesEnv = GsonHelper.GSON.fromJson(envJson, VcapServicesEnv.class);
		return vcapServicesEnv.getHanaDbEnv() != null ? vcapServicesEnv.getHanaDbEnv().get(0) : null;
	}

	public static HanaSchemaEnv getHanaSchemaEnv() {
		String envJson = EnvFacade.get(VCAP_SERVICES);
		VcapServicesEnv vcapServicesEnv = GsonHelper.GSON.fromJson(envJson, VcapServicesEnv.class);
		return vcapServicesEnv.getHanaSchemaEnv() != null ? vcapServicesEnv.getHanaSchemaEnv().get(0) : null;
	}

	public static class VcapServicesEnv {

		private List<XsuaaEnv> xsuaa;
		
		@SerializedName("postgresql")
		private List<PostgreDbEnv> postgreDbEnv;

		@SerializedName("hana-db")
		private List<HanaDbEnv> hanaDbEnv;

		@SerializedName("hana")
		private List<HanaSchemaEnv> hanaSchemaEnv;

		public List<XsuaaEnv> getXsuaa() {
			return xsuaa;
		}

		public void setXsuaa(List<XsuaaEnv> xsuaa) {
			this.xsuaa = xsuaa;
		}

		public List<PostgreDbEnv> getPostgreDbEnv() {
			return postgreDbEnv;
		}

		public void setPostgreDbEnv(List<PostgreDbEnv> postgreDbEnv) {
			this.postgreDbEnv = postgreDbEnv;
		}

		public List<HanaDbEnv> getHanaDbEnv() {
			return hanaDbEnv;
		}

		public void setHanaDbEnv(List<HanaDbEnv> hanaDbEnv) {
			this.hanaDbEnv = hanaDbEnv;
		}

		public List<HanaSchemaEnv> getHanaSchemaEnv() {
			return hanaSchemaEnv;
		}

		public void setHanaSchemaEnv(List<HanaSchemaEnv> hanaSchemaEnv) {
			this.hanaSchemaEnv = hanaSchemaEnv;
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

	public static class PostgreDbEnv {

		private PostgreDbCredentialsEnv credentials;

		public PostgreDbCredentialsEnv getCredentials() {
			return credentials;
		}

		public void setCredentials(PostgreDbCredentialsEnv credentials) {
			this.credentials = credentials;
		}

		public static class PostgreDbCredentialsEnv {

			@SerializedName("write_url")
			private String url;
			private String username;
			private String password;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getUsername() {
				return username;
			}

			public void setUsername(String username) {
				this.username = username;
			}

			public String getPassword() {
				return password;
			}

			public void setPassword(String password) {
				this.password = password;
			}
		}
	}

	public static class HanaDbEnv {

		private HanaDbCredentialsEnv credentials;

		public HanaDbCredentialsEnv getCredentials() {
			return credentials;
		}

		public void setCredentials(HanaDbCredentialsEnv credentials) {
			this.credentials = credentials;
		}

		public static class HanaDbCredentialsEnv {

			private String url;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}
		}
	}

	public static class HanaSchemaEnv {

		private HanaSchemaCredentialsEnv credentials;

		public HanaSchemaCredentialsEnv getCredentials() {
			return credentials;
		}

		public void setCredentials(HanaSchemaCredentialsEnv credentials) {
			this.credentials = credentials;
		}

		public static class HanaSchemaCredentialsEnv {

			private String url;

			@SerializedName("user")
			private String username;

			private String password;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getUsername() {
				return username;
			}

			public void setUsername(String username) {
				this.username = username;
			}

			public String getPassword() {
				return password;
			}

			public void setPassword(String password) {
				this.password = password;
			}
		}
	}
}