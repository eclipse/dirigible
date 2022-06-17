/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/** Client API for MongoDB */


exports.getClient = function() {
	var client = new Client();
	var native = org.eclipse.dirigible.api.mongodb.MongoDBFacade.getClient();
	client.native = native;
	return client;
};

exports.createBasicDBObject = function() {
	var dbObject = new DBObject();
	var native = org.eclipse.dirigible.api.mongodb.MongoDBFacade.createBasicDBObject();
	dbObject.native = native;
	extract(dbObject);
	return dbObject;
};

/**
 * Client object
 */
function Client() {
	
	this.getDB = function(name) {
		var db = new DB();
		var native = null;
		if (name) {
			native = this.native.getDB(name);
		} else {
			var defaultDB = org.eclipse.dirigible.api.mongodb.MongoDBFacade.getDefaultDatabaseName();
			native = this.native.getDB(defaultDB);
		}
		
		db.native = native;
		return db;
	};

}

/**
 * DB object
 */
function DB() {
	
	this.getCollection = function(name) {
		var dbCollection = new DBCollection();
		var native = this.native.getCollection(name);
		dbCollection.native = native;
		return dbCollection;
	};

}

/**
 * DBCollection object
 */
function DBCollection() {

	this.insert = function(dbObject) {
		dbObject = implicit(dbObject);
		this.native.insert(dbObject.native);
	};

	this.find = function(query, projection) {
		query = implicit(query);
		projection = implicit(projection);
		var dbCursor = new DBCursor();
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
		dbCursor.native = native;
		return dbCursor;
	};

	this.findOne = function(query, projection, sort) {
		query = implicit(query);
		projection = implicit(projection);
		var dbObject = exports.createBasicDBObject();
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

	this.findOneById = function(id, projection) {
		projection = implicit(projection);
		var dbObject = exports.createBasicDBObject();
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

	this.count = function(query) {
		query = implicit(query);
		if (query) {
			return this.native.count(query.native);
		}
		return this.native.count();
	};

	this.getCount = function(query) {
		query = implicit(query);
		if (query) {
			return this.native.getCount(query.native);
		}
		return this.native.getCount();
	};

	this.createIndex = function(keys, options) {
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

	this.createIndexForField = function(name) {
		if (name) {
			this.native.createIndex(name);
		} else {
			throw new Error("The filed name must be provided");
		}
	};

	this.distinct = function(name, query) {
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

	this.dropIndex = function(index) {
		if (index) {
			this.native.dropIndex(index);
		} else {
			throw new Error("The index parameter must be provided");
		}
	};

	this.dropIndexByName = function(name) {
		if (name) {
			this.native.dropIndex(name);
		} else {
			throw new Error("The index name must be provided");
		}
	};

	this.dropIndexes = function() {
		this.native.dropIndexes();
	};

	this.remove = function(query) {
		query = implicit(query);
		this.native.remove(query.native);
	};

	this.rename = function(newName) {
		this.native.rename(newName);
	};

	this.save = function(dbObject) {
		dbObject = implicit(dbObject);
		this.native.save(dbObject.native);
	};

	this.update = function(query, update, upsert, multi) {
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

	this.updateMulti = function(query, update) {
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

	this.getNextId = function() {
		var cursor = this.find({}, {"_id": 1}).sort({"_id": -1}).limit(1);
		if (!cursor.hasNext()) {
			return 1;
		} else {
			return cursor.next()["_id"] + 1;
		}
	}

	this.generateUUID = function() {
		return require("utils/v4/uuid").random();
	}

}

/**
 * DBCursor object
 */
function DBCursor() {

	this.one = function() {
		var dbObject = new DBObject();
		var native = this.native.one();
		dbObject.native = native;
		extract(dbObject);
		return dbObject;
	};

	this.batchSize = function(numberOfElements) {
		if (!numberOfElements) {
			throw new Error("The numberOfElements parameter must be provided");
		}
		this.native.batchSize(numberOfElements);
		return this;
	};

	this.getBatchSize = function() {
		return this.native.getBatchSize();
	};

	this.getCollection = function() {
		var dbCollection = new DBCollection();
		var native = this.native.getCollection();
		dbCollection.native = native;
		return dbCollection;
	};

	this.getCursorId = function() {
		return this.native.getCursorId();
	};

	this.getKeysWanted = function() {
		var dbObject = new DBObject();
		var native = this.native.getKeysWanted();
		dbObject.native = native;
		extract(dbObject);
		return dbObject;
	};

	this.getLimit = function() {
		return this.native.getLimit();
	};

	this.close = function() {
		this.native.close();
	};

	this.hasNext = function() {
		return this.native.hasNext();
	};

	this.next = function() {
		var dbObject = new DBObject();
		var native = this.native.next();
		dbObject.native = native;
		extract(dbObject);
		return dbObject;
	};

	this.getQuery = function() {
		var dbObject = new DBObject();
		var native = this.native.getQuery();
		dbObject.native = native;
		extract(dbObject);
		return dbObject;
	};

	this.length = function() {
		return this.native.length();
	};

	this.sort = function(orderBy) {
		orderBy = implicit(orderBy);
		if (!orderBy) {
			throw new Error("The orderBy parameter must be provided");
		}
		this.native.sort(orderBy.native);
		return this;
	};

	this.limit = function(limit) {
		if (!limit) {
			throw new Error("The limit parameter must be provided");
		}
		this.native.limit(limit);
		return this;
	};

	this.min = function(min) {
		if (!min) {
			throw new Error("The min parameter must be provided");
		}
		this.native.min(min);
		return this;
	};

	this.max = function(max) {
		if (!max) {
			throw new Error("The max parameter must be provided");
		}
		this.native.max(max);
		return this;
	};

	this.maxTime = function(maxTime) {
		if (!maxTime) {
			throw new Error("The maxTime parameter must be provided");
		}
		this.native.maxTime(maxTime, java.util.concurrent.TimeUnit.MILLISECONDS);
		return this;
	};

	this.size = function() {
		return this.native.size();
	};

	this.skip = function(numberOfElements) {
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
function DBObject() {
	
	this.append = function(key, value) {
		this.native.append(key, value);
		return this;
	};

	this.toJson = function() {
		return this.native.toJson();
	}

	this.markAsPartialObject = function() {
		this.native.markAsPartialObject();
	}

	this.isPartialObject = function() {
		return this.native.isPartialObject();
	}

	this.containsField = function(key) {
		return this.native.containsField(key);
	}

	this.get = function(key) {
		return this.native.get(key);
	}

	this.put = function(key, value) {
		if (!key) {
			throw new Error("The key parameter must be provided");
		}
		if (!value) {
			throw new Error("The value parameter must be provided");
		}
		return this.native.put(key, value);
	}

	this.removeField = function(key) {
		return this.native.removeField(key);
	}
	
}

function extract(dbObject) {
	if (!dbObject.native) {
		return {};
	}
	var extracted = JSON.parse(dbObject.native.toJson());
	for(var propertyName in extracted) {
		dbObject[propertyName] = extracted[propertyName];
	}
}

function implicit(object) {
	if (!object) {
		return object;
	}
	if (object.native) {
		return object;
	}
	var dbObject = exports.createBasicDBObject();

	for(var propertyName in object) {
		dbObject.append(propertyName, object[propertyName]);
	}
	return dbObject;
}

