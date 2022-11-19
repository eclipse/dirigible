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
package org.eclipse.dirigible.components.data.structures.domain;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class TableConstraintCheck.
 */
@Entity
@javax.persistence.Table(name = "DIRIGIBLE_TABLE_CHECKS")
public class TableConstraintCheck extends TableConstraint {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CHECK_ID", nullable = false)
	private Long id;
	
	/** The expression. */
	@Column(name = "CHECK_EXPRESSION", columnDefinition = "VARCHAR", nullable = true, length = 255)
	@Nullable
	private String expression;
	
	/**
	 * Instantiates a new table constraint check.
	 *
	 * @param name the name
	 * @param modifiers the modifiers
	 * @param columns the columns
	 * @param constraints the constraints
	 * @param expression the expression
	 */
	public TableConstraintCheck(String name, String[] modifiers, String[] columns, TableConstraints constraints,
			String expression) {
		super(name, modifiers, columns, constraints);
		this.expression = expression;
	}

	/**
	 * Instantiates a new table constraint check.
	 */
	public TableConstraintCheck() {
		super();
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the expression.
	 *
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Sets the expression.
	 *
	 * @param expression the expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "TableConstraintCheck [id=" + id + ", expression=" + expression + ", name=" + name + ", modifiers="
				+ modifiers + ", columns=" + columns + ", constraints.table=" + constraints.getTable().getName() + "]";
	}
	
	

}
