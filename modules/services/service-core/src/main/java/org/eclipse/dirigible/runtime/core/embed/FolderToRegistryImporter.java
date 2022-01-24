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
package org.eclipse.dirigible.runtime.core.embed;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

public class FolderToRegistryImporter extends SimpleFileVisitor<Path> {
	
	    private Path sourceDir;
	    private Path targetDir = Paths.get(IRepositoryStructure.SEPARATOR);
	    IRepository repository;
	 
	    public FolderToRegistryImporter(Path sourceDir, IRepository repository) {
	        this.sourceDir = sourceDir;
	        this.repository = repository;
	    }
	 
	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
	    	FileInputStream input = null;
	        try {
	            Path targetFile = targetDir.resolve(sourceDir.relativize(file));
	            input = new FileInputStream(file.toFile());
				byte[] bytes = IOUtils.toByteArray(input);
	           // create the provided source code as module in the Dirigible's registry
				repository.createResource(
					IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + targetFile.toString(), bytes);
	        } catch (IOException ex) {
	            System.err.println(ex);
	        } finally {
	        	if (input != null) {
	        		try {
						input.close();
					} catch (IOException e) {
						// do nothing
					}
	        	}
	        }
	 
	        return FileVisitResult.CONTINUE;
	    }
	 
	    @Override
	    public FileVisitResult preVisitDirectory(Path dir,
	            BasicFileAttributes attributes) {
	        return FileVisitResult.CONTINUE;
	    }
}
