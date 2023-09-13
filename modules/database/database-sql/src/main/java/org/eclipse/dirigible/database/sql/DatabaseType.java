package org.eclipse.dirigible.database.sql;

public enum DatabaseType {
	
	/** The rdbms. */
	RDBMS("RDBMS"),
	
	NOSQL("NOSQL");
	
	/** The name. */
	private String name;

	/**
	 * Instantiates a new database type.
	 *
	 * @param name the name
	 */
	DatabaseType(String name) {
        this.name = name;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * To string.
     *
     * @return the string
     */
    public String toString() {
        return name;
    }
    
    /**
     * Value of by name.
     *
     * @param name the name
     * @return the database type
     */
    public static final DatabaseType valueOfByName(String name) {
    	for(DatabaseType type : DatabaseType.class.getEnumConstants()) {
            if(type.toString().equals(name)) {
                return type;
            }
        }
       throw new IllegalArgumentException("DatabaseType not found: " + name);
    }

}
