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
var print = function(prefix, result, prettyPrint, severity){
	severity = console[severity] === undefined ? 'info': severity;
	var args = [result];
	if(prettyPrint){
		args.push(null);
		args.push(2);
	}
	console[severity](prefix + JSON.stringify.apply(this, args));
}

exports.settings = {
	
	prettyPrint: true,
	
	specStarted: function(result) {
		print('[Spec started]: ', result, this.prettyPrint);
	},
	specDone: function(result) {
		print('[Spec done]: ', result, this.prettyPrint);
	},
	suiteStarted: function(result) {
		print('[Suite started]: ', result, this.prettyPrint);
	},	
	suiteDone: function(result) {
		print('[Suite done]: ', result, this.prettyPrint);
	},
	jasmineStarted: function(suiteInfo) {
		print('[Jasmine started]: ', suiteInfo, this.prettyPrint);
	},
	jasmineDone: function() {
		console.info('[Jasmine done]');
	}
};