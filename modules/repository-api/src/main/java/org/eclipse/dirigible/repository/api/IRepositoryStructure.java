package org.eclipse.dirigible.repository.api;

public interface IRepositoryStructure {
	
	public static final String SEPARATOR = "/"; //$NON-NLS-1$
	
	public static final String ROOT = SEPARATOR;
	
	public static final String REGISTRY = "/registry"; //$NON-NLS-1$
	
	public static final String REGISTRY_PUBLIC = "/registry/public"; //$NON-NLS-1$
	
	public static final String USERS = "/users"; //$NON-NLS-1$
	
	public static final String USERS_WORKSPACE_DEFAULT = "/users/{0}/workspace"; //$NON-NLS-1$ /users/john/workspace
	
	public static final String USERS_WORKSPACE_NAMED = "/users/{0}/{1}"; //$NON-NLS-1$ /users/john/productv3
}
