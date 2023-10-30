package org.eclipse.dirigible.components.data.anonymize.domain;

/**
 * The Enum DataAnonymizeType.
 */
public enum DataAnonymizeType {
	
	/** The FULL_NAME. */
	FULL_NAME("FULL_NAME"),
	
	/** The first name. */
	FIRST_NAME("FIRST_NAME"),
	
	/** The last name. */
	LAST_NAME("FIRST_NAME"),
	
	/** The user name. */
	USER_NAME("USER_NAME"),
	
	/** The phone. */
	PHONE("PHONE"),
	
	/** The email. */
	EMAIL("EMAIL"),
	
	/** The address. */
	ADDRESS("ADDRESS"),
	
	/** The city. */
	CITY("CITY"),
	
	/** The country. */
	COUNTRY("COUNTRY"),
	
	/** The date. */
	DATE("DATE"),
	
	/** The random. */
	RANDOM("RANDOM"),
	
	/** The mask. */
	MASK("MASK"),
	
	/** The empty. */
	EMPTY("EMPTY"),
	
	/** The null. */
	NULL("NULL");
	
	
	/** The type. */
	private String type;

	/**
	 * Instantiates a new type.
	 *
	 * @param type the type
	 */
	DataAnonymizeType(String type) {
		this.type = type;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return this.type;
	}

}
