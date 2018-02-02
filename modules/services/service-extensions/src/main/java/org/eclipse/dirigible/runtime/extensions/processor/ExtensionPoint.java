/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.extensions.processor;

import java.util.List;

import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;

/**
 * The Extension Point transport object.
 */
public class ExtensionPoint {

	private ExtensionPointDefinition extensionPoint;

	private List<ExtensionDefinition> extensions;

	/**
	 * Instantiates a new extension point.
	 *
	 * @param extensionPoint
	 *            the extension point
	 * @param extensions
	 *            the extensions
	 */
	public ExtensionPoint(ExtensionPointDefinition extensionPoint, List<ExtensionDefinition> extensions) {
		super();
		this.extensionPoint = extensionPoint;
		this.extensions = extensions;
	}

	/**
	 * Gets the extension point.
	 *
	 * @return the extension point
	 */
	public ExtensionPointDefinition getExtensionPoint() {
		return extensionPoint;
	}

	/**
	 * Gets the extensions.
	 *
	 * @return the extensions
	 */
	public List<ExtensionDefinition> getExtensions() {
		return extensions;
	}

}
