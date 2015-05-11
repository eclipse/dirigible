/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.ide.registry;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IMarkerImageProvider;
import org.eclipse.ui.internal.ide.Policy;
import org.osgi.framework.Bundle;

/**
 * Implementation of a marker image registry which maps either a marker type to
 * a provider or to a static image.
 */
public class MarkerImageProviderRegistry {
	private static final String ATT_PROVIDER_CLASS = "class";//$NON-NLS-1$

	private static final String ATT_ICON = "icon";//$NON-NLS-1$

	private static final String ATT_MARKER_TYPE = "markertype";//$NON-NLS-1$

	private static final String ATT_ID = "id";//$NON-NLS-1$

	private static final String MARKER_ATT_KEY = "org.eclipse.ui.internal.registry.MarkerImageProviderRegistry";//$NON-NLS-1$

	private static final String TAG_PROVIDER = "imageprovider";//$NON-NLS-1$

	private ArrayList<Descriptor> descriptors = new ArrayList<Descriptor>();

	class Descriptor {
		String id;

		String markerType;

		String className;

		String imagePath;

		ImageDescriptor imageDescriptor;

		IConfigurationElement element;

		Bundle pluginBundle;

		IMarkerImageProvider provider;
	}

	/**
	 * Initialize this new MarkerImageProviderRegistry.
	 */
	public MarkerImageProviderRegistry() {
		class MarkerImageReader extends IDERegistryReader {
			protected boolean readElement(IConfigurationElement element) {
				if (element.getName().equals(TAG_PROVIDER)) {
					addProvider(element);
					return true;
				}

				return false;
			}

			public void readRegistry() {
				readRegistry(Platform.getExtensionRegistry(),
						IDEWorkbenchPlugin.IDE_WORKBENCH,
						IDEWorkbenchPlugin.PL_MARKER_IMAGE_PROVIDER);
			}
		}

		new MarkerImageReader().readRegistry();
	}

	/**
	 * Creates a descriptor for the marker provider extension and add it to the
	 * list of providers.
	 */
	@SuppressWarnings("deprecation")
	public void addProvider(IConfigurationElement element) {
		Descriptor desc = new Descriptor();
		desc.element = element;
		desc.pluginBundle = Platform.getBundle(element.getNamespace());
		desc.id = element.getAttribute(ATT_ID);
		desc.markerType = element.getAttribute(ATT_MARKER_TYPE);
		desc.imagePath = element.getAttribute(ATT_ICON);
		desc.className = element.getAttribute(ATT_PROVIDER_CLASS);
		if (desc.imagePath != null) {
			desc.imageDescriptor = getImageDescriptor(desc);
		}
		if (desc.className == null) {
			// Don't need to keep these references.
			desc.element = null;
			desc.pluginBundle = null;
		}
		descriptors.add(desc);
	}

	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(Object)
	 */
	public ImageDescriptor getImageDescriptor(IMarker marker) {
		int size = descriptors.size();
		for (int i = 0; i < size; i++) {
			Descriptor desc = (Descriptor) descriptors.get(i);
			try {
				if (marker.isSubtypeOf(desc.markerType)) {
					if (desc.className != null) {
						if (desc.pluginBundle.getState() == Bundle.ACTIVE) {
							// -- Get the image descriptor from the provider.
							// -- Save the image descriptor url as a persistable
							// property, so a
							// image descriptor can be created without
							// activating the plugin next
							// time the workbench is started.
							if (desc.provider == null) {
								desc.provider = (IMarkerImageProvider) IDEWorkbenchPlugin
										.createExtension(desc.element,
												ATT_PROVIDER_CLASS);
							}
							String path = desc.provider.getImagePath(marker);
							if (path != desc.imagePath) {
								desc.imagePath = path;
								desc.imageDescriptor = getImageDescriptor(desc);
								return desc.imageDescriptor;
							}
							return desc.imageDescriptor;
						} else {
							if (desc.imageDescriptor == null) {
								// Create a image descriptor to be used until
								// the plugin gets activated.
								desc.imagePath = (String) marker
										.getAttribute(MARKER_ATT_KEY);
								desc.imageDescriptor = getImageDescriptor(desc);
							}
							return desc.imageDescriptor;
						}
					} else if (desc.imageDescriptor != null) {
						return desc.imageDescriptor;
					}
				}
			} catch (CoreException e) {
				Policy.handle(e);
				return null;
			}
		}
		return null;
	}

	/**
	 * Returns the image descriptor with the given relative path.
	 */
	@SuppressWarnings("deprecation")
	ImageDescriptor getImageDescriptor(Descriptor desc) {
		URL url = Platform.find(desc.pluginBundle, new Path(desc.imagePath));
		return ImageDescriptor.createFromURL(url);
	}
}
