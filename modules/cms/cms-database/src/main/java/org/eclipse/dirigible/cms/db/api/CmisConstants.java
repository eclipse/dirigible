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
package org.eclipse.dirigible.cms.db.api;

public interface CmisConstants {

	// ---- Base ----
	public static final String NAME = "cmis:name";
	public static final String OBJECT_ID = "cmis:objectId";
	public static final String OBJECT_TYPE_ID = "cmis:objectTypeId";
	public static final String BASE_TYPE_ID = "cmis:baseTypeId";
	public static final String CREATED_BY = "cmis:createdBy";
	public static final String CREATION_DATE = "cmis:creationDate";
	public static final String LAST_MODIFIED_BY = "cmis:lastModifiedBy";
	public static final String LAST_MODIFICATION_DATE = "cmis:lastModificationDate";
	public static final String CHANGE_TOKEN = "cmis:changeToken";

	// ---- Document ----
	public static final String IS_IMMUTABLE = "cmis:isImmutable";
	public static final String IS_LATEST_VERSION = "cmis:isLatestVersion";
	public static final String IS_MAJOR_VERSION = "cmis:isMajorVersion";
	public static final String IS_LATEST_MAJOR_VERSION = "cmis:isLatestMajorVersion";
	public static final String VERSION_LABEL = "cmis:versionLabel";
	public static final String VERSION_SERIES_ID = "cmis:versionSeriesId";
	public static final String IS_VERSION_SERIES_CHECKED_OUT = "cmis:isVersionSeriesCheckedOut";
	public static final String VERSION_SERIES_CHECKED_OUT_BY = "cmis:versionSeriesCheckedOutBy";
	public static final String VERSION_SERIES_CHECKED_OUT_ID = "cmis:versionSeriesCheckedOutId";
	public static final String CHECKIN_COMMENT = "cmis:checkinComment";
	public static final String CONTENT_STREAM_LENGTH = "cmis:contentStreamLength";
	public static final String CONTENT_STREAM_MIME_TYPE = "cmis:contentStreamMimeType";
	public static final String CONTENT_STREAM_FILE_NAME = "cmis:contentStreamFileName";
	public static final String CONTENT_STREAM_ID = "cmis:contentStreamId";

	// ---- Folder ----
	public static final String PARENT_ID = "cmis:parentId";
	public static final String ALLOWED_CHILD_OBJECT_TYPE_IDS = "cmis:allowedChildObjectTypeIds";
	public static final String PATH = "cmis:path";

	// ---- Relationship ----
	public static final String SOURCE_ID = "cmis:sourceId";
	public static final String TARGET_ID = "cmis:targetId";

	// ---- Policy ----
	public static final String POLICY_TEXT = "cmis:policyText";

	// ---- Versioning States ----
	public static final String VERSIONING_STATE_NONE = "none";
	public static final String VERSIONING_STATE_MAJOR = "major";
	public static final String VERSIONING_STATE_MINOR = "minor";
	public static final String VERSIONING_STATE_CHECKEDOUT = "checkedout";

	// ---- Object Types ----
	public static final String OBJECT_TYPE_DOCUMENT = "cmis:document";
	public static final String OBJECT_TYPE_FOLDER = "cmis:folder";
	public static final String OBJECT_TYPE_RELATIONSHIP = "cmis:relationship";
	public static final String OBJECT_TYPE_POLICY = "cmis:policy";
	public static final String OBJECT_TYPE_ITEM = "cmis:item";
	public static final String OBJECT_TYPE_SECONDARY = "cmis:secondary";

}
