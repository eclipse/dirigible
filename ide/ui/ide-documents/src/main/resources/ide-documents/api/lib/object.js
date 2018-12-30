/*
 * Copyright (c) 2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

var cmis = require('cms/v3/cmis');
var cmisSession = cmis.getSession();

exports.getObject = function(path){
	try {
		if (path === null || path === undefined) {
			return null;
		}		
		return cmisSession.getObjectByPath(path);
	} catch(e) {
		console.error('Error [%s] in getting an object by path [%s]', e.message, path);
	}
	return null;
};

exports.getById = function(id) {
	return cmisSession.getObject(id);
};

exports.deleteObject = function(object){
	object.delete();
};

exports.renameObject = function(object, newName){
	object.rename(newName);
};
