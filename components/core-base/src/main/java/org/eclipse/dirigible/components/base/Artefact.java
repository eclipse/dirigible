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
package org.eclipse.dirigible.components.base;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.DatatypeConverter;

@MappedSuperclass
public class Artefact extends Auditable<String> implements Serializable {
	
	@Column(name = "CHECKSUM", columnDefinition = "VARCHAR", nullable = true, length = 32)
    protected String checksum;

	/**
	 * @return the checksum
	 */
	public String getChecksum() {
		return checksum;
	}



	/**
	 * @param checksum the checksum to set
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public void updateChecksum(byte[] content) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(content);
		    byte[] digest = md.digest();
		    this.checksum = DatatypeConverter.printHexBinary(digest).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			this.checksum = "";
		}
	}
	

}
