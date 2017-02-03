/* globals $ */
/* eslint-env node, dirigible */
"use strict";
var database = require("db/database");

var DAO = exports.DAO = function(orm, logCtxName){
	if(orm === undefined)
		throw Error('Illegal argument: orm['+ orm + ']');
	this.orm = require("daoism/orm").get(orm);
	this.$log = require('log/loggers').get(logCtxName || 'DAO');
	this.datasource = database.getDatasource();
	this.statements = require("daoism/statements").get();
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
			persistentItem[mandatories[i].name] = mandatories[i].dbValue.apply(this, [entity, this.orm.properties, mandatories[i]]);
		} else {
			persistentItem[mandatories[i].name] = entity[mandatories[i].name];
		}
	}
	var optionals = this.orm.getOptionalProperties();
	for(var i=0; i<optionals.length; i++){
		if(optionals[i].dbValue !== undefined){
			persistentItem[optionals[i].name] = optionals[i].dbValue.apply(this, [entity, this.orm.properties, optionals[i]]);
		} else {
			persistentItem[optionals[i].name] = entity[optionals[i].name] === undefined ? null : entity[optionals[i].name];
		} 
	}
	var msgIdSegment = persistentItem[this.orm.getPrimaryKey().name]?"["+persistentItem[this.orm.getPrimaryKey().name]+"]":"";
	this.$log.info("Transformation to " + this.orm.dbName + msgIdSegment + " DB JSON object finished");
	return persistentItem;
};

//create entity as JSON object from ResultSet current Row
DAO.prototype.createEntity = function(resultSet) {
    var entity = {};
    for(var i=0; i<this.orm.properties.length; i++){
    	var prop = this.orm.properties[i];
    	entity[prop.name] = resultSet['get'+prop.type](prop.dbName);
    	if(prop.value){
    		entity[prop.name] = prop.value(entity[prop.name]);
    	}
    }
    
    for(var key in Object.keys(entity)){
		if(entity[key] === null)
			entity[key] = undefined;
	}
    this.$log.info("Transformation from "+this.orm.dbName+"["+entity[this.orm.getPrimaryKey().name]+"] DB JSON object finished");
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
			throw new Error('Illegal ' + propName + ' attribute value in '+this.orm.dbName+' entity: ' + propValue);
		}
	}
};

DAO.prototype.insert = function(entity){
	this.$log.info('Inserting '+this.orm.dbName+' entity');
	this.validateEntity(entity, [this.orm.getPrimaryKey().name]);

    var dbEntity = this.createSQLEntity(entity);

    var connection = this.datasource.getConnection();
    try {
        var parametericStatement = this.orm.statements.insert.apply(this.orm);

        var id = this.datasource.getSequence(this.orm.dbName+'_'+this.orm.getPrimaryKey.name.toUpperCase()).next();
        dbEntity[this.orm.getPrimaryKey().name] = id;
		var updatedRecordCount = this.statements.execute(parametericStatement, connection, dbEntity);
		this.notify('afterInsert', dbEntity);
		this.notify('beforeInsertAssociationSets', dbEntity);
		if(this.orm.associationSets && Object.keys(this.orm.associationSets).length){
			//Insert dependencies if any are provided inline with this entity
			this.$log.info('Inserting association sets for '+this.orm.dbName + '['+dbEntity[this.orm.getPrimaryKey().name]+']');
			for(var idx in Object.keys(this.orm.associationSets)){
				var associationName = Object.keys(this.orm.associationSets)[idx];
				if(['many-to-many', 'many-to-one'].indexOf(this.orm.associationSets[associationName].associationType)>-1){
					if(dbEntity[associationName] && dbEntity[associationName].length>0){
						var associationDAO = this.orm.associationSets[associationName].dao.apply(this);
						this.notify('beforeInsertAssociationSet', dbEntity[associationName], dbEntity);
						for(var j=0; j<dbEntity[associationName].length; j++){
			        		var associatedEntity = dbEntity[associationName][j];
			        		var associatedEntityJoinKey = this.orm.associationSets[associationName].joinKey;
			        		associatedEntity[associatedEntityJoinKey] = dbEntity[this.orm.getPrimaryKey().name];
			        		this.notify('beforeInsertAssociationSetEntity', dbEntity[associationName], dbEntity);
							associationDAO.insert(associatedEntity);
			    		}
			    		this.notify('afterInsertAssociationSet', dbEntity[associationName], dbEntity);
					}				
				}
			}		
		}
		
        this.$log.info(updatedRecordCount>0 ? this.orm.dbName+'[' +  dbEntity[this.orm.getPrimaryKey().name] + '] entity inserted' : 'No changes incurred in '+this.orm.dbName);

        return dbEntity[this.orm.getPrimaryKey().name];

    } catch(e) {
    	this.$log.error(e.message, e);
    	this.$log.info('Rolling back changes after failed '+this.orm.dbName+'[' +  dbEntity[this.orm.getPrimaryKey().name] + '] insert. ');
		if(dbEntity[this.orm.getPrimaryKey().name]){
			try{
				this.remove(dbEntity[this.orm.getPrimaryKey().name]);
			} catch(err) {
				this.$log.error('Could not rollback changes after failed '+this.orm.dbName+'[' +  dbEntity[this.orm.getPrimaryKey().name] + '] insert. ', err);
			}
		}
		throw e;
    } finally {
        connection.close();
    }
};

