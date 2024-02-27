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
 * API Files
 */
import * as streams from "sdk/io/streams";
import * as bytes from "sdk/io/bytes";
const FilesFacade = Java.type("org.eclipse.dirigible.components.api.io.FilesFacade");
const File = Java.type("java.io.File")

export function exists(path){
	return FilesFacade.exists(path);
};

export function isExecutable(path) {
	return FilesFacade.isExecutable(path);
};

export function isReadable(path) {
	return FilesFacade.isReadable(path);
};

export function isWritable(path) {
	return FilesFacade.isWritable(path);
};

export function isHidden(path) {
	return FilesFacade.isHidden(path);
};

export function isDirectory(path) {
	return FilesFacade.isDirectory(path);
};

export function isFile(path) {
	return FilesFacade.isFile(path);
};

export function isSameFile(path1, path2) {
	return FilesFacade.isSameFile(path1, path2);
};

export function getCanonicalPath(path) {
	return FilesFacade.getCanonicalPath(path);
};

export function getName(path) {
	return FilesFacade.getName(path);
};

export function getParentPath(path) {
	return FilesFacade.getParentPath(path);
};

export function readBytes(path){
	const native = FilesFacade.readBytes(path);
	const data = bytes.toJavaScriptBytes(native);
	return data;
};

export function readBytesNative(path){
	return FilesFacade.readBytes(path);
};

export function readText(path){
	return FilesFacade.readText(path);
};

export function writeBytes(path, data){
	const native = bytes.toJavaBytes(data);
	FilesFacade.writeBytesNative(path, native);
};

export function writeBytesNative(path, data){
	FilesFacade.writeBytesNative(path, data);
};

export function writeText(path, text) {
	FilesFacade.writeText(path, text);
};

export function getLastModified(path) {
	return new Date(FilesFacade.getLastModified(path));
};

export function setLastModified(path, time) {
	FilesFacade.setLastModified(path, time.getMilliseconds());
};

export function getOwner(path) {
	return FilesFacade.getOwner(path);
};

export function setOwner(path, owner) {
	FilesFacade.setOwner(path, owner);
};

export function getPermissions(path) {
	return FilesFacade.getPermissions(path);
};

export function setPermissions(path, permissions) {
	FilesFacade.setPermissions(path, permissions);
};

export function size(path) {
	return FilesFacade.size(path);
};

export function createFile(path) {
	FilesFacade.createFile(path);
};

export function createDirectory(path) {
	FilesFacade.createDirectory(path);
};

export function copy(source, target) {
	FilesFacade.copy(source, target);
};

export function move(source, target) {
	FilesFacade.move(source, target);
};

export function deleteFile(path) {
	FilesFacade.deleteFile(path);
};

export function deleteDirectory(path, forced) {
	FilesFacade.deleteDirectory(path, forced);
};

export function createTempFile(prefix, suffix) {
	return FilesFacade.createTempFile(prefix, suffix);
};

export function createTempDirectory(prefix) {
	return FilesFacade.createTempDirectory(prefix);
};

export function createInputStream(path) {
	const native = FilesFacade.createInputStream(path);
	return new streams.InputStream(native);
};

export function createOutputStream(path) {
	const native = FilesFacade.createOutputStream(path);
	return new streams.OutputStream(native);
};

export function traverse(path) {
	return FilesFacade.traverse(path);
};

export function list(path) {
	return JSON.parse(FilesFacade.list(path)).map(e => e.path);
};

export function find(path, pattern) {
	return JSON.parse(FilesFacade.find(path, pattern));
};

export const separator = File.separator;
