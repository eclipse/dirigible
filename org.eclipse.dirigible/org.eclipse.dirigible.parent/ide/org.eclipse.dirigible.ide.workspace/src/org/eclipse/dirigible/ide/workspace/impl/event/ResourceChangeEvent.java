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

package org.eclipse.dirigible.ide.workspace.impl.event;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;

public class ResourceChangeEvent implements IResourceChangeEvent {

	private final IResource resource;

	private final int type;

	public ResourceChangeEvent(IResource resource, int type) {
		this.resource = resource;
		this.type = type;
	}

	public int getBuildKind() {
		return 0;
		// throw new UnsupportedOperationException();
	}

	public IResourceDelta getDelta() {
		return null;
		// throw new UnsupportedOperationException();
	}

	public IResource getResource() {
		return resource;
	}

	public Object getSource() {
		throw new UnsupportedOperationException();
	}

	public int getType() {
		return type;
	}

	public IMarkerDelta[] findMarkerDeltas(String type, boolean includeSubtypes) {
		throw new UnsupportedOperationException();
	}

}
