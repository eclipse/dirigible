/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.dialects.sybase;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.builders.AlterBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;

/**
 * The Sybase SQL Dialect.
 */
public class SybaseSqlDialect extends
        DefaultSqlDialect<SybaseSelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, CreateBranchingBuilder, AlterBranchingBuilder, DropBranchingBuilder, SybaseNextValueSequenceBuilder, SybaseLastValueIdentityBuilder> {

    /** The Constant FUNCTION_CURRENT_DATE. */
    public static final String FUNCTION_CURRENT_DATE = "current_date"; //$NON-NLS-1$

    /** The Constant FUNCTION_CURRENT_TIME. */
    public static final String FUNCTION_CURRENT_TIME = "current_time"; //$NON-NLS-1$

    /** The Constant FUNCTION_CURRENT_TIMESTAMP. */
    public static final String FUNCTION_CURRENT_TIMESTAMP = "getdate()"; //$NON-NLS-1$

    /** The Constant FUNCTIONS. */
    public static final Set<String> FUNCTIONS = Collections.synchronizedSet(new HashSet<String>(Arrays.asList(new String[] {"abs", "acos",
            "argn", "ascii", "asin", "atan", "atan2", "avg", "bfile", "biginttohex", "bit_length", "byte_length", "byte_length64",
            "byte_substr64", "cast", "ceil", "ceiling", "char", "char_length", "char_length64", "charindex", "coalesce", "col_length",
            "col_name", "connection_property", "convert", "corr", "cos", "cot", "covar_pop", "covar_samp", "count", "cume_dist", "date",
            "dateadd", "dateceiling", "datediff", "datefloor", "dateformat", "datename", "datepart", "dateround", "datetime", "day",
            "dayname", "days", "db_id", "db_name", "db_property", "degrees", "dense_rank", "difference", "dow", "errormsg",
            "event_condition", "event_condition_name", "event_parameter", "exp", "exp_weighted_avg", "first_value", "floor", "getdate",
            "graphical_plan", "grouping", "group_member", "hextobigint", "hextoint", "hour", "hours", "html_decode", "html_encode",
            "html_plan", "http_decode", "http_encode", "http_header", "http_variable", "ifnull", "index_col", "insertstr", "inttohex",
            "isdate", "isnull", "isnumeric", "lag", "last_value", "lcase", "lead", "left", "len", "length", "list", "ln", "locate", "log",
            "log10", "lower", "ltrim", "max", "median", "min", "minute", "minutes", "mod", "month", "monthname", "months", "newid",
            "next_connection", "next_database", "next_http_header", "next_http_variable", "now", "ntile", "nullif", "number", "object_id",
            "object_name", "octet_length", "patindex", "percent_rank", "percentile_cont", "percentile_disc", "pi", "power", "property",
            "property_description", "property_name", "property_number", "quarter", "radians", "rand", "rank", "regr_avgx", "regr_avgy",
            "regr_count", "regr_intercept", "regr_r2", "regr_slope", "regr_sxx", "regr_sxy", "regr_syy", "remainder", "repeat", "replace",
            "replicate", "reverse", "right", "round", "row_number", "rowid", "rtrim", "second", "seconds", "sign", "similar", "sin",
            "sortkey", "soundex", "space", "sqlflagger", "sqrt", "square", "stddev", "stddev_pop", "stddev_samp", "str", "str_replace",
            "string", "strtouuid", "stuff", "substring", "substring64", "sum", "suser_id", "suser_name", "tan", "today", "trim", "truncnum",
            "ts_arma_ar", "ts_arma_const", "ts_arma_ma", "ts_autocorrelation", "ts_auto_arima", "ts_auto_arima_outlier",
            "ts_auto_arima_result_aic", "ts_auto_arima_result_aicc", "ts_auto_arima_result_bic", "ts_auto_arima_result_forecast_value",
            "ts_auto_arima_result_forecast_error", "ts_auto_arima_result_model_d", "ts_auto_arima_result_model_p",
            "ts_auto_arima_result_model_q", "ts_auto_arima_result_model_s", "ts_auto_arima_result_residual_sigma", "ts_auto_uni_ar",
            "ts_box_cox_xform", "ts_difference", "ts_double_array", "ts_estimate_missing", "ts_garch", "ts_garch_result_a",
            "ts_garch_result_aic", "ts_garch_result_user", "ts_int_array", "ts_lack_of_fit", "ts_lack_of_fit_p", "ts_max_arma_ar",
            "ts_max_arma_const", "ts_max_arma_likelihood", "ts_max_arma_ma", "ts_outlier_identification", "ts_partial_autocorrelation",
            "ucase", "upper", "user_id", "user_name", "uuidtostr", "var_pop", "var_samp", "variance", "weeks", "weighted_avg",
            "width_bucket", "year", "years", "ymd",

            "and", "or", "between", "binary", "case", "div", "in", "is", "not", "null", "like", "rlike", "xor"

    })));

    /**
     * Creates the.
     *
     * @return the sybase create branching builder
     */
    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#create()
     */
    @Override
    public SybaseCreateBranchingBuilder create() {
        return new SybaseCreateBranchingBuilder(this);
    }

    /**
     * Drop.
     *
     * @return the sybase drop branching builder
     */
    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#drop()
     */
    @Override
    public SybaseDropBranchingBuilder drop() {
        return new SybaseDropBranchingBuilder(this);
    }

    /**
     * Nextval.
     *
     * @param sequence the sequence
     * @return the sybase next value sequence builder
     */
    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.
     * lang.String)
     */
    @Override
    public SybaseNextValueSequenceBuilder nextval(String sequence) {
        return new SybaseNextValueSequenceBuilder(this, sequence);

    }

    /**
     * Gets the data type name.
     *
     * @param dataType the data type
     * @return the data type name
     */
    @Override
    public String getDataTypeName(DataType dataType) {
        switch (dataType) {
            case TIMESTAMP:
                return "DATETIME";
            case BLOB:
                return "IMAGE";
            case BOOLEAN:
                return "BIT";
            case DOUBLE:
                return "DOUBLE PRECISION";
            default:
                return super.getDataTypeName(dataType);
        }
    }

    /**
     * Lastval.
     *
     * @param args the args
     * @return the sybase last value identity builder
     */
    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.
     * lang.String)
     */
    @Override
    public SybaseLastValueIdentityBuilder lastval(String... args) {
        return new SybaseLastValueIdentityBuilder(this);
    }

    /**
     * Select.
     *
     * @return the sybase select builder
     */
    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#select()
     */
    @Override
    public SybaseSelectBuilder select() {
        return new SybaseSelectBuilder(this);
    }

    /**
     * Function current date.
     *
     * @return the string
     */
    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#
     * functionCurrentDate()
     */
    @Override
    public String functionCurrentDate() {
        return FUNCTION_CURRENT_DATE;
    }

    /**
     * Function current time.
     *
     * @return the string
     */
    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#
     * functionCurrentTime()
     */
    @Override
    public String functionCurrentTime() {
        return FUNCTION_CURRENT_TIME;
    }

    /**
     * Function current timestamp.
     *
     * @return the string
     */
    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#
     * functionCurrentTimestamp()
     */
    @Override
    public String functionCurrentTimestamp() {
        return FUNCTION_CURRENT_TIMESTAMP;
    }

    /**
     * Gets the functions names.
     *
     * @return the functions names
     */
    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.dirigible.database.sql.ISqlDialect#getFunctionsNames()
     */
    @Override
    public Set<String> getFunctionsNames() {
        return FUNCTIONS;
    }

}
