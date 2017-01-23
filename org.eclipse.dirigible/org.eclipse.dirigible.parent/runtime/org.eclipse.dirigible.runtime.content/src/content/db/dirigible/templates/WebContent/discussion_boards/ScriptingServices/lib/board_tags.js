/* globals $ */
/* eslint-env node, dirigible */
(function(){
"use strict";

var database = require("db/database");
var datasource = database.getDatasource();

var log = require("logging/logger").logger;
log.ctx = "BoardTags DAO";

exports.listBoardTags = function(id){

	log.info('Finding ${packageName.toUpperCase()}_BOARD_TAG entities related to ${packageName.toUpperCase()}_BOARD['+id+']');

	if(id === undefined || id === null){
		throw new Error('Illegal argument for id parameter:' + id);
	}

    var connection = datasource.getConnection();
    try {
        var sql = "SELECT * FROM ANN_TAG LEFT JOIN ${packageName.toUpperCase()}_BOARD_TAG ON ${packageName.toUpperCase()}BT_ANN_ID=ANN_ID WHERE ${packageName.toUpperCase()}BT_${packageName.toUpperCase()}B_ID=?";
     
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, id);

        var resultSet = statement.executeQuery();
        var tagEntities = [];
        while (resultSet.next()) {
        	var tagEntity = {
        		id: resultSet.getInt("ANN_ID"),
			    defaultLabel: resultSet.getString("ANN_DEFAULT_LABEL"),
			    uri: resultSet.getString("ANN_URI")
        	};
        	tagEntities.push(tagEntity);
        } 
        log.info(tagEntities.length+' ${packageName.toUpperCase()}_BOARD_TAG entities related to ${packageName.toUpperCase()}_BOARD[' + id+ '] found');
        return tagEntities;
    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }	
};

exports.tag = function(id, tags, createOnDemand){
	var tagsLib = require('annotations/lib/tags_dao');
	var connection = datasource.getConnection();
	try{
	
		var existingBoardTagEntities = exports.listBoardTags(id);
		var existingBoardTags = [];
		if(existingBoardTagEntities!==null){
			existingBoardTags = existingBoardTagEntities.map(function(tagEntity){
				return tagEntity.defaultLabel;
			});
		}
	
		for(var i=0; i < tags.length; i++){
			
			if(existingBoardTags.indexOf(tags[i])>-1)
				continue;
			
			var tagEntity = tagsLib.findByTagValue(tags[i]);
			
			var tagId = tagEntity && tagEntity.id;
			if(!tagEntity && createOnDemand){
				tagId = tagsLib.insert({
										"defaultLabel": tags[i],
										"uri": tags[i]
									});								
			}

			log.info('Inserting ${packageName.toUpperCase()}_BOARD_TAG entity relation between ANN_TAG['+tagId+'] entity and ${packageName.toUpperCase()}_BOARD['+id+'] entity');
			
			var sql =  "INSERT INTO ${packageName.toUpperCase()}_BOARD_TAG (";
	        	sql += "${packageName.toUpperCase()}BT_ID, ${packageName.toUpperCase()}BT_${packageName.toUpperCase()}B_ID, ${packageName.toUpperCase()}BT_ANN_ID) "; 
	        	sql += "VALUES (?,?,?)";
	
	        var statement = connection.prepareStatement(sql);
	        
	        var j = 0;
	        var boardTagId = datasource.getSequence('${packageName.toUpperCase()}_BOARD_TAG_${packageName.toUpperCase()}BT_ID').next();
	        statement.setLong(++j, boardTagId);
	        statement.setInt(++j, id);        
	        statement.setLong(++j, tagId);        
		    
		    statement.executeUpdate();
	    	
	    	log.info('${packageName.toUpperCase()}_BOARD_TAG[' +  boardTagId + '] entity relation for ANN_TAG['+tagId+'] and ${packageName.toUpperCase()}_BOARD['+id+'] entity inserted');
		}
	} catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }

};

exports.untag = function(id, tags){
	log.info('Removing ${packageName.toUpperCase()}_BOARD_TAG entity relations to ${packageName.toUpperCase()}_BOARD['+id+'] entity');
	var connection = datasource.getConnection();
	try{
	
		var existingBoardTagEntities = exports.listBoardTags(id);
		var existingBoardTags = [];
		if(existingBoardTagEntities!==null){
			existingBoardTags = existingBoardTagEntities.map(function(tagEntity){
				return tagEntity.defaultLabel;
			});
		}
	
		for(var i=0; i < tags.length; i++){
			
			if(existingBoardTags.indexOf(tags[i])<0)
				continue;
			
			var tagEntity = existingBoardTagEntities[existingBoardTags.indexOf(tags[i])];
			
			var sql =  "DELETE FROM ${packageName.toUpperCase()}_BOARD_TAG ";
	        	sql += "WHERE ${packageName.toUpperCase()}BT_${packageName.toUpperCase()}B_ID=? AND ${packageName.toUpperCase()}BT_ANN_ID=? "; 
	        	sql += "VALUES (?,?)";
	
	        var statement = connection.prepareStatement(sql);
	        
	        var j = 0;
	        statement.setInt(++j, id);        
	        statement.setString(++j, tagEntity.id);        
		    
		    statement.executeUpdate();
	    	
	    	log.info('${packageName.toUpperCase()}_BOARD_TAG entity relation between ${packageName.toUpperCase()}_BOARD[' + id + '] entity and ANN_TAG['+tagEntity.id+'] removed');
		}
	} catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }

};

exports.setTags = function(id, tags, createOnDemand){
	log.info('Inserting ${packageName.toUpperCase()}_BOARD_TAG entity relations to ${packageName.toUpperCase()}_BOARD[' +  id+ '] entity');
	var boardTags = exports.listBoardTags(id);	
	var sql;
	try{ 
		var connection = datasource.getConnection();
		for(var i=0; i < boardTags.length; i++){
			log.info('Removing ${packageName.toUpperCase()}_BOARD_TAG entity relation between ${packageName.toUpperCase()}_BOARD['+id+'] entity and ANN_TAG['+boardTags[i].id+']');
			sql =  "DELETE FROM ${packageName.toUpperCase()}_BOARD_TAG ";
	        sql += "WHERE ${packageName.toUpperCase()}BT_${packageName.toUpperCase()}B_ID=? AND ${packageName.toUpperCase()}BT_ANN_ID=? "; 
	        var statement = connection.prepareStatement(sql);
	        var j = 0;
	        statement.setInt(++j, id);        
	        statement.setString(++j, boardTags[i].id);       
		    statement.executeUpdate();
		    log.info('${packageName.toUpperCase()}_BOARD_TAG entity relation between ${packageName.toUpperCase()}_BOARD['+id+'] entity and ANN_TAG['+boardTags[i].id+'] removed');
		}
		log.info(boardTags.length + ' ${packageName.toUpperCase()}_BOARD_TAG entity relations to ${packageName.toUpperCase()}_BOARD_TAG[' +  id+ '] entity removed');
	} catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }		
	exports.tag(id, tags, createOnDemand);	
};

})();