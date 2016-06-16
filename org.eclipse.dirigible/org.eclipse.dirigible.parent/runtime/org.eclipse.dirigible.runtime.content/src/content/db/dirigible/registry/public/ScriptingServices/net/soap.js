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

var streams = require("io/streams");
var request = require('net/http/request');

exports.createMessage = function() {
	var internalFactory = javax.xml.soap.MessageFactory.newInstance();
	var internalMessage = internalFactory.createMessage();
	return new Message(internalMessage);
};

exports.parseMessage = function(mimeHeaders, inputStream) {
	var internalFactory = javax.xml.soap.MessageFactory.newInstance();
	if (inputStream.getInternalObject()) {
		try {
			var internalMessage = internalFactory.createMessage(mimeHeaders.getInternalObject(), inputStream.getInternalObject());
			var internalPart = internalMessage.getSOAPPart();
			internalPart.getEnvelope();
			return new Message(internalMessage);
		} catch(e) {
			console.error(e);
			throw new Error("Input provided is null or in a worng format. HTTP method used must be POST. " + e.message);
		}
	}
	throw new Error("Input provided is null.");
};

exports.parseRequest = function() {
	if (request.getMethod().toUpperCase() !== "POST") {
		throw new Error("HTTP method used must be POST.");	
	}

	var inputStream = request.getInput();
	var mimeHeaders = exports.createMimeHeaders();
	return exports.parseMessage(mimeHeaders, inputStream);
};

exports.createMimeHeaders = function() {
	var internalMimeHeaders = javax.xml.soap.MimeHeaders();
	return new MimeHeaders(internalMimeHeaders);
};


/**
 * SOAP Message
 */
function Message(internalMessage) {
	this.internalMessage = internalMessage;
	this.getInternalObject = messageGetInternalObject;
	this.getPart = messageGetPart;
	this.getMimeHeaders = messageGetMimeHeaders;
	this.save = messageSaveChanges;
	this.getText = messageGetText;
}

function messageGetInternalObject() {
	return this.internalMessage;
}

function messageGetPart() {
	var internalPart = this.internalMessage.getSOAPPart();
	return new Part(internalPart);
}

function messageGetMimeHeaders() {
	var internalMimeHeaders = this.internalMessage.getMimeHeaders();
	return new MimeHeaders(internalMimeHeaders);
}

function messageSaveChanges() {
	this.internalMessage.saveChanges();
}

function messageGetText() {
	var outputStream = streams.createByteArrayOutputStream();
	this.internalMessage.writeTo(outputStream.getInternalObject());
	return outputStream.getText();
}

/**
 * SOAP Part
 */
function Part(internalPart) {
	this.internalPart = internalPart;
	this.getInternalObject = partGetInternalObject;
	this.getEnvelope = partGetEnvelope;
}

function partGetInternalObject() {
	return this.internalPart;
}

function partGetEnvelope() {
	var internalEnvelope = this.internalPart.getEnvelope();
	return new Envelope(internalEnvelope);
}

/**
 * SOAP Mime Headers
 */
function MimeHeaders(internalMimeHeaders) {
	this.internalMimeHeaders = internalMimeHeaders;
	this.getInternalObject = mimeHeadersGetInternalObject;
	this.addHeader = mimeHeadersAddHeader;
}

function mimeHeadersGetInternalObject() {
	return this.internalMimeHeaders;
}

function mimeHeadersAddHeader(name, value) {
	this.internalMimeHeaders.addHeader(name, value);
}

/**
 * SOAP Envelope
 */
function Envelope(internalEnvelope) {
	this.internalEnvelope = internalEnvelope;
	this.getInternalObject = envelopeGetInternalObject;
	this.addNamespaceDeclaration = envelopeAddNamespaceDeclaration;
	this.getBody = envelopeGetBody;
	this.getHeader = envelopeGetHeader;
	this.createName = envelopeCreateName;
}

function envelopeGetInternalObject() {
	return this.internalEnvelope;
}

function envelopeAddNamespaceDeclaration(prefix, uri) {
	this.internalEnvelope.addNamespaceDeclaration(prefix, uri);
}

function envelopeGetBody() {
	var internalBody = this.internalEnvelope.getBody();
	return new Body(internalBody);
}

