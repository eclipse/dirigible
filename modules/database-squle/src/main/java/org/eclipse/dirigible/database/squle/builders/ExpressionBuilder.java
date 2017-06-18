package org.eclipse.dirigible.database.squle.builders;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.database.squle.ISquleDialect;

public class ExpressionBuilder extends AbstractSquleBuilder {
	
	private List<String> expressions = new ArrayList<String>();
	
	public ExpressionBuilder(ISquleDialect dialect) {
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
