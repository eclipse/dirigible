package org.eclipse.dirigible.ide.registry.conf;

import java.io.IOException;

import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class HomeLocationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final Logger logger = Logger.getLogger(HomeLocationPreferencePage.class);

	private StringFieldEditor homeUrlField;

	public HomeLocationPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		super.initialize();
	}

	@Override
	protected void createFieldEditors() {
		homeUrlField = new StringFieldEditor(IRepositoryPaths.HOME_URL, "&Home Location:", getFieldEditorParent());
		Text text = homeUrlField.getTextControl(getFieldEditorParent());
		String homeUrl = getHomeUrl();
		text.setText(homeUrl != null ? homeUrl : ICommonConstants.EMPTY_STRING);
		addField(homeUrlField);

	}

	private String getHomeUrl() {
		try {
			final IRepository repository = RepositoryFacade.getInstance().getRepository();
			final IResource resource = repository.getResource(IRepositoryPaths.REPOSITORY_HOME_URL);
			if (resource.exists()) {
				return new String(resource.getContent());
			}
		} catch (RepositoryException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return IRepositoryPaths.INDEX_HTML_FALLBACK;
	}

	private void setHomeUrl(String url) {
		try {
			final IRepository repository = RepositoryFacade.getInstance().getRepository();
			final IResource resource = repository.getResource(IRepositoryPaths.REPOSITORY_HOME_URL);
			resource.setContent(url.getBytes());
		} catch (RepositoryException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

	private void storeValues() {
		String url = homeUrlField.getStringValue();
		setHomeUrl(url);
	}

	@Override
	protected void performApply() {
		super.performApply();
		storeValues();
	}

	@Override
	public boolean performOk() {
		storeValues();
		return super.performOk();
	}

}
