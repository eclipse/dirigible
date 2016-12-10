/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.db.viewer.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.common.image.ImageUtils;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.datasource.NamedDataSourcesInitializer;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class DatabaseViewerToolBar implements ISelectionProvider {

	private static final Logger logger = Logger.getLogger(DatabaseViewerToolBar.class);

	private static final Image REFRESH_ICON = ImageUtils
			.createImage(ImageUtils.getIconURL("org.eclipse.dirigible.ide.workspace.ui", "/resources/icons/", "refresh.png")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private Combo datasourcesCombo;

	public void createToolBar(Composite parent, Shell shell) {
		if (CommonIDEParameters.isRAP()) {
			int style = SWT.FLAT | SWT.FILL | SWT.RIGHT | SWT.BORDER | SWT.SHADOW_OUT;
			final Composite panel = new Composite(parent, style);
			panel.setLayout(new GridLayout(2, false));
			panel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
			createDatasourcesToolItem(panel);
			createRefreshButton(panel);
		}
	}

	private void createDatasourcesToolItem(Composite parent) {

		final ISelectionProvider selectionProvider = this;

		datasourcesCombo = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		datasourcesCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		datasourcesCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selection = new StructuredSelection(new Object[] { datasourcesCombo.getText() });
				SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(selectionProvider, selection);

				for (ISelectionChangedListener selectionChangedListener : listeners) {
					selectionChangedListener.selectionChanged(selectionChangedEvent);
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//
			}
		});
		fillDatasources();

	}

	private void createRefreshButton(Composite parent) {
		Button refreshBtn = new Button(parent, SWT.PUSH | SWT.FLAT);
		refreshBtn.setImage(REFRESH_ICON);
		refreshBtn.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, true));
		refreshBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				new NamedDataSourcesInitializer().initializeAvailableDataSources(CommonIDEParameters.getRequest(),
						RepositoryFacade.getInstance().getRepository());
				fillDatasources();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//
			}
		});
	}

	static final String DEFAULT_DS_NAME = "Default";

	private void fillDatasources() {
		datasourcesCombo.removeAll();
		// datasourcesCombo.add(DatabaseViewer.DEFAULT_DATASOURCE_NAME);
		List<String> datasourcesNames = new ArrayList<String>(DataSourceFacade.getInstance().getNamedDataSourcesNames());
		/*
		 * make sure:
		 * 1) data source names are alphabetically sorted
		 * 2) "Default" is always the first in the list
		 */
		if (datasourcesNames.size() > 1) {
			Collections.sort(datasourcesNames, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					if (DEFAULT_DS_NAME.equals(o2)) {
						return 1;
					}
					if (DEFAULT_DS_NAME.equals(o1)) {
						return -1;
					}
					return o1.compareTo(o2);
				}
			});
		}
		for (String datasourcesName : datasourcesNames) {
			datasourcesCombo.add(datasourcesName);
		}
		datasourcesCombo.select(0);
	}

	public String getSelectedDatasource() {
		return datasourcesCombo.getText();
	}

	private List<ISelectionChangedListener> listeners = Collections.synchronizedList(new ArrayList<ISelectionChangedListener>());

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		StructuredSelection selection = new StructuredSelection(new Object[] { datasourcesCombo.getText() });
		return selection;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		throw new UnsupportedOperationException();
	}

}
