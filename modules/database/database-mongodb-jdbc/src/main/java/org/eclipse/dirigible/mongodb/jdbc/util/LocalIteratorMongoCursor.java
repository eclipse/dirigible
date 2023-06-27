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
package org.eclipse.dirigible.mongodb.jdbc.util;

import java.util.Iterator;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;

public class LocalIteratorMongoCursor implements MongoCursor<String> {

	Iterator<String> iterator;
	
	LocalIteratorMongoCursor(Iterator<String> iterator){
		this.iterator = iterator;
	}
	
	@Override
	public void close() {}
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}
	@Override
	public String next() {
		return iterator.next();
	}
	@Override
	public String tryNext() {
		return null;
	}
	@Override
	public ServerCursor getServerCursor() {
		return null;
	}
	@Override
	public ServerAddress getServerAddress() {
		return null;
	}

}
