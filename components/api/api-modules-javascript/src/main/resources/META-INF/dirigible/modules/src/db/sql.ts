/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API SQL
 *
 */
const DatabaseFacade = Java.type("org.eclipse.dirigible.components.api.db.DatabaseFacade");

/**
			 * Select object
			 */
class Select {
	native: any;
	_parameters: any[];
	build: Function;
	parameters: Function;


	constructor(native, connection?) {
		this.native = native;
		this._parameters = [];

		if(connection) {
			this.build = getDialect(connection).build.bind(this);
			this.parameters = getDialect(connection).parameters.bind(this);
		} else {
			this.build = getDialect().build.bind(this);
			this.parameters = getDialect().parameters.bind(this);
		}
	}

	distinc(): Select {
		this.native.distinct();
		return this;
	};

	forUpdate(): Select {
		this.native.forUpdate();
		return this;
	};

	column(column: string): Select {
		this.native.column(column);
		return this;
	};

	from(table: string, alias: string): Select {
		if (alias !== undefined) {
			this.native.from(table, alias);
		} else {
			this.native.from(table);
		}
		return this;
	};

	join(table: string, on: string, alias: string): Select {
		if (alias !== undefined) {
			this.native.join(table, on, alias);
			if(arguments.length>3){
				if(Array.isArray(arguments[3]))
					this._parameters = this._parameters.concat([arguments[3]]);
				else
					this._parameters.push(arguments[3]);
			}
		} else {
			this.native.join(table, on);
			if(arguments.length>2){
				if(Array.isArray(arguments[2]))
					this._parameters = this._parameters.concat([arguments[2]]);
				else
					this._parameters.push(arguments[2]);
			}
		}
		return this;
	};

	innerJoin(table, on, alias) {
		if (alias !== undefined) {
			this.native.innerJoin(table, on, alias);
			if(arguments.length>3){
				if(Array.isArray(arguments[3]))
					this._parameters = this._parameters.concat(arguments[3]);
				else
					this._parameters.push(arguments[3]);
			}
		} else {
			this.native.innerJoin(table, on);
			if(arguments.length>2){
				if(Array.isArray(arguments[2]))
					this._parameters = this._parameters.concat(arguments[2]);
				else
					this._parameters.push(arguments[2]);
			}
		}
		return this;
	};

	outerJoin(table: string, on: string, alias: string): Select {
		if (alias !== undefined) {
			this.native.outerJoin(table, on, alias);
			if(arguments.length>3){
				if(Array.isArray(arguments[3]))
					this._parameters = this._parameters.concat(arguments[3]);
				else
					this._parameters.push(arguments[3]);
			}
		} else {
			this.native.outerJoin(table, on);
			if(arguments.length>2){
				if(Array.isArray(arguments[2]))
					this._parameters = this._parameters.concat(arguments[2]);
				else
					this._parameters.push(arguments[2]);
			}
		}
		return this;
	};

	leftJoin(table: string, on: string, alias: string): Select {
		if (alias !== undefined) {
			this.native.leftJoin(table, on, alias);
			if(arguments.length>3){
				if(Array.isArray(arguments[3]))
					this._parameters = this._parameters.concat(arguments[3]);
				else
					this._parameters.push(arguments[3]);
			}
		} else {
			this.native.leftJoin(table, on);
			if(arguments.length>2){
				if(Array.isArray(arguments[2]))
					this._parameters = this._parameters.concat(arguments[2]);
				else
					this._parameters.push(arguments[2]);
			}
		}
		return this;
	};

	rightJoin(table: string, on: string, alias: string): Select {
		if (alias !== undefined) {
			this.native.rightJoin(table, on, alias);
			if(arguments.length>3){
				if(Array.isArray(arguments[3]))
					this._parameters = this._parameters.concat(arguments[3]);
				else
					this._parameters.push(arguments[3]);
			}
		} else {
			this.native.rightJoin(table, on);
			if(arguments.length>2){
				if(Array.isArray(arguments[2]))
					this._parameters = this._parameters.concat(arguments[2]);
				else
					this._parameters.push(arguments[2]);
			}
		}
		return this;
	};

	fullJoin(table: string, on: string, alias: string): Select {
		if (alias !== undefined) {
			this.native.fullJoin(table, on, alias);
			if(arguments.length>3){
				if(Array.isArray(arguments[3]))
					this._parameters = this._parameters.concat(arguments[3]);
				else
					this._parameters.push(arguments[3]);
			}
		} else {
			this.native.fullJoin(table, on);
			if(arguments.length>2){
				if(Array.isArray(arguments[2]))
					this._parameters = this._parameters.concat(arguments[2]);
				else
					this._parameters.push(arguments[2]);
			}
		}
		return this;
	};

