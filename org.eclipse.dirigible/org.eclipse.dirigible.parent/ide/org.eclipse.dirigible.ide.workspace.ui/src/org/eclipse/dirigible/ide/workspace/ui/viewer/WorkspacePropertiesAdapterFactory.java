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

package org.eclipse.dirigible.ide.workspace.ui.viewer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.repository.ui.viewer.ArtifactPropertySource;
import org.eclipse.dirigible.repository.api.IEntity;

public class WorkspacePropertiesAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof IResource) {
			final IResource resource = (IResource) adaptableObject;
			if (adapterType.equals(IPropertySource.class)) {
				IEntity entity = RepositoryFacade.getInstance().getRepository()
						.getResource(resource.getRawLocation().toString());
				return new ArtifactPropertySource(entity);
			}
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
		return new Class<?>[] { IPropertySource.class };
	}

}
