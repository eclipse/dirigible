package org.eclipse.dirigible.ide.db.preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.datasource.NamedDataSourcesInitializer;
import org.eclipse.dirigible.repository.ext.conf.ConfigurationStore;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

public class DatasourcesListEditor extends ListEditor {

	private static final Logger logger = Logger.getLogger(DatasourcesListEditor.class);

	private boolean initalized;

	private DatasourcesListEditor() {
		super();
	}

	public DatasourcesListEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	@Override
	protected String createList(String[] items) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < items.length; i++) {
			if (i > 0) {
				buff.append(",");
			}
			buff.append(items[i]);
		}
		return buff.toString();
	}

	@Override
	protected String[] parseString(String stringList) {
		StringTokenizer tokenizer = new StringTokenizer(",");
		List<String> items = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			items.add(token);
		}
		return items.toArray(new String[items.size()]);
	}

	@Override
	protected String getNewInputObject() {
		String id = null;
		String name = null;
		String type = null;
		String location = null;
		NewDatasourceDialog dialog = new NewDatasourceDialog(getShell());
		dialog.create();

		if (dialog.open() == Window.OK) {
			id = dialog.getId();
			name = dialog.getName();
			type = dialog.getType();
			location = dialog.getLocation();

			Set<String> names = DataSourceFacade.getInstance().getNamedDataSourcesNames();
			if (names.contains(id)) {
				MessageDialog.openError(getShell(), "", String.format("Datasource with Id: %s already exists", id));
				return null;
			}

			Properties properties = new Properties();
			properties.setProperty(DataSourceFacade.PARAM_DB_ID, id);
			properties.setProperty(DataSourceFacade.PARAM_DB_NAME, name);
			properties.setProperty(DataSourceFacade.PARAM_DB_TYPE, type);
			properties.setProperty(DataSourceFacade.PARAM_DB_LOC, location);
			IRepository repository = RepositoryFacade.getInstance().getRepository();
			ConfigurationStore store = new ConfigurationStore(repository);
			try {
				store.setGlobalSettings(NamedDataSourcesInitializer.DATASOURCES_FOLDER, id, properties);
				reinitializeDatasources();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}

		}
		return id;
	}

	@Override
	protected void selectionChanged() {
		super.selectionChanged();

		if (isInitalized()) {
			Set<String> names = DataSourceFacade.getInstance().getNamedDataSourcesNames();
			org.eclipse.swt.widgets.List list = getList();
			String[] items = list.getItems();
			if (items.length < names.size()) {
				// something got removed
				for (String name : names) {
					if (!nameIsFound(name, items)) {
						IRepository repository = RepositoryFacade.getInstance().getRepository();
						ConfigurationStore store = new ConfigurationStore(repository);
						try {
							store.removeGlobalSettings(NamedDataSourcesInitializer.DATASOURCES_FOLDER, name);
							reinitializeDatasources();
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}

			}
		}

	}

	private void reinitializeDatasources() {
		new NamedDataSourcesInitializer().initializeAvailableDataSources(CommonIDEParameters.getRequest(),
				RepositoryFacade.getInstance().getRepository());
	}

	private boolean nameIsFound(String name, String[] items) {
		for (String item : items) {
			if (item.equals(name)) {
				return true;
			}
		}
		return false;
	}

	public boolean isInitalized() {
		return initalized;
	}

	public void setInitalized(boolean initalized) {
		this.initalized = initalized;
	}

}
