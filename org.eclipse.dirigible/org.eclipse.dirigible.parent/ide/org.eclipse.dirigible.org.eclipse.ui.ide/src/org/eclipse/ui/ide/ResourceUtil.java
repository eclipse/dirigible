/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.ide;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.resources.mapping.ResourceTraversal;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Utility class for manipulating resources and determining correspondences
 * between resources and workbench objects.
 * <p>
 * This class provides all its functionality via static methods. It is not
 * intended to be instantiated or subclassed.
 * </p>
 * 
 * @since 3.1
 */
public final class ResourceUtil {

	private ResourceUtil() {
		// prevent instantiation
	}

	/**
	 * Returns the file corresponding to the given editor input, or
	 * <code>null</code> if there is no applicable file. Returns
	 * <code>null</code> if the given editor input is <code>null</code>.
	 * 
	 * @param editorInput
	 *            the editor input, or <code>null</code>
	 * @return the file corresponding to the editor input, or <code>null</code>
	 */
	public static IFile getFile(IEditorInput editorInput) {
		if (editorInput == null) {
			return null;
		}
		// Note: do not treat IFileEditorInput as a special case. Use the
		// adapter mechanism instead.
		// See Bug 87288 [IDE] [EditorMgmt] Should avoid explicit checks for
		// [I]FileEditorInput
		Object o = editorInput.getAdapter(IFile.class);
		if (o instanceof IFile) {
			return (IFile) o;
		}
		return null;
	}

	/**
	 * Returns the resource corresponding to the given editor input, or
	 * <code>null</code> if there is no applicable resource. Returns
	 * <code>null</code> if the given editor input is <code>null</code>.
	 * 
	 * @param editorInput
	 *            the editor input
	 * @return the file corresponding to the editor input, or <code>null</code>
	 */
	public static IResource getResource(IEditorInput editorInput) {
		if (editorInput == null) {
			return null;
		}
		// Note: do not treat IFileEditorInput as a special case. Use the
		// adapter mechanism instead.
		// See Bug 87288 [IDE] [EditorMgmt] Should avoid explicit checks for
		// [I]FileEditorInput
		Object o = editorInput.getAdapter(IResource.class);
		if (o instanceof IResource) {
			return (IResource) o;
		}
		// the input may adapt to IFile but not IResource
		return getFile(editorInput);
	}

