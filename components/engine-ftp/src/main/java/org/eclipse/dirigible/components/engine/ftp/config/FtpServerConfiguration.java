/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.ftp.config;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.eclipse.dirigible.components.engine.ftp.domain.FtpUser;
import org.eclipse.dirigible.components.engine.ftp.repository.FtpUserRepository;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * The Class FtpServerConfiguration.
 */
@Configuration
public class FtpServerConfiguration {
	
	private static final String REPOSITORY_ROOT = "./target/dirigible/cms/dirigible/repository/root";

	/**
	 * File system factory.
	 *
	 * @return the file system factory
	 */
	@Bean
	FileSystemFactory fileSystemFactory() {
		NativeFileSystemFactory fileSystemFactory = new NativeFileSystemFactory();
		fileSystemFactory.setCreateHome(true);
		fileSystemFactory.setCaseInsensitive(false);
		return fileSystemFactory::createFileSystemView;
	}

	/**
	 * Nio listener.
	 *
	 * @param port the port
	 * @return the listener
	 */
	@Bean
	Listener nioListener(@Value("${ftp.port:8022}") int port) {
		ListenerFactory listenerFactory = new ListenerFactory();
		listenerFactory.setPort(port);
		return listenerFactory.createListener();
	}

	/**
	 * Ftp server.
	 *
	 * @param ftpletMap the ftplet map
	 * @param userManager the user manager
	 * @param nioListener the nio listener
	 * @param fileSystemFactory the file system factory
	 * @return the ftp server
	 */
	@Bean
	FtpServer ftpServer(Map<String, Ftplet> ftpletMap, UserManager userManager, Listener nioListener, FileSystemFactory fileSystemFactory) {
		FtpServerFactory ftpServerFactory = new FtpServerFactory();
		ftpServerFactory.setListeners(Collections.singletonMap("default", nioListener));
		ftpServerFactory.setFileSystem(fileSystemFactory);
		ftpServerFactory.setFtplets(ftpletMap);
		ftpServerFactory.setUserManager(userManager);
		ftpServerFactory.setConnectionConfig(new ConnectionConfigFactory().createConnectionConfig());
		try {
			userManager.save(new FtpUser("admin", "admin", true, Collections.EMPTY_LIST, -1, REPOSITORY_ROOT, true));
		} catch (FtpException e) {
			throw new RuntimeException(e);
		}
		return ftpServerFactory.createServer();
	}

	/**
	 * Destroys ftp server.
	 *
	 * @param ftpServer the ftp server
	 * @return the disposable bean
	 */
	@Bean
	DisposableBean destroysFtpServer(FtpServer ftpServer) {
		return ftpServer::stop;
	}

	/**
	 * Starts ftp server.
	 *
	 * @param ftpServer the ftp server
	 * @return the initializing bean
	 */
	@Bean
	InitializingBean startsFtpServer(FtpServer ftpServer) {
		return ftpServer::start;
	}

	/**
	 * User manager.
	 *
	 * @param root the root
	 * @param ftpUserRepository the ftp user repository
	 * @return the user manager
	 */
	@Bean
	UserManager userManager(@Value("${ftp.root:${HOME}/ftp/root}") File root, FtpUserRepository ftpUserRepository) {
		Assert.isTrue(root.exists() || root.mkdirs(), "the root directory must exist.");
		return new FtpUserManager(root, ftpUserRepository);
	}

}
