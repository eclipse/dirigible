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
import * as UI from '../ui/ui.js';

/**
 * @unrestricted
 */
export class PieChart {
  /**
   * @param {!PieChartOptions} options
   */
  constructor(options) {
    const {size, formatter, showLegend, chartName} = options;
    /** @type {!Map<!Element, !Element>} */
    this._sliceToLegendItem = new Map();
    this.element = createElement('div');
    this._shadowRoot = UI.Utils.createShadowRootWithCoreStyles(this.element, 'perf_ui/pieChart.css');
    const root = this._shadowRoot.createChild('div', 'root');
    UI.ARIAUtils.markAsGroup(root);
    UI.ARIAUtils.setAccessibleName(root, chartName);
    this._chartRoot = root.createChild('div', 'chart-root');
    const svg = this._createSVGChild(this._chartRoot, 'svg');
    this._group = this._createSVGChild(svg, 'g');
    this._innerR = 0.618;
    const strokeWidth = 1 / size;
    let circle = this._createSVGChild(this._group, 'circle');
    circle.setAttribute('r', 1);
    circle.setAttribute('stroke', 'hsl(0, 0%, 80%)');
    circle.setAttribute('fill', 'transparent');
    circle.setAttribute('stroke-width', strokeWidth);
    circle = this._createSVGChild(this._group, 'circle');
    circle.setAttribute('r', this._innerR);
    circle.setAttribute('stroke', 'hsl(0, 0%, 80%)');
    circle.setAttribute('fill', 'transparent');
    circle.setAttribute('stroke-width', strokeWidth);
    this._foregroundElement = this._chartRoot.createChild('div', 'pie-chart-foreground');
    this._totalElement = this._foregroundElement.createChild('div', 'pie-chart-total');
    this._totalElementClickHandler = this._focusClickedElement.bind(this, this._totalElement);
    this._totalElement.addEventListener('click', this._totalElementClickHandler);
    this._formatter = formatter;
    this._slices = [];
    this._lastAngle = -Math.PI / 2;
    if (showLegend) {
      this._legend = root.createChild('div', 'pie-chart-legend');
    }
    this._setSize(size);
    root.addEventListener('keydown', this._onKeyDown.bind(this));
  }

  /**
   * @param {number} totalValue
   */
  initializeWithTotal(totalValue) {
    for (let i = 0; i < this._slices.length; ++i) {
      this._slices[i].remove();
    }
    this._slices = [];
    this._totalValue = totalValue;
    this._lastAngle = -Math.PI / 2;
    let totalString;
    if (totalValue) {
      totalString = this._formatter ? this._formatter(totalValue) : totalValue;
    } else {
      totalString = '';
    }
    this._totalElement.textContent = totalString;
    const name = ls`Total`;
    if (this._legend) {
      this._legend.removeChildren();
      this._sliceToLegendItem.clear();
      const legendItem = this._addLegendItem(totalValue, this._totalElementClickHandler, name);
      this._sliceToLegendItem.set(this._totalElement, legendItem);
    }
    this._setSelectedElement(this._totalElement);
    UI.ARIAUtils.setAccessibleName(this._totalElement, name);
  }

  /**
   * @param {number} value
   */
  _setSize(value) {
    this._group.setAttribute('transform', 'scale(' + (value / 2) + ') translate(1, 1) scale(0.99, 0.99)');
    const size = value + 'px';
    this._chartRoot.style.width = size;
    this._chartRoot.style.height = size;
  }

  /**
   * @param {number} value
   * @param {string} color
   * @param {string=} name
   */
  addSlice(value, color, name) {
    let sliceAngle = value / this._totalValue * 2 * Math.PI;
    if (!isFinite(sliceAngle)) {
      return;
    }
    sliceAngle = Math.min(sliceAngle, 2 * Math.PI * 0.9999);
    const path = this._createSVGChild(this._group, 'path');
    path.classList.add('slice');
    path.tabIndex = -1;
    const x1 = Math.cos(this._lastAngle);
    const y1 = Math.sin(this._lastAngle);
    this._lastAngle += sliceAngle;
    const x2 = Math.cos(this._lastAngle);
    const y2 = Math.sin(this._lastAngle);
    const r2 = this._innerR;
    const x3 = x2 * r2;
    const y3 = y2 * r2;
    const x4 = x1 * r2;
    const y4 = y1 * r2;
    const largeArc = sliceAngle > Math.PI ? 1 : 0;
    path.setAttribute('d',
        `M${x1},${y1} A1,1,0,${largeArc},1,${x2},${y2} L${x3},${y3} A${r2},${r2},0,${largeArc},0,${x4},${y4} Z`);
    path.setAttribute('fill', color);
    this._slices.push(path);
    const clickHandler = this._focusClickedElement.bind(this, path);
    path.addEventListener('click', clickHandler);
    if (this._legend) {
      const legendItem = this._addLegendItem(value, clickHandler, name, color);
      this._sliceToLegendItem.set(path, legendItem);
    }
    if (name) {
      UI.ARIAUtils.setAccessibleName(path, name);
    }
  }

