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

package org.eclipse.dirigible.repository.ext.db;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;

public interface IDataUpdater {
	
	public void executeUpdate(List<String> knownFiles, List<String> errors) throws Exception;
	
	public void executeUpdate(List<String> knownFiles, HttpServletRequest request, List<String> errors) throws Exception;
	
	public void enumerateKnownFiles(ICollection collection, List<String> dsDefinitions) throws IOException;

	public void applyUpdates() throws IOException, Exception;
	
	public IRepository getRepository();
	
	public String getLocation();
}
