package org.eclipse.dirigible.database.ds.test;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class Table1
 *
 */
@Table(name = "TABLE1")
public class Table1 {
	
	/** The id. */
	@Id
	@GeneratedValue
	@Column(name = "TABLE1_ID", columnDefinition = "INTEGER", nullable = false)
	private int id;

	/** The foreign key to table3. */
	@Column(name = "TABLE3_ID", columnDefinition = "INTEGER", nullable = false)
	private String table3;
	
	/** The foreign key to table4. */
	@Column(name = "TABLE4_ID", columnDefinition = "INTEGER", nullable = false)
	private String table4;

}