  /**
   * @param {!Element} parent
   * @param {string} childType
   * @return {!Element}
   */
  _createSVGChild(parent, childType) {
    const child = parent.ownerDocument.createElementNS('http://www.w3.org/2000/svg', childType);
    parent.appendChild(child);
    return child;
  }

  /**
   * @param {number} value
   * @param { function() } clickHandler
   * @param {string=} name
   * @param {string=} color
   * @returns {!Element}
   */
  _addLegendItem(value, clickHandler, name, color) {
    const node = this._legend.ownerDocument.createElement('div');
    node.addEventListener('click', clickHandler);
    node.className = 'pie-chart-legend-row';
    // make sure total always appears at the bottom
    if (this._legend.childElementCount) {
      this._legend.insertBefore(node, this._legend.lastElementChild);
      node.tabIndex = -1;
    } else {
      node.tabIndex = 0;
      this._legend.appendChild(node);
    }
    const sizeDiv = node.createChild('div', 'pie-chart-size');
    const swatchDiv = node.createChild('div', 'pie-chart-swatch');
    const nameDiv = node.createChild('div', 'pie-chart-name');
    if (color) {
      swatchDiv.style.backgroundColor = color;
    } else {
      swatchDiv.classList.add('pie-chart-empty-swatch');
    }
    nameDiv.textContent = name;
    const size = this._formatter ? this._formatter(value) : value;
    sizeDiv.textContent = size;
    return node;
  }

  /**
   * @param {!Event} event
   */
  _onKeyDown(event) {
    let handled = false;
    if (event.key === 'ArrowDown') {
      this._focusNextElement();
      handled = true;
    } else if (event.key === 'ArrowUp') {
      this._focusPreviousElement();
      handled = true;
    }

    if (handled) {
      event.consume(true);
    }
  }

  /**
   * @param {!Element} element
   */
  _focusClickedElement(element) {
    this._setSelectedElement(element);
  }

  /**
   * @param {!Element} element
   */
  _setSelectedElement(element) {
    if (this._activeElement) {
      if (!this._legend) {
        this._activeElement.tabIndex = -1;
      }
      this._activeElement.classList.remove('selected');
      const legendElement = this._sliceToLegendItem.get(this._activeElement);
      if (legendElement) {
        legendElement.classList.remove('selected');
        legendElement.tabIndex = -1;
      }
    }
    this._activeElement = element;
    if (!this._legend) {
      this._activeElement.tabIndex = 1;
    }
    this._activeElement.classList.add('selected');
    const legendElement = this._sliceToLegendItem.get(this._activeElement);
    if (legendElement) {
      legendElement.classList.add('selected');
      legendElement.tabIndex = 0;
      legendElement.focus();
    }
    // make the active slice the last one so it draws on top
    if (this._activeElement.classList.contains('slice')) {
      this._group.appendChild(this._activeElement);
    }
  }

  _focusNextElement() {
    let nextElement = null;
    const lastSliceIndex = this._slices.length - 1;
    const currentSliceIndex = this._slices.indexOf(this._activeElement);
    if (currentSliceIndex === lastSliceIndex) {
      nextElement = this._totalElement;
    } else if (currentSliceIndex >= 0) {
      nextElement = this._slices[currentSliceIndex + 1];
    } else if (this._activeElement === this._totalElement) {
      nextElement = this._slices[0];
    }

    if (nextElement) {
      this._setSelectedElement(nextElement);
      if (this._legend) {
        this._sliceToLegendItem.get(nextElement).focus();
      } else {
        nextElement.focus();
      }
    }
  }

  _focusPreviousElement() {
    let previousElement = null;
    const lastSliceIndex = this._slices.length - 1;
    const currentSliceIndex = this._slices.indexOf(this._activeElement);
    if (this._activeElement === this._totalElement) {
      previousElement = this._slices[lastSliceIndex];
    } else if (currentSliceIndex > 0) {
      previousElement = this._slices[currentSliceIndex - 1];
    } else if (currentSliceIndex === 0) {
      previousElement = this._totalElement;
    }

    if (previousElement) {
      this._setSelectedElement(previousElement);
      if (this._legend) {
        this._sliceToLegendItem.get(previousElement).focus();
      } else {
        previousElement.focus();
      }
    }
  }
}

/** @typedef {{size: number, formatter: function(number):string, showLegend: (boolean|undefined), chartName: string}} */
export let PieChartOptions;
