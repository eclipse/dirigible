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
/**
 * API v4 Files
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */
const streams = require("io/v4/streams");
const bytes = require("io/v4/bytes");

exports.exists = function(path){
	return org.eclipse.dirigible.api.v3.io.FilesFacade.exists(path);
};

exports.isExecutable = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.isExecutable(path);
};

exports.isReadable = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.isReadable(path);
};

exports.isWritable = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.isWritable(path);
};

exports.isHidden = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.isHidden(path);
};

exports.isDirectory = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.isDirectory(path);
};

exports.isFile = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.isFile(path);
};

exports.isSameFile = function(path1, path2) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.isSameFile(path1, path2);
};

exports.getCanonicalPath = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.getCanonicalPath(path);
};

exports.getName = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.getName(path);
};

exports.getParentPath = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.getParentPath(path);
};

exports.readBytes = function(path){
	const native = org.eclipse.dirigible.api.v3.io.FilesFacade.readBytes(path);
	const data = bytes.toJavaScriptBytes(native);
	return data;
};

exports.readBytesNative = function(path){
	return org.eclipse.dirigible.api.v3.io.FilesFacade.readBytes(path);
};

exports.readText = function(path){
	return org.eclipse.dirigible.api.v3.io.FilesFacade.readText(path);
};

exports.writeBytes = function(path, data){
	const native = bytes.toJavaBytes(data);
	org.eclipse.dirigible.api.v3.io.FilesFacade.writeBytesNative(path, native);
};

exports.writeBytesNative = function(path, data){
	org.eclipse.dirigible.api.v3.io.FilesFacade.writeBytesNative(path, data);
};

exports.writeText = function(path, text) {
	org.eclipse.dirigible.api.v3.io.FilesFacade.writeText(path, text);
};

exports.getLastModified = function(path) {
	return new Date(org.eclipse.dirigible.api.v3.io.FilesFacade.getLastModified(path));
};

exports.setLastModified = function(path, time) {
	org.eclipse.dirigible.api.v3.io.FilesFacade.setLastModified(path, time.getMilliseconds());
};

exports.getOwner = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.getOwner(path);
};

exports.setOwner = function(path, owner) {
	org.eclipse.dirigible.api.v3.io.FilesFacade.setOwner(path, owner);
};

exports.getPermissions = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.getPermissions(path);
};

exports.setPermissions = function(path, permissions) {
	org.eclipse.dirigible.api.v3.io.FilesFacade.setPermissions(path, permissions);
};

exports.size = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.size(path);
};

exports.createFile = function(path) {
	org.eclipse.dirigible.api.v3.io.FilesFacade.createFile(path);
};

exports.createDirectory = function(path) {
	org.eclipse.dirigible.api.v3.io.FilesFacade.createDirectory(path);
};

exports.copy = function(source, target) {
	org.eclipse.dirigible.api.v3.io.FilesFacade.copy(path, source, target);
};

exports.move = function(source, target) {
	org.eclipse.dirigible.api.v3.io.FilesFacade.move(path, source, target);
};

exports.deleteFile = function(path) {
	org.eclipse.dirigible.api.v3.io.FilesFacade.deleteFile(path);
};

exports.deleteDirectory = function(path, forced) {
	org.eclipse.dirigible.api.v3.io.FilesFacade.deleteDirectory(path, forced);
};

exports.createTempFile = function(prefix, suffix) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.createTempFile(prefix, suffix);
};

exports.createTempDirectory = function(prefix) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.createTempDirectory(prefix);
};

exports.createInputStream = function(path) {
	const inputStream = new streams.InputStream();
	inputStream.native = org.eclipse.dirigible.api.v3.io.FilesFacade.createInputStream(path);
	return inputStream;
};

exports.createOutputStream = function(path) {
	var outputStream = new streams.OutputStream();
	var native = org.eclipse.dirigible.api.v3.io.FilesFacade.createOutputStream(path);
	outputStream.native = native;
	return outputStream;
};

exports.traverse = function(path) {
	return org.eclipse.dirigible.api.v3.io.FilesFacade.traverse(path);
};

exports.list = function(path) {
	return JSON.parse(org.eclipse.dirigible.api.v3.io.FilesFacade.list(path)).map(e => e.path);
};

exports.find = function(path, pattern) {
	return JSON.parse(org.eclipse.dirigible.api.v3.io.FilesFacade.find(path, pattern));
};
