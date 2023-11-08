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
package org.eclipse.dirigible.components.initializers.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationJob;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * The Class SystemScheduler.
 */
@Configuration
public class SystemScheduler {

    /** The Constant DIRIGIBLE_SYNCHRONIZER_FREQUENCY. */
    private static final String DIRIGIBLE_SYNCHRONIZER_FREQUENCY = "DIRIGIBLE_SYNCHRONIZER_FREQUENCY";

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SystemScheduler.class);

    /** The application context. */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Inits the.
     */
    @PostConstruct
    public void init() {
        logger.info("System Scheduler...");
    }

    /**
     * Spring bean job factory.
     *
     * @return the spring bean job factory
     */
    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        logger.debug("Configuring Job factory");

        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * Scheduler.
     *
     * @param trigger the trigger
     * @param job the job
     * @param factory the factory
     * @return the scheduler
     * @throws SchedulerException the scheduler exception
     */
    @Bean
    public Scheduler scheduler(Trigger trigger, JobDetail job, SchedulerFactoryBean factory) throws SchedulerException {
        logger.debug("Getting a handle to the Scheduler");
        Scheduler scheduler = factory.getScheduler();
        scheduler.scheduleJob(job, trigger);

        logger.debug("Starting Scheduler threads");
        scheduler.start();
        return scheduler;
    }

    /**
     * Scheduler factory bean.
     *
     * @return the scheduler factory bean
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(springBeanJobFactory());
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    /**
     * Quartz properties.
     *
     * @return the properties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    /**
     * Job detail.
     *
     * @return the job detail
     */
    @Bean
    public JobDetail jobDetail() {

        return newJob().ofType(SynchronizationJob.class)
                       .storeDurably()
                       .withIdentity(JobKey.jobKey("SynchronizationJobDetail"))
                       .withDescription("Invoke Synchronization Job service...")
                       .build();
    }

    /**
     * Trigger.
     *
     * @param job the job
     * @return the trigger
     */
    @Bean
    public Trigger trigger(JobDetail job) {

        String frequency = org.eclipse.dirigible.commons.config.Configuration.get(DIRIGIBLE_SYNCHRONIZER_FREQUENCY, "10");
        int frequencyInSec = Integer.parseInt(frequency);
        logger.info("Configuring trigger to fire every {} seconds", frequencyInSec);

        return newTrigger().forJob(job)
                           .withIdentity(TriggerKey.triggerKey("SynchronizationJobTrigger"))
                           .withDescription("Synchronization trigger")
                           .withSchedule(simpleSchedule().withIntervalInSeconds(frequencyInSec)
                                                         .repeatForever())
                           .build();
    }

}
