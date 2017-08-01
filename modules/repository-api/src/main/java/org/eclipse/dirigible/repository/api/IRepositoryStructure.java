package org.eclipse.dirigible.repository.api;

public interface IRepositoryStructure {

	public static final String SEPARATOR = "/"; //$NON-NLS-1$

	public static final String KEYWORD_REGISTRY = "registry"; //$NON-NLS-1$
	public static final String KEYWORD_PUBLIC = "public"; //$NON-NLS-1$
	public static final String KEYWORD_USERS = "users"; //$NON-NLS-1$
	public static final String KEYWORD_WORKSPACE = "workspace"; //$NON-NLS-1$

	public static final String PATH_ROOT = SEPARATOR;
	public static final String PATH_REGISTRY = PATH_ROOT + KEYWORD_REGISTRY; // /registry
	public static final String PATH_REGISTRY_PUBLIC = PATH_REGISTRY + SEPARATOR + KEYWORD_PUBLIC; // /registry/public
	public static final String PATH_USERS = PATH_ROOT + KEYWORD_USERS; // /users

	public static final String PATTERN_USERS_WORKSPACE_DEFAULT = PATH_USERS + SEPARATOR + "{0}" + SEPARATOR + KEYWORD_WORKSPACE; //$NON-NLS-1$ /users/john/workspace
	public static final String PATTERN_USERS_WORKSPACE_NAMED = PATH_USERS + SEPARATOR + "{0}" + SEPARATOR + "{1}"; //$NON-NLS-1$ /users/john/product3
}
