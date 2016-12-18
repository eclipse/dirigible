package org.eclipse.dirigible.repository.ext.messaging;

import java.util.Date;

/**
 * The definition of the Message
 */
public class MessageDefinition {

	private int id;

	private String topic;

	private String subject;

	private String body;

	private String sender;

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
	 * The setter for the id
	 *
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Getter for the topic
	 *
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Setter for the topic
	 *
	 * @param topic
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * Getter for the subject
	 *
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Setter for the subject
	 *
	 * @param subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Getter for the body
	 *
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Setter for the body
	 *
	 * @param body
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Getter for the sender
	 *
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * Setter for the sender
	 *
	 * @param sender
	 */
	public void setSender(String sender) {
		this.sender = sender;
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
	 * Setter for the creation
	 * 
	 * @param createdAt
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
