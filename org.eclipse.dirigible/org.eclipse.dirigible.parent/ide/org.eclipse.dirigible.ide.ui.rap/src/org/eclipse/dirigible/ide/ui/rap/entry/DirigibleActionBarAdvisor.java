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

package org.eclipse.dirigible.ide.ui.rap.entry;

import java.net.URL;
import java.util.Dictionary;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;

public class DirigibleActionBarAdvisor extends ActionBarAdvisor {

	private static final String HELP = Messages.DirigibleActionBarAdvisor_HELP;

	private static final String WINDOW = Messages.DirigibleActionBarAdvisor_WINDOW;

	private static final String RUNNING_ON_RAP_VERSION = Messages.DirigibleActionBarAdvisor_RUNNING_ON_RAP_VERSION;

	private static final String WORKBENCH = Messages.DirigibleActionBarAdvisor_WORKBENCH;

	private static final String HTTP_DIRIGIBLE_HOME = "http://www.dirigible.io"; //$NON-NLS-1$
	
	private static final String HTTP_DIRIGIBLE_HELP = "http://help.dirigible.io"; //$NON-NLS-1$
	
	private static final String HTTP_DIRIGIBLE_SAMPLES = "http://samples.dirigible.io"; //$NON-NLS-1$
	
	private static final String HTTP_DIRIGIBLE_FORUM = "http://forum.dirigible.io"; //$NON-NLS-1$
	
	private static final String HTTP_DIRIGIBLE_BUG = "https://bugs.eclipse.org/bugs/enter_bug.cgi?product=Dirigible"; //$NON-NLS-1$

	private static final String ABOUT = Messages.DirigibleActionBarAdvisor_ABOUT;

	private static final String DIRIGIBLE_HOME = Messages.DirigibleActionBarAdvisor_DIRIGIBLE_HOME;
	
	private static final String DIRIGIBLE_HELP = Messages.DirigibleActionBarAdvisor_DIRIGIBLE_HELP;
	
	private static final String DIRIGIBLE_SAMPLES = Messages.DirigibleActionBarAdvisor_DIRIGIBLE_SAMPLES;
	
	private static final String DIRIGIBLE_FORUM = Messages.DirigibleActionBarAdvisor_DIRIGIBLE_FORUM;
	
	private static final String DIRIGIBLE_BUG = Messages.DirigibleActionBarAdvisor_DIRIGIBLE_BUG;

	private static final String SHOW_VIEW = Messages.DirigibleActionBarAdvisor_SHOW_VIEW;

	private static final String SHOW_PERSPECTIVE = Messages.DirigibleActionBarAdvisor_SHOW_PERSPECTIVE;

	private static final Logger logger = Logger
			.getLogger(DirigibleActionBarAdvisor.class);

	private static final String WEB_PAGE_ERROR = Messages.DirigibleActionBarAdvisor_WEB_PAGE_ERROR;
	private static final String COULD_NOT_OPEN_WEB_PAGE = Messages.DirigibleActionBarAdvisor_COULD_NOT_OPEN_WEB_PAGE;

	private IWorkbenchAction newAction;
	private IWorkbenchAction saveAction;
	private IWorkbenchAction saveAllAction;
	private IWorkbenchAction importAction;
	private IWorkbenchAction exportAction;
	private IWorkbenchAction exitAction;

	private IWorkbenchAction undoAction;
	private IWorkbenchAction redoAction;

	private Action dirigibleHomeAction;
	private Action dirigibleHelpAction;
	private Action dirigibleSamplesAction;
	private Action dirigibleForumAction;
	private Action dirigibleBugAction;
	private Action aboutAction;
	private MenuManager showPerspectiveMenuMgr;
	private MenuManager showViewMenuMgr;
	private IWorkbenchAction preferencesAction;

	// private Action cheatSheetsAction;

	public DirigibleActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		newAction = ActionFactory.NEW.create(window);
		register(newAction);

		saveAction = ActionFactory.SAVE.create(window);
		register(saveAction);

		saveAllAction = ActionFactory.SAVE_ALL.create(window);
		register(saveAllAction);

