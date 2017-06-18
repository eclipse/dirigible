package org.eclipse.dirigible.database.persistence.test;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="ORDERS")
public class Order {
	
	@Id
	@GeneratedValue
	@Column(name="ORDER_ID", columnDefinition="BIGINT", nullable=false)
	private long id;
	
	@Column(name="CUSTOMER_SUBJECT", columnDefinition="VARCHAR", nullable=false, length=512)
	private String subject;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	
}
