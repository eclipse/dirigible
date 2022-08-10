/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.ide.generation.model.entity;

/**
 * The Class EntityDataModelPerspective.
 */
public class EntityDataModelPerspective {
	
	/** The name. */
	private String name;
	
	/** The label. */
	private String label;
	
	/** The icon. */
	private String icon;
	
	/** The order. */
	private int order;
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Sets the label.
	 *
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Gets the icon.
	 *
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}
	
	/**
	 * Sets the icon.
	 *
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}
	
	/**
	 * Sets the order.
	 *
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

}
