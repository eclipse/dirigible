/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.test;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "PROCESSES")
public class Process {

	enum ProcessType {
		STARTED, STOPPED, FAILED, INPROGRESS
	}

	@Id
	@GeneratedValue
	@Column(name = "PROCESS_ID", columnDefinition = "BIGINT", nullable = false)
	private long id;

	@Column(name = "PROCESS_NAME", columnDefinition = "VARCHAR", nullable = false, length = 512)
	private String name;

	@Column(name = "PROCESS_TYPE_AS_STRING")
	@Enumerated(EnumType.STRING)
	private ProcessType typeAsString;

	@Column(name = "PROCESS_TYPE_AS_INT")
	@Enumerated(EnumType.ORDINAL)
	private ProcessType typeAsInt;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProcessType getTypeAsString() {
		return typeAsString;
	}

	public void setTypeAsString(ProcessType typeAsString) {
		this.typeAsString = typeAsString;
	}

	public ProcessType getTypeAsInt() {
		return typeAsInt;
	}

	public void setTypeAsInt(ProcessType typeAsInt) {
		this.typeAsInt = typeAsInt;
	}

}
