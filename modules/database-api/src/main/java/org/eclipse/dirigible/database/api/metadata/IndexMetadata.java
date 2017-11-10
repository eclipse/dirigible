/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.api.metadata;

public class IndexMetadata {

	private String name;

	private String type;

	private String column;

	private boolean nonUnique;

	private String qualifier;

	private String ordinalPosition;

	private String sortOrder;

	private int cardinality;

	private int pages;

	private String filterCondition;

	private String kind = "index";

	public IndexMetadata(String name, String type, String column, boolean nonUnique, String qualifier, String ordinalPosition, String sortOrder,
			int cardinality, int pages, String filterCondition) {
		super();
		this.name = name;
		this.type = type;
		this.column = column;
		this.nonUnique = nonUnique;
		this.qualifier = qualifier;
		this.ordinalPosition = ordinalPosition;
		this.sortOrder = sortOrder;
		this.cardinality = cardinality;
		this.pages = pages;
		this.filterCondition = filterCondition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public boolean isNonUnique() {
		return nonUnique;
	}

	public void setNonUnique(boolean nonUnique) {
		this.nonUnique = nonUnique;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getOrdinalPosition() {
		return ordinalPosition;
	}

	public void setOrdinalPosition(String ordinalPosition) {
		this.ordinalPosition = ordinalPosition;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public int getCardinality() {
		return cardinality;
	}

	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

}
