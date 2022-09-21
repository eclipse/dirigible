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
const perspectiveData = {
	id: "guess-my-number", // ID used for sidebar indication and layout settings
	name: "BPM Workspace", // User-facing name
	link: "../ide-bpm-workspace/perspective.html", // Link to the main perspective view
	order: "1000", // Used to sort the tabs in the sidebar
	icon: "/services/v4/web/resources/unicons/process.svg",
};
if (typeof exports !== 'undefined') {
	exports.getPerspective = function () {
		return perspectiveData;
	}
}