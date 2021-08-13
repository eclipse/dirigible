/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
import * as Common from '../common/common.js';
import * as LayerViewer from '../layer_viewer/layer_viewer.js';
import * as SDK from '../sdk/sdk.js';  // eslint-disable-line no-unused-vars
import * as UI from '../ui/ui.js';

import {LayerPaintProfilerView} from './LayerPaintProfilerView.js';
import {Events, LayerTreeModel} from './LayerTreeModel.js';

/**
 * @implements {SDK.SDKModel.Observer}
 * @unrestricted
 */
export class LayersPanel extends UI.Panel.PanelWithSidebar {
  constructor() {
    super('layers', 225);

    /** @type {?LayerTreeModel} */
    this._model = null;

    SDK.SDKModel.TargetManager.instance().observeTargets(this);
    this._layerViewHost = new LayerViewer.LayerViewHost.LayerViewHost();
    this._layerTreeOutline = new LayerViewer.LayerTreeOutline.LayerTreeOutline(this._layerViewHost);
    this._layerTreeOutline.addEventListener(
        LayerViewer.LayerTreeOutline.Events.PaintProfilerRequested, this._onPaintProfileRequested, this);
    this.panelSidebarElement().appendChild(this._layerTreeOutline.element);
    this.setDefaultFocusedElement(this._layerTreeOutline.element);

    this._rightSplitWidget = new UI.SplitWidget.SplitWidget(false, true, 'layerDetailsSplitViewState');
    this.splitWidget().setMainWidget(this._rightSplitWidget);

    this._layers3DView = new LayerViewer.Layers3DView.Layers3DView(this._layerViewHost);
    this._rightSplitWidget.setMainWidget(this._layers3DView);
    this._layers3DView.addEventListener(
        LayerViewer.Layers3DView.Events.PaintProfilerRequested, this._onPaintProfileRequested, this);
    this._layers3DView.addEventListener(LayerViewer.Layers3DView.Events.ScaleChanged, this._onScaleChanged, this);

    this._tabbedPane = new UI.TabbedPane.TabbedPane();
    this._rightSplitWidget.setSidebarWidget(this._tabbedPane);

    this._layerDetailsView = new LayerViewer.LayerDetailsView.LayerDetailsView(this._layerViewHost);
    this._layerDetailsView.addEventListener(
        LayerViewer.LayerDetailsView.Events.PaintProfilerRequested, this._onPaintProfileRequested, this);
    this._tabbedPane.appendTab(DetailsViewTabs.Details, Common.UIString.UIString('Details'), this._layerDetailsView);

    this._paintProfilerView = new LayerPaintProfilerView(this._showImage.bind(this));
    this._tabbedPane.addEventListener(UI.TabbedPane.Events.TabClosed, this._onTabClosed, this);
    this._updateThrottler = new Common.Throttler.Throttler(100);
  }

  /**
   * @override
   */
  focus() {
    this._layerTreeOutline.focus();
  }

  /**
   * @override
   */
  wasShown() {
    super.wasShown();
    if (this._model) {
      this._model.enable();
    }
  }

  /**
   * @override
   */
  willHide() {
    if (this._model) {
      this._model.disable();
    }
    super.willHide();
  }

  /**
   * @override
   * @param {!SDK.SDKModel.Target} target
   */
  targetAdded(target) {
    if (this._model) {
      return;
    }
    this._model = target.model(LayerTreeModel);
    if (!this._model) {
      return;
    }
    this._model.addEventListener(Events.LayerTreeChanged, this._onLayerTreeUpdated, this);
    this._model.addEventListener(Events.LayerPainted, this._onLayerPainted, this);
    if (this.isShowing()) {
      this._model.enable();
    }
  }

  /**
   * @override
   * @param {!SDK.SDKModel.Target} target
   */
  targetRemoved(target) {
    if (!this._model || this._model.target() !== target) {
      return;
    }
    this._model.removeEventListener(Events.LayerTreeChanged, this._onLayerTreeUpdated, this);
    this._model.removeEventListener(Events.LayerPainted, this._onLayerPainted, this);
    this._model.disable();
    this._model = null;
  }

  _onLayerTreeUpdated() {
    this._updateThrottler.schedule(this._update.bind(this));
  }

  /**
   * @return {!Promise<*>}
   */
  _update() {
    if (this._model) {
      this._layerViewHost.setLayerTree(this._model.layerTree());
    }
    return Promise.resolve();
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onLayerPainted(event) {
    if (!this._model) {
      return;
    }
    const layer = /** @type {!SDK.LayerTreeBase.Layer} */ (event.data);
    if (this._layerViewHost.selection() && this._layerViewHost.selection().layer() === layer) {
      this._layerDetailsView.update();
    }
    this._layers3DView.updateLayerSnapshot(layer);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onPaintProfileRequested(event) {
    const selection = /** @type {!LayerViewer.LayerViewHost.Selection} */ (event.data);
    this._layers3DView.snapshotForSelection(selection).then(snapshotWithRect => {
      if (!snapshotWithRect) {
        return;
      }
      this._layerBeingProfiled = selection.layer();
      if (!this._tabbedPane.hasTab(DetailsViewTabs.Profiler)) {
        this._tabbedPane.appendTab(
            DetailsViewTabs.Profiler, Common.UIString.UIString('Profiler'), this._paintProfilerView, undefined, true,
            true);
      }
      this._tabbedPane.selectTab(DetailsViewTabs.Profiler);
      this._paintProfilerView.profile(snapshotWithRect.snapshot);
    });
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onTabClosed(event) {
    if (event.data.tabId !== DetailsViewTabs.Profiler || !this._layerBeingProfiled) {
      return;
    }
    this._paintProfilerView.reset();
    this._layers3DView.showImageForLayer(this._layerBeingProfiled, undefined);
    this._layerBeingProfiled = null;
  }

  /**
   * @param {string=} imageURL
   */
  _showImage(imageURL) {
    this._layers3DView.showImageForLayer(this._layerBeingProfiled, imageURL);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onScaleChanged(event) {
    this._paintProfilerView.setScale(/** @type {number} */ (event.data));
  }
}

export const DetailsViewTabs = {
  Details: 'details',
  Profiler: 'profiler'
};
