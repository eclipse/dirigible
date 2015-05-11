/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.ide.dialogs;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

/**
 * Selection dialog to select files and/or folders on the file system. Use
 * setInput to set input to an IFileStore that points to a folder.
 * 
 * @since 2.1
 */
public class FileFolderSelectionDialog extends ElementTreeSelectionDialog {

	private static final long serialVersionUID = 665754415050903301L;

	/**
	 * Label provider for IFileStore objects.
	 */
	private static class FileLabelProvider extends LabelProvider {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4516447133235673266L;

		private static final Image IMG_FOLDER = PlatformUI.getWorkbench()
				.getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);

		private static final Image IMG_FILE = PlatformUI.getWorkbench()
				.getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			if (element instanceof IFileStore) {
				IFileStore curr = (IFileStore) element;
				if (curr.fetchInfo().isDirectory()) {
					return IMG_FOLDER;
				}
				return IMG_FILE;
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			if (element instanceof IFileStore) {
				return ((IFileStore) element).getName();
			}
			return super.getText(element);
		}
	}

	/**
	 * Content provider for IFileStore objects.
	 */
	private static class FileContentProvider implements ITreeContentProvider {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8644302951345710230L;

		private static final Object[] EMPTY = new Object[0];

		private transient IFileStoreFilter fileFilter;

		/**
		 * Creates a new instance of the receiver.
		 * 
		 * @param showFiles
		 *            <code>true</code> files and folders are returned by the
		 *            receiver. <code>false</code> only folders are returned.
		 */
		public FileContentProvider(final boolean showFiles) {
			fileFilter = new IFileStoreFilter() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.ui.internal.ide.dialogs.IFileStoreFilter#accept
				 * (org.eclipse.core.filesystem.IFileStore)
				 */
				public boolean accept(IFileStore file) {
					if (!file.fetchInfo().isDirectory() && showFiles == false) {
						return false;
					}
					return true;
				}
			};
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IFileStore) {
				IFileStore[] children = IDEResourceInfoUtils.listFileStores(
						(IFileStore) parentElement, fileFilter,
						new NullProgressMonitor());
				if (children != null) {
					return children;
				}
			}
			return EMPTY; // NOPMD
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang
		 * .Object)
		 */
		public Object getParent(Object element) {
			if (element instanceof IFileStore) {
				return ((IFileStore) element).getParent();
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		public Object[] getElements(Object element) {
			return getChildren(element);
		}

		public void dispose() {
			//
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			//
		}
	}

	/**
	 * Viewer sorter that places folders first, then files.
	 */
	private static class FileViewerSorter extends ViewerComparator {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2487248717780318069L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
		 */
		public int category(Object element) {
			if (element instanceof IFileStore
					&& !((IFileStore) element).fetchInfo().isDirectory()) {
				return 1;
			}
			return 0;
		}
	}

	/**
	 * Validates the selection based on the multi select and folder setting.
	 */
	private static class FileSelectionValidator implements
			ISelectionStatusValidator {
		private boolean multiSelect;

		private boolean acceptFolders;

		/**
		 * Creates a new instance of the receiver.
		 * 
		 * @param multiSelect
		 *            <code>true</code> if multi selection is allowed.
		 *            <code>false</code> if only single selection is allowed.
		 * @param acceptFolders
		 *            <code>true</code> if folders can be selected in the
		 *            dialog. <code>false</code> only files and be selected.
		 */
		public FileSelectionValidator(boolean multiSelect, boolean acceptFolders) {
			this.multiSelect = multiSelect;
			this.acceptFolders = acceptFolders;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang
		 * .Object[])
		 */
		public IStatus validate(Object[] selection) {
			int nSelected = selection.length;
			String pluginId = IDEWorkbenchPlugin.IDE_WORKBENCH;

			if (nSelected == 0 || (nSelected > 1 && multiSelect == false)) {
				return new Status(IStatus.ERROR, pluginId, IStatus.ERROR,
						IDEResourceInfoUtils.EMPTY_STRING, null);
			}
			for (int i = 0; i < selection.length; i++) {
				Object curr = selection[i];
				if (curr instanceof IFileStore) {
					IFileStore file = (IFileStore) curr;
					if (acceptFolders == false
							&& file.fetchInfo().isDirectory()) {
						return new Status(IStatus.ERROR, pluginId,
								IStatus.ERROR,
								IDEResourceInfoUtils.EMPTY_STRING, null);
					}

				}
			}
			return Status.OK_STATUS;
		}
	}

	/**
	 * Creates a new instance of the receiver.
	 * 
	 * @param parent
	 * @param multiSelect
	 *            <code>true</code> if multi selection is allowed.
	 *            <code>false</code> if only single selection is allowed.
	 * @param type
	 *            one or both of <code>IResource.FILE</code> and
	 *            <code>IResource.FOLDER</code>, ORed together. If
	 *            <code>IResource.FILE</code> is specified files and folders are
	 *            displayed in the dialog. Otherwise only folders are displayed.
	 *            If <code>IResource.FOLDER</code> is specified folders can be
	 *            selected in addition to files.
	 */
	public FileFolderSelectionDialog(Shell parent, boolean multiSelect, int type) {
		super(parent, new FileLabelProvider(), new FileContentProvider(
				(type & IResource.FILE) != 0));
		setComparator(new FileViewerSorter());
		setValidator(new FileSelectionValidator(multiSelect,
				(type & IResource.FOLDER) != 0));
	}
}
