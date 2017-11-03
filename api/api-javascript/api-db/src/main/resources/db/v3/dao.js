/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */
"use strict";
var database = require("db/v3/database");
var sequence = require("db/v3/sequence");
var ormLib = require("db/v3/orm");
var ormStatements = require('db/v3/ormstatements');
var logging = require('log/logging');

var DAO = exports.DAO = function(orm, logCtxName, dataSourceName, databaseType){
	if(orm === undefined)
		throw Error('Illegal argument: orm['+ orm + ']');
	
	this.dataSourceName = dataSourceName;
	this.databaseType = databaseType;

	this.orm = ormLib.get(orm);
	
	//setup loggerName
	var loggerName = logCtxName;
	if(!loggerName){
		loggerName = 'org.eclipse.dirigible.db.dao';
		if(this.orm.table)
			loggerName = 'org.eclipse.dirigible.db.dao.'+(this.orm.table.toLowerCase());
	}
	this.$log = logging.getLogger(loggerName);
	
	var dbProductName = database.getMetadata(this.databaseType, this.dataSourceName).databaseProductName;
	this.ormstatements = ormStatements.forDatasource(this.orm, dbProductName);
};

DAO.prototype.getConnection = function(){
	return database.getConnection(this.databaseType, this.dataSourceName);
};

/*obsolete*/
DAO.prototype.withDataSource = function(ds){
	this.datasource = ds || database.getDataSources()[0];
	this.ormstatements = require('db/v3/ormstatements').forDatasource(this.orm, this.datasource);
	return this;
};

DAO.prototype.notify = function(event){
	var func = this[event];
	if(!this[event])
		return;
	if(typeof func !== 'function')
		throw Error('Illegal argument. Not a function: ' + func);
	var args = [].slice.call(arguments);
	func.apply(this, args.slice(1));
};

//Prepare a JSON object for insert into DB
DAO.prototype.createSQLEntity = function(entity) {
	var persistentItem = {};
	var mandatories = this.orm.getMandatoryProperties();
	for(var i=0; i<mandatories.length; i++){
		if(mandatories[i].dbValue){
			persistentItem[mandatories[i].name] = mandatories[i].dbValue.apply(this, [entity[mandatories[i].name], entity]);
		} else {
			persistentItem[mandatories[i].name] = entity[mandatories[i].name];
		}
	}
	var optionals = this.orm.getOptionalProperties();
	for(var i=0; i<optionals.length; i++){
		if(optionals[i].dbValue !== undefined){
			persistentItem[optionals[i].name] = optionals[i].dbValue.apply(this, [entity[optionals[i].name], entity]);
		} else {
			persistentItem[optionals[i].name] = entity[optionals[i].name] === undefined ? null : entity[optionals[i].name];
		} 
	}
	var msgIdSegment = persistentItem[this.orm.getPrimaryKey().name]?"["+persistentItem[this.orm.getPrimaryKey().name]+"]":"";
	this.$log.info("Transformation to {} DB JSON object finished", (this.orm.table + msgIdSegment));
	return persistentItem;
};

//create entity as JSON object from ResultSet current Row
DAO.prototype.createEntity = function(resultSet, entityPropertyNames) {
    var entity = {};
    var properties = this.orm.properties;
    if(entityPropertyNames && entityPropertyNames.length>0){
    	properties = properties.filter(function(prop){
    		return entityPropertyNames.indexOf(prop.name)>-1;
    	});
    }
    for(var i=0; i<properties.length; i++){
    	var prop = properties[i];
    	entity[prop.name] = resultSet['get'+prop.type](prop.column);
    	if(prop.value){
    		entity[prop.name] = prop.value(entity[prop.name]);
    	}
    }
    
    for(var key in Object.keys(entity)){
		if(entity[key] === null)
			entity[key] = undefined;
	}
	var entitySegment = "";
	if(entity[this.orm.getPrimaryKey().name]){
		entitySegment= "["+entity[this.orm.getPrimaryKey().name]+"]";
	}
    this.$log.info("Transformation from {} DB JSON object finished", (this.orm.table+entitySegment));
    return entity;
};

