# Eclipse Dirigible™ 

## SQL Module

The **SQL** module provides a factory and builders for defining SQL scripts.

It is inspired by the Javascript library [Squel](https://hiddentao.com/squel/), but not limited to its features nor striving for compatibility.

It aims at achieving:

* Type safe methods for the respective builders
* Native dialect dependent generation of the SQL
* Flexibility to extend the default builders at any level

It is purely text generation and formating library without a requirement for an active JDBC connection. The only exception is the *getNative(connection)* method below for deriving the actual dialect based on the Product Name for the JDBC meta-data.

## Supported Data Types

VARCHAR, CHAR, DATE, TIME, TIMESTAMP, INTEGER, TINYINT, BIGINT, SMALLINT, REAL, DOUBLE, BOOLEAN, BLOB, DECIMAL, BIT

## Samples

### Basic Sample

```java

	String sql = SqlFactory.getDefault()
			.select()
			.column("*")
			.from("CUSTOMERS")
			.build();

```

> SELECT * FROM CUSTOMERS

### Native Dialect

```java

	String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.nextval("CUSTOMERS_SEQUENCE")
			.build();
			
```

> SELECT nextval('CUSTOMERS_SEQUENCE')

### Derive Dialect

```java

	... 
	Connection connection = datasource.getConnection();
	String sql = SqlFactory.getNative(connection)
			.select()
			.column("*")
			.from("CUSTOMERS")
			.build();;
			
```

### Joining Tables

```java

	String sql = SqlFactory.getDefault()
			.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.leftJoin("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.build();
			
```

> SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS LEFT JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID

### Expressions

```java

	String sql = SqlFactory.getDefault()
			.select()
			.column("*")
			.from("CUSTOMERS")
			.where(SqlFactory.getDefault().expression().and("PRICE > ?").or("AMOUNT < ?").build())
			.build();
			
```

> SELECT * FROM CUSTOMERS WHERE (PRICE > ? OR AMOUNT < ?)


### Limit and Offset

```java

	String sql = SqlFactory.getDefault()
			.select()
			.column("*")
			.from("CUSTOMERS")
			.limit(10)
			.offset(20)
			.build();
			
```

> SELECT * FROM CUSTOMERS LIMIT 10 OFFSET 20

### Group By and Having

```java

	String sql = SqlFactory.getDefault()
			.select()
			.column("COUNT(FIRST_NAME)")
			.column("COUNTRY")
			.from("CUSTOMERS")
			.group("COUNTRY")
			.having("COUNT(FIRST_NAME) > 5")
			.build();
			
```

> SELECT COUNT(FIRST_NAME), COUNTRY FROM CUSTOMERS GROUP BY COUNTRY HAVING COUNT(FIRST_NAME) > 5


### Union

```java

	String sql = SqlFactory.getDefault()
			.select()
			.column("COUNTRY")
			.from("CUSTOMERS")
			.union(SqlFactory.getDefault().select().column("COUNTRY").from("SUPPLIERS").build())
			.build();
			
```

> SELECT COUNTRY FROM CUSTOMERS UNION SELECT COUNTRY FROM SUPPLIERS

### Create Table

```java

	String sql = SqlFactory.getDefault()
				.create()
				.table("CUSTOMERS")
				.column("ID", DataType.INTEGER, Modifiers.PRIMARY_KEY, Modifiers.NOT_NULL, Modifiers.NON_UNIQUE)
				.column("FIRST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NOT_NULL, Modifiers.UNIQUE, "(20)")
				.column("LAST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(30)")
				.build();
				
```

> CREATE TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) NOT NULL UNIQUE , LAST_NAME VARCHAR (30) )

### Create Table with Constraints

```java

	String sql = SqlFactory.getDefault()
				.create()
				.table("CUSTOMERS")
				.column("FIRST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NOT_NULL, Modifiers.UNIQUE, "(20)")
				.column("LAST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(30)")
				.column("ADDRESS_ID", DataType.INTEGER, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE)
				.primaryKey(new String[] { "FIRST_NAME", "LAST_NAME" })
				.foreignKey("ADDRESS_FK", new String[] { "PERSON_ADDRESS_ID" }, "ADDRESSES", new String[] { "ADDRESS_ID" })
				.unique("LAST_NAME_UNIQUE", new String[] { "LAST_NAME" })
				.check("LAST_NAME_CHECK", "LAST_NAME = 'Smith'")
				.build();
				
```

> CREATE TABLE CUSTOMERS ( FIRST_NAME VARCHAR (20) NOT NULL , LAST_NAME VARCHAR (30) , ADDRESS_ID INTEGER , PRIMARY KEY ( FIRST_NAME , LAST_NAME ), CONSTRAINT ADDRESS_FK FOREIGN KEY ( PERSON_ADDRESS_ID ) REFERENCES ADDRESSES( ADDRESS_ID ), CONSTRAINT LAST_NAME_UNIQUE UNIQUE ( LAST_NAME ), CONSTRAINT LAST_NAME_CHECK CHECK (LAST_NAME = 'Smith'))


### Create View

```java

	String sql = SqlFactory.getDefault()
				.create().view("CUSTOMERS_VIEW")
				.column("ID")
				.column("FIRST_NAME")
				.column("LAST_NAME")
				.asSelect(SqlFactory.getDefault().select().column("*").from("CUSTOMERS").build())
				.build();
				
```

> CREATE VIEW CUSTOMERS_VIEW ( ID , FIRST_NAME , LAST_NAME ) AS SELECT * FROM CUSTOMERS

### Insert Record

```java

	tring sql = SqlFactory.getDefault()
			.insert()
			.into("CUSTOMERS")
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.build();
			
```

> INSERT INTO CUSTOMERS (FIRST_NAME, LAST_NAME) VALUES (?, ?)

### Update Record

```java

	String sql = SqlFactory.getDefault()
				.update()
				.table("CUSTOMERS")
				.set("FIRST_NAME", "'John'")
				.set("LAST_NAME", "'Smith'")
				.where("AGE > ?")
				.where("COMPANY = 'SAP'")
				.build();
				
```

> UPDATE CUSTOMERS SET FIRST_NAME = 'John', LAST_NAME = 'Smith' WHERE (AGE > ?) AND (COMPANY = 'SAP')

### Delete Record

```java

	String sql = SqlFactory.getDefault()
				.delete()
				.from("CUSTOMERS")
				.where("AGE > ?")
				.where("COMPANY = 'SAP'")
				.build();
				
```

> DELETE FROM CUSTOMERS WHERE (AGE > ?) AND (COMPANY = 'SAP')

### Drop Table

```java

	String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.drop()
			.table("CUSTOMERS")
			.build();
			
```

> DROP TABLE CUSTOMERS

More samples are available under the test folder of the project.

For the complete yet simplified POJO based database manipulation you might want to see the **Persistence** module


