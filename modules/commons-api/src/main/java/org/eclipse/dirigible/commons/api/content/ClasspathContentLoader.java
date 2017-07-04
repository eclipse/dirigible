package org.eclipse.dirigible.commons.api.content;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathContentLoader {
	
	private static final Logger logger = LoggerFactory.getLogger(ClasspathContentLoader.class);
	
	private static List<String> RESOURCES = null;
	
	public static final void load() throws IOException {
		synchronized (ClasspathContentLoader.class) {
			if (RESOURCES == null) {
				List<String> resources = new ArrayList<String>();
				Enumeration<URL> urls = ClasspathContentLoader.class.getClassLoader().getResources("META-INF");
				while (urls.hasMoreElements()) {
					URL url = urls.nextElement();
					JarURLConnection urlConnection = (JarURLConnection) (url.openConnection());
			        try (JarFile jar = urlConnection.getJarFile();) {
			            Enumeration<JarEntry> entries = jar.entries();
			            while (entries.hasMoreElements()) {
			                String entry = entries.nextElement().getName();
			                if (entry.endsWith(".class") 
			                		|| entry.endsWith(".java") 
			                		|| entry.endsWith("/")
			                		|| entry.endsWith("MANIFEST.MF")
			                		|| entry.endsWith("pom.xml")
			                		|| entry.endsWith("pom.properties")) {
			                	continue;
			                }
			                resources.add(entry);
			                logger.debug("resource found: " + entry);
			            }
			        }
				}
				RESOURCES = Collections.unmodifiableList(resources);
			}
		}
	}
	
	public static List<String> getResources() throws IOException {
		if (RESOURCES == null) {
			load();
		}
		return RESOURCES;
	}

}