// update entity from a JSON object. Returns the id of the updated entity.
DAO.prototype.update = function(entity) {

	this.$log.info('Updating '+this.orm.dbName+'[' + entity!==undefined?entity[this.orm.getPrimaryKey().name]:entity + '] entity');

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
    
    var parametericStatement = this.orm.statements.update.apply(this.orm, [entity]);

	var dbEntity = this.createSQLEntity(entity);

    var connection = this.datasource.getConnection();
    try {
     	this.notify('beforeUpdateEntity', dbEntity);
    	var updatedRecordsCount = this.statements.execute(parametericStatement, connection, dbEntity);
        this.$log.info(updatedRecordsCount ? this.orm.dbName+'[' + dbEntity[this.orm.getPrimaryKey().name] + '] entity updated' : 'No changes incurred in '+this.orm.dbName);
        
        return this;
        
    } catch(e) {
    	this.$log.error(e.message, e);
		throw e;
    } finally {
        connection.close();
    }
};

// delete entity by id. Returns the id of the deleted entity.
DAO.prototype.remove = function(id) {

	this.$log.info('Deleting '+this.orm.dbName+'[' + id + '] entity');

	if(id === undefined || id === null){
		throw new Error('Illegal argument for id parameter:' + id);
	}

    var connection = this.datasource.getConnection();
    try {
    
    	var parametericStatement = this.orm.statements["delete"].apply(this.orm);
    	this.notify('beforeRemoveEntity', id);
    	
		//first we attempt to remove depndents if any
		if(this.orm.associationSets && Object.keys(this.orm.associationSets).length){
			//Remove associated dependencies
			for(var idx in Object.keys(this.orm.associationSets)){
				var associationName = Object.keys(this.orm.associationSets)[idx];
				if(['many-to-many', 'many-to-one'].indexOf(this.orm.associationSets[associationName].associationType)<0){
					this.$log.info('Inspecting '+this.orm.dbName+'[' + id + '] entity\'s dependency \''+ associationName + '\' for entities to delete.');
					var associationDAO = (this.orm.associationSets[associationName].dao && this.orm.associationSets[associationName].dao.apply(this)) || this;
					var settings = {};
					settings[this.orm.associationSets[associationName].joinKey] = id;
					var associatedEntities;
					//associatedEntities = this.expand(associationName, id);
					associatedEntities = associationDAO.list(settings);
					if(associatedEntities && associatedEntities.length > 0){
						this.$log.info('Deleting '+this.orm.dbName+'['+id+'] entity\'s '+associatedEntities.length+' dependent ' + associationName);
						
						this.notify('beforeRemoveAssociationSet', associatedEntities, id);
						
						for(var j=0; j<associatedEntities.length; j++){
							var associatedEntity = associatedEntities[j];
							
							this.notify('beforeRemoveAssociationSetEntity', associatedEntity, associatedEntities, id);
							
							associationDAO.remove(associatedEntity[associationDAO.orm.getPrimaryKey().name]);
						}
						this.$log.info(this.orm.dbName+'['+id+'] entity\'s '+associatedEntities.length+' dependent ' + associationName + ' '+ associatedEntities.length>1?'entities':'entity' +' deleted.');
					}
				}
			} 
        }
    	
		var params = {};
       	params[this.orm.getPrimaryKey().name] = id;
		var updatedRecordsCount = this.statements.execute(parametericStatement, connection, params);
   		this.$log.info(updatedRecordsCount>0?this.orm.dbName+'[' + id + '] entity deleted':'No changes incurred in '+this.orm.dbName);

        return this;

    } catch(e) {
		this.$log.error(e.message, e);
		throw e;
    } finally {
        connection.close();
    }
    
};

