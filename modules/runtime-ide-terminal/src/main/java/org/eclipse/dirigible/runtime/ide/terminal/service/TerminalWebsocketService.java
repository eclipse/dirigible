package org.eclipse.dirigible.runtime.ide.terminal.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.commons.process.Piper;
import org.eclipse.dirigible.commons.process.ProcessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@ServerEndpoint("/websockets/v3/ide/terminal")
public class TerminalWebsocketService {

	private static final Logger logger = LoggerFactory.getLogger(TerminalWebsocketService.class);

	private static final String Ctrl_C = "^C";

	private static Map<String, Session> OPEN_SESSIONS = new ConcurrentHashMap<String, Session>();
	private static Map<String, ProcessRunnable> SESSION_TO_PROCESS = new ConcurrentHashMap<String, ProcessRunnable>();

	@OnOpen
	public void onOpen(Session session) {
		OPEN_SESSIONS.put(session.getId(), session);
		logger.trace("[ws:terminal] onOpen: " + session.getId());
		startProcessRunnable(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		logger.trace("[ws:terminal] onMessage: " + message);

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

	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.info(String.format("[ws:terminal] Session %s error %s", session.getId(), throwable.getMessage()));
		logger.error("[ws:terminal] " + throwable.getMessage(), throwable);
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.trace(String.format("[ws:terminal] Session %s closed because of %s", session.getId(), closeReason));
		ProcessRunnable processRunnable = SESSION_TO_PROCESS.get(session.getId());
		if (processRunnable != null) {
			Process process = processRunnable.getProcess();
			terminateProcess(session, process);
		}
	}

	private void terminateProcess(Session session, Process process) {
		process.destroy();
		SESSION_TO_PROCESS.remove(session.getId());
		OPEN_SESSIONS.remove(session.getId());
	}

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

	class ProcessRunnable implements Runnable {

		private static final String BASH_COMMAND = "bash";
		private static final String CMD_COMMAND = "cmd";

		private Session session;

		private Process process;

		private boolean keepReplying = true;

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
							BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
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
