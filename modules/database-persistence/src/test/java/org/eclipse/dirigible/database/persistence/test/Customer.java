package org.eclipse.dirigible.database.persistence.test;

import javax.persistence.*;

@Table(name="CUSTOMERS", schema="FACTORY")
public class Customer {
	
	@Id
	@Column(name="CUSTOMER_ID", columnDefinition="INTEGER", nullable=false)
	private int id;
	
	@Column(name="CUSTOMER_FIRST_NAME", columnDefinition="VARCHAR", nullable=false, length=512)
	private String firstName;
	
	@Column(name="CUSTOMER_LAST_NAME", columnDefinition="VARCHAR", nullable=false, length=512)
	private String lastName;
	
	@Column(name="CUSTOMER_AGE", columnDefinition="INTEGER", nullable=false)
	private int age;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	
	
}