	/**
	 * Returns the editor in the given page whose input represents the given
	 * file, or <code>null</code> if there is no such editor.
	 * 
	 * @param page
	 *            the workbench page
	 * @param file
	 *            the file
	 * @return the matching editor, or <code>null</code>
	 */
	public static IEditorPart findEditor(IWorkbenchPage page, IFile file) {
		// handle the common case where the editor input is a FileEditorInput
		IEditorPart editor = page.findEditor(new FileEditorInput(file));
		if (editor != null) {
			return editor;
		}
		// check for editors that have their own kind of input that adapts to
		// IFile,
		// being careful not to force loading of the editor
		IEditorReference[] refs = page.getEditorReferences();
		for (int i = 0; i < refs.length; i++) {
			IEditorReference ref = refs[i];
			IEditorPart part = ref.getEditor(false);
			if (part != null) {
				IFile editorFile = getFile(part.getEditorInput());
				if (editorFile != null && file.equals(editorFile)) {
					return part;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the resource corresponding to the given model element, or
	 * <code>null</code> if there is no applicable resource.
	 * 
	 * @param element
	 *            the model element, or <code>null</code>
	 * @return the resource corresponding to the model element, or
	 *         <code>null</code>
	 * @since 3.2
	 */
	public static IResource getResource(Object element) {
		if (element == null) {
			return null;
		}
		if (element instanceof IResource) {
			return (IResource) element;
		}
		return (IResource) getAdapter(element, IResource.class, true);
	}

	/**
	 * Returns the file corresponding to the given model element, or
	 * <code>null</code> if there is no applicable file.
	 * 
	 * @param element
	 *            the model element, or <code>null</code>
	 * @return the resource corresponding to the model element, or
	 *         <code>null</code>
	 * @since 3.2
	 */
	public static IFile getFile(Object element) {
		if (element == null) {
			return null;
		}

		// try direct instanceof check
		if (element instanceof IFile) {
			return (IFile) element;
		}

		// try for ResourceMapping
		ResourceMapping mapping = getResourceMapping(element);
		if (mapping != null) {
			return getFileFromResourceMapping(mapping);
		}

		// try for IFile adapter (before IResource adapter, since it's more
		// specific)
		Object adapter = getAdapter(element, IFile.class, true);
		if (adapter instanceof IFile) {
			return (IFile) adapter;
		}

		// try for IResource adapter
		adapter = getAdapter(element, IResource.class, true);
		if (adapter instanceof IFile) {
			return (IFile) adapter;
		}
		return null;
	}

	/**
	 * Returns the resource mapping corresponding to the given model element, or
	 * <code>null</code> if there is no applicable resource mapping.
	 * 
	 * @param element
	 *            the model element, or <code>null</code>
	 * @return the resource mapping corresponding to the model element, or
	 *         <code>null</code>
	 * @since 3.2
	 */
	public static ResourceMapping getResourceMapping(Object element) {
		if (element == null) {
			return null;
		}

		// try direct instanceof check
		if (element instanceof ResourceMapping) {
			return (ResourceMapping) element;
		}

		// try for ResourceMapping adapter
		Object adapter = getAdapter(element, ResourceMapping.class, true);
		if (adapter instanceof ResourceMapping) {
			return (ResourceMapping) adapter;
		}
		return null;
	}

	/**
	 * Tries to extra a single file from the given resource mapping. Returns the
	 * file if the mapping maps to a single file, or <code>null</code> if it
	 * maps to zero or multiple files.
	 * 
	 * @param mapping
	 *            the resource mapping
	 * @return the file, or <code>null</code>
	 */
	private static IFile getFileFromResourceMapping(ResourceMapping mapping) {
		IResource resource = getResourceFromResourceMapping(mapping);
		if (resource instanceof IFile) {
			return (IFile) resource;
		}
		return null;
	}

	/**
	 * Tries to extra a single resource from the given resource mapping. Returns
	 * the resource if the mapping maps to a single resource, or
	 * <code>null</code> if it maps to zero or multiple resources.
	 * 
	 * @param mapping
	 *            the resource mapping
	 * @return the resource, or <code>null</code>
	 */
	private static IResource getResourceFromResourceMapping(
			ResourceMapping mapping) {
		try {
			ResourceTraversal[] traversals = mapping.getTraversals(null, null);
			if (traversals.length != 1) {
				return null;
			}
			ResourceTraversal traversal = traversals[0];
			// TODO: need to honour traversal flags
			IResource[] resources = traversal.getResources();
			if (resources.length != 1) {
				return null;
			}
			return resources[0];
		} catch (CoreException e) {
			StatusManager.getManager().handle(e,
					IDEWorkbenchPlugin.IDE_WORKBENCH);
			return null;
		}
	}

	/**
	 * Returns the specified adapter for the given element, or <code>null</code>
	 * if no such adapter was found.
	 * 
	 * @param element
	 *            the model element
	 * @param adapterType
	 *            the type of adapter to look up
	 * @param forceLoad
	 *            <code>true</code> to force loading of the plug-in providing
	 *            the adapter, <code>false</code> otherwise
	 * @return the adapter
	 * @since 3.2
	 */
	public static Object getAdapter(Object element, Class<?> adapterType,
			boolean forceLoad) {
		if (element instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) element;
			Object o = adaptable.getAdapter(adapterType);
			if (o != null) {
				return o;
			}
		}
		if (forceLoad) {
			return Platform.getAdapterManager().loadAdapter(element,
					adapterType.getName());
		}
		return Platform.getAdapterManager().getAdapter(element, adapterType);
	}

}
