/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.problems.model.response;

import org.eclipse.dirigible.core.problems.model.ProblemsModel;

import java.util.List;

public class ResponseModel {

    private List<ProblemsModel> result;
    private int selectedRows;
    private int totalRows;

    public ResponseModel() {
    }

    public ResponseModel(List<ProblemsModel> result, int selectedRows, int totalRows) {
        this.result = result;
        this.selectedRows = selectedRows;
        this.totalRows = totalRows;
    }

    public List<ProblemsModel> getResult() {
        return result;
    }

    public void setResult(List<ProblemsModel> result) {
        this.result = result;
    }

    public int getSelectedRows() {
        return selectedRows;
    }

    public void setSelectedRows(int selectedRows) {
        this.selectedRows = selectedRows;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }
}
