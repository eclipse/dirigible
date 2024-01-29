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

	public static separator = File.separator;
	
	public static exists(path: string): boolean{
		return FilesFacade.exists(path);
	};

	public static isExecutable(path: string): boolean {
		return FilesFacade.isExecutable(path);
	};

	public static isReadable(path: string): boolean {
		return FilesFacade.isReadable(path);
	};

	public static isWritable(path: string): boolean {
		return FilesFacade.isWritable(path);
	};

	public static isHidden(path: string): boolean {
		return FilesFacade.isHidden(path);
	};

	public static isDirectory(path: string): boolean {
		return FilesFacade.isDirectory(path);
	};

	public static isFile(path: string): boolean {
		return FilesFacade.isFile(path);
	};

	public static isSameFile(path1: string, path2: string): boolean {
		return FilesFacade.isSameFile(path1, path2);
	};

	public static getCanonicalPath(path: string): string {
		return FilesFacade.getCanonicalPath(path);
	};

	public static getName(path: string): string {
		return FilesFacade.getName(path);
	};

	public static getParentPath(path: string): string {
		return FilesFacade.getParentPath(path);
	};

	public static readBytes(path: string): Array<bytes> {
		const native = FilesFacade.readBytes(path);
		const data = bytes.toJavaScriptBytes(native);
		return data;
	};

	public static readBytesNative(path: string): Array<bytes>{
		return FilesFacade.readBytes(path);
	};

	public static readText(path: string): string{
		return FilesFacade.readText(path);
	};

	public static writeBytes(path: string, data): void{
		const native = bytes.toJavaBytes(data);
		FilesFacade.writeBytesNative(path, native);
	};

	public static writeBytesNative(path: string, data): void{
		FilesFacade.writeBytesNative(path, data);
	};

	public static writeText(path: string, text: string): void {
		FilesFacade.writeText(path, text);
	};

	public static getLastModified(path: string): Date {
		return new Date(FilesFacade.getLastModified(path));
	};

	public static setLastModified(path: string, time: Date): void {
		FilesFacade.setLastModified(path, time.getMilliseconds());
	};

	public static getOwner(path: string): string {
		return FilesFacade.getOwner(path);
	};

	public static setOwner(path: string, owner: string): void {
		FilesFacade.setOwner(path, owner);
	};

	public static getPermissions(path: string): string {
		return FilesFacade.getPermissions(path);
	};

	public static setPermissions(path: string, permissions: string): void {
		FilesFacade.setPermissions(path, permissions);
	};

	public static size(path: string): number {
		return FilesFacade.size(path);
	};

	public static createFile(path: string): void {
		FilesFacade.createFile(path);
	};

	public static createDirectory(path: string): void {
		FilesFacade.createDirectory(path);
	};

	public static copy(source: string, target: string): void {
		FilesFacade.copy(source, target);
	};

	public static move(source: string, target: string): void {
		FilesFacade.move(source, target);
	};

	public deleteFile(path: string): void {
		FilesFacade.deleteFile(path);
	};

	public static deleteDirectory(path: string, forced: boolean): void {
		FilesFacade.deleteDirectory(path, forced);
	};

	public static createTempFile(prefix: string, suffix: string): string {
		return FilesFacade.createTempFile(prefix, suffix);
	};

	public static createTempDirectory(prefix: string): string {
		return FilesFacade.createTempDirectory(prefix);
	};

	public static createInputStream(path: string): streams.InputStream {
		const native = FilesFacade.createInputStream(path);
		return new streams.InputStream(native);
	};

	public static createOutputStream(path: string): streams.OutputStream {
		const native = FilesFacade.createOutputStream(path);
		return new streams.OutputStream(native);
	};

	public static traverse(path: string): string {
		return FilesFacade.traverse(path);
	};

	public static list(path: string): JSON {
		return JSON.parse(FilesFacade.list(path)).map(e => e.path);
	};

	public static find(path: string, pattern: string): string {
		return JSON.parse(FilesFacade.find(path, pattern));
	};
}