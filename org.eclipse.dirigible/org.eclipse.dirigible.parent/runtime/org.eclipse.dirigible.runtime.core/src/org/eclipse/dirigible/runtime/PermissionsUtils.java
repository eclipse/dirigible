package org.eclipse.dirigible.runtime;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.utils.EnvUtils;

public class PermissionsUtils {

	public static final String PERMISSION_ERR = "%s called, but the user does not have permissions to do this operation";

	public static boolean isUserInRole(HttpServletRequest request, String role) {
		if (isRolesEnabled(request)) {
			return request.isUserInRole(role);
		}
		return true;
	}

	public static Boolean isRolesEnabled(HttpServletRequest request) {
		String enableRoles = EnvUtils.getEnv(ICommonConstants.INIT_PARAM_ENABLE_ROLES);
		Boolean rolesEnabled = Boolean.parseBoolean(enableRoles);
		return rolesEnabled;
	}
}
