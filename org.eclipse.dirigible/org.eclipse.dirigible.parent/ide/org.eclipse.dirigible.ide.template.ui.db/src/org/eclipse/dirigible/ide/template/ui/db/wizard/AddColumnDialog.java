/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.db.wizard;

import org.eclipse.dirigible.repository.datasource.DBSupportedTypesMap;
import org.eclipse.dirigible.repository.datasource.DBSupportedTypesMap.DataTypes;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddColumnDialog extends TitleAreaDialog {

	private static final Logger logger = Logger.getLogger(AddColumnDialog.class);

	private static final String END_OF_LINE = "\n"; //$NON-NLS-1$

	private static final String NAME = Messages.AddColumnDialog_NAME;

	private static final String TYPE = Messages.AddColumnDialog_TYPE;

	private static final String INPUT_THE_LENGTH_AS_INTEGER = Messages.AddColumnDialog_INPUT_THE_LENGTH_AS_INTEGER;

	private static final String LENGTH = Messages.AddColumnDialog_LENGTH;

	private static final String NOT_NULL = Messages.AddColumnDialog_NOT_NULL;

	private static final String PRIMARY_KEY = Messages.AddColumnDialog_PRIMARY_KEY;

	private static final String DEFAULT_VALUE = Messages.AddColumnDialog_DEFAULT_VALUE;

	private static final String ADD_COLUMN = Messages.AddColumnDialog_ADD_COLUMN;

	private static final String INPUT_THE_LENGTH = Messages.AddColumnDialog_INPUT_THE_LENGTH;

	private static final String INPUT_THE_TYPE = Messages.AddColumnDialog_INPUT_THE_TYPE;

	private static final String INPUT_THE_NAME = Messages.AddColumnDialog_INPUT_THE_NAME;

	private static final String DUPLICATE_COLUMN_NAME = Messages.AddColumnDialog_DUPLICATE_COLUMN_NAME;

	private static final long serialVersionUID = 131615855198018319L;

	private ColumnDefinition columnDefinition;
	private ColumnDefinition[] columnDefinitions;
	private Text nameText;
	private Combo typeCombo;
	private Text lengthText;
	private Button nnButton;
	private Button pkButton;
	private Text defaultText;
	private Shell parentShell;

	public AddColumnDialog(ColumnDefinition columnDefinition, ColumnDefinition[] columnDefinitions, Shell parentShell) {
		super(parentShell);

		this.parentShell = parentShell;
		this.parentShell.setEnabled(false);

		this.columnDefinition = columnDefinition;
		this.columnDefinitions = columnDefinitions;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(ADD_COLUMN);
	}

	@Override
	protected int getShellStyle() {
		return SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);
		ScrolledComposite sc = new ScrolledComposite(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayout(new GridLayout());
		sc.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		sc.setMinSize(200, 200);
		final Composite compositeInternal = new Composite(sc, SWT.NONE);
		compositeInternal.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		GridLayout layout = new GridLayout(2, false);
		layout.marginBottom = 20;
		compositeInternal.setLayout(layout);

		createNameField(compositeInternal);
		createTypeField(compositeInternal);
		createLengthField(compositeInternal);
		createNotNullField(compositeInternal);
		createPrimaryKeyField(compositeInternal);
		createDefaultValueField(compositeInternal);
		sc.setContent(compositeInternal);

		return composite;
	}

	private void createDefaultValueField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(DEFAULT_VALUE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		defaultText = new Text(parent, SWT.BORDER);
		defaultText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		defaultText.addModifyListener(new ModifyListener() {
			private static final long serialVersionUID = -8689439856255951278L;

			@Override
			public void modifyText(ModifyEvent event) {
				if (defaultText.getText() != null) {
					columnDefinition.setDefaultValue(defaultText.getText());
				}
			}
		});

	}

	private void createPrimaryKeyField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(PRIMARY_KEY);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		pkButton = new Button(parent, SWT.CHECK);
		pkButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		pkButton.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = -7266916932639020009L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				columnDefinition.setPrimaryKey(pkButton.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//
			}
		});
	}

	private void createNotNullField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(NOT_NULL);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		nnButton = new Button(parent, SWT.CHECK);
		nnButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		nnButton.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = 6729005491684506604L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				columnDefinition.setNotNull(nnButton.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//
			}
		});
	}

	private void createLengthField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(LENGTH);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		lengthText = new Text(parent, SWT.BORDER);
		lengthText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lengthText.addModifyListener(new ModifyListener() {
			private static final long serialVersionUID = 70852273750274067L;

			@Override
			public void modifyText(ModifyEvent event) {
				if ((lengthText.getText() == null) || "".equals(lengthText.getText())) { //$NON-NLS-1$
					if (DataTypes.VARCHAR.equals(DataTypes.valueOf(typeCombo.getText()))
							|| DataTypes.CHAR.equals(DataTypes.valueOf(typeCombo.getText()))) {
						setErrorMessage(INPUT_THE_LENGTH);
					} else {
						setErrorMessage(null);
					}
				} else {
					boolean isInt = false;
					try {
						int l = Integer.parseInt(lengthText.getText());
						if (l > 0) {
							isInt = true;
						}
					} catch (Exception e) {
						logger.debug(e.getMessage());
					}
					if (isInt) {
						setErrorMessage(null);
						columnDefinition.setLength(Integer.parseInt(lengthText.getText()));
					} else {
						lengthText.setText(""); //$NON-NLS-1$
						columnDefinition.setLength(0);
						setErrorMessage(INPUT_THE_LENGTH_AS_INTEGER);
					}

				}
			}
		});

	}

	private void createTypeField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(TYPE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		typeCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		typeCombo.setItems(DBSupportedTypesMap.getSupportedTypes());
		typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		typeCombo.addModifyListener(new ModifyListener() {
			private static final long serialVersionUID = 9064913692268796690L;

			@Override
			public void modifyText(ModifyEvent event) {
				if ((typeCombo.getText() == null) || "".equals(typeCombo.getText())) { //$NON-NLS-1$
					setErrorMessage(INPUT_THE_TYPE);
				} else {
					setErrorMessage(null);
					columnDefinition.setType(typeCombo.getText());
				}
			}
		});

	}

	private void createNameField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(NAME);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		nameText = new Text(parent, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		nameText.setFocus();
		nameText.addModifyListener(new ModifyListener() {
			private static final long serialVersionUID = -5729566562712894830L;

			@Override
			public void modifyText(ModifyEvent event) {
				if ((nameText.getText() == null) || "".equals(nameText.getText())) { //$NON-NLS-1$
					setErrorMessage(INPUT_THE_NAME);
				} else {
					setErrorMessage(null);
					columnDefinition.setName(nameText.getText());
				}
			}
		});
	}

	@Override
	protected void okPressed() {
		String errors = validateInput();
		if (errors == null) {
			super.okPressed();
			this.parentShell.setEnabled(true);
		} else {
			setErrorMessage(errors);
		}
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
		this.parentShell.setEnabled(true);
	}

	@Override
	protected void handleShellCloseEvent() {
		super.handleShellCloseEvent();
		this.parentShell.setEnabled(true);
	}

	private String validateInput() {
		StringBuilder buff = new StringBuilder();
		if ((columnDefinition.getName() == null) || "".equals(columnDefinition.getName())) { //$NON-NLS-1$
			buff.append(INPUT_THE_NAME + END_OF_LINE);
		}
		if ((columnDefinition.getType() == null) || "".equals(columnDefinition.getType())) { //$NON-NLS-1$
			buff.append(INPUT_THE_TYPE + END_OF_LINE);
		}
		if ((columnDefinition.getLength() == 0) && (DataTypes.VARCHAR.equals(DataTypes.valueOf(typeCombo.getText()))
				|| DataTypes.CHAR.equals(DataTypes.valueOf(typeCombo.getText())))) {
			buff.append(INPUT_THE_LENGTH + END_OF_LINE);
		}
		// check for duplicate column name
		for (ColumnDefinition columnDefinition_ : columnDefinitions) {
			if (columnDefinition.getName().equals(columnDefinition_.getName())) {
				buff.append(DUPLICATE_COLUMN_NAME + END_OF_LINE);
				break;
			}
		}
		if (buff.length() > 0) {
			return buff.toString();
		}
		return null;
	}

}
