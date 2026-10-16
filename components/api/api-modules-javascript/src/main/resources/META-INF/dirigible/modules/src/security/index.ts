/**
 * @module security/index
 * @package @aerokit/sdk/security
 * @overview
 *
 * Provides security helpers for user authentication, OAuth flows, and metadata decorators.
 * It exposes user management utilities, OAuth integration, and useful security decorators
 * for protected route handling and role-based access control.
 *
 * The main components of this module are:
 * - User: User identity and authentication utilities.
 * - Tenants: The tenants the user holds a role in, and runs across them.
 * - OAuth: OAuth2 integration helpers for authentication flows.
 * - Decorators: Security decorators for endpoint access control.
 */

export * from "./user";
export { User as user } from "./user";
export * from "./tenants";
export * from "./oauth";
export * from "./decorators";
export * as decorators from "./decorators";
