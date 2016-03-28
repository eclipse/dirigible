package org.eclipse.dirigible.ide.workspace.dual;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.widgets.Display;

public class CustomResourceChangeListener implements IResourceChangeListener {

	private static final Logger logger = Logger.getLogger(CustomResourceChangeListener.class.getName());

	private StructuredViewer viewer;
	private ICustomResourceChangeListenerCallback callbackObject;

	public CustomResourceChangeListener(StructuredViewer viewer, ICustomResourceChangeListenerCallback callbackObject) {
		super();
		this.viewer = viewer;
		this.callbackObject = callbackObject;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		final Display display = Display.getCurrent();
		UISession uiSession = RWT.getUISession(display);
		uiSession.exec(new Runnable() {
			@Override
			public void run() {
				if (!viewer.getControl().isDisposed()) {
					try {
						callbackObject.callback();
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
			}
		});

	}

}
