# Eclipse Dirigible™ 

## Squle Module

The **Squle** module provides a builder for defining SQL scripts.

It aims at achieving:

* Type safe methods for the respective builders
* Native dialect dependent generation of the SQL
* Flexibility to extend the default builders at any level

It is purely text generation and formating library without a requirement for an active JDBC connection. The only exception is the *getNative(connection)* method below for deriving the actual dialect based on the Product Name for the JDBC meta-data.

## Samples

### Basic Sample

```java

	String sql = Squle.getDefault()
			.select()
			.column("*")
			.from("CUSTOMERS")
			.build();

```

> SELECT * FROM CUSTOMERS

### Native Dialect

```java

	String sql = Squle.getNative(new PostgresSquleDialect())
			.nextval("CUSTOMERS_SEQUENCE")
			.build();
			
```

> SELECT nextval('CUSTOMERS_SEQUENCE')

### Derive Dialect

```java

	... 
	Connection connection = datasource.getConnection();
	String sql = Squle.getNative(connection)
			.select()
			.column("*")
			.from("CUSTOMERS")
			.build();;
			
```

### Joining Tables

```java

	String sql = Squle.getDefault()
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

	String sql = Squle.getDefault()
			.select()
			.column("*")
			.from("CUSTOMERS")
			.where(Squle.getDefault().expression().and("PRICE > ?").or("AMOUNT < ?").build())
			.build();
			
```

> SELECT * FROM CUSTOMERS WHERE (PRICE > ? OR AMOUNT < ?)


### Limit and Offset

```java

	String sql = Squle.getDefault()
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

	String sql = Squle.getDefault()
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

	String sql = Squle.getDefault()
			.select()
			.column("COUNTRY")
			.from("CUSTOMERS")
			.union(Squle.getDefault().select().column("COUNTRY").from("SUPPLIERS").build())
			.build();
			
```

> SELECT COUNTRY FROM CUSTOMERS UNION SELECT COUNTRY FROM SUPPLIERS




