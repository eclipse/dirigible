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

const Properties = Java.type("java.util.Properties");
const MailFacade = Java.type("org.eclipse.dirigible.components.api.mail.MailFacade");

export function getClient(options: object): MailClient {
    let native = null;

    if (options) {
        native = MailFacade.getInstance(toJavaProperties(options));
    } else {
        native = MailFacade.getInstance();
    }
    return new MailClient(native);
};

export function sendMultipart(from, recipients, subject, parts) {
    const mailClient = this.getClient();
    mailClient.sendMultipart(from, recipients, subject, parts);
};

export function send(from, recipients, subject, text: string, subType: string) {
    const mailClient = this.getClient();
    mailClient.send(from, recipients, subject, text, subType);
};

class MailClient {
    constructor(private native: any) {}

    send(from: object, _recipients: object | string, subject: object, text: string, contentType: string): void {
        switch (contentType) {
            case "html":
                contentType = "text/html";
                break;
            case "plain":
                contentType = "text/plain";
                break
        }

        const recipients = processRecipients(_recipients);

        const part = {
            contentType: contentType,
            text: text,
            type: 'text'
        };

        try {
            this.native.send(from, recipients.to, recipients.cc, recipients.bcc, subject, [part]);
        } catch (error) {
            console.error(error.message);
            throw new Error(error);
        }
    };

    sendMultipart(from: object, _recipients: object | string, subject: object, parts: {}): void {
        let recipients = processRecipients(_recipients);
        try {
            return this.native.send(from, recipients.to, recipients.cc, recipients.bcc, subject, stringifyPartData(parts));
        } catch (error) {
            console.error(error.message);
            throw new Error(error);
        }
    };
}

function stringifyPartData(parts: any): JSON {
    parts.forEach(function (part: any) {
        if (part.data) {
            part.data = JSON.stringify(part.data);
        }
        return part;
    })
    return parts;
}

function processRecipients(recipients: string | object) {
    let to = [];
    let cc = [];
    let bcc = [];
    if (typeof recipients === "string") {
        to.push(recipients);
    } else if (typeof recipients === "object") {
        to = parseRecipients(recipients, "to");
        cc = parseRecipients(recipients, "cc");
        bcc = parseRecipients(recipients, "bcc");
    } else {
        const errorMessage = "Invalid 'recipients' format: " + JSON.stringify(recipients);
        console.error(errorMessage);
        throw new Error(errorMessage);
    }

    return {to: to, cc: cc, bcc: bcc};
}

function toJavaProperties(properties: any) {
    const javaProperties = new Properties();
    Object.keys(properties).forEach(function (e) {
        javaProperties.put(e, properties[e]);
    });
    return javaProperties;
}

function parseRecipients(recipients: object, type: any): any {
    const objectType = typeof recipients[type];
    if (objectType === "string") {
        return [recipients[type]];
    } else if (Array.isArray(recipients[type])) {
        return recipients[type];
    } else if (objectType === "undefined") {
        return [];
    }
    const errorMessage = "Invalid 'recipients." + type + "' format: [" + recipients[type] + "|" + objectType + "]";
    console.error(errorMessage);
    throw new Error(errorMessage);
}
