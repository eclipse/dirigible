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

/**
 * The Class ChangelogResourceAccessor.
 */
public class ChangelogResourceAccessor extends AbstractResourceAccessor {
	
	/** The location. */
	private URI location;
    
    /** The stream. */
    private InputStream stream;
    
    /**
     * Instantiates a new changelog resource accessor.
     *
     * @param location the location
     * @param stream the stream
     */
    public ChangelogResourceAccessor(URI location, InputStream stream) {
        super();
        this.location = location;
        this.stream = stream;
    }

	/**
	 * Open streams.
	 *
	 * @param relativeTo the relative to
	 * @param streamPath the stream path
	 * @return the input stream list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public InputStreamList openStreams(String relativeTo, String streamPath) throws IOException {
		InputStreamList inputStreamList = new InputStreamList(location, stream);
        return inputStreamList;
	}

	/**
	 * List.
	 *
	 * @param relativeTo the relative to
	 * @param path the path
	 * @param recursive the recursive
	 * @param includeFiles the include files
	 * @param includeDirectories the include directories
	 * @return the sorted set
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public SortedSet<String> list(String relativeTo, String path, boolean recursive, boolean includeFiles,
			boolean includeDirectories) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Describe locations.
	 *
	 * @return the sorted set
	 */
	@Override
	public SortedSet<String> describeLocations() {
		// TODO Auto-generated method stub
		return null;
	}


}
