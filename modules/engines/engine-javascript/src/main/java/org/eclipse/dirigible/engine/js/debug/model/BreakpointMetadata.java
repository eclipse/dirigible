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
package org.eclipse.dirigible.engine.js.debug.model;

public class BreakpointMetadata implements Comparable<BreakpointMetadata> {

	private static final String SLASH = "/";
	private static final String ROW_D_PATH_S = "[row: %d | full path: %s]";
	private String fullPath;
	private Integer row;

	public BreakpointMetadata(String fullPath, Integer row) {
		super();
		this.fullPath = fullPath;
		this.row = row;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public String getFileName() {
		return fullPath.substring(fullPath.lastIndexOf(SLASH) + 1);
	}

	@Override
	public int compareTo(BreakpointMetadata that) {
		int result = 0;
		if (that != null) {
			result = this.getFullPath().compareTo(that.getFullPath());
			if (result == 0) {
				result = this.getRow().compareTo(that.getRow());
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((fullPath == null) ? 0 : fullPath.hashCode());
		result = (prime * result) + ((row == null) ? 0 : row.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BreakpointMetadata other = (BreakpointMetadata) obj;
		if (fullPath == null) {
			if (other.fullPath != null) {
				return false;
			}
		} else if (!fullPath.equals(other.fullPath)) {
			return false;
		}
		if (row == null) {
			if (other.row != null) {
				return false;
			}
		} else if (!row.equals(other.row)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String message = String.format(ROW_D_PATH_S, row, fullPath);
		return message;
	}

}
