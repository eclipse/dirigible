"use strict";

var BoardStatsEntityDef = {
	table: "DIRIGIBLE_DISCUSSIONS_BOARD_STATS",
	properties: [{
			name: "user",
			column: "USER_USERNAME",
			type: "VARCHAR",
			size: 100
		},{
			name: "visits",
			column: "DISB_VISITS",
			type: "INTEGER"
		},{
			name: "latestDiscussionUpdateTime",
			column: "LATEST_UPDATE_TIME",
			type: "BIGINT",
			value: function(dbValue){
				var _value;
				if(dbValue>0)
					_value = new Date(dbValue).toISOString();
				return _value;
			}
		},{
			name: "repliesCount",
			column: "REPLIES_COUNT",
			type: "INTEGER"
		},{
			name: "commentsCount",
			column: "COMMENTS_COUNT",
			type: "INTEGER"
		},{
			name: "participantsCount",
			column: "PARTICIPANTS_COUNT",
			type: "INTEGER"
		},{
			name: "totalVotes",
			column: "TOTAL_VOTES",
			type: "INTEGER"
		},{
			name: "upvotes",
			column: "UPVOTES",
			type: "INTEGER"
		},{
			name: "downvotes",
			column: "DOWNVOTES",
			type: "INTEGER"
		},{
			name: "rating",
			column: "RATING",
			type: "INTEGER"
		}	
	],
	associations: [{
			name: 'comments',
			targetDao: require("ide-discussions/lib/comment_dao").create,
			type: "one-to-many",
			joinKey: "boardId",
			defaults: {
				flat: false
			}
		}, {
			name: 'tagRefs',
			targetDao: require("ide-discussions/lib/board_tags_dao").create,
			joinKey: "boardId",
			type: "one-to-many"
		},{
			name: 'tags',
			joinDao: require("ide-discussions/lib/board_tags_dao").create,
			targetDao: require("ide-discussions/lib/tags_dao").create,
			joinKey: "boardId",
			type: "many-to-many"
		},{
		    name: 'votes',
			targetDao: require("ide-discussions/lib/board_votes_dao").create,
			joinKey: "boardId",
			type: "one-to-many"
		}]
};

var mashupORM = function(){
	var baseOrm = require("ide-discussions/lib/board_dao").create().orm;	
	baseOrm.properties = baseOrm.properties.filter(function(property){
		return property.name!=='user';
	});
	
	BoardStatsEntityDef.properties = baseOrm.properties.concat(BoardStatsEntityDef.properties);
	BoardStatsEntityDef.associations = baseOrm.associations;
	
	return BoardStatsEntityDef;
};

exports.getDao = function(commentsDao, tagsDao){
	commentsDao = commentsDao || require('ide-discussions/lib/comment_dao').create();
	tagsDao = tagsDao || require('ide-discussions/lib/board_tags_dao').create();
	
	var orm = mashupORM();
	
	var boardsDAO = require('ide-discussions/lib/board_dao').create(orm, 'ide-discussions.dao.BoardStatsDAO');
	
	var boardStatsDAO = require('db/v3/dao').create(orm, 'ide-discussions.dao.BoardStatsDAO');
	boardStatsDAO.visit = boardsDAO.visit.bind(boardStatsDAO);
	boardStatsDAO.commentsDao = boardStatsDAO;
	boardStatsDAO.tagsDao = tagsDao;
	//ensure readonly DAO ops
	['insert', 'remove', 'update', 'lock', 'unlock']
	.forEach(function(op){
		delete boardStatsDAO[op];
	});
	return boardStatsDAO;
};


/**
 * Factory function for Board Stats data service instances.
 */
exports.create = function(commentsDao, tagsDao){
	var rsdata = require('http/v3/rs-data');
	var boardStatsDao = this.getDao(commentsDao, tagsDao);
	var svc = rsdata.service().dao(boardStatsDao, 'ide-discussions.svc.BoardStatsService');
	svc.dao().afterFound = function(entity){
			var userLib = require("security/v3/user");
			var boardVotesDAO = require("ide-discussions/lib/board_votes_dao").create();
			var requestingUserName = userLib.getName();
			entity.editable = entity.user === requestingUserName;
			if(requestingUserName){
				var idDef = this.orm.getPrimaryKey();
				var userVote = boardVotesDAO.getVote(entity[idDef.name], requestingUserName);
				entity.currentUserVote = userVote;
			}
		};
	return svc;
};
