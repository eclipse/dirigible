/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.ide.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IMarkerHelpRegistry;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.internal.ide.Policy;
import org.osgi.framework.Bundle;

/**
 * This class is a registry for marker help contexts and resolutions.
 */
public class MarkerHelpRegistry implements IMarkerHelpRegistry {
	/**
	 * Table of queries for marker F1 help.
	 */
	@SuppressWarnings("rawtypes")
	private Map<MarkerQuery, Map> helpQueries = new HashMap<MarkerQuery, Map>();

	/**
	 * Sorted list of help queries. Used to ensure that the "most specific"
	 * query is tried first
	 */
	private List<MarkerQuery> sortedHelpQueries;

	/**
	 * Table of queries for marker resolutions
	 */
	@SuppressWarnings("rawtypes")
	private Map<MarkerQuery, Map> resolutionQueries = new LinkedHashMap<MarkerQuery, Map>();

	/**
	 * Help context id attribute in configuration element
	 */
	private static final String ATT_HELP = "helpContextId"; //$NON-NLS-1$

	/**
	 * Resolution class attribute name in configuration element
	 */
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$

	private class QueryComparator implements Comparator<Object> {
		/*
		 * (non-Javadoc) Method declared on Object.
		 */
		public boolean equals(Object o) {
			if (!(o instanceof QueryComparator)) {
				return false;
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			return QueryComparator.class.hashCode();
		}

		/*
		 * (non-Javadoc) Method declared on Comparator.
		 */
		public int compare(Object o1, Object o2) {
			// more attribues come first
			MarkerQuery q1 = (MarkerQuery) o1;
			MarkerQuery q2 = (MarkerQuery) o2;

			int size1 = q1.getAttributes().length;
			int size2 = q2.getAttributes().length;

			if (size1 > size2) {
				return -1;
			}
			if (size1 == size2) {
				return 0;
			}
			return 1;
		}
	}

