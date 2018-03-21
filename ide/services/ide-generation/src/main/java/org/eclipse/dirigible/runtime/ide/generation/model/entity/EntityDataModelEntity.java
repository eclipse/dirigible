package org.eclipse.dirigible.runtime.ide.generation.model.entity;

import java.util.List;

/**
 * Entity element from the Entity Data Model
 *
 */
public class EntityDataModelEntity {
	
	private String name;
	
	private String dataName;
	
	private boolean isPrimary;
	
	private String menuKey;
	
	private String menuLabel;
	
	private String layoutType;
	
	private List<EntityDataModelProperty> properties;

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
	 * @return the isPrimary
	 */
	public boolean isPrimary() {
		return isPrimary;
	}

	/**
	 * @param isPrimary the isPrimary to set
	 */
	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
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

}
