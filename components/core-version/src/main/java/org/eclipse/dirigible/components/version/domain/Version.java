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
package org.eclipse.dirigible.components.version.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Version properties.
 */
public class Version {

    /**
     * The product name.
     */
    private String productName;

    /**
     * The product version.
     */
    private String productVersion;

    /**
     * The product commit id.
     */
    private String productCommitId;

    /**
     * The product repository.
     */
    private String productRepository;

    /**
     * The product type.
     */
    private String productType;

    /**
     * The instance name.
     */
    private String instanceName;

    /**
     * The repository provider.
     */
    private String repositoryProvider = "local";

    /**
     * The database provider.
     */
    private String databaseProvider = "local";

    /**
     * The units.
     */
    private Set<String> units = new HashSet<String>();

    /**
     * The engines.
     */
    private Set<String> engines = new HashSet<String>();

    /**
     * Gets the product name.
     *
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the product name.
     *
     * @param productName the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets the product version.
     *
     * @return the productVersion
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * Sets the product version.
     *
     * @param productVersion the productVersion to set
     */
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    /**
     * Gets the product commit id.
     *
     * @return the productCommitId
     */
    public String getProductCommitId() {
        return productCommitId;
    }

    /**
     * Sets the product commit id.
     *
     * @param productCommitId the productCommitId to set
     */
    public void setProductCommitId(String productCommitId) {
        this.productCommitId = productCommitId;
    }

    /**
     * Gets the product repository.
     *
     * @return the productRepository
     */
    public String getProductRepository() {
        return productRepository;
    }

    /**
     * Sets the product repository.
     *
     * @param productRepository the productRepository to set
     */
    public void setProductRepository(String productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Gets the product type.
     *
     * @return the productType
     */
    public String getProductType() {
        return productType;
    }

    /**
     * Sets the product type.
     *
     * @param productType the productType to set
     */
    public void setProductType(String productType) {
        this.productType = productType;
    }

    /**
     * Gets the instance name.
     *
     * @return the instanceName
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * Sets the instance name.
     *
     * @param instanceName the instanceName to set
     */
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    /**
     * Gets the repository provider.
     *
     * @return the repositoryProvider
     */
    public String getRepositoryProvider() {
        return repositoryProvider;
    }

    /**
     * Sets the repository provider.
     *
     * @param repositoryProvider the repositoryProvider to set
     */
    public void setRepositoryProvider(String repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }

    /**
     * Gets the database provider.
     *
     * @return the databaseProvider
     */
    public String getDatabaseProvider() {
        return databaseProvider;
    }

    /**
     * Sets the database provider.
     *
     * @param databaseProvider the databaseProvider to set
     */
    public void setDatabaseProvider(String databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    /**
     * Gets the engines.
     *
     * @return the engines
     */
    public Set<String> getEngines() {
        return engines;
    }

    /**
     * Sets the engines.
     *
     * @param engines the engines to set
     */
    public void setEngines(Set<String> engines) {
        this.engines = engines;
    }
    
    /**
     * Gets the units.
     *
     * @return the units
     */
    public Set<String> getUnits() {
        return units;
    }

    /**
     * Sets the units.
     *
     * @param units the units to set
     */
    public void setUnits(Set<String> units) {
        this.units = units;
    }


}
