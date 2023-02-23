package org.eclipse.dirigible.components.jobs.config;

import org.eclipse.dirigible.components.base.artefact.Unit;
import org.springframework.stereotype.Component;

@Component
public class JobsUnit implements Unit {

	@Override
	public String getName() {
		return "Jobs";
	}

	@Override
	public String getProvider() {
		return "Eclipse Dirigible";
	}

}
