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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Terminal Websocket Service.
 */
@ServerEndpoint(
		value = "/websockets/v4/ide/xterminal",
		subprotocols = {"tty"}
		)
public class XTerminalWebsocketService {

	private static final String TERMINAL_PREFIX = "[ws:terminal] ";

	/** The Constant FEATURE_TERMINAL_IS_DISABLED_IN_THIS_MODE. */
	private static final String FEATURE_TERMINAL_IS_DISABLED_IN_THIS_MODE = "Feature 'Terminal' is disabled in this mode.";
	
	/** The Constant PERMISSIONS_FAILED. */
	private static final String PERMISSIONS_FAILED = "Failed to set permissions on file";

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(XTerminalWebsocketService.class);

	/** The open sessions. */
	private static Map<String, Session> OPEN_SESSIONS = new ConcurrentHashMap<String, Session>();
	
	/** The session to client. */
	private static Map<String, XTerminalWebsocketClientEndpoint> SESSION_TO_CLIENT = new ConcurrentHashMap<String, XTerminalWebsocketClientEndpoint>();
	
	static {
		runTTYD();
	}
	
	/** The started. */
	static volatile boolean started = false;

	/**
	 * Run TTYD.
	 */
	public synchronized static void runTTYD() {
		if (!started) {
			if (Configuration.isAnonymousModeEnabled()) {
				if (logger.isWarnEnabled()) {logger.warn(TERMINAL_PREFIX + FEATURE_TERMINAL_IS_DISABLED_IN_THIS_MODE);}
				return;
			}
			try {
				String command = "";
				String os = System.getProperty("os.name").toLowerCase();
				if ((os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 )) {
					command = "bash -c ./ttyd.sh";
					File ttydShell = new File("./ttyd.sh");
					if (!ttydShell.exists()) {
						// ttyd binary should be placed in advance to $CATALINA_HOME/bin
						
						createShellScript(ttydShell, "./ttyd -p 9000 bash");
						if (ttydShell.setExecutable(true)) {
							File ttydExecutable = new File("./ttyd");
							createExecutable(XTerminalWebsocketService.class.getResourceAsStream("/ttyd_linux.x86_64_1.6.0"), ttydExecutable);
							if (!ttydExecutable.setExecutable(true)) {
								if (logger.isWarnEnabled()) {logger.warn(TERMINAL_PREFIX + PERMISSIONS_FAILED);}
							}
						} else {
							if (logger.isWarnEnabled()) {logger.warn(TERMINAL_PREFIX + PERMISSIONS_FAILED);}
						}
					}
				} else if (os.indexOf("mac") >= 0) {
					command = "bash -c ./ttyd.sh";
					File ttydShell = new File("./ttyd.sh");
					if (!ttydShell.exists()) {
						// ttyd should be pre-installed with: brew install ttyd
	//					ProcessRunnable processRunnable = new ProcessRunnable("brew install ttyd");
	//					new Thread(processRunnable).start();
	//					processRunnable.getProcess().waitFor();
						
						createShellScript(ttydShell, "ttyd -p 9000 bash");
						ttydShell.setExecutable(true);
					}
				} else if (os.indexOf("win") >= 0) {
					throw new IllegalStateException("Windows is not yet supported");
				} else {
					throw new IllegalStateException("Unknown OS: " + os);
				}
					
				ProcessRunnable processRunnable = new ProcessRunnable(command);
				new Thread(processRunnable).start();
				
			} catch (IOException e) {
				logger.error(TERMINAL_PREFIX + e.getMessage(), e);
			}
			started = true;
		}
	}

	/**
	 * Creates the shell script.
	 *
	 * @param file the file
	 * @param command the command
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void createShellScript(File file, String command) throws FileNotFoundException, IOException {
		try (FileOutputStream fos = new FileOutputStream(file)) {
			IOUtils.write(command, fos, Charset.defaultCharset());
		}
	}
	
	/**
	 * Creates the executable.
	 *
	 * @param in the in
	 * @param file the file
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void createExecutable(InputStream in, File file) throws FileNotFoundException, IOException {
		try (FileOutputStream fos = new FileOutputStream(file)) {
			IOUtils.copy(in, fos);
		}
	}
	
	/**
	 * On open callback.
	 *
	 * @param session
	 *            the session
	 */
	@OnOpen
	public void onOpen(Session session) {
		if (Configuration.isAnonymousModeEnabled() || !Configuration.isTerminalEnabled()) {
			try {
				session.getBasicRemote().sendText(FEATURE_TERMINAL_IS_DISABLED_IN_THIS_MODE, true);
				if (logger.isWarnEnabled()) {logger.warn(FEATURE_TERMINAL_IS_DISABLED_IN_THIS_MODE);}
			} catch (IOException e) {
				logger.error(TERMINAL_PREFIX + e.getMessage(), e);
			}
			try {
				session.close();
			} catch (IOException e) {
				logger.error(TERMINAL_PREFIX + e.getMessage(), e);
			}
			return;
		}
		
		if (logger.isDebugEnabled()) {logger.debug("[ws:terminal] onOpen: " + session.getId());}
		try {
			XTerminalWebsocketClientEndpoint clientEndPoint = startClientWebsocket(session);
			SESSION_TO_CLIENT.put(session.getId(), clientEndPoint);
		} catch (URISyntaxException e) {
			logger.error(TERMINAL_PREFIX + e.getMessage(), e);
			try {
				session.close();
			} catch (IOException e1) {
				logger.error(TERMINAL_PREFIX + e.getMessage(), e);
			}
		}
		OPEN_SESSIONS.put(session.getId(), session);
	}

