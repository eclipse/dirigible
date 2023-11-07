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
package org.eclipse.dirigible.database.sql.dialects.mysql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.builders.AlterBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;

/**
 * The MySQL SQL Dialect.
 */
public class MySQLSqlDialect extends
    DefaultSqlDialect<SelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, MySQLCreateBranchingBuilder, AlterBranchingBuilder, MySQLDropBranchingBuilder, MySQLNextValueSequenceBuilder, MySQLLastValueIdentityBuilder> {

  /** The Constant MYSQL_KEYWORD_IDENTITY. */
  private static final String MYSQL_KEYWORD_IDENTITY = "AUTO_INCREMENT";

  /** The Constant FUNCTIONS. */
  public static final Set<String> FUNCTIONS = Collections.synchronizedSet(new HashSet<String>(Arrays.asList(new String[] {"null", "mod",
      "abs", "acos", "adddate", "addtime", "aes_decrypt", "aes_encrypt", "and", "any_value", "ascii", "asin", "atan", "atan2", "atan",
      "avg", "benchmark", "between", "bin", "bin_to_uuid", "binary", "bit_and", "bit_count", "bit_length", "bit_or", "bit_xor",
      "can_access_column", "can_access_database", "can_access_table", "can_access_user", "can_access_view", "case", "cast", "ceil",
      "ceiling", "char", "char_length", "character_length", "char_length", "charset", "coalesce", "coercibility", "collation", "compress",
      "concat", "concat_ws", "connection_id", "conv", "convert", "convert_tz", "cos", "cot", "count", "crc32", "cume_dist", "curdate",
      "current_date", "current_role", "current_time", "current_timestamp", "current_user", "curtime", "database", "date", "date_add",
      "date_format", "date_sub", "datediff", "day", "dayname", "dayofmonth", "dayofweek", "dayofyear", "default", "degrees", "dense_rank",
      "div", "elt", "exp", "export_set", "extract", "field", "find_in_set", "first_value", "floor", "format", "format_bytes",
      "format_pico_time", "found_rows", "from_base64", "from_days", "from_unixtime", "geomcollection", "geometrycollection",
      "get_dd_column_privileges", "get_dd_create_options", "get_dd_index_sub_part_length", "get_format", "get_lock", "greatest",
      "group_concat", "grouping", "gtid_subset", "gtid_subtract", "hex", "hour", "icu_version", "if", "ifnull", "in", "inet_aton",
      "inet_ntoa", "inet6_aton", "inet6_ntoa", "insert", "instr", "internal_auto_increment", "internal_avg_row_length",
      "internal_check_time", "internal_checksum", "internal_data_free", "internal_data_length", "internal_dd_char_length",
      "internal_get_comment_or_error", "internal_get_enabled_role_json", "internal_get_hostname", "internal_get_username",
      "internal_get_view_warning_or_error", "internal_index_column_cardinality", "internal_index_length", "internal_is_enabled_role",
      "internal_is_mandatory_role", "internal_keys_disabled", "internal_max_data_length", "internal_table_rows", "internal_update_time",
      "interval", "is", "is_free_lock", "is_ipv4", "is_ipv4_compat", "is_ipv4_mapped", "is_ipv6", "not", "null", "is_used_lock", "is_uuid",
      "isnull", "json_array", "json_array_append", "json_array_insert", "json_arrayagg", "json_contains", "json_contains_path",
      "json_depth", "json_extract", "json_insert", "json_keys", "json_length", "json_merge", "json_merge_patch", "json_merge_preserve",
      "json_object", "json_objectagg", "json_overlaps", "json_pretty", "json_quote", "json_remove", "json_replace", "json_schema_valid",
      "json_schema_validation_report", "json_search", "json_set", "json_storage_free", "json_storage_size", "json_table", "json_type",
      "json_unquote", "json_valid", "json_value", "lag", "last_day", "last_insert_id", "last_value", "lcase", "lead", "least", "left",
      "length", "like", "linestring", "ln", "load_file", "localtime", "localtimestamp", "locate", "log", "log10", "log2", "lower", "lpad",
      "ltrim", "make_set", "makedate", "maketime", "master_pos_wait", "match", "max", "mbrcontains", "mbrcoveredby", "mbrcovers",
      "mbrdisjoint", "mbrequals", "mbrintersects", "mbroverlaps", "mbrtouches", "mbrwithin", "md5", "member", "of", "or", "microsecond",
      "mid", "min", "minute", "mod", "month", "monthname", "multilinestring", "multipoint", "multipolygon", "name_const", "not", "between",
      "in", "now", "nth_value", "ntile", "nullif", "oct", "octet_length", "or", "ord", "percent_rank", "period_add", "period_diff", "pi",
      "point", "polygon", "position", "pow", "power", "ps_current_thread_id", "ps_thread_id", "quarter", "quote", "radians", "rand",
      "random_bytes", "rank", "regexp", "regexp_instr", "regexp_like", "regexp_replace", "regexp_substr", "release_all_locks",
      "release_lock", "repeat", "replace", "reverse", "right", "rlike", "roles_graphml", "round", "row_count", "row_number", "rpad",
      "rtrim", "schema", "sec_to_time", "second", "session_user", "sha1", "sha", "sha2", "sign", "sin", "sleep", "soundex", "sounds",
      "space", "sqrt", "st_area", "st_asbinary", "st_asgeojson", "st_astext", "st_aswkt", "st_buffer", "st_buffer_strategy", "st_centroid",
      "st_collect", "st_contains", "st_convexhull", "st_crosses", "st_difference", "st_dimension", "st_disjoint", "st_distance",
      "st_distance_sphere", "st_endpoint", "st_envelope", "st_equals", "st_exteriorring", "st_frechetdistance", "st_geohash",
      "st_geomcollfromtext", "st_geometrycollectionfromtext", "st_geomcollfromtxt", "st_geomcollfromwkb", "st_geometrycollectionfromwkb",
      "st_geometryn", "st_geometrytype", "st_geomfromgeojson", "st_geomfromtext", "st_geometryfromtext", "st_geomfromwkb",
      "st_geometryfromwkb", "st_hausdorffdistance", "st_interiorringn", "st_intersection", "st_intersects", "st_isclosed", "st_isempty",
      "st_issimple", "st_isvalid", "st_latfromgeohash", "st_latitude", "st_length", "st_linefromtext", "st_linestringfromtext",
      "st_linefromwkb", "st_linestringfromwkb", "st_lineinterpolatepoint", "st_lineinterpolatepoints", "st_longfromgeohash", "st_longitude",
      "st_makeenvelope", "st_mlinefromtext", "st_multilinestringfromtext", "st_mlinefromwkb", "st_multilinestringfromwkb",
      "st_mpointfromtext", "st_multipointfromtext", "st_mpointfromwkb", "st_multipointfromwkb", "st_mpolyfromtext",
      "st_multipolygonfromtext", "st_mpolyfromwkb", "st_multipolygonfromwkb", "st_numgeometries", "st_numinteriorring",
      "st_numinteriorrings", "st_numpoints", "st_overlaps", "st_pointatdistance", "st_pointfromgeohash", "st_pointfromtext",
      "st_pointfromwkb", "st_pointn", "st_polyfromtext", "st_polygonfromtext", "st_polyfromwkb", "st_polygonfromwkb", "st_simplify",
      "st_srid", "st_startpoint", "st_swapxy", "st_symdifference", "st_touches", "st_transform", "st_union", "st_validate", "st_within",
      "st_x", "st_y", "statement_digest", "statement_digest_text", "std", "stddev", "stddev_pop", "stddev_samp", "str_to_date", "strcmp",
      "subdate", "substr", "substring", "substring_index", "subtime", "sum", "sysdate", "system_user", "tan", "time", "time_format",
      "time_to_sec", "timediff", "timestamp", "timestampadd", "timestampdiff", "to_base64", "to_days", "to_seconds", "trim", "truncate",
      "ucase", "uncompress", "uncompressed_length", "unhex", "unix_timestamp", "updatexml", "upper", "user", "utc_date", "utc_time",
      "utc_timestamp", "uuid", "uuid_short", "uuid_to_bin", "validate_password_strength", "values", "var_pop", "var_samp", "variance",
      "version", "wait_for_executed_gtid_set", "wait_until_sql_thread_after_gtids", "week", "weekday", "weekofyear", "weight_string", "xor",
      "year", "yearweek",

      "asymmetric_decrypt", "asymmetric_derive", "asymmetric_encrypt", "asymmetric_sign", "asymmetric_verify",
      "asynchronous_connection_failover_add_managed", "asynchronous_connection_failover_add_source",
      "asynchronous_connection_failover_delete_managed", "asynchronous_connection_failover_delete_source", "audit_api_message_emit_udf",
      "audit_log_encryption_password_get", "audit_log_encryption_password_set", "audit_log_filter_flush", "audit_log_filter_remove_filter",
      "audit_log_filter_remove_user", "audit_log_filter_set_filter", "audit_log_filter_set_user", "audit_log_read",
      "audit_log_read_bookmark", "create_asymmetric_priv_key", "create_asymmetric_pub_key", "create_dh_parameters", "create_digest",
      "firewall_group_delist", "firewall_group_enlist", "gen_blacklist", "gen_blocklist", "gen_dictionary", "gen_dictionary_drop",
      "gen_dictionary_load", "gen_range", "gen_rnd_email", "gen_rnd_pan", "gen_rnd_ssn", "gen_rnd_us_phone",
      "group_replication_get_communication_protocol", "group_replication_get_write_concurrency", "group_replication_set_as_primary",
      "group_replication_set_communication_protocol", "group_replication_set_write_concurrency",
      "group_replication_switch_to_multi_primary_mode", "group_replication_switch_to_single_primary_mode", "keyring_aws_rotate_cmk",
      "keyring_aws_rotate_keys", "keyring_hashicorp_update_config", "keyring_key_fetch", "keyring_key_generate", "keyring_key_length_fetch",
      "keyring_key_remove", "keyring_key_store", "keyring_key_type_fetch", "load_rewrite_rules", "mask_inner", "mask_outer", "mask_pan",
      "mask_pan_relaxed", "mask_ssn", "mysql_firewall_flush_status", "mysql_query_attribute_string", "normalize_statement",
      "read_firewall_group_allowlist", "read_firewall_groups", "read_firewall_users", "read_firewall_whitelist", "service_get_read_locks",
      "service_get_write_locks", "service_release_locks", "set_firewall_group_mode", "set_firewall_mode", "version_tokens_delete",
      "version_tokens_edit", "version_tokens_lock_exclusive", "version_tokens_lock_shared", "version_tokens_set", "version_tokens_show",
      "version_tokens_unlock"

  })));

  /**
   * Creates the.
   *
   * @return the my SQL create branching builder
   */
  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#create()
   */
  @Override
  public MySQLCreateBranchingBuilder create() {
    return new MySQLCreateBranchingBuilder(this);
  }

  /**
   * Drop.
   *
   * @return the my SQL drop branching builder
   */
  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#drop()
   */
  @Override
  public MySQLDropBranchingBuilder drop() {
    return new MySQLDropBranchingBuilder(this);
  }

  /**
   * Nextval.
   *
   * @param sequence the sequence
   * @return the my SQL next value sequence builder
   */
  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.lang.String)
   */
  @Override
  public MySQLNextValueSequenceBuilder nextval(String sequence) {
    return new MySQLNextValueSequenceBuilder(this, sequence);
  }

  /**
   * Lastval.
   *
   * @param args the args
   * @return the my SQL last value identity builder
   */
  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.lang.String)
   */
  @Override
  public MySQLLastValueIdentityBuilder lastval(String... args) {
    return new MySQLLastValueIdentityBuilder(this);
  }

  /**
   * Gets the identity argument.
   *
   * @return the identity argument
   */
  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.dirigible.database.sql.ISqlDialect#getPrimaryKeyArgument()
   */
  @Override
  public String getIdentityArgument() {
    return MYSQL_KEYWORD_IDENTITY;
  }

  /**
   * Checks if is sequence supported.
   *
   * @return true, if is sequence supported
   */
  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.dirigible.database.sql.ISqlDialect#isSequenceSupported()
   */
  @Override
  public boolean isSequenceSupported() {
    return false;
  }

  /**
   * Exists.
   *
   * @param connection the connection
   * @param table the table
   * @return true, if successful
   * @throws SQLException the SQL exception
   */
  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.dirigible.database.sql.ISqlDialect#exists(java.sql.Connection, java.lang.String)
   */
  @Override
  public boolean existsTable(Connection connection, String table) throws SQLException {
    table = normalizeTableName(table);
    DatabaseMetaData metadata = connection.getMetaData();
    ResultSet resultSet = metadata.getTables(null, null, DefaultSqlDialect.normalizeTableName(table.toUpperCase()),
        ISqlKeywords.METADATA_TABLE_TYPES.toArray(new String[] {}));
    if (resultSet.next()) {
      return true;
    }
    return false;
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

  /**
   * Gets the escape symbol.
   *
   * @return the escape symbol
   */
  public String getEscapeSymbol() {
    return "`";
  }

}
