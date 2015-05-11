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

package org.eclipse.dirigible.repository.ext.conf;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public interface IConfigurationStore {
	
	public Properties getGlobalSettings(String path, String name) throws IOException;
	
	public byte[] getGlobalSettingsAsBytes(String path, String name) throws IOException;
	
	public List<String> listGlobalSettingsNames(String path) throws IOException;
	
	public void removeGlobalSettings(String path, String name) throws IOException;
	
	public void setGlobalSettings(String path, String name, Properties properties) throws IOException;
	
	public void setGlobalSettingsAsBytes(String path, String name, byte[] bytes) throws IOException;
	
	public Properties getUserSettings(String path, String name, String userName) throws IOException;
	
	public byte[] getUserSettingsAsBytes(String path, String name, String userName) throws IOException;
	
	public List<String> listUserSettingsNames(String path, String userName) throws IOException;
	
	public void removeUserSettings(String path, String name, String userName) throws IOException;
	
	public void setUserSettings(String path, String name, Properties properties, String userName) throws IOException;
	
	public void setUserSettingsAsBytes(String path, String name, byte[] bytes, String userName) throws IOException;

	boolean existsGlobalSettings(String path, String name) throws IOException;

	boolean existsUserSettings(String path, String userName, String name)
			throws IOException;

}
