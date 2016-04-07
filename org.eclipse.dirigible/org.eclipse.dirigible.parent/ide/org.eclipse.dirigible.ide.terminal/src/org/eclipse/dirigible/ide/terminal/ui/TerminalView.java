/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.terminal.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.repository.ext.command.Piper;
import org.eclipse.dirigible.repository.ext.command.ProcessUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class TerminalView extends ViewPart {

	private static final String EXECUTING_SHELL_COMMANDS_IS_DISABLED_IN_THIS_INSTANCE = "Executing shell commands is disabled in this instance";

	private static final String SHELL_COMMAND = "Shell Command";

	private static final String EXEEDS_TIMEOUT = "Exeeds timeout - ";

	private static final Logger logger = Logger.getLogger(TerminalView.class);

	private static Font terminalFont = new Font(null, "Courier New", 14, SWT.NORMAL);

	private Text commandLine;

	private Text commandHistory;

	public TerminalView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, false);

		parent.setLayout(layout);
		parent.setBackground(new Color(null, 0, 0, 0));
		parent.setForeground(new Color(null, 0, 255, 0));

		commandHistory = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
		commandHistory.setLayoutData(new GridData(GridData.FILL_BOTH));
		commandHistory.setBackground(new Color(null, 0, 0, 0));
		commandHistory.setForeground(new Color(null, 0, 255, 0));
		commandHistory.setFont(terminalFont);

		commandLine = new Text(parent, SWT.BORDER);
		commandLine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		commandLine.setBackground(new Color(null, 0, 0, 0));
		commandLine.setForeground(new Color(null, 0, 255, 0));
		commandLine.setFont(terminalFont);
		commandLine.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				//
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR) {

					if (!CommonParameters.isRolesEnabled()) {
						// assume trial instance, hence disable this function
						MessageDialog.openInformation(null, SHELL_COMMAND, EXECUTING_SHELL_COMMANDS_IS_DISABLED_IN_THIS_INSTANCE);
						return;
					}

					try {
						String result = executeCommand(ProcessUtils.translateCommandline(commandLine.getText()));
						commandHistory.setText(result);
					} catch (IOException ex) {
						commandHistory.setText(ex.getMessage());
					}
				}

			}
		});

	}

	@Override
	public void setFocus() {
		commandLine.setFocus();
	}

	private static String executeCommand(String[] args) throws IOException {
		if (args.length <= 0) {
			return "Need command to run";
		}

		IPreferenceStore preferenceStore = TerminalPreferencePage.getTerminalPreferenceStore();
		boolean limitEnabled = preferenceStore.getBoolean(TerminalPreferencePage.LIMIT_ENABLED);
		int limitTimeout = preferenceStore.getInt(TerminalPreferencePage.LIMIT_TIMEOUT);

		ProcessBuilder processBuilder = ProcessUtils.createProcess(args);
		ProcessUtils.addEnvironmentVariables(processBuilder, null);
		ProcessUtils.removeEnvironmentVariables(processBuilder, null);
		// processBuilder.directory(new File(workingDirectory));
		processBuilder.redirectErrorStream(true);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Process process = ProcessUtils.startProcess(args, processBuilder);
		Piper pipe = new Piper(process.getInputStream(), out);
		new Thread(pipe).start();
		try {
			// process.waitFor();

			int i = 0;
			boolean deadYet = false;
			do {
				Thread.sleep(ProcessUtils.DEFAULT_WAIT_TIME);
				try {
					process.exitValue();
					deadYet = true;
				} catch (IllegalThreadStateException e) {
					if (limitEnabled) {
						if (++i >= limitTimeout) {
							process.destroy();
							throw new RuntimeException(EXEEDS_TIMEOUT + ((ProcessUtils.DEFAULT_WAIT_TIME / 1000) * limitTimeout));
						}
					}
				}
			} while (!deadYet);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return e.getMessage();
		}
		return new String(out.toByteArray());
	}

}