	where(condition: string): Select {
		this.native.where(condition);
		if(arguments.length>1){
			if(Array.isArray(arguments[1]))
				this._parameters = this._parameters.concat(arguments[1]);
			else
				this._parameters.push(arguments[1]);
		}
		return this;
	};

	order(column: string, asc: boolean): Select {
		if (asc !== undefined) {
			this.native.order(column, asc);
		} else {
			this.native.order(column);
		}
		return this;
	};

	group(column: string): Select {
		this.native.group(column);
		return this;
	};

	limit(limit: number): Select {
		this.native.limit(limit);
		return this;
	};

	offset(offset: number): Select {
		this.native.offset(offset);
		return this;
	};

	having(having: string): Select {
		this.native.having(having);
		return this;
	};

	union(select: Select): Select {
		this.native.union(select);
		return this;
	};
}


/**
			 * Insert object
			 */
class Insert {
	native: any;
	_parameters: any[];
	build: Function;
	parameters: Function;

	constructor(native, connection?) {
		this.native = native;
		this._parameters = [];

		if(connection) {
			this.build = getDialect(connection).build.bind(this);
			this.parameters = getDialect(connection).parameters.bind(this);
		} else {
			this.build = getDialect().build.bind(this);
			this.parameters = getDialect().parameters.bind(this);
		}
	}

	into(table: string): Insert {
		this.native.into(table);
		return this;
	};

	column(column: string): Insert {
		this.native.column(column);
		return this;
	};

	value(value: string): Insert {
		this.native.value(value);
		if(arguments.length>1){
			if(Array.isArray(arguments[1]))
				this._parameters = this._parameters.concat(arguments[1]);
			else
				this._parameters.push(arguments[1]);
		}
		return this;
	};

	select(select: string): Insert {
		this.native.select(select);
		return this;
	};

};

/**
			 * Update object
			 */
class Update {
	native: any;
	_parameters: any[];
	build: Function;
	parameters: Function;

	constructor(native, connection?) {
		this.native = native;
		this._parameters = [];

		if(connection) {
			this.build = getDialect(connection).build.bind(this);
			this.parameters = getDialect(connection).parameters.bind(this);
		} else {
			this.build = getDialect().build.bind(this);
			this.parameters = getDialect().parameters.bind(this);
		}
	}

	table(table: string): Update {
		this.native.table(table);
		return this;
	};

	set(column: string, value: string): Update {
		this.native.set(column, value);
		if(arguments.length>2){
			if(Array.isArray(arguments[2]))
				this._parameters = this._parameters.concat(arguments[2]);
			else
				this._parameters.push(arguments[2]);
		}
		return this;
	};

	where(condition: string): Update {
		this.native.where(condition);
		if(arguments.length>1){
			if(Array.isArray(arguments[1]))
				this._parameters = this._parameters.concat(arguments[1]);
			else
				this._parameters.push(arguments[1]);
		}
		return this;
	};
};




/**
 * Delete object
*/
class Delete {
	native: any;
	_parameters: any[];
	build: Function;
	parameters: Function;
	
	constructor(native, connection?) {
		this.native = native;
		this._parameters = [];
		
		if(connection) {
			this.build = getDialect(connection).build.bind(this);
			this.parameters = getDialect(connection).parameters.bind(this);
		} else {
			this.build = getDialect().build.bind(this);
			this.parameters = getDialect().parameters.bind(this);
		}
	}
	
	from(table: string): Delete {
		this.native.from(table);
		return this;
	};
	
	where(condition: string): Delete {
		this.native.where(condition);
		if(arguments.length>1){
			if(Array.isArray(arguments[1]))
			this._parameters = this._parameters.concat(arguments[1]);
		else
			this._parameters.push(arguments[1]);
		}
		return this;
	}
}
type DataType = "VARCHAR" | "CHAR" | "DATE" | "SECONDDATE" | "TIME" | "TIMESTAMP" | "INTEGER" | "TINYINT" | "BIGINT" | "SMALLINT" | "REAL" | "DOUBLE" | "DOUBLE PRECISION" | "BOOLEAN" | "BLOB" | "DECIMAL" | "BIT" | "NVARCHAR" | "FLOAT" | "BYTE" | "NCLOB" | "ARRAY" | "VARBINARY" | "BINARY VARYING" | "SHORTTEXT" | "ALPHANUM" | "CLOB" | "SMALLDECIMAL" | "BINARY" | "ST_POINT" | "ST_GEOMETRY" | "CHARACTER VARYING" | "BINARY LARGE OBJECT" | "CHARACTER LARGE OBJECT" | "CHARACTER" | "NCHAR" | "NUMERIC";

