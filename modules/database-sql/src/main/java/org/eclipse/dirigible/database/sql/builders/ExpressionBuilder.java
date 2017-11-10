/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.builders;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.database.sql.ISqlDialect;

public class ExpressionBuilder extends AbstractSqlBuilder {
	
	private List<String> expressions = new ArrayList<String>();
	
	public ExpressionBuilder(ISqlDialect dialect) {
		super(dialect);
	}
	
	public ExpressionBuilder and(String name) {
		if (this.expressions.isEmpty()) {
			this.expressions.add(name);
		} else {
			this.expressions.add(KEYWORD_AND + SPACE + name);
		}
		return this;
	}
	
	public ExpressionBuilder or(String name) {
		if (this.expressions.isEmpty()) {
			this.expressions.add(name);
		} else {
			this.expressions.add(KEYWORD_OR + SPACE + name);
		}
		return this;
	}
	
	@Override
	public String generate() {
		return generateExpressions();
	}
	
	protected String generateExpressions() {
		StringBuilder snippet = new StringBuilder();
		for (String expression : this.expressions) {
			snippet.append(expression)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 1);
	}
	
	public List<String> getExpressions() {
		return expressions;
	}

}
