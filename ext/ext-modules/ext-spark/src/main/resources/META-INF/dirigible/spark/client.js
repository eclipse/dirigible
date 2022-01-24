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
exports.getSession = function(sparkUri) {
    var session = new Session();
    var native = org.eclipse.dirigible.api.spark.SparkFacade.getSession(sparkUri);
    session.native = native;
    return session;
};

exports.getDBTableDataset = function(sparkUri, dbName, user, pass, table) {
    var datasetRow = new DatasetRow
    var native = org.eclipse.dirigible.api.spark.SparkFacade.getDBTableDataset(sparkUri, dbName, user, pass, table);
    datasetRow.native = native;
    return datasetRow;
}

/**
 * Session object
 */
function Session() {
    this.readDefault = function(path) {
        var dataset = new DatasetRow();
        dataset.native = this.native.read().load(path);
        return dataset;
    }

    this.readFormat = function(path, format) {
        var dataset = new DatasetRow();
        dataset.native = this.native.read().format(format).load(path);
        return dataset;
    }
}

/**
 * DatasetRow object
 */
function DatasetRow() {
    this.getHeadRow = function () {
        var row = new Row();
        row.native = this.native.head();
        return row;
    }

    this.getRowAsString = function (rowNum) {
        var result = ""
        if (this.native) {
            result = this.native.collectAsList().get(rowNum).toString()
        } else {
            result = "Dataset is empty!"
            console.error(result)
        }
        return result
    }

    this.filterDataset = function(condition) {
        var dataset = new DatasetRow();
        if (/([\w]+[\s]?[\>?\<?\=?][\s]?[\w]+)/.test(condition)) {
            dataset.native = this.native.filter(condition)
        } else {
            console.error("Parameter not a valid boolean condition. Example: age > 34")
        }
        return dataset;
    }
}

/**
 * Row object
 */
function Row() {
    this.asJson = function() {
        var result = ""
        if (this.native) {
            result = this.native.prettyJson()
        } else {
            result = "Row is empty!"
        }
        return result
    }
}
