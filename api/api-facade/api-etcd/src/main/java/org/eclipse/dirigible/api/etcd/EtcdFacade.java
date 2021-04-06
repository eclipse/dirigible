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
package org.eclipse.dirigible.api.etcd;

import org.eclipse.dirigible.api.etcd.EtcdFacade;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;

import com.google.common.base.Charsets;

public class EtcdFacade implements IScriptingFacade {
	
	private static final String DIRIGIBLE_ETCD_CLIENT_ENDPOINT = "DIRIGIBLE_ETCD_CLIENT_ENDPOINT";

	private static final String CLIENT_ENDPOINT = "http://localhost:2379";
	
	private static final Logger logger = LoggerFactory.getLogger(EtcdFacade.class);

	public static KV getClient() {
		
		String clientEndpoint = Configuration.get(DIRIGIBLE_ETCD_CLIENT_ENDPOINT, CLIENT_ENDPOINT);
		
		Client client = Client.builder().endpoints(clientEndpoint).build();
		
		KV kvClient = client.getKVClient();
		return kvClient;
	}
	
	public static ByteSequence stringToByteSequence(String str) {
		return ByteSequence.from(str, Charsets.UTF_8);
	}
	
	public static ByteSequence byteArrayToByteSequence(byte[] arr) {
		return ByteSequence.from(arr);
	}
	
	public static String byteSequenceToString(ByteSequence value) {
		return value.toString(Charsets.UTF_8);
	}
	
}