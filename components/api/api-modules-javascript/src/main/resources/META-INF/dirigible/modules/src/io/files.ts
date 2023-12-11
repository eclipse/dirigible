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
import * as streams from "@dirigible/io/streams";
import {bytes} from ".";

const FilesFacade = Java.type("org.eclipse.dirigible.components.api.io.FilesFacade");
const File = Java.type("java.io.File")

export class Files{

	static separator = File.separator;
	
	static exists(path: string): boolean{
		return FilesFacade.exists(path);
	};

	static isExecutable(path: string): boolean {
		return FilesFacade.isExecutable(path);
	};

	static isReadable(path: string): boolean {
		return FilesFacade.isReadable(path);
	};

	static isWritable(path: string): boolean {
		return FilesFacade.isWritable(path);
	};

	static isHidden(path: string): boolean {
		return FilesFacade.isHidden(path);
	};

	static isDirectory(path: string): boolean {
		return FilesFacade.isDirectory(path);
	};

	static isFile(path: string): boolean {
		return FilesFacade.isFile(path);
	};

	static isSameFile(path1: string, path2: string): boolean {
		return FilesFacade.isSameFile(path1, path2);
	};

	static getCanonicalPath(path: string): string {
		return FilesFacade.getCanonicalPath(path);
	};

	static getName(path: string): string {
		return FilesFacade.getName(path);
	};

	static getParentPath(path: string): string {
		return FilesFacade.getParentPath(path);
	};

	static readBytes(path: string): Array<bytes> {
		const native = FilesFacade.readBytes(path);
		const data = bytes.toJavaScriptBytes(native);
		return data;
	};

	static readBytesNative(path: string): Array<bytes>{
		return FilesFacade.readBytes(path);
	};

	static readText(path: string): string{
		return FilesFacade.readText(path);
	};

	static writeBytes(path: string, data): void{
		const native = bytes.toJavaBytes(data);
		FilesFacade.writeBytesNative(path, native);
	};

	static writeBytesNative(path: string, data): void{
		FilesFacade.writeBytesNative(path, data);
	};

	static writeText(path: string, text: string): void {
		FilesFacade.writeText(path, text);
	};

	static getLastModified(path: string): Date {
		return new Date(FilesFacade.getLastModified(path));
	};

	static setLastModified(path: string, time: Date): void {
		FilesFacade.setLastModified(path, time.getMilliseconds());
	};

	static getOwner(path: string): string {
		return FilesFacade.getOwner(path);
	};

	static setOwner(path: string, owner: string): void {
		FilesFacade.setOwner(path, owner);
	};

	static getPermissions(path: string): string {
		return FilesFacade.getPermissions(path);
	};

	static setPermissions(path: string, permissions: string): void {
		FilesFacade.setPermissions(path, permissions);
	};

	static size(path: string): number {
		return FilesFacade.size(path);
	};

	static createFile(path: string): void {
		FilesFacade.createFile(path);
	};

	static createDirectory(path: string): void {
		FilesFacade.createDirectory(path);
	};

	static copy(source, target) {
		FilesFacade.copy(source, target);
	};

	static move(source: string, target: string): void {
		FilesFacade.move(source, target);
	};

	deleteFile(path: string): void {
		FilesFacade.deleteFile(path);
	};

	static deleteDirectory(path: string, forced: boolean): void {
		FilesFacade.deleteDirectory(path, forced);
	};

	static createTempFile(prefix: string, suffix: string): string {
		return FilesFacade.createTempFile(prefix, suffix);
	};

	static createTempDirectory(prefix: string): string {
		return FilesFacade.createTempDirectory(prefix);
	};

	static createInputStream(path: string): streams.InputStream {
		const native = FilesFacade.createInputStream(path);
		return new streams.InputStream(native);
	};

	static createOutputStream(path: string): streams.OutputStream {
		const native = FilesFacade.createOutputStream(path);
		return new streams.OutputStream(native);
	};

	static traverse(path: string): string {
		return FilesFacade.traverse(path);
	};

	static list(path: string): JSON {
		return JSON.parse(FilesFacade.list(path)).map(e => e.path);
	};

	static find(path: string, pattern: string): string {
		return JSON.parse(FilesFacade.find(path, pattern));
	};
}