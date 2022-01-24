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
package org.eclipse.dirigible.api.v3.io;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

public class FTPFacade {

	public static FTPClient connect(String host, int port, String username, String password) throws SocketException, IOException {
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(host, port);
		if (username != null && password != null) {
			ftpClient.login(username, password);
		}
		return ftpClient;
	}

	public static void disconnect(FTPClient ftpClient) throws IOException {
		if (ftpClient.isConnected()) {
			ftpClient.logout();
			ftpClient.disconnect();
		}
	}
}
