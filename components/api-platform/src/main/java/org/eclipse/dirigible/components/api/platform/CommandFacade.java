package org.eclipse.dirigible.components.api.platform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.dirigible.commons.process.execution.ProcessExecutor;
import org.eclipse.dirigible.commons.process.execution.output.OutputsPair;
import org.eclipse.dirigible.commons.process.execution.output.ProcessResult;

/**
 * The Class CommandFacade.
 */
public class CommandFacade {
	
	/**
	 * Execute service module.
	 *
	 * @param command the command line code
	 * @param add the add
	 * @param remove the remove
	 * @return the output of the command
	 * @throws ExecutionException the execution exception
	 * @throws InterruptedException the interrupted exception
	 */

    public static String execute(String command, Map<String, String> add, List<String> remove) throws ExecutionException, InterruptedException {
        Map<String, String> environmentVariablesToUse = createEnvironmentVariables(add, remove);
        ProcessExecutor<OutputsPair> processExecutor = ProcessExecutor.create();
        Future<ProcessResult<OutputsPair>> outputFuture = processExecutor.executeProcess(command, environmentVariablesToUse);
        ProcessResult<OutputsPair> output = outputFuture.get();
        return output.getProcessOutputs().getStandardOutput();
    }

    /**
     * Creates the environment variables.
     *
     * @param add the add
     * @param remove the remove
     * @return the map
     */
    private static Map<String, String> createEnvironmentVariables(Map<String, String> add, List<String> remove) {
        if (add == null) {
            return new ProcessBuilder().environment();
        }

        Map<String, String> environmentVariables = new HashMap<>(add);
        if (remove != null) {
        	remove.forEach(environmentVariables.keySet()::remove);
        }

        return environmentVariables;
    }

}
