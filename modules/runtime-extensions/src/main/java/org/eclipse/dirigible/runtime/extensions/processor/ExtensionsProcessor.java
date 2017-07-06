package org.eclipse.dirigible.runtime.extensions.processor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;

/**
 * Processing the Extensions Service incoming requests 
 *
 */
public class ExtensionsProcessor {
	
	@Inject
	private ExtensionsCoreService extensionsCoreService;
	
	public String renderTree() throws ExtensionsException {
		List<ExtensionPointBundle> bundles = new ArrayList<ExtensionPointBundle>();
		List<ExtensionPointDefinition> extensionPoints = extensionsCoreService.getExtensionPoints();
		for (ExtensionPointDefinition extensionPointDefinition : extensionPoints) {
			List<ExtensionDefinition> extensions = extensionsCoreService.getExtensionsByExtensionPoint(extensionPointDefinition.getLocation());
			ExtensionPointBundle bundle = new ExtensionPointBundle(extensionPointDefinition, extensions);
			bundles.add(bundle);
		}
		return GsonHelper.GSON.toJson(bundles);
	}
	
}
