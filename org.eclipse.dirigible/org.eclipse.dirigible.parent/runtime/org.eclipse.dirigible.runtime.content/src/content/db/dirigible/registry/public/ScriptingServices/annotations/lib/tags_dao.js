/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var database = require("db/database");

var datasource = database.getDatasource();

var persistentProperties = {
	mandatory: ["id"],
	optional: ["defaultLabel", "uri"]
};

var $log = require("logging/logger").logger;
$log.ctx = "Tags DAO";

// Parse JSON entity into SQL and insert in db. Returns the new record id.
exports.insert = function(entity) {

	$log.info('Inserting ANN_TAG entity');

	if(entity === undefined || entity === null){
		throw new Error('Illegal argument: entity is ' + entity);
	}
	
	for(var i = 0; i< persistentProperties.mandatory.length; i++){
		var propName = persistentProperties.mandatory[i];
		if(propName==='id')
			continue;//Skip validaiton check for id. It's epxected to be null on insert.
		var propValue = entity[propName];
		if(propValue === undefined || propValue === null){
			throw new Error('Illegal ' + propName + ' attribute value in DIS_BOARD entity for insert: ' + propValue);
		}
	}

    entity = createSQLEntity(entity);

    var connection = datasource.getConnection();
    try {
        var sql = "INSERT INTO ANN_TAG (";
        sql += "ANN_ID, ANN_DEFAULT_LABEL, ANN_URI) "; 
        sql += "VALUES (?,?,?)";

        var statement = connection.prepareStatement(sql);
        
        var i = 0;
        entity.id = datasource.getSequence('ANN_TAGS_ANN_ID').next();
        statement.setLong(++i,  entity.id);
        statement.setString(++i, entity.defaultLabel);        
        statement.setString(++i, entity.uri);

        statement.executeUpdate();

        $log.info('ANN_TAG[' +  entity.id + '] entity inserted');

        return entity.id;

    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }
};

// Reads a single entity by id, parsed into JSON object 
exports.find = function(id) {

	$log.info('Finding ANN_TAG[' + id + '] entity');

	if(id === undefined || id === null){
		throw new Error('Illegal argument for id parameter:' + id);
	}

    var connection = datasource.getConnection();
    try {
        var entity;
        var sql = "SELECT * FROM ANN_TAG WHERE " + exports.pkToSQL();
     
        var statement = connection.prepareStatement(sql);
        statement.setLong(1, id);

        var resultSet = statement.executeQuery();
        if (resultSet.next()) {
        	entity = createEntity(resultSet);
        	$log.info('ANN_TAG[' + id + '] entity found');
        } else {
        	$log.info('ANN_TAG[' + id + '] not found');
        }
        return entity;
    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }
};

// Reads a single entity by id, parsed into JSON object 
exports.findByTagValue = function(tag) {

	$log.info('Finding ANN_TAG entity with label[' + tag + ']');

	if(tag=== undefined || tag === null){
		throw new Error('Illegal argument for tag parameter:' + tag);
	}

    var connection = datasource.getConnection();
    try {
        var entity;
        var sql = "SELECT * FROM ANN_TAG WHERE ANN_DEFAULT_LABEL = ?";
     
        var statement = connection.prepareStatement(sql);
        statement.setString(1, tag);

        var resultSet = statement.executeQuery();
        if (resultSet.next()) {
        	entity = createEntity(resultSet);
        	$log.info('ANN_TAG[' + entity.id + '] entity with label[' + tag + '] found');
        } else {
        	$log.info('ANN_TAG[' + entity.id + '] entity with label[' + tag + '] not found');
        }
        return entity;
    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }
};


// Read all entities, parse and return them as an array of JSON objets
exports.list = function(limit, offset, sort, order, expanded, tag) {

	$log.info('Listing ANN_TAG entity collection expanded['+expanded+'] with list operators: limit['+limit+'], offset['+offset+'], sort['+sort+'], order['+order+'], tag['+tag+']');
	
    var connection = datasource.getConnection();
    try {
        var entities = [];
        var sql = "SELECT";
        if (limit !== null && offset !== null) {
            sql += " " + datasource.getPaging().genTopAndStart(limit, offset);
        }
        sql += " * FROM ANN_TAG";
        if (tag !== undefined && tag !== null) {
        	sql += " WHERE DEFAULT_LABEL LIKE '" + tag + "%%'";
    	}
        if (sort !== undefined && sort !== null) {
            sql += " ORDER BY " + sort;
        }
        if ((sort !== undefined && sort !== null) && (sort !== undefined && order !== null)) {
            sql += " " + order;
        }
        if ((limit !== undefined && limit !== null) && (offset !== undefined && offset !== null)) {
            sql += " " + datasource.getPaging().genLimitAndOffset(limit, offset);
        }

        var statement = connection.prepareStatement(sql);
        var resultSet = statement.executeQuery();
        while (resultSet.next()) {
        	var entity = createEntity(resultSet);
            entities.push(entity);
        }
        
        $log.info('' + entities.length +' ANN_TAG entities found');
        
        return entities;
    }  catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }
};

