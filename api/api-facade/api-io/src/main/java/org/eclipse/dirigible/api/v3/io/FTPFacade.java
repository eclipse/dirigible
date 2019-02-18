/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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
