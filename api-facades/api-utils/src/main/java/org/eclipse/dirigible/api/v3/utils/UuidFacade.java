package org.eclipse.dirigible.api.v3.utils;

import java.util.UUID;

public class UuidFacade {

	public static final String random() {
		return UUID.randomUUID().toString();
	}

	public static final boolean validate(String uuid) {
		try {
			UUID.fromString(uuid);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

}
