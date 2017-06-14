/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.ide;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.internal.ide.registry.MarkerImageProviderRegistry;
import org.eclipse.ui.internal.ide.registry.ProjectImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import com.ibm.icu.text.MessageFormat;

/**
 * This internal class represents the top of the IDE workbench.
 * This class is responsible for tracking various registries font, preference,
 * graphics, dialog store.
 * This class is explicitly referenced by the IDE workbench plug-in's
 * "plugin.xml"
 *
 * @since 3.0
 */
public class IDEWorkbenchPlugin extends AbstractUIPlugin {

	private static final String EXCEPTION_IN_0_1_2 = "Exception in {0}.{1}: {2}";

	// Default instance of the receiver
	private static IDEWorkbenchPlugin inst;

	// Global workbench ui plugin flag. Only workbench implementation is allowed
	// to use this flag
	// All other plugins, examples, or test cases must *not* use this flag.
	public static boolean DEBUG = false;

	/**
	 * The IDE workbench plugin ID.
	 */
	public static final String IDE_WORKBENCH = "org.eclipse.ui.ide"; //$NON-NLS-1$

	/**
	 * The ID of the default text editor. This must correspond to
	 * EditorsUI.DEFAULT_TEXT_EDITOR_ID.
	 */
	public static final String DEFAULT_TEXT_EDITOR_ID = "org.eclipse.ui.DefaultTextEditor"; //$NON-NLS-1$

	// IDE workbench extension point names
	public static final String PL_MARKER_IMAGE_PROVIDER = "markerImageProviders"; //$NON-NLS-1$
	public static final String PL_MARKER_HELP = "markerHelp"; //$NON-NLS-1$
	public static final String PL_MARKER_RESOLUTION = "markerResolution"; //$NON-NLS-1$
	public static final String PL_CAPABILITIES = "capabilities"; //$NON-NLS-1$
	public static final String PL_PROJECT_NATURE_IMAGES = "projectNatureImages"; //$NON-NLS-1$
	private final static String ICONS_PATH = "$nl$/icons/full/";//$NON-NLS-1$

	/**
	 * Project image registry; lazily initialized.
	 */
	private ProjectImageRegistry projectImageRegistry = null;

	/**
	 * Marker image registry; lazily initialized.
	 */
	private MarkerImageProviderRegistry markerImageProviderRegistry = null;

	private ResourceManager resourceManager;

	/**
	 * Create an instance of the receiver.
	 */
	public IDEWorkbenchPlugin() {
		super();
		inst = this;
	}

	/**
	 * Creates an extension. If the extension plugin has not been loaded a busy
	 * cursor will be activated during the duration of the load.
	 *
	 * @param element
	 *            the config element defining the extension
	 * @param classAttribute
	 *            the name of the attribute carrying the class
	 * @return Object the extension object
	 * @throws CoreException
	 *             Core Exception
	 */
	public static Object createExtension(final IConfigurationElement element, final String classAttribute) throws CoreException {
		// If plugin has been loaded create extension.
		// Otherwise, show busy cursor then create extension.
		@SuppressWarnings("deprecation")
		Bundle plugin = Platform.getBundle(element.getNamespace());
		if (plugin.getState() == Bundle.ACTIVE) {
			return element.createExecutableExtension(classAttribute);
		} else {
			final Object[] ret = new Object[1];
			final CoreException[] exc = new CoreException[1];
			BusyIndicator.showWhile(null, new Runnable() {
				@Override
				public void run() {
					try {
						ret[0] = element.createExecutableExtension(classAttribute);
					} catch (CoreException e) {
						exc[0] = e;
					}
				}
			});
			if (exc[0] != null) {
				throw exc[0];
			} else {
				return ret[0];
			}
		}
	}

	/*
	 * Return the default instance of the receiver. This represents the runtime
	 * plugin.
	 * @see AbstractPlugin for the typical implementation pattern for plugin
	 * classes.
	 */
	public static IDEWorkbenchPlugin getDefault() {
		return inst;
	}

	/**
	 * Return the workspace used by the workbench
	 * This method is internal to the workbench and must not be called by any
	 * plugins.
	 *
	 * @return Workspace
	 */
	public static IWorkspace getPluginWorkspace() {
		return RemoteResourcesPlugin.getWorkspace();
	}

	/**
	 * Logs the given message to the platform log.
	 * If you have an exception in hand, call log(String, Throwable) instead.
	 * If you have a status object in hand call log(String, IStatus) instead.
	 * This convenience method is for internal use by the IDE Workbench only and
	 * must not be called outside the IDE Workbench.
	 *
	 * @param message
	 *            A high level UI message describing when the problem happened.
	 */
	public static void log(String message) {
		getDefault().getLog().log(StatusUtil.newStatus(IStatus.ERROR, message, null));
	}

