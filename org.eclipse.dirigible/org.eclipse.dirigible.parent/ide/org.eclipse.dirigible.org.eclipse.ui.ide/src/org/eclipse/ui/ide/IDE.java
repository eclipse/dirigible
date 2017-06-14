/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.ide;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.mapping.IModelProviderDescriptor;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.resources.mapping.ModelProvider;
import org.eclipse.core.resources.mapping.ModelStatus;
import org.eclipse.core.resources.mapping.ResourceChangeValidator;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.resources.mapping.ResourceMappingContext;
import org.eclipse.core.resources.mapping.ResourceTraversal;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IMarkerHelpRegistry;
import org.eclipse.ui.ISaveableFilter;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.registry.MarkerHelpRegistry;
import org.eclipse.ui.internal.ide.registry.MarkerHelpRegistryReader;
import org.eclipse.ui.internal.misc.UIStats;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Collection of IDE-specific APIs factored out of existing workbench. This
 * class cannot be instantiated; all functionality is provided by static methods
 * and fields.
 *
 * @since 3.0
 */
@SuppressWarnings("restriction")
public final class IDE {
	private static final String OPEN_EDITOR_ON_MARKER_FAILED_MARKER_RESOURCE_NOT_AN_I_FILE = "Open editor on marker failed; marker resource not an IFile";

	/**
	 * The persistent property key used on IFile resources to contain the
	 * preferred editor ID to use.
	 * Example of retrieving the persisted editor id:
	 *
	 * <pre>
	 * <code>
	 *  IFile file = ...
	 *  IEditorDescriptor editorDesc = null;
	 *  try {
	 *  	String editorID = file.getPersistentProperty(EDITOR_KEY);
	 *  	if (editorID != null) {
	 *  		editorDesc = editorReg.findEditor(editorID);
	 *  	}
	 *  } catch (CoreException e) {
	 *  	// handle problem accessing persistent property here
	 *  }
	 * </code>
	 * </pre>
	 *
	 * Example of persisting the editor id:
	 *
	 * <pre>
	 * <code>
	 *  IFile file = ...
	 *  try {
	 *  	file.setPersistentProperty(EDITOR_KEY, editorDesc.getId());
	 *  } catch (CoreException e) {
	 *  	// handle problem setting persistent property here
	 *  }
	 * </code>
	 * </pre>
	 */
	public static final QualifiedName EDITOR_KEY = new QualifiedName("org.eclipse.ui.internal.registry.ResourceEditorRegistry", "EditorProperty");//$NON-NLS-2$//$NON-NLS-1$

	/**
	 * An optional attribute within a workspace marker (<code>IMarker</code>)
	 * which identifies the preferred editor type to be opened.
	 */
	public static final String EDITOR_ID_ATTR = "org.eclipse.ui.editorID"; //$NON-NLS-1$

	/**
	 * The resource based perspective identifier.
	 */
	public static final String RESOURCE_PERSPECTIVE_ID = "org.eclipse.ui.resourcePerspective"; //$NON-NLS-1$

	/**
	 * Marker help registry mapping markers to help context ids and resolutions;
	 * lazily initialized on fist access.
	 */
	private static MarkerHelpRegistry markerHelpRegistry = null;

	/**
	 * Standard shared images defined by the IDE. These are over and above the
	 * standard workbench images declared in
	 * {@link org.eclipse.ui.ISharedImages ISharedImages}.
	 * This interface is not intended to be implemented by clients.
	 *
	 * @see org.eclipse.ui.ISharedImages
	 */
	public interface SharedImages {
		/**
		 * Identifies a project image.
		 */
		public final static String IMG_OBJ_PROJECT = "IMG_OBJ_PROJECT"; //$NON-NLS-1$

		/**
		 * Identifies a closed project image.
		 */
		public final static String IMG_OBJ_PROJECT_CLOSED = "IMG_OBJ_PROJECT_CLOSED"; //$NON-NLS-1$

		/**
		 * Identifies the image used for "open marker".
		 */
		public final static String IMG_OPEN_MARKER = "IMG_OPEN_MARKER"; //$NON-NLS-1$

		/**
		 * Identifies the default image used to indicate a task.
		 */
		public final static String IMG_OBJS_TASK_TSK = "IMG_OBJS_TASK_TSK"; //$NON-NLS-1$

		/**
		 * Identifies the default image used to indicate a bookmark.
		 */
		public final static String IMG_OBJS_BKMRK_TSK = "IMG_OBJS_BKMRK_TSK"; //$NON-NLS-1$
	}

	/**
	 * Preferences defined by the IDE workbench.
	 * This interface is not intended to be implemented by clients.
	 */
	public interface Preferences {

		/**
		 * A named preference for how a new perspective should be opened when a
		 * new project is created.
		 * Value is of type <code>String</code>. The possible values are defined
		 * by the constants
		 * <code>OPEN_PERSPECTIVE_WINDOW, OPEN_PERSPECTIVE_PAGE,
		 * OPEN_PERSPECTIVE_REPLACE, and NO_NEW_PERSPECTIVE</code>.
		 *
		 * @see org.eclipse.ui.IWorkbenchPreferenceConstants#OPEN_PERSPECTIVE_WINDOW
		 * @see org.eclipse.ui.IWorkbenchPreferenceConstants#OPEN_PERSPECTIVE_PAGE
		 * @see org.eclipse.ui.IWorkbenchPreferenceConstants#OPEN_PERSPECTIVE_REPLACE
		 * @see org.eclipse.ui.IWorkbenchPreferenceConstants#NO_NEW_PERSPECTIVE
		 */
		public static final String PROJECT_OPEN_NEW_PERSPECTIVE = "PROJECT_OPEN_NEW_PERSPECTIVE"; //$NON-NLS-1$

