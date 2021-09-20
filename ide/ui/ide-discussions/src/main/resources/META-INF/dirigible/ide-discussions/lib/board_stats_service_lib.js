/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
"use strict";

let BoardStatsEntityDef = {
	table: "DIRIGIBLE_DISCUSSIONS_BOARD_STATS",
	properties: [{
		name: "user",
		column: "USER_USERNAME",
		type: "VARCHAR",
		size: 100
	}, {
		name: "visits",
		column: "DISB_VISITS",
		type: "INTEGER"
	}, {
		name: "latestDiscussionUpdateTime",
		column: "LATEST_UPDATE_TIME",
		type: "BIGINT",
		value: function (dbValue) {
			let _value;
			if (dbValue > 0)
				_value = new Date(dbValue).toISOString();
			return _value;
		}
	}, {
		name: "repliesCount",
		column: "REPLIES_COUNT",
		type: "INTEGER"
	}, {
		name: "commentsCount",
		column: "COMMENTS_COUNT",
		type: "INTEGER"
	}, {
		name: "participantsCount",
		column: "PARTICIPANTS_COUNT",
		type: "INTEGER"
	}, {
		name: "totalVotes",
		column: "TOTAL_VOTES",
		type: "INTEGER"
	}, {
		name: "upvotes",
		column: "UPVOTES",
		type: "INTEGER"
	}, {
		name: "downvotes",
		column: "DOWNVOTES",
		type: "INTEGER"
	}, {
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
	}, {
		name: 'tags',
		joinDao: require("ide-discussions/lib/board_tags_dao").create,
		targetDao: require("ide-discussions/lib/tags_dao").create,
		joinKey: "boardId",
		type: "many-to-many"
	}, {
		name: 'votes',
		targetDao: require("ide-discussions/lib/board_votes_dao").create,
		joinKey: "boardId",
		type: "one-to-many"
	}]
};

let mashupORM = function () {
	let baseOrm = require("ide-discussions/lib/board_dao").create().orm;
	baseOrm.properties = baseOrm.properties.filter(function (property) {
		return property.name !== 'user';
	});

	BoardStatsEntityDef.properties = baseOrm.properties.concat(BoardStatsEntityDef.properties);
	BoardStatsEntityDef.associations = baseOrm.associations;

	return BoardStatsEntityDef;
};

exports.getDao = function (commentsDao, tagsDao) {
	commentsDao = commentsDao || require('ide-discussions/lib/comment_dao').create();
	tagsDao = tagsDao || require('ide-discussions/lib/board_tags_dao').create();

	let orm = mashupORM();

	let boardsDAO = require('ide-discussions/lib/board_dao').create(orm, 'ide-discussions.dao.BoardStatsDAO');

	let boardStatsDAO = require('db/v4/dao').create(orm, 'ide-discussions.dao.BoardStatsDAO');
	boardStatsDAO.visit = boardsDAO.visit.bind(boardStatsDAO);
	boardStatsDAO.commentsDao = boardStatsDAO;
	boardStatsDAO.tagsDao = tagsDao;
	//ensure readonly DAO ops
	['insert', 'remove', 'update', 'lock', 'unlock']
		.forEach(function (op) {
			delete boardStatsDAO[op];
		});
	return boardStatsDAO;
};


/**
 * Factory function for Board Stats data service instances.
 */
exports.create = function (commentsDao, tagsDao) {
	let rsdata = require('http/v4/rs-data');
	let boardStatsDao = this.getDao(commentsDao, tagsDao);
	let svc = rsdata.service().dao(boardStatsDao, 'ide-discussions.svc.BoardStatsService');
	svc.dao().afterFound = function (entity) {
		let userLib = require("security/v4/user");
		let boardVotesDAO = require("ide-discussions/lib/board_votes_dao").create();
		let requestingUserName = userLib.getName();
		entity.editable = entity.user === requestingUserName;
		if (requestingUserName) {
			let idDef = this.orm.getPrimaryKey();
			let userVote = boardVotesDAO.getVote(entity[idDef.name], requestingUserName);
			entity.currentUserVote = userVote;
		}
	};
	return svc;
};
