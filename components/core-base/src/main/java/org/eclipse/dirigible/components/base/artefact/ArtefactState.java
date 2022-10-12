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
package org.eclipse.dirigible.components.base.artefact;

/**
 * The Enum ArtefactState.
 */
public enum ArtefactState {
	
		/** The initial. */
		INITIAL("Initial"), //

		/** The successful. */
		SUCCESSFUL("Successful"),
		/** The successful create. */
		//
		SUCCESSFUL_CREATE("Successful"),
		/** The successful update. */
		//
		SUCCESSFUL_UPDATE("Successful"),
		/** The successful create update. */
		//
		SUCCESSFUL_CREATE_UPDATE("Successful"),
		/** The successful delete. */
		//
		SUCCESSFUL_DELETE("Successful"), //

		/** The failed. */
		FAILED("Failed"),
		/** The failed create. */
		//
		FAILED_CREATE("Failed"),
		/** The failed update. */
		//
		FAILED_UPDATE("Failed"),
		/** The failed create update. */
		//
		FAILED_CREATE_UPDATE("Failed"),
		/** The failed delete. */
		//
		FAILED_DELETE("Failed"), //

		/** The in progress. */
		IN_PROGRESS("Processing..."), //

		/** The fatal. */
		FATAL("Erroneous"),

		/** The unknown. */
		UNKNOWN("Unknown");

		/** The state. */
		private String state;

		/**
		 * Instantiates a new artefact state.
		 *
		 * @param state the state
		 */
		ArtefactState(String state) {
			this.state = state;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public String getValue() {
			return this.state;
		}

}
