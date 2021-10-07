/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.getClient = function (options) {
    var native = null;
    if (options) {
        native = org.eclipse.dirigible.api.v3.mail.MailFacade.getInstance(toJavaProperties(options));
    } else {
        native = org.eclipse.dirigible.api.v3.mail.MailFacade.getInstance();
    }
    return new MailClient(native);
};

exports.sendMultipart = function (from, recipients, subject, parts) {
    var mailClient = this.getClient();
    mailClient.sendMultipart(from, recipients, subject, parts);
};

exports.send = function (from, recipients, subject, text, subType) {
    var mailClient = this.getClient();
    mailClient.send(from, recipients, subject, text, subType);
};

function MailClient(native) {
    this.native = native;

    this.send = function (from, recipients, subject, text, contentType) {
        switch (contentType) {
            case "html":
                contentType = "text/html";
                break;
            case "plain":
                contentType = "text/plain";
                break
        }

        var recipients = processRecipients(recipients)

        var part = {
            contentType: contentType,
            text: text,
            type: 'text'
        }

        this.native.send(from, recipients.to, recipients.cc, recipients.bcc, subject, [part]);
    };

    this.sendMultipart = function (from, recipients, subject, parts) {
        var recipients = processRecipients(recipients);
        return this.native.send(from, recipients.to, recipients.cc, recipients.bcc, subject, stringifyPartData(parts));
    };
}

function stringifyPartData(parts) {
    parts.forEach(function (part) {
        if (part.data) {
            part.data = JSON.stringify(part.data);
        }
        return part;
    })
    return parts;
}

function processRecipients(recipients) {
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

    return {to: to, cc: cc, bcc: bcc};
}

function toJavaProperties(properties) {
    var javaProperties = new java.util.Properties();
    Object.keys(properties).forEach(function (e) {
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
