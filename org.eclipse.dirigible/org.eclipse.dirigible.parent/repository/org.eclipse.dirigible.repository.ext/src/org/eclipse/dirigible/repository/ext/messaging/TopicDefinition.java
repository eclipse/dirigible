package org.eclipse.dirigible.repository.ext.messaging;

import java.util.Date;

/**
 * Definition of the Topic
 */
public class TopicDefinition {

	private int id;

	private String name;

	private String createdBy;

	private Date createdAt;

	/**
	 * Getter for the id
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter for the id
	 *
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Getter for the name
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the name
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for the creator
	 *
	 * @return the creator
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Setter for the creator
	 *
	 * @param createdBy
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Getter for the date of creation
	 *
	 * @return the date of creation
	 */
	public Date getCreatedAt() {
		return (Date) createdAt.clone();
	}

	/**
	 * Setter for the date of creation
	 * 
	 * @param createdAt
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
