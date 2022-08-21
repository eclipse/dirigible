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
 * The Class EntityDataModelProperty.
 */
public class EntityDataModelProperty {
	
	/** The name. */
	private String name;
	
	/** The is calculated property. */
	private Boolean isCalculatedProperty;
	
	/** The calculated property expression. */
	private String calculatedPropertyExpression;

	/** The data name. */
	private String dataName;
	
	/** The data type. */
	private String dataType;
	
	/** The data length. */
	private String dataLength;
	
	/** The data default value. */
	private String dataDefaultValue;
	
	/** The data primary key. */
	private Boolean dataPrimaryKey;
	
	/** The data auto increment. */
	private Boolean dataAutoIncrement;
	
	/** The data nullable. */
	private Boolean dataNullable;
	
	/** The data unique. */
	private Boolean dataUnique;
	
	/** The data precision. */
	private String dataPrecision;
	
	/** The data scale. */
	private String dataScale;

	/** The relationship type. */
	private String relationshipType;
	
	/** The relationship cardinality. */
	private String relationshipCardinality;
	
	/** The relationship name. */
	private String relationshipName;
	
	/** The relationship entity name. */
	private String relationshipEntityName;
	
	/** The relationship entity perspective name. */
	private String relationshipEntityPerspectiveName;
	
	/** The widget type. */
	private String widgetType;
	
	/** The widget length. */
	private String widgetLength;
	
	/** The widget pattern. */
	private String widgetPattern;
	
	/** The widget service. */
	private String widgetService;
	
	/** The widget label. */
	private String widgetLabel;
	
	/** The widget is major. */
	private Boolean widgetIsMajor;
	
	/** The widget section. */
	private Boolean widgetSection;
	
	/** The widget short label. */
	private Boolean widgetShortLabel;
	
	/** The widget format. */
	private Boolean widgetFormat;
	
	/** The widget drop down key. */
	private String widgetDropDownKey;
	
	/** The widget drop down value. */
	private String widgetDropDownValue;

	/** The feed property name. */
	private String feedPropertyName;

	/** The role read. */
	private String roleRead;
	
	/** The role write. */
	private String roleWrite;

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
	 * Gets the checks if is calculated property.
	 *
	 * @return the isCalculatedProperty
	 */
	public Boolean getIsCalculatedProperty() {
		return isCalculatedProperty;
	}

	/**
	 * Sets the checks if is calculated property.
	 *
	 * @param isCalculatedProperty the isCalculatedProperty to set
	 */
	public void setIsCalculatedProperty(Boolean isCalculatedProperty) {
		this.isCalculatedProperty = isCalculatedProperty;
	}

	/**
	 * Gets the calculated property expression.
	 *
	 * @return the calculatedPropertyExpression
	 */
	public String getCalculatedPropertyExpression() {
		return calculatedPropertyExpression;
	}

	/**
	 * Sets the calculated property expression.
	 *
	 * @param calculatedPropertyExpression the calculatedPropertyExpression to set
	 */
	public void setCalculatedPropertyExpression(String calculatedPropertyExpression) {
		this.calculatedPropertyExpression = calculatedPropertyExpression;
	}

	/**
	 * Gets the widget is major.
	 *
	 * @return the widgetIsMajor
	 */
	public Boolean getWidgetIsMajor() {
		return widgetIsMajor;
	}

