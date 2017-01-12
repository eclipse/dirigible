/* globals $ */
/* eslint-env node, dirigible */
(function(){
"use strict";

var database = require("db/database");
//var userLib = require("net/http/user");

var datasource = database.getDatasource();

var persistentProperties = {
	mandatory: ["idmu_id", "uname"],
	optional: ["pic"]
};

var $log = require("logging/lib/logger").logger;
$log.ctx = "User DAO";

// Parse JSON entity into SQL and insert in db. Returns the new record id.
exports.insert = function(entity, cascaded) {

	$log.info('Inserting IDM_USER entity cascaded['+cascaded+']');

	if(entity === undefined || entity === null){
		throw new Error('Illegal argument: entity is ' + entity);
	}
	
	for(var i = 0; i< persistentProperties.mandatory.length; i++){
		var propName = persistentProperties.mandatory[i];
		if(propName==='idmu_id')
			continue;//Skip validaiton check for id. It's epxected to be null on insert.
		var propValue = entity[propName];
		if(propValue === undefined || propValue === null){
			throw new Error('Illegal ' + propName + ' attribute value in IDM_USER entity for insert: ' + propValue);
		}
	}

	if(cascaded === undefined || cascaded === null){
		cascaded = false;
	}

    entity = createSQLEntity(entity);

    var connection = datasource.getConnection();
    try {
        var sql = "INSERT INTO IDM_USER (";
        sql += "IDMU_ID, IDMU_UNAME, IDMU_PIC) "; 
        sql += "VALUES (?,?,?)";

        var statement = connection.prepareStatement(sql);
        
        i = 0;
        entity.idmu_id = datasource.getSequence('IDM_USER_IDMU_ID').next();
        statement.setInt(++i,  entity.idmu_id);
        statement.setString(++i, entity.uname);        
        statement.setString(++i, entity.pic);        
        
        statement.executeUpdate();

        $log.info('IDM_USER entity inserted with id[' +  entity.idmu_id + ']');

        return entity.idmu_id;

    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }
};

// Reads a single entity by id, parsed into JSON object 
exports.find = function(id) {

	$log.info('Finding IDM_USER entity with id[' + id + ']');

	if(id === undefined || id === null){
		throw new Error('Illegal argument for id parameter:' + id);
	}

    var connection = datasource.getConnection();
    try {
        var entity;
        var sql = "SELECT * FROM IDM_USER WHERE " + exports.pkToSQL();
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, id);

        var resultSet = statement.executeQuery();
        if (resultSet.next()) {
        	entity = createEntity(resultSet);
			if(entity)
            	$log.info('IDM_USER entity with id[' + id + '] found');
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
exports.list = function(limit, offset, sort, order, expanded, username) {

	$log.info('Listing IDM_USER entity collection expanded['+expanded+'] with list operators: limit['+limit+'], offset['+offset+'], sort['+sort+'], order['+order+'], entityName['+username+']');
	
    var connection = datasource.getConnection();
    try {
        var entities = [];
        var sql = "SELECT";
        if (limit !== null && offset !== null) {
            sql += " " + datasource.getPaging().genTopAndStart(limit, offset);
        }
        
        sql += " * FROM IDM_USER";
        if (username !== undefined && username !== null) {
        	sql += " WHERE IDMU_UNAME LIKE '" + username + "%%'";
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
        
        $log.info('' + entities.length +' IDM_USER entities found');
        
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
	entity.idmu_id = resultSet.getInt("IDMU_ID");
    entity.uname = resultSet.getString("IDMU_UNAME");
   	entity.pic = resultSet.getString("IDMU_PIC");
	for(var key in Object.keys(entity)){
		if(entity[key] === null)
			entity[key] = undefined;
	}
    $log.info("Transformation from DB JSON object finished");
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
	$log.info("Transformation to DB JSON object finished");
	return persistentItem;
}

// update entity from a JSON object. Returns the id of the updated entity.
exports.update = function(entity) {

	$log.info('Updating IDM_USER entity with id[' + entity!==undefined?entity.idmu_id:entity + ']');

	if(entity === undefined || entity === null){
		throw new Error('Illegal argument: entity is ' + entity);
	}	
	
	for(var i = 0; i< persistentProperties.mandatory.length; i++){
		var propName = persistentProperties.mandatory[i];
		var propValue = entity[propName];
		if(propValue === undefined || propValue === null){
			throw new Error('Illegal ' + propName + ' attribute value in IDM_USER entity for update: ' + propValue);
		}
	}
	
	entity = createSQLEntity(entity);

    var connection = datasource.getConnection();
    try {
        var sql = "UPDATE IDM_USER SET IDMU_UNAME=?, IDMU_PIC=? WHERE IDMU_ID=?";
        var statement = connection.prepareStatement(sql);
        
        var i=0;
		statement.setString(++i, entity.uname);
		statement.setString(++i, entity.pic);
       	var id = entity.idmu_id;
	    statement.setInt(++i, id);
	    
        statement.executeUpdate();
            
        $log.info('IDM_USER entity with idmu_id[' + id + '] updated');
                
        return this;

    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }
};

// delete entity by id. Returns the id of the deleted entity.
exports.remove = function(id, cascaded) {

	$log.info('Deleting IDM_USER entity with id[' + id + '], cascaded['+cascaded+']');

    var connection = datasource.getConnection();
    try {
    
    	var sql = "DELETE FROM IDM_USER";
    	
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
                
        $log.info('IDM_PIC entity with idmp_id[' + id + '] deleted');                
        
        return this;

    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }
};

exports.count = function() {

	$log.info('Counting IDM_USER entities');

    var count = 0;
    var connection = datasource.getConnection();
    try {
    	var sql = 'SELECT COUNT(*) FROM IDM_USER';
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
    
    $log.info('' + count + ' IDM_USER entities counted');

    return count;
};

exports.getPrimaryKeys = function() {
    var result = [];
    var i = 0;
    result[i++] = 'IDMU_ID';
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

})();
