/* globals $ */
/* eslint-env node, dirigible */
(function(){
"use strict";

var database = require("db/database");
var datasource = database.getDatasource();

var log = require("logging/logger").logger;
log.ctx = "Board DAO";

exports.getVote = function(id, user){

	log.info('Finging USR_USER['+user+'] vote for ${packageName.toUpperCase()}_BOARD['+id+'] entity');

	if(id === undefined || id === null){
		throw new Error('Illegal argument for id parameter:' + id);
	}
	
	if(user === undefined || user === null){
		throw new Error('Illegal argument for user parameter:' + user);
	}	

    var connection = datasource.getConnection();
    var vote = 0;
    try {
        var sql = "SELECT * FROM ${packageName.toUpperCase()}_BOARD_VOTE WHERE ${packageName.toUpperCase()}V_${packageName.toUpperCase()}B_ID=? AND ${packageName.toUpperCase()}V_USER=?";
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.setString(2, user);
        
        var resultSet = statement.executeQuery();
        if (resultSet.next()) {
            vote = resultSet.getInt("${packageName.toUpperCase()}V_VOTE");
			if(vote!==0){
            	log.info('USR_USER['+user+'] vote for ${packageName.toUpperCase()}_BOARD['+id+'] entity found');
        	}
        } 
    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    }
	
	return vote;
};

exports.vote = function(id, user, vote){
	log.info("Recording user["+user+"] vote["+vote+"] for ${packageName.toUpperCase()}_BOARD["+id+"]");
	if(vote===0 || vote === undefined)
		throw Error('Illegal Argument: vote cannot be 0 or undefined');

	var previousVote = exports.getVote(id, user);

	var connection = datasource.getConnection();
    try {
    	var statement, sql, isInsert;
    	if(previousVote === undefined || previousVote === null || previousVote === 0){
    		//Operations is INSERT
    		isInsert = true; 
    		log.info("Inserting ${packageName.toUpperCase()}_BOARD_VOTE relation between ${packageName.toUpperCase()}_BOARD["+id+"] and USR_USER["+user+"]");
	        sql = "INSERT INTO ${packageName.toUpperCase()}_BOARD_VOTE (${packageName.toUpperCase()}V_ID, ${packageName.toUpperCase()}V_${packageName.toUpperCase()}B_ID, ${packageName.toUpperCase()}V_USER, ${packageName.toUpperCase()}V_VOTE) VALUES (?,?,?,?)";
	        statement = connection.prepareStatement(sql);
	        
	        var i = 0;
	        var voteId = datasource.getSequence('${packageName.toUpperCase()}_BOARD_VOTE_${packageName.toUpperCase()}V_ID').next();
	        statement.setInt(++i, voteId);
	        statement.setInt(++i, id);
	        statement.setString(++i, user);        
	        statement.setShort(++i, vote);	        
		} else {
    		//Operations is UPDATE
			isInsert = false;
			log.info("Updating ${packageName.toUpperCase()}_BOARD_VOTE relation between ${packageName.toUpperCase()}_BOARD["+id+"] and USR_USER["+user+"]");
	        sql = "UPDATE ${packageName.toUpperCase()}_BOARD_VOTE SET ${packageName.toUpperCase()}V_VOTE=? WHERE ${packageName.toUpperCase()}V_${packageName.toUpperCase()}B_ID=? AND ${packageName.toUpperCase()}V_USER=?";
	        statement = connection.prepareStatement(sql);
	        
	        var i = 0;
	       	statement.setShort(++i, vote);
	        statement.setInt(++i, id);
	        statement.setString(++i, user);        
		}
	    
	    statement.executeUpdate();
	    
	    var msgOperationResult = isInsert?"inserted":"updated";
	    log.info('${packageName.toUpperCase()}_BOARD_VOTE[' + voteId + '] entity relation between ${packageName.toUpperCase()}_BOARD[' + id + '] and USR_USER[' + user + '] ' + msgOperationResult);
	    
        return voteId;

    } catch(e) {
		e.errContext = sql;
		throw e;
    } finally {
        connection.close();
    } 
};

})();
