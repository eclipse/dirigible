# Eclipse Dirigible™ 

## Persistence Module

The **Persistence** module provides a simplified POJO base database manipulation library.

It uses some of the standard JPA annotations, but it is different in some aspects:

* Simple, straight forward, DAO like approach for database access
* Direct mapping from POJO to Tables as well as Views
* Flat entities only support
* No caches (L1, L2)
* No JPQL - it uses **SQL** module for native SQL script generation
* No heavyweight external dependencies - only to the JDBC driver and SQL module
* Enum types support
* Initializing of the schema (tables, views, sequences, etc.) can be done by itself or can be used an external library e.g. Liquibase
* Connection have to be provided from outside, hence it is flexible to be established from a container managed DataSource, a custom DataSource or even in standalone mode.
* Dependency Injection free - it can be used with the dependency injection framework of your choice, e.g. Spring, Guice, CDI, without collisions

## Samples

### Create Table

#### POJO Customer

```java

	@Table(name="CUSTOMERS", schema="FACTORY")
	public class Customer {
		
		@Id
		@Column(name="CUSTOMER_ID", columnDefinition="INTEGER", nullable=false)
		private int id;
		
		@Column(name="CUSTOMER_FIRST_NAME", columnDefinition="VARCHAR", nullable=false, length=512)
		private String firstName;
		
		@Column(name="CUSTOMER_LAST_NAME", columnDefinition="VARCHAR", nullable=false, length=512)
		private String lastName;
		
		@Column(name="CUSTOMER_AGE", columnDefinition="INTEGER", nullable=false)
		private int age;
	
		public int getId() {
			return id;
		}
	
		public void setId(int id) {
			this.id = id;
		}
	
		public String getFirstName() {
			return firstName;
		}
	
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
	
		public String getLastName() {
			return lastName;
		}
	
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
	
		public int getAge() {
			return age;
		}
	
		public void setAge(int age) {
			this.age = age;
		}
	
	}
	
```

#### Type safe PersistenceManager

```java

	PersistenceManager<Customer> persistenceManager = new PersistenceManager<Customer>();
	Connection connection = ...getConnection();
	try {
		persistenceManager.tableCreate(connection, Customer.class);
	} finally {
		connection.close();
	}
	
```

### Insert POJO

```java

	PersistenceManager<Customer> persistenceManager = new PersistenceManager<Customer>();
	Connection connection = ...getConnection();
	try {
		Customer customer = new Customer();
		customer.setId(1);
		customer.setFirstName("John");
		customer.setLastName("Smith");
		customer.setAge(33);
		int id = persistenceManager.insert(connection, customer);
	} finally {
		connection.close();
	}
	
```

### Find POJO

```java

	PersistenceManager<Customer> persistenceManager = new PersistenceManager<Customer>();
	Connection connection = ...getConnection();
	try {
		Customer customer = persistenceManager.find(connection, Customer.class, 1);
	} finally {
		connection.close();
	}
	
```

### Query POJO

```java

	PersistenceManager<Customer> persistenceManager = new PersistenceManager<Customer>();
	Connection connection = ...getConnection();
	try {
		String sql = SqlFactory.getNative(connection)
			.select()
			.column("*")
			.from("CUSTOMERS")
			.where("CUSTOMER_FIRST_NAME = ?")
			.build();

		List<Customer> list = persistenceManager.query(connection, Customer.class, sql, "John");
	} finally {
		connection.close();
	}
	
```

### Update POJO

```java

	PersistenceManager<Customer> persistenceManager = new PersistenceManager<Customer>();
	Connection connection = ...getConnection();
	try {
		Customer customer = persistenceManager.find(connection, Customer.class, 1);
		customer.setLastName("Wayne");
		int result = persistenceManager.update(connection, customer);
	} finally {
		connection.close();
	}
	
```

### Delete POJO

