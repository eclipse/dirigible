/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

exports.getClient = function(options) {
	var native = null;
	if (options) {
		native = new org.eclipse.dirigible.api.v3.mail.MailFacade.getInstance(toJavaProperties(options));
	} else {
		native = new org.eclipse.dirigible.api.v3.mail.MailFacade.getInstance();
	}
	return new MailClient(native);
};

exports.send = function(from, recipients, subject, text, subType) {
	var mailClient = this.getClient();
	mailClient.send(from, recipients, subject, text, subType);
};

function MailClient(native) {
	this.native = native;

	this.send = function(from, recipients, subject, text, subType) {
		var to = [];
		var cc = [];
		var bcc = [];
		if (typeof recipients === "string") {
			to.push(recipients);
		} else if (typeof recipients === "object") {
			to = parseRecipients(recipients, "to");
			cc = parseRecipients(recipients, "cc");
			bcc = parseRecipients(recipients, "bcc");
		} else {
			var errorMessage = "Invalid 'recipients' format: " + JSON.stringify(recipients);
			console.error(errorMessage);
			throw new Error(errorMessage);
		}
		this.native.send(from, to, cc, bcc, subject, text, subType ? subType : "plain");
	};
}

function toJavaProperties(properties) {
	var javaProperties = new java.util.Properties();
	Object.keys(properties).forEach(function(e) {
		javaProperties.put(e, properties[e]);
	});
	return javaProperties;
}

function parseRecipients(recipients, type) {
	var objectType = typeof recipients[type];
	if (objectType === "string") {
		return [recipients[type]];
	} else if (Array.isArray(recipients[type])) {
		return recipients[type];
	} else if (objectType === "undefined") {
		return [];
	}
	var errorMessage = "Invalid 'recipients." + type + "' format: [" + recipients[type] + "|" + objectType + "]";
	console.error(errorMessage);
	throw new Error(errorMessage);
}
