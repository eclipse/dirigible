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

package org.eclipse.dirigible.ide.debug.ui;

import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.osgi.framework.ServiceReference;

import com.google.gson.Gson;
import org.eclipse.dirigible.ide.common.CommonUtils;
import org.eclipse.dirigible.ide.debug.model.DebugModel;
import org.eclipse.dirigible.ide.debug.model.DebugModelFacade;
import org.eclipse.dirigible.ide.debug.model.IDebugController;
import org.eclipse.dirigible.ide.editor.js.JavaScriptEditor;
import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
import org.eclipse.dirigible.ide.workspace.ui.commands.OpenHandler;
import org.eclipse.dirigible.ide.workspace.ui.view.WebViewerView;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.debug.BreakpointMetadata;
import org.eclipse.dirigible.repository.ext.debug.BreakpointsMetadata;
import org.eclipse.dirigible.repository.ext.debug.DebugConstants;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionMetadata;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionsMetadata;
import org.eclipse.dirigible.repository.ext.debug.IDebugProtocol;
import org.eclipse.dirigible.repository.ext.debug.VariableValuesMetadata;
import org.eclipse.dirigible.repository.logging.Logger;

public class DebugView extends ViewPart implements IDebugController, IPropertyListener {
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

	private static final URL DIRIGIBLE_TERMINATE_ICON_URL = DebugView.class
			.getResource("/resources/terminate.png"); //$NON-NLS-1$
	private static final URL DIRIGIBLE_CONTINUE_ICON_URL = DebugView.class
			.getResource("/resources/resume.png"); //$NON-NLS-1$
	private static final URL DIRIGIBLE_STEP_OVER_ICON_URL = DebugView.class
			.getResource("/resources/step-out.png"); //$NON-NLS-1$
	private static final URL DIRIGIBLE_STEP_INTO_ICON_URL = DebugView.class
			.getResource("/resources/step-into.png"); //$NON-NLS-1$
	private static final URL DIRIGIBLE_REFRESH_ICON_URL = DebugView.class
			.getResource("/resources/refresh.png"); //$NON-NLS-1$

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

	private IDebugProtocol debugProtocol;

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

		final Composite holder = new Composite(parent, SWT.NONE);
		holder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		holder.setLayout(new GridLayout(8, false));
		createButtonsRow(holder);

		SashForm sashFormSessions = new SashForm(parent, SWT.HORIZONTAL | SWT.BORDER);
		sashFormSessions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createSessionsTable(sashFormSessions);

		SashForm sashForm = new SashForm(sashFormSessions, SWT.HORIZONTAL | SWT.BORDER);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createVariablesTable(sashForm);
		createBreakpointsTable(sashForm);

		ServiceReference<IDebugProtocol> sr = DebugUIActivator.getContext().getServiceReference(IDebugProtocol.class);
		this.debugProtocol = DebugUIActivator.getContext().getService(sr);
		if (debugProtocol == null) {
			logger.error("DebuggerBridge not present");
		} else {
			this.debugProtocol.addPropertyChangeListener(this);
		}
 
//		registerPreviewListener(this);
			
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

	private DebugModel refreshMetaData() {
		refreshAllViews();
		DebugModel debugModel = DebugModelFacade.getActiveDebugModel();
		refresh(debugModel);
		return debugModel;
	}
	
	
	Button stepIntoButton;
	Button stepOverButton;
	Button continueButton;
	Button skipAllBreakpointsButton;

