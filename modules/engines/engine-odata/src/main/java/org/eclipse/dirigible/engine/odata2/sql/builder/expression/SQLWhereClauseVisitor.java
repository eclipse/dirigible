/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.sql.builder.expression;

import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.INTERNAL_SERVER_ERROR;
import static org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils.evaluateDateTimeExpressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmLiteral;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.edm.EdmTypeKind;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.apache.olingo.odata2.api.uri.expression.CommonExpression;
import org.apache.olingo.odata2.api.uri.expression.ExpressionKind;
import org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.LiteralExpression;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodOperator;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderExpression;
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression;
import org.apache.olingo.odata2.api.uri.expression.SortOrder;
import org.apache.olingo.odata2.api.uri.expression.UnaryExpression;
import org.apache.olingo.odata2.api.uri.expression.UnaryOperator;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding.ColumnInfo;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLWhereClauseVisitor implements ExpressionVisitor {

    private static final Logger LOG = LoggerFactory.getLogger(SQLWhereClauseVisitor.class);
    private static Map<BinaryOperator, Integer> binaryOperatorPriorities;

    static {
        binaryOperatorPriorities = new HashMap<BinaryOperator, Integer>();
        binaryOperatorPriorities.put(BinaryOperator.PROPERTY_ACCESS, 100);
        binaryOperatorPriorities.put(BinaryOperator.MUL, 60);
        binaryOperatorPriorities.put(BinaryOperator.DIV, 60);
        binaryOperatorPriorities.put(BinaryOperator.MODULO, 60);
        binaryOperatorPriorities.put(BinaryOperator.ADD, 50);
        binaryOperatorPriorities.put(BinaryOperator.SUB, 50);
        binaryOperatorPriorities.put(BinaryOperator.LT, 40);
        binaryOperatorPriorities.put(BinaryOperator.GT, 40);
        binaryOperatorPriorities.put(BinaryOperator.GE, 40);
        binaryOperatorPriorities.put(BinaryOperator.LE, 40);
        binaryOperatorPriorities.put(BinaryOperator.EQ, 30);
        binaryOperatorPriorities.put(BinaryOperator.NE, 30);
        binaryOperatorPriorities.put(BinaryOperator.AND, 20);
        binaryOperatorPriorities.put(BinaryOperator.OR, 10);
    }

    private List<SQLExpressionWhere.Param> params = new ArrayList<SQLExpressionWhere.Param>();
    private SQLQuery query;
    private EdmStructuralType targetType;
    private boolean isInComplexType = false;

    public SQLWhereClauseVisitor(SQLQuery query, EdmStructuralType targetType) {
        this.params = new ArrayList<SQLExpressionWhere.Param>();
        this.targetType = targetType;
        this.query = query;
    }

    @Override
    public Object visitFilterExpression(FilterExpression filterExpression, String expressionString, Object expression) {
        return new SQLExpressionWhere((String) expression, params.toArray(new SQLExpressionWhere.Param[params.size()]));
    }

    @Override
    public Object visitBinary(BinaryExpression binaryExpression, BinaryOperator operator, Object leftSide, Object rightSide) {

        String leftSideString = null;
        String leftSideSqlType = null;
        String rightSideString = null;

        if (leftSide instanceof ColumnInfo) {
            leftSideString = ((ColumnInfo) leftSide).getColumnName();
            leftSideSqlType = ((ColumnInfo) leftSide).getSqlType();
        } else {
            leftSideString = (String) leftSide;
        }

        if (rightSide instanceof ColumnInfo) {
            rightSideString = ((ColumnInfo) rightSide).getColumnName();
        } else {
            rightSideString = (String) rightSide;
        }

        switch (operator) {
        case AND:
        case OR:
        case EQ:
        case NE:
        case LT:
        case LE:
        case GT:
        case GE:
            return escapeOperatorPrecedence(binaryExpression, binaryExpression.getLeftOperand(), //
                    writeParams(binaryExpression.getLeftOperand(), leftSideString)) + //
                    " " + translateToSQL(operator) + " " + //
                    escapeOperatorPrecedence(binaryExpression, binaryExpression.getRightOperand(), //
                            writeParams(binaryExpression.getRightOperand(), rightSideString, (String) null, leftSideSqlType));
        //TODO Add support for ADD SUB MUL DIV MOD (Relevant for SQL ?)
        case PROPERTY_ACCESS:
            //TODO this is when we have property access (need to use different table). what
            LOG.debug("Property access not implemented", binaryExpression.getUriLiteral());
            throw new RuntimeException(new ODataNotImplementedException());
        default:
            LOG.debug("Binary expression not implemented", binaryExpression.getUriLiteral());
            throw new RuntimeException(new ODataNotImplementedException());
        }
    }

    @Override
    public Object visitLiteral(LiteralExpression literal, EdmLiteral edmLiteral) {
        return edmLiteral.getLiteral();
    }

    @Override
    public Object visitMethod(MethodExpression methodExpression, MethodOperator method, List<Object> parameters) {

        String firstParameterAsString = null;
        Object firstParameter = parameters.get(0);
        if (firstParameter instanceof ColumnInfo) {
            firstParameterAsString = ((ColumnInfo) firstParameter).getColumnName();
        } else {
            firstParameterAsString = (String) firstParameter;
        }

        String secondParameterAsString = null;
        if (parameters.size() > 1) {
            Object secondParameter = parameters.get(1);
            if (secondParameter instanceof ColumnInfo) {
                secondParameterAsString = ((ColumnInfo) secondParameter).getColumnName();
            } else {
                secondParameterAsString = (String) secondParameter;
            }
        }

        switch (methodExpression.getMethod()) {
        case ENDSWITH:
            return String.format("%s LIKE %s", //
                    firstParameterAsString, //
                    writeParams(methodExpression.getParameters().get(1), secondParameterAsString, "%%%s"));
        case STARTSWITH:
            return String.format("%s LIKE %s", //
                    firstParameterAsString, //
                    writeParams(methodExpression.getParameters().get(1), secondParameterAsString, "%s%%"));
        case SUBSTRINGOF:
            return String.format("%s LIKE %s", //
                    secondParameterAsString, //
                    writeParams(methodExpression.getParameters().get(0), firstParameterAsString, "%%%s%%"));
        case TOLOWER:
            return String.format("LOWER(%s)", firstParameterAsString);
        case TOUPPER:
            return String.format("UPPER(%s)", firstParameterAsString);
        default:
            throw new RuntimeException(new ODataNotImplementedException());
        }
    }

    @Override
    public Object visitMember(MemberExpression memberExpression, Object path, Object property) {
        final EdmTypeKind propertyKind = memberExpression.getProperty().getEdmType().getKind();
        final CommonExpression pathEdmType = memberExpression.getPath();
        try {
            switch (propertyKind) {
            case SIMPLE:
                if (memberExpression.getPath().getKind() == ExpressionKind.MEMBER) {
                    final MemberExpression pathMemberExpression = (MemberExpression) memberExpression.getPath();
                    query.join((EdmStructuralType) pathMemberExpression.getProperty().getEdmType(),
                            (EdmStructuralType) pathMemberExpression.getPath().getEdmType());
                } else {
                    query.join((EdmStructuralType) memberExpression.getPath().getEdmType(), targetType);
                }
                return query.getSQLTableColumnInfo((EdmStructuralType) memberExpression.getPath().getEdmType(), (EdmProperty) property);
            case ENTITY:
                if (memberExpression.getPath().getKind() == ExpressionKind.MEMBER) {
                    final MemberExpression pathMemberExpression = (MemberExpression) memberExpression.getPath();
                    query.join((EdmStructuralType) pathMemberExpression.getProperty().getEdmType(),
                            (EdmStructuralType) pathMemberExpression.getPath().getEdmType());
                    return query.getSQLTableColumnInfo((EdmStructuralType) memberExpression.getPath().getEdmType(), (EdmProperty) property);
                } else {
                    query.join((EdmStructuralType) memberExpression.getPath().getEdmType(), targetType);
                }
                return property; // Return property here to be used as path for next level Member
            default:
                throw new OData2Exception(
                        String.format(
                                "Error during processing member expression between path %s and property %s: Unsupported property kind %s",
                                pathEdmType.getEdmType(), memberExpression.getProperty().getEdmType(), propertyKind),
                        INTERNAL_SERVER_ERROR);
            }
        } catch (EdmException e) {
            throw new OData2Exception(String.format("Error during processing member expression between path %s and property %s",
                    pathEdmType.getEdmType(), memberExpression.getProperty().getEdmType()), INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    public Object visitUnary(UnaryExpression unaryExpression, UnaryOperator operator, Object operand) {
        final ExpressionKind operandExpressionKind = unaryExpression.getOperand().getKind();
        String format = "%s %s";
        if (operandExpressionKind == ExpressionKind.UNARY || operandExpressionKind == ExpressionKind.BINARY) {
            // Brackets are only required in case Unary expression operates on another unary or binary expression
            format = "%s(%s)";
        }

        final String operandString = String.valueOf(operand);
        String operatorString = operator.name();
        switch (operator) {
        case NOT:
            operatorString = "NOT";
            break;
        case MINUS:
            operatorString = "-";
            break;
        }
        return String.format(format, operatorString, operandString);
    }

    @Override
    public Object visitOrderByExpression(OrderByExpression orderByExpression, String expressionString, List<Object> orders) {
        //TODO implement me 
        throw new RuntimeException(new ODataNotImplementedException());
    }

    @Override
    public Object visitOrder(OrderExpression orderExpression, Object filterResult, SortOrder sortOrder) {
        //TODO implement me 
        throw new RuntimeException(new ODataNotImplementedException());
    }

    @Override
    public Object visitProperty(PropertyExpression propertyExpression, String uriLiteral, EdmTyped edmProperty) {
        try {
            final EdmTypeKind propertyKind = edmProperty.getType().getKind();

            switch (propertyKind) {
            case COMPLEX:
                this.isInComplexType = true;
                EdmStructuralType complexType = (EdmStructuralType) edmProperty.getType();
                return complexType;
            case ENTITY:
                isInComplexType = true; // ???
                return propertyExpression.getEdmType();
            case SIMPLE:
                if (isInComplexType) {
                    this.isInComplexType = false;
                    return edmProperty;
                }
                try {
                    return query.getSQLTableColumnInfo(targetType, (EdmProperty) edmProperty);
                } catch (EdmException e) {
                    throw new OData2Exception(String.format("Unable to find binding for type %s and property %s", targetType, edmProperty),
                            INTERNAL_SERVER_ERROR, e);
                }
            default:
                throw new OData2Exception(String.format("Unable to handle PropertyKind %s for EdmType %s and PropertyProvider %s",
                        propertyKind, targetType, edmProperty.getClass().getName()), INTERNAL_SERVER_ERROR);

            }
        } catch (EdmException e) {
            throw new OData2Exception(
                    String.format("Unable to find binding for type %s and property %s", targetType, edmProperty.getClass().getName()),
                    INTERNAL_SERVER_ERROR, e);
        }
    }

    private String translateToSQL(final BinaryOperator operator) {
        switch (operator) {
        case EQ:
            // TODO CLEARIFY: Why not using operator.name() also here ???
            return "=";
        case NE:
            return "<>";
        case LT:
            return "<";
        case LE:
            return "<=";
        case GT:
            return ">";
        case GE:
            return ">=";
        default:
            return operator.name();
        }
    }

    private String writeParams(final CommonExpression expression, final String literal) {
        return writeParams(expression, literal, null);
    }

    private String writeParams(final CommonExpression expression, final String literal, final String format) {

        if (expression.getKind() == ExpressionKind.LITERAL) {
            final String value = (format != null ? String.format(format, literal) : literal);
            Object literalValue = evaluateDateTimeExpressions(value, (EdmSimpleType) expression.getEdmType());
            params.add(SQLExpressionWhere.param(literalValue, (EdmSimpleType) expression.getEdmType()));
            //we should be using a prepared statement, so therefore always return the question marks here, since we have added the param to the query
            return "?";
        }
        return literal;
    }

    private String writeParams(final CommonExpression expression, final String literal, final String format, final String sqlType) {

        if (expression.getKind() == ExpressionKind.LITERAL) {
            final String value = (format != null ? String.format(format, literal) : literal);
            Object literalValue = evaluateDateTimeExpressions(value, (EdmSimpleType) expression.getEdmType());
            params.add(SQLExpressionWhere.param(literalValue, (EdmSimpleType) expression.getEdmType(), sqlType));
            //we should be using a prepared statement, so therefore always return the question marks here, since we have added the param to the query
            return "?";
        }
        return literal;
    }

    private Object escapeOperatorPrecedence(BinaryExpression expression, CommonExpression subExpression, String subExpressionString) {

        if (subExpression.getKind() == ExpressionKind.BINARY) {
            final Integer expressionPriority = binaryOperatorPriorities.get(expression.getOperator());
            final Integer subExpressionPriority = binaryOperatorPriorities.get(((BinaryExpression) subExpression).getOperator());
            if (expressionPriority > subExpressionPriority) {
                // If subExpression has lower implicit priority use brackets to invert precedence
                return "(" + subExpressionString + ")";
            }
        }

        // Simply return expression as is
        return subExpressionString;
    }

}