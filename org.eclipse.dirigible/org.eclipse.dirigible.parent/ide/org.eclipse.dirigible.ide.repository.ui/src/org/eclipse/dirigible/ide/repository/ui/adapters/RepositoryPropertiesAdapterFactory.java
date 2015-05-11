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

package org.eclipse.dirigible.ide.repository.ui.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import org.eclipse.dirigible.ide.repository.ui.viewer.ArtifactPropertySource;
import org.eclipse.dirigible.ide.repository.ui.viewer.RepositoryViewer;
import org.eclipse.dirigible.repository.api.IEntity;

/**
 * This factory allows for each selected entity in the {@link RepositoryViewer}
 * viewer, corresponding properties to be displayed in the Properties view.
 * 
 */
public class RepositoryPropertiesAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof IEntity) {
			final IEntity entity = (IEntity) adaptableObject;
			if (adapterType.equals(IPropertySource.class)) {
				return new ArtifactPropertySource(entity);
			}
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
		return new Class<?>[] { IPropertySource.class };
	}

}
