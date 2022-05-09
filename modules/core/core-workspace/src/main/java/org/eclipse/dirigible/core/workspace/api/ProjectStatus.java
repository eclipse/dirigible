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
package org.eclipse.dirigible.core.workspace.api;

import java.util.Set;

public class ProjectStatus {
	
	private Set<String> added;
	
	private Set<String> changed;
	
	private Set<String> removed;
	
	private Set<String> missing;
	
	private Set<String> modified;
	
	private Set<String> conflicting;
	
	private Set<String> untracked;
	
	private Set<String> untrackedFolders;

	public ProjectStatus(Set<String> added, Set<String> changed, Set<String> removed, Set<String> missing,
			Set<String> modified, Set<String> conflicting, Set<String> untracked, Set<String> untrackedFolders) {
		super();
		this.added = added;
		this.changed = changed;
		this.removed = removed;
		this.missing = missing;
		this.modified = modified;
		this.conflicting = conflicting;
		this.untracked = untracked;
		this.untrackedFolders = untrackedFolders;
	}

	public Set<String> getAdded() {
		return added;
	}

	public Set<String> getChanged() {
		return changed;
	}

	public Set<String> getRemoved() {
		return removed;
	}

	public Set<String> getMissing() {
		return missing;
	}

	public Set<String> getModified() {
		return modified;
	}

	public Set<String> getConflicting() {
		return conflicting;
	}

	public Set<String> getUntracked() {
		return untracked;
	}
	
	public Set<String> getUntrackedFolders() {
		return untrackedFolders;
	}

}
