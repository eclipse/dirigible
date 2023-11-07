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
package org.eclipse.dirigible.components.extensions.synchronizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.util.List;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizersOrder;
import org.eclipse.dirigible.components.extensions.domain.ExtensionPoint;
import org.eclipse.dirigible.components.extensions.service.ExtensionPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class ExtensionPointsSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(SynchronizersOrder.EXTENSIONPOINT)
public class ExtensionPointsSynchronizer<A extends Artefact> implements Synchronizer<ExtensionPoint> {

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(ExtensionPointsSynchronizer.class);

  /** The Constant FILE_EXTENSION_EXTENSIONPOINT. */
  private static final String FILE_EXTENSION_EXTENSIONPOINT = ".extensionpoint";

  /** The extension point service. */
  private ExtensionPointService extensionPointService;

  /** The synchronization callback. */
  private SynchronizerCallback callback;

  /**
   * Instantiates a new extension points synchronizer.
   *
   * @param extensionPointService the extension point service
   */
  @Autowired
  public ExtensionPointsSynchronizer(ExtensionPointService extensionPointService) {
    this.extensionPointService = extensionPointService;
  }

  /**
   * Gets the service.
   *
   * @return the service
   */
  @Override
  public ArtefactService<ExtensionPoint> getService() {
    return extensionPointService;
  }

  /**
   * Checks if is accepted.
   *
   * @param file the file
   * @param attrs the attrs
   * @return true, if is accepted
   */
  @Override
  public boolean isAccepted(Path file, BasicFileAttributes attrs) {
    return file.toString()
               .endsWith(getFileExtension());
  }

  /**
   * Checks if is accepted.
   *
   * @param type the type
   * @return true, if is accepted
   */
  @Override
  public boolean isAccepted(String type) {
    return ExtensionPoint.ARTEFACT_TYPE.equals(type);
  }

  /**
   * Load.
   *
   * @param location the location
   * @param content the content
   * @return the list
   * @throws ParseException
   */
  @Override
  public List<ExtensionPoint> parse(String location, byte[] content) throws ParseException {
    ExtensionPoint extensionPoint = JsonHelper.fromJson(new String(content, StandardCharsets.UTF_8), ExtensionPoint.class);
    Configuration.configureObject(extensionPoint);
    extensionPoint.setLocation(location);
    extensionPoint.setType(ExtensionPoint.ARTEFACT_TYPE);
    extensionPoint.updateKey();
    try {
      ExtensionPoint maybe = getService().findByKey(extensionPoint.getKey());
      if (maybe != null) {
        extensionPoint.setId(maybe.getId());
      }
      extensionPoint = getService().save(extensionPoint);
    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error(e.getMessage(), e);
      }
      if (logger.isErrorEnabled()) {
        logger.error("extension point: {}", extensionPoint);
      }
      if (logger.isErrorEnabled()) {
        logger.error("content: {}", new String(content));
      }
      throw new ParseException(e.getMessage(), 0);
    }
    return List.of(extensionPoint);
  }

  /**
   * Retrieve.
   *
   * @param location the location
   * @return the list
   */
  @Override
  public List<ExtensionPoint> retrieve(String location) {
    return getService().getAll();
  }

  /**
   * Sets the status.
   *
   * @param artefact the artefact
   * @param lifecycle the lifecycle
   * @param error the error
   */
  @Override
  public void setStatus(Artefact artefact, ArtefactLifecycle lifecycle, String error) {
    artefact.setLifecycle(lifecycle);
    artefact.setError(error);
    getService().save((ExtensionPoint) artefact);
  }

  /**
   * Complete.
   *
   * @param wrapper the wrapper
   * @param flow the flow
   * @return true, if successful
   */
  @Override
  public boolean complete(TopologyWrapper<Artefact> wrapper, ArtefactPhase flow) {
    ExtensionPoint extensionPoint = null;
    if (wrapper.getArtefact() instanceof ExtensionPoint) {
      extensionPoint = (ExtensionPoint) wrapper.getArtefact();
    } else {
      throw new UnsupportedOperationException(String.format("Trying to process %s as Extension Point", wrapper.getArtefact()
                                                                                                              .getClass()));
    }

    switch (flow) {
      case CREATE:
        if (ArtefactLifecycle.NEW.equals(extensionPoint.getLifecycle())) {
          callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
        }
        break;
      case UPDATE:
        if (ArtefactLifecycle.MODIFIED.equals(extensionPoint.getLifecycle())) {
          callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED, "");
        }
        break;
      case DELETE:
        if (ArtefactLifecycle.CREATED.equals(extensionPoint.getLifecycle())
            || ArtefactLifecycle.UPDATED.equals(extensionPoint.getLifecycle())) {
          try {
            getService().delete(extensionPoint);
            callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, "");
          } catch (Exception e) {
            if (logger.isErrorEnabled()) {
              logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, e.getMessage());
          }
        }
        break;
      case START:
      case STOP:
    }
    return true;
  }

  /**
   * Cleanup.
   *
   * @param extensionPoint the extension point
   */
  @Override
  public void cleanup(ExtensionPoint extensionPoint) {
    try {
      getService().delete(extensionPoint);
    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error(e.getMessage(), e);
      }
      callback.addError(e.getMessage());
      callback.registerState(this, extensionPoint, ArtefactLifecycle.DELETED, e.getMessage());
    }
  }

  /**
   * Sets the callback.
   *
   * @param callback the new callback
   */
  @Override
  public void setCallback(SynchronizerCallback callback) {
    this.callback = callback;
  }

  /**
   * Gets the file extension.
   *
   * @return the file extension
   */
  @Override
  public String getFileExtension() {
    return FILE_EXTENSION_EXTENSIONPOINT;
  }

  /**
   * Gets the artefact type.
   *
   * @return the artefact type
   */
  @Override
  public String getArtefactType() {
    return ExtensionPoint.ARTEFACT_TYPE;
  }

}
