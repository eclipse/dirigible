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
package org.eclipse.dirigible.core.workspace.api;

/**
 * The Enum Status.
 */
public enum Status {
	
	/** A - Added (A new file has been added to the repository) M - Modified (An existing file has been changed) D - Deleted (The file has been deleted but the change has not been committed to the repository yet) U - Untracked (The file is new or has been changed but has not been added to the repository yet) C - Conflict (There is a conflict in the file on repository pull/merge) R - Renamed (The file has been renamed, the change has been added to the repository but has not been committed). */
	
	A, /** The m. */
 M, /** The d. */
 D, /** The u. */
 U, /** The c. */
 C, /** The r. */
 R
	
}
