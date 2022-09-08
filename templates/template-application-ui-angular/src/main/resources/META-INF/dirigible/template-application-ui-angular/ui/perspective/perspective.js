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
	id: "{{perspectiveName}}",
	name: "{{perspectiveName}}",
	link: "/services/v4/web/{{projectName}}/gen/ui/{{perspectiveName}}/index.html",
	order: "100",
	// icon: "/services/v4/web/chronos/gen/ui/Timesheets/images/workbench.svg",
};

if (typeof exports !== 'undefined') {
	exports.getPerspective = function () {
		return perspectiveData;
	}
}