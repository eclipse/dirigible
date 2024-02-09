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
"use strict";

import * as globals from "@dirigible/core/globals";
import * as mongodb from "./client"
import * as dirigibleOrm from "@dirigible/db/orm";
import * as logging from "@dirigible/log/logging";

const mongoClient = mongodb.getClient();
const db = mongoClient.getDB();

export class DAO {
    $log: any;

    constructor(private orm, logCtxName: string, dataSourceName: string, databaseType: string) {
        if (orm === undefined) {
            throw Error('Illegal argument: orm[' + orm + ']');
        }

        this.orm = dirigibleOrm.get(orm);

        //setup loggerName
        var loggerName = logCtxName;
        if (!loggerName) {
            loggerName = 'mongodb.dao';
            if (this.orm.table)
                loggerName = 'mongodb.dao.' + (this.orm.table.toLowerCase());
        }
        this.$log = logging.getLogger(loggerName);
    }

    notify(event: string, ...a: any): void {
        var func = this[event];
        if (!this[event])
            return;
        if (typeof func !== 'function')
            throw Error('Illegal argument. Not a function: ' + func);
        var args = [].slice.call(arguments);
        func.apply(this, args.slice(1));
    };
    
    //Prepare a JSON object for insert into DB
    createNoSQLEntity(entity: any): {} {
        var persistentItem = {};
        var mandatories = this.orm.getMandatoryProperties();
        for (var i = 0; i < mandatories.length; i++) {
            if (mandatories[i].dbValue) {
                persistentItem[mandatories[i].name] = mandatories[i].dbValue.apply(this, [entity[mandatories[i].name], entity]);
            } else {
                persistentItem[mandatories[i].name] = entity[mandatories[i].name];
            }
        }
        var optionals = this.orm.getOptionalProperties();
        for (var i = 0; i < optionals.length; i++) {
            if (optionals[i].dbValue !== undefined) {
                persistentItem[optionals[i].name] = optionals[i].dbValue.apply(this, [entity[optionals[i].name], entity]);
            } else {
                persistentItem[optionals[i].name] = entity[optionals[i].name] === undefined ? null : entity[optionals[i].name];
            }
        }
        var msgIdSegment = persistentItem[this.orm.getPrimaryKey().name] ? "[" + persistentItem[this.orm.getPrimaryKey().name] + "]" : "";
        this.$log.info("Transformation to {} DB JSON object finished", (this.orm.table + msgIdSegment));
        return persistentItem;
    };
    
    validateEntity(entity: any, skip: any): void {
        if (entity === undefined || entity === null) {
            throw new Error('Illegal argument: entity is ' + entity);
        }
        if (skip) {
            if (skip.constructor !== Array) {
                skip = [skip];
            }
            for (var j = 0; j < skip.length; j++) {
                skip[j];
            }
        }
        var mandatories = this.orm.getMandatoryProperties();
        for (var i = 0; i < mandatories.length; i++) {
            var propName = mandatories[i].name;
            if ((skip && skip.indexOf(propName) > -1) || mandatories[i].type.toUpperCase() === 'BOOLEAN')
                continue;
            var propValue = entity[propName];
            if (propValue === undefined || propValue === null) {
                throw new Error('Illegal ' + propName + ' attribute value in ' + this.orm.table + ' entity: ' + propValue);
            }
        }
    };
    
    insert(_entity: any): any {
    
        var entities = _entity;
        if (_entity.constructor !== Array) {
            entities = [_entity];
        }
    
        this.$log.info('Inserting {} {}', this.orm.table, (entities.length === 1 ? 'entity' : 'entities'));
    
        var updatedRecordCount = 0;
        var ids = [];
    
        for (var i = 0; i < entities.length; i++) {
    
            var entity = entities[i];
    
            this.validateEntity(entity, [this.orm.getPrimaryKey().name]);
    
            var dbEntity = this.createNoSQLEntity(entity);
    
            try {
    
                var collection = db.getCollection(this.orm.table);
                var id = collection.getNextId();
    
                dbEntity[this.orm.getPrimaryKey().name] = id;
                dbEntity["_id"] = id;
    
    
                collection.insert(dbEntity);
                updatedRecordCount++;
    
                this.notify('afterInsert', dbEntity);
                this.notify('beforeInsertAssociationSets', dbEntity);
    
                if (updatedRecordCount > 0) {
                    ids.push(dbEntity[this.orm.getPrimaryKey().name]);
                    this.$log.info('{}[] entity inserted', this.orm.table, dbEntity[this.orm.getPrimaryKey().name]);
                } else {
                    this.$log.info('No changes incurred in {}', this.orm.table);
                }
    
    
            } catch (e) {
                this.$log.error("Inserting {} {} failed", e, this.orm.table, (entities.length === 1 ? 'entity' : 'entities'));
                this.$log.info('Rolling back changes after failed {}[{}] insert. ', this.orm.table, dbEntity[this.orm.getPrimaryKey().name]);
                if (dbEntity[this.orm.getPrimaryKey().name]) {
                    try {
                        this.remove(dbEntity[this.orm.getPrimaryKey().name]);
                    } catch (err) {
                        this.$log.error('Could not rollback changes after failed {}[{}}] insert. ', this.orm.table, dbEntity[this.orm.getPrimaryKey().name]);
                    }
                }
                throw e;
            }
        }
    
        if (_entity.constructor !== Array)
            return ids[0];
        else
            return ids;
    };
    