```java

	PersistenceManager<Customer> persistenceManager = new PersistenceManager<Customer>();
	Connection connection = ...getConnection();
	try {
		Customer customer = persistenceManager.find(connection, Customer.class, 1);
		int result = persistenceManager.delete(connection, Customer.class, 1);
	} finally {
		connection.close();
	}
	
```

### Drop Table

```java

	PersistenceManager<Customer> persistenceManager = new PersistenceManager<Customer>();
	Connection connection = ...getConnection();
	try {
		persistenceManager.tableDrop(connection, Customer.class);
	} finally {
		connection.close();
	}
	
```

### Generated Id

#### POJO Order with a generated field **id**

Default strategy is **TABLE**. In case of **SEQUENCE** you can use ```@GeneratedValue(strategy = GenerationType.SEQUENCE)``` and in case of **IDENTITY** - ```@GeneratedValue(strategy = GenerationType.IDENTITY)```. **AUTO** is considered **TABLE**.

While the default strategy is the **TABLE** for better compatibility with the different databases, it is highly recommended to use **SEQUENCE** or **IDENTITY**, if possible. Use **IDENTITY** only with **Id** (Primary Key).

```java

	@Table(name = "ORDERS")
	public class Order {
	
		@Id
		@GeneratedValue
		@Column(name = "ORDER_ID", columnDefinition = "BIGINT", nullable = false)
		private long id;
	
		@Column(name = "CUSTOMER_SUBJECT", columnDefinition = "VARCHAR", nullable = false, length = 512)
		private String subject;
	
		public long getId() {
			return id;
		}
	
		public void setId(long id) {
			this.id = id;
		}
	
		public String getSubject() {
			return subject;
		}
	
		public void setSubject(String subject) {
			this.subject = subject;
		}
	
	}
	
```

#### Insert (no need to set the **id** field)

```java

	PersistenceManager<Order> persistenceManager = new PersistenceManager<Order>();
	Connection connection = ...getConnection();
	try {
		Order order = new Order();
		order.setSubject("Subject 1");
		persistenceManager.insert(connection, order);
	} finally {
		connection.close();
	}
	
```

### Enum fields

#### POJO Process with an Enum fields **typeAsString** and **typeAsInt**

```java

	@Table(name = "PROCESSES")
	public class Process {
	
		enum ProcessType {
			STARTED, STOPPED, FAILED, INPROGRESS
		}
	
		@Id
		@GeneratedValue
		@Column(name = "PROCESS_ID", columnDefinition = "BIGINT", nullable = false)
		private long id;
	
		@Column(name = "PROCESS_NAME", columnDefinition = "VARCHAR", nullable = false, length = 512)
		private String name;
	
		@Column(name = "PROCESS_TYPE_AS_STRING")
		@Enumerated(EnumType.STRING)
		private ProcessType typeAsString;
	
		@Column(name = "PROCESS_TYPE_AS_INT")
		@Enumerated(EnumType.ORDINAL)
		private ProcessType typeAsInt;
	
		public long getId() {
			return id;
		}
	
		public void setId(long id) {
			this.id = id;
		}
	
		public String getName() {
			return name;
		}
	
		public void setName(String name) {
			this.name = name;
		}
	
		public ProcessType getTypeAsString() {
			return typeAsString;
		}
	
		public void setTypeAsString(ProcessType typeAsString) {
			this.typeAsString = typeAsString;
		}
	
		public ProcessType getTypeAsInt() {
			return typeAsInt;
		}
	
		public void setTypeAsInt(ProcessType typeAsInt) {
			this.typeAsInt = typeAsInt;
		}
	
	}
	
```

#### Insert (both works: stored as a string or as an int value)

```java

	PersistenceManager<Process> persistenceManager = new PersistenceManager<Process>();
	Connection connection = ...getConnection();
	try {
		Process process = new Process();
		process.setName("Process1");
		process.setTypeAsInt(Process.ProcessType.STARTED);
		process.setTypeAsString(Process.ProcessType.STARTED);
		persistenceManager.insert(connection, process);
	} finally {
		connection.close();
	}
	
```
