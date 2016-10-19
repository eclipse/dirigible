package org.eclipse.dirigible.ide.publish;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class PublishActivator extends AbstractUIPlugin {

	public PublishActivator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		System.getProperties().put(ICommonConstants.LIFECYCLE_SERVICE, new LifecycleService());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		System.getProperties().remove(ICommonConstants.LIFECYCLE_SERVICE);
	}

}
