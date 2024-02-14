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
import * as bytes from "@dirigible/io/bytes";
const RepositoryFacade = Java.type("org.eclipse.dirigible.components.api.platform.RepositoryFacade");

export class Repository{
	public static getResource(path: string): Resource {
		const resourceInstance = RepositoryFacade.getResource(path);
		return new Resource(resourceInstance);
	};

	public static createResource(path: string, content: string, contentType: string): Resource {
		const resourceInstance = RepositoryFacade.createResource(path, content, contentType);
		return new Resource(resourceInstance);
	};

	public static createResourceNative(path: string, content: any, contentType: string): Resource {
		const resourceInstance = RepositoryFacade.createResourceNative(path, content, contentType);
		return new Resource(resourceInstance);
	};

	public static updateResource(path: string, content: string): Resource {
		const resourceInstance = RepositoryFacade.updateResource(path, content);
		return new Resource(resourceInstance);
	};

	public static updateResourceNative(path: string, content: any): Resource {
		const resourceInstance = RepositoryFacade.updateResourceNative(path, content);
		return new Resource(resourceInstance);
	};

	public static deleteResource(path: string): void {
		RepositoryFacade.deleteResource(path);
	};

	public static getCollection(path: string): Collection {
		const collectionInstance = RepositoryFacade.getCollection(path);
		return new Collection(collectionInstance);
	};

	public static createCollection(path: string): Collection {
		const collectionInstance = RepositoryFacade.createCollection(path);
		return new Collection(collectionInstance);
	};

	public static deleteCollection(path: string): void {
		RepositoryFacade.deleteCollection(path);
	};

	public static find(path: string, pattern: string): string {
		return JSON.parse(RepositoryFacade.find(path, pattern));
	};
}

class Resource {

	constructor(private native: any) { }

	getName(): string {
		return this.native.getName();
	};

	getPath(): string {
		return this.native.getPath();
	};

	getParent(): Collection {
		const collectionInstance = this.native.getParent();
		return new Collection(collectionInstance);
	};

	getInformation(): EntityInformation {
		const informationInstance = this.native.getInformation();
		return new EntityInformation(informationInstance);
	};

	create(): void {
		this.native.create();
	};

	delete(): void {
		this.native.delete();
	};

	renameTo(name: string): void {
		this.native.renameTo(name);
	};

	moveTo(path: string): void {
		this.native.moveTo(path);
	};

	copyTo(path: string): void {
		this.native.copyTo(path);
	};

	exists(): boolean {
		return this.native.exists();
	};

	isEmpty(): boolean {
		return this.native.isEmpty();
	};

	getText(): string {
		return bytes.byteArrayToText(this.getContent());
	};

	getContent(): any {
		let nativeContent = this.native.getContent();
		return bytes.toJavaScriptBytes(nativeContent);
	};

	getContentNative(): any {
		return this.native.getContent();
	};

	setText(text: string): void {
		let content = bytes.textToByteArray(text);
		this.setContent(content);
	};

	setContent(content: any): void {
		let nativeContent = bytes.toJavaBytes(content);
		this.native.setContent(nativeContent);
	};

	setContentNative(content: any): void {
		this.native.setContent(content);
	};

	isBinary(): void {
		this.native.isBinary();
	};

	getContentType(): void {
		this.native.getContentType();
	};
}

class Collection {

	constructor(private native: any) { }

	getName(): string {
		return this.native.getName();
	};

	getPath(): string {
		return this.native.getPath();
	};

	getParent(): Collection {
		const collectionInstance = this.native.getParent();
		return new Collection(collectionInstance);
	};

	getInformation(): EntityInformation {
		const informationInstance = this.native.getInformation();
		return new EntityInformation(informationInstance);
	};

	create(): void {
		this.native.create();
	};

	delete(): void {
		this.native.delete();
	};

	renameTo(name: string): void {
		this.native.renameTo(name);
	};

	moveTo(path: string): void {
		this.native.moveTo(path);
	};

	copyTo(path: string): void {
		this.native.copyTo(path);
	};

	exists(): boolean {
		return this.native.exists();
	};

	isEmpty(): boolean {
		return this.native.isEmpty();
	};

	getCollectionsNames(): any {
		return this.native.getCollectionsNames();
	};

	createCollection(name: string): Collection {
		const collectionInstance = this.native.createCollection(name);
		return new Collection(collectionInstance);
	};

	getCollection(name: string): Collection {
		const collectionInstance = this.native.getCollection(name);
		return new Collection(collectionInstance);
	};

	removeCollection(name: string): void {
		this.native.removeCollection(name);
	};

	getResourcesNames(): any {
		return this.native.getResourcesNames();
	};

	getResource(name: string): Resource {
		const resourceInstance = this.native.getResource(name);
		return new Resource(resourceInstance);
	};

	removeResource(name: string): void {
		this.native.removeResource(name);
	};

	createResource(name: string, content: string): Resource {
		const resourceInstance = this.native.createResource(name, content);
		return new Resource(resourceInstance);
	};
}

class EntityInformation {

	constructor(private native: any) { }

	getName(): string {
		return this.native.getName();
	};

	getPath(): string {
		return this.native.getPath();
	};

	getPermissions(): string {
		return this.native.getPermissions();
	};

	getSize(): number {
		return this.native.getSize();
	};

	getCreatedBy(): string {
		return this.native.getCreatedBy();
	};

	getCreatedAt(): string {
		return this.native.getCreatedAt();
	};

	getModifiedBy(): string {
		return this.native.getModifiedBy();
	};

	getModifiedAt(): string {
		return this.native.getModifiedAt();
	};
}