DAO.prototype.validateEntity = function(entity, skip){
	if(entity === undefined || entity === null){
		throw new Error('Illegal argument: entity is ' + entity);
	}
	if(skip){
		if(skip.constructor !== Array){
			skip = [skip];
		}
		for(var j=0; j<skip.length; j++){
			skip[j];
		}
	}
	var mandatories = this.orm.getMandatoryProperties();
	for(var i = 0; i< mandatories.length; i++){
		var propName = mandatories[i].name;
		if(skip && skip.indexOf(propName)>-1)
			continue;
		var propValue = entity[propName];
		if(propValue === undefined || propValue === null){
			throw new Error('Illegal ' + propName + ' attribute value in '+this.orm.table+' entity: ' + propValue);
		}
	}
};

DAO.prototype.insert = function(_entity){

	var entities = _entity;
	if(_entity.constructor !== Array){
		entities = [_entity];
	}

	this.$log.info('Inserting {} {}', this.orm.table, (entities.length===1?'entity':'entities'));
	
	for(var i=0; i<entities.length; i++) {
	
		var entity = entities[i];

		this.validateEntity(entity, [this.orm.getPrimaryKey().name]);

		var connection;
	
		//check for unique constraint violations
		var uniques = this.orm.getUniqueProperties();
		for(var _i = 0; _i< uniques.length; _i++){
			var prop = uniques[_i];
			var st = this.ormstatements.builder(this.ormstatements.dialect)
						.select(prop.column)
						.from(this.orm.table)
						.where(prop.column+'=?', [prop]);
			try{
				connection = this.getConnection();						
				var params = {};
				params[prop.name] = entity[prop.name];
				var rs = this.ormstatements.execute(st, connection, params);
				if(rs.next()){
					throw Error('Unique constraint violation for ' + prop.name + '['+entity[prop.name]+']');
				}
			} finally {
				connection.close();
			}
		}
	
	    var dbEntity = this.createSQLEntity(entity);
	
	    connection = this.getConnection();
	    var ids = [];
	    try {
	        var parametericStatement = this.ormstatements.insert.apply(this.ormstatements);

	        var id = sequence.nextval(this.orm.table+'_'+this.orm.getPrimaryKey.name.toUpperCase(), this.databaseType, this.datasourceName);
	        dbEntity[this.orm.getPrimaryKey().name] = id;
	        
			var updatedRecordCount = this.ormstatements.execute(parametericStatement, connection, dbEntity);
			
			this.notify('afterInsert', dbEntity);
			this.notify('beforeInsertAssociationSets', dbEntity);
			if(this.orm.associations && Object.keys(this.orm.associations).length){
				//Insert dependencies if any are provided inline with this entity
				this.$log.info('Inserting association sets for {}[{}]', this.orm.table, dbEntity[this.orm.getPrimaryKey().name]);
				for(var idx in Object.keys(this.orm.associations)){
					var association = this.orm.associations[idx];
					var associationName = association['name'];
					if([this.orm.ASSOCIATION_TYPES['MANY-TO-MANY'], this.orm.ASSOCIATION_TYPES['MANY-TO-ONE']].indexOf(association.type)<0){
						if(entity[associationName] && entity[associationName].length>0){
							var associationDaoFactoryFunc = association.targetDao || this;
							if(associationDaoFactoryFunc.constructor !== Function)
								throw Error('Invalid ORM: Association ' + associationName + ' dao property is expected to be function. Instead, it is: ' + (typeof associationDaoFactoryFunc))
							var associationDAO = associationDaoFactoryFunc.apply(this);
							this.notify('beforeInsertAssociationSet', entity[associationName], entity);
							this.$log.info('Inserting {} inline entities into association set {}', entity[associationName].length, associationName);
							for(var j=0; j<entity[associationName].length; j++){
				        		var associatedEntity = entity[associationName][j];
				        		var associatedEntityJoinKey = association.joinKey;
				        		var key = association.key || this.orm.getPrimaryKey().name;
				        		associatedEntity[associatedEntityJoinKey] = entity[key];
				        		this.notify('beforeInsertAssociationSetEntity', entity[associationName], dbEntity);
				        		
								associationDAO.insert.apply(associationDAO, [associatedEntity]);
								
				    		}
				    		this.$log.info('Inserting {} inline entities into association set {} finsihed', entity[associationName].length, associationName);
				    		this.notify('afterInsertAssociationSet', entity[associationName], dbEntity);
						}				
					}
				}		
			}

			if(updatedRecordCount>0){
				this.$log.info('{}[] entity inserted', this.orm.table, dbEntity[this.orm.getPrimaryKey().name]);
			} else {
				this.$log.info('No changes incurred in {}', this.orm.table);
			}

        	ids.push(dbEntity[this.orm.getPrimaryKey().name]);

	    } catch(e) {
	    	this.$log.error("Inserting "+this.orm.table+" "+(entities.length===1?'entity':'entities')+" failed", e);
	    	this.$log.info('Rolling back changes after failed {}[{}] insert. ', this.orm.table, dbEntity[this.orm.getPrimaryKey().name]);
			if(dbEntity[this.orm.getPrimaryKey().name]){
				try{
					this.remove(dbEntity[this.orm.getPrimaryKey().name]);
				} catch(err) {
					this.$log.error('Could not rollback changes after failed '+this.orm.table+'[' +  dbEntity[this.orm.getPrimaryKey().name] + '] insert. ', err);
				}
			}
			throw e;
	    } finally {
	        connection.close();
	    }
    }
    
    if(_entity.constructor!== Array)
    	return ids[0];
	else
		return ids;
};

