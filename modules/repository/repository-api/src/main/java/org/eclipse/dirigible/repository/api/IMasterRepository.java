/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.repository.api;

/**
 * Represents the Master Repository, which is used as an read only image for initial load or reset.
 */
public interface IMasterRepository extends IRepositoryReader {

	/** The Constant DIRIGIBLE_MASTER_REPOSITORY_PROVIDER. */
	public static final String DIRIGIBLE_MASTER_REPOSITORY_PROVIDER = "DIRIGIBLE_MASTER_REPOSITORY_PROVIDER"; //$NON-NLS-1$

}
