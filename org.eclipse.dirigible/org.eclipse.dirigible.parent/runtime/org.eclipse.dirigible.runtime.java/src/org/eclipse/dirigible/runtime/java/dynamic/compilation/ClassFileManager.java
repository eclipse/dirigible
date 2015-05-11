/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.java.dynamic.compilation;

import java.io.File;
import java.io.IOException;
import java.security.SecureClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

import org.eclipse.dirigible.repository.api.IEntityInformation;
import org.eclipse.dirigible.runtime.scripting.Module;

public class ClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	private static final String DOT = ".";
	private static final String SLASH = "/";
	private static final String PATH_SEPARATOR = "path.separator";
	private static final String separator = System.getProperty(PATH_SEPARATOR);
	
	private static ClassFileManager instance;
	private static final Map<String, JavaFileObject> lastKnownSourceFiles = Collections.synchronizedMap(new HashMap<String, JavaFileObject>());
	
	public synchronized static ClassFileManager getInstance(StandardJavaFileManager standardManager) {
		if (instance == null) {
			instance = new ClassFileManager(standardManager);
		}
		return new ClassFileManager(standardManager);
	}
	
	public static String getFQN(String module) {
		StringBuilder fqn = new StringBuilder(module);
		if (fqn.charAt(0) == SLASH.charAt(0)) {
			fqn.delete(0, 1);
		}
		int indexOf = fqn.indexOf(DOT);
		if (indexOf > 0) {
			fqn.delete(indexOf, fqn.length());
		}

		return fqn.toString().replace(SLASH, DOT);
	}
	
//	public static String getJars() throws URISyntaxException, IOException {
//		URL url = JavaExecutor.class.getProtectionDomain().getCodeSource().getLocation();
//		File libDirectory = new File(url.toURI()).getParentFile();
//		return getJars(libDirectory);
//	}
	
	public static String getJars(File libDirectory) throws IOException {
		
			StringBuilder jars = new StringBuilder();
			
			if (libDirectory == null) {
				throw new IOException("Lib directory is null");
			}
			
			if (!libDirectory.exists()) {
				throw new IOException(String.format("File %s does not exist", libDirectory.getCanonicalFile()));
			}
	
			for (File jar : libDirectory.listFiles()) {
				String jarPath = jar.getCanonicalPath();
				if (jar.isFile()) {
					if (!jarPath.contains(".source_")
							&& jarPath.endsWith(".jar")) { // exclude source bundles
						jars.append(jarPath + separator);
					}
//					if (jarPath.contains("javax.servlet")
//							&& jarPath.endsWith(".jar")) {
//						jars.append(jarPath + separator);
//					}
				} else {
					jars.append(getJars(jar));
				}
			}
			return jars.toString();
	}
	
	public static JavaFileObject getSourceFile(String className) {
		return lastKnownSourceFiles.get(className);
	}
	
	public static List<JavaFileObject> getSourceFiles(List<Module> modules) throws IOException {
		for (Module module : modules) {
			String fqn = getFQN(module.getName());
			long lastModified = getLastModified(module);

			JavaClassObject lastKnownSoruceFile = (JavaClassObject) getSourceFile(fqn);
			if(lastKnownSoruceFile == null || lastKnownSoruceFile.getLastModified() < lastModified) {
				String content = new String(module.getContent());
				lastKnownSourceFiles.put(fqn, new JavaClassObject(fqn, Kind.SOURCE, content, lastModified));
			}
		}
		JavaFileObject[] array = lastKnownSourceFiles.values().toArray(new JavaFileObject[]{});
		return Arrays.asList(array);
	}
	
	private static long getLastModified(Module module) {
		Date lastModified = null;
		IEntityInformation entityInformation = module.getEntityInformation();
		if (entityInformation != null) {
			lastModified = entityInformation.getModifiedAt();
		} else {
			lastModified = new Date();
		}
		return lastModified.getTime();
	}

	private ClassFileManager(StandardJavaFileManager standardManager) {
		super(standardManager);
	}

	@Override
	public ClassLoader getClassLoader(final Location location) {
		return new SecureClassLoader(ClassLoader.getSystemClassLoader()) {
			
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				Class<?> clazz = null;
				JavaClassObject javaClassObject = (JavaClassObject) lastKnownSourceFiles.get(name);
				if (javaClassObject != null) {
					byte[] bytes = javaClassObject.getBytes();
					clazz = super.defineClass(name, bytes, 0, bytes.length);
				} else {
					clazz = Servlet.class.getClassLoader().loadClass(name);
				}
				return clazz;
			}
		};
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
		return lastKnownSourceFiles.get(className);
	}
}
