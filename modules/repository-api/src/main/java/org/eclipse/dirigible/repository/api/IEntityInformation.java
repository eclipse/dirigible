/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.api;

import java.util.Date;

/**
 * Through this interface the user can obtain information on a particular
 * repository entity.
 * <p>
 * Help methods are available in the <code>
 * PermissionsHelper</code> class.
 * 
 */
public interface IEntityInformation {

	/**
	 * This flag indicates that a permission status for this entity could not be
	 * evaluated.
	 */
	public static final int PERMISSION_UNKNOWN = 1 << 31;

	/**
	 * This flag indicates that no one has any rights to this entity.
	 */
	public static final int PERMISSION_NONE = 0;

	/**
	 * This flag indicates that the owner of this entity is allowed to read its
	 * content.
	 */
	public static final int PERMISSION_OWNER_READ = 1 << 8;

	/**
	 * This flag indicates that the owner of this entity is allowed to write or
	 * delete it.
	 */
	public static final int PERMISSION_OWNER_WRITE = 1 << 7;

	/**
	 * This flag indicates that the owner of this entity is allowed to update
	 * its content.
	 */
	public static final int PERMISSION_OWNER_UPDATE = 1 << 6;

	/**
	 * This flag indicates that users, part of the owner's group, are allowed to
	 * read this entity.
	 */
	public static final int PERMISSION_GROUP_READ = 1 << 5;

	/**
	 * This flag indicates that users, part of the owner's group, are allowed to
	 * write or delete this entity.
	 */
	public static final int PERMISSION_GROUP_WRITE = 1 << 4;

	/**
	 * This flag indicates that users, part of the owner's group, are allowed to
	 * update the content of this entity.
	 */
	public static final int PERMISSION_GROUP_UPDATE = 1 << 3;

	/**
	 * This flag indicates that all other users are allowed to read this entity.
	 */
	public static final int PERMISSION_OTHERS_READ = 1 << 2;

	/**
	 * This flag indicates that all other users are allowed to write or delete
	 * this entity.
	 */
	public static final int PERMISSION_OTHERS_WRITE = 1 << 1;

	/**
	 * This flag indicates that all other users are allowed to update this
	 * entity's contents.
	 */
	public static final int PERMISSION_OTHERS_UPDATE = 1 << 0;

	/**
	 * Returns the name of the entity.
	 * <p>
	 * If the implementation cannot find the requested information, then a value
	 * of <code>
	 * null</code> is returned.
	 */
	public String getName();

	/**
	 * Returns the path of the entity within the repository.
	 * <p>
	 * If the implementation cannot find the requested information, then a value
	 * of <code>
	 * null</code> is returned.
	 */
	public String getPath();

	/**
	 * Returns a flag mask indicating the permissions of this entity. The result
	 * is a set of permission flags or-ed together.
	 * <p>
	 * If the implementation cannot find the requested information, then a value
	 * of {@link #PERMISSION_UNKNOWN} is returned.
	 * 
	 * @see #PERMISSION_OWNER_READ
	 * @see #PERMISSION_OWNER_WRITE
	 * @see #PERMISSION_OWNER_UPDATE
	 * @see #PERMISSION_GROUP_READ
	 * @see #PERMISSION_GROUP_WRITE
	 * @see #PERMISSION_GROUP_UPDATE
	 * @see #PERMISSION_OTHERS_READ
	 * @see #PERMISSION_OTHERS_WRITE
	 * @see #PERMISSION_OTHERS_UPDATE
	 */
	public int getPermissions();

	/**
	 * Returns the size of this resource.
	 * <p>
	 * If the implementation cannot find the requested information, then a value
	 * of <code>
	 * null</code> is returned.
	 */
	public Long getSize();

	/**
	 * The creator of the entity
	 * 
	 * @return
	 */
	public String getCreatedBy();

	/**
	 * Timestamp of the creation of the entity
	 * 
	 * @return
	 */
	public Date getCreatedAt();

	/**
	 * The last modifier of the entity
	 * 
	 * @return
	 */

	public String getModifiedBy();

	/**
	 * Timestamp of the last modification of the entity
	 * 
	 * @return
	 */
	public Date getModifiedAt();

}
