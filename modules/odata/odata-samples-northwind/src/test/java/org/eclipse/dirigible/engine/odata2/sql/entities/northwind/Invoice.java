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
package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import java.util.Date;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "Invoice")
@EdmEntitySet(name = "Invoices", container = "NorthwindEntities")
public class Invoice {

    @EdmProperty(facets = @EdmFacets(maxLength = 40))
    private String shipName;

    @EdmProperty(facets = @EdmFacets(maxLength = 60))
    private String shipAddress;

    @EdmProperty(facets = @EdmFacets(maxLength = 15))
    private String shipCity;

    @EdmProperty(facets = @EdmFacets(maxLength = 15))
    private String shipRegion;

    @EdmProperty(facets = @EdmFacets(maxLength = 10))
    private String shipPostalCode;

    @EdmProperty(facets = @EdmFacets(maxLength = 15))
    private String shipCountry;

    @EdmProperty(name = "CustomerID", facets = @EdmFacets(maxLength = 5))
    private String customerId;

    @EdmKey
    @EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
    private String customerName;

    @EdmProperty(facets = @EdmFacets(maxLength = 60))
    private String address;

    @EdmProperty(facets = @EdmFacets(maxLength = 15))
    private String city;

    @EdmProperty(facets = @EdmFacets(maxLength = 15))
    private String region;

    @EdmProperty(facets = @EdmFacets(maxLength = 10))
    private String postalCode;

    @EdmProperty(facets = @EdmFacets(maxLength = 15))
    private String country;

    @EdmKey
    @EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 31))
    private String salesperson;

    @EdmKey
    @EdmProperty(name = "OrderID", facets = @EdmFacets(nullable = false))
    private Integer orderId;

    @EdmProperty
    private Date orderDate;

    @EdmProperty
    private Date requiredDate;

    @EdmProperty
    private Date shippedDate;

    @EdmKey
    @EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
    private String shipperName;

    @EdmKey
    @EdmProperty(name = "ProductID", facets = @EdmFacets(nullable = false))
    private Integer productId;

    @EdmKey
    @EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
    private String productName;

    @EdmKey
    @EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(nullable = false, precision = 19, scale = 4))
    private Double unitPrice;

    @EdmKey
    @EdmProperty(facets = @EdmFacets(nullable = false))
    private Short quantity;

    @EdmKey
    @EdmProperty(facets = @EdmFacets(nullable = false))
    private Float discount;

    @EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(precision = 19, scale = 4))
    private Double extendedPrice;

    @EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(precision = 19, scale = 4))
    private Double freight;

}
