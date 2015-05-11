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

package org.eclipse.dirigible.ide.editor.text.editor;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.ui.IEditorInput;

import org.eclipse.dirigible.repository.logging.Logger;

public class ContentProviderFactory {

	private static final String CANNOT_READ_CONTENT_PROVIDER_EXTENSION_CLASS_0_INPUT_TYPE_1 = Messages.ContentProviderFactory_CANNOT_READ_CONTENT_PROVIDER_EXTENSION_CLASS_0_INPUT_TYPE_1;

	private static final String UNSUPPORTED_I_EDITOR_INPUT = Messages.ContentProviderFactory_UNSUPPORTED_I_EDITOR_INPUT;

	private static final Logger LOGGER = Logger
			.getLogger(ContentProviderFactory.class);

	private static final String EXT_POINT_ID = "org.eclipse.dirigible.ide.editor.text.contentProviders"; //$NON-NLS-1$

	private static ContentProviderFactory INSTANCE;

	private Map<String, IContentProvider> contentProviders;

	private ContentProviderFactory() {
		contentProviders = new HashMap<String, IContentProvider>();
		readExtensions();
	}

	public static ContentProviderFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ContentProviderFactory();
		}
		return INSTANCE;
	}

	public IContentProvider getContentProvider(IEditorInput input) {
		Class<? extends IEditorInput> inputClass = input.getClass();

		for (Entry<String, IContentProvider> entry : contentProviders
				.entrySet()) {
			// exact matches
			String entryInputClassName = entry.getKey();
			if (inputClass.getName().equals(entryInputClassName)) {
				return entry.getValue();
			}
		}

		for (Entry<String, IContentProvider> entry : contentProviders
				.entrySet()) {
			String entryInputClassName = entry.getKey();

			if (isInstanceOf(entryInputClassName, inputClass)) {
				return entry.getValue();
			}
		}

		LOGGER.warn(UNSUPPORTED_I_EDITOR_INPUT
				+ input.getClass().getCanonicalName());
		return null;
	}

	@SuppressWarnings("rawtypes")
	private boolean isInstanceOf(String objectClassName, Class clazz) {
		if (clazz.getName().equals(objectClassName)) {
			return true;
		}

		Class superclass = clazz.getSuperclass();
		if (superclass != null && isInstanceOf(objectClassName, superclass)) {
			return true;
		}

		for (Class intf : clazz.getInterfaces()) {
			if (isInstanceOf(objectClassName, intf)) {
				return true;
			}
		}

		return false;
	}

	private void readExtensions() {
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		IConfigurationElement[] configurationElements = registry
				.getConfigurationElementsFor(EXT_POINT_ID);

		for (IConfigurationElement configurationElement : configurationElements) {
			String inputType = configurationElement.getAttribute("inputTypes"); //$NON-NLS-1$

			try {
				contentProviders.put(inputType,
						(IContentProvider) configurationElement
								.createExecutableExtension("class")); //$NON-NLS-1$
			} catch (CoreException e) {
				String msg = MessageFormat
						.format(CANNOT_READ_CONTENT_PROVIDER_EXTENSION_CLASS_0_INPUT_TYPE_1,
								configurationElement.getAttribute("class"), inputType); //$NON-NLS-1$
				LOGGER.error(msg, e);
			}
		}
	}

}
