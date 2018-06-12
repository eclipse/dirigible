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
	
	private String dataQuery;
	
	private String type;
	
	private String menuKey;
	
	private String menuLabel;
	
	private String menuIndex;
	
	private String layoutType;
	
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

}
