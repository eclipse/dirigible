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

package org.eclipse.dirigible.ide.publish;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

public interface IPublisher {

	public void publish(IProject project) throws PublishException;
	
	public void activate(IProject project) throws PublishException;
	
	public void activateFile(IFile file) throws PublishException;

	// returns the name of the folder recognizable by the specific publisher
	public String getFolderType();

	public boolean recognizedFile(IFile file);

	public String getPublishedLocation(IFile file);

	public String getPublishedEndpoint(IFile file);
	
	public String getPublishedContainerMapping(IFile file);
	
	public String getActivatedLocation(IFile file);

	public String getActivatedEndpoint(IFile file);

	public String getActivatedContainerMapping(IFile file);

	public boolean isAutoActivationAllowed();

	public String getDebugEndpoint(IFile file);

}
