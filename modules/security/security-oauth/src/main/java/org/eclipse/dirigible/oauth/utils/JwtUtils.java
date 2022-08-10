/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
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

/**
 * The Class JwtUtils.
 */
public class JwtUtils {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	/** The Constant AUTHORIZATION_HEADER. */
	private static final String AUTHORIZATION_HEADER = "Authorization";
	
	/** The Constant AUTHORIZATION_HEADER_VALUE_BEARER. */
	private static final String AUTHORIZATION_HEADER_VALUE_BEARER = "Bearer ";
	
	/** The Constant JWT_COOKIE_NAME. */
	private static final String JWT_COOKIE_NAME = "jwt-cookie";
	
	/** The Constant JWT_SESSION_NAME. */
	private static final String JWT_SESSION_NAME = "jwt-session";
	
	/** The Constant BASE64. */
	private static final Base64 BASE64 = new Base64(true);
	
	/** The Constant JWT_SPLIT_TOKEN. */
	private static final String JWT_SPLIT_TOKEN = "\\.";
	
	/** The Constant JWT_HEADER. */
	private static final int JWT_HEADER = 0;
	
	/** The Constant JWT_BODY. */
	private static final int JWT_BODY = 1;
	
	/** The Constant JWT_SIGNATURE. */
	private static final int JWT_SIGNATURE = 2;

	/** The Constant SCOPE_SEPARATOR. */
	private static final String SCOPE_SEPARATOR = ".";

	/**
	 * Checks if is in role.
	 *
	 * @param request the request
	 * @param role the role
	 * @return true, if is in role
	 */
	public static boolean isInRole(ServletRequest request, String role) {
		String jwt = JwtUtils.getJwt(request);
		JwtClaim claim = JwtUtils.getClaim(jwt);
		List<String> scope = claim.getScope();
		return scope.contains(getScope(role)) || scope.contains(role);
	}

	/**
	 * Gets the scope.
	 *
	 * @param role the role
	 * @return the scope
	 */
	public static String getScope(String role) {
		return new StringBuilder()
				.append(OAuthUtils.getOAuthApplicationName())
				.append(SCOPE_SEPARATOR)
				.append(role)
				.toString();
	}

