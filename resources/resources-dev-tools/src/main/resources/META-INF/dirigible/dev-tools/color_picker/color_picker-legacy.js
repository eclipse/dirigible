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
// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as ColorPickerModule from './color_picker.js';

self.ColorPicker = self.ColorPicker || {};
ColorPicker = ColorPicker || {};

/** @constructor */
ColorPicker.ContrastDetails = ColorPickerModule.ContrastDetails.ContrastDetails;

ColorPicker.ContrastDetails.Events = ColorPickerModule.ContrastDetails.Events;

/** @constructor */
ColorPicker.ContrastDetails.Swatch = ColorPickerModule.ContrastDetails.Swatch;

/** @constructor */
ColorPicker.ContrastInfo = ColorPickerModule.ContrastInfo.ContrastInfo;

ColorPicker.ContrastInfo.Events = ColorPickerModule.ContrastInfo.Events;

/** @constructor */
ColorPicker.ContrastOverlay = ColorPickerModule.ContrastOverlay.ContrastOverlay;

/** @constructor */
ColorPicker.ContrastRatioLineBuilder = ColorPickerModule.ContrastOverlay.ContrastRatioLineBuilder;

/** @constructor */
ColorPicker.Spectrum = ColorPickerModule.Spectrum.Spectrum;

/** @constructor */
ColorPicker.Spectrum.PaletteGenerator = ColorPickerModule.Spectrum.PaletteGenerator;

/** @constructor */
ColorPicker.Spectrum.Swatch = ColorPickerModule.Spectrum.Swatch;

ColorPicker.Spectrum.Events = ColorPickerModule.Spectrum.Events;

ColorPicker.Spectrum._ChangeSource = ColorPickerModule.Spectrum.ChangeSource;
