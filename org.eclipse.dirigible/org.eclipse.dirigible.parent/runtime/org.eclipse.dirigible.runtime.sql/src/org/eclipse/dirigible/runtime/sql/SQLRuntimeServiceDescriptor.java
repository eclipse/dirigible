package org.eclipse.dirigible.runtime.sql;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the SQL Execution Service
 */
public class SQLRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "SQL Execution";
	private final String description = "SQL Execution Service triggers the execution of a specified service written in SQL.";
	private final String endpoint = "/sql";
	private final String documentation = "http://www.dirigible.io/help/service_sql.html";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getEndpoint() {
		return endpoint;
	}

	@Override
	public String getDocumentation() {
		return documentation;
	}

}