		importAction = ActionFactory.IMPORT.create(window);
		register(importAction);

		exportAction = ActionFactory.EXPORT.create(window);
		register(exportAction);

		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);

		undoAction = ActionFactory.UNDO.create(window);
		register(undoAction);

		redoAction = ActionFactory.REDO.create(window);
		register(redoAction);

		showPerspectiveMenuMgr = new MenuManager(SHOW_PERSPECTIVE,
				"showPerspective"); //$NON-NLS-1$

		showViewMenuMgr = new MenuManager(SHOW_VIEW, "showView"); //$NON-NLS-1$
		IContributionItem showViewMenu = ContributionItemFactory.VIEWS_SHORTLIST
				.create(window);
		showViewMenuMgr.add(showViewMenu);

		preferencesAction = ActionFactory.PREFERENCES.create(window);
		register(preferencesAction);

		// helpAction = ActionFactory.HELP_CONTENTS.create(window);
		// register(helpAction);
		//
		// cheatSheetsAction = new Open Action() {
		// public void run() {
		// onWebPageAction(window.getWorkbench());
		// }
		// };
		// cheatSheetsAction.setText("Cheat Sheets");
		// cheatSheetsAction.setId("org.eclipse.ui.cheatsheets.openCheatSheet");
		// register(cheatSheetsAction);

		// Home
		dirigibleHomeAction = new Action() {
			private static final long serialVersionUID = 7112545561507879756L;
			
			public void run() {
				onWebPageAction(window.getWorkbench(), dirigibleHomeAction, HTTP_DIRIGIBLE_HOME);
			}
		};
		dirigibleHomeAction.setText(DIRIGIBLE_HOME);
		dirigibleHomeAction.setId("org.eclipse.dirigible.ide.home"); //$NON-NLS-1$
		register(dirigibleHomeAction);
		
		// Help
		dirigibleHelpAction = new Action() {
			private static final long serialVersionUID = 7112545561507879756L;
			
			public void run() {
				onWebPageAction(window.getWorkbench(), dirigibleHelpAction, HTTP_DIRIGIBLE_HELP);
			}
		};
		dirigibleHelpAction.setText(DIRIGIBLE_HELP);
		dirigibleHelpAction.setId("org.eclipse.dirigible.ide.help"); //$NON-NLS-1$
		register(dirigibleHelpAction);
		
		// Samples
		dirigibleSamplesAction = new Action() {
			private static final long serialVersionUID = 7112545561507879756L;
			
			public void run() {
				onWebPageAction(window.getWorkbench(), dirigibleSamplesAction, HTTP_DIRIGIBLE_SAMPLES);
			}
		};
		dirigibleSamplesAction.setText(DIRIGIBLE_SAMPLES);
		dirigibleSamplesAction.setId("org.eclipse.dirigible.ide.samples"); //$NON-NLS-1$
		register(dirigibleSamplesAction);
		
		// Forum
		dirigibleForumAction = new Action() {
			private static final long serialVersionUID = 7112545561507879756L;
			
			public void run() {
				onWebPageAction(window.getWorkbench(), dirigibleForumAction, HTTP_DIRIGIBLE_FORUM);
			}
		};
		dirigibleForumAction.setText(DIRIGIBLE_FORUM);
		dirigibleForumAction.setId("org.eclipse.dirigible.ide.forum"); //$NON-NLS-1$
		register(dirigibleForumAction);
		
		// Bug
		dirigibleBugAction = new Action() {
			private static final long serialVersionUID = 7112545561507879756L;
			
			public void run() {
				onWebPageAction(window.getWorkbench(), dirigibleBugAction, HTTP_DIRIGIBLE_BUG);
			}
		};
		dirigibleBugAction.setText(DIRIGIBLE_BUG);
		dirigibleBugAction.setId("org.eclipse.dirigible.ide.bug"); //$NON-NLS-1$
		register(dirigibleBugAction);
		
		// About
		aboutAction = new Action() {
			private static final long serialVersionUID = 8477239924815783883L;

			public void run() {
				onAboutAction(window.getShell());
			}
		};
		aboutAction.setText(ABOUT);
		aboutAction.setId("org.eclipse.dirigible.ide.about"); //$NON-NLS-1$
		register(aboutAction);
	}

	private void onWebPageAction(IWorkbench workbench, Action action, String url) {
		IWorkbenchBrowserSupport browserSupport = workbench.getBrowserSupport();
		int style = IWorkbenchBrowserSupport.AS_EXTERNAL;
		try {
			IWebBrowser browser = browserSupport.createBrowser(style,
					action.getId(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$
			browser.openURL(new URL(url));
		} catch (Exception e) {
			logger.error(COULD_NOT_OPEN_WEB_PAGE, e);
			MessageDialog.openError(null, WEB_PAGE_ERROR,
					COULD_NOT_OPEN_WEB_PAGE);
		}
	}

	private void onAboutAction(Shell shell) {
		Bundle bundle = Platform.getBundle(PlatformUI.PLUGIN_ID);
		Dictionary<?, ?> headers = bundle.getHeaders();
		Object version = headers.get(Constants.BUNDLE_VERSION);
		MessageDialog.openInformation(shell,
				ICommonConstants.DIRIGIBLE_PRODUCT_NAME + WORKBENCH,
				ICommonConstants.DIRIGIBLE_PRODUCT_NAME + " " //$NON-NLS-1$
						+ ICommonConstants.DIRIGIBLE_PRODUCT_VERSION + "\n" //$NON-NLS-1$
						+ RUNNING_ON_RAP_VERSION + version + "\n"
						+ "under Eclipse Public License v1.0");

	}

	protected void fillMenuBar(IMenuManager menuBar) {
		// MenuManager newMenu = new MenuManager("New", "id.file");
		// newMenu.add(newAction);

		// File menu
		MenuManager fileMenu = new MenuManager(Messages.DirigibleActionBarAdvisor_FILE, "id.file"); //$NON-NLS-2$
		// Other plug-ins can contribute there actions here
		fileMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		// fileMenu.add(newMenu);
		fileMenu.add(new Separator());
		fileMenu.add(saveAction);
		fileMenu.add(saveAllAction);
		// fileMenu.add(new Separator());
		// fileMenu.add(importAction);
		// fileMenu.add(exportAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);
		menuBar.add(fileMenu);

		// Edit menu
		// MenuManager editMenu = new MenuManager("Edit", "id.edit");
		// editMenu.add(undoAction);
		// editMenu.add(redoAction);
		// editMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		// menuBar.add(editMenu);

		// Window menu
		MenuManager windowMenu = new MenuManager(WINDOW,
				IWorkbenchActionConstants.M_WINDOW);
		windowMenu.add(showPerspectiveMenuMgr);
		windowMenu.add(showViewMenuMgr);
		windowMenu.add(new Separator());
		windowMenu.add(preferencesAction);
		menuBar.add(windowMenu);

		// Help menu
		MenuManager helpMenu = new MenuManager(HELP,
				IWorkbenchActionConstants.M_HELP);
		helpMenu.add(dirigibleHelpAction);
		helpMenu.add(new Separator());
		helpMenu.add(dirigibleHomeAction);
		helpMenu.add(dirigibleSamplesAction);
		helpMenu.add(dirigibleForumAction);
		helpMenu.add(dirigibleBugAction);
		// helpMenu.add(helpAction);
		helpMenu.add(new Separator("about")); //$NON-NLS-1$
		helpMenu.add(aboutAction);
		menuBar.add(helpMenu);
	}

	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		toolbar.add(undoAction);
		toolbar.add(redoAction);
		toolbar.add(saveAction);
		toolbar.add(saveAllAction);
		toolbar.add(new Separator("control")); //$NON-NLS-1$
		coolBar.add(new ToolBarContributionItem(toolbar, "main")); //$NON-NLS-1$
	}

	protected void fillStatusLine(IStatusLineManager statusLine) {
//		statusLine.add(dirigibleHomeAction);
//		statusLine.add(dirigibleForumAction);
		statusLine.add(dirigibleBugAction);
//		statusLine.add(aboutAction);
	}
}
