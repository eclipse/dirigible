/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.ui.view;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import org.eclipse.dirigible.ide.common.ExtensionPointUtils;
import org.eclipse.dirigible.ide.common.image.ImageUtils;
import org.eclipse.dirigible.ide.workspace.ui.view.newmenu.NewMenuException;
import org.eclipse.dirigible.ide.workspace.ui.view.newmenu.NewMenuItemDescriptor;
import org.eclipse.dirigible.repository.logging.Logger;

public class NewMenuItemViewProvider {

	private static final Logger logger = Logger.getLogger(NewMenuItemViewProvider.class);

	private static final String NEW_MENU_ITEM_ELEMENT_NAME = "newMenuItem"; //$NON-NLS-1$
	
	private static final String NEW_MENU_ITEM_DEFAULT_HANDLER_ATTRIBUTE = "defaultHandler"; //$NON-NLS-1$
	private static final String NEW_MENU_ITEM_IMAGE_NAME_ATTRIBUTE = "imageName"; //$NON-NLS-1$
	private static final String NEW_MENU_ITEM_IMAGE_PREFIX_ATTRIBUTE = "imagePrefix"; //$NON-NLS-1$
	private static final String NEW_MENU_ITEM_IMAGE_BUNDLE_ATTRIBUTE = "imageBundle"; //$NON-NLS-1$

	private static final String NEW_MENU_ITEM_ORDER_ATTRIBUTE = "order"; //$NON-NLS-1$
	private static final String NEW_MENU_ITEM_TOOL_TIP_ATTRIBUTE = "toolTip"; //$NON-NLS-1$
	private static final String NEW_MENU_ITEM_TEXT_ATTRIBUTE = "text"; //$NON-NLS-1$
	private static final String NEW_MENU_EXTENSION_POINT_ID = "org.eclipse.dirigible.ide.workspace.ui.new.menu";

	private static final String INVALID_DEFAULT_HANDLER_IMPLEMENTING_CLASS_CONFIGURED = Messages.WorkspaceExplorerView_INVALID_DEFAULT_HANDLER_IMPLEMENTING_CLASS_CONFIGURED;
	private static final String COULD_NOT_EXECUTE_COMMAND_DUE_TO_THE_FOLLOWING_ERROR = Messages.WorkspaceExplorerView_COULD_NOT_EXECUTE_COMMAND_DUE_TO_THE_FOLLOWING_ERROR;
	private static final String COULD_NOT_EXECUTE_COMMAND = Messages.WorkspaceExplorerView_COULD_NOT_EXECUTE_COMMAND;
	private static final String EXTENSION_POINT_0_COULD_NOT_BE_FOUND = Messages.WorkspaceExplorerView_EXTENSION_POINT_0_COULD_NOT_BE_FOUND;
	private static final String COULD_NOT_CREATE_NEW_MENU_ITEM_INSTANCE = Messages.WorkspaceExplorerView_COULD_NOT_CREATE_NEW_MENU_ITEM_INSTANCE;
	private static final String OPERATION_FAILED = Messages.WorkspaceExplorerView_OPERATION_FAILED;
	private static final String CHECK_LOGS_FOR_MORE_INFO = Messages.WorkspaceExplorerView_CHECK_LOGS_FOR_MORE_INFO;
	
