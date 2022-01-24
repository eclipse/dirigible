/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.changelog.synchronizer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.SortedSet;

import liquibase.resource.AbstractResourceAccessor;
import liquibase.resource.InputStreamList;

public class ChangelogResourceAccessor extends AbstractResourceAccessor {
	
	private URI location;
    private InputStream stream;
    
    public ChangelogResourceAccessor(URI location, InputStream stream) {
        super();
        this.location = location;
        this.stream = stream;
    }

	@Override
	public InputStreamList openStreams(String relativeTo, String streamPath) throws IOException {
		InputStreamList inputStreamList = new InputStreamList(location, stream);
        return inputStreamList;
	}

	@Override
	public SortedSet<String> list(String relativeTo, String path, boolean recursive, boolean includeFiles,
			boolean includeDirectories) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedSet<String> describeLocations() {
		// TODO Auto-generated method stub
		return null;
	}


}
