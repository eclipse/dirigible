/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.dirigible.ide.ui.rap.stacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.ui.interactiondesign.ConfigurableStack;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.presentations.IStackPresentationSite;

import org.eclipse.dirigible.ide.ui.rap.builders.DummyBuilder;
import org.eclipse.dirigible.ide.ui.rap.managers.ViewToolBarManager;
import org.eclipse.dirigible.ide.ui.rap.shared.LayoutSetConstants;

/**
 * This popup dialog is used for configure presentation properties. Its opened
 * in the <code>{@link ExampleConfigAction#run()}</code> method.
 */
public class ConfigurationDialog extends PopupDialog {

	private static final long serialVersionUID = -431031471140171671L;
	private static final String CONFIGURATION_FOR = Messages.ConfigurationDialog_CONFIGURATION_FOR;
	private static final int OFFSET = 3;
	private IStackPresentationSite site;
	private ConfigAction action;
	private HashMap<String, Button> actionButtonMap = new HashMap<String, Button>();
	private List<String> actionList = new ArrayList<String>();
	private transient ElementBuilder builder;
	private Button viewMenuBox;
	private boolean viewMenuVisChanged;
	private Label lastImageLabel;
	private Label description;
	private Shell modalBackground;

	public ConfigurationDialog(final Shell parent, final int shellStyle,
			final IStackPresentationSite site, final ConfigAction action) {
		super(parent, shellStyle, true, false, false, false, false, null, null);

		// parent.setBackgroundMode( SWT.INHERIT_NONE );
		this.site = site;
		this.action = action;
		builder = new DummyBuilder(parent,
				LayoutSetConstants.SET_ID_CONFIG_DIALOG);
		viewMenuVisChanged = false;
	}

	protected void adjustBounds() {
		getShell().layout();
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		Rectangle bounds = window.getShell().getBounds();
		int newWidth = getShell().getBounds().width + 20;
		int newHeight = getShell().getBounds().height + 20;
		getShell().setBounds(bounds.x + (bounds.width / 2) - (newWidth / 2),
				bounds.y + (bounds.height / 2) - (newHeight / 2), newWidth,
				newHeight);
	}

	public boolean close(final boolean save) {
		if (save) {
			// save the viewmenu visibility
			saveViewMenuVisibility();
			// Save ViewActionVisibility
			saveViewActionVisibilities();
		}
		return close();
	}

	public boolean close() {
		if (!modalBackground.isDisposed()) {
			modalBackground.close();
			modalBackground.dispose();
		}
		ConfigurableStack stackPresentation = (ConfigurableStack) action
				.getStackPresentation();
		IToolBarManager manager = stackPresentation.getPartToolBarManager();
		if (manager != null) {
			manager.update(true);
		}
		action.fireToolBarChange();
		return super.close();
	}

	protected Control createDialogArea(final Composite parent) {
		Composite background = new Composite(parent, SWT.NONE);
		background.setLayout(new FormLayout());
		Color white = builder.getColor(LayoutSetConstants.CONFIG_WHITE);

		Composite configComposite = new Composite(background, SWT.NONE);
		FormData fdConfigComposite = new FormData();
		fdConfigComposite.top = new FormAttachment(0, 0);
		fdConfigComposite.left = new FormAttachment(0, 10);
		fdConfigComposite.right = new FormAttachment(100, -10);
		fdConfigComposite.bottom = new FormAttachment(100, -10);
		configComposite.setLayoutData(fdConfigComposite);
		configComposite.setLayout(new FormLayout());

		// Fill with ViewActions
		loadActionSettings(configComposite);

		// Viewmenu
		hookViewMenuArea(white, configComposite);

		// OK / Cancel buttons
		Button cancel = new Button(configComposite, SWT.PUSH);
		cancel.setText(Messages.ConfigurationDialog_CANCEL);
		FormData fdCancel = new FormData();
		cancel.setLayoutData(fdCancel);
		fdCancel.bottom = new FormAttachment(100, 0);
		fdCancel.right = new FormAttachment(100, 0);
		fdCancel.width = 90;
		cancel.addSelectionListener(new SelectionAdapter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -2484093510302579922L;

			public void widgetSelected(final SelectionEvent e) {
				close(false);
			};
		});