	/**
	 * Sets the widget is major.
	 *
	 * @param widgetIsMajor the widgetIsMajor to set
	 */
	public void setWidgetIsMajor(Boolean widgetIsMajor) {
		this.widgetIsMajor = widgetIsMajor;
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
	 * Gets the data type.
	 *
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Sets the data type.
	 *
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * Gets the data length.
	 *
	 * @return the dataLength
	 */
	public String getDataLength() {
		return dataLength;
	}

	/**
	 * Sets the data length.
	 *
	 * @param dataLength the dataLength to set
	 */
	public void setDataLength(String dataLength) {
		this.dataLength = dataLength;
	}

	/**
	 * Gets the data default value.
	 *
	 * @return the dataDefaultValue
	 */
	public String getDataDefaultValue() {
		return dataDefaultValue;
	}

	/**
	 * Sets the data default value.
	 *
	 * @param dataDefaultValue the dataDefaultValue to set
	 */
	public void setDataDefaultValue(String dataDefaultValue) {
		this.dataDefaultValue = dataDefaultValue;
	}

	/**
	 * Gets the data primary key.
	 *
	 * @return the dataPrimaryKey
	 */
	public Boolean getDataPrimaryKey() {
		return dataPrimaryKey;
	}

	/**
	 * Sets the data primary key.
	 *
	 * @param dataPrimaryKey the dataPrimaryKey to set
	 */
	public void setDataPrimaryKey(Boolean dataPrimaryKey) {
		this.dataPrimaryKey = dataPrimaryKey;
	}

	/**
	 * Gets the data auto increment.
	 *
	 * @return the dataAutoIncrement
	 */
	public Boolean getDataAutoIncrement() {
		return dataAutoIncrement;
	}

	/**
	 * Sets the data auto increment.
	 *
	 * @param dataAutoIncrement the dataAutoIncrement to set
	 */
	public void setDataAutoIncrement(Boolean dataAutoIncrement) {
		this.dataAutoIncrement = dataAutoIncrement;
	}

	/**
	 * Gets the data nullable.
	 *
	 * @return the dataNullable
	 */
	public Boolean getDataNullable() {
		return dataNullable;
	}

	/**
	 * Sets the data nullable.
	 *
	 * @param dataNullable the dataNullable to set
	 */
	public void setDataNullable(Boolean dataNullable) {
		this.dataNullable = dataNullable;
	}

	/**
	 * Gets the data unique.
	 *
	 * @return the dataUnique
	 */
	public Boolean getDataUnique() {
		return dataUnique;
	}

	/**
	 * Sets the data unique.
	 *
	 * @param dataUnique the dataUnique to set
	 */
	public void setDataUnique(Boolean dataUnique) {
		this.dataUnique = dataUnique;
	}

	/**
	 * Gets the data precision.
	 *
	 * @return the dataPrecision
	 */
	public String getDataPrecision() {
		return dataPrecision;
	}

	/**
	 * Sets the data precision.
	 *
	 * @param dataPrecision the dataPrecision to set
	 */
	public void setDataPrecision(String dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	/**
	 * Gets the data scale.
	 *
	 * @return the dataScale
	 */
	public String getDataScale() {
		return dataScale;
	}

	/**
	 * Sets the data scale.
	 *
	 * @param dataScale the dataScale to set
	 */
	public void setDataScale(String dataScale) {
		this.dataScale = dataScale;
	}

	/**
	 * Gets the relationship type.
	 *
	 * @return the relationshipType
	 */
	public String getRelationshipType() {
		return relationshipType;
	}

	/**
	 * Sets the relationship type.
	 *
	 * @param relationshipType the relationshipType to set
	 */
	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	/**
	 * Gets the relationship cardinality.
	 *
	 * @return the relationshipCardinality
	 */
	public String getRelationshipCardinality() {
		return relationshipCardinality;
	}

	/**
	 * Sets the relationship cardinality.
	 *
	 * @param relationshipCardinality the relationshipCardinality to set
	 */
	public void setRelationshipCardinality(String relationshipCardinality) {
		this.relationshipCardinality = relationshipCardinality;
	}

	/**
	 * Gets the relationship name.
	 *
	 * @return the relationshipName
	 */
	public String getRelationshipName() {
		return relationshipName;
	}

	/**
	 * Sets the relationship name.
	 *
	 * @param relationshipName the relationshipName to set
	 */
	public void setRelationshipName(String relationshipName) {
		this.relationshipName = relationshipName;
	}
	
	/**
	 * Gets the relationship entity name.
	 *
	 * @return the relationshipEntityName
	 */
	public String getRelationshipEntityName() {
		return relationshipEntityName;
	}

	/**
	 * Sets the relationship entity name.
	 *
	 * @param relationshipEntityName the relationship entity name to set
	 */
	public void setRelationshipEntityName(String relationshipEntityName) {
		this.relationshipEntityName = relationshipEntityName;
	}

	/**
	 * Gets the relationship entity perspective name.
	 *
	 * @return the relationshipEntityPerspectiveName
	 */
	public String getRelationshipEntityPerspectiveName() {
		return relationshipEntityPerspectiveName;
	}

	/**
	 * Sets the relationship entity perspective name.
	 *
	 * @param relationshipEntityPerspectiveName the relationship entity perspective name to set
	 */
	public void setRelationshipEntityPerspectiveName(String relationshipEntityPerspectiveName) {
		this.relationshipEntityPerspectiveName = relationshipEntityPerspectiveName;
	}

	/**
	 * Gets the widget type.
	 *
	 * @return the widgetType
	 */
	public String getWidgetType() {
		return widgetType;
	}

	/**
	 * Sets the widget type.
	 *
	 * @param widgetType the widgetType to set
	 */
	public void setWidgetType(String widgetType) {
		this.widgetType = widgetType;
	}

	/**
	 * Gets the widget length.
	 *
	 * @return the widgetLength
	 */
	public String getWidgetLength() {
		return widgetLength;
	}

	/**
	 * Sets the widget length.
	 *
	 * @param widgetLength the widgetLength to set
	 */
	public void setWidgetLength(String widgetLength) {
		this.widgetLength = widgetLength;
	}

	/**
	 * Gets the widget pattern.
	 *
	 * @return the widgetPattern
	 */
	public String getWidgetPattern() {
		return widgetPattern;
	}

	/**
	 * Sets the widget pattern.
	 *
	 * @param widgetPattern the widgetPattern to set
	 */
	public void setWidgetPattern(String widgetPattern) {
		this.widgetPattern = widgetPattern;
	}

	/**
	 * Gets the widget service.
	 *
	 * @return the widgetService
	 */
	public String getWidgetService() {
		return widgetService;
	}

	/**
	 * Sets the widget service.
	 *
	 * @param widgetService the widgetService to set
	 */
	public void setWidgetService(String widgetService) {
		this.widgetService = widgetService;
	}

	/**
	 * Gets the widget label.
	 *
	 * @return the widgetLabel
	 */
	public String getWidgetLabel() {
		return widgetLabel;
	}

	/**
	 * Sets the widget label.
	 *
	 * @param widgetLabel the widgetLabel to set
	 */
	public void setWidgetLabel(String widgetLabel) {
		this.widgetLabel = widgetLabel;
	}

	/**
	 * Gets the widget section.
	 *
	 * @return the widget section
	 */
	public Object getWidgetSection() {
		return widgetSection;
	}
	
	/**
	 * Sets the widget section.
	 *
	 * @param widgetSection the widget section
	 */
	public void setWidgetSection(Boolean widgetSection) {
		this.widgetSection = widgetSection;
	}

	/**
	 * Gets the widget short label.
	 *
	 * @return the widget short label
	 */
	public Boolean getWidgetShortLabel() {
		return widgetShortLabel;
	}

	/**
	 * Sets the widget short label.
	 *
	 * @param widgetShortLabel the widget short label
	 */
	public void setWidgetShortLabel(Boolean widgetShortLabel) {
		this.widgetShortLabel = widgetShortLabel;
	}

	/**
	 * Gets the widget format.
	 *
	 * @return the widget format
	 */
	public Boolean getWidgetFormat() {
		return widgetFormat;
	}

	/**
	 * Sets the widget format.
	 *
	 * @param widgetFormat the widget format
	 */
	public void setWidgetFormat(Boolean widgetFormat) {
		this.widgetFormat = widgetFormat;
	}

	/**
	 * Gets the widget drop down key.
	 *
	 * @return widget drop down key
	 */
	public String getWidgetDropDownKey() {
		return widgetDropDownKey;
	}

	/**
	 * Sets the widget drop down key.
	 *
	 * @param widgetDropDownKey the widget drop down key
	 */
	public void setWidgetDropDownKey(String widgetDropDownKey) {
		this.widgetDropDownKey = widgetDropDownKey;
	}

	/**
	 * Gets the widget drop down value.
	 *
	 * @return widget drop down value
	 */
	public String getWidgetDropDownValue() {
		return widgetDropDownValue;
	}

	/**
	 * Sets the widget drop down value.
	 *
	 * @param widgetDropDownValue the widget drop down value
	 */
	public void setWidgetDropDownValue(String widgetDropDownValue) {
		this.widgetDropDownValue = widgetDropDownValue;
	}

	/**
	 * Gets the feed property name.
	 *
	 * @return the feedPropertyName
	 */
	public String getFeedPropertyName() {
		return feedPropertyName;
	}

	/**
	 * Sets the feed property name.
	 *
	 * @param feedPropertyName the feedPropertyName to set
	 */
	public void setFeedPropertyName(String feedPropertyName) {
		this.feedPropertyName = feedPropertyName;
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
	 * Sets the roles.
	 *
	 * @param roleWrite the roleWrite to set
	 */
	public void setRoles(String roleWrite) {
		this.roleWrite = roleWrite;
	}

}
