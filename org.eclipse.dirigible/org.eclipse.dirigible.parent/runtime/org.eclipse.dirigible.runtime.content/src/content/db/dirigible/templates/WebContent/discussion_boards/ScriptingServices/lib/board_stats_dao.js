#set( $D = '$' )
/* globals ${D} */
/* eslint-env node, dirigible */
"use strict";

var boardStatsDAOorm = {
	dbName: "${packageName.toUpperCase()}_BOARD_STATS",
	properties: [{
			name: "user",
			dbName: "USER_USERNAME",
			type: "String",
			size: 100
		},{
			name: "visits",
			dbName: "${packageName.toUpperCase()}B_VISITS",
			type: "Int"
		},{
			name: "latestDiscussionUpdateTime",
			dbName: "LATEST_UPDATE_TIME",
			type: "Long",
			value: function(dbValue){
				var _value;
				if(dbValue>0)
					_value = new Date(dbValue).toISOString();
				return _value;
			}
		},{
			name: "repliesCount",
			dbName: "REPLIES_COUNT",
			type: "Int"
		},{
			name: "commentsCount",
			dbName: "COMMENTS_COUNT",
			type: "Int"
		},{
			name: "participantsCount",
			dbName: "PARTICIPANTS_COUNT",
			type: "Int"
		},{
			name: "totalVotes",
			dbName: "TOTAL_VOTES",
			type: "Int"
		},{
			name: "upvotes",
			dbName: "UPVOTES",
			type: "Int"
		},{
			name: "downvotes",
			dbName: "DOWNVOTES",
			type: "Int"
		},{
			name: "rating",
			dbName: "RATING",
			type: "Int"
		}	
	],
	associationSets: {
		comments: {
			dao: require("${packageName}/lib/comment_dao").get,
			associationType: "one-to-many",
			joinKey: "boardId",
			defaults: {
				flat: false
			}
		},
		tagRefs: {
			dao: require("${packageName}/lib/board_tags_dao").get,
			joinKey: "boardId",
			associationType: "one-to-many"
		},
		tags: {
			daoJoin: require("${packageName}/lib/board_tags_dao").get,
			daoN: require("annotations/lib/tags_dao").get,
			joinKey: "boardId",
			associationType: "many-to-many"
		},
		votes: {
			dao: require("${packageName}/lib/board_votes_dao").get,
			joinKey: "boardId",
			associationType: "one-to-many"
		}
	}
};

var mashupORM = function(){
	var baseOrm = require("${packageName}/lib/board_dao").get().orm;	
	baseOrm.properties = baseOrm.properties.filter(function(property){
		return property.name!=='user';
	});
	
	boardStatsDAOorm.properties = baseOrm.properties.concat(boardStatsDAOorm.properties);
	boardStatsDAOorm.associationSets = baseOrm.associationSets;
	
	return boardStatsDAOorm;
};

var BoardDAO = require('${packageName}/lib/board_dao').BoardDAO;

var BoardStatsDAO  = exports.BoardStatsDAO = function(orm, commentsDao, tagsDao){
	orm = orm || mashupORM();
	BoardDAO.call(this, orm, commentsDao, tagsDao);
	this.${D}log.loggerName = 'BoardStats DAO';
};
BoardStatsDAO.prototype = Object.create( BoardDAO.prototype );

exports.get = function(commentsDao, tagsDao){
	commentsDao = commentsDao || require('${packageName}/lib/comment_dao').get();
	tagsDao = tagsDao || require('${packageName}/lib/board_tags_dao').get();
	var bs = new BoardStatsDAO(undefined, commentsDao, tagsDao);
	//ensure readonly DAO ops
	['insert', 'remove', 'update', 'lock', 'unlock']
	.forEach(function(op){
		delete bs[op];
	});
	return bs;
};