		/**
		 * Specifies whether or not the workspace selection dialog should be
		 * shown on startup.
		 * The default value for this preference is <code>true</code>.
		 *
		 * @since 3.1
		 */
		public static final String SHOW_WORKSPACE_SELECTION_DIALOG = "SHOW_WORKSPACE_SELECTION_DIALOG"; //$NON-NLS-1$

		/**
		 * Stores the maximum number of workspaces that should be displayed in
		 * the ChooseWorkspaceDialog.
		 *
		 * @since 3.1
		 */
		public static final String MAX_RECENT_WORKSPACES = "MAX_RECENT_WORKSPACES"; //$NON-NLS-1$

		/**
		 * Stores a comma separated list of the recently used workspace paths.
		 *
		 * @since 3.1
		 */
		public static final String RECENT_WORKSPACES = "RECENT_WORKSPACES"; //$NON-NLS-1$

		/**
		 * Stores the version of the protocol used to decode/encode the list of
		 * recent workspaces.
		 *
		 * @since 3.1
		 */
		public static final String RECENT_WORKSPACES_PROTOCOL = "RECENT_WORKSPACES_PROTOCOL"; //$NON-NLS-1$

	}

	/**
	 * A saveable filter that selects savables that contain resources that are
	 * descendants of the roots of the filter.
	 *
	 * @since 3.3
	 */
	private static class SaveFilter implements ISaveableFilter {
		private static final String AN_INTERNAL_ERROR_OCCURRED_WHILE_DETERMINING_THE_RESOURCES_FOR_0 = "An internal error occurred while determining the resources for {0}";
		private final IResource[] roots;

