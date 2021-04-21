/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/** Client API for Apache Spark */

exports.getSession = function() {
    var session = new Session();
    var native = org.eclipse.dirigible.api.spark.SparkFacade.getSession();
    session.native = native;
    return session;
};

exports.getHead = function(dataset) {
    return org.eclipse.dirigible.api.spark.SparkFacade.getHead(dataset);
};

exports.getDBTableDataset = function(dbName, user, pass, table) {
    var datasetRow = new DatasetRow
    var native = org.eclipse.dirigible.api.spark.SparkFacade.getDBTableDataset(dbName, user, pass, table);
    datasetRow.native = native;
    return datasetRow;
}

/**
 * Session object
 */
function Session() {
    this.readDataset = function(file) {
        var dataset = new Dataset();
        dataset.native = this.native.read().textFile(file).cache();
        return dataset;
    }
}

function Dataset() {
    this.getHead = function () {
        return this.native.head();
    }

    this.getUniqueEntries = function () {
        return this.native.distinct();
    }
}

function DatasetRow() {

}
