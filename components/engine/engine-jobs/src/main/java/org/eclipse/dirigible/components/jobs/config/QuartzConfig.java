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
package org.eclipse.dirigible.components.jobs.config;

import org.eclipse.dirigible.components.data.sources.config.SystemDataSourceName;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * The Class SystemScheduler.
 */
@Configuration
class QuartzConfig {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);

    /**
     * Scheduler.
     *
     * @param factory the factory
     * @param systemDataSource the system data source
     * @param systemDataSourceName the system data source name
     * @return the scheduler
     * @throws SchedulerException the scheduler exception
     */
    @Bean
    Scheduler scheduler(SchedulerFactoryBean factory, @Qualifier("SystemDB") DataSource systemDataSource,
            @SystemDataSourceName String systemDataSourceName) throws SchedulerException {
        factory.setDataSource(systemDataSource);
        DBConnectionManager.getInstance()
                           .addConnectionProvider(systemDataSourceName, new CustomConnectionProvider(systemDataSource));

        Scheduler scheduler = factory.getScheduler();
        logger.debug("Starting Scheduler threads");

        // give some time for spring auto configurations to pass before starting
        scheduler.startDelayed(10);

        return scheduler;
    }

    /**
     * Scheduler factory bean.
     *
     * @param jobFactory the job factory
     * @return the scheduler factory bean
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Bean
    SchedulerFactoryBean schedulerFactoryBean(AutoWiringSpringBeanJobFactory jobFactory) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    /**
     * Quartz properties.
     *
     * @return the properties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}
