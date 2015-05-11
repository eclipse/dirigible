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

package org.eclipse.dirigible.ide.shared.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class EditorUtil {

	private static final String COULD_NOT_FIND_EXTENSION_POINT = Messages.EditorUtil_COULD_NOT_FIND_EXTENSION_POINT;
	private static final String EDITOR_EXTENSION_POINT_ID = "org.eclipse.ui.editors"; //$NON-NLS-1$

	/**
	 * Returns the id of the editor that can process the specified file
	 * extension.
	 * <p>
	 * When more than one editor is available, the first one is returned. If no
	 * editors are found a value of <code>null</code> is returned.
	 * <p>
	 * The <code>extension</code> parameter is provided without a dot (e.g.
	 * 'wsdl' or 'xml').
	 * 
	 * @param extension
	 *            the file extension for which an editor id is requested.
	 * @return a string containing the id of the editor that can process files
	 *         with the specified id, or <code>null</code> if none is available.
	 */
	public static String getEditorIdForExtension(String extension) {
		IExtensionPoint extensionPoint = getExtensionPoint(EDITOR_EXTENSION_POINT_ID);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension ext : extensions) {
			IConfigurationElement[] elements = ext.getConfigurationElements();
			IConfigurationElement[] editorElements = getConfigurationElements(
					elements, "editor"); //$NON-NLS-1$
			for (IConfigurationElement editorElement : editorElements) {
				String extensionsAttribute = editorElement
						.getAttribute("extensions"); //$NON-NLS-1$
				if (extensionsAttribute == null) {
					continue;
				}
				Set<String> values = getValues(extensionsAttribute);
				if (values.contains(extension)) {
					String idAttribute = editorElement.getAttribute("id"); //$NON-NLS-1$
					if (idAttribute != null) {
						return idAttribute;
					}
				}
			}
		}
		return null;
	}

	private static Set<String> getValues(String attribute) {
		Set<String> result = new HashSet<String>();
		extractValues(attribute, result);
		return result;
	}

	private static void extractValues(String text, Set<String> result) {
		if (text == null) {
			return;
		}
		int commaIndex = text.indexOf(',');
		if (commaIndex != -1) {
			String value = text.substring(0, commaIndex);
			result.add(value);
			extractValues(text.substring(commaIndex + 1), result);
		} else {
			result.add(text);
		}
	}

	private static IConfigurationElement[] getConfigurationElements(
			IConfigurationElement[] elements, String name) {
		List<IConfigurationElement> result = new ArrayList<IConfigurationElement>();
		for (IConfigurationElement element : elements) {
			if (name.equals(element.getName())) {
				result.add(element);
			}
		}
		return result.toArray(new IConfigurationElement[0]);
	}

	private static IExtensionPoint getExtensionPoint(String id) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint result = registry.getExtensionPoint(id);
		if (result == null) {
			throw new RuntimeException(COULD_NOT_FIND_EXTENSION_POINT + id);
		}
		return result;
	}

}
