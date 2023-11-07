/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.store;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class ObjectStore.
 */
@Component
public class DataStore {

  /** The session factory. */
  private SessionFactory sessionFactory;

  /** The datasources manager. */
  private DataSourcesManager datasourcesManager;

  /** The data source. */
  private DataSource dataSource;


  /** The mappings. */
  private Map<String, String> mappings = new HashMap<String, String>();

  /**
   * Instantiates a new object store.
   *
   * @param datasourcesManager the datasources manager
   */
  @Autowired
  public DataStore(DataSourcesManager datasourcesManager) {
    this.datasourcesManager = datasourcesManager;
  }

  /**
   * Gets the datasources manager.
   *
   * @return the datasources manager
   */
  public DataSourcesManager getDatasourcesManager() {
    return datasourcesManager;
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
   * Removes the mapping.
   *
   * @param name the name
   */
  public void removeMapping(String name) {
    mappings.remove(name);
  }

  /**
   * Initialize.
   */
  public void initialize() {
    if (this.dataSource == null) {
      this.dataSource = datasourcesManager.getDefaultDataSource();
    }

    Configuration configuration = new Configuration().setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                                                     .setProperty("hibernate.show_sql", "true")
                                                     .setProperty("hibernate.hbm2ddl.auto", "update")
                                                     .setProperty("hibernate.current_session_context_class",
                                                         "org.hibernate.context.internal.ThreadLocalSessionContext");

    mappings.entrySet()
            .forEach(e -> configuration.addInputStream(IOUtils.toInputStream(e.getValue(), StandardCharsets.UTF_8)));

    StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
    serviceRegistryBuilder.applySetting(Environment.DATASOURCE, getDataSource());
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
    save(type, json, getDataSource());
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
   * Save.
   *
   * @param type the type
   * @param object the object
   */
  public void save(String type, Map object) {
    save(type, object, getDataSource());
  }

  /**
   * Save.
   *
   * @param type the type
   * @param object the object
   * @param datasource the datasource
   */
  public void save(String type, Map object, DataSource datasource) {
    try (Session session = sessionFactory.openSession()) {
      Transaction transaction = session.beginTransaction();
      session.save(type, object);
      transaction.commit();
    }
  }

  /**
   * Delete.
   *
   * @param type the type
   * @param id the id
   */
  public void delete(String type, Serializable id) {
    delete(type, id, getDataSource());
  }

  /**
   * Delete.
   *
   * @param type the type
   * @param id the id
   * @param datasource the datasource
   */
  public void delete(String type, Serializable id, DataSource datasource) {
    try (Session session = sessionFactory.openSession()) {
      Transaction transaction = session.beginTransaction();
      Object object = get(type, id);
      session.delete(type, object);
      transaction.commit();
    }
  }

  /**
   * Gets the.
   *
   * @param type the type
   * @param id the id
   * @return the map
   */
  public Map get(String type, Serializable id) {
    return get(type, id, getDataSource());
  }

  /**
   * Gets the.
   *
   * @param type the type
   * @param id the id
   * @param datasource the datasource
   * @return the map
   */
  public Map get(String type, Serializable id, DataSource datasource) {
    try (Session session = sessionFactory.openSession()) {
      return (Map) session.get(type, id);
    }
  }

  /**
   * Contains.
   *
   * @param type the type
   * @param json the json
   * @return true, if successful
   */
  public boolean contains(String type, String json) {
    return contains(type, json, getDataSource());
  }

  /**
   * Contains.
   *
   * @param type the type
   * @param json the json
   * @param datasource the datasource
   * @return true, if successful
   */
  public boolean contains(String type, String json, DataSource datasource) {
    try (Session session = sessionFactory.openSession()) {
      Map object = JsonHelper.fromJson(json, Map.class);
      return session.contains(type, object);
    }
  }

  /**
   * List.
   *
   * @param type the type
   * @return the list
   */
  public List<Map> list(String type) {
    return list(type, getDataSource());
  }

  /**
   * List.
   *
   * @param type the type
   * @param datasource the datasource
   * @return the list
   */
  public List<Map> list(String type, DataSource datasource) {
    try (Session session = sessionFactory.openSession()) {
      return session.createCriteria(type)
                    .list();
    }
  }

  /**
   * Criteria.
   *
   * @param type the type
   * @param restrictions the restrictions
   * @param aliases the aliases
   * @return the list
   */
  public List<Map> criteria(String type, Map<String, String> restrictions, Map<String, String> aliases) {
    return criteria(type, restrictions, aliases, getDataSource());
  }

  /**
   * Criteria.
   *
   * @param type the type
   * @param restrictions the restrictions
   * @param aliases the aliases
   * @param datasource the datasource
   * @return the list
   */
  public List<Map> criteria(String type, Map<String, String> restrictions, Map<String, String> aliases, DataSource datasource) {
    try (Session session = sessionFactory.openSession()) {
      Criteria criteria = session.createCriteria(type);
      if (aliases != null) {
        aliases.entrySet()
               .forEach(e -> criteria.createAlias(e.getKey(), e.getValue()));
      }
      if (restrictions != null) {
        restrictions.entrySet()
                    .forEach(e -> criteria.add(Restrictions.like(e.getKey(), e.getValue())));
      }
      return criteria.list();
    }
  }

  /**
   * Query.
   *
   * @param query the query
   * @return the list
   */
  public List<Map> query(String query) {
    return query(query, getDataSource());
  }

  /**
   * Query.
   *
   * @param query the query
   * @param datasource the datasource
   * @return the list
   */
  public List<Map> query(String query, DataSource datasource) {
    try (Session session = sessionFactory.openSession()) {
      return session.createNativeQuery(query)
                    .list();
    }
  }


  // /**
  // * List.
  // *
  // * @param type the type
  // * @return the string
  // */
  // public String list(String type) {
  // try (Session session = sessionFactory.openSession()) {
  // List list = session.createSQLQuery("SELECT * FROM " + type).list();
  // return JsonHelper.toJson(list);
  // }
  // }

}
