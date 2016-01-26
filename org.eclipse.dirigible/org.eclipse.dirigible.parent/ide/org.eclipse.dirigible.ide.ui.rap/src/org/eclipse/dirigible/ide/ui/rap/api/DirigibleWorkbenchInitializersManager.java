package org.eclipse.dirigible.ide.ui.rap.api;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.dirigible.ide.common.ExtensionPointUtils;
import org.eclipse.dirigible.ide.ui.rap.entry.DirigibleWorkbench;

public class DirigibleWorkbenchInitializersManager {

	private static final String EXTENSION_POINT_CANNOT_BE_FOUND_S = "Extension point cannot be found: %s";

	private static final String COULD_NOT_CREATE_INITIALIZER_INSTANCE = "Could not create Initializer instance";

	private static final String INITIALIZER_CLASS_IS_INVALID = "Initializer class is invalid";

	private static final String ATTRIBUTE_INITIALIZER = "initializer";

	private static final String ATTRIBUTE_CLASS = "class";

	private static final String EXTENSION_POINT_ID = "org.eclipse.dirigible.ide.ui.rap.init";

	static List<IDirigibleWorkbenchInitializer> initializers = null;

	public static List<IDirigibleWorkbenchInitializer> getInitializers() {

		synchronized (DirigibleWorkbench.class) {
			if (initializers == null) {
				initializers = new ArrayList<IDirigibleWorkbenchInitializer>();
				final IExtensionPoint extensionPoint = ExtensionPointUtils.getExtensionPoint(EXTENSION_POINT_ID);
				if (extensionPoint == null) {
					throw new RuntimeException(format(EXTENSION_POINT_CANNOT_BE_FOUND_S, EXTENSION_POINT_ID));
				}
				final IConfigurationElement[] initializerElements = getInitializerElements(extensionPoint.getExtensions());

				String initializerName = null;
				try {
					for (IConfigurationElement initializerElement : initializerElements) {
						initializerName = initializerElement.getAttribute(ATTRIBUTE_CLASS);
						initializers.add(createInitializer(initializerElement));
					}
				} catch (CoreException ex) {
					throw new RuntimeException(String.format(COULD_NOT_CREATE_INITIALIZER_INSTANCE, initializerName), ex);
				}
			}
			return initializers;
		}

	}

	private static IConfigurationElement[] getInitializerElements(IExtension[] extensions) {
		final List<IConfigurationElement> result = new ArrayList<IConfigurationElement>();
		for (IExtension extension : extensions) {
			for (IConfigurationElement element : extension.getConfigurationElements()) {
				if (ATTRIBUTE_INITIALIZER.equals(element.getName())) {
					result.add(element);
				}
			}
		}
		return result.toArray(new IConfigurationElement[0]);
	}

	private static IDirigibleWorkbenchInitializer createInitializer(IConfigurationElement initializerElement) throws CoreException {
		final Object initializer = initializerElement.createExecutableExtension(ATTRIBUTE_CLASS);
		if (!(initializer instanceof IDirigibleWorkbenchInitializer)) {
			throw new RuntimeException(INITIALIZER_CLASS_IS_INVALID);
		}
		return (IDirigibleWorkbenchInitializer) initializer;
	}

}
