package org.eclipse.dirigible.components.base.persistence;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class ObjectStore.
 */
@Component
public class ObjectStore {

	/** The session factory. */
	private SessionFactory sessionFactory;
	
	/** The data source. */
	private DataSource dataSource;
	
	/** The mappings. */
	private Map<String, String> mappings = new HashMap<String, String>();

	/**
	 * Instantiates a new object store.
	 *
	 * @param dataSource the data source
	 */
	@Autowired
	public ObjectStore(DataSource dataSource) {
		this.dataSource = dataSource;
		initialize();
	}
	
	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	public DataSource getDataSource() {
		return dataSource;
	}
	
	/**
	 * Sets the data source.
	 *
	 * @param dataSource the new data source
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Adds the mapping.
	 *
	 * @param name the name
	 * @param content the content
	 */
	public void addMapping(String name, String content) {
		mappings.put(name, content);
	}
	
	/**
	 * Initialize.
	 */
	public void initialize() {
		
		Configuration configuration = new Configuration()
				.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
				.setProperty("hibernate.show_sql", "true")
				.setProperty("hibernate.hbm2ddl.auto", "update")
				.setProperty("hibernate.current_session_context_class", "org.hibernate.context.internal.ThreadLocalSessionContext");
		
		mappings.entrySet().forEach(e -> configuration.addInputStream(IOUtils.toInputStream(e.getValue(), StandardCharsets.UTF_8)));
		
        StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
        serviceRegistryBuilder.applySetting(Environment.DATASOURCE, dataSource);
        serviceRegistryBuilder.applySettings(configuration.getProperties());
        StandardServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}

	/**
	 * Save.
	 *
	 * @param type the type
	 * @param json the json
	 */
	public void save(String type, String json) {
		save(type, json, dataSource);
	}

	/**
	 * Save.
	 *
	 * @param type the type
	 * @param json the json
	 * @param datasource the datasource
	 */
	public void save(String type, String json, DataSource datasource) {
        
		try (Session session = sessionFactory.openSession()) {
			
			Transaction transaction = session.beginTransaction();
			
			Map object = JsonHelper.fromJson(json, Map.class);
			
			session.save(type, object);
	
			transaction.commit();
		}
	}
	
	/**
	 * List.
	 *
	 * @param type the type
	 * @return the string
	 */
	public String list(String type) {
		try (Session session = sessionFactory.openSession()) {
			List list = session.createSQLQuery("SELECT * FROM " + type).list();
			return JsonHelper.toJson(list);
		}
	}

}