	/*
	 * (non-Javadoc) Method declared on IMarkerHelpRegistry.
	 */
	public String getHelp(IMarker marker) {
		if (sortedHelpQueries == null) {
			Set<MarkerQuery> set = helpQueries.keySet();
			sortedHelpQueries = new ArrayList<MarkerQuery>(set.size());
			sortedHelpQueries.addAll(set);
			Collections.sort(sortedHelpQueries, new QueryComparator());
		}

		// Return the first match (we assume there is only one)
		for (Iterator<MarkerQuery> iter = sortedHelpQueries.iterator(); iter.hasNext();) {
			MarkerQuery query = (MarkerQuery) iter.next();
			MarkerQueryResult result = query.performQuery(marker);
			if (result != null) {
				// See if a matching result is registered
				Map<?, ?> resultsTable = (Map<?, ?>) helpQueries.get(query);

				if (resultsTable.containsKey(result)) {

					Iterator<?> elements = ((Collection<?>) resultsTable.get(result))
							.iterator();
					while (elements.hasNext()) {
						IConfigurationElement element = (IConfigurationElement) elements
								.next();
						// We have a match so return the help context id
						return element.getAttribute(ATT_HELP);
					}
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc) Method declared on IMarkerHelpRegistry.
	 */
	public boolean hasResolutions(IMarker marker) {
		// Detect a match
		for (Iterator<MarkerQuery> iter = resolutionQueries.keySet().iterator(); iter
				.hasNext();) {
			MarkerQuery query = (MarkerQuery) iter.next();
			MarkerQueryResult result = query.performQuery(marker);
			if (result != null) {
				// See if a matching result is registered
				Map<?, ?> resultsTable = (Map<?, ?>) resolutionQueries.get(query);

				if (resultsTable.containsKey(result)) {

					Iterator<?> elements = ((Collection<?>) resultsTable.get(result))
							.iterator();
					while (elements.hasNext()) {
						IConfigurationElement element = (IConfigurationElement) elements
								.next();

						if (hasResolution(marker, element))
							return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Return whether or not this configuration element has a resolution for the
	 * marker.
	 * 
	 * @param marker
	 * @param element
	 * @return boolean <code>true</code> if there is a resolution.
	 */
	@SuppressWarnings("deprecation")
	private boolean hasResolution(IMarker marker, IConfigurationElement element) {
		IMarkerResolutionGenerator generator = null;
		if (Platform.getBundle(element.getNamespace()).getState() == Bundle.ACTIVE) {
			// The element's plugin is loaded so we instantiate
			// the resolution
			try {
				generator = (IMarkerResolutionGenerator) element
						.createExecutableExtension(ATT_CLASS);
			} catch (CoreException e) {
				Policy.handle(e);
			}
			if (generator != null) {
				if (generator instanceof IMarkerResolutionGenerator2) {
					if (((IMarkerResolutionGenerator2) generator)
							.hasResolutions(marker)) {
						return true;
					}
				} else {
					IMarkerResolution[] resolutions = generator
							.getResolutions(marker);
					if (resolutions.length > 0) {
						// there is at least one resolution
						return true;
					}
				}
			}
		} else {
			// The element's plugin in not loaded so we assume
			// the generator will produce resolutions for the marker
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IMarkerHelpRegistry#getResolutions(org.eclipse.core.resources
	 * .IMarker)
	 */
	@SuppressWarnings("rawtypes")
	public IMarkerResolution[] getResolutions(IMarker marker) {
		// Collect all matches
		ArrayList<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();
		for (Iterator<?> iter = resolutionQueries.entrySet().iterator(); iter
				.hasNext();) {
			Map.Entry entry = (Entry<?, ?>) iter.next();
			MarkerQuery query = (MarkerQuery) entry.getKey();
			MarkerQueryResult result = query.performQuery(marker);
			if (result != null) {
				// See if a matching result is registered
				Map<?, ?> resultsTable = (Map<?, ?>) entry.getValue();

				if (resultsTable.containsKey(result)) {

					Iterator<?> elements = ((Collection<?>) resultsTable.get(result))
							.iterator();
					while (elements.hasNext()) {
						IConfigurationElement element = (IConfigurationElement) elements
								.next();

						IMarkerResolutionGenerator generator = null;
						try {
							generator = (IMarkerResolutionGenerator) element
									.createExecutableExtension(ATT_CLASS);
						} catch (CoreException e) {
							Policy.handle(e);
						}
						if (generator != null) {
							IMarkerResolution[] generatedResolutions = generator
									.getResolutions(marker);
							for (int i = 0; i < generatedResolutions.length; i++) {
								resolutions.add(generatedResolutions[i]);
							}
						}

					}
				}
			}
		}
		return (IMarkerResolution[]) resolutions
				.toArray(new IMarkerResolution[resolutions.size()]);
	}

	/**
	 * Adds a help query to the registry.
	 * 
	 * @param query
	 *            a marker query
	 * @param result
	 *            a result for the given query
	 * @param element
	 *            the configuration element defining the result
	 */
	public void addHelpQuery(MarkerQuery query, MarkerQueryResult result,
			IConfigurationElement element) {

		addQuery(helpQueries, query, result, element);
	}

	/**
	 * Adds a resolution query to the registry.
	 * 
	 * @param query
	 *            a marker query
	 * @param result
	 *            a result for the given query
	 * @param element
	 *            the configuration element defining the result
	 */
	public void addResolutionQuery(MarkerQuery query, MarkerQueryResult result,
			IConfigurationElement element) {

		addQuery(resolutionQueries, query, result, element);
	}

	/**
	 * Adds a query to the given table.
	 * 
	 * @param table
	 *            the table to which the query is added
	 * @param query
	 *            a marker query
	 * @param result
	 *            a result for the given query
	 * @param element
	 *            the configuration element defining the result
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addQuery(Map<MarkerQuery, Map> table, MarkerQuery query,
			MarkerQueryResult result, IConfigurationElement element) {

		// See if the query is already in the table
		Map<MarkerQueryResult, Collection<IConfigurationElement>> results = (Map<MarkerQueryResult, Collection<IConfigurationElement>>) table.get(query);
		if (results == null) {
			// Create a new results table
			results = new HashMap<MarkerQueryResult, Collection<IConfigurationElement>>();

			// Add the query to the table
			table.put(query, results);
		}

		if (results.containsKey(result)) {
			Collection<IConfigurationElement> currentElements = (Collection<IConfigurationElement>) results.get(result);
			currentElements.add(element);
		} else {
			Collection<IConfigurationElement> elements = new HashSet<IConfigurationElement>();
			elements.add(element);

			// Add the new result
			results.put(result, elements);
		}
	}
}
