package org.eclipse.dirigible.repository;

import org.eclipse.dirigible.repository.local.LocalRepositoryModule;
import org.eclipse.dirigible.repository.master.MasterRepositoryModule;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

@Singleton
public class RepositoryModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new LocalRepositoryModule());
		install(new MasterRepositoryModule());
	}

}
