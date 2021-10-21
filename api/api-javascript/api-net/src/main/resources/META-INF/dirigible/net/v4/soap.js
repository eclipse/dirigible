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
var streams = require("io/v4/streams");
var request = require('http/v4/request');
var base64 = require("utils/v4/base64");

exports.createMessage = function () {
    var internalFactory = javax.xml.soap.MessageFactory.newInstance();
    var internalMessage = internalFactory.createMessage();
    return new Message(internalMessage);
};

exports.parseMessage = function (mimeHeaders, inputStream) {
    var internalFactory = javax.xml.soap.MessageFactory.newInstance();
    if (inputStream.native) {
        try {
            var internalMessage = internalFactory.createMessage(mimeHeaders.native, inputStream.native);
            var internalPart = internalMessage.getSOAPPart();
            internalPart.getEnvelope();
            return new Message(internalMessage);
        } catch (e) {
            console.error(e);
            throw new Error("Input provided is null or in a worng format. HTTP method used must be POST. " + e.message);
        }
    }
    throw new Error("Input provided is null.");
};

exports.parseRequest = function () {
    if (request.getMethod().toUpperCase() !== "POST") {
        throw new Error("HTTP method used must be POST.");
    }

    var inputStream = request.getInputStream();
    var mimeHeaders = {};//exports.createMimeHeaders();
    return exports.parseMessage(mimeHeaders, inputStream);
};

exports.createMimeHeaders = function () {
    var internalMimeHeaders = javax.xml.soap.MimeHeaders();
    return new MimeHeaders(internalMimeHeaders);
};


/**
 * SOAP Message
 */
function Message(internalMessage) {
    this.internalMessage = internalMessage;
    this.native = internalMessage;

    this.getPart = function () {
        var internalPart = this.internalMessage.getSOAPPart();
        return new Part(internalPart);
    };

    this.getMimeHeaders = function () {
        var internalMimeHeaders = this.internalMessage.getMimeHeaders();
        return new MimeHeaders(internalMimeHeaders);
    };

    this.save = function () {
        this.internalMessage.saveChanges();
    };

    this.getText = function () {
        var outputStream = streams.createByteArrayOutputStream();
        this.internalMessage.writeTo(outputStream.native);
        return outputStream.getText();
    };
}

/**
 * SOAP Part
 */
function Part(internalPart) {
    this.internalPart = internalPart;
    this.native = internalPart;

    this.getEnvelope = function () {
        var internalEnvelope = this.internalPart.getEnvelope();
        return new Envelope(internalEnvelope);
    };
}

/**
 * SOAP Mime Headers
 */
function MimeHeaders(internalMimeHeaders) {
    this.internalMimeHeaders = internalMimeHeaders;
    this.native = internalMimeHeaders;

    this.addHeader = function (name, value) {
        this.internalMimeHeaders.addHeader(name, value);
    };

    this.addBasicAuthenticationHeader = function (username, password) {
        var userAndPassword = `${username}:${password}`;
        var basicAuth = base64.encode(userAndPassword);
        this.internalMimeHeaders.addHeader("Authorization", "Basic " + basicAuth);
    };
}

/**
 * SOAP Envelope
 */
function Envelope(internalEnvelope) {
    this.internalEnvelope = internalEnvelope;
    this.native = internalEnvelope;

    this.addNamespaceDeclaration = function (prefix, uri) {
        this.internalEnvelope.addNamespaceDeclaration(prefix, uri);
    };

    this.getBody = function () {
        var internalBody = this.internalEnvelope.getBody();
        return new Body(internalBody);
    };

    this.getHeader = function () {
        var internalHeader = this.internalEnvelope.getHeader();
        return new Header(internalHeader);
    };

    this.createName = function (localName, prefix, uri) {
        var internalName = this.internalEnvelope.createName(localName, prefix, uri);
        return new Name(internalName);
    };
}

/**
 * SOAP Body
 */
function Body(internalBody) {
    this.internalBody = internalBody;
    this.native = internalBody;

    this.addChildElement = function (localName, prefix) {
        var internalElement = this.internalBody.addChildElement(localName, prefix);
        return new Element(internalElement);
    };

    this.getChildElements = function () {
        var childElements = [];
        var internalElementsIterator = this.internalBody.getChildElements();
        while (internalElementsIterator.hasNext()) {
            var internalElement = internalElementsIterator.next();
            childElements.push(new Element(internalElement));
        }
        return childElements;
    };
}

/**
 * SOAP Header
 */
function Header(internalHeader) {
    this.internalHeader = internalHeader;
    this.native = internalHeader;

    this.addHeaderElement = function (name) {
        var internalElement = this.internalHeader.addHeaderElement(name.native);
        return new Element(internalElement);
    };
}

/**
 * SOAP Name
 */
function Name(internalName) {
    this.internalName = internalName;
    this.native = internalName;

    this.getLocalName = function () {
        return this.internalName.getLocalName();
    };

    this.getPrefix = function () {
        return this.internalName.getPrefix();
    };

    this.getQualifiedName = function () {
        return this.internalName.getQualifiedName();
    };

    this.getURI = function () {
        return this.internalName.getURI();
    };
}

/**
 * SOAP Element
 */
function Element(element) {
    this.internalElement = element;
    this.native = element;

    this.addChildElement = function (localName, prefix) {
        var internalElement = this.internalElement.addChildElement(localName, prefix);
        return new Element(internalElement);
    };

    this.addTextNode = function (text) {
        var internalElement = this.internalElement.addTextNode(text);
        return new Element(internalElement);
    };

    this.addAttribute = function (name, value) {
        var internalElement = this.internalElement.addAttribute(name.native, value);
        return new Element(internalElement);
    };

    this.getChildElements = function () {
        var childElements = [];
        var internalElementsIterator = this.internalElement.getChildElements();
        while (internalElementsIterator.hasNext()) {
            var internalElement = internalElementsIterator.next();
            childElements.push(new Element(internalElement));
        }
        return childElements;
    };

    this.getElementName = function () {
        try {
            var internalName = this.internalElement.getElementName();
            return new Name(internalName);
        } catch (e) {
            //  can we assume that always an exception here means the element is not an SOAPElement
            //	console.log(e);
        }
        return null;
    };

    this.getValue = function () {
        return this.internalElement.getValue();
    };

    this.isSOAPElement = function () {
        return this.getElementName() !== null;
    };
}

/**
 * Call a given SOAP endpoint with a given request message
 */
exports.call = function (message, url) {
    var soapConnectionFactory = javax.xml.soap.SOAPConnectionFactory.newInstance();
    var internalConnection = soapConnectionFactory.createConnection();
    var internalResponse = internalConnection.call(message.native, url);
    return new Message(internalResponse);
};

exports.trustAll = function () {
    // TODO
};