/**
 * CreateTable object
*/
class CreateTable {
	native: any;
	_parameters: any[];
	build: Function;
	parameters: Function;

	constructor(native, connection?) {
		this.native = native;
		this._parameters = [];

		if(connection) {
			this.build = getDialect(connection).build.bind(this);
			this.parameters = getDialect(connection).parameters.bind(this);
		} else {
			this.build = getDialect().build.bind(this);
			this.parameters = getDialect().parameters.bind(this);
		}
	}

	

	column(column: string, type: DataType, isPrimaryKey?: boolean, isNullable?: boolean, isUnique?: boolean, args?: string) {
		if (args !== undefined) {
			this.native.column(column, type, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.column(column, type, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.column(column, type, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.column(column, type, isPrimaryKey);
					} else {
						this.native.column(column, type);
					}
				}
			}
		}

		return this;
	};

	columnVarchar(column: string, length: number, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnVarchar(column, length, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnVarchar(column, length, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnVarchar(column, length, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnVarchar(column, length, isPrimaryKey);
					} else {
						this.native.columnVarchar(column, length);
					}
				}
			}
		}

		return this;
	};

	columnChar(column: string, length: number, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnChar(column, length, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnChar(column, length, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnChar(column, length, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnChar(column, length, isPrimaryKey);
					} else {
						this.native.columnChar(column, length);
					}
				}
			}
		}

		return this;
	};

	columnDate(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string) {
		if (args !== undefined) {
			this.native.columnDate(column, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnDate(column, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnDate(column, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnDate(column, isPrimaryKey);
					} else {
						this.native.columnDate(column);
					}
				}
			}
		}

		return this;
	};

	columnTime(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnTime(column, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnTime(column, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnTime(column, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnTime(column, isPrimaryKey);
					} else {
						this.native.columnTime(column);
					}
				}
			}
		}

		return this;
	};

	columnTimestamp(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnTimestamp(column, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnTimestamp(column, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnTimestamp(column, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnTimestamp(column, isPrimaryKey);
					} else {
						this.native.columnTimestamp(column);
					}
				}
			}
		}

		return this;
	};

	columnInteger(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnInteger(column, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnInteger(column, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnInteger(column, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnInteger(column, isPrimaryKey);
					} else {
						this.native.columnInteger(column);
					}
				}
			}
		}

		return this;
	};

	columnTinyint(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnTinyint(column, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnTinyint(column, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnTinyint(column, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnTinyint(column, isPrimaryKey);
					} else {
						this.native.columnTinyint(column);
					}
				}
			}
		}

		return this;
	};

	columnBigint(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnBigint(column, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnBigint(column, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnBigint(column, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnBigint(column, isPrimaryKey);
					} else {
						this.native.columnBigint(column);
					}
				}
			}
		}

		return this;
	};

	columnSmallint(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnSmallint(column, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnSmallint(column, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnSmallint(column, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnSmallint(column, isPrimaryKey);
					} else {
						this.native.columnSmallint(column);
					}
				}
			}
		}

		return this;
	};

	columnReal(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnReal(column, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnReal(column, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnReal(column, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnReal(column, isPrimaryKey);
					} else {
						this.native.columnReal(column);
					}
				}
			}
		}

		return this;
	};

	columnDouble(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnDouble(column, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnDouble(column, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnDouble(column, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnDouble(column, isPrimaryKey);
					} else {
						this.native.columnDouble(column);
					}
				}
			}
		}

		return this;
	};

	columnBoolean(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnBoolean(column, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnBoolean(column, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnBoolean(column, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnBoolean(column, isPrimaryKey);
					} else {
						this.native.columnBoolean(column);
					}
				}
			}
		}

		return this;
	};

	columnBlob(column: string, precision: number, scale: number, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnBlob(column, precision, scale, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnBlob(column, precision, scale, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnBlob(column, precision, scale, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnBlob(column, precision, scale, isPrimaryKey);
					} else {
						this.native.columnBlob(column, precision, scale);
					}
				}
			}
		}

		return this;
	};

	columnDecimal(column: string, precision: number, scale: number, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
		if (args !== undefined) {
			this.native.columnDecimal(column, precision, scale, isPrimaryKey, isNullable, isUnique, args);
		} else {
			if (isUnique) {
				this.native.columnDecimal(column, precision, scale, isPrimaryKey, isNullable, isUnique);
			} else {
				if (isNullable) {
					this.native.columnDecimal(column, precision, scale, isPrimaryKey, isNullable);
				} else {
					if (isPrimaryKey) {
						this.native.columnDecimal(column, precision, scale, isPrimaryKey);
					} else {
						this.native.columnDecimal(column, precision, scale);
					}
				}
			}
		}

		return this;
	};

	primaryKey(columns: string[], name?: string): CreateTable {
		if (name !== undefined) {
			this.native.primaryKey(name, columns);
		} else {
			this.native.primaryKey(columns);
		}
		return this;
	};

	foreignKey(name: string, columns: string[], referencedTable: string, referencedColumns: string[]): CreateTable {
		this.native.foreignKey(name, columns, referencedTable, referencedColumns);
		return this;
	};

	unique(name: string, columns: string[]): CreateTable {
		this.native.unique(name, columns);
		return this;
	};

	check(name: string, expression: string): CreateTable {
		this.native.check(name, expression);
		return this;
	};
}


