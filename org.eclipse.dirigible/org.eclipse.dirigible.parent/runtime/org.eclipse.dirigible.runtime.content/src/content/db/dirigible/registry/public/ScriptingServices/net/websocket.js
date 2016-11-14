/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ javax */
/* eslint-env node, dirigible */

exports.getSession = function() {
	var internalSession = $.getExecutionContext().get("websocket-session");
	return new WebsocketSession(internalSession);
};

exports.getOpenSessions = function() {
	var openSessions = [];
	var internalSessions = $.getExecutionContext().get("websocket-sessions");
	var iterator = internalSessions.iterator();
	while (iterator.hasNext()) {
		var internalSession = iterator.next();
		var session = new WebsocketSession(internalSession);
		openSessions.push(session);
	}
	return openSessions;
};

/**
 * Session object
 */
function WebsocketSession(internalSession) {
	this.internalSession = internalSession;

	this.getInternalObject = function() {
		return this.internalSession;
	};

	this.sendText = function(text) {
		this.internalSession.getBasicRemote().sendText(text);	
	};

	this.sendTextAsync = function(text) {
		this.internalSession.getAsyncRemote().sendText(text);	
	};

	this.getId = function() {
		this.internalSession.getId();	
	};

	this.close = function() {
		this.internalSession.close();	
	};
}