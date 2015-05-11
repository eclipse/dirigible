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

package org.eclipse.dirigible.ide.debug.ui;

import java.net.URL;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.eclipse.dirigible.ide.common.image.ImageUtils;

public class AbstractDebugLabelProvider extends LabelProvider {
	
	private static final long serialVersionUID = 4166213744757581077L;
	
	public static final URL DEBUG_SESSION_ICON_URL = getIconURL("debug-session.png"); //$NON-NLS-1$
	public static final URL BREAKPOINT_ICON_URL = getIconURL("breakpoint.png"); //$NON-NLS-1$
	public static final URL VARIABLE_ICON_URL = getIconURL("variable.png"); //$NON-NLS-1$

	public static URL getIconURL(String iconName) {
		URL url = ImageUtils.getIconURL("org.eclipse.dirigible.ide.debug.ui", "/resources/", iconName);
		return url;
	}

	protected Image createImage(URL imageURL) {
		return ImageUtils.createImage(imageURL);
	}

	

}
