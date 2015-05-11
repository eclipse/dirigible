/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
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
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.ui.interactiondesign.ConfigurableStack;
import org.eclipse.rap.ui.interactiondesign.ConfigurationAction;
import org.eclipse.rap.ui.interactiondesign.IConfigurationChangeListener;
import org.eclipse.rap.ui.interactiondesign.PresentationFactory;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.presentations.PresentablePart;
import org.eclipse.ui.presentations.IPartMenu;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackDropResult;

import org.eclipse.dirigible.ide.ui.rap.builders.StackPresentationBuider;
import org.eclipse.dirigible.ide.ui.rap.shared.LayoutSetConstants;

@SuppressWarnings("restriction")
public class ViewStackPresentation extends ConfigurableStack {

	private static final String HAS_NO_ACTIONS_OR_VIEWMENU_TO_CONFIGURE = Messages.ViewStackPresentation_HAS_NO_ACTIONS_OR_VIEWMENU_TO_CONFIGURE;
	private static final String CONFIGURE_THE_ACTIONS_AND_VIEWMENU_FROM = Messages.ViewStackPresentation_CONFIGURE_THE_ACTIONS_AND_VIEWMENU_FROM;
	private static final String VARIANT_PART_INACTIVE = "partInactive"; //$NON-NLS-1$
	private static final String VARIANT_PART_ACTIVE = "partActive"; //$NON-NLS-1$
	private static final String VARIANT_PART_INACTIVE_ACTIVE = "partInActiveActive"; //$NON-NLS-1$
	private static final int BUTTON_SPACING = 6;
	private static final String ID_CLOSE = "close"; //$NON-NLS-1$
	private static final String BUTTON_ID = "buttonId"; //$NON-NLS-1$
	private static final int WIDTH_SPACING = 65;
	private static final int HEIGHT_SPACING = 15;

	private Control presentationControl;
	private IPresentablePart currentPart;
	private ElementBuilder stackBuilder;
	private Composite tabBg;
	private Composite confArea;
	private Button confButton;
	private Label confCorner;
	private Map<IPresentablePart, Composite> partButtonMap = new HashMap<IPresentablePart, Composite>();
	private List<IPresentablePart> partList = new ArrayList<IPresentablePart>();
	private List<Object> buttonList = new ArrayList<Object>();
	private Composite toolbarBg;
	private Shell toolBarLayer;
	private int state;
	protected boolean deactivated;
	private Button viewMenuButton;
	private Map<IPresentablePart, IPropertyListener> dirtyListenerMap = new HashMap<IPresentablePart, IPropertyListener>();
	private Button overflowButton;
	private List<Control> overflowButtons = new ArrayList<Control>();
	private Map<Control, IPresentablePart> buttonPartMap = new HashMap<Control, IPresentablePart>();
	private IPresentablePart oldPart;
	private boolean allActionsVisible; // I061150: Look down for more info

	private class DirtyListener implements IPropertyListener {

		private IPresentablePart part;

		public DirtyListener(final IPresentablePart part) {
			this.part = part;
		}

		public void propertyChanged(Object source, int propId) {
			if (propId == ISaveablePart.PROP_DIRTY) {
				Button partButton = getPartButton(part);
				if (partButton != null) {
					String text = partButton.getText();
					char lastCharacter = getLastCharacter(text);
					if (part.isDirty()) {
						// mark the part as dirty
						if (lastCharacter != '*') {
							text = text + "*"; //$NON-NLS-1$
						}
					} else {
						// mark the part as clean
						if (lastCharacter == '*') {
							text = text.substring(0, text.length() - 1);
						}
					}
					partButton.setText(text);
				}
			}
		}

		private Button getPartButton(final IPresentablePart part) {
			Button result = null;
			Control object = partButtonMap.get(part);
			if (object instanceof Composite) {
				Control[] children = ((Composite) object).getChildren();
				if (children.length > 0 && children[0] instanceof Button) {
					result = (Button) children[0];
				}
			}
			return result;
		}

		private char getLastCharacter(final String text) {
			char[] starArray = new char[1];
			text.getChars(text.length() - 1, text.length(), starArray, 0);
			return starArray[0];
		}
	};

	public ViewStackPresentation() {
		state = AS_INACTIVE;
		deactivated = false;
		// I061150: I have introduced the allActionsVisible variable
		// in order to prevent an exception that gets thrown by the
		// handleConfigurationButton() method. Using the static method
		// ConfigAction.allActionsVisible() throws an exception when
		// the workbench gets destroyed (sometimes) and whenever the
		// workbench gets recreated (when the rap web page gets refreshed).
		allActionsVisible = ConfigAction.allActionsVisible();
	}

	public void init() {
		ConfigurationAction action = getConfigAction();
		if (action != null) {
			action.addConfigurationChangeListener(new IConfigurationChangeListener() {

				public void toolBarChanged() {
					ViewToolBarRegistry registry = ViewToolBarRegistry
							.getInstance();
					registry.fireToolBarChanged();
				}

				public void presentationChanged(
						final String newStackPresentationId) {
					// do nothing atm
				}
			});
		}
		presentationControl = createStyledControl();
		ViewToolBarRegistry registry = ViewToolBarRegistry.getInstance();
		registry.addViewPartPresentation(this);
	}

	void catchToolbarChange() {
		layoutToolBar();
		setBounds(presentationControl.getBounds());
	}

	private void createToolBarBg() {
		Composite tabBar = getTabBar();
		toolbarBg = new Composite(tabBar.getParent(), SWT.NONE);
		toolbarBg.setLayout(new FormLayout());
		Image bg = stackBuilder
				.getImage(LayoutSetConstants.STACK_VIEW_TOOLBAR_BG);
		toolbarBg.setBackgroundImage(bg);
		FormData fdToolBar = new FormData();
		toolbarBg.setLayoutData(fdToolBar);
		fdToolBar.left = new FormAttachment(0);
		fdToolBar.right = new FormAttachment(100);
		fdToolBar.top = new FormAttachment(tabBar);
		fdToolBar.height = bg.getBounds().height;
		toolbarBg.moveAbove(tabBar);
	}

