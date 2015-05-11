package org.eclipse.dirigible.repository.ext.extensions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.ext.db.AbstractDataUpdater;
import org.eclipse.dirigible.repository.logging.Logger;

public class ExtensionUpdater extends AbstractDataUpdater {
	
	private static final String NODE_DESCRIPTION = "description";
	private static final String NODE_EXTENSION_POINT = "extension-point";
	private static final String NODE_EXTENSION = "extension";
	public static final String EXTENSION_EXTENSION = ".extension"; //$NON-NLS-1$
	public static final String EXTENSION_EXTENSION_POINT = ".extensionpoint"; //$NON-NLS-1$
	public static final String REGISTRY_EXTENSION_DEFINITIONS_DEFAULT = ICommonConstants.EXTENSION_REGISTRY_PUBLISH_LOCATION;

	private static final Logger logger = Logger.getLogger(ExtensionUpdater.class);
	
	private IRepository repository;
	private DataSource dataSource;
	private String location;
	private ExtensionManager extensionManager;

	public ExtensionUpdater(IRepository repository, DataSource dataSource,
			String location) {
		this.repository = repository;
		this.dataSource = dataSource;
		this.location = location;
		this.extensionManager = ExtensionManager.getInstance(repository, dataSource);
	}

	@Override
	public void executeUpdate(List<String> knownFiles,
			HttpServletRequest request, List<String> errors) throws Exception {
		if (knownFiles.size() == 0) {
			return;
		}

		try {
			Connection connection = dataSource.getConnection();

			try {
				for (Iterator<String> iterator = knownFiles.iterator(); iterator
						.hasNext();) {
					String dsDefinition = iterator.next();
					try {
						if (dsDefinition.endsWith(EXTENSION_EXTENSION)) {
							executeExtensionUpdate(connection, dsDefinition, request);
						} else if (dsDefinition.endsWith(EXTENSION_EXTENSION_POINT)) {
							executeExtensionPointUpdate(connection, dsDefinition, request);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						errors.add(e.getMessage());
					}
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void executeExtensionUpdate(Connection connection,
			String scDefinition, HttpServletRequest request)
			throws SQLException, IOException {
		JsonObject edDefinition = parseExtension(scDefinition);
		String extensionName = edDefinition.get(NODE_EXTENSION).getAsString(); //$NON-NLS-1$
		String extensionPointName = edDefinition.get(NODE_EXTENSION_POINT).getAsString(); //$NON-NLS-1$
		String extensionDescription = edDefinition.get(NODE_DESCRIPTION).getAsString(); //$NON-NLS-1$
		ExtensionDefinition extensionDefinition = this.extensionManager.getExtension(extensionName, extensionPointName);
		if (extensionDefinition == null) {
			this.extensionManager.createExtension(extensionName, extensionPointName, extensionDescription, request);
		} else {
			this.extensionManager.updateExtension(extensionName, extensionPointName, extensionDescription, request);
		}
	}
	
	private void executeExtensionPointUpdate(Connection connection,
			String scDefinition, HttpServletRequest request)
			throws SQLException, IOException {
		JsonObject edDefinition = parseExtensionPoint(scDefinition);
		String extensionPointName = edDefinition.get(NODE_EXTENSION_POINT).getAsString(); //$NON-NLS-1$
		String extensionDescription = edDefinition.get(NODE_DESCRIPTION).getAsString(); //$NON-NLS-1$
		ExtensionPointDefinition extensionPointDefinition = this.extensionManager.getExtensionPoint(extensionPointName);
		if (extensionPointDefinition == null) {
			this.extensionManager.createExtensionPoint(extensionPointName, extensionDescription, request);
		} else {
			this.extensionManager.updateExtensionPoint(extensionPointName, extensionDescription, request);
		}
	}


	private JsonObject parseExtension(String dsDefinition) throws IOException {
		// {
		// "extension":"/${projectName}/extension",
		// "extension-point":"/${projectName}/extension-point",
		// "description":"description for the extension"
		// }

		IRepository repository = this.repository;
//		# 177
//		IResource resource = repository.getResource(this.location + dsDefinition);
		IResource resource = repository.getResource(dsDefinition);
		
		String content = new String(resource.getContent());
		JsonParser parser = new JsonParser();
		JsonObject dsDefinitionObject = (JsonObject) parser.parse(content);

		// TODO validate the parsed content has the right structure

		return dsDefinitionObject;
	}
	
	private JsonObject parseExtensionPoint(String dsDefinition) throws IOException {
		// {
		// "extension-point":"/${projectName}/extension-point",
		// "description":"description for the extension point"
		// }

		IRepository repository = this.repository;
//		#177
//		IResource resource = repository.getResource(this.location + dsDefinition);
		IResource resource = repository.getResource(dsDefinition);
		
		String content = new String(resource.getContent());
		JsonParser parser = new JsonParser();
		JsonObject dsDefinitionObject = (JsonObject) parser.parse(content);

		// TODO validate the parsed content has the right structure

		return dsDefinitionObject;
	}

	public void enumerateKnownFiles(ICollection collection,
			List<String> dsDefinitions) throws IOException {
		if (collection.exists()) {
			List<IResource> resources = collection.getResources();
			for (Iterator<IResource> iterator = resources.iterator(); iterator
					.hasNext();) {
				IResource resource = iterator.next();
				if (resource != null && resource.getName() != null) {
					if (resource.getName().endsWith(EXTENSION_EXTENSION)
							|| resource.getName().endsWith(EXTENSION_EXTENSION_POINT)) {
//						# 177
//						String fullPath = collection.getPath().substring(
//								this.location.length())
//								+ IRepository.SEPARATOR + resource.getName();
						String fullPath = resource.getPath();
						dsDefinitions.add(fullPath);
					}
				}
			}

			List<ICollection> collections = collection.getCollections();
			for (Iterator<ICollection> iterator = collections.iterator(); iterator
					.hasNext();) {
				ICollection subCollection = iterator.next();
				enumerateKnownFiles(subCollection, dsDefinitions);
			}
		}
	}

	public void applyUpdates() throws IOException, Exception {
		List<String> knownFiles = new ArrayList<String>();
		ICollection srcContainer = this.repository.getCollection(this.location);
		if (srcContainer.exists()) {
			enumerateKnownFiles(srcContainer, knownFiles);// fill knownFiles[]
															// with urls to
															// recognizable
															// repository files
			executeUpdate(knownFiles, null);// execute the real updates
		}
	}
	
	@Override
	public IRepository getRepository() {
		return repository;
	}
	
	@Override
	public String getLocation() {
		return location;
	}
	
	@Override
	public void executeUpdate(List<String> knownFiles, List<String> errors) throws Exception {
		executeUpdate(knownFiles, null, errors);
	}

}
