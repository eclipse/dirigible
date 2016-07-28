package org.eclipse.dirigible.ide.common.status;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dirigible.repository.logging.Logger;

public class LogProgressMonitor implements IProgressMonitor {

	private static final Logger logger = Logger.getLogger(LogProgressMonitor.class);

	boolean cancelled;

	@Override
	public void beginTask(String name, int totalWork) {
		logger.info(String.format("beginTask: %s, %d ", name, totalWork));
	}

	@Override
	public void done() {
		logger.info("done.");
	}

	@Override
	public void internalWorked(double work) {
		logger.info(String.format("internalWorked: %d ", work));
	}

	@Override
	public boolean isCanceled() {
		return this.cancelled;
	}

	@Override
	public void setCanceled(boolean value) {
		this.cancelled = value;
	}

	@Override
	public void setTaskName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subTask(String name) {
		logger.info(String.format("subTask: %s ", name));

	}

	@Override
	public void worked(int work) {
		logger.info(String.format("worked: %d ", work));
	}

}
