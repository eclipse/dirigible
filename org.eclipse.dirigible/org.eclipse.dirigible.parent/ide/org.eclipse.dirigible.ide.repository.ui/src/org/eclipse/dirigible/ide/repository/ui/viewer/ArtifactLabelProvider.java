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

package org.eclipse.dirigible.ide.repository.ui.viewer;

import org.eclipse.swt.graphics.Image;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IResource;

public class ArtifactLabelProvider extends AbstractArtifactLabelProvider {

	private static final long serialVersionUID = 7784009188278209963L;

	@Override
	public String getText(Object element) {
		if (element instanceof IEntity) {
			return getEntityText((IEntity) element);
		}
		if (element instanceof RepositoryViewerResolveNode) {
			return getResolveNodeText((RepositoryViewerResolveNode) element);
		}
		return null;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IEntity) {
			return getEntityImage((IEntity) element);
		}
		return null;
	}

	protected String getEntityText(IEntity entity) {
		final String name = entity.getName();
		if (name.isEmpty()) {
			return REPOSITORY_ROOT;
		}
		return name;
	}

	protected String getResolveNodeText(RepositoryViewerResolveNode node) {
		return node.getText();
	}

	protected Image getEntityImage(IEntity entity) {
		if (entity instanceof IResource) {
			final String resourceName = ((IResource) entity).getName()
					.toLowerCase();
			return getResourceImage(resourceName);
		}
		if (entity instanceof ICollection) {
			if (entity.getName().isEmpty()) {
				return getRepositoryRootImage((ICollection) entity);
			}
			return getCollectionImage((ICollection) entity);
		}
		return createImage(TYPE_UNKNOWN_ICON_URL);
	}

	protected Image getCollectionImage(ICollection collection) {
		if (collection != null) {
			String name = collection.getName();
			return getCollectionImageByName(name);
		}
		return createImage(TYPE_COLLECTION_ICON_URL);
	}

	protected Image getCollectionImageByName(String name) {
		if (name.equals(ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES)) {
			return createImage(TYPE_COLLECTION_DS_ICON_URL);
		}
		if (name.equals(ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS)) {
			return createImage(TYPE_COLLECTION_EXT_ICON_URL);
		}
		if (name.equals(ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES)) {
			return createImage(TYPE_COLLECTION_IS_ICON_URL);
		}
		if (name.equals(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES)) {
			return createImage(TYPE_COLLECTION_SS_ICON_URL);
		}
		if (name.equals(ICommonConstants.ARTIFACT_TYPE.SECURITY_CONSTRAINTS)) {
			return createImage(TYPE_COLLECTION_SEC_ICON_URL);
		}
		if (name.equals(ICommonConstants.ARTIFACT_TYPE.TEST_CASES)) {
			return createImage(TYPE_COLLECTION_TEST_ICON_URL);
		}
		if (name.equals(ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT)) {
			return createImage(TYPE_COLLECTION_WEB_ICON_URL);
		}
		if (name.equals(ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT)) {
			return createImage(TYPE_COLLECTION_WIKI_ICON_URL);
		}
		return createImage(TYPE_COLLECTION_ICON_URL);
	}

	protected Image getRepositoryRootImage(ICollection collection) {
		return createImage(TYPE_REPOSITORY_ROOT_ICON_URL);
	}

}