//create entity as JSON object from ResultSet current Row
function createEntity(resultSet) {
    var entity = {};
	entity.id = resultSet.getLong("ANN_ID");
    entity.defaultLabel = resultSet.getString("ANN_DEFAULT_LABEL");	
    entity.uri = resultSet.getString("ANN_URI");    
	for(var key in Object.keys(entity)){
		if(entity[key] === null)
			entity[key] = undefined;
	}	
    $log.info("Transformation from ANN_TAG["+entity.id+"] DB JSON object finished");
    return entity;
}


//Prepare a JSON object for insert into DB
function createSQLEntity(entity) {
	var persistentItem = {};
	for(var i=0;i<persistentProperties.mandatory.length;i++){
		persistentItem[persistentProperties.mandatory[i]] = entity[persistentProperties.mandatory[i]];
	}
	for(var i=0;i<persistentProperties.optional.length;i++){
		if(entity[persistentProperties.optional[i]] !== undefined){
			persistentItem[persistentProperties.optional[i]] = entity[persistentProperties.optional[i]];
		} else {
			persistentItem[persistentProperties.optional[i]] = null;
		}
	}	
		
	$log.info("Transformation to ANN_TAG["+entity.id+"] DB JSON object finished");
	return persistentItem;
}

// update entity from a JSON object. Returns the id of the updated entity.
exports.update = function(entity) {

	$log.info('Updating ANN_TAG[' + entity!==undefined?entity.id:entity + '] entity');

	if(entity === undefined || entity === null){
		throw new Error('Illegal argument: entity is ' + entity);
	}
	
	for(var i = 0; i< persistentProperties.mandatory.length; i++){
		var propName = persistentProperties.mandatory[i];
		var propValue = entity[propName];
		if(propValue === undefined || propValue === null){
			throw new Error('Illegal ' + propName + ' attribute value in ANN_TAG entity for update: ' + propValue);
		}
	}
	
	entity = createSQLEntity(entity);
	
    var connection = datasource.getConnection();
    try {
    
        var sql = "UPDATE ANN_TAG";
        sql += " SET ANN_DEFAULT_LABEL=?, ANN_URI=?"; 
        sql += " WHERE ANN_ID = ?";
        var statement = connection.prepareStatement(sql);
        var i = 0;
        statement.setString(++i, entity.defaultLabel);        
        statement.setString(++i, entity.uri);
        var id = entity.id;
        statement.setLong(++i, id);
        statement.executeUpdate();
            
        $log.info('ANN_TAG[' + id + '] entity updated');
        
        return this;
        
    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }
};

// delete entity by id. Returns the id of the deleted entity.
exports.remove = function(id) {

	$log.info('Deleting ANN_TAG[' + id + '] entity');

    var connection = datasource.getConnection();
    try {
    
    	var sql = "DELETE FROM ANN_TAG";
    	
    	if(id !== null){
    	 	sql += " WHERE " + exports.pkToSQL();
    	 	if(id.constructor === Array){
    	 		sql += "IN ("+id.join(',')+")";
    	 	} else {
    	 		" = "  + id;
    	 	}
		}

        var statement = connection.prepareStatement(sql);
        if(id!==null && id.constructor !== Array){
        	statement.setString(1, id);
        }
        statement.executeUpdate();
                
        $log.info('ANN_TAG[' + id + '] entity deleted');                
        
        return this;

    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }
};

exports.count = function() {

	$log.info('Counting ANN_TAG entities');

    var count = 0;
    var connection = datasource.getConnection();
    try {
    	var sql = 'SELECT COUNT(*) FROM ANN_TAG';
        var statement = connection.prepareStatement(sql);
        var rs = statement.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }
    
    $log.info('' + count + ' ANN_TAG entities counted');

    return count;
};

exports.getPrimaryKeys = function() {
    var result = [];
    var i = 0;
    result[i++] = 'ANN_ID';
    if (result === 0) {
        throw new Error("There is no primary key");
    } else if(result.length > 1) {
        throw new Error("More than one Primary Key is not supported.");
    }
    return result;
};

exports.getPrimaryKey = function() {
	return exports.getPrimaryKeys()[0].toLowerCase();
};

exports.pkToSQL = function() {
    var pks = exports.getPrimaryKeys();
    return pks[0] + " = ?";
};
