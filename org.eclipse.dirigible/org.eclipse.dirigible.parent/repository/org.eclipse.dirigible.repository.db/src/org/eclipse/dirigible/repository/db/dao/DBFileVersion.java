/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.db.dao;

import org.eclipse.dirigible.repository.db.DBBaseException;
import org.eclipse.dirigible.repository.db.DBRepository;

/**
 * Internal representation of a File/Resource kind of object
 * 
 */
public class DBFileVersion extends DBFile {

	private int version;

	private byte[] bytes;

	public DBFileVersion(DBRepository repository, boolean isBinary,
			String contentType, int version, byte[] bytes) {
		super(repository, isBinary, contentType);
		this.version = version;
		this.bytes = bytes;
	}

	@Override
	public byte[] getData() throws DBBaseException {
		byte[] data = this.bytes;
		return data;
	}

	public int getVersion() {
		return version;
	}

}
