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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.mongodb.Block;
import com.mongodb.Function;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

public class ListMongoIterable implements MongoIterable<String> {

	List<String> entries;

	public ListMongoIterable(List<String> backingList){
		this.entries = backingList;
	}
	
	@Override
	public <U> MongoIterable<U> map(Function<String, U> mapper) {
		return null;
	}
	
	@Override
	public MongoCursor<String> iterator() {
		return new LocalIteratorMongoCursor(entries.iterator());
	}				
	@Override
	public <A extends Collection<? super String>> A into(A target) {
		return null;
	}
	@Override
	public void forEach(Block<? super String> block) {
	}
	@Override
	public String first() {
		Iterator<String> iter = this.iterator();
		return iter.hasNext()?this.iterator().next():null;
	}
	@Override
	public MongoIterable<String> batchSize(int batchSize) {
		return this;
	}

	@Override
	public MongoCursor<String> cursor() {
		return null;
	}
	
}