	/**
	 * On message callback.
	 *
	 * @param message
	 *            the byte buffer
	 * @param session
	 *            the session
	 */
	@OnMessage
	public void onMessage(ByteBuffer message, Session session) {
		if (logger.isTraceEnabled()) {logger.trace("[ws:terminal] onMessage: " + new String(message.array()));}
		
		XTerminalWebsocketClientEndpoint clientEndPoint = SESSION_TO_CLIENT.get(session.getId());
		
		if (clientEndPoint != null) {
			synchronized(clientEndPoint) {
				// send message to websocket
			    clientEndPoint.sendMessage(message);
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
		if (logger.isInfoEnabled()) {logger.info(String.format("[ws:terminal] Session %s error %s", session.getId(), throwable.getMessage()));}
		logger.error(TERMINAL_PREFIX + throwable.getMessage(), throwable);
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
		if (logger.isTraceEnabled()) {logger.trace(String.format("[ws:terminal] Session %s closed because of %s", session.getId(), closeReason));}
		OPEN_SESSIONS.remove(session.getId());
		XTerminalWebsocketClientEndpoint clientEndPoint = SESSION_TO_CLIENT.remove(session.getId());
		try {
			if (clientEndPoint != null && clientEndPoint.getSession() != null) {
				clientEndPoint.getSession().close();
			}
		} catch (IOException e) {
			logger.error(TERMINAL_PREFIX + e.getMessage(), e);
		}
	}

	/**
	 * Start the WebSocket proxy.
	 *
	 * @param session the source session
	 * @return the x terminal websocket client endpoint
	 * @throws URISyntaxException the URI syntax exception
	 */
	private XTerminalWebsocketClientEndpoint startClientWebsocket(Session session) throws URISyntaxException {
		
		final XTerminalWebsocketClientEndpoint clientEndPoint =
				new XTerminalWebsocketClientEndpoint(new URI("ws://localhost:9000/ws"));

        // add listener
        clientEndPoint.addMessageHandler(new XTerminalWebsocketClientEndpoint.MessageHandler() {
            public void handleMessage(ByteBuffer message) throws IOException {
            	session.getBasicRemote().sendBinary(message);
            }
        });
        
        return clientEndPoint;
	}

	/**
	 * The Process Runnable.
	 */
	static class ProcessRunnable implements Runnable {
		
		/** The command. */
		private String command;
		
		/** The process. */
		private Process process;
		
		/**
		 * Instantiates a new process runnable.
		 *
		 * @param command the command
		 */
		ProcessRunnable(String command) {
			this.command = command;
		}
		
		/**
		 * Gets the process.
		 *
		 * @return the process
		 */
		public Process getProcess() {
			return process;
		}

		/**
		 * Run.
		 */
		@Override
		public void run() {
			try {
				this.process = Runtime.getRuntime().exec(this.command);
				
				Thread reader = new Thread(new Runnable() {
		            public void run() {
		                try {
		                	try(BufferedReader input = new BufferedReader(
		    						new InputStreamReader(process.getInputStream()))) {
		    				    String line;
	
		    				    while ((line = input.readLine()) != null) {
		    				    	if (logger.isDebugEnabled()) {logger.debug(TERMINAL_PREFIX + line);}
		    				    }
		    				}
		                } catch (IOException e) {
		                	logger.error(TERMINAL_PREFIX + e.getMessage(), e);
						}
		            }
		        });
				reader.start();
		        
				
				Thread error = new Thread(new Runnable() {
		            public void run() {
		                try {
		                	try(BufferedReader input = new BufferedReader(
		    						new InputStreamReader(process.getErrorStream()))) {
		    				    String line;

		    				    while ((line = input.readLine()) != null) {
		    				    	logger.error(TERMINAL_PREFIX + line);
		    				    }
		    				}
		                } catch (IOException e) {
		                	logger.error(TERMINAL_PREFIX + e.getMessage(), e);
						}
		            }
		        });
				error.start();
				
//				logger.info("[ws:terminal] " + process.exitValue());
			} catch (IOException e) {
				logger.error(TERMINAL_PREFIX + e.getMessage(), e);
			}
			
		}
		
	}

}