export function getDialect(connection?) {
	/**
	 * Dialect object
	 */
	let Dialect = function() {

		let _parameters = [];

		const parameters = function (): any[] {
			return _parameters;
		};

		const build = function (): string {
			return this.native.build();
		};

		this.select = function(): Select{
			return new Select(this.native.select(), connection);
		};

		this.insert = function() {
			return new Insert(this.native.insert(), connection);
		};

		this.update = function() {
			return new Update(this.native.update(), connection);
		};

		this.delete = function() {
			return new Delete(this.native.delete(), connection);
		};


		this.nextval = function(name: string): Function {

			/**
			 * Nextval object
			 */
			let Nextval = function(name: string): void {
				this.name = name;
				this.build = build.bind(this);
			}

			const nextval = new Nextval(name);
			nextval.native = this.native.nextval(name);
			return nextval;
		};

		this.create = function() {

			/**
			 * Create object
			 */
			let Create = function() {

				this.table = function(table) {
					return new CreateTable(this.native.table(table), connection);
				};

				this.view = function(view) {
					/**
					 * CreateView object
					 */
					let CreateView = function() {

						this.column = function(column) {
							this.native.column(column);
							return this;
						};

						this.asSelect = function(select) {
							this.native.asSelect(select);
							return this;
						};

						this.build = build.bind(this);
					}

					_parameters = [];
					const createView = new CreateView();
					createView.native = this.native.view(view);
					return createView;
				};

				this.sequence = function(sequence) {
					/**
					 * CreateSequence object
					 */
					let CreateSequence = function() {

						this.build = build.bind(this);
					}

					const createSequence = new CreateSequence();
					createSequence.native = this.native.sequence(sequence);
					return createSequence;
				};

			}

			const create = new Create();
			create.native = this.native.create();
			return create;
		};

		this.drop = function() {

			/**
			 * Drop object
			 */
			let Drop = function() {

				this.table = function(table) {
					/**
					 * DropTable object
					 */
					const DropTable = function () {

						this.build = build.bind(this);
					};

					const dropTable = new DropTable();
					dropTable.native = this.native.table(table);
					return dropTable;
				};

				this.view = function(view) {
					/**
					 * DropView object
					 */
					const DropView = function () {

						this.build = build.bind(this);
					};

					const dropView = new DropView();
					dropView.native = this.native.view(view);
					return dropView;
				};

				this.sequence = function(sequence) {
					/**
					 * DropSequence object
					 */
					let DropSequence = function() {

						this.build = build.bind(this);
					}

					const dropSequence = new DropSequence();
					dropSequence.native = this.native.sequence(sequence);
					return dropSequence;
				};

			}

			const drop = new Drop();
			drop.native = this.native.drop();
			return drop;
		};
	}

	const dialect = new Dialect();
	let native;
	if (connection) {
		native = DatabaseFacade.getNative(connection.native);
	} else {
		native = DatabaseFacade.getDefault();
	}
	dialect.native = native;
	return dialect;
};
