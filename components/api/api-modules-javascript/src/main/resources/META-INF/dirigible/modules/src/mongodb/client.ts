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

const MongoDBFacade = Java.type("org.eclipse.dirigible.components.api.mongodb.MongoDBFacade");
const TimeUnit = Java.type("java.util.concurrent.TimeUnit");
import * as uuid from "@dirigible/utils/uuid";

export function getClient(): Client {
    var native = MongoDBFacade.getClient();
    return new Client(native);
};

export function createBasicDBObject(): DBObject {
    var native = MongoDBFacade.createBasicDBObject();
    var dbObject = new DBObject(native);
    extract(dbObject);
    return dbObject;
};

/**
 * Client object
 */
class Client {

    constructor(private native: any) { }

    getDB(name?: string): DB {

        var native = null;
        if (name) {
            native = this.native.getDB(name);
        } else {
            var defaultDB = MongoDBFacade.getDefaultDatabaseName();
            native = this.native.getDB(defaultDB);
        }

        return new DB(native);
    };

}

/**
 * DB object
 */
class DB {

    constructor(private native: any) { }

    getCollection(name?: string): DBCollection {
        var native = this.native.getCollection(name);
        return new DBCollection(native);
    };

}

/**
 * DBCollection object
 */
class DBCollection {

    constructor(private native: any) { }

    insert(dbObject: any): void {
        dbObject = implicit(dbObject);
        this.native.insert(dbObject.native);
    };

    find(query?: any, projection?: any): DBCursor {
        query = implicit(query);
        projection = implicit(projection);

        var native = null;
        if (query) {
            if (projection) {
                native = this.native.find(query.native, projection.native);
            } else {
                native = this.native.find(query.native);
            }
        } else {
            native = this.native.find();
        }

        return new DBCursor(native);
    };

    findOne(query: any, projection: any, sort: any): DBObject {
        query = implicit(query);
        projection = implicit(projection);
        var dbObject = createBasicDBObject();
        var native = null;
        if (query) {
            if (projection) {
                if (sort) {
                    native = this.native.findOne(query.native, projection.native, sort.native);
                } else {
                    native = this.native.findOne(query.native, projection.native);
                }
            } else {
                native = this.native.findOne(query.native);
            }
        } else {
            native = this.native.findOne();
        }
        dbObject.native = native;
        extract(dbObject);
        return dbObject;
    };

    findOneById(id: any, projection?: any): DBObject {
        projection = implicit(projection);
        var dbObject = createBasicDBObject();
        var native = null;
        if (id) {
            if (projection) {
                native = this.native.findOne(id, projection.native);
            } else {
                native = this.native.findOne(id);
            }
        } else {
            throw new Error("The id must be provided");
        }
        dbObject.native = native;
        extract(dbObject);
        return dbObject;
    };

    count(query?: any): number {
        query = implicit(query);
        if (query) {
            return this.native.count(query.native);
        }
        return this.native.count();
    };

    getCount(query: any): number {
        query = implicit(query);
        if (query) {
            return this.native.getCount(query.native);
        }
        return this.native.getCount();
    };

    createIndex(keys: any, options: any) {
        keys = implicit(keys);
        options = implicit(options);
        if (keys) {
            if (options) {
                this.native.createIndex(keys.native, options.native);
            } else {
                this.native.createIndex(keys.native);
            }
        } else {
            throw new Error("At least Keys parameter must be provided");
        }
    };

    createIndexForField(name: string): void {
        if (name) {
            this.native.createIndex(name);
        } else {
            throw new Error("The filed name must be provided");
        }
    };

    distinct(name: string, query: any, keys: any): void {
        query = implicit(query);
        if (name) {
            if (query) {
                this.native.distinct(keys.native, query.native);
            } else {
                this.native.distinct(name);
            }
        } else {
            throw new Error("At least the filed name parameter must be provided");
        }
    };

    dropIndex(index: number): void {
        if (index) {
            this.native.dropIndex(index);
        } else {
            throw new Error("The index parameter must be provided");
        }
    };

    dropIndexByName(name: string): void {
        if (name) {
            this.native.dropIndex(name);
        } else {
            throw new Error("The index name must be provided");
        }
    };

    dropIndexes(): void {
        this.native.dropIndexes();
    };

    remove(query: any): void {
        query = implicit(query);
        this.native.remove(query.native);
    };

    rename(newName: string): void {
        this.native.rename(newName);
    };

    save(dbObject: DBObject): void {
        dbObject = implicit(dbObject);
        this.native.save(dbObject.native);
    };

    update(query: any, update: any, upsert?: any, multi?: any): void {
        query = implicit(query);
        update = implicit(update);
        if (query) {
            if (update) {
                if (upsert) {
                    if (multi) {
                        this.native.update(query.native, update.native, upsert, multi);
                    } else {
                        this.native.update(query.native, update.native, upsert);
                    }
                } else {
                    this.native.update(query.native, update.native);
                }
            } else {
                throw new Error("The query parameter must be provided");
            }
        } else {
            throw new Error("The query parameter must be provided");
        }
    };