// update entity from a JSON object. Returns the id of the updated entity.
DAO.prototype.update = function(entity) {

	this.$log.info('Updating {}[{}] entity', this.orm.table, entity!==undefined?entity[this.orm.getPrimaryKey().name]:entity);

	if(entity === undefined || entity === null){
		throw new Error('Illegal argument: entity is ' + entity);
	}	
	
	var ignoredProperties = this.orm.getMandatoryProperties()
							.filter(function(property){
								return property.allowedOps && property.allowedOps.indexOf('update')<0;
							})
							.map(function(property){
								return property.name;
							});
	this.validateEntity(entity, ignoredProperties);
    
    var parametericStatement = this.ormstatements.update.apply(this.ormstatements, [entity]);

	var dbEntity = this.createSQLEntity(entity);

    var connection = this.getConnection();
    try {
     	this.notify('beforeUpdateEntity', dbEntity);
    	var updatedRecordsCount = this.ormstatements.execute(parametericStatement, connection, dbEntity);
        this.$log.info(updatedRecordsCount ? this.orm.table+'[' + dbEntity[this.orm.getPrimaryKey().name] + '] entity updated' : 'No changes incurred in '+this.orm.table);
        
        return this;
        
    } catch(e) {
    	this.$log.error('Updating '+this.orm.table+'['+entity!==undefined?entity[this.orm.getPrimaryKey().name]:entity+'] failed', e);
		throw e;
    } finally {
        connection.close();
    }
};

