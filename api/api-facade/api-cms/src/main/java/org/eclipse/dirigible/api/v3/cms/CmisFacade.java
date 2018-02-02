/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.cms;

import org.eclipse.dirigible.cms.api.CmsModule;

public class CmisFacade {
	
	public static final String VERSIONING_STATE_NONE = "none";
	public static final String VERSIONING_STATE_MAJOR = "major";
	public static final String VERSIONING_STATE_MINOR = "minor";
	public static final String VERSIONING_STATE_CHECKEDOUT = "checkedout";
	
	/**
	 * CMIS Session
	 *
	 * @return the CMIS session object
	 */
	public static final Object getSession() {
		Object session = CmsModule.getSession();
		return session;
	}
	
	/**
	 * Mapping utility between the CMIS standard and Javascript string representation of the versioning state
	 * @param state the Javascript state
	 * @return the CMIS state
	 */
	public static final Object getVersioningState(String state) {
		if (VERSIONING_STATE_NONE.equals(state)) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.NONE;
		} else if (VERSIONING_STATE_MAJOR.equals(state)) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR;
		} else if (VERSIONING_STATE_MINOR.equals(state)) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.MINOR;
		}  else if (VERSIONING_STATE_CHECKEDOUT.equals(state)) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.CHECKEDOUT;
		}
		return org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR;
	}
	
	public static final Object getUnifiedObjectDelete() {
		return org.apache.chemistry.opencmis.commons.enums.UnfileObject.DELETE;
	}

}
