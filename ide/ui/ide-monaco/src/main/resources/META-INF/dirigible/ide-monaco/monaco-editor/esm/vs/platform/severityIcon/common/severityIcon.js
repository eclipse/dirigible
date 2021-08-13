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
import Severity from '../../../base/common/severity.js';
import { registerThemingParticipant } from '../../theme/common/themeService.js';
import { problemsErrorIconForeground, problemsInfoIconForeground, problemsWarningIconForeground } from '../../theme/common/colorRegistry.js';
export var SeverityIcon;
(function (SeverityIcon) {
    function className(severity) {
        switch (severity) {
            case Severity.Ignore:
                return 'severity-ignore codicon-info';
            case Severity.Info:
                return 'codicon-info';
            case Severity.Warning:
                return 'codicon-warning';
            case Severity.Error:
                return 'codicon-error';
        }
        return '';
    }
    SeverityIcon.className = className;
})(SeverityIcon || (SeverityIcon = {}));
registerThemingParticipant(function (theme, collector) {
    var errorIconForeground = theme.getColor(problemsErrorIconForeground);
    if (errorIconForeground) {
        collector.addRule("\n\t\t\t.monaco-editor .zone-widget .codicon-error,\n\t\t\t.markers-panel .marker-icon.codicon-error,\n\t\t\t.extensions-viewlet > .extensions .codicon-error,\n\t\t\t.monaco-dialog-box .dialog-message-row .codicon-error {\n\t\t\t\tcolor: " + errorIconForeground + ";\n\t\t\t}\n\t\t");
    }
    var warningIconForeground = theme.getColor(problemsWarningIconForeground);
    if (errorIconForeground) {
        collector.addRule("\n\t\t\t.monaco-editor .zone-widget .codicon-warning,\n\t\t\t.markers-panel .marker-icon.codicon-warning,\n\t\t\t.extensions-viewlet > .extensions .codicon-warning,\n\t\t\t.extension-editor .codicon-warning,\n\t\t\t.monaco-dialog-box .dialog-message-row .codicon-warning {\n\t\t\t\tcolor: " + warningIconForeground + ";\n\t\t\t}\n\t\t");
    }
    var infoIconForeground = theme.getColor(problemsInfoIconForeground);
    if (errorIconForeground) {
        collector.addRule("\n\t\t\t.monaco-editor .zone-widget .codicon-info,\n\t\t\t.markers-panel .marker-icon.codicon-info,\n\t\t\t.extensions-viewlet > .extensions .codicon-info,\n\t\t\t.extension-editor .codicon-info,\n\t\t\t.monaco-dialog-box .dialog-message-row .codicon-info {\n\t\t\t\tcolor: " + infoIconForeground + ";\n\t\t\t}\n\t\t");
    }
});
