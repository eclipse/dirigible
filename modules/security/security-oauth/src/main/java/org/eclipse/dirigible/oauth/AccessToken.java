/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.oauth;

import com.google.gson.annotations.SerializedName;

/**
 * The Class AccessToken.
 */
public class AccessToken {

	/** The access token. */
	@SerializedName("access_token")
	private String accessToken;

	/** The token type. */
	@SerializedName("token_type")
	private String tokenType;

	/** The id token. */
	@SerializedName("id_token")
	private String idToken;

	/** The refresh token. */
	@SerializedName("refresh_token")
	private String refreshToken;

	/** The expires in. */
	@SerializedName("expires_in")
	private long expiresIn;

	/** The scope. */
	private String scope;

	/** The jti. */
	private String jti;

	/**
	 * Gets the access token.
	 *
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * Sets the access token.
	 *
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Gets the token type.
	 *
	 * @return the tokenType
	 */
	public String getTokenType() {
		return tokenType;
	}

	/**
	 * Sets the token type.
	 *
	 * @param tokenType the tokenType to set
	 */
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	/**
	 * Gets the id token.
	 *
	 * @return the idToken
	 */
	public String getIdToken() {
		return idToken;
	}

	/**
	 * Sets the id token.
	 *
	 * @param idToken the idToken to set
	 */
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	/**
	 * Gets the refresh token.
	 *
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * Sets the refresh token.
	 *
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * Gets the expires in.
	 *
	 * @return the expiresIn
	 */
	public long getExpiresIn() {
		return expiresIn;
	}

	/**
	 * Sets the expires in.
	 *
	 * @param expiresIn the expiresIn to set
	 */
	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Sets the scope.
	 *
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Gets the jti.
	 *
	 * @return the jti
	 */
	public String getJti() {
		return jti;
	}

	/**
	 * Sets the jti.
	 *
	 * @param jti the jti to set
	 */
	public void setJti(String jti) {
		this.jti = jti;
	}

}