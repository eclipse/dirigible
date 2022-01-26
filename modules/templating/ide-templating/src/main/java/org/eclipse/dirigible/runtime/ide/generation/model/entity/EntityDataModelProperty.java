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

public class EntityDataModelProperty {
	
	private String name;
	private Boolean isCalculatedProperty;
	private String calculatedPropertyExpression;

	private String dataName;
	private String dataType;
	private String dataLength;
	private String dataDefaultValue;
	private Boolean dataPrimaryKey;
	private Boolean dataAutoIncrement;
	private Boolean dataNullable;
	private Boolean dataUnique;
	private String dataPrecision;
	private String dataScale;

	private String relationshipType;
	private String relationshipCardinality;
	private String relationshipName;
	private String relationshipEntityName;
	private String relationshipEntityPerspectiveName;
	
	private String widgetType;
	private String widgetLength;
	private String widgetPattern;
	private String widgetService;
	private String widgetLabel;
	private Boolean widgetIsMajor;
	private Boolean widgetSection;
	private Boolean widgetShortLabel;
	private Boolean widgetFormat;
	private String widgetDropDownKey;
	private String widgetDropDownValue;

	private String feedPropertyName;

	private String roleRead;
	private String roleWrite;

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
	 * @return the isCalculatedProperty
	 */
	public Boolean getIsCalculatedProperty() {
		return isCalculatedProperty;
	}

	/**
	 * @param isCalculatedProperty the isCalculatedProperty to set
	 */
	public void setIsCalculatedProperty(Boolean isCalculatedProperty) {
		this.isCalculatedProperty = isCalculatedProperty;
	}

	/**
	 * @return the calculatedPropertyExpression
	 */
	public String getCalculatedPropertyExpression() {
		return calculatedPropertyExpression;
	}

	/**
	 * @param calculatedPropertyExpression the calculatedPropertyExpression to set
	 */
	public void setCalculatedPropertyExpression(String calculatedPropertyExpression) {
		this.calculatedPropertyExpression = calculatedPropertyExpression;
	}

	/**
	 * @return the widgetIsMajor
	 */
	public Boolean getWidgetIsMajor() {
		return widgetIsMajor;
	}