	/**
	 * Gets the jwt.
	 *
	 * @param request the request
	 * @return the jwt
	 */
	public static String getJwt(ServletRequest request) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String jwt = getJwtFromCookie(httpServletRequest);
		if (jwt == null) {
			jwt = getJwtFromHeader(httpServletRequest);
		}
		return jwt;
	}

	/**
	 * Gets the jwt from cookie.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the jwt from cookie
	 */
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

	/**
	 * Gets the jwt from header.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the jwt from header
	 */
	private static String getJwtFromHeader(HttpServletRequest httpServletRequest) {
		String jwt = null;
		String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
		if (authorizationHeader != null) {	
			// Expected format Authorization header value: Bearer eyJhbGciOiJS...
			if (authorizationHeader.toLowerCase().startsWith(AUTHORIZATION_HEADER_VALUE_BEARER.toLowerCase())) {
				String tokenValue = authorizationHeader.substring(AUTHORIZATION_HEADER_VALUE_BEARER.length());
				if (isValidJwt(httpServletRequest, tokenValue)) {
					jwt = tokenValue;
				}
			}
		}
		return jwt;
	}

	/**
	 * Sets the jwt.
	 *
	 * @param response the response
	 * @param jwt the jwt
	 */
	public static void setJwt(ServletResponse response, String jwt) {
		((HttpServletResponse) response).addCookie(createJwtCookie(jwt));
	}

	/**
	 * Creates the jwt cookie.
	 *
	 * @param value the value
	 * @return the cookie
	 */
	private static Cookie createJwtCookie(String value) {
		Cookie jwtCookie = new Cookie(JWT_COOKIE_NAME, value);
		jwtCookie.setPath("/");
		return jwtCookie;
	}

	/**
	 * Gets the header.
	 *
	 * @param jwt the jwt
	 * @return the header
	 */
	public static JwtHeader getHeader(String jwt) {
		String header = getToken(jwt, JWT_HEADER);
		if (header != null) {
			return GsonHelper.GSON.fromJson(header, JwtHeader.class);
		}
		return null;
	}

	/**
	 * Gets the claim.
	 *
	 * @param jwt the jwt
	 * @return the claim
	 */
	public static JwtClaim getClaim(String jwt) {
		String body = getToken(jwt, JWT_BODY);
		if (body != null) {
			return GsonHelper.GSON.fromJson(body, JwtClaim.class);
		}
		return null;
	}

	/**
	 * Gets the signature.
	 *
	 * @param jwt the jwt
	 * @return the signature
	 */
	public static String getSignature(String jwt) {
		return getToken(jwt, JWT_SIGNATURE, false);
	}

	/**
	 * The Class JwtHeader.
	 */
	public static class JwtHeader {

		/** The type. */
		@SerializedName("typ")
		private String type;

		/** The algorithm. */
		@SerializedName("alg")
		private String algorithm;

		/** The key id. */
		@SerializedName("kid")
		private String keyId;

		/** The jwk set url. */
		@SerializedName("jku")
		private String jwkSetUrl;

		/**
		 * Gets the type.
		 *
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * Sets the type.
		 *
		 * @param type the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * Gets the algorithm.
		 *
		 * @return the algorithm
		 */
		public String getAlgorithm() {
			return algorithm;
		}

		/**
		 * Sets the algorithm.
		 *
		 * @param algorithm the algorithm to set
		 */
		public void setAlgorithm(String algorithm) {
			this.algorithm = algorithm;
		}

		/**
		 * Gets the key id.
		 *
		 * @return the keyId
		 */
		public String getKeyId() {
			return keyId;
		}

		/**
		 * Sets the key id.
		 *
		 * @param keyId the keyId to set
		 */
		public void setKeyId(String keyId) {
			this.keyId = keyId;
		}

		/**
		 * Gets the jwk set url.
		 *
		 * @return the jwkSetUrl
		 */
		public String getJwkSetUrl() {
			return jwkSetUrl;
		}

		/**
		 * Sets the jwk set url.
		 *
		 * @param jwkSetUrl the jwkSetUrl to set
		 */
		public void setJwkSetUrl(String jwkSetUrl) {
			this.jwkSetUrl = jwkSetUrl;
		}

	}

	/**
	 * The Class JwtClaim.
	 */
	public static class JwtClaim {

		/** The id. */
		@SerializedName("jti")
		private String id;

		/** The given name. */
		@SerializedName("given_name")
		private String givenName;

		/** The family name. */
		@SerializedName("family_name")
		private String familyName;

		/** The scope. */
		private List<String> scope;

		/** The client id. */
		@SerializedName("client_id")
		private String clientId;

		/** The grant type. */
		@SerializedName("grant_type")
		private String grantType;

		/** The user id. */
		@SerializedName("user_id")
		private String userId;

		/** The user name. */
		@SerializedName("user_name")
		private String userName;

		/** The email. */
		@SerializedName("email")
		private String email;

		/** The auth time. */
		@SerializedName("auth_time")
		private long authTime;

		/** The issued at. */
		@SerializedName("iat")
		private long issuedAt;

		/** The expirantion time. */
		@SerializedName("exp")
		private long expirantionTime;

		/** The issuer. */
		@SerializedName("iss")
		private String issuer;

		/** The audience. */
		@SerializedName("aud")
		private List<String> audience;

		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * Sets the id.
		 *
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * Gets the given name.
		 *
		 * @return the givenName
		 */
		public String getGivenName() {
			return givenName;
		}

		/**
		 * Sets the given name.
		 *
		 * @param givenName the givenName to set
		 */
		public void setGivenName(String givenName) {
			this.givenName = givenName;
		}

		/**
		 * Gets the family name.
		 *
		 * @return the familyName
		 */
		public String getFamilyName() {
			return familyName;
		}

		/**
		 * Sets the family name.
		 *
		 * @param familyName the familyName to set
		 */
		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}

		/**
		 * Gets the scope.
		 *
		 * @return the scope
		 */
		public List<String> getScope() {
			return scope;
		}

		/**
		 * Sets the scope.
		 *
		 * @param scope the scope to set
		 */
		public void setScope(List<String> scope) {
			this.scope = scope;
		}

		/**
		 * Gets the client id.
		 *
		 * @return the clientId
		 */
		public String getClientId() {
			return clientId;
		}

		/**
		 * Sets the client id.
		 *
		 * @param clientId the clientId to set
		 */
		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		/**
		 * Gets the grant type.
		 *
		 * @return the grantType
		 */
		public String getGrantType() {
			return grantType;
		}

		/**
		 * Sets the grant type.
		 *
		 * @param grantType the grantType to set
		 */
		public void setGrantType(String grantType) {
			this.grantType = grantType;
		}

		/**
		 * Gets the user id.
		 *
		 * @return the userId
		 */
		public String getUserId() {
			return userId;
		}

		/**
		 * Sets the user id.
		 *
		 * @param userId the userId to set
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}

		/**
		 * Gets the user name.
		 *
		 * @return the userName
		 */
		public String getUserName() {
			return userName;
		}

		/**
		 * Sets the user name.
		 *
		 * @param userName the userName to set
		 */
		public void setUserName(String userName) {
			this.userName = userName;
		}

		/**
		 * Gets the email.
		 *
		 * @return the email
		 */
		public String getEmail() {
			return email;
		}

		/**
		 * Sets the email.
		 *
		 * @param email the email to set
		 */
		public void setEmail(String email) {
			this.email = email;
		}

		/**
		 * Gets the auth time.
		 *
		 * @return the authTime
		 */
		public long getAuthTime() {
			return authTime;
		}

		/**
		 * Sets the auth time.
		 *
		 * @param authTime the authTime to set
		 */
		public void setAuthTime(long authTime) {
			this.authTime = authTime;
		}

		/**
		 * Gets the issued at.
		 *
		 * @return the issuedAt
		 */
		public long getIssuedAt() {
			return issuedAt;
		}

		/**
		 * Sets the issued at.
		 *
		 * @param issuedAt the issuedAt to set
		 */
		public void setIssuedAt(long issuedAt) {
			this.issuedAt = issuedAt;
		}

		/**
		 * Gets the expirantion time.
		 *
		 * @return the expirantionTime
		 */
		public long getExpirantionTime() {
			return expirantionTime;
		}

		/**
		 * Sets the expirantion time.
		 *
		 * @param expirantionTime the expirantionTime to set
		 */
		public void setExpirantionTime(long expirantionTime) {
			this.expirantionTime = expirantionTime;
		}

		/**
		 * Gets the issuer.
		 *
		 * @return the issuer
		 */
		public String getIssuer() {
			return issuer;
		}

		/**
		 * Sets the issuer.
		 *
		 * @param issuer the issuer to set
		 */
		public void setIssuer(String issuer) {
			this.issuer = issuer;
		}

		/**
		 * Gets the audience.
		 *
		 * @return the audience
		 */
		public List<String> getAudience() {
			return audience;
		}

		/**
		 * Sets the audience.
		 *
		 * @param audience the audience to set
		 */
		public void setAudience(List<String> audience) {
			this.audience = audience;
		}

	}

	/**
	 * Gets the token.
	 *
	 * @param jwt the jwt
	 * @param index the index
	 * @return the token
	 */
	private static String getToken(String jwt, int index) {
		return getToken(jwt, index, true);
	}

	/**
	 * Gets the token.
	 *
	 * @param jwt the jwt
	 * @param index the index
	 * @param encode the encode
	 * @return the token
	 */
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

	/**
	 * Checks if is valid jwt.
	 *
	 * @param request the request
	 * @param token the token
	 * @return true, if is valid jwt
	 */
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

	/**
	 * Verify jwt.
	 *
	 * @param token the token
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GeneralSecurityException the general security exception
	 */
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

	/**
	 * Checks if is expired jwt.
	 *
	 * @param request the request
	 * @param token the token
	 * @return true, if is expired jwt
	 */
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

	/**
	 * Gets the session token.
	 *
	 * @param request the request
	 * @return the session token
	 */
	private static String getSessionToken(ServletRequest request) {
		String value = null;
		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session != null) {
			value = (String) session.getAttribute(JWT_SESSION_NAME);
		}
		return value;
	}

	/**
	 * Sets the session token.
	 *
	 * @param request the request
	 * @param value the value
	 */
	private static void setSessionToken(ServletRequest request, String value) {
		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session != null) {
			session.setAttribute(JWT_SESSION_NAME, value);
		}
	}

	/**
	 * Removes the session token.
	 *
	 * @param request the request
	 */
	private static void removeSessionToken(ServletRequest request) {
		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session != null) {
			session.removeAttribute(JWT_SESSION_NAME);
		}
	}

	/**
	 * Gets the public key from string.
	 *
	 * @param key the key
	 * @return the public key from string
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GeneralSecurityException the general security exception
	 */
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
