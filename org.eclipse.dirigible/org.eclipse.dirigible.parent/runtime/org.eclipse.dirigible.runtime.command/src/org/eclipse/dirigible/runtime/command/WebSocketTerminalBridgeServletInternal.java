/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.command;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.eclipse.dirigible.repository.ext.command.Piper;
import org.eclipse.dirigible.repository.ext.command.ProcessUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class WebSocketTerminalBridgeServletInternal {

	private static final String Ctrl_C = "^C";

	private static final Logger logger = Logger.getLogger(WebSocketTerminalBridgeServletInternal.class);

	private static Map<String, Session> openSessions = new ConcurrentHashMap<String, Session>();
	private static Map<String, ProcessRunnable> session2process = new ConcurrentHashMap<String, ProcessRunnable>();

	@OnOpen
	public void onOpen(Session session) throws IOException {
		openSessions.put(session.getId(), session);
		// session.getBasicRemote().sendText("[terminal] open: " + session.getId());
		logger.debug("[ws:terminal] onOpen: " + session.getId());
		startProcessRunnable(session);

	}

	protected void startProcessRunnable(Session session) {
		try {
			ProcessRunnable processRunnable = new ProcessRunnable(session);
			new Thread(processRunnable).start();
			logger.debug("process started");
			session2process.put(session.getId(), processRunnable);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		logger.debug("[ws:terminal] onMessage: " + message);

		ProcessRunnable processRunnable = session2process.get(session.getId());
		Process process = processRunnable.getProcess();
		if (process != null) {
			try {
				if (Ctrl_C.equalsIgnoreCase(message.trim())) {
					logger.debug("onMessage: exit command received");
					process.destroy();
					session2process.remove(session.getId());
					// startProcessRunnable(session);
					session.close();
				} else {
					byte[] data = message.getBytes();
					process.getOutputStream().write(data);
					process.getOutputStream().flush();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	class ProcessRunnable implements Runnable {

		private static final String BASH_COMMAND = "bash";
		private static final String CMD_COMMAND = "cmd";

		private Session session;

		private Process process;

		public Process getProcess() {
			return process;
		}

		public ProcessRunnable(Session session) {
			this.session = session;
		}

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
							synchronized (session) {
								BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
								String line = null;
								while ((line = reader.readLine()) != null) {
									logger.debug("sending process data: " + line);
									if (session.isOpen()) {
										session.getBasicRemote().sendText(line);
									}
								}
								out.reset();
							}

							process.exitValue();
							deadYet = true;
							removeProcess(process);
						} catch (IllegalThreadStateException e) {
							if (++i >= 600) {
								process.destroy();
								throw new RuntimeException("Exeeds timeout: " + ((ProcessUtils.DEFAULT_WAIT_TIME / 1000) * 600));
							}
						}
					} while (!deadYet);
					session.close();

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				synchronized (session) {
					if (session.isOpen()) {
						session.getBasicRemote().sendText(new String(out.toByteArray()));
					}
				}

			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	private Process startProcess(final String message, final Session session) throws IOException {

		logger.debug("entering startProcess: " + message + " | " + session.getId());

		String[] args = ProcessUtils.translateCommandline(message);

		ProcessBuilder processBuilder = ProcessUtils.createProcess(args);
		ProcessUtils.addEnvironmentVariables(processBuilder, null);
		ProcessUtils.removeEnvironmentVariables(processBuilder, null);
		// processBuilder.directory(new File(workingDirectory));
		processBuilder.redirectErrorStream(true);

		Process process = ProcessUtils.startProcess(args, processBuilder);

		logger.debug("exiting startProcess: " + message + " | " + session.getId());

		return process;
	}

	protected void removeProcess(Process process) {
		Iterator<Entry<String, ProcessRunnable>> iter = session2process.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, ProcessRunnable> entry = iter.next();
			if (entry.getValue().getProcess().equals(process)) {
				iter.remove();
				break;
			}
		}
	}

	@OnError
	public void onError(Session session, String error) {
		logger.debug("[ws:terminal] onError: " + error);
	}

	@OnClose
	public void onClose(Session session) {
		ProcessRunnable processRunnable = session2process.get(session.getId());
		processRunnable.getProcess().destroy();
		openSessions.remove(session.getId());
		logger.debug("[ws:terminal] onClose: Session " + session.getId() + " has ended");
	}

	public void closeAll() {
		for (Session session : openSessions.values()) {
			try {
				synchronized (session) {
					ProcessRunnable processRunnable = session2process.get(session.getId());
					processRunnable.getProcess().destroy();
					session.close();
				}
			} catch (Throwable e) {
				// do not log it with the Logger
				e.printStackTrace();
			}
		}
	}

}
