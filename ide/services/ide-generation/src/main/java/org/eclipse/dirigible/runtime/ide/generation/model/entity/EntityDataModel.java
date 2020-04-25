/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.ide.generation.model.entity;

/**
 * Transport object for the Entity Data Model
 *
 */
public class EntityDataModel {
	
	private EntityDataModelRoot model;

	/**
	 * @return the model
	 */
	public EntityDataModelRoot getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(EntityDataModelRoot model) {
		this.model = model;
	}

}
