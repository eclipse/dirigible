/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.ide.terminal.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.process.Piper;
import org.eclipse.dirigible.commons.process.ProcessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Terminal Websocket Service.
 * Use {@link XTerminalWebsocketService}
 */
@Deprecated
@ServerEndpoint("/websockets/v4/ide/terminal")
public class TerminalWebsocketService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TerminalWebsocketService.class);

	/** The Constant Ctrl_C. */
	private static final String Ctrl_C = "^C";

	/** The open sessions. */
	private static Map<String, Session> OPEN_SESSIONS = new ConcurrentHashMap<String, Session>();

	/** The session to process. */
	private static Map<String, ProcessRunnable> SESSION_TO_PROCESS = new ConcurrentHashMap<String, ProcessRunnable>();

	/**
	 * On open callback.
	 *
	 * @param session
	 *            the session
	 */
	@OnOpen
	public void onOpen(Session session) {
		OPEN_SESSIONS.put(session.getId(), session);
		logger.debug("[ws:terminal] onOpen: " + session.getId());
		startProcessRunnable(session);
	}

	/**
	 * On message callback.
	 *
	 * @param message
	 *            the message
	 * @param session
	 *            the session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		logger.trace("[ws:terminal] onMessage: " + message);
		
		if (Configuration.isAnonymousModeEnabled() || !Configuration.isTerminalEnabled()) {
			try {
				session.getBasicRemote().sendText("Feature 'Terminal' is disabled in this mode.", true);
			} catch (IOException e) {
				logger.error("[ws:terminal] " + e.getMessage(), e);
			}
			return;
		}

		ProcessRunnable processRunnable = SESSION_TO_PROCESS.get(session.getId());
		Process process = processRunnable.getProcess();
		if (process != null) {
			try {
				if (Ctrl_C.equalsIgnoreCase(message.trim())) {
					logger.trace("[ws:terminal] onMessage: exit command received");
					process.destroy();
					SESSION_TO_PROCESS.remove(session.getId());
					// startProcessRunnable(session);
					session.close();
				} else {
					if (!"".equals(message.trim())) {
						byte[] data = message.getBytes(StandardCharsets.UTF_8);
						processRunnable.keepReplying = true;
						process.getOutputStream().write(data);
						process.getOutputStream().flush();
					} else {
						session.getBasicRemote().sendText("\n", true);
					}
				}
			} catch (IOException e) {
				logger.error("[ws:terminal] " + e.getMessage(), e);
			}
		}
	}

	/**
	 * On error callback.
	 *
	 * @param session
	 *            the session
	 * @param throwable
	 *            the throwable
	 */
	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.info(String.format("[ws:terminal] Session %s error %s", session.getId(), throwable.getMessage()));
		logger.error("[ws:terminal] " + throwable.getMessage(), throwable);
	}

	/**
	 * On close callback.
	 *
	 * @param session
	 *            the session
	 * @param closeReason
	 *            the close reason
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.trace(String.format("[ws:terminal] Session %s closed because of %s", session.getId(), closeReason));
		ProcessRunnable processRunnable = SESSION_TO_PROCESS.get(session.getId());
		if (processRunnable != null) {
			Process process = processRunnable.getProcess();
			terminateProcess(session, process);
		}
	}

	/**
	 * Terminate process.
	 *
	 * @param session
	 *            the session
	 * @param process
	 *            the process
	 */
	private void terminateProcess(Session session, Process process) {
		process.destroy();
		SESSION_TO_PROCESS.remove(session.getId());
		OPEN_SESSIONS.remove(session.getId());
	}

	/**
	 * Start process runnable.
	 *
	 * @param session
	 *            the session
	 */
	protected void startProcessRunnable(Session session) {
		try {
			ProcessRunnable processRunnable = new ProcessRunnable(session);
			new Thread(processRunnable).start();
			logger.debug("[ws:terminal] process started");
			SESSION_TO_PROCESS.put(session.getId(), processRunnable);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * The Process Runnable.
	 */
	class ProcessRunnable implements Runnable {

		/** The Constant BASH_COMMAND. */
		private static final String BASH_COMMAND = "bash";

		/** The Constant CMD_COMMAND. */
		private static final String CMD_COMMAND = "cmd";

		/** The session. */
		private Session session;

		/** The process. */
		private Process process;

		/** The keep replying. */
		private boolean keepReplying = true;

		/**
		 * Gets the process.
		 *
		 * @return the process
		 */
		public Process getProcess() {
			return process;
		}

		/**
		 * Instantiates a new process runnable.
		 *
		 * @param session
		 *            the session
		 */
		public ProcessRunnable(Session session) {
			this.session = session;
		}

		/**
		 * Run.
		 */
		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				String os = System.getProperty("os.name").toLowerCase();
				String command = BASH_COMMAND;
				if (os.indexOf("windows") >= 0) {
					command = CMD_COMMAND;
				}
				this.process = startProcess(command, session);

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				Piper pipe = new Piper(process.getInputStream(), out);

				new Thread(pipe).start();
				try {

					int i = 0;
					boolean deadYet = false;
					do {
						Thread.sleep(ProcessUtils.DEFAULT_WAIT_TIME);
						try {
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(new ByteArrayInputStream(out.toByteArray()), StandardCharsets.UTF_8));
							String line = null;
							while ((line = reader.readLine()) != null) {
								logger.debug("sending process data: " + line);
								sendLine(line);
								i = 0;
								keepReplying = false;
							}
							out.reset();

							sendLine("exit: " + process.exitValue());

							deadYet = true;
						} catch (IllegalThreadStateException e) {
							if (++i >= ProcessUtils.DEFAULT_LOOP_COUNT) {
								terminateProcess(session, process);
								throw new RuntimeException(
										"Exeeds timeout: " + ((ProcessUtils.DEFAULT_WAIT_TIME / 1000) * ProcessUtils.DEFAULT_LOOP_COUNT));
							}
							if (keepReplying) {
								sendLine("");
								keepReplying = false;
							}

						}
					} while (!deadYet);

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				String line = new String(out.toByteArray(), StandardCharsets.UTF_8);
				sendLine(line);
				terminateProcess(session, process);
			} catch (

			IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

		/**
		 * Send line.
		 *
		 * @param line
		 *            the line
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		private void sendLine(String line) throws IOException {
			if (session.isOpen()) {
				synchronized (this) {
					String lineToSend = line;
					if ("".equals(lineToSend)) {
						lineToSend = "\n";
					}
					session.getBasicRemote().sendText(lineToSend, true);
				}
			}
		}

	}

	/**
	 * Start process.
	 *
	 * @param message
	 *            the message
	 * @param session
	 *            the session
	 * @return the process
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Process startProcess(final String message, final Session session) throws IOException {

		logger.trace("entering startProcess: " + message + " | " + session.getId());

		String[] args = ProcessUtils.translateCommandline(message);

		ProcessBuilder processBuilder = ProcessUtils.createProcess(args);
		ProcessUtils.addEnvironmentVariables(processBuilder, null);
		ProcessUtils.removeEnvironmentVariables(processBuilder, null);
		// processBuilder.directory(new File(workingDirectory));
		processBuilder.redirectErrorStream(true);

		Process process = ProcessUtils.startProcess(args, processBuilder);

		logger.trace("exiting startProcess: " + message + " | " + session.getId());

		return process;
	}
}