		Button ok = new Button(configComposite, SWT.PUSH);
		ok.setText(Messages.ConfigurationDialog_OK);
		FormData fdOK = new FormData();
		ok.setLayoutData(fdOK);
		fdOK.right = new FormAttachment(cancel, -OFFSET);
		fdOK.bottom = fdCancel.bottom;
		fdOK.width = 90;
		ok.addSelectionListener(new SelectionAdapter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1428398836574666017L;

			public void widgetSelected(final SelectionEvent e) {
				close(true);
			};
		});
		ok.moveAbove(cancel);
		return background;
	}

	@SuppressWarnings("deprecation")
	private void hookViewMenuArea(final Color white,
			final Composite configComposite) {
		if (action.hasPartMenu()) {
			viewMenuBox = new Button(configComposite, SWT.CHECK);
			viewMenuBox.setForeground(white);
			viewMenuBox.setSelection(action.isPartMenuVisible());
			viewMenuVisChanged = viewMenuBox.getSelection();
			FormData fdViewMenuBox = new FormData();
			viewMenuBox.setLayoutData(fdViewMenuBox);
			viewMenuBox.setData(WidgetUtil.CUSTOM_VARIANT, "configMenuButton"); //$NON-NLS-1$
			if (lastImageLabel != null) {
				fdViewMenuBox.top = new FormAttachment(lastImageLabel, OFFSET);
				fdViewMenuBox.left = new FormAttachment(lastImageLabel,
						OFFSET + 5);
			} else {
				fdViewMenuBox.top = new FormAttachment(description, OFFSET);
				fdViewMenuBox.left = new FormAttachment(0, OFFSET + 5);
			}
			viewMenuBox.setText("viewmenu"); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("deprecation")
	private void loadActionSettings(final Composite container) {
		ConfigurableStack stackPresentation = (ConfigurableStack) action
				.getStackPresentation();
		IToolBarManager manager = stackPresentation.getPartToolBarManager();
		description = null;
		description = new Label(container, SWT.NONE);
		description.setText("Visible actions"); //$NON-NLS-1$
		FormData fdActionDesc = new FormData();
		description.setLayoutData(fdActionDesc);
		if (viewMenuBox != null) {
			fdActionDesc.top = new FormAttachment(viewMenuBox, OFFSET);
		} else {
			fdActionDesc.top = new FormAttachment(0, OFFSET);
		}
		fdActionDesc.left = new FormAttachment(0, OFFSET);
		if (manager != null) {
			String paneId = stackPresentation.getPaneId(site);
			if (manager instanceof ViewToolBarManager) {
				// manager.update( true );
				List<ToolItem> toolItems = ((ViewToolBarManager) manager)
						.getToolItems();
				for (int i = 0; i < toolItems.size(); i++) {
					ToolItem item = (ToolItem) toolItems.get(i);
					if (item != null && !item.isDisposed()) {
						// handle parameter
						IContributionItem contribItem = (IContributionItem) item
								.getData();
						String itemId = contribItem.getId();
						Image icon = item.getImage();
						String text = ""; //$NON-NLS-1$
						if (item.getText() != null
								&& !item.getText().equals("")) { //$NON-NLS-1$
							text = item.getText();
						} else {
							text = item.getToolTipText();
						}

						// render the dialog
						Label imageLabel = new Label(container, SWT.NONE);
						imageLabel.setImage(icon);
						FormData fdImageLabel = new FormData();
						imageLabel.setLayoutData(fdImageLabel);
						if (lastImageLabel != null) {
							fdImageLabel.top = new FormAttachment(
									lastImageLabel, OFFSET);
							lastImageLabel = imageLabel;
						} else {
							fdImageLabel.top = new FormAttachment(description,
									OFFSET);
							lastImageLabel = imageLabel;
						}
						fdImageLabel.left = new FormAttachment(0, OFFSET * 4);

						Button check = new Button(container, SWT.CHECK);
						check.setText(text);
						check.setLayoutData(new GridData(
								GridData.FILL_HORIZONTAL));
						boolean selected = action.isViewActionVisibile(paneId,
								itemId);
						FormData fdCheck = new FormData();
						check.setLayoutData(fdCheck);
						fdCheck.left = new FormAttachment(imageLabel,
								OFFSET + 5);
						fdCheck.top = fdImageLabel.top;
						check.setSelection(selected);
						check.setData(WidgetUtil.CUSTOM_VARIANT,
								"configMenuButton"); //$NON-NLS-1$
						actionButtonMap.put(itemId, check);
						actionList.add(itemId);
						lastImageLabel = imageLabel;
					}
				}
			}
		}
	}

	public int open() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		Rectangle bounds = window.getShell().getBounds();
		modalBackground = new Shell(getParentShell(), SWT.NO_TRIM);
		modalBackground.setAlpha(80);
		modalBackground.setBounds(bounds);
		modalBackground.open();

		int result = super.open();
		Shell shell = getShell();
		shell.setBackgroundMode(SWT.INHERIT_NONE);
		shell.setText(CONFIGURATION_FOR + site.getSelectedPart().getName());
		String configDialogIcon = LayoutSetConstants.CONFIG_DIALOG_ICON;
		shell.setImage(builder.getImage(configDialogIcon));
		shell.setActive();
		shell.setFocus();
		action.fireToolBarChange();
		adjustBounds();
		return result;
	}

	private void saveViewActionVisibilities() {
		ConfigurableStack stackPresentation = (ConfigurableStack) action
				.getStackPresentation();
		String paneId = stackPresentation.getPaneId(site);

		for (int i = 0; i < actionList.size(); i++) {
			String actionId = (String) actionList.get(i);
			Button check = (Button) actionButtonMap.get(actionId);
			action.saveViewActionVisibility(paneId, actionId,
					check.getSelection());
		}
	}

	private void saveViewMenuVisibility() {
		if (viewMenuBox != null) {
			boolean selection = viewMenuBox.getSelection();
			if (selection != viewMenuVisChanged) {
				action.savePartMenuVisibility(selection);
			}
		}
	}

}
