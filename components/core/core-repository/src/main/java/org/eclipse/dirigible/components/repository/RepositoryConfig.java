/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.repository;

import java.io.File;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
class RepositoryConfig {


    @Bean
    IRepository repository() {
        String repoFolderPath = Configuration.get(Configuration.DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER, "target");
        boolean absolutePath = repoFolderPath.startsWith(File.separator);
        LocalRepository localRepository = new LocalRepository(repoFolderPath, absolutePath);

        // To be removed once moved to Spring entirely
        StaticObjects.set(StaticObjects.REPOSITORY, localRepository);

        return localRepository;
    }

}
