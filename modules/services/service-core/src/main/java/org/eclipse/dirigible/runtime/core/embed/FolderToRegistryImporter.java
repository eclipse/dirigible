/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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
	    private Path targetDir = Paths.get("/");
	    IRepository repository;
	 
	    public FolderToRegistryImporter(Path sourceDir, IRepository repository) {
	        this.sourceDir = sourceDir;
//	        this.targetDir = targetDir;
	        this.repository = repository;
	    }
	 
	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
	 
	        try {
	            Path targetFile = targetDir.resolve(sourceDir.relativize(file));
//	            Files.copy(file, targetFile);
	            byte[] bytes = IOUtils.toByteArray(new FileInputStream(file.toFile()));
	           // create the provided source code as module in the Dirigible's registry
				repository.createResource(
					IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + targetFile.toString(), bytes);
	        } catch (IOException ex) {
	            System.err.println(ex);
	        }
	 
	        return FileVisitResult.CONTINUE;
	    }
	 
	    @Override
	    public FileVisitResult preVisitDirectory(Path dir,
	            BasicFileAttributes attributes) {
//	        try {
//	            Path newDir = targetDir.resolve(sourceDir.relativize(dir));
//	            Files.createDirectory(newDir);
//	        } catch (IOException ex) {
//	            System.err.println(ex);
//	        }
	 
	        return FileVisitResult.CONTINUE;
	    }
}
