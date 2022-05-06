package org.eclipse.dirigible.core.workspace.api;

public enum Status {
	
	/**
	 * A - Added (A new file has been added to the repository)
	 * M - Modified (An existing file has been changed)
	 * D - Deleted (The file has been deleted but the change has not been committed to the repository yet)
	 * U - Untracked (The file is new or has been changed but has not been added to the repository yet)
	 * C - Conflict (There is a conflict in the file on repository pull/merge)
	 * R - Renamed (The file has been renamed, the change has been added to the repository but has not been committed)
	 */
	
	A, M, D, U, C, R
	
}
