/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.debug.ui;

import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.common.CommonIDEUtils;
import org.eclipse.dirigible.ide.debug.model.IDebugIDEController;
import org.eclipse.dirigible.ide.editor.ace.AceEditor;
import org.eclipse.dirigible.ide.editor.orion.OrionEditor;
import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
import org.eclipse.dirigible.ide.workspace.ui.commands.OpenHandler;
import org.eclipse.dirigible.ide.workspace.ui.view.WebViewerView;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.debug.BreakpointMetadata;
import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.ext.debug.DebugModelFacade;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionModel;
import org.eclipse.dirigible.repository.ext.debug.LinebreakMetadata;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class DebugView extends ViewPart implements IDebugIDEController, IPropertyListener {

	public static final String ID = "org.eclipse.dirigible.ide.debug.ui.DebugView";

	private static final String INTERNAL_ERROR_DEBUG_BRIDGE_IS_NOT_PRESENT = "Internal error - DebugBridge is not present";
	private static final String DEBUG_PROCESS_TITLE = "Debug Process";
	private static final String FILE = Messages.DebugView_FILE;
	private static final String SLASH = "/"; //$NON-NLS-1$
	private static final String SCRIPTING_SERVICES = "/ScriptingServices"; //$NON-NLS-1$
	private static final String SOURCE = Messages.DebugView_SOURCE;
	private static final String ROW = Messages.DebugView_ROW;
	private static final String VALUES = Messages.DebugView_VALUES;
	private static final String VARIABLES = Messages.DebugView_VARIABLES;
	private static final String SESSIONS = Messages.DebugView_SESSIONS;
	private static final String SKIP_BREAKPOINTS = Messages.DebugView_SKIP_BREAKPOINTS;
	private static final String CONTINUE = Messages.DebugView_CONTINUE;
	private static final String STEP_OVER = Messages.DebugView_STEP_OVER;
	private static final String STEP_INTO = Messages.DebugView_STEP_INTO;
	private static final String REFRESH = Messages.DebugView_REFRESH;

	private static final URL DIRIGIBLE_TERMINATE_ICON_URL = DebugView.class.getResource("/resources/terminate.png"); //$NON-NLS-1$
	private static final URL DIRIGIBLE_CONTINUE_ICON_URL = DebugView.class.getResource("/resources/resume.png"); //$NON-NLS-1$
	private static final URL DIRIGIBLE_STEP_OVER_ICON_URL = DebugView.class.getResource("/resources/step-out.png"); //$NON-NLS-1$
	private static final URL DIRIGIBLE_STEP_INTO_ICON_URL = DebugView.class.getResource("/resources/step-into.png"); //$NON-NLS-1$
	private static final URL DIRIGIBLE_REFRESH_ICON_URL = DebugView.class.getResource("/resources/refresh.png"); //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(DebugView.class);

	private static final int MAX_WAITS = 20;
	private static final int SLEEP_TIME = 500;

	private final ResourceManager resourceManager;

	private TreeViewer sessionsTreeViewer;
	private SessionsViewContentProvider sessionsContentProvider;

	private TreeViewer variablesTreeViewer;
	private VariablesViewContentProvider variablesContentProvider;

	private TreeViewer breakpointsTreeViewer;
	private BreakpointViewContentProvider breakpointsContentProvider;

	private boolean sessionsMetadataRecieved = false;

	public DebugView() {
		super();
		this.resourceManager = new LocalResourceManager(JFaceResources.getResources());
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		registerPreviewListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);

		DebugModelFacade.createDebugModel(CommonIDEParameters.getUserName(), this);

		final Composite holder = new Composite(parent, SWT.NONE);
		holder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		holder.setLayout(new GridLayout(8, false));
		createButtonsRow(holder);

		SashForm sashFormSessions = new SashForm(parent, SWT.HORIZONTAL | SWT.BORDER);
		sashFormSessions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		if (isRAPView) {
			createSessionsTableRAP(sashFormSessions);
		} else {
			createSessionsTable(sashFormSessions);
		}

		SashForm sashForm = new SashForm(sashFormSessions, SWT.HORIZONTAL | SWT.BORDER);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createVariablesTable(sashForm);
		createBreakpointsTable(sashForm);

		registerPreviewListener(this);

		enableDebugButtons(true);
	}

	private boolean previewListenerRegistered = false;

	private void registerPreviewListener(final DebugView debugView) {

		if (!previewListenerRegistered) {
			IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (workbenchPage != null) {
				IViewPart viewPart = workbenchPage.findView(WebViewerView.ID);
				if (viewPart == null) {
					try {
						viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(WebViewerView.ID);
					} catch (PartInitException e) {
						logError(e.getMessage(), e);
						return;
					}
				}
				viewPart.addPropertyListener(debugView);
				previewListenerRegistered = true;
			}
		}
	};

	private DebugSessionModel refreshMetaData() {
		DebugSessionModel session = getDebugModel(CommonIDEParameters.getUserName()).getActiveSession();
		refreshAllViews();
		return session;
	}

	Button stepIntoButton;
	Button stepOverButton;
	Button continueButton;
	Button skipAllBreakpointsButton;

	private boolean isRAPView = false;

	private void createButtonsRow(final Composite holder) {

		if (isRAPView) {
			// Make sense for RAP based View only
			Button refreshButton = createButton(holder, REFRESH, DIRIGIBLE_REFRESH_ICON_URL);
			refreshButton.addSelectionListener(new SelectionAdapter() {
				private static final long serialVersionUID = 1316287800753595995L;

				@Override
				public void widgetSelected(SelectionEvent e) {
					waitForMetadata(refreshMetaData());
				}

			});
		}

		stepIntoButton = createButton(holder, STEP_INTO, DIRIGIBLE_STEP_INTO_ICON_URL);
		stepIntoButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = -2027392635482495783L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				DebugSessionModel session = getDebugModel(CommonIDEParameters.getUserName()).getActiveSession();
				if (session != null) {
					stepInto();
					waitForMetadata(session);
				}
			}

		});

		stepOverButton = createButton(holder, STEP_OVER, DIRIGIBLE_STEP_OVER_ICON_URL);
		stepOverButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 6512558201116618008L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				DebugSessionModel session = getDebugModel(CommonIDEParameters.getUserName()).getActiveSession();
				if (session != null) {
					stepOver();
					waitForMetadata(session);
				}
			}

		});

		continueButton = createButton(holder, CONTINUE, DIRIGIBLE_CONTINUE_ICON_URL);
		continueButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = -478646368834480614L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				DebugSessionModel session = getDebugModel(CommonIDEParameters.getUserName()).getActiveSession();
				if (session != null) {
					continueExecution();
					waitForMetadata(session);
				}
			}

		});

		skipAllBreakpointsButton = createButton(holder, SKIP_BREAKPOINTS, DIRIGIBLE_TERMINATE_ICON_URL);
		skipAllBreakpointsButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 5141833336402908961L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				DebugSessionModel session = getDebugModel(CommonIDEParameters.getUserName()).getActiveSession();
				if (session != null) {
					skipAllBreakpoints();
					waitForMetadata(session);
				}
			}

		});

		enableDebugButtons(false);
	}

	private Button createButton(Composite holder, String toolTipText, URL imageUrl) {
		Button button = new Button(holder, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.UP, false, false));
		button.setToolTipText(toolTipText);
		button.setImage(createImage(imageUrl));
		return button;
	}

	// RAP based View
	private void createSessionsTableRAP(final Composite holder) {
		sessionsTreeViewer = new TreeViewer(holder, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		sessionsTreeViewer.getTree().setHeaderVisible(true);

		Tree tree = sessionsTreeViewer.getTree();

		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText(SESSIONS);
		column.setWidth(150);

		sessionsContentProvider = new SessionsViewContentProvider();
		sessionsTreeViewer.setContentProvider(sessionsContentProvider);
		sessionsTreeViewer.setLabelProvider(new SessionsViewLabelProvider());
		sessionsTreeViewer.setInput(getViewSite());
		sessionsTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty() && (event.getSelection() instanceof IStructuredSelection)) {
					IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
					String sessionInfo = (String) structuredSelection.getFirstElement();
					selectedSessionTreeItem(sessionInfo);
				}
			}
		});
	}

	// WebSocket based View
	private void createSessionsTable(final Composite holder) {
		SessionsWSView sessionsWSView = new SessionsWSView(holder);
	}

	private void selectedSessionTreeItem(String sessionInfo) {
		StringTokenizer tokenizer = new StringTokenizer(sessionInfo, ICommonConstants.DEBUG_SEPARATOR);
		String userId = tokenizer.nextToken();
		String sessionId = tokenizer.nextToken();
		String executionId = tokenizer.nextToken();
		selectedDebugSession(executionId);
	}

	private void selectedDebugSession(String executionId) {
		DebugSessionModel session = getDebugModel(CommonIDEParameters.getUserName()).getSessionByExecutionId(executionId);
		getDebugModel(CommonIDEParameters.getUserName()).setActiveSession(session);
	}

	private void createVariablesTable(final Composite holder) {
		variablesTreeViewer = new TreeViewer(holder, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		variablesTreeViewer.getTree().setHeaderVisible(true);

		Tree tree = variablesTreeViewer.getTree();

		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText(VARIABLES);
		column.setWidth(150);

		column = new TreeColumn(tree, SWT.LEFT);
		column.setText(VALUES);
		column.setWidth(595);

		variablesContentProvider = new VariablesViewContentProvider(getDebugModel(CommonIDEParameters.getUserName()));
		variablesTreeViewer.setContentProvider(variablesContentProvider);
		variablesTreeViewer.setLabelProvider(new VariablesViewLabelProvider());
		variablesTreeViewer.setInput(getViewSite());
	}

	private void createBreakpointsTable(final Composite holder) {
		breakpointsTreeViewer = new TreeViewer(holder, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		breakpointsTreeViewer.getTree().setHeaderVisible(true);

		Tree tree = breakpointsTreeViewer.getTree();

		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText(FILE);
		column.setWidth(150);

		column = new TreeColumn(breakpointsTreeViewer.getTree(), SWT.LEFT);
		column.setText(ROW);
		column.setWidth(50);

		column = new TreeColumn(breakpointsTreeViewer.getTree(), SWT.LEFT);
		column.setText(SOURCE);
		column.setWidth(350);

		breakpointsContentProvider = new BreakpointViewContentProvider(getDebugModel(CommonIDEParameters.getUserName()).getBreakpointsMetadata());
		breakpointsTreeViewer.setContentProvider(breakpointsContentProvider);
		breakpointsTreeViewer.setLabelProvider(new BreakpointViewLabelProvider());
		breakpointsTreeViewer.setInput(getViewSite());
		breakpointsTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				logger.debug("DebugView -> BreakpointsView -> selectionChanged");
				if (!event.getSelection().isEmpty() && (event.getSelection() instanceof IStructuredSelection)) {
					IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
					BreakpointMetadata breakpointMetadata = (BreakpointMetadata) structuredSelection.getFirstElement();
					openEditor(CommonIDEUtils.formatToIDEPath(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES, breakpointMetadata.getFullPath()),
							breakpointMetadata.getRow());
				}
			}
		});
	}

	private void refreshAllViews() {
		try {
			breakpointsTreeViewer.refresh(true);
			variablesTreeViewer.refresh(true);

			sessionsTreeViewer.refresh(true);
			if (sessionsTreeViewer.getSelection().isEmpty()
					&& !DebugModelFacade.getDebugModel(CommonIDEParameters.getUserName()).getSessions().isEmpty()) {
				TreeItem treeItem = sessionsTreeViewer.getTree().getTopItem();
				sessionsTreeViewer.getTree().setSelection(treeItem);
				selectedSessionTreeItem(treeItem.getText());
			}

			enableDebugButtons(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private void enableDebugButtons(boolean enabled) {
		stepIntoButton.setEnabled(enabled);
		stepOverButton.setEnabled(enabled);
		continueButton.setEnabled(enabled);
		skipAllBreakpointsButton.setEnabled(enabled);
	}

	private void waitForMetadata(final DebugSessionModel session) {
		if (session == null) {
			// refresh(session);
			return;
		}
		final Display display = PlatformUI.createDisplay();
		final ServerPushSession pushSession = new ServerPushSession();
		Runnable backGroundRunnable = new Runnable() {
			boolean openEditor = false;

			@Override
			public void run() {

				int wait = 0;

				while (wait < MAX_WAITS) {
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						logError(e.getMessage(), e);
					}
					wait++;

					if (session.isUpdated()) {
						refreshAllViews();
					}

					if (session.getCurrentLineBreak() != null) {
						openEditor = true;
						wait = MAX_WAITS;
					}

					session.setUpdated(false);
				}

				// schedule the UI update
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						refreshMetaData();

						if (openEditor) {
							String path = session.getCurrentLineBreak().getBreakpoint().getFullPath();
							Integer row = session.getCurrentLineBreak().getBreakpoint().getRow();
							try {
								openEditor(CommonIDEUtils.formatToIDEPath(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES, path), row);
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
							openEditor = false;
						}
						pushSession.stop();
					}

				});
			}
		};

		pushSession.start();
		Thread backGroundThread = new Thread(backGroundRunnable);
		backGroundThread.setDaemon(true);
		backGroundThread.start();
	}

	@Override
	public void setFocus() {
		//
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	private Image createImage(URL imageURL) {
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(imageURL);
		return resourceManager.createImage(imageDescriptor);
	}

	@Override
	public void register(DebugSessionModel session) {
		sessionsMetadataRecieved = true;
		// waitForMetadata(session);
	}

	@Override
	public void finish(DebugSessionModel session) {
		session.getModel().removeSession(session);
		sessionsMetadataRecieved = true;
	}

	@Override
	public void onLineChange(LinebreakMetadata linebreak, DebugSessionModel session) {
		session.setCurrentLineBreak(linebreak);
		sessionsMetadataRecieved = true;
	}

	@Override
	public void refreshVariables() {
		sessionsMetadataRecieved = true;
	}

	@Override
	public void refreshBreakpoints() {
		sessionsMetadataRecieved = true;
	}

	public void removeSession(String sessionId, String executionId, String userId) {
		DebugModelFacade.getInstance().removeSession(userId, executionId);
	}

	public DebugModel getDebugModel(String user) {
		return DebugModelFacade.getDebugModel(user);
	}

	public DebugSessionModel getDebugSessionModel(String executionId, String userId) {
		return getDebugModel(userId).getSessionByExecutionId(executionId);
	}

	@Override
	public void refresh() {
	}

	private boolean checkDebugExecutor() {
		if (getDebugModel(CommonIDEParameters.getUserName()).getActiveSession() == null) {
			logger.error("No active debug session");
			return false;
		}
		if (getDebugModel(CommonIDEParameters.getUserName()).getActiveSession().getDebugExecutor() == null) {
			logger.error("Active debug session exists, but there is no executor assigned");
			return false;
		}
		return true;
	}

	@Override
	public void stepInto() {
		if (checkDebugExecutor()) {
			getDebugModel(CommonIDEParameters.getUserName()).getActiveSession().getDebugExecutor().stepInto();
		}
	}

	@Override
	public void stepOver() {
		if (checkDebugExecutor()) {
			getDebugModel(CommonIDEParameters.getUserName()).getActiveSession().getDebugExecutor().stepOver();
		}
	}

	@Override
	public void continueExecution() {
		if (checkDebugExecutor()) {
			getDebugModel(CommonIDEParameters.getUserName()).getActiveSession().getDebugExecutor().continueExecution();
		}
	}

	@Override
	public void skipAllBreakpoints() {
		if (checkDebugExecutor()) {
			getDebugModel(CommonIDEParameters.getUserName()).getActiveSession().getDebugExecutor().skipAllBreakpoints();
		}
	}

	@Override
	public IEditorPart openEditor(String path, int row) {
		IPath location = new Path(path);
		IWorkspace workspace = RemoteResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		if (root.exists(location)) {
			IFile file = root.getFile(location);
			return openWorkspaceFile(row, file);
		}
		IProject[] projects = root.getProjects();
		for (IProject project : projects) {
			if (project.exists(location)) {
				IFile file = project.getFile(location);
				return openWorkspaceFile(row, file);
			}
		}

		return null;
	}

	private IEditorPart openWorkspaceFile(int row, IFile file) {
		IEditorPart sourceCodeEditor = OpenHandler.open(file, row);
		if ((sourceCodeEditor != null) && (sourceCodeEditor instanceof AceEditor)) {
			((AceEditor) sourceCodeEditor).setDebugRow(row);
		} else if ((sourceCodeEditor != null) && (sourceCodeEditor instanceof OrionEditor)) {
			((OrionEditor) sourceCodeEditor).setDebugRow(row);
		}
		return sourceCodeEditor;
	}

	@Override
	public void setBreakpoint(String path, int row) {
		BreakpointMetadata breakpoint = new BreakpointMetadata(path, row);
		getDebugModel(CommonIDEParameters.getUserName()).getBreakpointsMetadata().getBreakpoints().add(breakpoint);
		refreshAllViews();
	}

	@Override
	public void clearBreakpoint(String path, int row) {
		BreakpointMetadata breakpoint = new BreakpointMetadata(path, row);
		Set<BreakpointMetadata> breakpoints = getDebugModel(CommonIDEParameters.getUserName()).getBreakpointsMetadata().getBreakpoints();
		for (Iterator iterator = breakpoints.iterator(); iterator.hasNext();) {
			BreakpointMetadata breakpointMetadata = (BreakpointMetadata) iterator.next();
			if (breakpointMetadata.equals(breakpoint)) {
				iterator.remove();
				break;
			}
		}
		refreshAllViews();
	}

	@Override
	public void clearAllBreakpoints() {
		getDebugModel(CommonIDEParameters.getUserName()).getBreakpointsMetadata().getBreakpoints().clear();
	}

	private void logError(String message, Throwable t) {
		if (logger.isErrorEnabled()) {
			logger.error(message, t);
		}
	}

	private void logDebug(String message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
	}

	private void logWarn(String message) {
		if (logger.isWarnEnabled()) {
			logger.warn(message);
		}
	}

	private void refreshSessionsView() {
		final Display display = PlatformUI.createDisplay();
		final ServerPushSession pushSession = new ServerPushSession();
		Runnable backGroundRunnable = new Runnable() {
			@Override
			public void run() {

				int wait = 0;

				while (wait < MAX_WAITS) {
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						logError(e.getMessage(), e);
					}
					wait++;

					if (sessionsMetadataRecieved) {
						sessionsMetadataRecieved = false;
						wait = MAX_WAITS;
					}
				}

				// schedule the UI update
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						refreshAllViews();
						pushSession.stop();
					}
				});
			}
		};
		pushSession.start();
		Thread backGroundThread = new Thread(backGroundRunnable);
		backGroundThread.setDaemon(true);
		backGroundThread.start();
	}

	@Override
	public void propertyChanged(Object source, int propId) {
		waitForMetadata(getDebugModel(CommonIDEParameters.getUserName()).getActiveSession());
	}

}
