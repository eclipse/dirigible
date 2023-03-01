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
package org.eclipse.dirigible.database.ds.test;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class Table1
 *
 */
@Table(name = "TABLE1")
public class Table1 {
	
	/** The id. */
	@Id
	@GeneratedValue
	@Column(name = "TABLE1_ID", columnDefinition = "INTEGER", nullable = false)
	private int id;

	/** The foreign key to table3. */
	@Column(name = "TABLE3_ID", columnDefinition = "INTEGER", nullable = false)
	private String table3;
	
	/** The foreign key to table4. */
	@Column(name = "TABLE4_ID", columnDefinition = "INTEGER", nullable = false)
	private String table4;

}
