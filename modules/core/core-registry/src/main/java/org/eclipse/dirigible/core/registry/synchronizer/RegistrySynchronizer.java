package org.eclipse.dirigible.core.registry.synchronizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.eclipse.dirigible.commons.api.helpers.FileSystemUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RegistrySynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(RegistrySynchronizer.class);

	/** The Constant DIRIGIBLE_REGISTRY_SYNCH_ROOT_FOLDER. */
	public static final String DIRIGIBLE_REGISTRY_SYNCH_ROOT_FOLDER = "DIRIGIBLE_REGISTRY_SYNCH_ROOT_FOLDER"; //$NON-NLS-1$

	private Map<String, String> resourceLocations = Collections.synchronizedMap(new HashMap<String, String>());
	private Map<String, Boolean> targetLocations = Collections.synchronizedMap(new HashMap<String, Boolean>());
	private String rootFolder = null;

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized(RegistrySynchronizer.class) {
			try {
				rootFolder = Configuration.get(DIRIGIBLE_REGISTRY_SYNCH_ROOT_FOLDER);
				if (rootFolder != null) {
					logger.trace("Synchronizing registry.");
					synchronizeRegistry();
					synchronizeResources();
					cleanup();
					logger.trace("Done synchronizing registry.");
				}
			} catch (Exception e) {
				logger.error("Synchronizing registry failed.", e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String sourceLocation = resource.getPath();
		String targetLocation = new RepositoryPath(rootFolder, sourceLocation).toString();
		resourceLocations.put(sourceLocation, targetLocation);
		targetLocations.put(targetLocation, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		resourceLocations.clear();

		// Set "dirty" flag, for the target location files
		for (Entry<String, Boolean> next: targetLocations.entrySet()) {
			next.setValue(false);
		}
	}

	/**
	 * Synchronize registry resources.
	 *
	 * @throws SynchronizationException
	 *             the synchronization exception
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws RepositoryReadException 
	 */
	private void synchronizeResources() throws SynchronizationException, RepositoryReadException, FileNotFoundException, IOException {
		for (Entry<String, String> next : resourceLocations.entrySet()) {
			String sourceLocation = next.getKey();
			String targetLocation = next.getValue();
			IResource sourceResource = getRepository().getResource(sourceLocation);
			FileSystemUtils.saveFile(targetLocation, sourceResource.getContent());
		}

		List<String> removeTargetLocations = new ArrayList<String>();
		for (Entry<String, Boolean> next : targetLocations.entrySet()) {
			Boolean locationExistsInRegistry = next.getValue();
			String targetLocation = next.getKey();

			// Check for "dirty" files in the target location
			if (!locationExistsInRegistry) {
				FileSystemUtils.removeFile(targetLocation);
				removeTargetLocations.add(next.getKey());
			}
		}

		for (String nextLocaation : removeTargetLocations) {
			targetLocations.remove(nextLocaation);
		}
			
	}
}