	public static Menu createMenu(Composite parent) {
		final Menu menu = new Menu(parent);

		for (final NewMenuItemDescriptor descriptor : getNewMenuItemDescriptors()) {
			createMenuItem(menu, descriptor.getText(), ImageUtils.createImage(ImageUtils
					.getIconURL(descriptor.getImageBundle(), descriptor.getImagePrefix(),
							descriptor.getImageName())), new SelectionListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						descriptor.getDefaultHandler().execute(null);
					} catch (ExecutionException ex) {
						logger.error(COULD_NOT_EXECUTE_COMMAND, ex);
						MessageDialog.openError(
								null,
								OPERATION_FAILED,
								COULD_NOT_EXECUTE_COMMAND_DUE_TO_THE_FOLLOWING_ERROR
										+ ex.getMessage() + CHECK_LOGS_FOR_MORE_INFO);
					}

				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					//
				}
			});
		}

		return menu;
	}
	
	public static List<NewMenuItemDescriptor> getNewMenuItemDescriptors() {

		List<NewMenuItemDescriptor> newMenuItemDescriptors = new ArrayList<NewMenuItemDescriptor>();
		final IExtensionPoint extensionPoint = ExtensionPointUtils
				.getExtensionPoint(NEW_MENU_EXTENSION_POINT_ID);
		if (extensionPoint == null) {
			throw new NewMenuException(format(EXTENSION_POINT_0_COULD_NOT_BE_FOUND,
					NEW_MENU_EXTENSION_POINT_ID));
		}
		final IConfigurationElement[] newMenuItemDescriptorElements = getNewMenuElements(extensionPoint
				.getExtensions());

		String newMenuItemName = null;
		try {
			for (IConfigurationElement descriptorElement : newMenuItemDescriptorElements) {
				newMenuItemName = descriptorElement.getAttribute(NEW_MENU_ITEM_TEXT_ATTRIBUTE);
				NewMenuItemDescriptor descriptor = createNewMenuItemDescriptor(descriptorElement);
				if (descriptor != null) {
					newMenuItemDescriptors.add(descriptor);
				}
			}
		} catch (CoreException e) {
			throw new NewMenuException(String.format(COULD_NOT_CREATE_NEW_MENU_ITEM_INSTANCE,
					newMenuItemName), e);
		}

		Collections.sort(newMenuItemDescriptors);
		return newMenuItemDescriptors;
	}
	
	private static IConfigurationElement[] getNewMenuElements(IExtension[] extensions) {
		final List<IConfigurationElement> result = new ArrayList<IConfigurationElement>();
		for (IExtension extension : extensions) {
			for (IConfigurationElement element : extension.getConfigurationElements()) {
				if (NEW_MENU_ITEM_ELEMENT_NAME.equals(element.getName())) {
					result.add(element);
				}
			}
		}
		return result.toArray(new IConfigurationElement[0]);
	}

	private static NewMenuItemDescriptor createNewMenuItemDescriptor(
			IConfigurationElement configurationElement) throws CoreException {

		NewMenuItemDescriptor newMenuItemDescriptor = new NewMenuItemDescriptor();
		newMenuItemDescriptor.setText(configurationElement
				.getAttribute(NEW_MENU_ITEM_TEXT_ATTRIBUTE));
		newMenuItemDescriptor.setToolTip(configurationElement
				.getAttribute(NEW_MENU_ITEM_TOOL_TIP_ATTRIBUTE));
		try {
			newMenuItemDescriptor.setOrder(Integer.parseInt(configurationElement
					.getAttribute(NEW_MENU_ITEM_ORDER_ATTRIBUTE)));
		} catch (NumberFormatException e) {
			newMenuItemDescriptor.setOrder(0);
		}
		newMenuItemDescriptor.setImageBundle(configurationElement
				.getAttribute(NEW_MENU_ITEM_IMAGE_BUNDLE_ATTRIBUTE));
		newMenuItemDescriptor.setImagePrefix(configurationElement
				.getAttribute(NEW_MENU_ITEM_IMAGE_PREFIX_ATTRIBUTE));
		newMenuItemDescriptor.setImageName(configurationElement
				.getAttribute(NEW_MENU_ITEM_IMAGE_NAME_ATTRIBUTE));

		Object handler = configurationElement
				.createExecutableExtension(NEW_MENU_ITEM_DEFAULT_HANDLER_ATTRIBUTE);
		if (!(handler instanceof IHandler)) {
			throw new NewMenuException(INVALID_DEFAULT_HANDLER_IMPLEMENTING_CLASS_CONFIGURED);
		}

		newMenuItemDescriptor.setDefaultHandler((IHandler) handler);
		return newMenuItemDescriptor;
	}

	private static MenuItem createMenuItem(Menu menu, String text, Image image, SelectionListener listener) {
		MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText(text);
		menuItem.setImage(image);
		menuItem.addSelectionListener(listener);
		return menuItem;
	}
}