    updateMulti(query: any, update: any): void {
        query = implicit(query);
        update = implicit(update);
        if (query) {
            if (update) {
                this.native.update(query.native, update.native);
            } else {
                throw new Error("The query parameter must be provided");
            }
        } else {
            throw new Error("The query parameter must be provided");
        }
    };

    getNextId(): number {
        var cursor = this.find({}, { "_id": 1 }).sort({ "_id": -1 }).limit(1);
        if (!cursor.hasNext()) {
            return 1;
        } else {
            return cursor.next()["_id"] + 1;
        }
    }

    generateUUID(): string {
        return uuid.random();
    }

}

/**
 * DBCursor object
 */
class DBCursor {

    constructor(private native: any) { }

    one(): DBObject {
        var native = this.native.one();
        var dbObject = new DBObject(native);
        extract(dbObject);
        return dbObject;
    };

    batchSize(numberOfElements: number): DBCursor {
        if (!numberOfElements) {
            throw new Error("The numberOfElements parameter must be provided");
        }
        this.native.batchSize(numberOfElements);
        return this;
    };

    getBatchSize(): number {
        return this.native.getBatchSize();
    };

    getCollection(): DBCollection {
        var native = this.native.getCollection();
        return new DBCollection(native);
    };

    getCursorId(): number {
        return this.native.getCursorId();
    };

    getKeysWanted(): DBObject {
        var native = this.native.getKeysWanted();
        var dbObject = new DBObject(native);
        extract(dbObject);
        return dbObject;
    };

    getLimit() {
        return this.native.getLimit();
    };

    close(): void {
        this.native.close();
    };

    hasNext(): boolean {
        return this.native.hasNext();
    };

    next(): DBObject {
        var native = this.native.next();
        var dbObject = new DBObject(native);
        extract(dbObject);
        return dbObject;
    };

    getQuery(): DBObject {
        var native = this.native.getQuery();
        var dbObject = new DBObject(native);
        extract(dbObject);
        return dbObject;
    };

    length(): number {
        return this.native.length();
    };

    sort(orderBy: any): DBCursor {
        orderBy = implicit(orderBy);
        if (!orderBy) {
            throw new Error("The orderBy parameter must be provided");
        }
        this.native.sort(orderBy.native);
        return this;
    };

    limit(limit: number): DBCursor {
        if (!limit) {
            throw new Error("The limit parameter must be provided");
        }
        this.native.limit(limit);
        return this;
    };

    min(min: number): DBCursor {
        if (!min) {
            throw new Error("The min parameter must be provided");
        }
        this.native.min(min);
        return this;
    };

    max(max: number): DBCursor {
        if (!max) {
            throw new Error("The max parameter must be provided");
        }
        this.native.max(max);
        return this;
    };

    maxTime(maxTime: number): DBCursor {
        if (!maxTime) {
            throw new Error("The maxTime parameter must be provided");
        }
        this.native.maxTime(maxTime, TimeUnit.MILLISECONDS);
        return this;
    };

    size(): number {
        return this.native.size();
    };

    skip(numberOfElements: number): DBCursor {
        if (!numberOfElements) {
            throw new Error("The numberOfElements parameter must be provided");
        }
        this.native.skip(numberOfElements);
        return this;
    };

}

/**
 * DBObject object
 */
class DBObject {

    constructor(public native: any) { }

    append(key: string, value: any): DBObject {
        this.native.append(key, value);
        return this;
    };

    toJson(): Object {
        return this.native.toJson();
    }

    markAsPartialObject(): void {
        this.native.markAsPartialObject();
    }

    isPartialObject(): boolean {
        return this.native.isPartialObject();
    }

    containsField(key: string): boolean {
        return this.native.containsField(key);
    }

    get(key: string): any {
        return this.native.get(key);
    }

    put(key: string, value: any): any {
        if (!key) {
            throw new Error("The key parameter must be provided");
        }
        if (!value) {
            throw new Error("The value parameter must be provided");
        }
        return this.native.put(key, value);
    }

    removeField(key: string): any {
        return this.native.removeField(key);
    }

}

function extract(dbObject: DBObject): {} {
    if (!dbObject.native) {
        return {};
    }
    var extracted = JSON.parse(dbObject.native.toJson());
    for (var propertyName in extracted) {
        dbObject[propertyName] = extracted[propertyName];
    }
}

function implicit(object: any): DBObject {
    if (!object) {
        return object;
    }
    if (object.native) {
        return object;
    }
    var dbObject = createBasicDBObject();

    for (var propertyName in object) {
        dbObject.append(propertyName, object[propertyName]);
    }
    return dbObject;
}

