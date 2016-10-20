package org.eclipse.dirigible.ide.workspace;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class WorkspaceActivator extends AbstractUIPlugin {

	public WorkspaceActivator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		System.getProperties().put(ICommonConstants.WORKSPACES_SERVICE, new Workspaces());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		System.getProperties().remove(ICommonConstants.WORKSPACES_SERVICE);
	}

}
