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

import java.util.ArrayList;
import java.util.List;

/**
 * Entity element from the Entity Data Model.
 */
public class EntityDataModelEntity {
	
	/** The name. */
	private String name;

	/** The data name. */
	private String dataName;
	
	/** The data count. */
	private String dataCount;
	
	/** The data query. */
	private String dataQuery;
	
	/** The type. */
	private String type;
	
	/** The title. */
	private String title;
	
	/** The tooltip. */
	private String tooltip;
	
	/** The icon. */
	private String icon;
	
	/** The menu key. */
	private String menuKey;
	
	/** The menu label. */
	private String menuLabel;
	
	/** The menu index. */
	private String menuIndex;
	
	/** The layout type. */
	private String layoutType;
	
	/** The perspective name. */
	private String perspectiveName;
	
	/** The perspective icon. */
	private String perspectiveIcon;
	
	/** The perspective order. */
	private int perspectiveOrder;
	
	/** The feed url. */
	private String feedUrl;
	
	/** The feed username. */
	private String feedUsername;
	
	/** The feed password. */
	private String feedPassword;
	
	/** The feed schedule. */
	private String feedSchedule;
	
	/** The feed path. */
	private String feedPath;

	/** The role read. */
	private String roleRead;
	
	/** The role write. */
	private String roleWrite;
	
	/** The projection referenced model. */
	private String projectionReferencedModel;
	
	/** The projection referenced entity. */
	private String projectionReferencedEntity;

	/** The properties. */
	private List<EntityDataModelProperty> properties = new ArrayList<EntityDataModelProperty>();
	
	/** The compositions. */
	private List<EntityDataModelComposition> compositions = new ArrayList<EntityDataModelComposition>();

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
	 * Gets the data name.
	 *
	 * @return the dataName
	 */
	public String getDataName() {
		return dataName;
	}

	/**
	 * Sets the data name.
	 *
	 * @param dataName the dataName to set
	 */
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	
	/**
	 * Gets the data count.
	 *
	 * @return the dataCount
	 */
	public String getDataCount() {
		return dataCount;
	}

	/**
	 * Sets the data count.
	 *
	 * @param dataCount the dataCount to set
	 */
	public void setDataCount(String dataCount) {
		this.dataCount = dataCount;
	}
	
	/**
	 * Gets the data query.
	 *
	 * @return the dataQuery
	 */
	public String getDataQuery() {
		return dataQuery;
	}

