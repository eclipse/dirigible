package org.eclipse.dirigible.ide.workspace.dual;

import java.util.logging.Logger;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.jface.viewers.StructuredViewer;

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
		try {
			callbackObject.callback();
		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
	}

}
