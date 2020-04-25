/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.git.model;

import java.util.List;

import org.eclipse.dirigible.core.git.GitBranch;

public class GitProjectRemoteBranches {
	
	private List<GitBranch> remote;

	/**
	 * Getter for the remote branches
	 * 
	 * @return the remote
	 */
	public List<GitBranch> getRemote() {
		return remote;
	}

	/**
	 * Setter for the remote branches
	 * 
	 * @param remote the remote to set
	 */
	public void setRemote(List<GitBranch> remote) {
		this.remote = remote;
	}

}
