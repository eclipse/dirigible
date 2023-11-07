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
package org.eclipse.dirigible.components.listeners.synchronizer;

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
import org.eclipse.dirigible.components.base.artefact.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizersOrder;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.components.listeners.service.ListenerService;
import org.eclipse.dirigible.components.listeners.service.ListenersManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class ListenerSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(SynchronizersOrder.LISTENER)
public class ListenerSynchronizer<A extends Artefact> implements Synchronizer<Listener> {

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(ListenerSynchronizer.class);

  /** The Constant FILE_EXTENSION_LISTENER. */
  private static final String FILE_EXTENSION_LISTENER = ".listener";

  /** The callback. */
  private SynchronizerCallback callback;

  /** The listener service. */
  @Autowired
  private ListenerService listenerService;

  /** The Scheduler manager. */
  @Autowired
  private ListenersManager listenersManager;

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
               .endsWith(FILE_EXTENSION_LISTENER);
  }

  /**
   * Checks if is accepted.
   *
   * @param type the type
   * @return true, if is accepted
   */
  @Override
  public boolean isAccepted(String type) {
    return Listener.ARTEFACT_TYPE.equals(type);
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
  public List<Listener> parse(String location, byte[] content) throws ParseException {
    Listener listener = JsonHelper.fromJson(new String(content, StandardCharsets.UTF_8), Listener.class);
    Configuration.configureObject(listener);
    listener.setLocation(location);
    listener.setType(Listener.ARTEFACT_TYPE);
    listener.updateKey();
    try {
      Listener maybe = getService().findByKey(listener.getKey());
      if (maybe != null) {
        listener.setId(maybe.getId());
      }
      listener = getService().save(listener);
    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error(e.getMessage(), e);
      }
      if (logger.isErrorEnabled()) {
        logger.error("listener: {}", listener);
      }
      if (logger.isErrorEnabled()) {
        logger.error("content: {}", new String(content));
      }
      throw new ParseException(e.getMessage(), 0);
    }
    return List.of(listener);
  }

  /**
   * Retrieve.
   *
   * @param location the location
   * @return the list
   */
  @Override
  public List<Listener> retrieve(String location) {
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
    getService().save((Listener) artefact);
  }

  /**
   * Gets the service.
   *
   * @return the service
   */
  @Override
  public ArtefactService<Listener> getService() {
    return listenerService;
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
    Listener listener = null;
    if (wrapper.getArtefact() instanceof Listener) {
      listener = (Listener) wrapper.getArtefact();
    } else {
      throw new UnsupportedOperationException(String.format("Trying to process %s as Listener", wrapper.getArtefact()
                                                                                                       .getClass()));
    }

    switch (flow) {
      case CREATE:
        if (ArtefactLifecycle.NEW.equals(listener.getLifecycle())) {
          try {
            listenersManager.startListener(listener);
            listener.setRunning(true);
            getService().save(listener);
            callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
          } catch (Exception e) {
            if (logger.isErrorEnabled()) {
              logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, e.getMessage());
          }
        }
        break;
      case UPDATE:
        if (ArtefactLifecycle.MODIFIED.equals(listener.getLifecycle())) {
          try {
            listenersManager.stopListener(listener);
            listener.setRunning(false);
            getService().save(listener);
            listenersManager.startListener(listener);
            listener.setRunning(true);
            getService().save(listener);
            callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED, "");
          } catch (Exception e) {
            if (logger.isErrorEnabled()) {
              logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, e.getMessage());
          }
        }
        break;
      case DELETE:
        if (ArtefactLifecycle.CREATED.equals(listener.getLifecycle()) || ArtefactLifecycle.UPDATED.equals(listener.getLifecycle())) {
          try {
            listenersManager.stopListener(listener);
            listener.setRunning(false);
            getService().delete(listener);
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
        if (listener.getRunning() == null || !listener.getRunning()) {
          try {
            listenersManager.startListener(listener);
            listener.setRunning(true);
            getService().save(listener);
          } catch (Exception e) {
            if (logger.isErrorEnabled()) {
              logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
          }
        }
        break;
      case STOP:
        if (listener.getRunning()) {
          try {
            listenersManager.stopListener(listener);
            listener.setRunning(false);
            getService().save(listener);
          } catch (Exception e) {
            if (logger.isErrorEnabled()) {
              logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
          }
        }
        break;
    }

    return true;
  }

  /**
   * Cleanup.
   *
   * @param listener the listener
   */
  @Override
  public void cleanup(Listener listener) {
    try {
      listenersManager.stopListener(listener);
      getService().delete(listener);
    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error(e.getMessage(), e);
      }
      callback.addError(e.getMessage());
      callback.registerState(this, listener, ArtefactLifecycle.DELETED, e.getMessage());
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
    return FILE_EXTENSION_LISTENER;
  }

  /**
   * Gets the artefact type.
   *
   * @return the artefact type
   */
  @Override
  public String getArtefactType() {
    return Listener.ARTEFACT_TYPE;
  }
}