// delete entity by id, or array of ids, or delete all (if not argument is provided).
DAO.prototype.remove = function() {

	var ids = [];
	if(arguments.length===0){
		ids = this.list({
			"$select": [this.orm.getPrimaryKey().name]
		}).map(function(ent){
			return ent[this.orm.getPrimaryKey().name];
		}.bind(this));
	} else {
		if(arguments[0].constructor !== Array){
			ids = [arguments[0]];
		} else {
			ids = arguments[0];
		}
	}

	this.$log.info('Deleting '+this.orm.table+((ids!==undefined && ids.length===1)?'['+ids[0]+'] entity': ids.length+' entities'));
	
	for(var i=0; i<ids.length; i++) {
	
		var id = ids[i];
       	//prevent implicit type convertion
       	if(this.orm.getPrimaryKey().type !== 'string')
       		id = parseInt(id, 10);
       		
		if(ids.length>1)
			this.$log.info('Deleting {}[{}] entity', this.orm.table, id);
	
		if(id === undefined || id === null){
			throw new Error('Illegal argument for id parameter:' + id);
		}
	
	    var connection = this.getConnection();
	    try {
	    
	    	this.notify('beforeRemoveEntity', id);
	    	
			//first we attempt to remove depndents if any
			if(this.orm.associations){
				//Remove associated dependencies
				for(var idx in Object.keys(this.orm.associations)){
					var association = this.orm.associations[idx];
					var associationName = association['name'];
					if([this.orm.ASSOCIATION_TYPES['MANY-TO-MANY'], this.orm.ASSOCIATION_TYPES['MANY-TO-ONE']].indexOf(association.type)<0){
						this.$log.info("Inspecting {}[{}}] entity's dependency '{}' for entities to delete.", this.orm.table, id, associationName);
						var associationDAO = association.targetDao ? association.targetDao() : this;
						var settings = {};
						var joinId = id;
						//check if we are joining on field, other than pk
						if(association.key!==undefined){
							var ctxEntity = this.find(id);
							joinId = ctxEntity[association.key];
						}
						settings[association.joinKey] = joinId;
						var associatedEntities;
						//associatedEntities = this.expand(associationName, id);
						associatedEntities = associationDAO.list(settings);
						if(associatedEntities && associatedEntities.length > 0){
							this.$log.info("Deleting {}[{}] entity's {} dependent {}", this.orm.table, id, associatedEntities.length, associationName);
							this.notify('beforeRemoveAssociationSet', associatedEntities, id);
							for(var j=0; j<associatedEntities.length; j++){
								var associatedEntity = associatedEntities[j];
								this.notify('beforeRemoveAssociationSetEntity', associatedEntity, associatedEntities, id);
								
								associationDAO.remove.apply(associationDAO, [associatedEntity[associationDAO.orm.getPrimaryKey().name]]);
								
							}
							this.$log.info("{}[{}] entity's {} dependent {} {} deleted.", this.orm.table, id, associatedEntities.length, associationName, associatedEntities.length>1?'entities':'entity');
						}					}
				} 
	        }
	    	//Delete by primary key value
	    	var parametericStatement = this.ormstatements["delete"].apply(this.ormstatements, [this.orm.getPrimaryKey().name]);
			var params = {};
	       	params[this.orm.getPrimaryKey().name] = id;
	       	
			var updatedRecordsCount = this.ormstatements.execute(parametericStatement, connection, params);
			
	   		this.$log.info(updatedRecordsCount>0?this.orm.table+'[' + id + '] entity deleted':'No changes incurred in '+this.orm.table);

	    } catch(e) {
			this.$log.error(e.message, e);
			throw e;
	    } finally {
	        connection.close();
	    }
	    
    }
    
};

