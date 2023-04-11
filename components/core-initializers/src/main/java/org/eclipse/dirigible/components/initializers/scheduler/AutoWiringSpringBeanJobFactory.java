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
package org.eclipse.dirigible.components.initializers.scheduler;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * A factory for creating AutoWiringSpringBeanJob objects.
 */
public class AutoWiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
	
	/** The bean factory. */
	private transient AutowireCapableBeanFactory beanFactory;

    /**
     * Sets the application context.
     *
     * @param applicationContext the new application context
     * @throws BeansException the beans exception
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    /**
     * Creates a new AutoWiringSpringBeanJob object.
     *
     * @param bundle the bundle
     * @return the object
     * @throws Exception the exception
     */
    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }

}