	/**
	 * @param widgetIsMajor the widgetIsMajor to set
	 */
	public void setWidgetIsMajor(Boolean widgetIsMajor) {
		this.widgetIsMajor = widgetIsMajor;
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
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the dataLength
	 */
	public String getDataLength() {
		return dataLength;
	}

	/**
	 * @param dataLength the dataLength to set
	 */
	public void setDataLength(String dataLength) {
		this.dataLength = dataLength;
	}

	/**
	 * @return the dataDefaultValue
	 */
	public String getDataDefaultValue() {
		return dataDefaultValue;
	}

	/**
	 * @param dataDefaultValue the dataDefaultValue to set
	 */
	public void setDataDefaultValue(String dataDefaultValue) {
		this.dataDefaultValue = dataDefaultValue;
	}

	/**
	 * @return the dataPrimaryKey
	 */
	public Boolean getDataPrimaryKey() {
		return dataPrimaryKey;
	}

	/**
	 * @param dataPrimaryKey the dataPrimaryKey to set
	 */
	public void setDataPrimaryKey(Boolean dataPrimaryKey) {
		this.dataPrimaryKey = dataPrimaryKey;
	}

	/**
	 * @return the dataAutoIncrement
	 */
	public Boolean getDataAutoIncrement() {
		return dataAutoIncrement;
	}

	/**
	 * @param dataAutoIncrement the dataAutoIncrement to set
	 */
	public void setDataAutoIncrement(Boolean dataAutoIncrement) {
		this.dataAutoIncrement = dataAutoIncrement;
	}

	/**
	 * @return the dataNullable
	 */
	public Boolean getDataNullable() {
		return dataNullable;
	}

	/**
	 * @param dataNullable the dataNullable to set
	 */
	public void setDataNullable(Boolean dataNullable) {
		this.dataNullable = dataNullable;
	}

	/**
	 * @return the dataUnique
	 */
	public Boolean getDataUnique() {
		return dataUnique;
	}

	/**
	 * @param dataUnique the dataUnique to set
	 */
	public void setDataUnique(Boolean dataUnique) {
		this.dataUnique = dataUnique;
	}

	/**
	 * @return the dataPrecision
	 */
	public String getDataPrecision() {
		return dataPrecision;
	}

	/**
	 * @param dataPrecision the dataPrecision to set
	 */
	public void setDataPrecision(String dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	/**
	 * @return the dataScale
	 */
	public String getDataScale() {
		return dataScale;
	}

	/**
	 * @param dataScale the dataScale to set
	 */
	public void setDataScale(String dataScale) {
		this.dataScale = dataScale;
	}

	/**
	 * @return the relationshipType
	 */
	public String getRelationshipType() {
		return relationshipType;
	}

	/**
	 * @param relationshipType the relationshipType to set
	 */
	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	/**
	 * @return the relationshipCardinality
	 */
	public String getRelationshipCardinality() {
		return relationshipCardinality;
	}

	/**
	 * @param relationshipCardinality the relationshipCardinality to set
	 */
	public void setRelationshipCardinality(String relationshipCardinality) {
		this.relationshipCardinality = relationshipCardinality;
	}

	/**
	 * @return the relationshipName
	 */
	public String getRelationshipName() {
		return relationshipName;
	}

	/**
	 * @param relationshipName the relationshipName to set
	 */
	public void setRelationshipName(String relationshipName) {
		this.relationshipName = relationshipName;
	}
	
	/**
	 * @return the relationshipEntityName
	 */
	public String getRelationshipEntityName() {
		return relationshipEntityName;
	}

	/**
	 * @param relationshipEntityName the relationship entity name to set
	 */
	public void setRelationshipEntityName(String relationshipEntityName) {
		this.relationshipEntityName = relationshipEntityName;
	}

	/**
	 * @return the relationshipEntityPerspectiveName
	 */
	public String getRelationshipEntityPerspectiveName() {
		return relationshipEntityPerspectiveName;
	}

	/**
	 * @param relationshipEntityPerspectiveName the relationship entity perspective name to set
	 */
	public void setRelationshipEntityPerspectiveName(String relationshipEntityPerspectiveName) {
		this.relationshipEntityPerspectiveName = relationshipEntityPerspectiveName;
	}

	/**
	 * @return the widgetType
	 */
	public String getWidgetType() {
		return widgetType;
	}

	/**
	 * @param widgetType the widgetType to set
	 */
	public void setWidgetType(String widgetType) {
		this.widgetType = widgetType;
	}

	/**
	 * @return the widgetLength
	 */
	public String getWidgetLength() {
		return widgetLength;
	}

	/**
	 * @param widgetLength the widgetLength to set
	 */
	public void setWidgetLength(String widgetLength) {
		this.widgetLength = widgetLength;
	}

	/**
	 * @return the widgetPattern
	 */
	public String getWidgetPattern() {
		return widgetPattern;
	}

	/**
	 * @param widgetPattern the widgetPattern to set
	 */
	public void setWidgetPattern(String widgetPattern) {
		this.widgetPattern = widgetPattern;
	}

	/**
	 * @return the widgetService
	 */
	public String getWidgetService() {
		return widgetService;
	}

	/**
	 * @param widgetService the widgetService to set
	 */
	public void setWidgetService(String widgetService) {
		this.widgetService = widgetService;
	}

	/**
	 * @return the widgetLabel
	 */
	public String getWidgetLabel() {
		return widgetLabel;
	}

	/**
	 * @param widgetLabel the widgetLabel to set
	 */
	public void setWidgetLabel(String widgetLabel) {
		this.widgetLabel = widgetLabel;
	}

	/**
	 * @return the widget section
	 */
	public Object getWidgetSection() {
		return widgetSection;
	}
	
	/**
	 * @param widgetSection the widget section
	 */
	public void setWidgetSection(Boolean widgetSection) {
		this.widgetSection = widgetSection;
	}

	/**
	 * @return the widget short label
	 */
	public Boolean getWidgetShortLabel() {
		return widgetShortLabel;
	}

	/**
	 * @param widgetShortLabel the widget short label
	 */
	public void setWidgetShortLabel(Boolean widgetShortLabel) {
		this.widgetShortLabel = widgetShortLabel;
	}

	/**
	 * @return the widget format
	 */
	public Boolean getWidgetFormat() {
		return widgetFormat;
	}

	/**
	 * @param widgetFormat the widget format
	 */
	public void setWidgetFormat(Boolean widgetFormat) {
		this.widgetFormat = widgetFormat;
	}

	/**
	 * @return widget drop down key
	 */
	public String getWidgetDropDownKey() {
		return widgetDropDownKey;
	}

	/**
	 * @param widgetDropDownKey the widget drop down key
	 */
	public void setWidgetDropDownKey(String widgetDropDownKey) {
		this.widgetDropDownKey = widgetDropDownKey;
	}

	/**
	 * @return widget drop down value
	 */
	public String getWidgetDropDownValue() {
		return widgetDropDownValue;
	}

	/**
	 * @param widgetDropDownValue the widget drop down value
	 */
	public void setWidgetDropDownValue(String widgetDropDownValue) {
		this.widgetDropDownValue = widgetDropDownValue;
	}

	/**
	 * @return the feedPropertyName
	 */
	public String getFeedPropertyName() {
		return feedPropertyName;
	}

	/**
	 * @param feedPropertyName the feedPropertyName to set
	 */
	public void setFeedPropertyName(String feedPropertyName) {
		this.feedPropertyName = feedPropertyName;
	}

	/**
	 * @return the roleRead
	 */
	public String getRoleRead() {
		return roleRead;
	}

	/**
	 * @param roleRead the roleRead to set
	 */
	public void setRoleRead(String roleRead) {
		this.roleRead = roleRead;
	}

	/**
	 * @return the roleWrite
	 */
	public String getRoleWrite() {
		return roleWrite;
	}

	/**
	 * @param roleWrite the roleWrite to set
	 */
	public void setRoles(String roleWrite) {
		this.roleWrite = roleWrite;
	}

}
