/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.ide.generation.model.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity element from the Entity Data Model
 *
 */
public class EntityDataModelEntity {
	
	private String name;
	
	private String dataName;
	
	private String dataCount;
	
	private String dataQuery;
	
	private String type;
	
	private String title;
	
	private String tooltip;
	
	private String icon;
	
	private String menuKey;
	
	private String menuLabel;
	
	private String menuIndex;
	
	private String layoutType;
	
	private String perspectiveName;
	
	private String perspectiveIcon;
	
	private int perspectiveOrder;
	
	private List<EntityDataModelProperty> properties = new ArrayList<EntityDataModelProperty>();
	
	private List<EntityDataModelComposition> compositions = new ArrayList<EntityDataModelComposition>();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the dataName
	 */
	public String getDataName() {
		return dataName;
	}

	/**
	 * @param dataName the dataName to set
	 */
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	
	/**
	 * @return the dataCount
	 */
	public String getDataCount() {
		return dataCount;
	}

	/**
	 * @param dataCount the dataCount to set
	 */
	public void setDataCount(String dataCount) {
		this.dataCount = dataCount;
	}
	
	/**
	 * @return the dataQuery
	 */
	public String getDataQuery() {
		return dataQuery;
	}

	/**
	 * @param dataQuery the dataQuery to set
	 */
	public void setDataQuery(String dataQuery) {
		this.dataQuery = dataQuery;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the tooltip
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * @param tooltip the tooltip to set
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the menuKey
	 */
	public String getMenuKey() {
		return menuKey;
	}

	/**
	 * @param menuKey the menuKey to set
	 */
	public void setMenuKey(String menuKey) {
		this.menuKey = menuKey;
	}

	/**
	 * @return the menuLabel
	 */
	public String getMenuLabel() {
		return menuLabel;
	}

	/**
	 * @param menuLabel the menuLabel to set
	 */
	public void setMenuLabel(String menuLabel) {
		this.menuLabel = menuLabel;
	}
	
	/**
	 * @return the menuIndex
	 */
	public String getMenuIndex() {
		return menuIndex;
	}

	/**
	 * @param menuIndex the menuIndex to set
	 */
	public void setMenuIndex(String menuIndex) {
		this.menuIndex = menuIndex;
	}

	/**
	 * @return the layoutType
	 */
	public String getLayoutType() {
		return layoutType;
	}

	/**
	 * @param layoutType the layoutType to set
	 */
	public void setLayoutType(String layoutType) {
		this.layoutType = layoutType;
	}

	/**
	 * @return the properties
	 */
	public List<EntityDataModelProperty> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<EntityDataModelProperty> properties) {
		this.properties = properties;
	}
	
	/**
	 * @return the compositions
	 */
	public List<EntityDataModelComposition> getCompositions() {
		return compositions;
	}

	/**
	 * @param compositions the compositions to set
	 */
	public void setCompositions(List<EntityDataModelComposition> compositions) {
		this.compositions = compositions;
	}

	/**
	 * @return the perspectiveName
	 */
	public String getPerspectiveName() {
		return perspectiveName;
	}

	/**
	 * @param perspectiveName the perspectiveName to set
	 */
	public void setPerspectiveName(String perspectiveName) {
		this.perspectiveName = perspectiveName;
	}

	/**
	 * @return the perspectiveIcon
	 */
	public String getPerspectiveIcon() {
		return perspectiveIcon;
	}

	/**
	 * @param perspectiveIcon the perspectiveIcon to set
	 */
	public void setPerspectiveIcon(String perspectiveIcon) {
		this.perspectiveIcon = perspectiveIcon;
	}

	/**
	 * @return the perspectiveOrder
	 */
	public int getPerspectiveOrder() {
		return perspectiveOrder;
	}

	/**
	 * @param perspectiveOrder the perspectiveOrder to set
	 */
	public void setPerspectiveOrder(int perspectiveOrder) {
		this.perspectiveOrder = perspectiveOrder;
	}
	
}
