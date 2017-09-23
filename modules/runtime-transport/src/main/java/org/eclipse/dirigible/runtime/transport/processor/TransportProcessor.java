package org.eclipse.dirigible.runtime.transport.processor;

import java.util.Arrays;

import javax.inject.Inject;

import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Database SQL Queries Service incoming requests
 */
public class TransportProcessor {

	private static final Logger logger = LoggerFactory.getLogger(TransportProcessor.class);

	@Inject
	private WorkspacesCoreService workspacesCoreService;
	
	@Inject
	private IRepository repository;

	public void importProject(String workspace, byte[] content) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		repository.importZip(content, workspaceApi.getPath(), true, false, null);
	}

	public byte[] exportProject(String workspace, String project) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		IProject projectApi = getProject(workspaceApi, project);
		return repository.exportZip(projectApi.getPath(), true);
	}
	
	public byte[] exportWorkspace(String workspace) {
		IWorkspace workspaceApi = getWorkspace(workspace);
		return repository.exportZip(workspaceApi.getPath(), false);
	}

	private IWorkspace getWorkspace(String workspace) {
		return workspacesCoreService.getWorkspace(workspace);
	}

	private IProject getProject(IWorkspace workspaceApi, String project) {
		return workspaceApi.getProject(project);
	}
	
	public void importSnapshot(byte[] content) {
		repository.importZip(content, IRepositoryStructure.SEPARATOR, true, false, null);
	}

	public byte[] exportSnapshot() {
		return repository.exportZip(IRepositoryStructure.SEPARATOR, true);
	}

	
}
