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
package org.eclipse.dirigible.core.scheduler.api;

public interface ISynchronizerArtefactType {
	
	public enum ArtefactState {
		INITIAL("Initial"), //

		SUCCESSFUL("Successful"),  //
		SUCCESSFUL_CREATE("Successful"), //
		SUCCESSFUL_UPDATE("Successful"), //
		SUCCESSFUL_CREATE_UPDATE("Successful"), //
		SUCCESSFUL_DELETE("Successful"), //

		FAILED("Failed"), //
		FAILED_CREATE("Failed"), //
		FAILED_UPDATE("Failed"), //
		FAILED_CREATE_UPDATE("Failed"), //
		FAILED_DELETE("Failed"), //

		IN_PROGRESS("Processing..."), //

		FATAL("Erroneous"),

		UNKNOWN("Unknown");

		private String state;

		ArtefactState(String state) {
			this.state = state;
		}

		public String getValue() {
			return this.state;
		}
	}

	public String getId();

	public String getStateMessage(ArtefactState state);

	public String getStateMessage(ArtefactState state, String message);
}
