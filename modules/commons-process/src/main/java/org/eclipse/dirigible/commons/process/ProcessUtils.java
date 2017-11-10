/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.commons.process;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProcessUtils {

	public static int DEFAULT_WAIT_TIME = 1000;
	public static int DEFAULT_LOOP_COUNT = 600;

	public static ProcessBuilder createProcess(String[] args) throws IOException {

		ProcessBuilder processBuilder = new ProcessBuilder(args);
		return processBuilder;
	}

	public static void addEnvironmentVariables(ProcessBuilder processBuilder, Map<String, String> forAdding) {
		if ((processBuilder != null) && (forAdding != null)) {
			Map<String, String> env = processBuilder.environment();
			env.putAll(forAdding);
		}
	}

	public static void removeEnvironmentVariables(ProcessBuilder processBuilder, List<String> forRemoving) {
		if ((processBuilder != null) && (forRemoving != null)) {
			Map<String, String> env = processBuilder.environment();
			for (String remove : forRemoving) {
				env.remove(remove);
			}
		}
	}

	public static Process startProcess(String[] args, ProcessBuilder processBuilder) throws IOException {

		Process process = processBuilder.start();
		return process;
	}

	public static String[] translateCommandline(final String toProcess) {
		return Commandline.translateCommandline(toProcess);
	}

}
