/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.commons.api.content;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.ServiceLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ClasspathContentLoader utility used for loading the resourced from the JAR files in the class path.
 */
public class ClasspathContentLoader {

	private static final Logger logger = LoggerFactory.getLogger(ClasspathContentLoader.class);

	private static final String ROOT = "/";

	private static Boolean LOADED = false;

	/**
	 * Load the resources from the JAR files and give them to the {@link IClasspathContentHandler} instances}.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static final void load() throws IOException {
		synchronized (ClasspathContentLoader.class) {
			if (!LOADED) {
				ServiceLoader<IClasspathContentHandler> contentHandlers = ServiceLoader.load(IClasspathContentHandler.class);
				for (IClasspathContentHandler contentHandler : contentHandlers) {
					String message = "Registering Content Handler: " + contentHandler.getClass().getCanonicalName();
					logger.info(message);
				}
				Enumeration<URL> urls = ClasspathContentLoader.class.getClassLoader().getResources("META-INF");
				while (urls.hasMoreElements()) {
					URL url = urls.nextElement();
					JarURLConnection urlConnection = (JarURLConnection) (url.openConnection());
					try (JarFile jar = urlConnection.getJarFile();) {
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							String entry = entries.nextElement().getName();
							for (IClasspathContentHandler contentHandler : contentHandlers) {
								contentHandler.accept(ROOT + entry);
							}
							logger.trace("resource found: " + entry);
						}
					}
				}
				LOADED = true;
			}
		}
	}

}