    // update entity from a JSON object. Returns the id of the updated entity.
    update(entity: any): DAO {
    
        this.$log.info('Updating {}[{}] entity', this.orm.table, entity !== undefined ? entity[this.orm.getPrimaryKey().name] : entity);
    
        if (entity === undefined || entity === null) {
            throw new Error('Illegal argument: entity is ' + entity);
        }
    
        var ignoredProperties = this.orm.getMandatoryProperties()
            .filter(function (property) {
                return property.allowedOps && property.allowedOps.indexOf('update') < 0;
            })
            .map(function (property) {
                return property.name;
            });
        this.validateEntity(entity, ignoredProperties);
    
        var dbEntity = this.createNoSQLEntity(entity);
        dbEntity[this.orm.getPrimaryKey().name] = entity["_id"];
        dbEntity["_id"] = entity["_id"];
    
        try {
            this.notify('beforeUpdateEntity', dbEntity);
    
            var collection = db.getCollection(this.orm.table);
            collection.update({ "_id": entity["_id"] }, dbEntity);
    
            this.$log.info('{}[{}] entity updated', this.orm.table, dbEntity[this.orm.getPrimaryKey().name]);
    
            return this;
        } catch (e) {
            this.$log.error('Updating {}[{}] failed', this.orm.table, entity !== undefined ? entity[this.orm.getPrimaryKey().name] : entity);
            throw e;
        }
    };
    
    // delete entity by id, or array of ids, or delete all (if not argument is provided).
    remove(id: number): void {
    
        var ids = [];
        if (arguments.length === 0) {
            ids = this.list({
                "$select": [this.orm.getPrimaryKey().name]
            }).map(function (ent) {
                return ent[this.orm.getPrimaryKey().name];
            }.bind(this));
        } else {
            if (arguments[0].constructor !== Array) {
                ids = [arguments[0]];
            } else {
                ids = arguments[0];
            }
        }
    
        this.$log.info('Deleting ' + this.orm.table + ((ids !== undefined && ids.length === 1) ? '[' + ids[0] + '] entity' : ids.length + ' entities'));
    
    
        for (var i = 0; i < ids.length; i++) {
    
            var id: number = ids[i];
    
            if (ids.length > 1)
                this.$log.info('Deleting {}[{}] entity', this.orm.table, id);
    
            if (id === undefined || id === null) {
                throw new Error('Illegal argument for id parameter:' + id);
            }
    
            try {
    
                this.notify('beforeRemoveEntity', id);
    
                var collection = db.getCollection(this.orm.table);
                collection.remove({ "_id": id });
    
            } catch (e) {
                this.$log.error('Deleting {}[{}] entity failed', this.orm.table, id);
                throw e;
            }
    
        }
    
    };
    
    expand(expansionPath: string, context: any): void {
        this.$log.info('Expanding for association path {} and context entity {}', expansionPath, (typeof arguments[1] !== 'object' ? 'id ' : '') + JSON.stringify(arguments[1]));
        throw Error("Not implemented.");
    };
    
    /* 
        Reads a single entity by id, parsed into JSON object. 
        If requested as expanded the returned entity will comprise associated (dependent) entities too. Expand can be a string tha tis a valid association name defined in this dao orm or
        an array of such names.
    */
    find(id: number, expand: any, select: any): any {
    
        if (typeof arguments[0] === 'object') {
            id = arguments[0].id;
            expand = arguments[0].$expand || arguments[0].expand;
            select = arguments[0].$select || arguments[0].select;
        }
    
        this.$log.info('Finding {}[{}] entity with list parameters expand[{}], select[{}]', this.orm.table, id, expand, select);
    
        if (id === undefined || id === null) {
            throw new Error('Illegal argument for id parameter:' + id);
        }
    
        try {
            var entity: any;
            if (select !== undefined) {
                if (select.constructor !== Array) {
                    if (select.constructor === String) {
                        select = select.split(',').map(function (sel) {
                            if (sel.constructor !== String)
                                throw Error('Illegal argument: select array components are expected ot be strings but found ' + (typeof sel));
                            return sel.trim();
                        });
                    } else {
                        throw Error('Illegal argument: select is expected to be string or array of strings but was ' + (typeof select));
                    }
                }
            }
            //ensure that joinkeys for required expands are available and not filtered by select
            if (select !== undefined && expand !== undefined) {
                select.push(this.orm.getPrimaryKey().name);
            }
    
            var params = {};
            params["_id"] = id;
    
            var collection = db.getCollection(this.orm.table);
            if (select !== undefined) {
                entity = collection.find(params, select);
            } else {
                entity = collection.findOneById(id);
            }
    
    
            if (entity) {
                this.$log.info('{}[{}] entity found', this.orm.table, id);
                this.notify('afterFound', entity);
            } else {
                this.$log.info('{}[{}] entity not found', this.orm.table, id);
            }
            return entity;
        } catch (e) {
            this.$log.error("Finding {}[{}] entitiy failed.", this.orm.table, id);
            throw e;
        }
    };
    
