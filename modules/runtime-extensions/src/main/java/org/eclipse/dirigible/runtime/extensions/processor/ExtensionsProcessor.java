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
	
	public String renderExtensionPoints() throws ExtensionsException {
		List<ExtensionPoint> bundles = new ArrayList<ExtensionPoint>();
		List<ExtensionPointDefinition> extensionPoints = extensionsCoreService.getExtensionPoints();
		for (ExtensionPointDefinition extensionPointDefinition : extensionPoints) {
			List<ExtensionDefinition> extensions = extensionsCoreService.getExtensionsByExtensionPoint(extensionPointDefinition.getName());
			ExtensionPoint bundle = new ExtensionPoint(extensionPointDefinition, extensions);
			bundles.add(bundle);
		}
		return GsonHelper.GSON.toJson(bundles);
	}
	
	public String renderExtensionPoint(String name) throws ExtensionsException {
		ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.getExtensionPointByName(name);
		if (extensionPointDefinition == null) {
			return null;
		}
		List<ExtensionDefinition> extensions = extensionsCoreService.getExtensionsByExtensionPoint(extensionPointDefinition.getName());
		ExtensionPoint bundle = new ExtensionPoint(extensionPointDefinition, extensions);
		return GsonHelper.GSON.toJson(bundle);
	}
	
}