DAO.prototype.expand = function(expansionPath, contextId){
	if(!expansionPath || !expansionPath.length){
		throw Error('Illegal argument: expansionPath['+expansionPath+']');
	}
	if(!contextId){
		throw Error('Illegal argument: contextId['+contextId+']');
	}
	this.$log.info('Expanding for relation path ' + expansionPath + ' and context entity id ' + contextId);
	var associationName = expansionPath.splice?expansionPath.splice(0,1):expansionPath;
	
	if(!associationName || !this.orm.associationSets[associationName])
		throw Error('Unknown association for this DAO: ' + associationName);
	
	var contextEntity = this.find(contextId);
	if(!contextEntity){
		throw Error('No record found for context entity id['+contextId+']');
	}

	var associationSet = [];
	var associationSetDAO = (this.orm.associationSets[associationName].dao && this.orm.associationSets[associationName].dao()) || this;

	if(this.orm.associationSets[associationName].associationType==='one-to-one' || this.orm.associationSets[associationName].associationType==='many-to-one'){
		var joinId = contextEntity[this.orm.associationSets[associationName].joinKey];
		var daoOne = this.orm.associationSets[associationName].dao ? this.orm.associationSets[associationName].dao() : this;
		var expandedEntity = associationSetDAO.find.apply(associationSetDAO, [joinId, undefined, {"one":daoOne}]);
		if(expansionPath.length<1){
			return expandedEntity;
		} else {
			this.expand(expansionPath, expandedEntity[associationSetDAO.orm.getPrimaryKey().name]);
		}
	} else if(this.orm.associationSets[associationName].associationType==='one-to-many'){
		var settings = {};
		var joinId = contextEntity[this.orm.getPrimaryKey().name];
		settings[this.orm.associationSets[associationName].joinKey] = joinId;
		if(this.orm.associationSets[associationName].defaults){
			for(var i in this.orm.associationSets[associationName].defaults){
				settings[i] = this.orm.associationSets[associationName].defaults[i];
			}
		}
		var daoMany = this.orm.associationSets[associationName].dao? this.orm.associationSets[associationName].dao() : this;
		associationSet = associationSet.concat(associationSetDAO.list.apply(associationSetDAO, [settings, {"many":daoMany}]));
		if(expansionPath.length<1){
			return associationSet;
		} else {
			for(var i=0; i<associationSet.length; i++){
				this.expand(expansionPath, associationSet[i][associationSetDAO.orm.getPrimaryKey().name]);	
			}
		}
	} else if(this.orm.associationSets[associationName].associationType==='many-to-many'){
		var settings = {};
		var associationSetMDAO = this;
		var joinTableDAO = (this.orm.associationSets[associationName].daoJoin && this.orm.associationSets[associationName].daoJoin());
		var associationSetNDAO = (this.orm.associationSets[associationName].daoN && this.orm.associationSets[associationName].daoN());
		var joinId = contextEntity[this.orm.getPrimaryKey().name];
		settings[this.orm.associationSets[associationName].joinKey] = joinId;
		associationSet = associationSet.concat(joinTableDAO.listJoins.apply(joinTableDAO, [settings, {"m":associationSetMDAO, "n":associationSetNDAO, "join": joinTableDAO}]));
		if(expansionPath.length<1){
			return associationSet;
		} else {
			for(var i=0; i<associationSet.length; i++){
				this.expand(expansionPath, associationSet[i][associationSetDAO.orm.getPrimaryKey().name]);	
			}
		}
	}
	return;
};