    count(): number {
    
        this.$log.info('Counting ' + this.orm.table + ' entities');
    
        var count = 0;
        try {
            var collection = db.getCollection(this.orm.table);
            count = collection.count();
        } catch (e) {
            this.$log.error('Counting {} entities failed', e, this.orm.table);
            // e.errContext = parametericStatement.toString(); // TODO: parametericStatement?
            throw e;
        }
    
        this.$log.info('{} {} entities counted', String(count), this.orm.table);
    
        return count;
    };
    
    /*
     * list parameters:
     * - $expand
     * - $filter
     * - $select
     * - $sort
     * - $order 
     * - $limit
     * - $offset
     */
    list(settings: any): any[] {
    
        settings = settings || {};
    
        var expand = settings.$expand || settings.expand;
        if (expand !== undefined) {
            if (expand.constructor !== Array) {
                if (expand.constructor === String) {
                    if (expand.indexOf(',') > -1) {
                        settings.$expand = expand.split(',').map(function (exp) {
                            if (exp.constructor !== String)
                                throw Error('Illegal argument: expand array components are expected ot be strings but found ' + (typeof exp));
                            return exp.trim();
                        });
                    } else {
                        settings.$expand = [expand];
                    }
                } else {
                    throw Error('Illegal argument: expand is expected to be string or array of strings but was ' + (typeof expand));
                }
            }
        }
    
        var select = settings.$select || settings.select;
        if (select !== undefined) {
            if (select.constructor !== Array) {
                if (select.constructor === String) {
                    if (select.indexOf(',') > -1) {
                        settings.$select = select.split(',').map(function (exp) {
                            if (exp.constructor !== String)
                                throw Error('Illegal argument: select array components are expected ot be strings but found ' + (typeof exp));
                            return exp.trim();
                        });
                    } else {
                        settings.$select = [select];
                    }
                } else {
                    throw Error('Illegal argument: select is expected to be string or array of strings but was ' + (typeof expand));
                }
            }
        }
    
        var listArgs = [];
        for (var key in settings) {
            listArgs.push(' ' + key + '[' + settings[key] + ']');
        }
    
        this.$log.info('Listing {} entity collection with list operators: {}', this.orm.table, listArgs.join(','));
    
        if (settings.$select !== undefined && expand !== undefined) {
            settings.$select.push(this.orm.getPrimaryKey().name);
        }
    
        //simplistic filtering of (only) string properties with like
        if (settings.$filter) {
            if (settings.$filter.indexOf(',') > -1) {
                settings.$filter = settings.$filter.split(',');
            } else {
                settings.$filter = [settings.$filter];
            }
            settings.$filter = settings.$filter.filter(function (filterField) {
                var prop = this.ormstatements.orm.getProperty(filterField);
                if (prop === undefined || prop.type.toUpperCase() !== 'VARCHAR' || settings[prop.name] === undefined)
                    return false;
                settings[prop.name] = '%' + settings[prop.name] + '%';
                return true;
            }.bind(this));
        }
    
        try {
            var entities = [];
            var collection = db.getCollection(this.orm.table);
            var cursor = collection.find();
            while (cursor.hasNext()) {
                entities.push(cursor.next());
            }
    
            this.$log.info('{} {} entities found', entities.length, this.orm.table);
    
            return entities;
        } catch (e) {
            this.$log.error("Listing {} entities failed.", this.orm.table);
            throw e;
        }
    };
    
    existsTable() {
        return true;
    };
    
    createTable() {
    };
    
    dropTable(dropIdSequence) {
        return this;
    };
};

/**
 * oDefinition can be table definition or standard orm definition object. Or it can be a valid path to
 * a .table file, or any other text file contianing a standard dao orm definition.
 */
export function create(oDefinition: any, logCtxName: string, dataSourceName: string, databaseType: string): DAO {
    var orm;

    orm = oDefinition;

    var productName = globals.get(databaseType + "_" + dataSourceName);
    if (!productName) {
        productName = "MongoDB";
        globals.set(databaseType + "_" + dataSourceName, productName);
    }

    return new DAO(orm, logCtxName, dataSourceName, databaseType);
};

export function dao(oDefinition, logCtxName, dataSourceName, databaseType) {
    return create(oDefinition, logCtxName, dataSourceName, databaseType);
}