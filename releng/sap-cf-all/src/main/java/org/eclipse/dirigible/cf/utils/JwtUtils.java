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
package org.eclipse.dirigible.cf.utils;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

import com.google.gson.annotations.SerializedName;

public class JwtUtils {

	private static final String AUTHORIZATION_HEADER = "authorization";
	private static final Base64 BASE64 = new Base64(true);
	private static final String JWT_SPLIT_TOKEN = "\\.";
	private static final int JWT_HEADER = 0;
	private static final int JWT_BODY = 1;
	private static final int JWT_SIGNATURE = 2;

	public static String getJwt(ServletRequest request) {
		return ((HttpServletRequest) request).getHeader(AUTHORIZATION_HEADER);
	}

	public static JwtHeader getHeader(String jwt) {
		String header = getToken(jwt, JWT_HEADER);
		if (header != null) {
			return GsonHelper.GSON.fromJson(header, JwtHeader.class);
		}
		return null;
	}

	public static JwtClaim getClaim(String jwt) {
		String body = getToken(jwt, JWT_BODY);
		if (body != null) {
			return GsonHelper.GSON.fromJson(body, JwtClaim.class);
		}
		return null;
	}

	public static String getSignature(String jwt) {
		return getToken(jwt, JWT_SIGNATURE, false);
	}

	public static class JwtHeader {

		@SerializedName("typ")
		private String type;

		@SerializedName("alg")
		private String algorithm;

		@SerializedName("kid")
		private String keyId;

		@SerializedName("jku")
		private String jwkSetUrl;

		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * @param type the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * @return the algorithm
		 */
		public String getAlgorithm() {
			return algorithm;
		}

		/**
		 * @param algorithm the algorithm to set
		 */
		public void setAlgorithm(String algorithm) {
			this.algorithm = algorithm;
		}

		/**
		 * @return the keyId
		 */
		public String getKeyId() {
			return keyId;
		}

		/**
		 * @param keyId the keyId to set
		 */
		public void setKeyId(String keyId) {
			this.keyId = keyId;
		}

		/**
		 * @return the jwkSetUrl
		 */
		public String getJwkSetUrl() {
			return jwkSetUrl;
		}

		/**
		 * @param jwkSetUrl the jwkSetUrl to set
		 */
		public void setJwkSetUrl(String jwkSetUrl) {
			this.jwkSetUrl = jwkSetUrl;
		}

	}

	/**
	 * @author i302281
	 *
	 */
	public static class JwtClaim {

		@SerializedName("jti")
		private String id;

		@SerializedName("given_name")
		private String givenName;

		@SerializedName("family_name")
		private String familyName;

		private List<String> scope;

		@SerializedName("client_id")
		private String clientId;

		@SerializedName("grant_type")
		private String grantType;

		@SerializedName("user_id")
		private String userId;

		@SerializedName("user_name")
		private String userName;

		@SerializedName("email")
		private String email;

		@SerializedName("auth_time")
		private long authTime;

		@SerializedName("iat")
		private long issuedAt;

		@SerializedName("exp")
		private long expirantionTime;

		@SerializedName("iss")
		private String issuer;

		@SerializedName("aud")
		private List<String> audience;

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @return the givenName
		 */
		public String getGivenName() {
			return givenName;
		}

		/**
		 * @param givenName the givenName to set
		 */
		public void setGivenName(String givenName) {
			this.givenName = givenName;
		}

		/**
		 * @return the familyName
		 */
		public String getFamilyName() {
			return familyName;
		}

		/**
		 * @param familyName the familyName to set
		 */
		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}

		/**
		 * @return the scope
		 */
		public List<String> getScope() {
			return scope;
		}

		/**
		 * @param scope the scope to set
		 */
		public void setScope(List<String> scope) {
			this.scope = scope;
		}

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
		 * @return the grantType
		 */
		public String getGrantType() {
			return grantType;
		}

		/**
		 * @param grantType the grantType to set
		 */
		public void setGrantType(String grantType) {
			this.grantType = grantType;
		}

		/**
		 * @return the userId
		 */
		public String getUserId() {
			return userId;
		}

		/**
		 * @param userId the userId to set
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}

		/**
		 * @return the userName
		 */
		public String getUserName() {
			return userName;
		}

		/**
		 * @param userName the userName to set
		 */
		public void setUserName(String userName) {
			this.userName = userName;
		}

		/**
		 * @return the email
		 */
		public String getEmail() {
			return email;
		}

		/**
		 * @param email the email to set
		 */
		public void setEmail(String email) {
			this.email = email;
		}

		/**
		 * @return the authTime
		 */
		public long getAuthTime() {
			return authTime;
		}

		/**
		 * @param authTime the authTime to set
		 */
		public void setAuthTime(long authTime) {
			this.authTime = authTime;
		}

		/**
		 * @return the issuedAt
		 */
		public long getIssuedAt() {
			return issuedAt;
		}

		/**
		 * @param issuedAt the issuedAt to set
		 */
		public void setIssuedAt(long issuedAt) {
			this.issuedAt = issuedAt;
		}

		/**
		 * @return the expirantionTime
		 */
		public long getExpirantionTime() {
			return expirantionTime;
		}

		/**
		 * @param expirantionTime the expirantionTime to set
		 */
		public void setExpirantionTime(long expirantionTime) {
			this.expirantionTime = expirantionTime;
		}

		/**
		 * @return the issuer
		 */
		public String getIssuer() {
			return issuer;
		}

		/**
		 * @param issuer the issuer to set
		 */
		public void setIssuer(String issuer) {
			this.issuer = issuer;
		}

		/**
		 * @return the audience
		 */
		public List<String> getAudience() {
			return audience;
		}

		/**
		 * @param audience the audience to set
		 */
		public void setAudience(List<String> audience) {
			this.audience = audience;
		}

	}

	private static String getToken(String jwt, int index) {
		return getToken(jwt, index, true);
	}
	private static String getToken(String jwt, int index, boolean encode) {
		if (jwt != null) {
			String[] tokens = jwt.split(JWT_SPLIT_TOKEN);
			String token = tokens[index];
			if (encode) {
				return new String(BASE64.decode(token));
			}
			return token;
		}
		return null;
	}
}
