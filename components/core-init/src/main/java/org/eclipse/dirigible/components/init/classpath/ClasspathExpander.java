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
package org.eclipse.dirigible.components.init.classpath;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClasspathExpander {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ClasspathExpander.class);
	
	/** The Constant ROOT. */
	private static final String ROOT = "META-INF/dirigible";
	
	private IRepository repository;
	
	@Autowired
	public ClasspathExpander(IRepository repository) {
		this.repository = repository;
	}
	
	public void expandContent() {
		try {
			Enumeration<URL> urls = ClasspathContentInitializer.class.getClassLoader().getResources("META-INF");
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				URLConnection urlConnection = url.openConnection();
				if (urlConnection instanceof JarURLConnection) {
					JarURLConnection jarUrlConnection = (JarURLConnection) (url.openConnection());
					try (JarFile jar = jarUrlConnection.getJarFile();) {
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							JarEntry entry = entries.nextElement();
							if (entry.getName().startsWith(ROOT)) {
								if (!entry.isDirectory()) {
									if (logger.isDebugEnabled()) {logger.debug("resource found: " + entry);}
									byte[] content = IOUtils.toByteArray(jar.getInputStream(entry));
									String registryPath = entry.getName().substring(ROOT.length());
									repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepository.SEPARATOR + registryPath, content);
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
