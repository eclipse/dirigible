package org.eclipse.dirigible.components.ide.workspace.endpoint;

import static java.text.MessageFormat.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.process.Piper;
import org.eclipse.dirigible.commons.process.ProcessUtils;
import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.ide.workspace.domain.File;
import org.eclipse.dirigible.components.ide.workspace.domain.Project;
import org.eclipse.dirigible.components.ide.workspace.service.WorkspaceService;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.local.LocalWorkspaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Class WorkspaceActionsEndpoint.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_IDE + "workspace-actions")
public class WorkspaceActionsEndpoint {
	
	 /** The Constant LOGGER. */
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceActionsEndpoint.class);
	
	/** The workspace service. */
    @Autowired
    private WorkspaceService workspaceService;
	
	/**
	 * Creates the project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param action the action
	 * @return the response
	 * @throws URISyntaxException the URI syntax exception
	 */
	@PostMapping("/{workspace}/{project}/{action}")
	public ResponseEntity<String> executeProjectAction(
			@PathVariable("workspace") String workspace,
			@PathVariable("project") String project,
			@PathVariable("action") String action)
			throws URISyntaxException {
		if (!workspaceService.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		if (!workspaceService.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist.", project);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		Project projectObject = workspaceService.getProject(workspace, project);
		File fileObject = projectObject.getFile("project.json");
		if (!fileObject.exists()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, workspace + "/" + project + "/" + "project.json does not exist");
		}
		try {
			ProjectJson projectJson = GsonHelper.fromJson(new String(fileObject.getContent()), ProjectJson.class);
			List<ProjectAction> actions = projectJson.getActions();
			if (actions == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Actions section not found in the project descriptor file: " + project);
			}
			Optional<ProjectAction> actionCommand = actions.stream().filter(a -> a.name.equals(action)).findFirst();
			if (actionCommand.isPresent()) {
				String workingDirectory = LocalWorkspaceMapper.getMappedName((FileSystemRepository) projectObject.getRepository(), projectObject.getPath());
				String result = executeCommandLine(workingDirectory, actionCommand.get().command);
				logger.info(result);
				return ResponseEntity.ok().build();
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Action not found: " + action);
			}
		} catch (Exception e) {
			String error = "Malformed project file: " + project + " (" + e.getMessage() + ")";
			logger.error(error);
			logger.trace(e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error);
		}
		
	}
	
	/**
	 * Creates the project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the response
	 * @throws URISyntaxException the URI syntax exception
	 */
	@GetMapping(value = "/{workspace}/{project}", produces="application/json")
	public ResponseEntity<String> listProjectAction(
			@PathVariable("workspace") String workspace,
			@PathVariable("project") String project)
			throws URISyntaxException {
		if (!workspaceService.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		if (!workspaceService.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist.", project);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		Project projectObject = workspaceService.getProject(workspace, project);
		File fileObject = projectObject.getFile("project.json");
		if (!fileObject.exists()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, workspace + "/" + project + "/" + "project.json does not exist");
		}
		try {
			ProjectJson projectJson = GsonHelper.fromJson(new String(fileObject.getContent()), ProjectJson.class);
			List<ProjectAction> actions = projectJson.getActions();
			if (actions == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Actions section not found in the project descriptor file: " + project);
			}			
			return ResponseEntity.ok(GsonHelper.toJson(actions));
		} catch (Exception e) {
			String error = "Malformed project file: " + project + " (" + e.getMessage() + ")";
			logger.error(error);
			logger.trace(e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error);
		}
		
	}
	
	/**
	 * Execute command line.
	 *
	 * @param workingDirectory the working directory
	 * @param commandLine the command line
	 * @return the string
	 * @throws Exception the exception
	 */
	public String executeCommandLine(String workingDirectory, String commandLine) throws Exception {
		String result;

		String[] args;
		try {
			args = ProcessUtils.translateCommandline(commandLine);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			throw new Exception(e);
		}

		ByteArrayOutputStream out;
		try {
			ProcessBuilder processBuilder = ProcessUtils.createProcess(args);

			processBuilder.directory(new java.io.File(workingDirectory));

			processBuilder.redirectErrorStream(true);

			out = new ByteArrayOutputStream();
			Process process = ProcessUtils.startProcess(args, processBuilder);
			Piper pipe = new Piper(process.getInputStream(), out);
			new Thread(pipe).start();
			try {
				int i = 0;
				boolean deadYet = false;
				do {
					Thread.sleep(ProcessUtils.DEFAULT_WAIT_TIME);
					try {
						process.exitValue();
						deadYet = true;
					} catch (IllegalThreadStateException e) {
						if (++i >= ProcessUtils.DEFAULT_LOOP_COUNT) {
							process.destroy();
							throw new RuntimeException(
									"Exceeds timeout - " + ((ProcessUtils.DEFAULT_WAIT_TIME / 1000) * ProcessUtils.DEFAULT_LOOP_COUNT));
						}
					}
				} while (!deadYet);

			} catch (Exception e) {
				if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
				throw new IOException(e);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			throw new Exception(e);
		}
		result = out.toString(StandardCharsets.UTF_8);
		return result;
	}
	
	/**
     * The Class ProjectJson.
     */
    class ProjectJson {

        /** The guid. */
        private final String guid;
        
        /** The actions. */
        private final List<ProjectAction> actions;

        /**
         * Instantiates a new project json.
         *
         * @param guid the guid
         * @param actions the actions
         */
        ProjectJson(String guid, @Nullable List<ProjectAction> actions) {
            this.guid = guid;
            this.actions = actions;
        }

        /**
         * Gets the guid.
         *
         * @return the guid
         */
        public String getGuid() {
            return guid;
        }

        /**
         * Gets the actions the.
         *
         * @return the actions the
         */
        @Nullable
        public List<ProjectAction> getActions() {
            return actions;
        }
    }
    
    /**
     * The Class ProjectAction.
     */
    class ProjectAction {
    	
	    /** The name. */
	    String name;
    	
	    /** The command. */
	    String command;
    }

}
