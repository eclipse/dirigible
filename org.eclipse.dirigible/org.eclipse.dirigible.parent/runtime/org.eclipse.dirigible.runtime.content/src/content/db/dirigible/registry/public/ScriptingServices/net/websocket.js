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
	this.getInternalObject = sessionGetInternalObject;
	this.sendText = sessionSendText;
	this.sendTextAsync = sessionSendTextAsync;
	this.getId = sessionGetId;
	this.close = sessionClose;
}

function sessionGetInternalObject() {
	return this.internalSession;
}

function sessionSendText(text) {
	this.internalSession.getBasicRemote().sendText(text);	
}

function sessionSendTextAsync(text) {
	this.internalSession.getAsyncRemote().sendText(text);	
}

function sessionGetId() {
	this.internalSession.getId();	
}

function sessionClose() {
	this.internalSession.close();	
}
