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
/** Client API for  Apache Cassandra */
exports.getSession = function (host, port) {
    var session = new Session();
    var native = org.eclipse.dirigible.api.cassandra.CassandraFacade.connect(host, port)
    session.native = native;
    return session;
};
exports.getDBResults = function ( keySpaceName, query) {
    var resultSet = new ResultSet();
    var native = org.eclipse.dirigible.api.cassandra.CassandraFacade.getResultSet(keySpaceName,query);
    resultSet.native = native;
    return resultSet;
}


/**
 * Session object
 */
function Session() {

    this.executeQuery = function (query) {
        return this.native.execute(query)
    }

    this.getLoggedKeyspaceName = function () {

        return this.native.getLoggedKeyspace();
    }

    this.closeSession = function () {
        return this.native.close();
    }
    this.getDBResult = function (keySpaceName, query) {
        var resultSet = new ResultSet();
        var native = org.eclipse.dirigible.api.cassandra.CassandraFacade.getResultSet(keySpaceName,query);
        resultSet.native = native;
        return resultSet;
    };
}

/**
 * ResultSet object
 */
function ResultSet() {
    this.getRowAsString = function () {
        var result = "";
        if (this) {
            result = this.native.all().toString();
        } else {
            result = "Result Set is empty"
            console.log(result)
        }
        return result;
    }
}

/**
 * Row object
 */
function Row() {
    this.asJson = function () {
        var result = ""
        if (this.native) {
            result = this.native.prettyJson()
        } else {
            result = "Row is empty!"
        }
        return result
    }


}