/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
const j = require("jasmine/jasmine");
const jasmine = j.core(j);
const env = jasmine.getEnv();
const junitReporter = require("/jasmine/reporters/junit_reporter");


const $$j = j.interface(jasmine, env);

$$j.describe("A suite is just a function", function () {
	$$j.it("and has a positive case", function () {

		$$j.expect(false).toBe(true);
	});

	$$j.it("and can have a negative case", function () {
		$$j.expect(false).not.toBe(true);
	});

});

env.addReporter(new junitReporter.JUnitXmlReporter({
	savePath: "/test-results",
	filePrefix: "report",
	consolidateAll: true
}));

env.execute();
