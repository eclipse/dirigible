/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.processors.identity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "DIRIGIBLE_IDENTITY")
public class Identity {

	@Id
	@Column(name = "IDENTITY_TABLE", columnDefinition = "VARCHAR", nullable = false, length = 512)
	private String table;

	@Column(name = "IDENTITY_VALUE", columnDefinition = "BIGINT", nullable = false)
	private long value;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

}
