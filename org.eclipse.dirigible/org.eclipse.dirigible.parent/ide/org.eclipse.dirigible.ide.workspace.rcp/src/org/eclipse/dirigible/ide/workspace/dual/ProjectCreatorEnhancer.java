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

package org.eclipse.dirigible.ide.workspace.dual;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

import org.eclipse.dirigible.repository.api.ICommonConstants;

public class ProjectCreatorEnhancer {
	
	public static void enhance(IProject project) throws CoreException {
//		project.refreshLocal(1, null);
////		createProjectFile(project);
//		createClasspathFile(project);
		
		try {
			//set the Java nature
			IProjectDescription description = project.getDescription();
			description.setNatureIds(new String[] { JavaCore.NATURE_ID }); // ModuleCoreNature?
			 
			//create the project
			project.setDescription(description, null);
			IJavaProject javaProject = JavaCore.create(project);
			 
			ClasspathEntry cpWeb = new ClasspathEntry(IPackageFragmentRoot.K_BINARY,IClasspathEntry.CPE_CONTAINER,
					new Path("org.eclipse.jst.j2ee.internal.web.container"),
					ClasspathEntry.INCLUDE_ALL,ClasspathEntry.EXCLUDE_NONE,
					null,null,null,false,(IAccessRule[])null,false,ClasspathEntry.NO_EXTRA_ATTRIBUTES);
			
			ClasspathEntry cpModule = new ClasspathEntry(IPackageFragmentRoot.K_BINARY,IClasspathEntry.CPE_CONTAINER,
					new Path("org.eclipse.jst.j2ee.internal.module.container"),
					ClasspathEntry.INCLUDE_ALL,ClasspathEntry.EXCLUDE_NONE,
					null,null,null,false,(IAccessRule[])null,false,ClasspathEntry.NO_EXTRA_ATTRIBUTES);
			
			ClasspathEntry cpTomcat = new ClasspathEntry(IPackageFragmentRoot.K_BINARY,IClasspathEntry.CPE_CONTAINER,
					new Path("org.eclipse.jst.server.core.container/org.eclipse.jst.server.tomcat.runtimeTarget/Apache Tomcat v7.0"),
					ClasspathEntry.INCLUDE_ALL,ClasspathEntry.EXCLUDE_NONE,
					null,null,null,false,(IAccessRule[])null,false,ClasspathEntry.NO_EXTRA_ATTRIBUTES);
			

			//set the build path
			IClasspathEntry[] buildPath = {
					JavaCore.newSourceEntry(project.getFullPath().append(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES)),
							JavaRuntime.getDefaultJREContainerEntry(),
							cpWeb, cpModule, cpTomcat};
			

			javaProject.setRawClasspath(buildPath, project.getFullPath().append("bin"), null);
			
		} catch (JavaModelException e) {
			throw new CoreException(new Status(IStatus.ERROR, // NOPMD
					"org.eclipse.dirigible.ide.workspace.rcp", e.getMessage())); //$NON-NLS-1$ // NOPMD
		}

	}

}
