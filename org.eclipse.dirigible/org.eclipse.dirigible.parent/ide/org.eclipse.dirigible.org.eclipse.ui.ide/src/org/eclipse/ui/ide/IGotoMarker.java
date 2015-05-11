/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.ide;

import org.eclipse.core.resources.IMarker;

/**
 * An adapter interface for editors, which allows the editor to reveal the
 * position of a given marker.
 * 
 * @since 3.0
 */
public interface IGotoMarker {
	/**
	 * Sets the cursor and selection state for an editor to reveal the position
	 * of the given marker.
	 * 
	 * @param marker
	 *            the marker
	 */
	public void gotoMarker(IMarker marker);
}