	private void createButtonsRow(final Composite holder) {
		Button refreshButton = createButton(holder, REFRESH, DIRIGIBLE_REFRESH_ICON_URL);
		refreshButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1316287800753595995L;

			public void widgetSelected(SelectionEvent e) {
				waitForMetadata(refreshMetaData());
			}


		});

		stepIntoButton = createButton(holder, STEP_INTO, DIRIGIBLE_STEP_INTO_ICON_URL);
		stepIntoButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = -2027392635482495783L;

			public void widgetSelected(SelectionEvent e) {
				DebugModel debugModel = DebugModelFacade.getActiveDebugModel();
				if (debugModel != null) {
					stepInto(debugModel);
					waitForMetadata(debugModel);
				}
			}

		});

		stepOverButton = createButton(holder, STEP_OVER, DIRIGIBLE_STEP_OVER_ICON_URL);
		stepOverButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 6512558201116618008L;

			public void widgetSelected(SelectionEvent e) {
				DebugModel debugModel = DebugModelFacade.getActiveDebugModel();
				if (debugModel != null) {
					stepOver(debugModel);
					waitForMetadata(debugModel);
				}
			}

		});

		continueButton = createButton(holder, CONTINUE, DIRIGIBLE_CONTINUE_ICON_URL);
		continueButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = -478646368834480614L;

			public void widgetSelected(SelectionEvent e) {
				DebugModel debugModel = DebugModelFacade.getActiveDebugModel();
				if (debugModel != null) {
					continueExecution(debugModel);
					waitForMetadata(debugModel);
				}
			}

		});

		skipAllBreakpointsButton = createButton(holder, SKIP_BREAKPOINTS,
				DIRIGIBLE_TERMINATE_ICON_URL);
		skipAllBreakpointsButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 5141833336402908961L;

			public void widgetSelected(SelectionEvent e) {
				DebugModel debugModel = DebugModelFacade.getActiveDebugModel();
				if (debugModel != null) {
					skipAllBreakpoints(debugModel);
					waitForMetadata(debugModel);
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

	private void createSessionsTable(final Composite holder) {
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
				if (!event.getSelection().isEmpty()
						&& event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) event
							.getSelection();
					String sessionInfo = (String) structuredSelection.getFirstElement();
					selectedSessionTreeItem(sessionInfo);
				}
			}
		});
	}
	
	private void selectedSessionTreeItem(String sessionInfo) {
		StringTokenizer tokenizer = new StringTokenizer(sessionInfo,
				ICommonConstants.DEBUG_SEPARATOR);
		String userId = tokenizer.nextToken();
		String sessionId = tokenizer.nextToken();
		String executionId = tokenizer.nextToken();
		selectedDebugSession(executionId);
	}
	
	private void selectedDebugSession(String executionId) {
		DebugModel debugModel = DebugModelFacade.getInstance().getDebugModel(
				executionId);
		DebugModelFacade.setActiveDebugModel(debugModel);
		refresh(debugModel);
		waitForMetadata(debugModel);
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

		variablesContentProvider = new VariablesViewContentProvider();
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

		breakpointsContentProvider = new BreakpointViewContentProvider();
		breakpointsTreeViewer.setContentProvider(breakpointsContentProvider);
		breakpointsTreeViewer.setLabelProvider(new BreakpointViewLabelProvider());
		breakpointsTreeViewer.setInput(getViewSite());
		breakpointsTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()
						&& event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) event
							.getSelection();
					BreakpointMetadata breakpointMetadata = (BreakpointMetadata) structuredSelection
							.getFirstElement();
					openEditor(CommonUtils.formatToIDEPath(
							ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES,
							breakpointMetadata.getFullPath()), breakpointMetadata.getRow());
				}
			}
		});
	}

	private void refreshAllViews() {
		breakpointsTreeViewer.refresh(true);
		variablesTreeViewer.refresh(true);
		sessionsTreeViewer.refresh(true);
		if (sessionsTreeViewer.getSelection().isEmpty()
				&& !DebugModelFacade.getDebugModels().isEmpty()) {
			TreeItem treeItem = sessionsTreeViewer.getTree().getTopItem();
			sessionsTreeViewer.getTree().setSelection(treeItem);
			selectedSessionTreeItem(treeItem.getText());
		}
		
		enableDebugButtons(!sessionsTreeViewer.getSelection().isEmpty());
	}

	private void enableDebugButtons(boolean enabled) {
		stepIntoButton.setEnabled(enabled);
		stepOverButton.setEnabled(enabled);
		continueButton.setEnabled(enabled);
		skipAllBreakpointsButton.setEnabled(enabled);
	}

	private void waitForMetadata(final DebugModel debugModel) {
		if (debugModel == null) {
			refresh(debugModel);
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
					if (debugModel.getVariableValuesMetadata() != null) {
						variablesContentProvider.setVariablesMetaData(debugModel.getVariableValuesMetadata());
						wait = MAX_WAITS;
					}
					if (debugModel.getBreakpointsMetadata() != null) {
						breakpointsContentProvider.setBreakpointMetadata(debugModel.getBreakpointsMetadata());
						wait = MAX_WAITS;
					}

					if (debugModel.getCurrentLineBreak() != null) {
						openEditor = true;
						wait = MAX_WAITS;
					}
				}

				// schedule the UI update
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						refreshMetaData();
//						refreshAllViews();

						if (openEditor) {
							String path = debugModel.getCurrentLineBreak().getFullPath();
							Integer row = debugModel.getCurrentLineBreak().getRow();
							openEditor(path, row);
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
	public void propertyChange(PropertyChangeEvent evt) {
		String commandId = evt.getPropertyName();
		String clientId = (String) evt.getOldValue();
		String commandBody = (String) evt.getNewValue();
		logDebug("DebugView.propertyChange() with commandId: " + commandId + ", clientId: "
				+ clientId + ", commandBody: " + commandBody);
		Gson gson = new Gson();

		if (commandId.startsWith(DebugConstants.VIEW)) {

			if (commandId.equals(DebugConstants.VIEW_REGISTER)) {
				DebugSessionMetadata debugSessionMetadata = gson.fromJson(commandBody,
						DebugSessionMetadata.class);
				createDebugModel(debugSessionMetadata.getSessionId(),
						debugSessionMetadata.getExecutionId(), debugSessionMetadata.getUserId());
				sessionsMetadataRecieved = true;

			} else if (commandId.equals(DebugConstants.VIEW_FINISH)) {
				DebugSessionMetadata debugSessionMetadata = gson.fromJson(commandBody,
						DebugSessionMetadata.class);
				removeDebugModel(debugSessionMetadata.getSessionId(),
						debugSessionMetadata.getExecutionId(), debugSessionMetadata.getUserId());
				sessionsMetadataRecieved = true;

			} else if (commandId.equals(DebugConstants.VIEW_SESSIONS)) {
				DebugSessionsMetadata debugSessionsMetadata = gson.fromJson(commandBody,
						DebugSessionsMetadata.class);
				reinitializeDebugModels(debugSessionsMetadata);
				sessionsMetadataRecieved = true;

			} else if (commandId.equals(DebugConstants.VIEW_VARIABLE_VALUES)) {
				VariableValuesMetadata variableValuesMetadata = gson.fromJson(commandBody,
						VariableValuesMetadata.class);
				DebugModel debugModel = getDebugModel(variableValuesMetadata.getSessionId(),
						variableValuesMetadata.getExecutionId(), variableValuesMetadata.getUserId());
				debugModel.setVariableValuesMetadata(variableValuesMetadata);
				sessionsMetadataRecieved = true;

			} else if (commandId.equals(DebugConstants.VIEW_BREAKPOINT_METADATA)) {
				BreakpointsMetadata breakpointsMetadata = gson.fromJson(commandBody,
						BreakpointsMetadata.class);
				DebugModel debugModel = getDebugModel(breakpointsMetadata.getSessionId(),
						breakpointsMetadata.getExecutionId(), breakpointsMetadata.getUserId());
				debugModel.setBreakpointsMetadata(breakpointsMetadata);
				sessionsMetadataRecieved = true;

			} else if (commandId.equals(DebugConstants.VIEW_ON_LINE_CHANGE)) {
				BreakpointMetadata currentLineBreak = gson.fromJson(commandBody,
						BreakpointMetadata.class);
				DebugModel debugModel = getDebugModel(currentLineBreak.getSessionId(),
						currentLineBreak.getExecutionId(), currentLineBreak.getUserId());
				debugModel.setCurrentLineBreak(currentLineBreak);
				StringBuilder path = new StringBuilder(currentLineBreak.getFullPath());
//				int lastIndex = path.lastIndexOf(SLASH);
//				if (lastIndex != -1) {
//					# 177
//					path.insert(lastIndex, SCRIPTING_SERVICES);
					path.insert(0, SCRIPTING_SERVICES);
					currentLineBreak.setFullPath(path.toString());
//				}
				sessionsMetadataRecieved = true;
			}
		}
	}

	public DebugModel createDebugModel(String sessionId, String executionId, String userId) {
		return DebugModelFacade.getInstance()
				.createDebugModel(sessionId, executionId, userId, this);
	}

	public void reinitializeDebugModels(DebugSessionsMetadata debugSessionsMetadata) {
		DebugModelFacade debugModelFacade = DebugModelFacade.getInstance();
		debugModelFacade.clearDebugModels();
		for (DebugSessionMetadata debugSessionMetadata : debugSessionsMetadata
				.getDebugSessionsMetadata()) {
			debugModelFacade.createDebugModel(debugSessionMetadata.getSessionId(),
					debugSessionMetadata.getExecutionId(), debugSessionMetadata.getUserId(), this);
		}
	}

	public void removeDebugModel(String sessionId, String executionId, String userId) {
		DebugModelFacade.getInstance().removeDebugModel(executionId);
	}

	public DebugModel getDebugModel(String sessionId, String executionId, String userId) {
		DebugModel debugModel = null;
		if ((debugModel = DebugModelFacade.getInstance().getDebugModel(executionId)) == null) {
			debugModel = DebugModelFacade.getInstance().createDebugModel(sessionId, executionId,
					userId, this);
		}
		return debugModel;
	}

	private void sendCommand(final String commandId, final String clientId, String commandBody) {
		logDebug("entering DebugView.sendCommand() with commandId: " + commandId
				+ ", clientId: " + clientId + ", commandBody: " + commandBody);
		DebugModel debugModel = DebugModelFacade.getActiveDebugModel();
		if (debugModel != null) {
			if (commandBody == null) {
				Gson gson = new Gson();
				commandBody = gson.toJson(new DebugSessionMetadata(debugModel.getSessionId(),
						debugModel.getExecutionId(), debugModel.getUserId()));
			}
			sendToBridge(commandId, clientId, commandBody);
		} else if (DebugConstants.DEBUG_REFRESH.equals(commandId)) {
			sendToBridge(commandId, clientId, commandBody);
		} else {
			logWarn("sending in DebugView.sendCommand() failed - DebugModel is null for commandId: "
					+ commandId + ", and commandBody: " + commandBody);
		}
		logDebug("exiting DebugView.sendCommand() with commandId: " + commandId
				+ ", and commandBody: " + commandBody);
	}

	private void sendToBridge(final String commandId, final String clientId, String commandBody) {
		if (this.debugProtocol != null) {
			this.debugProtocol.firePropertyChange(commandId, clientId, commandBody);
		} else {
			logDebug("sending DebugView.sendCommand() failed - DebugBridge is not present - with commandId: "
					+ commandId + ", and commandBody: " + commandBody);
			MessageDialog.openError(null, DEBUG_PROCESS_TITLE,
					INTERNAL_ERROR_DEBUG_BRIDGE_IS_NOT_PRESENT);
		}
	}

	@Override
	public void refresh(DebugModel debugModel) {
		String clientId = (debugModel == null) ? "debug.global.manager" : debugModel
				.getExecutionId();
		final String commandId = DebugConstants.DEBUG_REFRESH;
		sendCommand(commandId, clientId, null);
	}

	@Override
	public void stepInto(DebugModel debugModel) {
		final String commandId = DebugConstants.DEBUG_STEP_INTO;
		sendCommand(commandId, debugModel.getExecutionId(), null);
	}

	@Override
	public void stepOver(DebugModel debugModel) {
		final String commandId = DebugConstants.DEBUG_STEP_OVER;
		sendCommand(commandId, debugModel.getExecutionId(), null);
	}

	@Override
	public void continueExecution(DebugModel debugModel) {
		final String commandId = DebugConstants.DEBUG_CONTINUE;
		sendCommand(commandId, debugModel.getExecutionId(), null);

	}

	@Override
	public void skipAllBreakpoints(DebugModel debugModel) {
		final String commandId = DebugConstants.DEBUG_SKIP_ALL_BREAKPOINTS;
		sendCommand(commandId, debugModel.getExecutionId(), null);
	}

	@Override
	public IEditorPart openEditor(String path, int row) {
		IPath location = new Path(path);
		IWorkspace workspace = RemoteResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		if (root.exists(location)) {
			IFile file = root.getFile(location);
			return openWorkspaceFile(row, file);
		} else {
			IProject[] projects = root.getProjects();
			for (IProject project : projects) {
				if (project.exists(location)) {
					IFile file = project.getFile(location);
					return openWorkspaceFile(row, file);
				}
			}
		}
		return null;
	}

	private IEditorPart openWorkspaceFile(int row, IFile file) {
		IEditorPart sourceCodeEditor = OpenHandler.open(file, row);
		if (sourceCodeEditor != null && sourceCodeEditor instanceof JavaScriptEditor) {
			((JavaScriptEditor) sourceCodeEditor).setDebugRow(row);
		}
		return sourceCodeEditor;
	}

	@Override
	public void setBreakpoint(DebugModel debugModel, String path, int row) {
		if (debugModel != null) {
			BreakpointMetadata breakpoint = new BreakpointMetadata(debugModel.getSessionId(),
					debugModel.getExecutionId(), debugModel.getUserId(), path, row);
			String commandBody = new Gson().toJson(breakpoint);
			final String commandId = DebugConstants.DEBUG_SET_BREAKPOINT;
			sendCommand(commandId, debugModel.getExecutionId(), commandBody);
			waitForMetadata(debugModel);
		}
	}

	@Override
	public void clearBreakpoint(DebugModel debugModel, String path, int row) {
		if (debugModel != null) {
			BreakpointMetadata breakpoint = new BreakpointMetadata(debugModel.getSessionId(),
					debugModel.getExecutionId(), debugModel.getUserId(), path, row);
			String commandBody = new Gson().toJson(breakpoint);
			final String commandId = DebugConstants.DEBUG_CLEAR_BREAKPOINT;
			sendCommand(commandId, debugModel.getExecutionId(), commandBody);
			waitForMetadata(debugModel);
		}
	}

	@Override
	public void clearAllBreakpoints(DebugModel debugModel) {
		final String commandId = DebugConstants.DEBUG_CLEAR_ALL_BREAKPOINTS;
		sendCommand(commandId, debugModel.getExecutionId(), null);
	}

	@Override
	public void clearAllBreakpoints(DebugModel debugModel, String path) {
		final String commandId = DebugConstants.DEBUG_CLEAR_ALL_BREAKPOINTS_FOR_FILE;
		sendCommand(commandId, debugModel.getExecutionId(), path);
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
		if (sessionsTreeViewer == null
				|| sessionsTreeViewer.getTree() == null
				|| sessionsTreeViewer.getTree().isDisposed()) {
			return;
		}
		
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
		refreshSessionsView();
	}
}
