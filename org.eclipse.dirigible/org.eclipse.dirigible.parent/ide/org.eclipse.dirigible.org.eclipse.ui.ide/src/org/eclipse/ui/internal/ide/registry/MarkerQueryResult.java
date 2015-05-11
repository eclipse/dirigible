/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.ide.registry;

/**
 * Instances of this class represent the result of a specific marker query.
 * Specifically they contain an ordered collection of marker attribute values.
 */

public class MarkerQueryResult {
	/**
	 * An ordered collection of marker attribute values.
	 */
	private String[] values;

	/**
	 * Cached hash code value
	 */
	private int hashCode;

	/**
	 * Creates a new marker query result with the given values.
	 * <p>
	 * The values may not be empty.
	 * </p>
	 * 
	 * @param markerAttributeValues
	 *            the target marker's attribute values
	 */
	public MarkerQueryResult(String[] markerAttributeValues) {
		if (markerAttributeValues == null) {
			throw new IllegalArgumentException();
		}
		values = markerAttributeValues;
		computeHashCode();
	}

	/*
	 * (non-Javadoc) Method declared on Object.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof MarkerQueryResult)) {
			return false;
		}

		if (o == this) {
			return true;
		}

		MarkerQueryResult mqr = (MarkerQueryResult) o;
		if (values.length != mqr.values.length) {
			return false;
		}

		for (int i = 0; i < values.length; i++) {
			if (!(values[i].equals(mqr.values[i]))) {
				return false;
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc) Method declared on Object.
	 */
	public int hashCode() {
		return hashCode;
	}

	/**
	 * Computes the hash code for this instance.
	 */
	public void computeHashCode() {
		hashCode = 19;

		for (int i = 0; i < values.length; i++) {
			hashCode = hashCode * 37 + values[i].hashCode();
		}
	}
}
