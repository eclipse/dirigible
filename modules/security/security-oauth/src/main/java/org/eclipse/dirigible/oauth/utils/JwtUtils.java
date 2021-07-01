/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.oauth.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Verification;
import com.google.gson.annotations.SerializedName;

public class JwtUtils {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_HEADER_VALUE_BEARER = "Bearer ";
	private static final String JWT_COOKIE_NAME = "jwt-cookie";
	private static final String JWT_SESSION_NAME = "jwt-session";
	private static final Base64 BASE64 = new Base64(true);
	private static final String JWT_SPLIT_TOKEN = "\\.";
	private static final int JWT_HEADER = 0;
	private static final int JWT_BODY = 1;
	private static final int JWT_SIGNATURE = 2;

	private static final String SCOPE_SEPARATOR = ".";

	public static boolean isInRole(ServletRequest request, String role) {
		String jwt = JwtUtils.getJwt(request);
		JwtClaim claim = JwtUtils.getClaim(jwt);
		List<String> scope = claim.getScope();
		return scope.contains(getScope(role)) || scope.contains(role);
	}

	public static String getScope(String role) {
		return new StringBuilder()
				.append(OAuthUtils.getOAuthApplicationName())
				.append(SCOPE_SEPARATOR)
				.append(role)
				.toString();
	}

	public static String getJwt(ServletRequest request) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String jwt = getJwtFromCookie(httpServletRequest);
		if (jwt == null) {
			jwt = getJwtFromHeader(httpServletRequest);
		}
		return jwt;
	}

	private static String getJwtFromCookie(HttpServletRequest httpServletRequest) {
		String jwt = null;
		Cookie[] cookies = httpServletRequest.getCookies();
		for (int i = 0; cookies != null && i < cookies.length; i ++) {
			if (cookies[i].getName().equals(JWT_COOKIE_NAME)) {
				jwt = cookies[i].getValue();
				break;
			}
		}
		return jwt;
	}

	private static String getJwtFromHeader(HttpServletRequest httpServletRequest) {
		String jwt = null;
		String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
		if (authorizationHeader != null) {	
			// Expected format Authorization header value: Bearer eyJhbGciOiJS...
			if (authorizationHeader.startsWith(AUTHORIZATION_HEADER_VALUE_BEARER)) {
				String tokenValue = authorizationHeader.replace(AUTHORIZATION_HEADER_VALUE_BEARER, "");
				if (isValidJwt(httpServletRequest, tokenValue)) {
					jwt = tokenValue;
				}
			}
		}
		return jwt;
	}

	public static void setJwt(ServletResponse response, String jwt) {
		((HttpServletResponse) response).addCookie(createJwtCookie(jwt));
	}

	private static Cookie createJwtCookie(String value) {
		Cookie jwtCookie = new Cookie(JWT_COOKIE_NAME, value);
		jwtCookie.setPath("/");
		return jwtCookie;
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

	public static boolean isValidJwt(ServletRequest request, String token) {
		boolean isValid = true;

		String sessionToken = getSessionToken(request);
		if (sessionToken == null || !sessionToken.equals(token)) {
			try {
				verifyJwt(token);
				setSessionToken(request, token);
			} catch (Exception e) {
				isValid = false;
				removeSessionToken(request);
				logger.error(e.getMessage(), e);
			}
		}

		return isValid;
	}

	public static void verifyJwt(String token) throws IOException, GeneralSecurityException {
		String verificationKey = OAuthUtils.getOAuthVerificationKey();
		RSAPublicKey publicKey = getPublicKeyFromString(verificationKey);
		Algorithm algorithm = Algorithm.RSA256(publicKey, null);
		Verification verification = JWT.require(algorithm)
				.acceptLeeway(1) // 1 sec for nbf and iat
				.acceptExpiresAt(5) // 5 secs for exp
				.withAudience(OAuthUtils.getOAuthClientId());

		if (Boolean.parseBoolean(Configuration.get(OAuthService.DIRIGIBLE_OAUTH_CHECK_ISSUER_ENABLED, Boolean.TRUE.toString()))) {
			verification.withIssuer(OAuthUtils.getOAuthTokenUrl(), OAuthUtils.getOAuthIssuer());
		}

		JWTVerifier verifier = verification.build();
		verifier.verify(token);
	}

	public static boolean isExpiredJwt(ServletRequest request, String token) {
		JwtClaim claim = getClaim(token);

		long currentTime = new Date().getTime();
		long expirantionTime = (claim.getExpirantionTime() - 60 ) * 1000;
		boolean isExpired = currentTime >= expirantionTime;

		if (isExpired) {
			removeSessionToken(request);
		}

		return isExpired;
	}

	private static String getSessionToken(ServletRequest request) {
		String value = null;
		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session != null) {
			value = (String) session.getAttribute(JWT_SESSION_NAME);
		}
		return value;
	}

	private static void setSessionToken(ServletRequest request, String value) {
		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session != null) {
			session.setAttribute(JWT_SESSION_NAME, value);
		}
	}

	private static void removeSessionToken(ServletRequest request) {
		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session != null) {
			session.removeAttribute(JWT_SESSION_NAME);
		}
	}

	private static RSAPublicKey getPublicKeyFromString(String key) throws IOException, GeneralSecurityException {
		String publicKeyPEM = key;
		publicKeyPEM = publicKeyPEM.replace("\n", "");
		publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "");
		publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");

		byte[] encoded = BASE64.decode(publicKeyPEM);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));

		return pubKey;
	}
}
