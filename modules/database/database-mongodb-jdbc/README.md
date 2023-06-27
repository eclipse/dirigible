# mongo-jdbc
JDBC channel for MongoDB.
While this driver mostly adheres to the JDBC ways of connections, statements and resultsets, note that this driver does not translate to/from SQL. It uses Mongo's query format instead. 
You have the option to choose between browsing result sets using the JDBC API or directly get the underlying document and operate on it.

Example:

	MongodbConnection conn = new MongodbConnection("jdbc:mongodb://localhost/mydb", null);
	try {
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("{ find: \"testCollection\",filter: {$text:{$search:\"name\"}} }");
		while(rs.next()){
			System.out.println("get string by column name: " + rs.getString("name"));
			System.out.println("get string by column index: " + rs.getString(2));
			System.out.println("raw document: " + rs.getString(-100));
		}
		rs.close();
		st.close();
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		try{
			conn.close();
		} catch(SQLException sqlEx){
			sqlEx.printStackTrace();
		}
	}

### Tips, tricks, fancy features

#### Get the raw Document from ResultSet iteration as JSON

	String doc = rs.getObject(-100);

#### Specify a collection to query

	stmt.executeQuery(‘{find:"testCollection"}’);
	
