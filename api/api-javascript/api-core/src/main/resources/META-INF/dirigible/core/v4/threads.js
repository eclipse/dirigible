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
/**
 * API v4 Threads
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

/**
 * Creates a new Thread by a runnable function and a name
 */
exports.create = function(runnable, name) {
	if (typeof runnable !== "function") {
		throw new Error("Provided runnable is not a function!");
	}
	return new Thread(new java.lang.Thread(runnable, name));
};

/**
 * Thread object
 */
function Thread(internalThread) {
	this.internalThread = internalThread;
	this.getInternalObject = threadGetInternalObject;
	this.start = threadStart;
	this.interrupt = threadInterrupt;
	this.join = threadJoin;
	this.getId = threadGetId;
	this.getName = threadGetName;
	this.isAlive = threadIsAlive;
}

function threadGetInternalObject() {
	return this.internalThread;
}

function threadStart() {
	return this.internalThread.start();
}

function threadInterrupt() {
	return this.internalThread.interrupt();
}

function threadJoin() {
	return this.internalThread.join();
}

function threadGetId() {
	return this.internalThread.getId();
}

function threadGetName() {
	return this.internalThread.getName();
}

function threadIsAlive() {
	return this.internalThread.isAlive();
}


exports.sleep = function(millis) {
	java.lang.Thread.sleep(millis);
};

exports.current = function() {
	java.lang.Thread.currentThread();
};

function getClassObject(clazz) {
	var objClazz;
	if (engine === "nashorn") {
		let objType = Java.type(clazz);
		objClazz = new objType().getClass();
	} else {
		objClazz = java.lang.Class.forName(clazz);
	}
	return objClazz;
}

Object.prototype.wait = function(millis) {
	if (millis) {
		var objClazz = getClassObject('java.lang.Object');
		var waitMethod = objClazz.getMethod('wait', java.lang.Long.TYPE);
		waitMethod.invoke(this, new java.lang.Long(millis));
	}
};

Object.prototype.notify = function() {
	var objClazz = getClassObject('java.lang.Object');
	var notifyMethod = objClazz.getMethod('notify', null);
	notifyMethod.invoke(this, null);
};

Object.prototype.notifyAll = function() {
	var objClazz = getClassObject('java.lang.Object');
	var notifyAllMethod = objClazz.getMethod('notifyAll', null);
	notifyAllMethod.invoke(this, null);
};

exports.sync = function (f) {
	return new Packages.org.mozilla.javascript.Synchronizer(f);
};

//// sync
//Object.prototype.sync = function(func, syncobj) {
//        if (arguments.length < 1 || arguments.length > 2 ) {
//            throw "sync(function [,object]) parameter count mismatch";
//        }
//        if (__engine === "nashorn") {
//        	return Java.synchronized(func, syncobj);
//    	}
//    	return new Packages.org.mozilla.javascript.Synchronizer(func);
//};
