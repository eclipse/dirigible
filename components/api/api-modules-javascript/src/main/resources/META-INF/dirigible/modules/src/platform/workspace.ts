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
import { json } from "@dirigible/http/response";
import * as bytes from "@dirigible/io/bytes";
import { workspace } from ".";
const WorkspaceFacade = Java.type("org.eclipse.dirigible.components.api.platform.WorkspaceFacade");

export class Workspaces{
	public static createWorkspace(name: string): Workspace {
		return new Workspace(WorkspaceFacade.createWorkspace(name));
	};

	public static getWorkspace(name: string): Workspace {
		return new Workspace(WorkspaceFacade.getWorkspace(name));
	};

	public static getWorkspacesNames(): JSON | any {
		let workspacesNames = WorkspaceFacade.getWorkspacesNames();

		if (workspacesNames) {
			return JSON.parse(workspacesNames);
		}

		return workspacesNames;
	};

	public static deleteWorkspace(name: string): void {
		WorkspaceFacade.deleteWorkspace(name);
	};
}

/**
 * Workspace object
 */
class Workspace {

	constructor(private native: any) { }

	getProjects(): Projects {
		const native = this.native.getProjects();
		return new Projects(native);
	};

	createProject(name: string): Project {
		const native = this.native.createProject(name);
		return new Project(native);
	};

	getProject(name: string): Project {
		const native = this.native.getProject(name);
		return new Project(native);
	};

	deleteProject(name: string): void {
		this.native.deleteProject(name);
	};

	exists(): boolean {
		return this.native.exists();
	};

	existsFolder(path: string): boolean {
		return this.native.existsFolder(path);
	};

	existsFile(path: string): boolean {
		return this.native.existsFile(path);
	};

	copyProject(source: string, target: string): void {
		this.native.copyProject(source, target);
	};

	moveProject(source: string, target: string): void {
		this.native.moveProject(source, target);
	};

}

/**
 * Projects object
 */
class Projects {

	constructor(private native: any) { }

	size(): number {
		return this.native.size();
	};

	get(index: string): Project {
		const native = this.native.get(index);
		return new Project(native);
	};

}

/**
 * Project object
 */
class Project {

	constructor(private native: any) { }

	getName(): string {
		const collection = this.native.getInternal();
		return collection.getName();
	};

	getPath(): string {
		const collection = this.native.getInternal();
		return collection.getPath();
	};

	createFolder(path: string): Folder {
		const native = this.native.createFolder(path);
		return new Folder(native);
	};

	exists(): boolean {
		return this.native.exists();
	};

	existsFolder(path: string): boolean {
		return this.native.existsFolder(path);
	};

	getFolder(path: string): Folder {
		const native = this.native.getFolder(path);
		return new Folder(native);
	};

	getFolders(path: string): Folders {
		const native = this.native.getFolders(path);
		return new Folders(native);
	};

	deleteFolder(path: string): void {
		return this.native.deleteFolder(path);
	};

	createFile(path: string, input: any): File {
		const native = this.native.createFile(path, input);
		return new File(native);
	};

	existsFile(path: string): boolean {
		return this.native.existsFile(path);
	};

	getFile(path: string): File {
		const native = this.native.getFile(path);
		return new File(native);
	};

	getFiles(path: string): Files {
		const native = this.native.getFiles(path);
		return new Files(native);
	};

	deleteFile(path: string): void {
		return this.native.deleteFile(path);
	};

}

/**
 * Folders object
 */
class Folders {

	constructor(private native: any) { }

	size(): number {
		const size = this.native.size();
		return size;
	};

	get(index: number): Folder {
		const native = this.native.get(index);
		return new Folder(native);
	};

}

/**
 * Files object
 */
class Files {

	constructor(private native: any) { }

	size(): number {
		const size = this.native.size();
		return size;
	};

	get(index: number): File {
		const native = this.native.get(index);
		return new File(native);
	};

}

/**
 * Folder object
 */
class Folder {

	constructor(private native: any) { }

	getName(): string {
		const collection = this.native.getInternal();
		const name = collection.getName();
		return name;
	};

	getPath(): string {
		const collection = this.native.getInternal();
		return collection.getPath();
	};

	createFolder(path: string): Folder {
		const native = this.native.createFolder(path);
		return new Folder(native);
	};

	exists(): boolean {
		return this.native.exists();
	};

	existsFolder(path: string): boolean {
		return this.native.existsFolder(path);
	};

	getFolder(path: string): Folder {
		const native = this.native.getFolder(path);
		return new Folder(native);
	};

	getFolders(path: string): Folders {
		const native = this.native.getFolders(path);
		return new Folders(native);
	};

	deleteFolder(path: string): void {
		return this.native.deleteFolder(path);
	};

	createFile(path: string, input: any): File {
		const native = this.native.createFile(path, input);
		return new File(native);
	};

	existsFile(path: string): boolean {
		return this.native.existsFile(path);
	};

	getFile(path: string): File {
		const native = this.native.getFile(path);
		return new File(native);
	};

	getFiles(path: string): Files {
		const native = this.native.getFiles(path);
		return new Files(native);
	};

	deleteFile(path: string): void {
		return this.native.deleteFile(path);
	};

}

/**
 * File object
 */
class File {

	constructor(private native: any) { }

	getName(): string {
		const collection = this.native.getInternal();
		return collection.getName();
	};

	getPath(): string {
		const collection = this.native.getInternal();
		return collection.getPath();
	};

	getContentType(): string {
		return this.native.getContentType();
	};

	isBinary(): boolean {
		return this.native.isBinary();
	};

	getContent(): any {
		const output = WorkspaceFacade.getContent(this.native);
		if (output) {
			output;
		}
		return output;
	};

	getText(): string {
		const bytesOutput = this.getContent();
		return bytes.byteArrayToText(bytesOutput);
	};

	setContent(input: any): any {
		const output = WorkspaceFacade.setContent(this.native, input);
		return output;
	};

	setText(input: any): any {
		const bytesInput = bytes.textToByteArray(input);
		return this.setContent(bytesInput);
	};

	exists(): boolean {
		return this.native.exists();
	};
}
