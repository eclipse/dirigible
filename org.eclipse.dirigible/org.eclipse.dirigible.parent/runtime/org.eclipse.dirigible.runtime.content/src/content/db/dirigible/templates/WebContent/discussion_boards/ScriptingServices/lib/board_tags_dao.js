#set( $D = '$' )
/* globals ${D} */
/* eslint-env node, dirigible */
(function(){
"use strict";

var BoardTagORM = {
	dbName: '${fileNameNoExtension.toUpperCase()}_BOARD_TAG',
	properties: [{
			name: "id",
			dbName: "${fileNameNoExtension.toUpperCase()}BT_ID",
			id: true,
			required: true,
			type: "Long"
		},{
			name: "boardId",
			dbName: "${fileNameNoExtension.toUpperCase()}BT_${fileNameNoExtension.toUpperCase()}B_ID",
			id: true,
			required: true,
			type: "Long"
		},{
			name: "tagId",
			dbName: "${fileNameNoExtension.toUpperCase()}BT_ANN_ID",
			id: true,
			required: true,
			type: "Long"
		}],
	associations: [{
			name: 'board',
			targetDao: require("${packageName}/lib/board_dao").get,
			type: 'many-to-one',
			joinKey: "boardId"
		}, {
			name: 'tag',
			targetDao: require("annotations/lib/tags_dao").get,
			type: 'many-to-one',
			joinKey: "tagId"
		}]
};

var DAO = require('daoism/dao').DAO;
var BoardTagDAO  = exports.BoardTagDAO = function(orm, boardDao, tagsDao){
	orm = orm || BoardTagORM;
	DAO.call(this, orm, 'BoardTagDAO');
	this.boardDao = boardDao;
	this.tagsDao = tagsDao;
};
BoardTagDAO.prototype = Object.create(DAO.prototype);

BoardTagDAO.prototype.listJoins = function(settings, daos){
	var boardId;
	if(typeof settings === 'string'){
		boardId = settings;
	} else if(typeof settings === 'object'){
		boardId = settings.boardId;
	}

	this.${D}log.info('Finding '+daos.targetDao.orm.dbName+' entities related to '+daos.sourceDao.orm.dbName+'['+boardId+']');

	if(boardId === undefined || boardId === null){
		throw new Error('Illegal argument for id parameter:' + boardId);
	}

    var connection = this.datasource.getConnection();
    try {
        var sql = "SELECT * FROM "+daos.targetDao.orm.dbName+" LEFT JOIN "+daos.joinDao.orm.dbName+" ON "+daos.joinDao.orm.getProperty('tagId').dbName+"="+daos.targetDao.orm.getPrimaryKey().dbName+" WHERE "+daos.joinDao.orm.getProperty('boardId').dbName+"=?";
     
        var statement = connection.prepareStatement(sql);
        statement.setLong(1, boardId);

        var resultSet = statement.executeQuery();
        var tagEntities = [];
        while (resultSet.next()) {
        	var tagEntity = {
        		id: resultSet['get'+daos.targetDao.orm.getPrimaryKey().type](daos.targetDao.orm.getPrimaryKey().dbName),
        		defaultLabel: resultSet['get'+daos.targetDao.orm.getProperty('defaultLabel').type](daos.targetDao.orm.getProperty('defaultLabel').dbName),
			    uri: resultSet['get'+daos.targetDao.orm.getProperty('uri').type](daos.targetDao.orm.getProperty('uri').dbName)
        	};
        	tagEntities.push(tagEntity);
        } 
        this.${D}log.info(tagEntities.length+' '+daos.targetDao.orm.dbName+' entities related to '+daos.sourceDao.orm.dbName+'[' + boardId+ '] found');
        return tagEntities;
    } finally {
        connection.close();
    }	
};

exports.get = function(boardDao, tagsDao){
	var boardTagDAO = new BoardTagDAO(BoardTagORM, boardDao, tagsDao);
	return boardTagDAO;
};
})();
