/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var defaults = {
	builders: {
		"droptable": function(){
			var tableName = this.tableName || this.tables
										.map(function(table){
											return table.name;
										})[0];	
			this.sql = 'DROP TABLE ' + tableName;
			return this.sql;
		},		
		"insert": function(){
			this.sql = 'INSERT INTO ' + this.tables
										.map(function(table){
											return table.name;
										}).join(',');
			this.sql+= '(';
			for(var i in this.updFieldSet){
				this.sql += this.updFieldSet[i].dbName+', ';
			}
			this.sql = this.sql.substring(0, this.sql.length-2);
			this.sql+= ') VALUES(';
			for(var i in this.fieldValueSet){
				var val = this.fieldValueSet[i];
				if(val!=='?' && this.updFieldSet[i].type==='String')
					val="'"+val+"'";
				this.sql += val+', ';
			}
			this.sql = this.sql.substring(0, this.sql.length-2);
			this.sql+= ')';	
			return this.sql;
		},
		"update": function(){
			this.sql = 'UPDATE ' + this.tables
									.map(function(table){
										return table.name;
									}).join(',') + ' SET ';
			if(this.updFieldSet){
				for(var i in this.updFieldSet){
					//var val = this.updValueSet[i];
					this.sql += this.updFieldSet[i].dbName+'='+this.fieldValueSet[i]+', ';
				}
				this.sql = this.sql.substring(0, this.sql.length-2);
			}
			if(this.filters){
				this.sql += ' WHERE ' + this.filters.join(' AND ');
			}			
			return this.sql;		
		},
		"delete": function(){
			this.sql = 'DELETE FROM ' + this.tables
									.map(function(table){
										return table.name + (table.alias?' as '+ table.alias:'');
									}).join(',');
			if(this.filters){
				this.sql += ' WHERE ' + this.filters.join(' AND ');
			}								
			return this.sql;
		}	
	},
	
	sqlTypeFor: function(name, length){
		if(name==='Int'){
			return "INTEGER";
		}
		if(name==='String'){
			if(length === 1)
				return 'CHAR';				
			if(length < 32672)
				return 'VARCHAR';
			return 'CLOB';
		}
		if(name==='Long'){
			return "BIGINT";
		}
		if(name==='Float'){
			return "REAL";
		}
		if(name==='Double'){
			return "DOUBLE";
		}
		if(name==='Short'){
			return "SMALLINT";
		}
		if(name==='Boolean'){
			return "BOOLEAN";
		}		
		if(name==='Timestamp'){
			return "TIMESTAMP";
		}
		if(name==='Time'){
			return "TIME";
		}		
		if(name==='Date'){
			return "DATE";
		}
		return;
	}
};

exports.get = function(){
	return defaults;
};
