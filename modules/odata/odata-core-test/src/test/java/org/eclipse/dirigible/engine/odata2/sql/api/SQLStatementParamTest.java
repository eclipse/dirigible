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
package org.eclipse.dirigible.engine.odata2.sql.api;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.EdmType;
import org.apache.olingo.odata2.core.edm.EdmBinary;
import org.apache.olingo.odata2.core.edm.EdmBoolean;
import org.apache.olingo.odata2.core.edm.EdmByte;
import org.apache.olingo.odata2.core.edm.EdmDateTime;
import org.apache.olingo.odata2.core.edm.EdmDateTimeOffset;
import org.apache.olingo.odata2.core.edm.EdmDecimal;
import org.apache.olingo.odata2.core.edm.EdmDouble;
import org.apache.olingo.odata2.core.edm.EdmGuid;
import org.apache.olingo.odata2.core.edm.EdmInt16;
import org.apache.olingo.odata2.core.edm.EdmInt32;
import org.apache.olingo.odata2.core.edm.EdmInt64;
import org.apache.olingo.odata2.core.edm.EdmNull;
import org.apache.olingo.odata2.core.edm.EdmSByte;
import org.apache.olingo.odata2.core.edm.EdmSingle;
import org.apache.olingo.odata2.core.edm.EdmString;
import org.apache.olingo.odata2.core.edm.EdmTime;
import org.junit.Assert;
import org.junit.Test;

public class SQLStatementParamTest {

  @Test
  public void testEdmTypeInt32ToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmInt32(), EdmSimpleTypeKind.Int32);
  }

  @Test
  public void testEdmTypeInt16ToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmInt16(), EdmSimpleTypeKind.Int16);
  }

  @Test
  public void testEdmTypeInt64ToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmInt64(), EdmSimpleTypeKind.Int64);
  }

  @Test
  public void testEdmTypeStringToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmString(), EdmSimpleTypeKind.String);
  }

  @Test
  public void testEdmTypeDoubleToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmDouble(), EdmSimpleTypeKind.Double);
  }

  @Test
  public void testEdmTypeDecimalToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmDecimal(), EdmSimpleTypeKind.Decimal);
  }

  @Test
  public void testEdmTypeGuidToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmGuid(), EdmSimpleTypeKind.Guid);
  }

  @Test
  public void testEdmTypeSingleToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmSingle(), EdmSimpleTypeKind.Single);
  }

  @Test
  public void testEdmTypeSByteToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmSByte(), EdmSimpleTypeKind.SByte);
  }

  @Test
  public void testEdmTypeByteToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmByte(), EdmSimpleTypeKind.Byte);
  }

  @Test
  public void testEdmTypeDateTimeToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmDateTime(), EdmSimpleTypeKind.DateTime);
  }

  @Test
  public void testEdmTypeDateTimeOffsetToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmDateTimeOffset(), EdmSimpleTypeKind.DateTimeOffset);
  }

  @Test
  public void testEdmTypeTimeToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmTime(), EdmSimpleTypeKind.Time);
  }

  @Test
  public void testEdmTypeNullToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmNull(), EdmSimpleTypeKind.Null);
  }

  @Test
  public void testEdmTypeBinaryToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmBinary(), EdmSimpleTypeKind.Binary);
  }

  @Test
  public void testEdmTypeBooleanToSimpleKind(){
    testEdmTypeToSimpleKind(new EdmBoolean(), EdmSimpleTypeKind.Boolean);
  }

  private void testEdmTypeToSimpleKind(EdmType edmType, EdmSimpleTypeKind expectedKind){
    SQLStatementParam sqlStatementParam = new SQLStatementParam(null, edmType, null);
    Assert.assertEquals("Unexpected EdmSimpleKind.", expectedKind, sqlStatementParam.getEdmSimpleKind());
  }

}
