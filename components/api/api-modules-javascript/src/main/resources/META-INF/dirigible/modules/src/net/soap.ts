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
import * as streams from "sdk/io/streams";
import * as request from "sdk/http/request";
import * as base64 from "sdk/utils/base64";
const MessageFactory = Java.type("jakarta.xml.soap.MessageFactory");
const MimeHeadersInternal = Java.type("jakarta.xml.soap.MimeHeaders");
const SOAPConnectionFactory = Java.type("jakarta.xml.soap.SOAPConnectionFactory");

export function createMessage() {
	const internalFactory = MessageFactory.newInstance();
	const internalMessage = internalFactory.createMessage();
	return new Message(internalMessage);
};

export function parseMessage(mimeHeaders, inputStream) {
	const internalFactory = MessageFactory.newInstance();
	if (inputStream.native) {
		try {
			const internalMessage = internalFactory.createMessage(mimeHeaders.native, inputStream.native);
			const internalPart = internalMessage.getSOAPPart();
			internalPart.getEnvelope();
			return new Message(internalMessage);
		} catch (e) {
			console.error(e);
			throw new Error("Input provided is null or in a worng format. HTTP method used must be POST. " + e.message);
		}
	}
	throw new Error("Input provided is null.");
};

export function parseRequest() {
	if (request.getMethod().toUpperCase() !== "POST") {
		throw new Error("HTTP method used must be POST.");
	}

	const inputStream = request.getInputStream();
	const mimeHeaders = {};//export function createMimeHeaders();
	return parseMessage(mimeHeaders, inputStream);
};

export function createMimeHeaders() {
	const internalMimeHeaders = new MimeHeadersInternal();
	return new MimeHeaders(internalMimeHeaders);
};


/**
 * SOAP Message
 */
class Message {

	constructor(private native) { }

	getPart() {
		const internalPart = this.native.getSOAPPart();
		return new Part(internalPart);
	};

	getMimeHeaders() {
		const internalMimeHeaders = this.native.getMimeHeaders();
		return new MimeHeaders(internalMimeHeaders);
	};

	save() {
		this.native.saveChanges();
	};

	getText() {
		const outputStream = streams.createByteArrayOutputStream();
		this.native.writeTo(outputStream.native);
		return outputStream.getText();
	};
}

/**
 * SOAP Part
 */
class Part {

	constructor(private native) { }

	getEnvelope() {
		const internalEnvelope = this.native.getEnvelope();
		return new Envelope(internalEnvelope);
	};
}

/**
 * SOAP Mime Headers
 */
class MimeHeaders {

	constructor(private native) {}

	addHeader(name, value) {
		this.native.addHeader(name, value);
	};

	addBasicAuthenticationHeader(username, password) {
		const userAndPassword = `${username}:${password}`;
		const basicAuth = base64.encode(userAndPassword);
		this.native.addHeader("Authorization", "Basic " + basicAuth);
	};
}

/**
 * SOAP Envelope
 */
class Envelope {

	constructor(private native) { }

	addNamespaceDeclaration(prefix, uri) {
		this.native.addNamespaceDeclaration(prefix, uri);
	};

	getBody() {
		const internalBody = this.native.getBody();
		return new Body(internalBody);
	};

	getHeader() {
		const internalHeader = this.native.getHeader();
		return new Header(internalHeader);
	};

	createName(localName, prefix, uri) {
		const internalName = this.native.createName(localName, prefix, uri);
		return new Name(internalName);
	};
}

/**
 * SOAP Body
 */
class Body {

	constructor(private native) {
	}

	addChildElement(localName, prefix) {
		const internalElement = this.native.addChildElement(localName, prefix);
		return new Element(internalElement);
	};

	getChildElements() {
		const childElements = [];
		const internalElementsIterator = this.native.getChildElements();
		while (internalElementsIterator.hasNext()) {
			const internalElement = internalElementsIterator.next();
			childElements.push(new Element(internalElement));
		}
		return childElements;
	};
}

/**
 * SOAP Header
 */
class Header {

	constructor(private native) { }

	addHeaderElement(name) {
		const internalElement = this.native.addHeaderElement(name.native);
		return new Element(internalElement);
	};
}

/**
 * SOAP Name
 */
class Name {

	constructor(private native) {
	}

	getLocalName() {
		return this.native.getLocalName();
	};

	getPrefix() {
		return this.native.getPrefix();
	};

	getQualifiedName() {
		return this.native.getQualifiedName();
	};

	getURI() {
		return this.native.getURI();
	};
}

/**
 * SOAP Element
 */
class Element {

	constructor(private native) { }

	addChildElement(localName, prefix) {
		const internalElement = this.native.addChildElement(localName, prefix);
		return new Element(internalElement);
	};

	addTextNode(text) {
		const internalElement = this.native.addTextNode(text);
		return new Element(internalElement);
	};

	addAttribute(name, value) {
		const internalElement = this.native.addAttribute(name.native, value);
		return new Element(internalElement);
	};

	getChildElements() {
		const childElements = [];
		const internalElementsIterator = this.native.getChildElements();
		while (internalElementsIterator.hasNext()) {
			const internalElement = internalElementsIterator.next();
			childElements.push(new Element(internalElement));
		}
		return childElements;
	};

	getElementName() {
		try {
			const internalName = this.native.getElementName();
			return new Name(internalName);
		} catch (e) {
			//  can we assume that always an exception here means the element is not an SOAPElement
			//	console.log(e);
		}
		return null;
	};

	getValue() {
		return this.native.getValue();
	};

	isSOAPElement() {
		return this.getElementName() !== null;
	};
}

/**
 * Call a given SOAP endpoint with a given request message
 */
export function call(message, url) {
	const soapConnectionFactory = SOAPConnectionFactory.newInstance();
	const internalConnection = soapConnectionFactory.createConnection();
	const internalResponse = internalConnection.call(message.native, url);
	return new Message(internalResponse);
};

export function trustAll() {
	// TODO
};