		/**
		 * Create the filter
		 *
		 * @param roots
		 *            the save roots
		 */
		public SaveFilter(IResource[] roots) {
			this.roots = roots;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.ISaveableFilter#select(org.eclipse.ui.Saveable,
		 * org.eclipse.ui.IWorkbenchPart[])
		 */
		@Override
		public boolean select(Saveable saveable, IWorkbenchPart[] containingParts) {
			if (isDescendantOfRoots(saveable)) {
				return true;
			}
			// For backwards compatibility, we need to check the parts
			for (IWorkbenchPart workbenchPart : containingParts) {
				if (workbenchPart instanceof IEditorPart) {
					IEditorPart editorPart = (IEditorPart) workbenchPart;
					if (isEditingDescendantOf(editorPart)) {
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * Return whether the given saveable contains any resources that are
		 * descendants of the root resources.
		 *
		 * @param saveable
		 *            the saveable
		 * @return whether the given saveable contains any resources that are
		 *         descendants of the root resources
		 */
		private boolean isDescendantOfRoots(Saveable saveable) {
			// First, try and adapt the saveable to a resource mapping.
			ResourceMapping mapping = ResourceUtil.getResourceMapping(saveable);
			if (mapping != null) {
				try {
					ResourceTraversal[] traversals = mapping.getTraversals(ResourceMappingContext.LOCAL_CONTEXT, null);
					for (ResourceTraversal traversal : traversals) {
						IResource[] resources = traversal.getResources();
						for (IResource resource : resources) {
							if (isDescendantOfRoots(resource)) {
								return true;
							}
						}
					}
				} catch (CoreException e) {
					IDEWorkbenchPlugin.log(NLS.bind(AN_INTERNAL_ERROR_OCCURRED_WHILE_DETERMINING_THE_RESOURCES_FOR_0, saveable.getName()), e);
				}
			} else {
				// If there is no mapping, try to adapt to a resource or file
				// directly
				IFile file = ResourceUtil.getFile(saveable);
				if (file != null) {
					return isDescendantOfRoots(file);
				}
			}
			return false;
		}

		/**
		 * Return whether the given resource is either equal to or a descendant
		 * of one of the given roots.
		 *
		 * @param resource
		 *            the resource to be tested
		 * @return whether the given resource is either equal to or a descendant
		 *         of one of the given roots
		 */
		private boolean isDescendantOfRoots(IResource resource) {
			for (IResource root : roots) {
				if (root.getFullPath().isPrefixOf(resource.getFullPath())) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Return whether the given dirty editor part is editing resources that
		 * are descendants of the given roots.
		 *
		 * @param part
		 *            the dirty editor part
		 * @return whether the given dirty editor part is editing resources that
		 *         are descendants of the given roots
		 */
		private boolean isEditingDescendantOf(IEditorPart part) {
			IFile file = ResourceUtil.getFile(part.getEditorInput());
			if (file != null) {
				return isDescendantOfRoots(file);
			}
			return false;
		}

	}

	/**
	 * Block instantiation.
	 */
	private IDE() {
		// do nothing
	}

	/**
	 * Returns the marker help registry for the workbench.
	 *
	 * @return the marker help registry
	 */
	public static IMarkerHelpRegistry getMarkerHelpRegistry() {
		synchronized (IDE.class) {
			if (markerHelpRegistry == null) {
				markerHelpRegistry = new MarkerHelpRegistry();
				new MarkerHelpRegistryReader().addHelp(markerHelpRegistry);
			}
			return markerHelpRegistry;
		}
	}

	/**
	 * Sets the cursor and selection state for the given editor to reveal the
	 * position of the given marker. This is done on a best effort basis. If the
	 * editor does not provide an <code>IGotoMarker</code> interface (either
	 * directly or via <code>IAdaptable.getAdapter</code>), this has no effect.
	 *
	 * @param editor
	 *            the editor
	 * @param marker
	 *            the marker
	 */
	public static void gotoMarker(IEditorPart editor, IMarker marker) {
		IGotoMarker gotoMarker = null;
		if (editor instanceof IGotoMarker) {
			gotoMarker = (IGotoMarker) editor;
		} else {
			gotoMarker = editor.getAdapter(IGotoMarker.class);
		}
		if (gotoMarker != null) {
			gotoMarker.gotoMarker(marker);
		}
	}

	/**
	 * Opens an editor on the given object.
	 * If the page already has an editor open on the target object then that
	 * editor is brought to front; otherwise, a new editor is opened.
	 *
	 * @param page
	 *            the page in which the editor will be opened
	 * @param input
	 *            the editor input
	 * @param editorId
	 *            the id of the editor extension to use
	 * @return an open editor or <code>null</code> if an external editor was
	 *         opened
	 * @exception PartInitException
	 *                if the editor could not be initialized
	 * @see org.eclipse.ui.IWorkbenchPage#openEditor(IEditorInput, String)
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, IEditorInput input, String editorId) throws PartInitException {
		// sanity checks
		if (page == null) {
			throw new IllegalArgumentException();
		}

		// open the editor on the file
		return page.openEditor(input, editorId);
	}

	/**
	 * Opens an editor on the given IFileStore object.
	 * Unlike the other <code>openEditor</code> methods, this one can be used to
	 * open files that reside outside the workspace resource set.
	 * If the page already has an editor open on the target object then that
	 * editor is brought to front; otherwise, a new editor is opened.
	 *
	 * @param page
	 *            the page in which the editor will be opened
	 * @param uri
	 *            the URI of the file store representing the file to open
	 * @param editorId
	 *            the id of the editor extension to use
	 * @param activate
	 *            if <code>true</code> the editor will be activated opened
	 * @return an open editor or <code>null</code> if an external editor was
	 * @exception PartInitException
	 *                if the editor could not be initialized
	 * @see org.eclipse.ui.IWorkbenchPage#openEditor(IEditorInput, String)
	 * @see EFS#getStore(URI)
	 * @since 3.3
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, URI uri, String editorId, boolean activate) throws PartInitException {
		// sanity checks
		if (page == null) {
			throw new IllegalArgumentException();
		}

		IFileStore fileStore;
		try {
			fileStore = EFS.getStore(uri);
		} catch (CoreException e) {
			throw new PartInitException(IDEWorkbenchMessages.IDE_coreExceptionFileStore, e);
		}

		IEditorInput input = getEditorInput(fileStore);

		// open the editor on the file
		return page.openEditor(input, editorId, activate);
	}

	/**
	 * Create the Editor Input appropriate for the given <code>IFileStore</code>
	 * . The result is a normal file editor input if the file exists in the
	 * workspace and, if not, we create a wrapper capable of managing an
	 * 'external' file using its <code>IFileStore</code>.
	 *
	 * @param fileStore
	 *            The file store to provide the editor input for
	 * @return The editor input associated with the given file store
	 * @since 3.3
	 */
	private static IEditorInput getEditorInput(IFileStore fileStore) {
		IFile workspaceFile = getWorkspaceFile(fileStore);
		if (workspaceFile != null) {
			return new FileEditorInput(workspaceFile);
		}
		return new FileStoreEditorInput(fileStore);
	}

	/**
	 * Determine whether or not the <code>IFileStore</code> represents a file
	 * currently in the workspace.
	 *
	 * @param fileStore
	 *            The <code>IFileStore</code> to test
	 * @return The workspace's <code>IFile</code> if it exists or
	 *         <code>null</code> if not
	 */
	private static IFile getWorkspaceFile(IFileStore fileStore) {
		IWorkspaceRoot root = RemoteResourcesPlugin.getWorkspace().getRoot();
		IFile[] files = root.findFilesForLocationURI(fileStore.toURI());
		files = filterNonExistentFiles(files);
		if ((files == null) || (files.length == 0)) {
			return null;
		}

		// for now only return the first file
		return files[0];
	}

	/**
	 * Filter the incoming array of <code>IFile</code> elements by removing any
	 * that do not currently exist in the workspace.
	 *
	 * @param files
	 *            The array of <code>IFile</code> elements
	 * @return The filtered array
	 */
	private static IFile[] filterNonExistentFiles(IFile[] files) {
		if (files == null) {
			return null;
		}

		int length = files.length;
		ArrayList<IFile> existentFiles = new ArrayList<IFile>(length);
		for (int i = 0; i < length; i++) {
			if (files[i].exists()) {
				existentFiles.add(files[i]);
			}
		}
		return existentFiles.toArray(new IFile[existentFiles.size()]);
	}

	/**
	 * Opens an editor on the given object.
	 * If the page already has an editor open on the target object then that
	 * editor is brought to front; otherwise, a new editor is opened. If
	 * <code>activate == true</code> the editor will be activated.
	 *
	 * @param page
	 *            the page in which the editor will be opened
	 * @param input
	 *            the editor input
	 * @param editorId
	 *            the id of the editor extension to use
	 * @param activate
	 *            if <code>true</code> the editor will be activated
	 * @return an open editor or <code>null</code> if an external editor was
	 *         opened
	 * @exception PartInitException
	 *                if the editor could not be initialized
	 * @see org.eclipse.ui.IWorkbenchPage#openEditor(IEditorInput, String,
	 *      boolean)
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, IEditorInput input, String editorId, boolean activate) throws PartInitException {
		// sanity checks
		if (page == null) {
			throw new IllegalArgumentException();
		}

		// open the editor on the file
		return page.openEditor(input, editorId, activate);
	}

	/**
	 * Opens an editor on the given file resource. This method will attempt to
	 * resolve the editor based on content-type bindings as well as traditional
	 * name/extension bindings.
	 * If the page already has an editor open on the target object then that
	 * editor is brought to front; otherwise, a new editor is opened. If
	 * <code>activate == true</code> the editor will be activated.
	 *
	 * @param page
	 *            the page in which the editor will be opened
	 * @param input
	 *            the editor input
	 * @param activate
	 *            if <code>true</code> the editor will be activated
	 * @return an open editor or <code>null</code> if an external editor was
	 *         opened
	 * @exception PartInitException
	 *                if the editor could not be initialized
	 * @see org.eclipse.ui.IWorkbenchPage#openEditor(org.eclipse.ui.IEditorInput,
	 *      String, boolean)
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, IFile input, boolean activate) throws PartInitException {
		return openEditor(page, input, activate, true);
	}

	/**
	 * Opens an editor on the given file resource. This method will attempt to
	 * resolve the editor based on content-type bindings as well as traditional
	 * name/extension bindings if <code>determineContentType</code> is
	 * <code>true</code>.
	 * If the page already has an editor open on the target object then that
	 * editor is brought to front; otherwise, a new editor is opened. If
	 * <code>activate == true</code> the editor will be activated.
	 *
	 * @param page
	 *            the page in which the editor will be opened
	 * @param input
	 *            the editor input
	 * @param activate
	 *            if <code>true</code> the editor will be activated
	 * @param determineContentType
	 *            attempt to resolve the content type for this file
	 * @return an open editor or <code>null</code> if an external editor was
	 *         opened
	 * @exception PartInitException
	 *                if the editor could not be initialized
	 * @see org.eclipse.ui.IWorkbenchPage#openEditor(org.eclipse.ui.IEditorInput,
	 *      String, boolean)
	 * @since 3.1
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, IFile input, boolean activate, boolean determineContentType) throws PartInitException {
		// sanity checks
		if (page == null) {
			throw new IllegalArgumentException();
		}

		// open the editor on the file
		IEditorDescriptor editorDesc = getEditorDescriptor(input, determineContentType);
		return page.openEditor(new FileEditorInput(input), editorDesc.getId(), activate);
	}

	/**
	 * Opens an editor on the given file resource. This method will attempt to
	 * resolve the editor based on content-type bindings as well as traditional
	 * name/extension bindings.
	 * If the page already has an editor open on the target object then that
	 * editor is brought to front; otherwise, a new editor is opened.
	 *
	 * @param page
	 *            the page in which the editor will be opened
	 * @param input
	 *            the editor input
	 * @return an open editor or <code>null</code> if an external editor was
	 *         opened
	 * @exception PartInitException
	 *                if the editor could not be initialized
	 * @see org.eclipse.ui.IWorkbenchPage#openEditor(IEditorInput, String)
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, IFile input) throws PartInitException {
		// sanity checks
		if (page == null) {
			throw new IllegalArgumentException();
		}

		// open the editor on the file
		IEditorDescriptor editorDesc = getEditorDescriptor(input);
		return page.openEditor(new FileEditorInput(input), editorDesc.getId());
	}

	/**
	 * Opens an editor on the given file resource.
	 * If the page already has an editor open on the target object then that
	 * editor is brought to front; otherwise, a new editor is opened.
	 *
	 * @param page
	 *            the page in which the editor will be opened
	 * @param input
	 *            the editor input
	 * @param editorId
	 *            the id of the editor extension to use
	 * @return an open editor or <code>null</code> if an external editor was
	 *         opened
	 * @exception PartInitException
	 *                if the editor could not be initialized
	 * @see org.eclipse.ui.IWorkbenchPage#openEditor(IEditorInput, String)
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, IFile input, String editorId) throws PartInitException {
		// sanity checks
		if (page == null) {
			throw new IllegalArgumentException();
		}

		// open the editor on the file
		return page.openEditor(new FileEditorInput(input), editorId);
	}

	/**
	 * Opens an editor on the given file resource.
	 * If the page already has an editor open on the target object then that
	 * editor is brought to front; otherwise, a new editor is opened. If
	 * <code>activate == true</code> the editor will be activated.
	 *
	 * @param page
	 *            the page in which the editor will be opened
	 * @param input
	 *            the editor input
	 * @param editorId
	 *            the id of the editor extension to use
	 * @param activate
	 *            if <code>true</code> the editor will be activated
	 * @return an open editor or <code>null</code> if an external editor was
	 *         opened
	 * @exception PartInitException
	 *                if the editor could not be initialized
	 * @see org.eclipse.ui.IWorkbenchPage#openEditor(IEditorInput, String,
	 *      boolean)
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, IFile input, String editorId, boolean activate) throws PartInitException {
		// sanity checks
		if (page == null) {
			throw new IllegalArgumentException();
		}

		// open the editor on the file
		return page.openEditor(new FileEditorInput(input), editorId, activate);
	}

	/**
	 * Returns an editor descriptor appropriate for opening the given file
	 * resource.
	 * The editor descriptor is determined using a multi-step process. This
	 * method will attempt to resolve the editor based on content-type bindings
	 * as well as traditional name/extension bindings.
	 * <ol>
	 * <li>The file is consulted for a persistent property named
	 * <code>IDE.EDITOR_KEY</code> containing the preferred editor id to be
	 * used.</li>
	 * <li>The workbench editor registry is consulted to determine if an editor
	 * extension has been registered for the file type. If so, an instance of
	 * the editor extension is opened on the file. See
	 * <code>IEditorRegistry.getDefaultEditor(String)</code>.</li>
	 * <li>The operating system is consulted to determine if an in-place
	 * component editor is available (e.g. OLE editor on Win32 platforms).</li>
	 * <li>The operating system is consulted to determine if an external editor
	 * is available.</li>
	 * </ol>
	 *
	 * @param file
	 *            the file
	 * @return an editor descriptor, appropriate for opening the file
	 * @throws PartInitException
	 *             if no editor can be found
	 */
	public static IEditorDescriptor getEditorDescriptor(IFile file) throws PartInitException {
		return getEditorDescriptor(file, true);
	}

	/**
	 * Returns an editor descriptor appropriate for opening the given file
	 * resource.
	 * The editor descriptor is determined using a multi-step process. This
	 * method will attempt to resolve the editor based on content-type bindings
	 * as well as traditional name/extension bindings if
	 * <code>determineContentType</code>is <code>true</code>.
	 * <ol>
	 * <li>The file is consulted for a persistent property named
	 * <code>IDE.EDITOR_KEY</code> containing the preferred editor id to be
	 * used.</li>
	 * <li>The workbench editor registry is consulted to determine if an editor
	 * extension has been registered for the file type. If so, an instance of
	 * the editor extension is opened on the file. See
	 * <code>IEditorRegistry.getDefaultEditor(String)</code>.</li>
	 * <li>The operating system is consulted to determine if an in-place
	 * component editor is available (e.g. OLE editor on Win32 platforms).</li>
	 * <li>The operating system is consulted to determine if an external editor
	 * is available.</li>
	 * </ol>
	 *
	 * @param file
	 *            the file
	 * @param determineContentType
	 *            query the content type system for the content type of the file
	 * @return an editor descriptor, appropriate for opening the file
	 * @throws PartInitException
	 *             if no editor can be found
	 * @since 3.1
	 */
	public static IEditorDescriptor getEditorDescriptor(IFile file, boolean determineContentType) throws PartInitException {

		if (file == null) {
			throw new IllegalArgumentException();
		}

		return getEditorDescriptor(file.getName(), PlatformUI.getWorkbench().getEditorRegistry(), getDefaultEditor(file, determineContentType));
	}

	/**
	 * Returns an editor id appropriate for opening the given file store.
	 * The editor descriptor is determined using a multi-step process. This
	 * method will attempt to resolve the editor based on content-type bindings
	 * as well as traditional name/extension bindings.
	 * <ol>
	 * <li>The workbench editor registry is consulted to determine if an editor
	 * extension has been registered for the file type. If so, an instance of
	 * the editor extension is opened on the file. See
	 * <code>IEditorRegistry.getDefaultEditor(String)</code>.</li>
	 * <li>The operating system is consulted to determine if an in-place
	 * component editor is available (e.g. OLE editor on Win32 platforms).</li>
	 * <li>The operating system is consulted to determine if an external editor
	 * is available.</li>
	 * </ol>
	 *
	 * @param fileStore
	 *            the file store
	 * @return the id of an editor, appropriate for opening the file
	 * @throws PartInitException
	 *             if no editor can be found
	 */
	private static String getEditorId(IFileStore fileStore) throws PartInitException {
		String name = fileStore.fetchInfo().getName();
		if (name == null) {
			throw new IllegalArgumentException();
		}

		IContentType contentType = null;
		try {
			InputStream is = null;
			try {
				is = fileStore.openInputStream(EFS.NONE, null);
				contentType = Platform.getContentTypeManager().findContentTypeFor(is, name);
			} finally {
				if (is != null) {
					is.close();
				}
			}
		} catch (CoreException ex) {
			// continue without content type
		} catch (IOException ex) {
			// continue without content type
		}

		IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry();

		return getEditorDescriptor(name, editorReg, editorReg.getDefaultEditor(name, contentType)).getId();
	}

	/**
	 * Returns an editor descriptor appropriate for opening a file resource with
	 * the given name.
	 * The editor descriptor is determined using a multi-step process. This
	 * method will attempt to infer content type from the file name.
	 * <ol>
	 * <li>The file is consulted for a persistent property named
	 * <code>IDE.EDITOR_KEY</code> containing the preferred editor id to be
	 * used.</li>
	 * <li>The workbench editor registry is consulted to determine if an editor
	 * extension has been registered for the file type. If so, an instance of
	 * the editor extension is opened on the file. See
	 * <code>IEditorRegistry.getDefaultEditor(String)</code>.</li>
	 * <li>The operating system is consulted to determine if an in-place
	 * component editor is available (e.g. OLE editor on Win32 platforms).</li>
	 * <li>The operating system is consulted to determine if an external editor
	 * is available.</li>
	 * </ol>
	 *
	 * @param name
	 *            the file name
	 * @return an editor descriptor, appropriate for opening the file
	 * @throws PartInitException
	 *             if no editor can be found
	 * @since 3.1
	 */
	public static IEditorDescriptor getEditorDescriptor(String name) throws PartInitException {
		return getEditorDescriptor(name, true);
	}

	/**
	 * Returns an editor descriptor appropriate for opening a file resource with
	 * the given name.
	 * The editor descriptor is determined using a multi-step process. This
	 * method will attempt to infer the content type of the file if
	 * <code>inferContentType</code> is <code>true</code>.
	 * <ol>
	 * <li>The file is consulted for a persistent property named
	 * <code>IDE.EDITOR_KEY</code> containing the preferred editor id to be
	 * used.</li>
	 * <li>The workbench editor registry is consulted to determine if an editor
	 * extension has been registered for the file type. If so, an instance of
	 * the editor extension is opened on the file. See
	 * <code>IEditorRegistry.getDefaultEditor(String)</code>.</li>
	 * <li>The operating system is consulted to determine if an in-place
	 * component editor is available (e.g. OLE editor on Win32 platforms).</li>
	 * <li>The operating system is consulted to determine if an external editor
	 * is available.</li>
	 * </ol>
	 *
	 * @param name
	 *            the file name
	 * @param inferContentType
	 *            attempt to infer the content type from the file name if this
	 *            is <code>true</code>
	 * @return an editor descriptor, appropriate for opening the file
	 * @throws PartInitException
	 *             if no editor can be found
	 * @since 3.1
	 */
	public static IEditorDescriptor getEditorDescriptor(String name, boolean inferContentType) throws PartInitException {

		if (name == null) {
			throw new IllegalArgumentException();
		}

		IContentType contentType = inferContentType ? Platform.getContentTypeManager().findContentTypeFor(name) : null;
		IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry();

		return getEditorDescriptor(name, editorReg, editorReg.getDefaultEditor(name, contentType));
	}

	/**
	 * Get the editor descriptor for a given name using the editorDescriptor
	 * passed in as a default as a starting point.
	 *
	 * @param name
	 *            The name of the element to open.
	 * @param editorReg
	 *            The editor registry to do the lookups from.
	 * @param defaultDescriptor
	 *            IEditorDescriptor or <code>null</code>
	 * @return IEditorDescriptor
	 * @throws PartInitException
	 *             if no valid editor can be found
	 * @since 3.1
	 */
	private static IEditorDescriptor getEditorDescriptor(String name, IEditorRegistry editorReg, IEditorDescriptor defaultDescriptor)
			throws PartInitException {

		if (defaultDescriptor != null) {
			return defaultDescriptor;
		}

		IEditorDescriptor editorDesc = defaultDescriptor;

		// next check the OS for in-place editor (OLE on Win32)
		if (editorReg.isSystemInPlaceEditorAvailable(name)) {
			editorDesc = editorReg.findEditor(IEditorRegistry.SYSTEM_INPLACE_EDITOR_ID);
		}

		// next check with the OS for an external editor
		// if (editorDesc == null
		// && editorReg.isSystemExternalEditorAvailable(name)) {
		// editorDesc = editorReg
		// .findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
		// }

		// next lookup the default text editor
		if (editorDesc == null) {
			editorDesc = editorReg.findEditor(IDEWorkbenchPlugin.DEFAULT_TEXT_EDITOR_ID);
		}

		// if no valid editor found, bail out
		if (editorDesc == null) {
			throw new PartInitException(IDEWorkbenchMessages.IDE_noFileEditorFound);
		}

		return editorDesc;
	}

	/**
	 * Opens an editor on the file resource of the given marker.
	 * If this page already has an editor open on the marker resource file that
	 * editor is brought to front; otherwise, a new editor is opened.The cursor
	 * and selection state of the editor are then updated from information
	 * recorded in the marker.
	 * If the marker contains an <code>EDITOR_ID_ATTR</code> attribute the
	 * attribute value will be used to determine the editor type to be opened.
	 * If not, the registered editor for the marker resource file will be used.
	 *
	 * @param page
	 *            the workbench page to open the editor in
	 * @param marker
	 *            the marker to open
	 * @return an open editor or <code>null</code> not possible
	 * @exception PartInitException
	 *                if the editor could not be initialized
	 * @see #openEditor(org.eclipse.ui.IWorkbenchPage,
	 *      org.eclipse.core.resources.IMarker, boolean)
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, IMarker marker) throws PartInitException {
		return openEditor(page, marker, true);
	}

	/**
	 * Opens an editor on the file resource of the given marker.
	 * If this page already has an editor open on the marker resource file that
	 * editor is brought to front; otherwise, a new editor is opened. If
	 * <code>activate == true</code> the editor will be activated. The cursor
	 * and selection state of the editor are then updated from information
	 * recorded in the marker.
	 * If the marker contains an <code>EDITOR_ID_ATTR</code> attribute the
	 * attribute value will be used to determine the editor type to be opened.
	 * If not, the registered editor for the marker resource file will be used.
	 *
	 * @param page
	 *            the workbench page to open the editor in
	 * @param marker
	 *            the marker to open
	 * @param activate
	 *            if <code>true</code> the editor will be activated
	 * @return an open editor or <code>null</code> not possible
	 * @exception PartInitException
	 *                if the editor could not be initialized
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, IMarker marker, boolean activate) throws PartInitException {
		// sanity checks
		if ((page == null) || (marker == null)) {
			throw new IllegalArgumentException();
		}

		// get the marker resource file
		if (!(marker.getResource() instanceof IFile)) {
			IDEWorkbenchPlugin.log(OPEN_EDITOR_ON_MARKER_FAILED_MARKER_RESOURCE_NOT_AN_I_FILE);
			return null;
		}
		IFile file = (IFile) marker.getResource();

		// get the preferred editor id from the marker
		IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry();
		IEditorDescriptor editorDesc = null;
		try {
			String editorID = (String) marker.getAttribute(EDITOR_ID_ATTR);
			if (editorID != null) {
				editorDesc = editorReg.findEditor(editorID);
			}
		} catch (CoreException e) {
			// ignore this
		}

		// open the editor on the marker resource file
		IEditorPart editor = null;
		if (editorDesc == null) {
			editor = openEditor(page, file, activate);
		} else {
			editor = page.openEditor(new FileEditorInput(file), editorDesc.getId(), activate);
		}

		// get the editor to update its position based on the marker
		if (editor != null) {
			gotoMarker(editor, marker);
		}

		return editor;
	}

	/**
	 * Opens an editor on the given IFileStore object.
	 * Unlike the other <code>openEditor</code> methods, this one can be used to
	 * open files that reside outside the workspace resource set.
	 * If the page already has an editor open on the target object then that
	 * editor is brought to front; otherwise, a new editor is opened.
	 *
	 * @param page
	 *            the page in which the editor will be opened
	 * @param fileStore
	 *            the IFileStore representing the file to open
	 * @return an open editor or <code>null</code> if an external editor was
	 *         opened
	 * @exception PartInitException
	 *                if the editor could not be initialized
	 * @see org.eclipse.ui.IWorkbenchPage#openEditor(IEditorInput, String)
	 * @since 3.3
	 */
	public static IEditorPart openEditorOnFileStore(IWorkbenchPage page, IFileStore fileStore) throws PartInitException {
		// sanity checks
		if (page == null) {
			throw new IllegalArgumentException();
		}

		IEditorInput input = getEditorInput(fileStore);
		String editorId = getEditorId(fileStore);

		// open the editor on the file
		return page.openEditor(input, editorId);
	}

	/**
	 * Save all dirty editors in the workbench whose editor input is a child
	 * resource of one of the <code>IResource</code>'s provided. Opens a dialog
	 * to prompt the user if <code>confirm</code> is true. Return true if
	 * successful. Return false if the user has canceled the command.
	 *
	 * @since 3.0
	 * @param resourceRoots
	 *            the resource roots under which editor input should be saved,
	 *            other will be left dirty
	 * @param confirm
	 *            <code>true</code> to ask the user before saving unsaved
	 *            changes (recommended), and <code>false</code> to save unsaved
	 *            changes without asking
	 * @return <code>true</code> if the command succeeded, and
	 *         <code>false</code> if the operation was canceled by the user or
	 *         an error occurred while saving
	 */
	public static boolean saveAllEditors(final IResource[] resourceRoots, final boolean confirm) {

		if (resourceRoots.length == 0) {
			return true;
		}

		final boolean[] result = new boolean[] { true };
		SafeRunner.run(new SafeRunnable(IDEWorkbenchMessages.ErrorOnSaveAll) {
			/**
			 *
			 */
			private static final long serialVersionUID = 784352547668323665L;

			@Override
			public void run() {
				IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (w == null) {
					IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
					if (windows.length > 0) {
						w = windows[0];
					}
				}
				if (w != null) {
					result[0] = PlatformUI.getWorkbench().saveAll(w, w, new SaveFilter(resourceRoots), confirm);
				}
			}
		});
		return result[0];
	}

	/**
	 * Sets the default editor id for a given file. This value will be used to
	 * determine the default editor descriptor for the file in future calls to
	 * <code>getDefaultEditor(IFile)</code>.
	 *
	 * @param file
	 *            the file
	 * @param editorID
	 *            the editor id
	 */
	public static void setDefaultEditor(IFile file, String editorID) {
		try {
			file.setPersistentProperty(EDITOR_KEY, editorID);
		} catch (CoreException e) {
			// do nothing
		}
	}

	/**
	 * Returns the default editor for a given file. This method will attempt to
	 * resolve the editor based on content-type bindings as well as traditional
	 * name/extension bindings.
	 * A default editor id may be registered for a specific file using
	 * <code>setDefaultEditor</code>. If the given file has a registered default
	 * editor id the default editor will derived from it. If not, the default
	 * editor is determined by taking the file name for the file and obtaining
	 * the default editor for that name.
	 *
	 * @param file
	 *            the file
	 * @return the descriptor of the default editor, or <code>null</code> if not
	 *         found
	 */
	public static IEditorDescriptor getDefaultEditor(IFile file) {
		return getDefaultEditor(file, true);
	}

	/**
	 * Returns the default editor for a given file. This method will attempt to
	 * resolve the editor based on content-type bindings as well as traditional
	 * name/extension bindings if <code>determineContentType</code> is
	 * <code>true</code>.
	 * A default editor id may be registered for a specific file using
	 * <code>setDefaultEditor</code>. If the given file has a registered default
	 * editor id the default editor will derived from it. If not, the default
	 * editor is determined by taking the file name for the file and obtaining
	 * the default editor for that name.
	 *
	 * @param file
	 *            the file
	 * @param determineContentType
	 *            determine the content type for the given file
	 * @return the descriptor of the default editor, or <code>null</code> if not
	 *         found
	 * @since 3.1
	 */
	public static IEditorDescriptor getDefaultEditor(IFile file, boolean determineContentType) {
		// Try file specific editor.
		IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry();
		try {
			String editorID = file.getPersistentProperty(EDITOR_KEY);
			if (editorID != null) {
				IEditorDescriptor desc = editorReg.findEditor(editorID);
				if (desc != null) {
					return desc;
				}
			}
		} catch (CoreException e) {
			// do nothing
		}

		IContentType contentType = null;
		if (determineContentType) {
			contentType = getContentType(file);
		}
		// Try lookup with filename
		return editorReg.getDefaultEditor(file.getName(), contentType);
	}

	/**
	 * Extracts and returns the <code>IResource</code>s in the given selection
	 * or the resource objects they adapts to.
	 *
	 * @param originalSelection
	 *            the original selection, possibly empty
	 * @return list of resources (element type: <code>IResource</code>),
	 *         possibly empty
	 */
	public static List<Object> computeSelectedResources(IStructuredSelection originalSelection) {
		List<Object> resources = null;
		for (Iterator<?> e = originalSelection.iterator(); e.hasNext();) {
			Object next = e.next();
			Object resource = null;
			if (next instanceof IResource) {
				resource = next;
			} else if (next instanceof IAdaptable) {
				resource = ((IAdaptable) next).getAdapter(IResource.class);
			}
			if (resource != null) {
				if (resources == null) {
					// lazy init to avoid creating empty lists
					// assume selection contains mostly resources most times
					resources = new ArrayList<Object>(originalSelection.size());
				}
				resources.add(resource);
			}
		}
		if (resources == null) {
			return Collections.emptyList();
		}
		return resources;

	}

	/**
	 * Return the content type for the given file.
	 *
	 * @param file
	 *            the file to test
	 * @return the content type, or <code>null</code> if it cannot be
	 *         determined.
	 * @since 3.1
	 */
	public static IContentType getContentType(IFile file) {
		try {
			UIStats.start(UIStats.CONTENT_TYPE_LOOKUP, file.getName());
			IContentDescription contentDescription = file.getContentDescription();
			if (contentDescription == null) {
				return null;
			}
			return contentDescription.getContentType();
		} catch (CoreException e) {
			if (e.getStatus().getCode() == IResourceStatus.OUT_OF_SYNC_LOCAL) {
				// Determine the content type from the file name.
				return Platform.getContentTypeManager().findContentTypeFor(file.getName());
			}
			return null;
		} finally {
			UIStats.end(UIStats.CONTENT_TYPE_LOOKUP, file, file.getName());
		}
	}

	/**
	 * Guess at the content type of the given file based on the filename.
	 *
	 * @param file
	 *            the file to test
	 * @return the content type, or <code>null</code> if it cannot be
	 *         determined.
	 * @since 3.2
	 */
	public static IContentType guessContentType(IFile file) {
		String fileName = file.getName();
		try {
			UIStats.start(UIStats.CONTENT_TYPE_LOOKUP, fileName);
			IContentTypeMatcher matcher = file.getProject().getContentTypeMatcher();
			return matcher.findContentTypeFor(fileName);
		} catch (CoreException e) {
			return null;
		} finally {
			UIStats.end(UIStats.CONTENT_TYPE_LOOKUP, file, fileName);
		}
	}

	/**
	 * Prompt the user to inform them of the possible side effects of an
	 * operation on resources. Do not prompt for side effects from ignored model
	 * providers. A model provider can be ignored if it is the client calling
	 * this API. Any message from the provided model provider id or any model
	 * providers it extends will be ignored.
	 *
	 * @param shell
	 *            the shell to parent the prompt dialog
	 * @param title
	 *            the title of the dialog
	 * @param message
	 *            the message for the dialog
	 * @param delta
	 *            a delta built using an
	 *            {@link IResourceChangeDescriptionFactory}
	 * @param ignoreModelProviderIds
	 *            model providers to be ignored
	 * @param syncExec
	 *            prompt in a sync exec (required when called from a non-UI
	 *            thread)
	 * @return whether the user chose to continue
	 * @since 3.2
	 */
	public static boolean promptToConfirm(final Shell shell, final String title, String message, IResourceDelta delta,
			String[] ignoreModelProviderIds, boolean syncExec) {
		IStatus status = ResourceChangeValidator.getValidator().validateChange(delta, null);
		if (status.isOK()) {
			return true;
		}
		final IStatus displayStatus;
		if (status.isMultiStatus()) {
			List<IStatus> result = new ArrayList<IStatus>();
			IStatus[] children = status.getChildren();
			for (IStatus element : children) {
				IStatus child = element;
				if (!isIgnoredStatus(child, ignoreModelProviderIds)) {
					result.add(child);
				}
			}
			if (result.isEmpty()) {
				return true;
			}
			if (result.size() == 1) {
				displayStatus = result.get(0);
			} else {
				displayStatus = new MultiStatus(status.getPlugin(), status.getCode(), result.toArray(new IStatus[result.size()]), status.getMessage(),
						status.getException());
			}
		} else {
			if (isIgnoredStatus(status, ignoreModelProviderIds)) {
				return true;
			}
			displayStatus = status;
		}

		if (message == null) {
			message = IDEWorkbenchMessages.IDE_sideEffectWarning;
		}
		final String dialogMessage = NLS.bind(IDEWorkbenchMessages.IDE_areYouSure, message);

		final boolean[] result = new boolean[] { false };
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				ErrorDialog dialog = new ErrorDialog(shell, title, dialogMessage, displayStatus, IStatus.ERROR | IStatus.WARNING | IStatus.INFO) {
					/**
									 *
									 */
					private static final long serialVersionUID = -7744904312001901326L;

					@Override
					protected void createButtonsForButtonBar(Composite parent) {
						createButton(parent, IDialogConstants.YES_ID, IDialogConstants.get().YES_LABEL, false);
						createButton(parent, IDialogConstants.NO_ID, IDialogConstants.get().NO_LABEL, true);
						createDetailsButton(parent);
					}

					/*
					 * (non-Javadoc)
					 * @see
					 * org.eclipse.jface.dialogs.ErrorDialog#buttonPressed(int)
					 */
					@Override
					protected void buttonPressed(int id) {
						if (id == IDialogConstants.YES_ID) {
							super.buttonPressed(IDialogConstants.OK_ID);
						} else if (id == IDialogConstants.NO_ID) {
							super.buttonPressed(IDialogConstants.CANCEL_ID);
						}
						super.buttonPressed(id);
					}
				};
				int code = dialog.open();
				result[0] = code == 0;
			}
		};
		if (syncExec) {
			shell.getDisplay().syncExec(runnable);
		} else {
			runnable.run();
		}
		return result[0];
	}

	private static boolean isIgnoredStatus(IStatus status, String[] ignoreModelProviderIds) {
		if (ignoreModelProviderIds == null) {
			return false;
		}
		if (status instanceof ModelStatus) {
			ModelStatus ms = (ModelStatus) status;
			for (String id : ignoreModelProviderIds) {
				if (ms.getModelProviderId().equals(id)) {
					return true;
				}
				IModelProviderDescriptor desc = ModelProvider.getModelProviderDescriptor(id);
				String[] extended = desc.getExtendedModels();
				if (isIgnoredStatus(status, extended)) {
					return true;
				}
			}
		}
		return false;
	}
}
