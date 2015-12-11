package org.eclipse.dirigible.ide.db.preferences;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewDatasourceDialog extends TitleAreaDialog {

	private String id;
	private String name;
	private String type;
	private String location;

	private Text idValue;
	private Text nameValue;
	private Combo typeValue;
	private Text locationValue;

	public NewDatasourceDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add new Data Source");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		createId(container);

		createName(container);

		createType(container);

		createLocation(container);

		return area;
	}

	private void createId(Composite container) {
		Label lId = new Label(container, SWT.NONE);
		lId.setText("Id");
		GridData dataId = new GridData();
		dataId.grabExcessHorizontalSpace = true;
		dataId.horizontalAlignment = GridData.FILL;
		idValue = new Text(container, SWT.BORDER);
		idValue.setLayoutData(dataId);
	}

	private void createName(Composite container) {
		Label lName = new Label(container, SWT.NONE);
		lName.setText("Name");
		GridData dataName = new GridData();
		dataName.grabExcessHorizontalSpace = true;
		dataName.horizontalAlignment = GridData.FILL;
		nameValue = new Text(container, SWT.BORDER);
		nameValue.setLayoutData(dataName);
	}

	private void createType(Composite container) {
		Label lType = new Label(container, SWT.NONE);
		lType.setText("Type");
		GridData dataType = new GridData();
		dataType.grabExcessHorizontalSpace = true;
		dataType.horizontalAlignment = GridData.FILL;
		typeValue = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		typeValue.add("JNDI");
		typeValue.add("Local");
		typeValue.setText("JNDI");
		typeValue.setLayoutData(dataType);
	}

	private void createLocation(Composite container) {
		Label lLocation = new Label(container, SWT.NONE);
		lLocation.setText("Location");
		GridData dataLocation = new GridData();
		dataLocation.grabExcessHorizontalSpace = true;
		dataLocation.horizontalAlignment = GridData.FILL;
		locationValue = new Text(container, SWT.BORDER);
		locationValue.setText("java:comp/env/jdbc/");
		locationValue.setLayoutData(dataLocation);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		id = idValue.getText();
		name = nameValue.getText();
		type = typeValue.getText();
		location = locationValue.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getLocation() {
		return location;
	}

}
