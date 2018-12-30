/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var java = require('core/v3/java');
var database = require('db/v3/database');

exports.getDialect = function(connection) {
	/**
	 * Dialect object
	 */
	var Dialect = function() {
		
		var _parameters = [];
		
		var parameters = function(){
			return _parameters;
		};
		
		var build = function() {
			return java.invoke(this.uuid, 'build', []);
		};
		
		this.select = function() {
			
			/**
			 * Select object
			 */
			var Select = function() {
				
				this.distinct = function() {
					java.invoke(this.uuid, 'distinct', []);
					return this;
				};
				
				this.forUpdate = function() {
					java.invoke(this.uuid, 'forUpdate', []);
					return this;
				};
				
				this.column = function(column) {
					java.invoke(this.uuid, 'column', [column]);
					return this;
				};
				
				this.from = function(table, alias) {
					if (alias !== undefined) {
						java.invoke(this.uuid, 'from', [table, alias]);
					} else {
						java.invoke(this.uuid, 'from', [table]);
					}
					return this;
				};
				
				this.join = function(table, on, alias) {
					if (alias !== undefined) {
						java.invoke(this.uuid, 'join', [table, on, alias]);
						if(arguments.length>3){
							if(Array.isArray(arguments[3]))
								_parameters = _parameters.concat([arguments[3]]);
							else
								_parameters.push(arguments[3]);
						}
					} else {
						java.invoke(this.uuid, 'join', [table, on]);
						if(arguments.length>2){
							if(Array.isArray(arguments[2]))
								_parameters = _parameters.concat([arguments[2]]);
							else
								_parameters.push(arguments[2]);
						}
					}
					return this;
				};
				
				this.innerJoin = function(table, on, alias) {
					if (alias !== undefined) {
						java.invoke(this.uuid, 'innerJoin', [table, on, alias]);
						if(arguments.length>3){
							if(Array.isArray(arguments[3]))
								_parameters = _parameters.concat(arguments[3]);
							else
								_parameters.push(arguments[3]);
						}
					} else {
						java.invoke(this.uuid, 'innerJoin', [table, on]);
						if(arguments.length>2){
							if(Array.isArray(arguments[2]))
								_parameters = _parameters.concat(arguments[2]);
							else
								_parameters.push(arguments[2]);
						}
					}
					return this;
				};
				
				this.outerJoin = function(table, on, alias) {
					if (alias !== undefined) {
						java.invoke(this.uuid, 'outerJoin', [table, on, alias]);
						if(arguments.length>3){
							if(Array.isArray(arguments[3]))
								_parameters = _parameters.concat(arguments[3]);
							else
								_parameters.push(arguments[3]);
						}
					} else {
						java.invoke(this.uuid, 'outerJoin', [table, on]);
						if(arguments.length>2){
							if(Array.isArray(arguments[2]))
								_parameters = _parameters.concat(arguments[2]);
							else
								_parameters.push(arguments[2]);
						}
					}
					return this;
				};
				
				this.leftJoin = function(table, on, alias) {
					if (alias !== undefined) {
						java.invoke(this.uuid, 'leftJoin', [table, on, alias]);
						if(arguments.length>3){
							if(Array.isArray(arguments[3]))
								_parameters = _parameters.concat(arguments[3]);
							else
								_parameters.push(arguments[3]);
						}
					} else {
						java.invoke(this.uuid, 'leftJoin', [table, on]);
						if(arguments.length>2){
							if(Array.isArray(arguments[2]))
								_parameters = _parameters.concat(arguments[2]);
							else
								_parameters.push(arguments[2]);
						}
					}
					return this;
				};
				
				this.rightJoin = function(table, on, alias) {
					if (alias !== undefined) {
						java.invoke(this.uuid, 'rightJoin', [table, on, alias]);
						if(arguments.length>3){
							if(Array.isArray(arguments[3]))
								_parameters = _parameters.concat(arguments[3]);
							else
								_parameters.push(arguments[3]);
						}
					} else {
						java.invoke(this.uuid, 'rightJoin', [table, on]);
						if(arguments.length>2){
							if(Array.isArray(arguments[2]))
								_parameters = _parameters.concat(arguments[2]);
							else
								_parameters.push(arguments[2]);
						}
					}
					return this;
				};
				
				this.fullJoin = function(table, on, alias) {
					if (alias !== undefined) {
						java.invoke(this.uuid, 'fullJoin', [table, on, alias]);
						if(arguments.length>3){
							if(Array.isArray(arguments[3]))
								_parameters = _parameters.concat(arguments[3]);
							else
								_parameters.push(arguments[3]);
						}
					} else {
						java.invoke(this.uuid, 'fullJoin', [table, on]);
						if(arguments.length>2){
							if(Array.isArray(arguments[2]))
								_parameters = _parameters.concat(arguments[2]);
							else
								_parameters.push(arguments[2]);
						}
					}
					return this;
				};
				
				this.where = function(condition) {
					java.invoke(this.uuid, 'where', [condition]);
					if(arguments.length>1){
						if(Array.isArray(arguments[1]))
							_parameters = _parameters.concat(arguments[1]);
						else
							_parameters.push(arguments[1]);
					}
					return this;
				};
				
				this.order = function(column, asc) {
					if (asc !== undefined) {
						java.invoke(this.uuid, 'order', [column, asc]);
					} else {
						java.invoke(this.uuid, 'order', [column]);
					}
					return this;
				};
				
				this.group = function(column) {
					java.invoke(this.uuid, 'group', [column]);
					return this;
				};
				
				this.limit = function(limit) {
					java.invoke(this.uuid, 'limit', [limit]);
					return this;
				};
				
				this.offset = function(offset) {
					java.invoke(this.uuid, 'offset', [offset]);
					return this;
				};
				
				this.having = function(having) {
					java.invoke(this.uuid, 'having', [having]);
					return this;
				};

				this.union = function(select) {
					java.invoke(this.uuid, 'union', [select]);
					return this;
				};

				this.build = build.bind(this);

				this.parameters = parameters.bind(this);

			}
			
			_parameters = [];
			var selectInstance = java.invoke(this.uuid, 'select', [], true);
			var select = new Select();
			select.uuid = selectInstance.uuid;
			return select;
		};
		
		this.insert = function() {
			
			/**
			 * Insert object
			 */
			var Insert = function() {
				
				this.into = function(table) {
					java.invoke(this.uuid, 'into', [table]);
					return this;
				};
				
				this.column = function(column) {
					java.invoke(this.uuid, 'column', [column]);
					return this;
				};
				
				this.value = function(value) {
					java.invoke(this.uuid, 'value', [value]);
					if(arguments.length>1){
						if(Array.isArray(arguments[1]))
							_parameters = _parameters.concat(arguments[1]);
						else
							_parameters.push(arguments[1]);
					}
					return this;
				};
				
				this.select = function(select) {
					java.invoke(this.uuid, 'select', [select]);
					return this;
				};

				this.build = build.bind(this);
				
				this.parameters = parameters.bind(this);

			};

			_parameters = [];			
			var insertInstance = java.invoke(this.uuid, 'insert', [], true);
			var insert = new Insert();
			insert.uuid = insertInstance.uuid;
			return insert;
		};
	
		this.update = function() {
			
			/**
			 * Update object
			 */
			var Update = function() {
				
				this.table = function(table) {
					java.invoke(this.uuid, 'table', [table]);
					return this;
				};
				
				this.set = function(column, value) {
					java.invoke(this.uuid, 'set', [column, value]);
					if(arguments.length>2){
						if(Array.isArray(arguments[2]))
							_parameters = _parameters.concat(arguments[2]);
						else
							_parameters.push(arguments[2]);
					}
					return this;
				};
				
				this.where = function(condition) {
					java.invoke(this.uuid, 'where', [condition]);
					if(arguments.length>1){
						if(Array.isArray(arguments[1]))
							_parameters = _parameters.concat(arguments[1]);
						else
							_parameters.push(arguments[1]);
					}
					return this;
				};
				
				this.build = build.bind(this);
				
				this.parameters = parameters.bind(this);

			};
			
			_parameters = [];
			var updateInstance = java.invoke(this.uuid, 'update', [], true);
			var update = new Update();
			update.uuid = updateInstance.uuid;
			return update;
		};
	
		this.delete = function() {
			
			/**
			 * Delete object
			 */
			var Delete = function() {
				
				this.from = function(table) {
					java.invoke(this.uuid, 'from', [table]);
					return this;
				};
				
				this.where = function(condition) {
					java.invoke(this.uuid, 'where', [condition]);
					if(arguments.length>1){
						if(Array.isArray(arguments[1]))
							_parameters = _parameters.concat(arguments[1]);
						else
							_parameters.push(arguments[1]);
					}
					return this;
				};
				
				this.build = build.bind(this);
				
				this.parameters = parameters.bind(this);

			}
			
			_parameters = [];			
			var deleteInstance = java.invoke(this.uuid, 'delete', [], true);
			var deleteRows = new Delete();
			deleteRows.uuid = deleteInstance.uuid;
			return deleteRows;
		};
		
		
		this.nextval = function(name) {
			
			/**
			 * Nextval object
			 */
			var Nextval = function(name) {
				
				this.name = name;
				
				this.build = build.bind(this);

			}
			
			var nextvalInstance = java.invoke(this.uuid, 'nextval', [name], true);
			var nextval = new Nextval();
			nextval.uuid = nextvalInstance.uuid;
			return nextval;
		};
		
		
		this.create = function() {
			
			/**
			 * Create object
			 */
			var Create = function() {
				
				this.table = function(table) {
					/**
					 * CreateTable object
					 */
					var CreateTable = function() {
						
						this.column = function(column, type, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'column', [column, type, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'column', [column, type, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'column', [column, type, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'column', [column, type, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'column', [column, type]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnVarchar = function(column, length, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnVarchar', [column, length, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnVarchar', [column, length, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnVarchar', [column, length, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnVarchar', [column, length, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnVarchar', [column, length]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnChar = function(column, length, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnChar', [column, length, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnChar', [column, length, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnChar', [column, length, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnChar', [column, length, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnChar', [column, length]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnDate = function(column, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnDate', [column, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnDate', [column, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnDate', [column, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnDate', [column, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnDate', [column]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnTime = function(column, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnTime', [column, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnTime', [column, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnTime', [column, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnTime', [column, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnTime', [column]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnTimestamp = function(column, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnTimestamp', [column, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnTimestamp', [column, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnTimestamp', [column, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnTimestamp', [column, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnTimestamp', [column]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnInteger = function(column, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnInteger', [column, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnInteger', [column, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnInteger', [column, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnInteger', [column, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnInteger', [column]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnTinyint = function(column, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnTinyint', [column, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnTinyint', [column, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnTinyint', [column, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnTinyint', [column, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnTinyint', [column]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnBigint = function(column, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnBigint', [column, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnBigint', [column, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnBigint', [column, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnBigint', [column, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnBigint', [column]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnSmallint = function(column, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnSmallint', [column, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnSmallint', [column, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnSmallint', [column, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnSmallint', [column, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnSmallint', [column]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnReal = function(column, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnReal', [column, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnReal', [column, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnReal', [column, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnReal', [column, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnReal', [column]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnDouble = function(column, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnDouble', [column, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnDouble', [column, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnDouble', [column, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnDouble', [column, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnDouble', [column]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnBoolean = function(column, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnBoolean', [column, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnBoolean', [column, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnBoolean', [column, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnBoolean', [column, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnBoolean', [column]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.columnBlob = function(column, isNullable, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnBlob', [column, isPrimaryKey, isNullable, isUnique, args]);
							} else {
								if (isNullable) {
									java.invoke(this.uuid, 'columnBlob', [column, isPrimaryKey, isNullable]);
								} else {						
									java.invoke(this.uuid, 'columnBlob', [column]);
								}
							}
							
							return this;
						};
						
						this.columnDecimal = function(column, precision, scale, isPrimaryKey, isNullable, isUnique, args) {
							if (args !== undefined) {
								java.invoke(this.uuid, 'columnDecimal', [column, precision, scale, isPrimaryKey, isNullable, isUnique, args]);	
							} else {
								if (isUnique) {
									java.invoke(this.uuid, 'columnDecimal', [column, precision, scale, isPrimaryKey, isNullable, isUnique]);
								} else {
									if (isNullable) {
										java.invoke(this.uuid, 'columnDecimal', [column, precision, scale, isPrimaryKey, isNullable]);
									} else {
										if (isPrimaryKey) {
											java.invoke(this.uuid, 'columnDecimal', [column, precision, scale, isPrimaryKey]);
										} else {
											java.invoke(this.uuid, 'columnDecimal', [column, precision, scale]);
										}
									}
								}
							}
							
							return this;
						};
						
						this.primaryKey = function(columns, name) {
							if (name !== undefined) {
								java.invoke(this.uuid, 'primaryKey', [name, columns]);
							} else {
								java.invoke(this.uuid, 'primaryKey', [columns]);
							}
							return this;
						};
						
						this.foreignKey = function(name, columns, referencedTable, referencedColumns) {
							java.invoke(this.uuid, 'foreignKey', [name, columns, referencedTable, referencedColumns]);
							return this;
						};
						
						this.unique = function(name, columns) {
							java.invoke(this.uuid, 'unique', [name, columns]);
							return this;
						};
						
						this.check = function(name, expression) {
							java.invoke(this.uuid, 'check', [name, expression]);
							return this;
						};
						
						this.build = build.bind(this);
						
					}
					
					var createTableInstance = java.invoke(this.uuid, 'table', [table], true);
					var createTable = new CreateTable();
					createTable.uuid = createTableInstance.uuid;
					return createTable;
				};
				
				this.view = function(view) {
					/**
					 * CreateView object
					 */
					var CreateView = function() {
						
						this.column = function(column) {
							java.invoke(this.uuid, 'column', [column]);
							return this;
						};
						
						this.asSelect = function(select) {
							java.invoke(this.uuid, 'asSelect', [select]);
							return this;
						};
						
						this.build = build.bind(this);
					}
					
					_parameters = [];
					var createViewInstance = java.invoke(this.uuid, 'view', [view], true);
					var createView = new CreateView();
					createView.uuid = createViewInstance.uuid;
					return createView;
				};
				
				this.sequence = function(sequence) {
					/**
					 * CreateSequence object
					 */
					var CreateSequence = function() {
						
						this.build = build.bind(this);
					}
					
					var createSequenceInstance = java.invoke(this.uuid, 'sequence', [sequence], true);
					var createSequence = new CreateSequence();
					createSequence.uuid = createSequenceInstance.uuid;
					return createSequence;
				};
				
			}
			
			var createInstance = java.invoke(this.uuid, 'create', [], true);
			var create = new Create();
			create.uuid = createInstance.uuid;
			return create;
		};
		
		this.drop = function() {
			
			/**
			 * Drop object
			 */
			var Drop = function() {
				
				this.table = function(table) {
					/**
					 * DropTable object
					 */
					var DropTable = function() {
						
						this.build = build.bind(this);
					}
					
					var dropTableInstance = java.invoke(this.uuid, 'table', [table], true);
					var dropTable = new DropTable();
					dropTable.uuid = dropTableInstance.uuid;
					return dropTable;
				};
				
				this.view = function(view) {
					/**
					 * DropView object
					 */
					var DropView = function() {
						
						this.build = build.bind(this);
					}
					
					var dropViewInstance = java.invoke(this.uuid, 'view', [view], true);
					var dropView = new DropView();
					dropView.uuid = dropViewInstance.uuid;
					return dropView;
				};
				
				this.sequence = function(sequence) {
					/**
					 * DropSequence object
					 */
					var DropSequence = function() {
						
						this.build = build.bind(this);
					}
					
					var dropSequenceInstance = java.invoke(this.uuid, 'sequence', [sequence], true);
					var dropSequence = new DropSequence();
					dropSequence.uuid = dropSequenceInstance.uuid;
					return dropSequence;
				};
				
			}
			
			var dropInstance = java.invoke(this.uuid, 'drop', [], true);
			var drop = new Drop();
			drop.uuid = dropInstance.uuid;
			return drop;
		};
	}
	
	var dialectInstance;
	if (connection) {
		dialectInstance = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'getNative', [connection.uuid], true);
	} else {
		dialectInstance = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'getDefault', [], true);
	}
	var dialect = new Dialect();
	dialect.uuid = dialectInstance.uuid;
	return dialect;
};