	/**
	 * Sets the data query.
	 *
	 * @param dataQuery the dataQuery to set
	 */
	public void setDataQuery(String dataQuery) {
		this.dataQuery = dataQuery;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the tooltip.
	 *
	 * @return the tooltip
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * Sets the tooltip.
	 *
	 * @param tooltip the tooltip to set
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
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
	 * Gets the menu key.
	 *
	 * @return the menuKey
	 */
	public String getMenuKey() {
		return menuKey;
	}

	/**
	 * Sets the menu key.
	 *
	 * @param menuKey the menuKey to set
	 */
	public void setMenuKey(String menuKey) {
		this.menuKey = menuKey;
	}

	/**
	 * Gets the menu label.
	 *
	 * @return the menuLabel
	 */
	public String getMenuLabel() {
		return menuLabel;
	}

	/**
	 * Sets the menu label.
	 *
	 * @param menuLabel the menuLabel to set
	 */
	public void setMenuLabel(String menuLabel) {
		this.menuLabel = menuLabel;
	}
	
	/**
	 * Gets the menu index.
	 *
	 * @return the menuIndex
	 */
	public String getMenuIndex() {
		return menuIndex;
	}

	/**
	 * Sets the menu index.
	 *
	 * @param menuIndex the menuIndex to set
	 */
	public void setMenuIndex(String menuIndex) {
		this.menuIndex = menuIndex;
	}

	/**
	 * Gets the layout type.
	 *
	 * @return the layoutType
	 */
	public String getLayoutType() {
		return layoutType;
	}

	/**
	 * Sets the layout type.
	 *
	 * @param layoutType the layoutType to set
	 */
	public void setLayoutType(String layoutType) {
		this.layoutType = layoutType;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public List<EntityDataModelProperty> getProperties() {
		return properties;
	}

	/**
	 * Sets the properties.
	 *
	 * @param properties the properties to set
	 */
	public void setProperties(List<EntityDataModelProperty> properties) {
		this.properties = properties;
	}
	
	/**
	 * Gets the compositions.
	 *
	 * @return the compositions
	 */
	public List<EntityDataModelComposition> getCompositions() {
		return compositions;
	}

	/**
	 * Sets the compositions.
	 *
	 * @param compositions the compositions to set
	 */
	public void setCompositions(List<EntityDataModelComposition> compositions) {
		this.compositions = compositions;
	}

	/**
	 * Gets the perspective name.
	 *
	 * @return the perspectiveName
	 */
	public String getPerspectiveName() {
		return perspectiveName;
	}

	/**
	 * Sets the perspective name.
	 *
	 * @param perspectiveName the perspectiveName to set
	 */
	public void setPerspectiveName(String perspectiveName) {
		this.perspectiveName = perspectiveName;
	}

	/**
	 * Gets the perspective icon.
	 *
	 * @return the perspectiveIcon
	 */
	public String getPerspectiveIcon() {
		return perspectiveIcon;
	}

	/**
	 * Sets the perspective icon.
	 *
	 * @param perspectiveIcon the perspectiveIcon to set
	 */
	public void setPerspectiveIcon(String perspectiveIcon) {
		this.perspectiveIcon = perspectiveIcon;
	}

	/**
	 * Gets the perspective order.
	 *
	 * @return the perspectiveOrder
	 */
	public int getPerspectiveOrder() {
		return perspectiveOrder;
	}

	/**
	 * Sets the perspective order.
	 *
	 * @param perspectiveOrder the perspectiveOrder to set
	 */
	public void setPerspectiveOrder(int perspectiveOrder) {
		this.perspectiveOrder = perspectiveOrder;
	}

	/**
	 * Gets the feed url.
	 *
	 * @return the feedUrl
	 */
	public String getFeedUrl() {
		return feedUrl;
	}

	/**
	 * Sets the feed url.
	 *
	 * @param feedUrl the feedUrl to set
	 */
	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}

	/**
	 * Gets the feed username.
	 *
	 * @return the feedUsername
	 */
	public String getFeedUsername() {
		return feedUsername;
	}

	/**
	 * Sets the feed username.
	 *
	 * @param feedUsername the feedUsername to set
	 */
	public void setFeedUsername(String feedUsername) {
		this.feedUsername = feedUsername;
	}

	/**
	 * Gets the feed password.
	 *
	 * @return the feedPassword
	 */
	public String getFeedPassword() {
		return feedPassword;
	}

	/**
	 * Sets the feed password.
	 *
	 * @param feedPassword the feedPassword to set
	 */
	public void setFeedPassword(String feedPassword) {
		this.feedPassword = feedPassword;
	}

	/**
	 * Gets the feed schedule.
	 *
	 * @return the feedSchedule
	 */
	public String getFeedSchedule() {
		return feedSchedule;
	}

	/**
	 * Sets the feed schedule.
	 *
	 * @param feedSchedule the feedSchedule to set
	 */
	public void setFeedSchedule(String feedSchedule) {
		this.feedSchedule = feedSchedule;
	}

	/**
	 * Gets the feed path.
	 *
	 * @return the feedPath
	 */
	public String getFeedPath() {
		return feedPath;
	}

	/**
	 * Sets the feed path.
	 *
	 * @param feedPath the feedPath to set
	 */
	public void setFeedPath(String feedPath) {
		this.feedPath = feedPath;
	}


	/**
	 * Gets the role read.
	 *
	 * @return the roleRead
	 */
	public String getRoleRead() {
		return roleRead;
	}

	/**
	 * Sets the role read.
	 *
	 * @param roleRead the roleRead to set
	 */
	public void setRoleRead(String roleRead) {
		this.roleRead = roleRead;
	}

	/**
	 * Gets the role write.
	 *
	 * @return the roleWrite
	 */
	public String getRoleWrite() {
		return roleWrite;
	}

	/**
	 * Sets the role write.
	 *
	 * @param roleWrite the roleWrite to set
	 */
	public void setRoleWrite(String roleWrite) {
		this.roleWrite = roleWrite;
	}

	/**
	 * Gets the projection referenced model.
	 *
	 * @return the projection referenced model
	 */
	public String getProjectionReferencedModel() {
		return projectionReferencedModel;
	}

	/**
	 * Sets the projection referenced model.
	 *
	 * @param projectionReferencedModel the new projection referenced model
	 */
	public void setProjectionReferencedModel(String projectionReferencedModel) {
		this.projectionReferencedModel = projectionReferencedModel;
	}

	/**
	 * Gets the projection referenced entity.
	 *
	 * @return the projection referenced entity
	 */
	public String getProjectionReferencedEntity() {
		return projectionReferencedEntity;
	}

	/**
	 * Sets the projection referenced entity.
	 *
	 * @param projectionReferencedEntity the new projection referenced entity
	 */
	public void setProjectionReferencedEntity(String projectionReferencedEntity) {
		this.projectionReferencedEntity = projectionReferencedEntity;
	}

}
