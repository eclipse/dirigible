/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.cf.utils;

import java.util.List;

import org.eclipse.dirigible.api.v3.core.EnvFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

import com.google.gson.annotations.SerializedName;

public class CloudFoundryUtils {

	private static final String HTTPS = "https://";
	private static final String VCAP_SERVICES = "VCAP_SERVICES";
	private static final String VCAP_APPLICATION = "VCAP_APPLICATION";

	public static String getApplicationHost() {
		String applicationHost = null;
		VcapApplicationEnv vcapApplicationEnv = getApplicationEnv();
		if (vcapApplicationEnv.getApplicationUris() != null && vcapApplicationEnv.getApplicationUris().size() > 0) {
			applicationHost = vcapApplicationEnv.getApplicationUris().get(0);
		} else if (vcapApplicationEnv.getUris() != null && vcapApplicationEnv.getUris().size() > 0) {
			applicationHost = vcapApplicationEnv.getUris().get(0);
		}
		if (applicationHost != null && !applicationHost.startsWith(HTTPS)) {
			applicationHost = HTTPS + applicationHost;
		}
		return applicationHost;
	}

	public static VcapApplicationEnv getApplicationEnv() {
		String envJson = EnvFacade.get(VCAP_APPLICATION);
		return GsonHelper.GSON.fromJson(envJson, VcapApplicationEnv.class);
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

	public static PostgreHyperscalerDbEnv getPostgreHyperscalerDbEnv() {
		String envJson = EnvFacade.get(VCAP_SERVICES);
		VcapServicesEnv vcapServicesEnv = GsonHelper.GSON.fromJson(envJson, VcapServicesEnv.class);
		return vcapServicesEnv.getPostgreHyperscalerDbEnv() != null ? vcapServicesEnv.getPostgreHyperscalerDbEnv().get(0) : null;
	}

	public static HanaDbEnv getHanaDbEnv() {
		String envJson = EnvFacade.get(VCAP_SERVICES);
		VcapServicesEnv vcapServicesEnv = GsonHelper.GSON.fromJson(envJson, VcapServicesEnv.class);
		return vcapServicesEnv.getHanaDbEnv() != null ? vcapServicesEnv.getHanaDbEnv().get(0) : null;
	}

	public static HanaCloudDbEnv getHanaCloudDbEnv() {
		String envJson = EnvFacade.get(VCAP_SERVICES);
		VcapServicesEnv vcapServicesEnv = GsonHelper.GSON.fromJson(envJson, VcapServicesEnv.class);
		return vcapServicesEnv.getHanaCloudDbEnv() != null ? vcapServicesEnv.getHanaCloudDbEnv().get(0) : null;
	}

	public static HanaSchemaEnv getHanaSchemaEnv() {
		String envJson = EnvFacade.get(VCAP_SERVICES);
		VcapServicesEnv vcapServicesEnv = GsonHelper.GSON.fromJson(envJson, VcapServicesEnv.class);
		return vcapServicesEnv.getHanaSchemaEnv() != null ? vcapServicesEnv.getHanaSchemaEnv().get(0) : null;
	}

	public static DestinationEnv getDestinationEnv() {
		String envJson = EnvFacade.get(VCAP_SERVICES);
		VcapServicesEnv vcapServicesEnv = GsonHelper.GSON.fromJson(envJson, VcapServicesEnv.class);
		return vcapServicesEnv.getDestinationEnv() != null ? vcapServicesEnv.getDestinationEnv().get(0) : null;
	}

	public static class VcapApplicationEnv {

		private String name;

		private List<String> uris;

		@SerializedName("application_name")
		private String applicationName;

		@SerializedName("application_uris")
		private List<String> applicationUris;

		@SerializedName("organization_id")
		private String organizationId;

		@SerializedName("organization_name")
		private String organizationName;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getUris() {
			return uris;
		}

		public void setUris(List<String> uris) {
			this.uris = uris;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public List<String> getApplicationUris() {
			return applicationUris;
		}

		public void setApplicationUris(List<String> applicationUris) {
			this.applicationUris = applicationUris;
		}

		public String getOrganizationId() {
			return organizationId;
		}

		public void setOrganizationId(String organizationId) {
			this.organizationId = organizationId;
		}

		public String getOrganizationName() {
			return organizationName;
		}

		public void setOrganizationName(String organizationName) {
			this.organizationName = organizationName;
		}
	}

	public static class VcapServicesEnv {

		private List<XsuaaEnv> xsuaa;
		
		@SerializedName("postgresql")
		private List<PostgreDbEnv> postgreDbEnv;

		@SerializedName("postgresql-db")
		private List<PostgreHyperscalerDbEnv> postgreHyperscalerDbEnv;

		@SerializedName("hana-db")
		private List<HanaDbEnv> hanaDbEnv;

		@SerializedName("hana-cloud")
		private List<HanaCloudDbEnv> hanaCloudDbEnv;

		@SerializedName("hana")
		private List<HanaSchemaEnv> hanaSchemaEnv;

		private List<DestinationEnv> destinationEnv;

		public List<XsuaaEnv> getXsuaa() {
			return xsuaa;
		}

		public void setXsuaa(List<XsuaaEnv> xsuaa) {
			this.xsuaa = xsuaa;
		}

		public List<PostgreDbEnv> getPostgreDbEnv() {
			return postgreDbEnv;
		}

		public List<PostgreHyperscalerDbEnv> getPostgreHyperscalerDbEnv() {
			return postgreHyperscalerDbEnv;
		}

		public void setPostgreHyperscalerDbEnv(List<PostgreHyperscalerDbEnv> postgreHyperscalerDbEnv) {
			this.postgreHyperscalerDbEnv = postgreHyperscalerDbEnv;
		}

		public void setPostgreDbEnv(List<PostgreDbEnv> postgreDbEnv) {
			this.postgreDbEnv = postgreDbEnv;
		}

		public List<HanaDbEnv> getHanaDbEnv() {
			return hanaDbEnv;
		}

		public List<HanaCloudDbEnv> getHanaCloudDbEnv() {
			return hanaCloudDbEnv;
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

		public List<DestinationEnv> getDestinationEnv() {
			return destinationEnv;
		}

		public void setDestinationEnv(List<DestinationEnv> destinationEnv) {
			this.destinationEnv = destinationEnv;
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

			@SerializedName("verificationkey")
			private String verificationKey;

			@SerializedName("xsappname")
			private String applicationName;

			@SerializedName("identityzone")
			private String identityZone;

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
			 * @return the verificationKey
			 */
			public String getVerificationKey() {
				return verificationKey;
			}

			/**
			 * @param verificationKey the verificationKey to set
			 */
			public void setVerificationKey(String verificationKey) {
				this.verificationKey = verificationKey;
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

			/**
			 * @return the identityZone
			 */
			public String getIdentityZone() {
				return identityZone;
			}

			/**
			 * @param identityZone the identityZone to set
			 */
			public void setIdentityZone(String identityZone) {
				this.identityZone = identityZone;
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

	public static class PostgreHyperscalerDbEnv {

		private PostgreHyperscalerDbCredentialsEnv credentials;

		public PostgreHyperscalerDbCredentialsEnv getCredentials() {
			return credentials;
		}

		public void setCredentials(PostgreHyperscalerDbCredentialsEnv credentials) {
			this.credentials = credentials;
		}
	}

	public static class PostgreHyperscalerDbCredentialsEnv {

		private static final String NO_SSL = "?sslfactory=org.postgresql.ssl.NonValidatingFactory&ssl=true";

		private String username;

		private String password;

		private String hostname;

		private String dbname;

		private String port;

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

		public String getHostname() {
			return hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		public String getDbname() {
			return dbname;
		}

		public void setDbname(String dbname) {
			this.dbname = dbname;
		}

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			this.port = port;
		}

		public String getUrl() {
			return "jdbc:postgresql://" + getHostname() + ":" + getPort() + "/" + getDbname() + NO_SSL;
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

	public static class HanaCloudDbEnv {

		private HanaCloudDbCredentialsEnv credentials;

		public HanaCloudDbCredentialsEnv getCredentials() {
			return credentials;
		}

		public void setCredentials(HanaCloudDbCredentialsEnv credentials) {
			this.credentials = credentials;
		}

		public static class HanaCloudDbCredentialsEnv {

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

	public static class DestinationEnv {

		private DestinationCredentialsEnv credentials;

		public DestinationCredentialsEnv getCredentials() {
			return credentials;
		}

		public void setCredentials(DestinationCredentialsEnv credentials) {
			this.credentials = credentials;
		}

		public static class DestinationCredentialsEnv {

			private String clientId;
			private String clientSecret;
			private String url;
			private String uri;

			public String getClientId() {
				return clientId;
			}

			public void setClientId(String clientId) {
				this.clientId = clientId;
			}

			public String getClientSecret() {
				return clientSecret;
			}

			public void setClientSecret(String clientSecret) {
				this.clientSecret = clientSecret;
			}

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getUri() {
				return uri;
			}

			public void setUri(String uri) {
				this.uri = uri;
			}
		}
	}
}