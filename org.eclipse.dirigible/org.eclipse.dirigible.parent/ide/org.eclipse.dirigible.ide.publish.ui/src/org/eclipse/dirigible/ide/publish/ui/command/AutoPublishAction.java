package org.eclipse.dirigible.ide.publish.ui.command;

import org.eclipse.dirigible.ide.common.dual.DualParameters;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class AutoPublishAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		DualParameters.set(DualParameters.SET_AUTO_PUBLISH, action.isChecked());
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// no need
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}