	/**
	 * Logs the given message and throwable to the platform log.
	 * If you have a status object in hand call log(String, IStatus) instead.
	 * This convenience method is for internal use by the IDE Workbench only and
	 * must not be called outside the IDE Workbench.
	 *
	 * @param message
	 *            A high level UI message describing when the problem happened.
	 * @param t
	 *            The throwable from where the problem actually occurred.
	 */
	public static void log(String message, Throwable t) {
		IStatus status = StatusUtil.newStatus(IStatus.ERROR, message, t);
		log(message, status);
	}

	/**
	 * Logs the given throwable to the platform log, indicating the class and
	 * method from where it is being logged (this is not necessarily where it
	 * occurred).
	 * This convenience method is for internal use by the IDE Workbench only and
	 * must not be called outside the IDE Workbench.
	 *
	 * @param clazz
	 *            The calling class.
	 * @param methodName
	 *            The calling method name.
	 * @param t
	 *            The throwable from where the problem actually occurred.
	 */
	public static void log(Class<?> clazz, String methodName, Throwable t) {
		String msg = MessageFormat.format(EXCEPTION_IN_0_1_2, new Object[] { clazz.getName(), methodName, t });
		log(msg, t);
	}

	/**
	 * Logs the given message and status to the platform log.
	 * This convenience method is for internal use by the IDE Workbench only and
	 * must not be called outside the IDE Workbench.
	 *
	 * @param message
	 *            A high level UI message describing when the problem happened.
	 *            May be <code>null</code>.
	 * @param status
	 *            The status describing the problem. Must not be null.
	 */
	public static void log(String message, IStatus status) {

		// 1FTUHE0: ITPCORE:ALL - API - Status & logging - loss of semantic info

		if (message != null) {
			getDefault().getLog().log(StatusUtil.newStatus(IStatus.ERROR, message, null));
		}

		getDefault().getLog().log(status);
	}

	/*
	 * (non-javadoc) Method declared on AbstractUIPlugin
	 */
	@Override
	protected void refreshPluginActions() {
		// do nothing
	}

	/**
	 * Return the manager that maps project nature ids to images.
	 *
	 * @return Project Image Registry
	 */
	public ProjectImageRegistry getProjectImageRegistry() {
		if (projectImageRegistry == null) {
			projectImageRegistry = new ProjectImageRegistry();
			projectImageRegistry.load();
		}
		return projectImageRegistry;
	}

	/**
	 * Returns the marker image provider registry for the workbench.
	 *
	 * @return the marker image provider registry
	 */
	public MarkerImageProviderRegistry getMarkerImageProviderRegistry() {
		if (markerImageProviderRegistry == null) {
			markerImageProviderRegistry = new MarkerImageProviderRegistry();
		}
		return markerImageProviderRegistry;
	}

	/**
	 * Returns the about information of all known features, omitting any
	 * features which are missing this information.
	 *
	 * @return a possibly empty list of about infos
	 */
	public AboutInfo[] getFeatureInfos() {
		// cannot be cached since bundle groups come and go
		List<AboutInfo> infos = new ArrayList<AboutInfo>();

		// add an entry for each bundle group
		IBundleGroupProvider[] providers = Platform.getBundleGroupProviders();
		if (providers != null) {
			for (IBundleGroupProvider provider : providers) {
				IBundleGroup[] bundleGroups = provider.getBundleGroups();
				for (IBundleGroup bundleGroup : bundleGroups) {
					infos.add(new AboutInfo(bundleGroup));
				}
			}
		}

		return infos.toArray(new AboutInfo[infos.size()]);
	}

	/**
	 * Returns the about information of the primary feature.
	 *
	 * @return info about the primary feature, or <code>null</code> if there is
	 *         no primary feature or if this information is unavailable
	 */
	public AboutInfo getPrimaryInfo() {
		IProduct product = Platform.getProduct();
		return product == null ? null : new AboutInfo(product);
	}

	/**
	 * Get the workbench image with the given path relative to ICON_PATH.
	 *
	 * @param relativePath
	 *            the relative path
	 * @return Image Descriptor
	 */
	public static ImageDescriptor getIDEImageDescriptor(String relativePath) {
		return imageDescriptorFromPlugin(IDE_WORKBENCH, ICONS_PATH + relativePath);
	}

	/**
	 * Return the resourceManager used by this plug-in.
	 *
	 * @return Resource Manager
	 */
	public ResourceManager getResourceManager() {
		if (resourceManager == null) {
			resourceManager = new LocalResourceManager(JFaceResources.getResources());
		}
		return resourceManager;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		if (resourceManager != null) {
			resourceManager.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=235179
		// Code that depends on Platform.getBundleGroupProviders() during
		// workbench startup must have org.eclipse.update.configurator
		// available.
		Bundle bundleGroupBundle = Platform.getBundle("org.eclipse.update.configurator"); //$NON-NLS-1$
		if (bundleGroupBundle != null) {
			try {
				bundleGroupBundle.start(Bundle.START_TRANSIENT);
			} catch (BundleException e) {
				// we can't start the org.eclipse.update.configurator
				// this is not something we want to force, so we'll call this a
				// NO-OP
			}
		}
	}
}