DAO.prototype.expand = function(expansionPath, context){
	this.$log.info('Expanding for association path {} and context entity {}', expansionPath, (typeof arguments[1] !== 'object' ? 'id ': '') + JSON.stringify(arguments[1]));
	if(!expansionPath || !expansionPath.length){
		throw new Error('Illegal argument: expansionPath['+expansionPath+']');
	}
	if(!context){
		throw new Error('Illegal argument: context['+context+']');
	}
	var associationName = expansionPath.splice?expansionPath.splice(0,1):expansionPath;
	var association = this.orm.getAssociation(associationName);
	if(!associationName || !association)
		throw new Error('Illegal argument: Unknown association for this DAO [' + associationName + ']');
	var joinKey = association.joinKey;
		
	var contextEntity;
	if(context[this.orm.getPrimaryKey().name] !== undefined){
		contextEntity = context;
	} else {
		contextEntity = this.find(context);
	}

	if(!contextEntity){
		throw Error('No record found for context entity ['+context+']');
	}

	var associationTargetDAO = association.targetDao? association.targetDao.apply(this) : this;
	if(!associationTargetDAO)
		throw Error('No target association DAO instance available for association '+associationName);

	var expansion;
	var associationEntities= [];

	if(association.type===this.orm.ASSOCIATION_TYPES['ONE-TO-ONE'] || association.type===this.orm.ASSOCIATION_TYPES['MANY-TO-ONE']){
		var joinId = contextEntity[joinKey];
		this.$log.info('Expanding association type {} on {}[{}]', association.type, joinKey, joinId);
		expansion = associationTargetDAO.find.apply(associationTargetDAO, [joinId]);
		
		if(expansionPath.length>0){
			this.expand(expansionPath, expansion);
		}
	} else if(association.type===this.orm.ASSOCIATION_TYPES['ONE-TO-MANY']){
		var settings = {};
		if(association.defaults)
			settings = association.defaults;
		var key = association.key || this.orm.getPrimaryKey().name;
		var joinId = contextEntity[key];
		this.$log.info('Expanding association type {} on {}[{}]', association.type, joinKey, joinId);
		settings[joinKey] = joinId;
		associationEntities = associationEntities.concat(associationTargetDAO.list.apply(associationTargetDAO, [settings]));
		
		if(expansionPath.length>0){
			for(var i=0; i<associationEntities.length; i++){
				this.expand(expansionPath, associationEntities[i]);	
			}
		} else {
			expansion = associationEntities;
		}
	} else if(association.type===this.orm.ASSOCIATION_TYPES['MANY-TO-MANY']){
		var joinDAO = association.joinDao();
		if(!joinDAO)
			throw Error('No join DAO instance available for association ' + associationName);
		if(!joinDAO.listJoins)
			throw Error('No listJoins function in join DAO instance available for association '+associationName);
		var settings = {};
		var key = association.key || this.orm.getPrimaryKey().name;
		var joinId = contextEntity[key];
		settings[association.joinKey] = joinId;
		associationEntities = associationEntities.concat(joinDAO.listJoins.apply(joinDAO, [settings, {"sourceDao": this, "joinDao":joinDAO, "targetDao":associationTargetDAO}]));
		if(expansionPath.length>0){
			for(var i=0; i<associationEntities.length; i++){
				this.expand(expansionPath, associationEntities[i]);	
			}
		} else {
			expansion = associationEntities;
		}
	}
	return expansion;
};

/* 
	Reads a single entity by id, parsed into JSON object. 
	If requested as expanded the returned entity will comprise associated (dependent) entities too. Expand can be a string tha tis a valid association name defined in this dao orm or
	an array of such names.
*/
DAO.prototype.find = function(id, expand, select) {
	if(typeof arguments[0] === 'object'){
		id = arguments[0].id;
		expand = arguments[0].$expand || arguments[0].expand;
		select = arguments[0].$select || arguments[0].select;
	}

	this.$log.info('Finding {}[{}] entity with list parameters expand[{}], select[{}]', this.orm.table, id, expand, select);

	if(id === undefined || id === null){
		throw new Error('Illegal argument for id parameter:' + id);
	}

    var connection = this.getConnection();
    try {
        var entity;
        if(select!==undefined){
			if(select.constructor !== Array){
				if(select.constructor === String){
					select= select.split(',').map(function(sel){
						if(sel.constructor !== String)
							throw Error('Illegal argument: select array components are expected ot be strings but found ' + (typeof sel));
						return sel.trim();
					});
				} else {
					throw Error('Illegal argument: select is expected to be string or array of strings but was ' + (typeof select));
				}
			}
		}
		//ensure that joinkeys for required expands are available and not filtered by select
		if(select!==undefined && expand!==undefined){
			select.push(this.orm.getPrimaryKey().name);
			//TODO: checks
			/*for(var i in expand){
				var association = this.orm.associations[expand[i]];
				if(association && select.indexOf(association.joinKey)<1){ 
					select.push(association.joinKey);
				}
			}*/
		}
		var findQbParams = {
			select: select
		};		
        var parametericStatement = this.ormstatements.find.apply(this.ormstatements, [findQbParams]);
       	var params = {};
       	
       	//prevent implicit type convertion
       	if(this.orm.getPrimaryKey().type !== 'string')
       		id = parseInt(id, 10);
       	
       	params[this.orm.getPrimaryKey().name] = id;
       	var resultSet = this.ormstatements.execute(parametericStatement, connection, params);
 
        if (resultSet.next()) {
        	entity = this.createEntity(resultSet, select);
			if(entity){
            	this.$log.info('{}[{}] entity found', this.orm.table, id);
            	this.notify('afterFound', entity);
				if(expand!==undefined){
					if(expand.constructor !== Array){
						if(expand.constructor === String){
							expand = String(new java.lang.String(""+expand));
							expand =  expand.split(',').map(function(exp){
								if(exp.constructor !== String)
									throw Error('Illegal argument: expand array components are expected ot be strings but found ' + (typeof exp));
								return exp.trim();
							});
						} else {
							throw Error('Illegal argument: expand is expected to be string or array of strings but was ' + (typeof expand));
						}
					}
					var associationNames = this.orm.getAssociationNames();
					for(var idx in associationNames){
						var associationName = associationNames[idx];
						if(expand.indexOf(associationName)>-1){
							entity[associationName] = this.expand([associationName], entity);
						}
					}
				}		
        	} else {
	        	this.$log.info('{}[{}] entity not found', this.orm.table, id);
        	}
        } 
        return entity;
    } catch(e) {
        this.$log.error(e.message, e);
		throw e;
    } finally {
        connection.close();
    }
};

