package org.eclipse.dirigible.runtime.command;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

	private static final Logger logger = Logger.getLogger(WebSocketTerminalBridgeServletInternal.class);

	private static Map<String, Session> openSessions = new ConcurrentHashMap<String, Session>();
	private static Map<String, Process> session2process = new ConcurrentHashMap<String, Process>();

	@OnOpen
	public void onOpen(Session session) throws IOException {
		openSessions.put(session.getId(), session);
		session.getBasicRemote().sendText("[terminal] open: " + session.getId());
		logger.debug("[ws:terminal] onOpen: " + session.getId());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		logger.debug("[ws:terminal] onMessage: " + message);

		Process process = session2process.get(session.getId());
		if (process != null) {
			try {
				if ("exit".equalsIgnoreCase(message)) {
					logger.debug("onMessage: exit command received");
					process.destroy();
				} else {
					new OutputStreamWriter(new BufferedOutputStream(process.getOutputStream())).write(message);
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			try {
				process = startProcess(message, session);
				logger.debug("onMessage: process started");
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			session2process.put(session.getId(), process);
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

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Process process = ProcessUtils.startProcess(args, processBuilder);
		// Piper piper = new Piper(process.getInputStream(), session.getBasicRemote().getSendStream());
		Piper pipe = new Piper(process.getInputStream(), out);

		new Thread(pipe).start();
		try {
			// process.waitFor();

			int i = 0;
			boolean deadYet = false;
			do {
				Thread.sleep(ProcessUtils.DEFAULT_WAIT_TIME);
				try {
					synchronized (out) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
						String line = null;
						while ((line = reader.readLine()) != null) {
							logger.debug("sending process data: " + line);
							// synchronized (session) {
							if (session.isOpen()) {
								session.getBasicRemote().sendText(line);
								out.reset();
							}
							// }
						}
					}

					process.exitValue();
					deadYet = true;
					removeProcess(process);
				} catch (IllegalThreadStateException e) {
					// if (limitEnabled) {
					if (++i >= 600) {
						process.destroy();
						throw new RuntimeException("Exeeds timeout: " + ((ProcessUtils.DEFAULT_WAIT_TIME / 1000) * 600));
					}
				}
				// }
			} while (!deadYet);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		session.getBasicRemote().sendText(new String(out.toByteArray()));

		logger.debug("exiting startProcess: " + message + " | " + session.getId());

		return process;
	}

	protected void removeProcess(Process process) {
		Iterator<Entry<String, Process>> iter = session2process.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Process> entry = iter.next();
			if (entry.getValue().equals(process)) {
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
		Process process = session2process.get(session.getId());
		process.destroy();
		openSessions.remove(session.getId());
		logger.debug("[ws:terminal] onClose: Session " + session.getId() + " has ended");
	}

	public void closeAll() {
		for (Session session : openSessions.values()) {
			try {
				synchronized (session) {
					Process process = session2process.get(session.getId());
					process.destroy();
					session.close();
				}
			} catch (Throwable e) {
				// do not log it with the Logger
				e.printStackTrace();
			}
		}
	}

}
