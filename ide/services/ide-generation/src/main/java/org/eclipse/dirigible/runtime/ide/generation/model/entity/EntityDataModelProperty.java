package org.eclipse.dirigible.runtime.ide.generation.model.entity;

public class EntityDataModelProperty {
	
	private String name;

	private String dataName;
	private String dataType;
	private String dataLength;
	private String dataDefaultValue;
	private Boolean dataPrimaryKey;
	private Boolean dataAutoIncrement;
	private Boolean dataNotNull;
	private Boolean dataUnique;
	private String dataPrecision;
	private String dataScale;

	private String relationshipType;
	private String relationshipCardinality;
	private String relationshipName;
	private String relationshipEntityName;
	
	private String widgetType;
	private String widgetLength;
	private String widgetPattern;
	private String widgetService;
	private String widgetLabel;
	private Boolean widgetIsMajor;
	private Boolean widgetSection;
	private Boolean widgetShortLabel;
	private Boolean widgetFormat;
	private Boolean widgetDropDownKey;
	private Boolean widgetDropDownValue;
	
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
	 * @return the dataNotNull
	 */
	public Boolean getDataNotNull() {
		return dataNotNull;
	}

	/**
	 * @param dataNotNull the dataNotNull to set
	 */
	public void setDataNotNull(Boolean dataNotNull) {
		this.dataNotNull = dataNotNull;
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
	 * @param relationshipName the relationshipEntityName to set
	 */
	public void setRelationshipEntityName(String relationshipEntityName) {
		this.relationshipEntityName = relationshipEntityName;
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
	public Boolean getWidgetDropDownKey() {
		return widgetDropDownKey;
	}

	/**
	 * @param widgetDropDownKey the widget drop down key
	 */
	public void setWidgetDropDownKey(Boolean widgetDropDownKey) {
		this.widgetDropDownKey = widgetDropDownKey;
	}

	/**
	 * @return widget drop down value
	 */
	public Boolean getWidgetDropDownValue() {
		return widgetDropDownValue;
	}

	/**
	 * @param widgetDropDownValue the widget drop down value
	 */
	public void setWidgetDropDownValue(Boolean widgetDropDownValue) {
		this.widgetDropDownValue = widgetDropDownValue;
	}

	
	
}
