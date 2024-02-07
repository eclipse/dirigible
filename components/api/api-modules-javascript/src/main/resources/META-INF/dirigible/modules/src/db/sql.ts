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
	private native: any;
	private _parameters: any[];
	public build: Function;
	public parameters: Function;


	constructor(native, connection?) {
		this.native = native;
		this._parameters = [];

		this.build = getDialect(connection).build.bind(this);
		this.parameters = getDialect(connection).parameters.bind(this);
	}

	public distinc(): Select {
		this.native.distinct();
		return this;
	};

	public forUpdate(): Select {
		this.native.forUpdate();
		return this;
	};

	public column(column: string): Select {
		this.native.column(column);
		return this;
	};

	public from(table: string, alias: string): Select {
		if (alias !== undefined) {
			this.native.from(table, alias);
		} else {
			this.native.from(table);
		}
		return this;
	};

	public join(table: string, on: string, alias: string): Select {
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

	public innerJoin(table, on, alias): Select {
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

	public outerJoin(table: string, on: string, alias: string): Select {
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

	public leftJoin(table: string, on: string, alias: string): Select {
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

	public rightJoin(table: string, on: string, alias: string): Select {
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

	public fullJoin(table: string, on: string, alias: string): Select {
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

	public where(condition: string): Select {
		this.native.where(condition);
		if(arguments.length>1){
			if(Array.isArray(arguments[1]))
				this._parameters = this._parameters.concat(arguments[1]);
			else
				this._parameters.push(arguments[1]);
		}
		return this;
	};

	public order(column: string, asc: boolean): Select {
		if (asc !== undefined) {
			this.native.order(column, asc);
		} else {
			this.native.order(column);
		}
		return this;
	};

	public group(column: string): Select {
		this.native.group(column);
		return this;
	};

	public limit(limit: number): Select {
		this.native.limit(limit);
		return this;
	};

	public offset(offset: number): Select {
		this.native.offset(offset);
		return this;
	};

	public having(having: string): Select {
		this.native.having(having);
		return this;
	};

	public union(select: Select): Select {
		this.native.union(select);
		return this;
	};
}



/**
 * Insert object
 */
class Insert {
	private native: any;
	private _parameters: any[];
	public build: Function;
	public parameters: Function;

	constructor(native, connection?) {
		this.native = native;
		this._parameters = [];

		this.build = getDialect(connection).build.bind(this);
		this.parameters = getDialect(connection).parameters.bind(this);
	}

	public into(table: string): Insert {
		this.native.into(table);
		return this;
	};

	public column(column: string): Insert {
		this.native.column(column);
		return this;
	};

	public value(value: string): Insert {
		this.native.value(value);
		if(arguments.length>1){
			if(Array.isArray(arguments[1]))
				this._parameters = this._parameters.concat(arguments[1]);
			else
				this._parameters.push(arguments[1]);
		}
		return this;
	};

	public select(select: string): Insert {
		this.native.select(select);
		return this;
	};

};



/**
 * Update object
 */
class Update {
	private native: any;
	private _parameters: any[];
	public build: Function;
	public parameters: Function;

	constructor(native, connection?) {
		this.native = native;
		this._parameters = [];

		this.build = getDialect(connection).build.bind(this);
		this.parameters = getDialect(connection).parameters.bind(this);
	}

	public table(table: string): Update {
		this.native.table(table);
		return this;
	};

	public set(column: string, value: string): Update {
		this.native.set(column, value);
		if(arguments.length>2){
			if(Array.isArray(arguments[2]))
				this._parameters = this._parameters.concat(arguments[2]);
			else
				this._parameters.push(arguments[2]);
		}
		return this;
	};

	public where(condition: string): Update {
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
	private native: any;
	private _parameters: any[];
	public build: Function;
	public parameters: Function;
	
	constructor(native, connection?) {
		this.native = native;
		this._parameters = [];
		
		this.build = getDialect(connection).build.bind(this);
		this.parameters = getDialect(connection).parameters.bind(this);
	}
	
	public from(table: string): Delete {
		this.native.from(table);
		return this;
	};
	
	public where(condition: string): Delete {
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



/**
 * Nextval object
 */
class Nextval {
	public name: string;
	private native: any;
	public build: Function;

	constructor(name: string, native?, connection?) {
		this.name = name;
		this.native = native;
		
		this.build = getDialect(connection).build.bind(this);
	}
}



type DataType = "VARCHAR" | "CHAR" | "DATE" | "SECONDDATE" | "TIME" | "TIMESTAMP" | "INTEGER" | "TINYINT" | "BIGINT" | "SMALLINT" | "REAL" | "DOUBLE" | "DOUBLE PRECISION" | "BOOLEAN" | "BLOB" | "DECIMAL" | "BIT" | "NVARCHAR" | "FLOAT" | "BYTE" | "NCLOB" | "ARRAY" | "VARBINARY" | "BINARY VARYING" | "SHORTTEXT" | "ALPHANUM" | "CLOB" | "SMALLDECIMAL" | "BINARY" | "ST_POINT" | "ST_GEOMETRY" | "CHARACTER VARYING" | "BINARY LARGE OBJECT" | "CHARACTER LARGE OBJECT" | "CHARACTER" | "NCHAR" | "NUMERIC";
/**
 * CreateTable object
*/
class CreateTable {
	private native: any;
	public build: Function;

	constructor(native, connection?) {
		this.native = native;

		this.build = getDialect(connection).build.bind(this);
	}

	
	public column(column: string, type: DataType, isPrimaryKey?: boolean, isNullable?: boolean, isUnique?: boolean, args?: string): CreateTable {
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

	public columnVarchar(column: string, length: number, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnChar(column: string, length: number, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnDate(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnTime(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnTimestamp(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnInteger(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnTinyint(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnBigint(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnSmallint(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnReal(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnDouble(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnBoolean(column: string, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnBlob(column: string, precision: number, scale: number, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public columnDecimal(column: string, precision: number, scale: number, isPrimaryKey: boolean, isNullable: boolean, isUnique: boolean, args: string): CreateTable {
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

	public primaryKey(columns: string[], name?: string): CreateTable {
		if (name !== undefined) {
			this.native.primaryKey(name, columns);
		} else {
			this.native.primaryKey(columns);
		}
		return this;
	};

	public foreignKey(name: string, columns: string[], referencedTable: string, referencedColumns: string[]): CreateTable {
		this.native.foreignKey(name, columns, referencedTable, referencedColumns);
		return this;
	};

	public unique(name: string, columns: string[]): CreateTable {
		this.native.unique(name, columns);
		return this;
	};

	public check(name: string, expression: string): CreateTable {
		this.native.check(name, expression);
		return this;
	};
}



/**
 * CreateView object
 */
class CreateView {
	private native: any;
	private _parameters: any[];
	public build: Function;

	constructor(native, connection?) {
		this.native = native;
		this._parameters = [];

		this.build = getDialect(connection).build.bind(this);
	}


	public column(column: string): CreateView {
		this.native.column(column);
		return this;
	};

	public asSelect(select: string): CreateView {
		this.native.asSelect(select);
		return this;
	};
}



/**
 * CreateSequence object
 */
class CreateSequence {
	private native: any;
	public build: Function;

	constructor(native, connection?) {
		this.native = native;
		this.build = getDialect(connection).build.bind(this);
	}
}



/**
 * Create object
 */
class Create {
	private native: any;
	private connection: any;

	constructor(native, connection?) {
		this.native = native;
		this.connection = connection;
	}

	public table(table: string): CreateTable {
		return new CreateTable(this.native.table(table), this.connection);
	};

	public view(view: string): CreateView {
		return new CreateView(this.native.view(view), this.connection);
	};

	public sequence(sequence: string): CreateSequence {
		return new CreateSequence(this.native.sequence(sequence), this.connection);
	};

}



/**
 * DropTable object
 */
class DropTable {
	private native: any;
	public build: Function;

	constructor(native, connection?) {
		this.native = native;

		this.build = getDialect(connection).build.bind(this);
	}
};



/**
 * DropView object
 */
class DropView {
	private native: any;
	public build: Function;

	constructor(native, connection?) {
		this.native = native;

		this.build = getDialect(connection).build.bind(this);
	}
};



/**
 * DropSequence object
 */
class DropSequence {
	private native: any;
	public build: Function;

	constructor(native, connection?) {
		this.native = native;

		this.build = getDialect(connection).build.bind(this);
	}
}


/**
 * Drop object
 */
class Drop {
	private native: any;
	private connection: any;

	constructor(native, connection?) {
		this.native = native;
		this.connection = connection;
	}

	public table(table: string): DropTable {
		return new DropTable(this.native.table(table), this.connection);
	};

	public view(view: string): DropView {
		return new DropView(this.native.view(view), this.connection);
	};

	public sequence(sequence: string): DropSequence {
		return new DropSequence(this.native.sequence(sequence), this.connection);
	};

}



/**
 * Dialect object
 */
class Dialect {
	private native: any;
	private connection: any;
	private _parameters: any[];

	constructor(connection?) {
		if (connection) {
			this.native = DatabaseFacade.getNative(connection.native);
		} else {
			this.native = DatabaseFacade.getDefault();
		}

		this.connection = connection;
		this._parameters = [];
	}

	public parameters(): any[] {
		return this._parameters;
	};

	public build(): string {
		return this.native.build();
	};

	public select(): Select{
		return new Select(this.native.select(), this.connection);
	};

	public insert(): Insert{
		return new Insert(this.native.insert(), this.connection);
	};

	public update(): Update{
		return new Update(this.native.update(), this.connection);
	};

	public delete(): Delete{
		return new Delete(this.native.delete(), this.connection);
	};


	public nextval(name: string): Nextval {
		return new Nextval(name, this.native.nextval(), this.connection);
	};

	public create(): Create {
		return new Create(this.native.create(), this.connection);
	};

	public drop(): Drop {
		return new Drop(this.native.drop(), this.connection);
	};
}


export function getDialect(connection?): Dialect {
	return new Dialect(connection);
};
