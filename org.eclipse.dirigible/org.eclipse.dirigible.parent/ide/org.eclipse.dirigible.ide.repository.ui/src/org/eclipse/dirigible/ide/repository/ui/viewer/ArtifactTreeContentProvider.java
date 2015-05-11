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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.logging.Logger;

public class ArtifactTreeContentProvider implements ITreeContentProvider {

	private static final long serialVersionUID = 5108751356542487531L;
	
	private static final String COULD_NOT_CHECK_IF_COLLECTION_HAS_CHILDREN = Messages.ArtifactTreeContentProvider_COULD_NOT_CHECK_IF_COLLECTION_HAS_CHILDREN;
	private static final String ERROR_CLICK_TO_RETRY = Messages.ArtifactTreeContentProvider_ERROR_CLICK_TO_RETRY;
	private static final String COULD_NOT_RESOLVE_COLLECTION_S_CHILDREN = Messages.ArtifactTreeContentProvider_COULD_NOT_RESOLVE_COLLECTION_S_CHILDREN;
	private static final Logger log = Logger
			.getLogger(ArtifactTreeContentProvider.class.getCanonicalName());

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		return null;
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ICollection) {
			return getCollectionChildren((ICollection) parentElement);
		}
		return new Object[0];
	}

	private Object[] getCollectionChildren(ICollection collection) {
		try {
			final List<IEntity> children = new ArrayList<IEntity>();
			children.addAll(collection.getCollections());
			children.addAll(collection.getResources());
			return children.toArray();
		} catch (IOException ex) {
			log.error(COULD_NOT_RESOLVE_COLLECTION_S_CHILDREN);
			log.error(ArtifactTreeContentProvider.class.getCanonicalName(), ex);
			return new Object[] { new RepositoryViewerResolveNode(collection,
					ERROR_CLICK_TO_RETRY) }; // TODO:
												// I18N
		}
	}

	public Object getParent(Object element) {
		if (element instanceof IEntity) {
			return getEntityParent((IEntity) element);
		}
		if (element instanceof RepositoryViewerResolveNode) {
			return getResolveNodeParent((RepositoryViewerResolveNode) element);
		}
		return null;
	}

	private Object getEntityParent(IEntity entity) {
		return entity.getParent();
	}

	private Object getResolveNodeParent(RepositoryViewerResolveNode node) {
		return node.getParent();
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IEntity) {
			return entityHasChildren((IEntity) element);
		}
		return false;
	}

	private boolean entityHasChildren(IEntity entity) {
		if (entity instanceof ICollection) {
			return collectionHasChildren((ICollection) entity);
		}
		return false;
	}

	private boolean collectionHasChildren(ICollection collection) {
		try {
			return !collection.isEmpty();
		} catch (IOException ex) {
			log.error(COULD_NOT_CHECK_IF_COLLECTION_HAS_CHILDREN);
			log.error(ArtifactTreeContentProvider.class.getCanonicalName(), ex);
			// We give the user a chance to try and expand the node.
			// Any errors will be handled during expand.
			return true;
		}
	}

	public void dispose() {
		//
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//
	}

}
