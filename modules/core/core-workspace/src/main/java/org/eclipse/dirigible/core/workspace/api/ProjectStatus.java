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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectStatus {
	
	private static final String DIRIGIBLE_FOLDER = "/dirigible/";

	private static final Logger logger = LoggerFactory.getLogger(ProjectStatus.class);
	
	private String project;
	
	private Set<String> added;
	
	private Set<String> changed;
	
	private Set<String> removed;
	
	private Set<String> missing;
	
	private Set<String> modified;
	
	private Set<String> conflicting;
	
	private Set<String> untracked;
	
	private Set<String> untrackedFolders;

	public ProjectStatus(String project, Set<String> added, Set<String> changed, Set<String> removed, Set<String> missing,
			Set<String> modified, Set<String> conflicting, Set<String> untracked, Set<String> untrackedFolders) {
		super();
		this.project = project;
		this.added = remapFilesIfNeeded(added, project);
		this.changed = remapFilesIfNeeded(changed, project);
		this.removed = remapFilesIfNeeded(removed, project);
		this.missing = remapFilesIfNeeded(missing, project);
		this.modified = remapFilesIfNeeded(modified, project);
		this.conflicting = remapFilesIfNeeded(conflicting, project);
		this.untracked = remapFilesIfNeeded(untracked, project);
		this.untrackedFolders = remapFilesIfNeeded(untrackedFolders, project);
	}
	
	public String getProject() {
		return project;
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
	
	private Set<String> remapFilesIfNeeded(Set<String> originals, String project) {
		Set<String> result = new HashSet<String>();
		for (String original : originals) {
			if (original.startsWith(project)) {
				result.add(original);
			} else {
				String search = DIRIGIBLE_FOLDER + project;
				int index = original.indexOf(search);
				if (index > 0) {
					result.add(original.substring(index + DIRIGIBLE_FOLDER.length()));
				} else {
					logger.error("Wrong layout of project: " + project);
					result.add(original);
					
				}
			}
		}
		return result;
	}

}