function envelopeGetHeader() {
	var internalHeader = this.internalEnvelope.getHeader();
	return new Header(internalHeader);
}

function envelopeCreateName(localName, prefix, uri) {
	var internalName = this.internalEnvelope.createName(localName, prefix, uri);
	return new Name(internalName);
}

/**
 * SOAP Body
 */
function Body(internalBody) {
	this.internalBody = internalBody;
	this.getInternalObject = bodyGetInternalObject;
	this.addChildElement = bodyAddChildElement;
	this.getChildElements = bodyGetChildElements;
}

function bodyGetInternalObject() {
	return this.internalBody;
}

function bodyAddChildElement(localName, prefix) {
	var internalElement = this.internalBody.addChildElement(localName, prefix);
	return new Element(internalElement);
}

function bodyGetChildElements() {
	var childElements = [];
	var internalElementsIterator = this.internalBody.getChildElements();
	while (internalElementsIterator.hasNext()) {
		var internalElement = internalElementsIterator.next();
		childElements.push(new Element(internalElement));
	}
	return childElements;
}

/**
 * SOAP Header
 */
function Header(internalHeader) {
	this.internalHeader = internalHeader;
	this.getInternalObject = headerGetInternalObject;
	this.addHeaderElement = headerAddHeaderElement;
}

function headerGetInternalObject() {
	return this.internalNeader;
}

function headerAddHeaderElement(name) {
	var internalElement = this.internalHeader.addHeaderElement(name.getInternalObject());
	return new Element(internalElement);
}

/**
 * SOAP Name
 */
function Name(internalName) {
	this.internalName = internalName;
	this.getInternalObject = nameGetInternalObject;
	this.getLocalName = nameGetLocalName;
	this.getPrefix = nameGetPrefix;
	this.getQualifiedName = nameGetQualifiedName;
	this.getURI = nameGetURI;
}

function nameGetInternalObject() {
	return this.internalName;
}

function nameGetLocalName() {
	return this.internalName.getLocalName();
}

function nameGetPrefix() {
	return this.internalName.getPrefix();
}

function nameGetQualifiedName() {
	return this.internalName.getQualifiedName();
}

function nameGetURI() {
	return this.internalName.getURI();
}

/**
 * SOAP Element
 */
function Element(internalElement) {
	this.internalElement = internalElement;
	this.getInternalObject = elementGetInternalObject;
	this.addChildElement = elementAddChildElement;
	this.addTextNode = elementAddTextNode;
	this.addAttribute = elementAddAttribute;
	this.getChildElements = elementGetChildElements;
	this.getElementName = elementGetElementName;
	this.getValue = elementGetValue;
	this.isSOAPElement = elementIsSOAPElement;
}

function elementGetInternalObject() {
	return this.internalElement;
}

function elementAddChildElement(localName, prefix) {
	var internalElement = this.internalElement.addChildElement(localName, prefix);
	return new Element(internalElement);
}

function elementAddTextNode(text) {
	var internalElement = this.internalElement.addTextNode(text);
	return new Element(internalElement);
}

function elementAddAttribute(name, value) {
	var internalElement = this.internalElement.addAttribute(name.getInternalObject(), value);
	return new Element(internalElement);
}

function elementGetChildElements() {
	var childElements = [];
	var internalElementsIterator = this.internalElement.getChildElements();
	while (internalElementsIterator.hasNext()) {
		var internalElement = internalElementsIterator.next();
		childElements.push(new Element(internalElement));
	}
	return childElements;
}

function elementGetElementName() {
	try {
		var internalName = this.internalElement.getElementName();
		return new Name(internalName);
	} catch(e) {
	//  can we assume that always an exception here means the element is not an SOAPElement
	//	console.log(e);
	}
	return null;
}

function elementGetValue() {
	return this.internalElement.getValue();
}

function elementIsSOAPElement() {
	return this.getElementName() !== null;
}


/**
 * Call a given SOAP endpoint with a given request message
 */
exports.call = function(message, url) {
	var soapConnectionFactory = javax.xml.soap.SOAPConnectionFactory.newInstance();
	var internalConnection = soapConnectionFactory.createConnection();
	var internalResponse = internalConnection.call(message.getInternalObject(), url);
	return new Message(internalResponse);
};

exports.trustAll = function() {
	// TODO
};