	@SuppressWarnings("deprecation")
	private Control createStyledControl() {
		getParent().setData(WidgetUtil.CUSTOM_VARIANT, "compGray"); //$NON-NLS-1$
		final Composite parent = new Composite(getParent(), SWT.NONE);
		parent.addControlListener(new ControlAdapter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 977072776009331463L;

			public void controlResized(ControlEvent e) {
				setBounds(parent.getBounds());
			};
		});

		parent.setData(WidgetUtil.CUSTOM_VARIANT, "compGray"); //$NON-NLS-1$
		String setID = LayoutSetConstants.SET_ID_STACKPRESENTATION;
		stackBuilder = new StackPresentationBuider(parent, setID);
		stackBuilder.build();
		return parent;
	}

	private boolean isStandalone() {
		return getType().equals(PresentationFactory.KEY_STANDALONE_VIEW);
	}

	@SuppressWarnings("deprecation")
	public void addPart(final IPresentablePart newPart, final Object cookie) {
		checkTabBg();
		if (!isStandalone()) {
			createPartButton(newPart);
			partList.add(newPart);
			Control partControl = newPart.getControl();
			if (partControl != null) {
				partControl.getParent().setBackgroundMode(SWT.INHERIT_NONE);
				partControl.setData(WidgetUtil.CUSTOM_VARIANT, "partBorder"); //$NON-NLS-1$
			}
		} else {
			decorateStandaloneView(newPart);
		}
		// add the lsitener for the dirty state
		IPropertyListener listener = new DirtyListener(newPart);
		dirtyListenerMap.put(newPart, listener);
		newPart.addPropertyListener(listener);
	}

	@SuppressWarnings("deprecation")
	private void decorateStandaloneView(final IPresentablePart newPart) {
		checkTabBg();
		if (getShowTitle()) {
			getTabBar().setVisible(true);
			tabBg.setVisible(true);
			Label title = new Label(tabBg, SWT.NONE);
			title.setData(WidgetUtil.CUSTOM_VARIANT, "standaloneView"); //$NON-NLS-1$
			title.setText(newPart.getName());
		} else {
			getTabBar().setVisible(false);
			Object labelMap = stackBuilder.getAdapter(Map.class);
			if (labelMap != null && (labelMap instanceof Map)) {
				Map<?, ?> map = (Map<?, ?>) labelMap;
				Label left = (Label) map.get(StackPresentationBuider.LEFT);
				Label right = (Label) map.get(StackPresentationBuider.RIGHT);
				left.setVisible(false);
				right.setVisible(false);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void layoutToolBar() {
		if (toolbarBg == null && tabBg != null) {
			createToolBarBg();
		}
		if (currentPart != null && getPartPane(currentPart) != null) {
			Control toolBar = currentPart.getToolBar();
			final IPartMenu viewMenu = currentPart.getMenu();
			// viewmenu
			if (viewMenu != null) {
				if (viewMenuButton == null) {
					viewMenuButton = new Button(toolbarBg, SWT.PUSH);
					viewMenuButton.setData(WidgetUtil.CUSTOM_VARIANT,
							"clearButton"); //$NON-NLS-1$
					Image icon = stackBuilder
							.getImage(LayoutSetConstants.STACK_VIEW_MENU_ICON);
					viewMenuButton.setImage(icon);
					FormData fdViewMenuButton = new FormData();
					viewMenuButton.setLayoutData(fdViewMenuButton);
					fdViewMenuButton.right = new FormAttachment(100, -4);
					fdViewMenuButton.top = new FormAttachment(0, 8);
					viewMenuButton.addSelectionListener(new SelectionAdapter() {
						/**
						 * 
						 */
						private static final long serialVersionUID = -8904227778922403945L;

						public void widgetSelected(final SelectionEvent e) {
							Display display = viewMenuButton.getDisplay();
							int height = viewMenuButton.getSize().y;
							Point newLoc = display.map(viewMenuButton, null, 0,
									height);
							viewMenu.showMenu(newLoc);
						};
					});
				}
			} else if (viewMenuButton != null) {
				viewMenuButton.setVisible(false);
				viewMenuButton.dispose();
				viewMenuButton = null;
			}
			// toolbar
			Point size = toolbarBg.getSize();
			if (toolBar != null) {
				Point point = currentPart.getControl().getLocation();
				point.y -= (size.y + 2);
				point.x += (size.x - toolBar.getSize().x);
				if (viewMenu != null) {
					point.x -= 20;
				}
				toolBar.setLocation(point);
				toolbarBg.moveBelow(toolBar);
				presentationControl.moveBelow(toolBar);
				currentPart.getControl().moveBelow(toolBar);
			}
			// toolbarbg and layer
			if (toolBar != null || viewMenu != null) {
				toolbarBg.setVisible(true);
				// Toolbar Layer
				if (!deactivated) {
					getToolBarLayer();
					if (toolBarLayer != null) {
						toolBarLayer.setVisible(false);
						if (state != AS_ACTIVE_FOCUS) {
							Display display = toolBarLayer.getDisplay();
							Point newLocation = display.map(toolbarBg, null, 0,
									0);
							toolBarLayer.setBounds(newLocation.x,
									newLocation.y, size.x, size.y - 1);
							// toolBarLayer.moveAbove( toolBar );
							toolBarLayer.setVisible(true);
						}
					}
				}
			} else {
				toolbarBg.setVisible(false);
			}
			toolbarBg.layout(true);
		}
		handleConfigurationButton();
	}

	/*
	 * Deactivates the configuration button if the current part has nothing to
	 * configure.
	 */
	private void handleConfigurationButton() {
		boolean hasViewMenu = false;
		if (currentPart != null && currentPart instanceof PresentablePart) {
			PresentablePart part = (PresentablePart) currentPart;
			if (part.getPane() != null) {
				hasViewMenu = part.getPane().hasViewMenu();
				IToolBarManager manager = getPartToolBarManager();
				boolean hasViewActions = manager != null
						&& manager.getItems().length > 0;
				if ((hasViewActions || hasViewMenu) && !allActionsVisible) {
					if (confButton != null) {
						// enable conf button
						confButton.setEnabled(true);
						String toolTip = CONFIGURE_THE_ACTIONS_AND_VIEWMENU_FROM
								+ currentPart.getName();
						confButton.setToolTipText(toolTip);
					}
				} else {
					if (confButton != null) {
						// disable conf button
						confButton.setEnabled(false);
						String toolTip = currentPart.getName()
								+ HAS_NO_ACTIONS_OR_VIEWMENU_TO_CONFIGURE;
						confButton.setToolTipText(toolTip);
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void createPartButton(final IPresentablePart part) {
		Composite buttonArea = new Composite(tabBg, SWT.NONE);
		buttonArea.setData(WidgetUtil.CUSTOM_VARIANT, "inactiveButton"); //$NON-NLS-1$
		buttonArea.setLayout(new FormLayout());

		final Button partButton = new Button(buttonArea, SWT.PUSH);
		partButton.setData(WidgetUtil.CUSTOM_VARIANT, VARIANT_PART_INACTIVE);
		partButton.setText(part.getName());
		partButton.setImage(part.getTitleImage());
		partButton.setToolTipText(part.getTitleToolTip());
		final IPropertyListener nameListener = new IPropertyListener() {
			public void propertyChanged(final Object source, final int propId) {
				if (propId == IPresentablePart.PROP_PART_NAME) {
					partButton.setText(part.getName());
					tabBg.layout();
				} else if (propId == IPresentablePart.PROP_TITLE) {
					partButton.setToolTipText(part.getTitleToolTip());
				}
			}
		};
		part.addPropertyListener(nameListener);
		partButton.addDisposeListener(new DisposeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2510007493869927900L;

			public void widgetDisposed(final DisposeEvent event) {
				partButton.removeDisposeListener(this);
				part.removePropertyListener(nameListener);
			}
		});
		FormData fdPartButton = new FormData();
		partButton.setLayoutData(fdPartButton);
		fdPartButton.left = new FormAttachment(0);
		fdPartButton.top = new FormAttachment(0, 4);
		fdPartButton.bottom = new FormAttachment(100);
		partButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1275793364909865107L;

			public void widgetSelected(SelectionEvent e) {
				if (!currentPart.equals(part)) {
					selectPart(part);
				}
				activatePart(part);
				if (toolBarLayer != null) {
					toolBarLayer.setVisible(false);
				}
				// move the toolbar on top
				currentPart.getControl().moveAbove(null);
				Control toolBar = currentPart.getToolBar();
				if (toolBar != null) {
					toolBar.moveAbove(null);
				}
			};
		});
		partButton.addListener(SWT.MouseDoubleClick, new Listener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -5326419633493662414L;

			public void handleEvent(Event event) {
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();
				page.toggleZoom(getReference(part));
				if (toolBarLayer != null) {
					toolBarLayer.setVisible(false);
				}
				if (currentPart != null) {
					currentPart.getControl().moveAbove(null);
					Control toolBar = currentPart.getToolBar();
					if (toolBar != null) {
						toolBar.moveAbove(null);
					}
				}

			}
		});

		Composite corner = new Composite(buttonArea, SWT.NONE);
		corner.setData(WidgetUtil.CUSTOM_VARIANT, "compTrans"); //$NON-NLS-1$
		corner.setLayout(new FormLayout());
		String separatorActive = LayoutSetConstants.STACK_TAB_INACTIVE_SEPARATOR_ACTIVE;
		Image cornerImage = stackBuilder.getImage(separatorActive);
		FormData fdCorner = new FormData();
		corner.setLayoutData(fdCorner);
		fdCorner.right = new FormAttachment(100);
		fdCorner.bottom = new FormAttachment(100);
		fdCorner.width = cornerImage.getBounds().width;
		fdCorner.height = cornerImage.getBounds().height;
		fdPartButton.height = cornerImage.getBounds().height;
		fdPartButton.right = new FormAttachment(corner, -8);
		partButtonMap.put(part, buttonArea);
		buttonPartMap.put(buttonArea, part);
		buttonList.add(buttonArea);
	}

	protected void activatePart(final IPresentablePart part) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		IWorkbenchPart workbenchPart = getReference(part).getPart(true);
		if (workbenchPart != null) {
			if (oldPart != null) {
				Control toolBar = oldPart.getToolBar();
				if (toolBar != null) {
					toolBar.setVisible(false);
				}
			}
			activePage.activate(workbenchPart);
		}
	}

	private IWorkbenchPartReference getReference(final IPresentablePart part) {
		IWorkbenchPartReference result = null;
		if (part instanceof PresentablePart) {
			PresentablePart presentablePart = (PresentablePart) part;
			PartPane pane = presentablePart.getPane();
			result = pane.getPartReference();
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	private void makePartButtonActive(final IPresentablePart part) {
		Control object = partButtonMap.get(part);
		if (object instanceof Composite) {
			Composite buttonArea = (Composite) object;
			buttonArea.setData(WidgetUtil.CUSTOM_VARIANT, "tabInactive"); //$NON-NLS-1$
			checkHideSeparator(buttonArea);
			Color bg = stackBuilder
					.getColor(LayoutSetConstants.STACK_BUTTON_INACTIVE);
			buttonArea.setBackground(bg);
			Control[] children = buttonArea.getChildren();
			buttonArea.setLayout(new FormLayout());

			for (int i = 0; i < children.length; i++) {
				Control child = children[i];
				if (child instanceof Button) {
					// Partbutton
					Button partButton = (Button) child;
					partButton.setData(WidgetUtil.CUSTOM_VARIANT,
							VARIANT_PART_ACTIVE);
					FormData fdButton = (FormData) partButton.getLayoutData();
					FormData pos = stackBuilder
							.getPosition(LayoutSetConstants.STACK_BUTTON_TOP);
					fdButton.top = pos.top;
				} else if (child instanceof Composite) {
					// Corner
					Composite corner = (Composite) child;
					corner.setVisible(true);
					String cornerDesc = LayoutSetConstants.STACK_TAB_INACTIVE_CORNER_ACTIVE;
					Image cornerImage = stackBuilder.getImage(cornerDesc);
					corner.setBackgroundImage(null);
					FormData fdCorner = (FormData) corner.getLayoutData();
					fdCorner.top = new FormAttachment(0);
					fdCorner.width = cornerImage.getBounds().width;
					fdCorner.height = cornerImage.getBounds().height;
					if (part.isCloseable()) {
						Button close = new Button(buttonArea, SWT.PUSH);
						close.setData(BUTTON_ID, ID_CLOSE);
						close.setData(WidgetUtil.CUSTOM_VARIANT,
								"viewCloseInactive"); //$NON-NLS-1$
						close.addSelectionListener(new SelectionAdapter() {
							/**
							 * 
							 */
							private static final long serialVersionUID = 7874703475285130718L;

							public void widgetSelected(SelectionEvent e) {
								IStackPresentationSite site = getSite();
								if (site.isCloseable(part)) {
									site.close(new IPresentablePart[] { part });
									showPartButton(currentPart);
								}
							};
						});
						FormData fdClose = new FormData();
						close.setLayoutData(fdClose);
						fdClose.right = new FormAttachment(100, -5);
						fdClose.top = new FormAttachment(0, 6);
						fdClose.width = 7;
						fdClose.height = 8;
						close.setLayoutData(fdClose);
						close.moveAbove(corner);
					}
				}
			}
			buttonArea.getParent().layout();
		}

	}

	private void checkHideSeparator(final Composite buttonArea) {
		int indexOf = buttonList.indexOf(buttonArea);
		for (int i = 0; i < buttonList.size(); i++) {
			Composite area = (Composite) buttonList.get(i);
			Control[] children = area.getChildren();
			for (int j = 0; j < children.length; j++) {
				if (children[j] instanceof Composite) {
					if (i == indexOf || (i == indexOf - 1)) {
						((Composite) children[j]).setVisible(false);
					} else {
						((Composite) children[j]).setVisible(true);
					}

				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void makePartButtonInactive(final IPresentablePart part) {
		Control object = partButtonMap.get(part);
		if (object instanceof Composite) {
			Composite buttonArea = (Composite) object;
			buttonArea.setData(WidgetUtil.CUSTOM_VARIANT, "inactiveButton"); //$NON-NLS-1$
			buttonArea.setBackground(null);
			Control[] children = buttonArea.getChildren();
			for (int i = 0; i < children.length; i++) {
				Control child = children[i];
				if (child instanceof Button) {
					Button button = (Button) child;
					// Partbutton
					if (button.getData(BUTTON_ID) != null) {
						// close button
						button.setVisible(false);
						button.dispose();
					} else {
						// Part button
						button.setData(WidgetUtil.CUSTOM_VARIANT,
								VARIANT_PART_INACTIVE);
						FormData fdButton = (FormData) button.getLayoutData();
						fdButton.top = new FormAttachment(0, 0);
					}
				} else if (child instanceof Composite) {
					// Corner
					Composite corner = (Composite) child;
					corner.setVisible(true);
					String sepConst = LayoutSetConstants.STACK_TAB_INACTIVE_SEPARATOR_ACTIVE;
					Image cornerImage = stackBuilder.getImage(sepConst);
					corner.setBackgroundImage(cornerImage);
					FormData fdCorner = (FormData) corner.getLayoutData();
					fdCorner.width = cornerImage.getBounds().width;
					fdCorner.height = cornerImage.getBounds().height;
					fdCorner.top = new FormAttachment(0, 6);
				}
			}
			buttonArea.getParent().layout();
		}

	}

	/*
	 * check if the tabBg exists. If not it will create it.
	 */
	@SuppressWarnings("deprecation")
	private void checkTabBg() {
		Composite tabBar = getTabBar();
		if (tabBg == null && tabBar != null) {
			tabBg = new Composite(tabBar, SWT.NONE);
			tabBg.setData(WidgetUtil.CUSTOM_VARIANT, "compTrans"); //$NON-NLS-1$
			FormData fdTabBg = new FormData();
			tabBg.setLayoutData(fdTabBg);
			fdTabBg.left = new FormAttachment(0);
			fdTabBg.top = new FormAttachment(0);
			fdTabBg.bottom = new FormAttachment(100);
			createConfArea(fdTabBg);

			FormData fdLayout = stackBuilder
					.getPosition(LayoutSetConstants.STACK_TABBG_POS);
			RowLayout layout = new RowLayout(SWT.HORIZONTAL);
			layout.spacing = 0;
			layout.marginBottom = 0;
			if (!isStandalone()) {
				layout.marginHeight = 0;
				layout.marginLeft = fdLayout.width;
			} else {
				layout.marginHeight = 4;
				layout.marginLeft = BUTTON_SPACING;
			}
			layout.marginRight = 16;
			layout.marginTop = fdLayout.height;
			layout.marginWidth = 0;
			layout.wrap = false;
			tabBg.setLayout(layout);
			// calculate overflow
			presentationControl.addControlListener(new ControlAdapter() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 5576373970930725957L;

				public void controlResized(final ControlEvent e) {
					manageOverflow();
				};
			});
		}
	}

	private void manageOverflow() {
		int tabChildrenSize = getTabChildrenSize();
		if (tabChildrenSize > tabBg.getBounds().width
				&& moreThanOneChildVisible()) {
			hideLastVisibleButton();
			manageOverflow();
		} else {
			showLastChildIfNecessary(0);
		}
		handleOverflowButton();
	}

	private boolean moreThanOneChildVisible() {
		boolean result = false;
		Control[] children = tabBg.getChildren();
		int visibleChilds = 0;
		for (int i = 0; i < children.length && !result; i++) {
			if (children[i].isVisible()) {
				visibleChilds++;
				if (visibleChilds > 1) {
					result = true;
				}
			}
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	private void handleOverflowButton() {
		if (overflowButton == null) {
			overflowButton = new Button(tabBg.getParent(), SWT.PUSH);
			String stackOverflowPosition = LayoutSetConstants.STACK_OVERFLOW_POSITION;
			FormData fdOverflowButton = stackBuilder
					.getPosition(stackOverflowPosition);
			overflowButton.setLayoutData(fdOverflowButton);
			String stackTabOverflowActive = LayoutSetConstants.STACK_TAB_OVERFLOW_ACTIVE;
			Image icon = stackBuilder.getImage(stackTabOverflowActive);
			fdOverflowButton.height = icon.getBounds().height;
			fdOverflowButton.width = icon.getBounds().width;
			String variant = "tabOverflowInactive"; //$NON-NLS-1$
			if (state == AS_ACTIVE_FOCUS) {
				variant = "tabOverflowActive"; //$NON-NLS-1$
			}
			overflowButton.setData(WidgetUtil.CUSTOM_VARIANT, variant);
			overflowButton.moveAbove(tabBg);
			overflowButton.addSelectionListener(new SelectionAdapter() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 2310748321926511870L;

				public void widgetSelected(final SelectionEvent e) {
					performOverflow();
				};
			});
		}
		if (tabBgHasInvisibleButtons()) {
			overflowButton.setVisible(true);
		} else {
			overflowButton.setVisible(false);
		}
	}

	private void performOverflow() {
		activatePart(currentPart);
		Menu overflowMenu = new Menu(overflowButton);
		for (int i = 0; i < overflowButtons.size(); i++) {
			final IPresentablePart part = buttonPartMap.get(overflowButtons
					.get(i));
			MenuItem item = new MenuItem(overflowMenu, SWT.PUSH);
			item.setText(part.getName());
			item.addSelectionListener(new SelectionAdapter() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 799464307276740925L;

				public void widgetSelected(final SelectionEvent e) {
					activatePart(part);
					showPartButton(part);
				};
			});
		}
		// show popup
		overflowButton.setMenu(overflowMenu);
		overflowMenu.setVisible(true);
		Display display = overflowButton.getDisplay();
		Point newLocation = display.map(overflowButton, null, 0, 10);
		overflowMenu.setLocation(newLocation);
	}

	private void showPartButton(final IPresentablePart part) {
		Control button = partButtonMap.get(part);
		Control hiddenButton = hideLastVisibleButton();
		if (button != null && !button.isDisposed()) {
			button.setVisible(true);
			overflowButtons.remove(button);
			button.moveAbove(hiddenButton);
			tabBg.layout(true, true);
			manageOverflow();
		}
	}

	private void showLastChildIfNecessary(final int recursionCount) {
		Control childToShow = getLastInvisibleButton();
		if (childToShow != null
				&& futureTabChildrenSize(childToShow) < tabBg.getBounds().width
				&& tabBgHasInvisibleButtons()) {
			childToShow.setVisible(true);
			IPresentablePart part = buttonPartMap.get(childToShow);
			makePartButtonInactive(part);
			overflowButtons.remove(childToShow);
			tabBg.layout(true, true);
			if (recursionCount <= tabBg.getChildren().length) {
				int newCount = recursionCount + 1;
				showLastChildIfNecessary(newCount);
			}
		}
	}

	private boolean tabBgHasInvisibleButtons() {
		boolean result = false;
		Control[] children = tabBg.getChildren();
		for (int i = 0; i < children.length && !result; i++) {
			if (!children[i].isVisible()) {
				result = true;
			}
		}
		return result;
	}

	private int futureTabChildrenSize(final Control childToShow) {
		int result = 0;
		result = getTabChildrenSize();
		result += childToShow.getBounds().width;
		return result;
	}

	private Control getLastInvisibleButton() {
		Control result = null;
		Control[] children = tabBg.getChildren();
		boolean childShowedUp = false;
		for (int i = children.length - 1; i >= 0 && !childShowedUp; i--) {
			if (children[i].isVisible()) {
				if (children.length >= (i + 2)) {
					result = children[i + 1];
				} else {
					result = children[i];
				}
				childShowedUp = true;
			}
		}
		return result;
	}

	/*
	 * Returns the control which was hide.
	 */
	private Control hideLastVisibleButton() {
		Control result = null;
		if (tabBg != null && !tabBg.isDisposed()) {
			Control[] children = tabBg.getChildren();
			boolean lastChildHidden = false;
			for (int i = children.length - 1; i >= 0 && !lastChildHidden; i--) {
				if (children[i].isVisible()) {
					if (isButtonActive(children[i])) {
						if (i > 0) {
							children[i - 1].setVisible(false);
							result = children[i - 1];
							overflowButtons.add(children[i - 1]);
							children[i].moveAbove(children[i - 1]);
						}
					} else {
						children[i].setVisible(false);
						result = children[i];
						overflowButtons.add(children[i]);
					}
					lastChildHidden = true;

					tabBg.layout(true, true);

				}
			}
		}
		return result;
	}

	private boolean isButtonActive(final Control control) {
		boolean result = false;
		// check against the button variant
		if (control instanceof Composite) {
			Composite buttonArea = (Composite) control;
			Control[] children = buttonArea.getChildren();
			for (int i = 0; i < children.length && !result; i++) {
				if (children[i] instanceof Button) {
					@SuppressWarnings("deprecation")
					Object data = children[i]
							.getData(WidgetUtil.CUSTOM_VARIANT);
					if (data.equals(VARIANT_PART_INACTIVE_ACTIVE)
							|| data.equals(VARIANT_PART_ACTIVE)) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	private int getTabChildrenSize() {
		int result = 0;
		Control[] children = tabBg.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i].isVisible() && !children[i].isDisposed()) {
				result += (children[i].getSize().x + BUTTON_SPACING);
			}
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	private void createConfArea(final FormData fdTabBg) {
		final ConfigurationAction configAction = getConfigAction();

		if (configAction != null) {
			confArea = new Composite(getTabBar(), SWT.NONE);
			Image confBg = stackBuilder
					.getImage(LayoutSetConstants.STACK_CONF_BG_INACTIVE);
			confArea.setBackgroundImage(confBg);
			confArea.setLayout(new FormLayout());
			confArea.setBackgroundMode(SWT.INHERIT_FORCE);
			FormData fdConfArea = new FormData();
			confArea.setLayoutData(fdConfArea);
			fdConfArea.top = new FormAttachment(0);
			fdConfArea.bottom = new FormAttachment(100);
			fdConfArea.right = new FormAttachment(100);
			fdConfArea.width = 28;
			fdTabBg.right = new FormAttachment(confArea);

			confCorner = new Label(confArea, SWT.NONE);
			Image cornerImage = stackBuilder
					.getImage(LayoutSetConstants.STACK_INACTIVE_CORNER);
			confCorner.setImage(cornerImage);
			FormData fdCorner = new FormData();
			confCorner.setLayoutData(fdCorner);
			fdCorner.left = new FormAttachment(0);
			fdCorner.top = new FormAttachment(0);
			fdCorner.bottom = new FormAttachment(100);

			confButton = new Button(confArea, SWT.PUSH);
			Image confImage = stackBuilder
					.getImage(LayoutSetConstants.STACK_CONF_INACTIVE);
			confButton.setImage(confImage);
			confButton.setData(WidgetUtil.CUSTOM_VARIANT, "clearButton"); //$NON-NLS-1$
			FormData fdConfButton = stackBuilder
					.getPosition(LayoutSetConstants.STACK_CONF_POSITION);
			confButton.setLayoutData(fdConfButton);
			FormData fdConfPos = stackBuilder
					.getPosition(LayoutSetConstants.STACK_CONF_POS);
			fdConfButton.right = fdConfPos.right;
			confButton.addSelectionListener(new SelectionAdapter() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -9060515185384736476L;

				public void widgetSelected(SelectionEvent e) {
					activatePart(getSite().getSelectedPart());
					configAction.run();
				};
			});
		} else {
			// make tabarea full width if no confarea exist.
			fdTabBg.right = new FormAttachment(100);
		}
	}

	public void dispose() {
		ViewToolBarRegistry registry = ViewToolBarRegistry.getInstance();
		registry.removeViewPartPresentation(this);
		if (toolBarLayer != null) {
			toolBarLayer.dispose();
		}
		presentationControl.dispose();
	}

	public Control getControl() {
		return presentationControl;
	}

	public Control[] getTabList(final IPresentablePart part) {
		ArrayList<Control> list = new ArrayList<Control>();
		if (getControl() != null) {
			list.add(getControl());
		}
		if (part.getToolBar() != null) {
			list.add(part.getToolBar());
		}
		if (part.getControl() != null) {
			list.add(part.getControl());
		}
		return (Control[]) list.toArray(new Control[list.size()]);
	}

	public void removePart(final IPresentablePart oldPart) {
		Control object = partButtonMap.get(oldPart);
		buttonPartMap.remove(object);
		if (toolBarLayer != null) {
			toolBarLayer.setVisible(false);
		}
		// remove the dirtyListener
		IPropertyListener listener = dirtyListenerMap.get(oldPart);
		if (listener != null) {
			oldPart.removePropertyListener(listener);
		}
		partButtonMap.remove(oldPart);
		buttonList.remove(object);
		((Composite) object).dispose();
		partList.remove(oldPart);
		oldPart.setVisible(false);
		tabBg.layout();
	}

	public void selectPart(final IPresentablePart toSelect) {
		if (toSelect != null) {
			toSelect.setVisible(true);
		}
		if (currentPart != null) {
			oldPart = currentPart;
			if (currentPart instanceof PresentablePart
					&& ((PresentablePart) currentPart).getPane() != null) {
				currentPart.setVisible(false);
			}
		}
		makePartButtonInactive(currentPart);
		currentPart = toSelect;
		if (currentPart != null)
			currentPart.getControl().moveAbove(null);
		makePartButtonActive(currentPart);
		setBounds(presentationControl.getBounds());
		if (toolBarLayer != null) {
			toolBarLayer.setVisible(false);
		}
	}

	public StackDropResult dragOver(final Control currentControl,
			final Point location) {
		return null;
	}

	@SuppressWarnings("deprecation")
	public void setActive(final int newState) {
		state = newState;
		Image confBg = null;
		Image cornerImage = null;
		Image confImage = null;
		Image tabBgImage = null;
		String tabOverflow = "tabOverflowInactive"; //$NON-NLS-1$
		// create the necessary images
		if (newState == AS_ACTIVE_FOCUS) {
			if (!isStandalone()) {
				changeSelectedActiveButton(true);
			}
			confBg = stackBuilder
					.getImage(LayoutSetConstants.STACK_CONF_BG_ACTIVE);
			String rightActive = LayoutSetConstants.STACK_TAB_INACTIVE_RIGHT_ACTIVE;
			cornerImage = stackBuilder.getImage(rightActive);
			confImage = stackBuilder
					.getImage(LayoutSetConstants.STACK_CONF_ACTIVE);
			tabOverflow = "tabOverflowActive"; //$NON-NLS-1$
			tabBgImage = stackBuilder
					.getImage(LayoutSetConstants.STACK_TAB_BG_ACTIVE);
			changeStack(true);
		} else {
			if (!isStandalone()) {
				changeSelectedActiveButton(false);
			}
			confBg = stackBuilder
					.getImage(LayoutSetConstants.STACK_CONF_BG_INACTIVE);
			cornerImage = stackBuilder
					.getImage(LayoutSetConstants.STACK_INACTIVE_CORNER);
			confImage = stackBuilder
					.getImage(LayoutSetConstants.STACK_CONF_INACTIVE);
			String stackTabInactiveBgActive = LayoutSetConstants.STACK_TAB_INACTIVE_BG_ACTIVE;
			tabBgImage = stackBuilder.getImage(stackTabInactiveBgActive);
			changeStack(false);
		}

		// set the images
		if (tabBg != null) {
			tabBg.getParent().setBackgroundImage(tabBgImage);
		}
		if (confArea != null) {
			confArea.setBackgroundImage(confBg);
			if (confCorner != null) {
				confCorner.setImage(cornerImage);
			}
			if (confButton != null) {
				confButton.setImage(confImage);
			}
			confArea.getParent().layout(true);
			if (currentPart != null && getPartPane(currentPart) != null) {
				currentPart.setVisible(true);
			}
			confArea.layout(true);
		}
		if (overflowButton != null) {
			overflowButton.setData(WidgetUtil.CUSTOM_VARIANT, tabOverflow);
		}
		setBounds(presentationControl.getBounds());
	}

	private void changeStack(boolean active) {
		Object adapter = stackBuilder.getAdapter(Map.class);
		if (adapter != null && adapter instanceof Map) {
			Map<?, ?> labelMap = (Map<?, ?>) adapter;
			Label leftLabel = (Label) labelMap
					.get(StackPresentationBuider.LEFT);
			Label rightLabel = (Label) labelMap
					.get(StackPresentationBuider.RIGHT);
			Label leftBorder = (Label) labelMap
					.get(StackPresentationBuider.LEFT_BORDER);
			Label rightBorder = (Label) labelMap
					.get(StackPresentationBuider.RIGHT_BORDER);
			Label bottomBorder = (Label) labelMap
					.get(StackPresentationBuider.BOTTOM_BORDER);
			Label topBorder = (Label) labelMap
					.get(StackPresentationBuider.TOP_BORDER);
			Image left;
			Image right;
			Image leftBorderImg;
			Image rightBorderImg;
			Image bottomBorderImg;
			Image topBorderImg;
			if (active) {
				String leftActive = LayoutSetConstants.STACK_TABBAR_LEFT_ACTIVE;
				left = stackBuilder.getImage(leftActive);
				String rightActive = LayoutSetConstants.STACK_TABBAR_RIGHT_ACTIVE;
				right = stackBuilder.getImage(rightActive);
				String bottomActive = LayoutSetConstants.STACK_BORDER_BOTTOM_ACTIVE;
				bottomBorderImg = stackBuilder.getImage(bottomActive);
				String leftBorderActive = LayoutSetConstants.STACK_BORDER_LEFT_ACTIVE;
				leftBorderImg = stackBuilder.getImage(leftBorderActive);
				String rightBorderActive = LayoutSetConstants.STACK_BORDER_RIGHT_AVTIVE;
				rightBorderImg = stackBuilder.getImage(rightBorderActive);
				String stackTopStandaloneActive = LayoutSetConstants.STACK_TOP_STANDALONE_ACTIVE;
				topBorderImg = stackBuilder.getImage(stackTopStandaloneActive);
			} else {
				String leftInactive = LayoutSetConstants.STACK_TABBAR_LEFT_INACTIVE;
				left = stackBuilder.getImage(leftInactive);
				String rightInactive = LayoutSetConstants.STACK_TABBAR_RIGHT_INACTIVE;
				right = stackBuilder.getImage(rightInactive);
				bottomBorderImg = stackBuilder
						.getImage(LayoutSetConstants.STACK_BORDER_BOTTOM);
				leftBorderImg = stackBuilder
						.getImage(LayoutSetConstants.STACK_BORDER_LEFT);
				rightBorderImg = stackBuilder
						.getImage(LayoutSetConstants.STACK_BORDER_RIGHT);
				String stackTopStandaloneInactive = LayoutSetConstants.STACK_TOP_STANDALONE_INACTIVE;
				topBorderImg = stackBuilder
						.getImage(stackTopStandaloneInactive);
			}
			leftLabel.setImage(left);
			rightLabel.setImage(right);
			leftBorder.setBackgroundImage(leftBorderImg);
			rightBorder.setBackgroundImage(rightBorderImg);
			bottomBorder.setBackgroundImage(bottomBorderImg);
			// top image for standalone view
			if (isStandalone() && topBorderImg != null) {
				topBorder.setBackgroundImage(topBorderImg);
				int height = topBorderImg.getBounds().height;
				FormData fdTopBorder = (FormData) topBorder.getLayoutData();
				fdTopBorder.height = height;
				fdTopBorder.top = new FormAttachment(0, 7);
				FormData fdLeftBorder = (FormData) leftBorder.getLayoutData();
				FormData fdRightBorder = (FormData) rightBorder.getLayoutData();
				fdLeftBorder.top = new FormAttachment(0, height + 6);
				fdRightBorder.top = new FormAttachment(0, height + 6);
				topBorder.getParent().layout(true);
				topBorder.moveAbove(null);
			}
		}
	}

	private PartPane getPartPane(IPresentablePart part) {
		PartPane result = null;
		if (part instanceof PresentablePart) {
			result = ((PresentablePart) part).getPane();
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	private void changeSelectedActiveButton(final boolean selected) {
		String close = ""; //$NON-NLS-1$
		Color buttonAreaBg;
		String font = ""; //$NON-NLS-1$
		String tab = ""; //$NON-NLS-1$
		if (selected) {
			buttonAreaBg = stackBuilder
					.getColor(LayoutSetConstants.STACK_BUTTON_ACTIVE);
			close = "viewClose"; //$NON-NLS-1$
			font = "partInActiveActive"; //$NON-NLS-1$
			tab = "tabActive"; //$NON-NLS-1$
		} else {
			buttonAreaBg = stackBuilder
					.getColor(LayoutSetConstants.STACK_BUTTON_INACTIVE);
			close = "viewCloseInactive"; //$NON-NLS-1$
			font = "partActive"; //$NON-NLS-1$
			tab = "tabInactive"; //$NON-NLS-1$
		}
		Control object = partButtonMap.get(currentPart);
		if (object != null && object instanceof Composite) {
			Composite buttonArea = (Composite) object;
			buttonArea.setData(WidgetUtil.CUSTOM_VARIANT, tab);
			buttonArea.setBackground(buttonAreaBg);
			Control[] children = buttonArea.getChildren();
			for (int i = 0; i < children.length; i++) {
				Control child = children[i];
				if (child instanceof Button) {
					Button button = (Button) child;
					if (button.getData(BUTTON_ID) != null) {
						button.setData(WidgetUtil.CUSTOM_VARIANT, close);
					} else {
						button.setData(WidgetUtil.CUSTOM_VARIANT, font);
					}
				}
			}
		}
	}

	public void setBounds(final Rectangle bounds) {
		presentationControl.setBounds(bounds);
		Composite tabBar = getTabBar();
		if (currentPart != null && tabBar != null
				&& getPartPane(currentPart) != null) {
			int newHeight = bounds.height - 16;
			int partBoundsY = bounds.y + 8;
			if (getTabBar().isVisible()) {
				newHeight -= (tabBar.getBounds().height);
				partBoundsY += tabBar.getBounds().height;
			}

			Control toolBar = currentPart.getToolBar();
			if (toolbarBg != null && (toolbarBg.isVisible() || toolBar != null)) {
				int toolbarHeight = toolbarBg.getBounds().height;
				newHeight -= toolbarHeight;
				partBoundsY += toolbarHeight;
			}
			String stackTopStandaloneActive = LayoutSetConstants.STACK_TOP_STANDALONE_ACTIVE;
			Image stackTop = stackBuilder.getImage(stackTopStandaloneActive);
			if (stackTop != null) {
				partBoundsY += 1;
				newHeight -= 1;
			}
			Rectangle partBounds = new Rectangle(bounds.x + 8, partBoundsY,
					bounds.width - 16, newHeight);
			currentPart.setBounds(partBounds);
		}
		layoutToolBar();
	}

	@SuppressWarnings("deprecation")
	private Shell getToolBarLayer() {
		if (toolBarLayer == null && toolbarBg != null) {
			toolBarLayer = new Shell(toolbarBg.getShell(), SWT.NO_TRIM);
			toolBarLayer.setData(WidgetUtil.CUSTOM_VARIANT, "toolbarLayer"); //$NON-NLS-1$
			toolBarLayer.setAlpha(200);
			toolBarLayer.addListener(SWT.MouseDown, new Listener() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -2560553663509404538L;

				public void handleEvent(final Event event) {
					activatePart(currentPart);
				}
			});
		}
		return toolBarLayer;
	}

	private Composite getTabBar() {
		Composite result = null;
		Object adapter = stackBuilder.getAdapter(this.getClass());
		if (adapter != null && adapter instanceof Composite) {
			result = (Composite) adapter;
		}
		return result;
	}

	public void setState(final int state) {
		//
	}

	public void setVisible(final boolean isVisible) {
		if (currentPart != null && getPartPane(currentPart) != null) {
			currentPart.setVisible(isVisible);
			// Toolbar Layer
			deactivated = !isVisible;

			layoutToolBar();
			if (toolBarLayer != null) {
				if (!isVisible) {
					toolBarLayer.setVisible(false);
				}

			}
			setBounds(presentationControl.getBounds());
		}
	}

	public void showPaneMenu() {
		//
	}

	public void showSystemMenu() {
		//
	}

	public int computePreferredSize(final boolean width,
			final int availableParallel, final int availablePerpendicular,
			final int preferredResult) {
		int result = preferredResult;
		if (width) {
			// preferred width
			int minWidth = calculateMinimumWidth();
			if (getSite().getState() == IStackPresentationSite.STATE_MINIMIZED
					|| preferredResult < minWidth) {
				result = minWidth;
			}
		} else {
			// preferred height
			result = calculateMinimumHeight();
		}
		return result;
	}

	/*
	 * Returns the height of the tabbar plus a spacing
	 */
	private int calculateMinimumHeight() {
		int result = 0;
		if (tabBg != null) {
			tabBg.pack();
			result = tabBg.getSize().y;
		}
		return result + HEIGHT_SPACING;
	}

	/*
	 * Calculates the width of the biggest child
	 */
	private int calculateMinimumWidth() {
		int result = 0;
		if (tabBg != null) {
			Control[] children = tabBg.getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i].getSize().x >= result) {
					result = children[i].getSize().x;
				}
			}
		}
		return result + WIDTH_SPACING;
	}
}