DAO.prototype.count = function() {

	this.$log.info('Counting '+this.orm.table+' entities');

    var count = 0;
    var connection = this.getConnection();
    try {
    	var parametericStatement = this.ormstatements.count.apply(this.ormstatements);
		var rs = this.ormstatements.execute(parametericStatement, connection);
        if (rs.next()) {
            count = rs.getString(1);
        }
    } catch(e) {
    	this.$log.error('Counting '+this.orm.table+' entities failed', e);
		e.errContext = parametericStatement.toString();
		throw e;
    } finally {
        connection.close();
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
DAO.prototype.list = function(settings) {
	settings = settings || {};
	var expand = settings.$expand || settings.expand;
	if(expand!==undefined){
		if(expand.constructor !== Array){
			if(expand.constructor === String){
				expand =  expand.split(',').map(function(exp){
					if(exp.constructor !== String)
						throw Error('Illegal argument: expand array components are expected ot be strings but found ' + (typeof exp));
					return exp.trim();
				});
			} else {
				throw Error('Illegal argument: expand is expected to be string or array of strings but was ' + (typeof expand));
			}
		}
	}			

	var listArgs = [];
	for(var key in settings){
		listArgs.push(' ' + key + '[' + settings[key] + ']');
	}
	
	this.$log.info('Listing {} entity collection with list operators: {}', this.orm.table, listArgs.join(','));
	
	if(settings.$select!==undefined && expand!==undefined){
		settings.$select.push(this.orm.getPrimaryKey().name);
	}
   //simplistic filtering of (only) string properties with like
   if(settings.$filter){
		settings.$filter = settings.$filter.split(',');
		var self = this;
		settings.$filter = settings.$filter.filter(function(filterField){
			var prop = self.ormstatements.orm.getProperty(filterField);
			if(prop===undefined || prop.type!=='String' || settings[prop.name]===undefined)
				return false;
			settings[prop.name] = settings[prop.name] + '%%';
			return true;
		});
	}
	
	var parametericStatement = this.ormstatements.list.apply(this.ormstatements, [settings]);
    var connection = this.getConnection();
    try {
        var entities = [];
		var resultSet = this.ormstatements.execute(parametericStatement, connection, settings);
        
        while (resultSet.next()) {
        	var entity = this.createEntity(resultSet, settings.$select);
        	if(expand){
        		var associationNames = this.orm.getAssociationNames();
				for(var idx in associationNames){
					var associationName = associationNames[idx];
					if(expand.indexOf(associationName)>-1){
						entity[associationName] = this.expand([associationName], entity);
					}
				}
        	}
        	this.notify('afterFound', entity, settings);
            entities.push(entity);
        }
        this.$log.info('{} {} entities found', entities.length, this.orm.table);
        
        return entities;
    }  catch(e) {
        this.$log.error(e.message, e);
		throw e;
    } finally {
        connection.close();
    }
};

DAO.prototype.createTable = function() {
	this.$log.info('Creating table {}', this.orm.table);
	var parametericStatement = this.ormstatements.createTable.apply(this.ormstatements);
    var connection = this.getConnection();
    try {
    	this.ormstatements.execute(parametericStatement, connection);
        this.$log.info('{} table created', this.orm.table);
        return this;
    } catch(e) {
    	this.$log.error(e.message, e);
		throw e;
    } finally {
        connection.close();
    }
    return this;
};

DAO.prototype.dropTable = function() {
	this.$log.info('Dropping table {}', this.orm.table);
	var parametericStatement = this.ormstatements.dropTable.apply(this.ormstatements);
    var connection = this.getConnection();
    try {
    	this.ormstatements.execute(parametericStatement, connection);
        this.$log.info('{} table dropped', this.orm.table);
        return this;
    } catch(e) {
    	this.$log.error(e.message, e);
		throw e;
    } finally {
        connection.close();
    }
    return this;
};


var toCamelCase = function(str){
	return str.toLowerCase().replace(/(?:_| |\b)(\w)/g, function(str, p1, offset) {
		return offset===0 ? p1 : p1.toUpperCase();
	});
};

//TODO: this must be fetched dynammically based on dialect
var typeForSql = function(name, length){
	if(name==='INTEGER'){
		return "Int";
	}
	if(['CHAR', 'VARCHAR', 'CLOB'].indexOf(name)>-1){
		return 'String';
	}
	if(name==='BIGINT'){
		return "Long";
	}
	if(name==='REAL'){
		return "Float";
	}
	if(name==='DOUBLE'){
		return "Double";
	}
	if(name==='SMALLINT'){
		return "Short";
	}
	if(name==='BOOLEAN'){
		return "Boolean";
	}		
	if(name==='TIMESTAMP'){
		return "Timestamp";
	}
	if(name==='TIME'){
		return "Time";
	}		
	if(name==='DATE'){
		return "Date";
	}
	return;
};

var fromTableDef = function(tableDef){
	var orm = {};
	orm["table"] = tableDef["name"];
	if(tableDef["columns"]){
		orm["properties"] = tableDef["columns"].map(function(columnDef, idx, arr){
			var property = {
				"name": toCamelCase(columnDef["name"]),
				"column": columnDef["name"],
				"type": typeForSql(columnDef["type"]),
				"size": columnDef["length"] !== undefined && columnDef["length"] !=="0"? parseInt(columnDef["length"], 10) : undefined,
				"id": columnDef["primaryKey"]==='true',
				"required": columnDef["nullable"] !== 'true'
			};
			return property;
		});
	};
	return orm;
};


exports.dao = function(tableDef, logCtxName, ds){
	var orm = fromTableDef (tableDef);
	return new DAO(orm, logCtxName, ds);
};

/**
 * oDefinition can be a valid path to a .table file or any other text file contianing a standard dao orm definition.
 * Or it can be table definition or standard orm definition object.
 */
exports.dao = function(oDefinition, logCtxName, ds){
	var orm;
	if(typeof oDefinition === 'string'){
		var files = require('io/v3/files');
		if(files.isReadable(oDefinition)){
			var defText = files.readText(oDefinition);
			try{
				oDefinition = JSON.parse(defText);
			} catch (parseError){
				var logger = logging.getLogger("org.eclipse.dirigible.db.dao");
				logger.error("Invalid JSON in " + oDefinition, parseError);
				throw parseError;
			}
		} else {
			throw Error('Cannot get dao definition from ' + oDefinition + '. Check path and read permissions.');
		}
	} 

	if(oDefinition["name"] && oDefinition["type"] && ["TABLE","VIEW"].indexOf(oDefinition["type"])>-1){
		orm = fromTableDef(oDefinition);
	} else {
		orm = oDefinition;
	} console.error(JSON.stringify(orm))
	return new DAO(orm, logCtxName, ds);
};