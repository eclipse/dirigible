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
package org.eclipse.dirigible.database.persistence.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class All Data Types.
 */
@Table(name = "DT")
public class AllDataTypes {
	
	@Id
	@Column(name = "ALL_VARCHAR", columnDefinition = "VARCHAR", nullable = false, length = 512)
	private String _varchar;

	@Column(name = "ALL_CHAR", columnDefinition = "CHAR", nullable = false, length = 10)
	private String _char;
	
	@Column(name = "ALL_DATE", columnDefinition = "DATE", nullable = false)
	private Date _date;
	
	@Column(name = "ALL_TIME", columnDefinition = "TIME", nullable = false)
	private Time _time;
	
	@Column(name = "ALL_TIMESTAMP", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp _timestamp;
	
	@Column(name = "ALL_INTEGER", columnDefinition = "INTEGER", nullable = false)
	private int _integer;
	
	@Column(name = "ALL_TINYINT", columnDefinition = "TINYINT", nullable = false)
	private byte _tinyint;
	
	@Column(name = "ALL_BIGINT", columnDefinition = "BIGINT", nullable = false)
	private BigInteger _bigint;
	
	@Column(name = "ALL_SMALLINT", columnDefinition = "SMALLINT", nullable = false)
	private short _smallint;
	
	@Column(name = "ALL_REAL", columnDefinition = "REAL", nullable = false)
	private float _real;
	
	@Column(name = "ALL_DOUBLE", columnDefinition = "DOUBLE", nullable = false)
	private double _double;
	
	@Column(name = "ALL_BOOLEAN", columnDefinition = "BOOLEAN", nullable = false)
	private boolean _boolean;
	
	@Column(name = "ALL_BLOB", columnDefinition = "BLOB", nullable = false)
	private byte[] _blob;
	
	@Column(name = "ALL_DECIMAL", columnDefinition = "DECIMAL", nullable = false, length = 10, scale = 2)
	private BigDecimal _decimal;
	
	@Column(name = "ALL_BIT", columnDefinition = "BIT", nullable = false)
	private boolean _bit;

	public String get_varchar() {
		return _varchar;
	}

	public void set_varchar(String _varchar) {
		this._varchar = _varchar;
	}

	public String get_char() {
		return _char;
	}

	public void set_char(String _char) {
		this._char = _char;
	}

	public Date get_date() {
		return _date;
	}

	public void set_date(Date _date) {
		this._date = _date;
	}

	public Time get_time() {
		return _time;
	}

	public void set_time(Time _time) {
		this._time = _time;
	}

	public Timestamp get_timestamp() {
		return _timestamp;
	}

	public void set_timestamp(Timestamp _timestamp) {
		this._timestamp = _timestamp;
	}

	public int get_integer() {
		return _integer;
	}

	public void set_integer(int _integer) {
		this._integer = _integer;
	}

	public byte get_tinyint() {
		return _tinyint;
	}

	public void set_tinyint(byte _tinyint) {
		this._tinyint = _tinyint;
	}

	public BigInteger get_bigint() {
		return _bigint;
	}

	public void set_bigint(BigInteger _bigint) {
		this._bigint = _bigint;
	}

	public short get_smallint() {
		return _smallint;
	}

	public void set_smallint(short _smallint) {
		this._smallint = _smallint;
	}

	public float get_real() {
		return _real;
	}

	public void set_real(float _real) {
		this._real = _real;
	}

	public double get_double() {
		return _double;
	}

	public void set_double(double _double) {
		this._double = _double;
	}

	public boolean is_boolean() {
		return _boolean;
	}

	public void set_boolean(boolean _boolean) {
		this._boolean = _boolean;
	}

	public byte[] get_blob() {
		return _blob;
	}

	public void set_blob(byte[] _blob) {
		this._blob = _blob;
	}

	public BigDecimal get_decimal() {
		return _decimal;
	}

	public void set_decimal(BigDecimal _decimal) {
		this._decimal = _decimal;
	}

	public boolean is_bit() {
		return _bit;
	}

	public void set_bit(boolean _bit) {
		this._bit = _bit;
	}

	
	
}
