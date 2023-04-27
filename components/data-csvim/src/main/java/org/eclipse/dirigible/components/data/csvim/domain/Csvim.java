/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.csvim.domain;

import com.google.gson.annotations.Expose;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.data.structures.domain.TableColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//	{
//		files: [
//			{name: "asd.csv"}
//		]
//	}

/**
 * The Csvim Entity.
 */
@Entity
@javax.persistence.Table(name = "DIRIGIBLE_CSVIM")
public class Csvim extends Artefact {

    /**
     * The Constant ARTEFACT_TYPE.
     */
    public static final String ARTEFACT_TYPE = "csvim";

    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CSVIM_ID", nullable = false)
    private Long id;

    /**
     * The csv file definitions.
     */
    @OneToMany(mappedBy = "csvim", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Nullable
    @Expose
    private List<CsvFile> csvFile = new ArrayList<CsvFile>();

    public Csvim(Long id, List<CsvFile> csvFile) {
        this.id = id;
        this.csvFile = csvFile;
    }

    public Csvim() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Nullable
    public List<CsvFile> getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(@Nullable List<CsvFile> csvFile) {
        this.csvFile = csvFile;
    }

    public CsvFile getCsvFile(String location) {
        if (csvFile != null) {
            for (CsvFile cf : csvFile) {
                if (cf.getLocation().equals(location)) {
                    return cf;
                }
            }
        }
        return null;
    }
}
