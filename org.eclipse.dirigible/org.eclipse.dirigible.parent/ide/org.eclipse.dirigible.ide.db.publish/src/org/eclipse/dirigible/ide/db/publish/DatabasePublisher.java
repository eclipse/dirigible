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

package org.eclipse.dirigible.ide.db.publish;

import static org.eclipse.dirigible.ide.db.publish.DatabaseConstants.REGISTYRY_PUBLISH_LOCATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import org.eclipse.dirigible.ide.common.CommonUtils;
import org.eclipse.dirigible.ide.datasource.DataSourceFacade;
import org.eclipse.dirigible.ide.publish.AbstractPublisher;
import org.eclipse.dirigible.ide.publish.IPublisher;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.db.DatabaseUpdater;
import org.eclipse.dirigible.repository.ext.db.DsvUpdater;
import org.eclipse.dirigible.repository.logging.Logger;

public class DatabasePublisher extends AbstractPublisher implements IPublisher {

	private static final String DOT = ".";
	private static final Logger logger = Logger
			.getLogger(DatabasePublisher.class);

	public DatabasePublisher() {
		super();
	}

	@Override
	public void publish(IProject project) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(
					project, getRegistryLocation());
			final IFolder sourceFolder = getSourceFolder(project,
					ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES);
			copyAllFromTo(sourceFolder, targetContainer);

			IRepository repository = RepositoryFacade.getInstance().getRepository();
			DataSource dataSource = DataSourceFacade.getInstance().getDataSource();
			
			ICollection sourceProjectContainer = getSourceProjectContainer(project);
			ICollection sourceContainer = sourceProjectContainer.getCollection(ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES);

//			# 177
//			processTablesAndViews(targetContainer, repository, dataSource);
//			processDSV(targetContainer, repository, dataSource);
			
			processTablesAndViews(sourceContainer, repository, dataSource);
			processDSV(sourceProjectContainer, repository, dataSource);
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	@Override
	protected String getRegistryLocation() {
		return REGISTYRY_PUBLISH_LOCATION;
	}
	
	// no sandboxing for database artifacts
	@Override
	public void activate(IProject project) throws PublishException {
		publish(project);
	}
	
	@Override
	public void activateFile(IFile file) throws PublishException {
		publish(file.getProject());		
	}

	private void processTablesAndViews(final ICollection targetContainer,
			IRepository repository, DataSource dataSource) throws IOException,
			Exception {
		List<String> knownFiles = new ArrayList<String>();
		DatabaseUpdater databaseUpdater = new DatabaseUpdater(repository,
				dataSource, getRegistryLocation());
		databaseUpdater.enumerateKnownFiles(targetContainer, knownFiles);
		List<String> errors = new ArrayList<String>();
		databaseUpdater.executeUpdate(knownFiles, errors);
		if (errors.size() > 0) {
			throw new PublishException(CommonUtils.concatenateListOfStrings(errors, "\n"));
		}
	}

	private void processDSV(ICollection targetContainer,
			IRepository repository, DataSource dataSource) throws IOException,
			Exception {
		List<String> knownFiles = new ArrayList<String>();
		DsvUpdater dsvUpdater = new DsvUpdater(repository, dataSource, getRegistryLocation());
		dsvUpdater.enumerateKnownFiles(targetContainer, knownFiles);
		List<String> errors = new ArrayList<String>();
		dsvUpdater.executeUpdate(knownFiles, errors);
		if (errors.size() > 0) {
			throw new PublishException(CommonUtils.concatenateListOfStrings(errors, "\n"));
		}
	}

	@Override
	public String getFolderType() {
		return ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES;
	}

	@Override
	public boolean recognizedFile(IFile file) {
		if (checkFolderType(file)) {
			if (DatabaseUpdater.EXTENSION_TABLE.equals(DOT
					+ file.getFileExtension())
					|| DatabaseUpdater.EXTENSION_VIEW.equals(DOT
							+ file.getFileExtension())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPublishedContainerMapping(IFile file) {
		return null;
	}
	
	@Override
	public String getActivatedContainerMapping(IFile file) {
		return null;
	}

	@Override
	public boolean isAutoActivationAllowed() {
		return false;
	}
	
	@Override
	protected String getSandboxLocation() {
		return null;
	}
	
}