/* 
	Reads a single entity by id, parsed into JSON object. 
	If requested as expanded (=true) the returned entity will comprise associated (dependent) entities too. 
*/
DAO.prototype.find = function(id, select) {
	this.$log.info('Finding '+this.orm.dbName+'[' +  id + '] entity');

	if(id === undefined || id === null){
		throw new Error('Illegal argument for id parameter:' + id);
	}

    var connection = this.datasource.getConnection();
    try {
        var entity;
        var parametericStatement = this.orm.statements.find.apply(this.orm);
       	var params = {};
       	params[this.orm.getPrimaryKey().name] = id;
       	var resultSet = this.statements.execute(parametericStatement, connection, params);
 
        if (resultSet.next()) {
        	entity = this.createEntity(resultSet);
			if(entity){
            	this.$log.info(this.orm.dbName+'[' +  id + '] entity found');
            	this.notify('afterFound', entity);
				if(select!==undefined){
					if(select.constructor !== Array){
						if(select.constructor === String){
							select = String(new java.lang.String(""+select));
							select =  select.split(',').map(function(sel){
								if(select.constructor !== String)
									throw Error('Illegal argument: select array components are expected ot be strings but found ' + (typeof sel));
								return sel.trim();
							});
						} else {
							throw Error('Illegal argument: select is expected to be string or array of strings but was ' + (typeof select));
						}
					}
					for(var idx in Object.keys(this.orm.associationSets)){
						var associationName = Object.keys(this.orm.associationSets)[idx];
						if(select.indexOf(associationName)>-1){
							entity[associationName] = this.expand([associationName], id);
						}
					}
				}		
        	} else {
	        	this.$log.info(this.orm.dbName+'[' +  id + '] entity not found');
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

	this.$log.info('Counting '+this.orm.dbName+' entities');

    var count = 0;
    var connection = this.datasource.getConnection();
    try {
    	var parametericStatement = this.orm.statements.count.apply(this.orm);
		var rs = this.statements.execute(parametericStatement, connection);
        if (rs.next()) {
            count = rs.getInt(1);
        }
    } catch(e) {
        console.error(e.message);
    	console.error(e.stack);
		e.errContext = parametericStatement.toString();
		throw e;
    } finally {
        connection.close();
    }
    
    this.$log.info('' + count + ' '+this.orm.dbName+' entities counted');

    return count;
};

DAO.prototype.list = function(settings) {
	var expanded = settings.expanded;
	var select = settings.select;
	if(expanded || select){
		if(select){
			if(select.constructor !== Array){
				if(select.constructor === String){
					select = String(new java.lang.String(""+select));
					select = select.split(',').map(function(sel){
						if(select.constructor !== String)
							throw Error('Illegal argument: select array components are expected ot be strings but found ' + (typeof sel));
						return sel.trim();
					});
				} else {
					throw Error('Illegal argument: select expected to be string or array of strings but was ' + (typeof select));
				}
			}
		} else {
			select = Object.keys(this.orm.associationSets);
		}        		
	}

	var listArgs = [];
	for(var key in settings){
		if(settings[key] && settings[key].constructor === Array)
			listArgs.push(' ' + key + settings[key]);
		else
			listArgs.push(' ' + key + '[' + settings[key] + ']');
	}
	
	this.$log.info('Listing '+this.orm.dbName+' entity collection with list operators:' + listArgs.join(','));
	var parametericStatement = this.orm.statements.list.apply(this.orm, [settings]);
    var connection = this.datasource.getConnection();
    try {
        var entities = [];
        settings.select = select;
		var resultSet = this.statements.execute(parametericStatement, connection, settings);
/*        var sql = this.sql.list.apply(this,[settings]);
		this.$log.info('Prepare statement: ' + sql);
        var statement = connection.prepareStatement(sql);
*/	
		//Bind statement parameters if any
/*		var self = this;
		var keyDefinitions = this.associationKeys().filter(function(joinKey){
        	for(var settingName in settings){
        		if(settingName === joinKey)
        			return joinKey;
        	}
        	return;
        }).filter(function(keyDef){
        	return keyDef!==undefined;
        }).map(function(key){
        	var matchedDefinition = self.orm.properties.filter(function(property){
        		return key === property.name;
        	});
        	return matchedDefinition?matchedDefinition[0]:undefined;
        }).filter(function(keyDef){
        	return keyDef!==undefined;
        });
        
        for(var i=0; i<keyDefinitions.length; i++){
        	var val = settings[keyDefinitions[i].name];
        	this.$log.info('Binding to parameter[' + (i+1) + ']:' + val);
        	statement['set'+keyDefinitions[i].type]((i+1), val);
        }
		
        var resultSet = statement.executeQuery();*/
        
        while (resultSet.next()) {
        	var entity = this.createEntity(resultSet);
        	if(select && this.orm.associationSets){
				for(var idx in Object.keys(this.orm.associationSets)){
					var associationName = Object.keys(this.orm.associationSets)[idx];
					if(select.indexOf(associationName)>-1){
						var id = entity[this.orm.getPrimaryKey().name];
						entity[associationName] = this.expand([associationName], id);
					}
				}
        	}
        	this.notify('afterFound', entity);
            entities.push(entity);
        }
        
        this.$log.info('' + entities.length +' '+this.orm.dbName+' entities found');
        
        return entities;
    }  catch(e) {
        this.$log.error(e.message, e);
		throw e;
    } finally {
        connection.close();
    }
};

exports.get = function(orm, logCtxName){
	return new DAO(orm, logCtxName);
};