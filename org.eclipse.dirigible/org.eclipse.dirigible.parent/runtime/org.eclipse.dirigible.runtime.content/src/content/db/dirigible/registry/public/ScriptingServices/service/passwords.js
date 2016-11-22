/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ */
/* eslint-env node, dirigible */

var env = require('core/env');
var streams = require('io/streams');

const PASSWORD_STORAGE_KEY = 'PasswordStorage';
const JNDI_PASSWORD_STORAGE = 'jndiPasswordStorage';

exports.setPassword = function(alias, password) {
	getPasswordStorage().setPassword(alias, password.split(''));
};
 
exports.getPassword = function(alias){
	var password = null;
	var passwordArray = getPasswordStorage().getPassword(alias);
	if (passwordArray !== null && passwordArray.length !== 0) {
		password = streams.byteArrayToText(passwordArray);
	}
	return password;
};
 
exports.deletePassword = function(alias) {
    return getPasswordStorage().deletePassword(alias);
};

function getPasswordStorage() {
	var passwordStorage = env.get(PASSWORD_STORAGE_KEY);
	if (passwordStorage === null) {
		try {
			var passwordStorageService = env.get(JNDI_PASSWORD_STORAGE);
			passwordStorage = $.getInitialContext().lookup(passwordStorageService);
			if (passwordStorage === null) {
				console.error('Password Storage is null!');
				throw new Error('Password Storage is null!');
			}
		} catch (e) {
			var message = 'Password Storage lookup failed! ' + e;
			console.log(message);
			throw new Error(message);
		}
	}
	return passwordStorage;
}
