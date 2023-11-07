/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.sftp.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.Collections;

import org.apache.commons.codec.binary.Base64;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class FtpServerConfiguration.
 */
@Configuration
public class SftpServerConfiguration {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SftpServerConfiguration.class);

    private static final String REPOSITORY_ROOT = "./target/dirigible/cms/dirigible/repository/root";


    @Bean
    SshServer sshServer() throws IOException {
        String portValue = org.eclipse.dirigible.commons.config.Configuration.get("DIRIGIBLE_SFTP_PORT", "8022");
        int port = 8022;
        try {
            port = Integer.parseInt(portValue);
        } catch (NumberFormatException e) {
            logger.error("Wrong configuration for SFTP port provided: " + portValue);
        }
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser").toPath()));
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
        String username = org.eclipse.dirigible.commons.config.Configuration.get("DIRIGIBLE_SFTP_USERNAME", "YWRtaW4="); // admin
        String password = org.eclipse.dirigible.commons.config.Configuration.get("DIRIGIBLE_SFTP_PASSWORD", "YWRtaW4="); // admin
        sshd.setPasswordAuthenticator(
                (u, p, session) -> u.equals(new String(new Base64().decode(username.getBytes()), StandardCharsets.UTF_8).trim())
                        && p.equals(new String(new Base64().decode(password.getBytes()), StandardCharsets.UTF_8).trim()));
        String defaultHome = new File(REPOSITORY_ROOT).getCanonicalPath();
        sshd.setFileSystemFactory(new VirtualFileSystemFactory(FileSystems.getDefault()
                                                                          .getPath(defaultHome)));
        sshd.start();
        logger.info("SFTP server started at: " + defaultHome);
        return sshd;
    }

}
