/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var Derby = require('daoism/dialects/derby').get();

Derby.builders.select = function(){
	this.sql = 'SELECT';
	if(!this.selectFields){
		this.sql += ' *';
	} else {
		this.sql += ' ' + this.selectFields
		.map(function(field){
			return field.alias?field.name + ' AS ' + field.alias: field.name;
		}).join(', ');
	}
	this.sql += ' FROM ' + this.tables
							.map(function(table){
								return table.name + (table.alias?' as '+ table.alias:'');
							}).join(',');
	if(this.leftJoins){
		this.sql += this.leftJoins
					.map(function(join){
						return ' LEFT JOIN ' + join.table + (join.alias?' \''+join.alias+'\'':'') + ' ON ' + join.statement;
					}).join(' ');
	}
	if(this.filters){
		this.sql += ' WHERE ' + this.filters.join(' AND ');
	}
	if(this.orderFields){
		this.sql += ' ORDER BY ' + this.orderFields
					.map(function(field){
						return field.name + ' ' + (field.order?'ASC':'DESC');
					})
					.join(', ');
	}
	if (this._offset!==undefined){
		this.sql += " OFFSET " + this._offset;
	}
	if (this._limit!==undefined){
		this.sql += " LIMIT " + this._limit;
	}
	return this.sql;
};

exports["Derby"] = Derby;

exports.get = function(){
	return Derby;
};