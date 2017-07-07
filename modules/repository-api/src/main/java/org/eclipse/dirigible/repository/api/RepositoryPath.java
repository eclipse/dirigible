/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.api;

import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Utility class representing the path object in the Repository
 */
public class RepositoryPath {

	private String path;

	private final String[] segments;

	public RepositoryPath(String path) {
		this.path = path;
		final StringTokenizer tokenizer = new StringTokenizer(path, IRepository.SEPARATOR);
		segments = new String[tokenizer.countTokens()];
		for (int i = 0; i < segments.length; ++i) {
			segments[i] = tokenizer.nextToken();
		}
	}

	public RepositoryPath(RepositoryPath repositoryPath) {
		this(repositoryPath.segments);
	}

	public RepositoryPath(String...segments) {
		this.segments = Arrays.copyOf(segments, segments.length);
		this.path = toString();
	}
	
	/**
	 * Getter for the last segment
	 *
	 * @return
	 */
	public String getLastSegment() {
		if (segments.length == 0) {
			return ""; //$NON-NLS-1$
		}
		return segments[segments.length - 1];
	}

	/**
	 * Getter for the path of the parent
	 *
	 * @return
	 */
	public RepositoryPath getParentPath() {
		if (segments.length == 0) {
			return null;
		}
		final String[] newSegments = Arrays.copyOf(segments, segments.length - 1);
		return new RepositoryPath(newSegments);
	}

	/**
	 * Add new segment after the last position
	 *
	 * @param name
	 * @return
	 */
	public RepositoryPath append(String name) {

		final StringTokenizer tokenizer = new StringTokenizer(name, IRepository.SEPARATOR);
		final String[] newSegments = Arrays.copyOf(segments, segments.length + tokenizer.countTokens());
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			newSegments[segments.length + i++] = tokenizer.nextToken();
		}

		return new RepositoryPath(newSegments);
	}

	@Override
	public String toString() {
		if (segments.length == 0) {
			return IRepository.SEPARATOR;
		}
		final StringBuilder builder = new StringBuilder();
		for (String segment : segments) {
			builder.append(IRepository.SEPARATOR);
			builder.append(segment);
		}
		return builder.toString();
	}

	public String getPath() {
		return path;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof RepositoryPath)) {
			return false;
		}
		final RepositoryPath other = (RepositoryPath) obj;
		return getPath().equals(other.getPath());
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	public String[] getSegments() {
		return Arrays.copyOf(segments, segments.length);
	}

	public String constructPath(int number) {
		if (number >= segments.length) {
			return toString();
		}
		if (segments.length == 0) {
			return IRepository.SEPARATOR;
		}
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < number; i++) {
			builder.append(IRepository.SEPARATOR);
			builder.append(segments[i]);
		}
		return builder.toString();
	}

	public static String normalizePath(String path, String name) {
		String normalizedPath = null;
		if (path != null) {
			if (path.endsWith(IRepository.SEPARATOR)) {
				normalizedPath = path + name;
			} else {
				normalizedPath = path + IRepository.SEPARATOR + name;
			}
		}
		return normalizedPath;
	}

	public static String normalizeName(String name) {
		return name.replaceAll("[^A-Za-z0-9_]", "_");
	}
}
