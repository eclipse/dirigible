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
package org.eclipse.dirigible.components.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanProvider implements InitializingBean {

  private static SpringBeanProvider INSTANCE;
  private final ApplicationContext applicationContext;

  @Autowired
  public SpringBeanProvider(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void afterPropertiesSet() {
    INSTANCE = this;
  }

  public static <T> T getBean(Class<T> clazz) {
    return INSTANCE.applicationContext.getBean(clazz);
  }

  public static <T> T getBean(String className) {
    return (T) INSTANCE.applicationContext.getBean(className);
  }
}
