package org.eclipse.dirigible.core.extensions.synchronizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.logging.LoggingHelper;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsConstants;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExtensionsSynchronizer implements IExtensionsConstants {
	
	private static final Logger logger = LoggerFactory.getLogger(ExtensionsSynchronizer.class);
	
	private static final Map<String, ExtensionPointDefinition> EXTENSION_POINTS_PREDELIVERED = Collections.synchronizedMap(new HashMap<String, ExtensionPointDefinition>());
	
	private static final Map<String, ExtensionDefinition> EXTENSIONS_PREDELIVERED = Collections.synchronizedMap(new HashMap<String, ExtensionDefinition>());
	
	private static final List<String> EXTENSION_POINTS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());
	
	private static final List<String> EXTENSIONS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());
	
	private static LoggingHelper loggingHelper = new LoggingHelper(logger);
	
	@Inject
	private ExtensionsCoreService extensionsCoreService;
	
	@Inject
	private IRepository repository;
	
	public void registerPredeliveredExtensionPoint(String extensionPointPath) throws IOException {
		InputStream in = ExtensionsSynchronizer.class.getResourceAsStream(extensionPointPath);
		String json = IOUtils.toString(in, Configuration.UTF8);
		ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.parseExtensionPoint(json);
		EXTENSION_POINTS_PREDELIVERED.put(extensionPointPath, extensionPointDefinition);
	}
	
	public void registerPredeliveredExtension(String extensionPath) throws IOException {
		InputStream in = ExtensionsSynchronizer.class.getResourceAsStream(extensionPath);
		String json = IOUtils.toString(in, Configuration.UTF8);
		ExtensionDefinition extensionDefinition = extensionsCoreService.parseExtension(json);
		EXTENSIONS_PREDELIVERED.put(extensionPath, extensionDefinition);
	}
	
	public void synchronizeAll() {
		loggingHelper.beginGroup("Synchronizing Extension Points and Extensions...");
		try {
			EXTENSION_POINTS_SYNCHRONIZED.clear();
			EXTENSIONS_SYNCHRONIZED.clear();
			synchronizePredelivered();
			synchronizeRegistry();
			cleanup();
		} catch (ExtensionsException e) {
			logger.error("Synchronizing process for Extension Points and Extensions failed.", e);
		}
		loggingHelper.endGroup("Done synchronizing Extension Points and Extensions.");
	}

	private void synchronizePredelivered() throws ExtensionsException {
		loggingHelper.debug("Synchronizing predelivered Extension Points and Extensions...");
		// Extension Points
		for (ExtensionPointDefinition extensionPointDefinition : EXTENSION_POINTS_PREDELIVERED.values()) {
			synchronizeExtensionPoint(extensionPointDefinition);
		}
		// Extensions
		for (ExtensionDefinition extensionDefinition : EXTENSIONS_PREDELIVERED.values()) {
			synchronizeExtension(extensionDefinition);
		}
		loggingHelper.debug("Done synchronizing predelivered Extension Points and Extensions.");
	}

	

	private void synchronizeExtensionPoint(ExtensionPointDefinition extensionPointDefinition) throws ExtensionsException {
		if (!extensionsCoreService.existsExtensionPoint(extensionPointDefinition.getLocation())) {
			extensionsCoreService.createExtensionPoint(extensionPointDefinition.getLocation(), extensionPointDefinition.getDescription());
			loggingHelper.info("Synchronized Extension Point [{}]", extensionPointDefinition.getLocation());
		}
		EXTENSION_POINTS_SYNCHRONIZED.add(extensionPointDefinition.getLocation());
	}
	
	private void synchronizeExtension(ExtensionDefinition extensionDefinition) throws ExtensionsException {
		if (!extensionsCoreService.existsExtension(extensionDefinition.getLocation())) {
			extensionsCoreService.createExtension(extensionDefinition.getLocation(), extensionDefinition.getExtensionPoint(), extensionDefinition.getDescription());
			loggingHelper.info("Synchronized Extension [{}] for Extension Point [{}]", extensionDefinition.getLocation(), extensionDefinition.getExtensionPoint());
		}
		EXTENSIONS_SYNCHRONIZED.add(extensionDefinition.getLocation());
	}
	
	private void synchronizeRegistry() throws ExtensionsException {
		loggingHelper.debug("Synchronizing Extension Points and Extensions from Registry...");
		
		ICollection collection = repository.getCollection(IRepositoryStructure.REGISTRY_PUBLIC);
		if (collection.exists()) {
			synchronizeCollection(collection);
		}
	
		loggingHelper.debug("Done synchronizing Extension Points and Extensions from Registry.");
	}

	private void synchronizeCollection(ICollection collection) throws ExtensionsException {
		List<IResource> resources = collection.getResources();
		for (IResource resource : resources) {
			String resourceName = resource.getName();
			if (resourceName.endsWith(FILE_EXTENSION_EXTENSIONPOINT)) {
				ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.parseExtensionPoint(resource.getContent());
				synchronizeExtensionPoint(extensionPointDefinition);
			}
			
			if (resourceName.endsWith(FILE_EXTENSION_EXTENSION)) {
				ExtensionDefinition extensionDefinition = extensionsCoreService.parseExtension(resource.getContent());
				synchronizeExtension(extensionDefinition);
			}
		}
		List<ICollection> collections = collection.getCollections();
		for (ICollection childCollection : collections) {
			synchronizeCollection(childCollection);
		}
	}
	
	private void cleanup() throws ExtensionsException {
		loggingHelper.debug("Cleaning up Extension Points and Extensions...");
		
		List<ExtensionPointDefinition> extensionPointDefinitions = extensionsCoreService.getExtensionPoints();
		for (ExtensionPointDefinition extensionPointDefinition : extensionPointDefinitions) {
			if (!EXTENSION_POINTS_SYNCHRONIZED.contains(extensionPointDefinition.getLocation())) {
				extensionsCoreService.removeExtensionPoint(extensionPointDefinition.getLocation());
				loggingHelper.warn("Cleaned up Extension Point [{}]", extensionPointDefinition.getLocation());
			}
		}
		
		List<ExtensionDefinition> extensionDefinitions = extensionsCoreService.getExtensions();
		for (ExtensionDefinition extensionDefinition : extensionDefinitions) {
			if (!EXTENSIONS_SYNCHRONIZED.contains(extensionDefinition.getLocation())) {
				extensionsCoreService.removeExtension(extensionDefinition.getLocation());
				loggingHelper.warn("Cleaned up Extension [{}]", extensionDefinition.getLocation());
			}
		}
		
		loggingHelper.debug("Done cleaning up Extension Points and Extensionsy.");
	}
}
