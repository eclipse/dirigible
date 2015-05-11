/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.ide.registry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * A strategy to project nature image extensions from the registry.
 */
public class ProjectImageRegistryReader extends IDERegistryReader {
	private static final String TAG_IMAGE = "image";//$NON-NLS-1$

	private static final String ATT_ID = "id";//$NON-NLS-1$

	private static final String ATT_NATURE_ID = "natureId";//$NON-NLS-1$

	private static final String ATT_ICON = "icon";//$NON-NLS-1$

	private ProjectImageRegistry registry;

	/**
	 * Reads the contents of the given element
	 */
	protected boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(TAG_IMAGE)) {
			return false;
		}

		String id = element.getAttribute(ATT_ID);
		if (id == null) {
			logMissingAttribute(element, ATT_ID);
			return true;
		}

		String natureId = element.getAttribute(ATT_NATURE_ID);
		if (natureId == null) {
			logMissingAttribute(element, ATT_NATURE_ID);
			return true;
		}

		String icon = element.getAttribute(ATT_ICON);
		if (icon == null) {
			logMissingAttribute(element, ATT_ICON);
			return true;
		}
		@SuppressWarnings("deprecation")
		String extendingPluginId = element.getNamespace();
		ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin(
				extendingPluginId, icon);

		if (image != null) {
			registry.setNatureImage(natureId, image);
		}

		return true;
	}

	/**
	 * Read the project nature images within a registry.
	 */
	public void readProjectNatureImages(IExtensionRegistry in,
			ProjectImageRegistry out) {
		registry = out;
		readRegistry(in, IDEWorkbenchPlugin.IDE_WORKBENCH,
				IDEWorkbenchPlugin.PL_PROJECT_NATURE_IMAGES);
	}
}
