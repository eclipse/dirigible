/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */

var java = require('core/v3/java');

exports.get = function(path){
    return new File(path);        
}

function File(path){
    this.path = path;
    
    this.exists = function(){
    	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "exists", [this.path]);
    }
}

exports.readText = function(path){
    return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "readText", [path]);
}