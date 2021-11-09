declare module "@dirigible/security" {
    module oauth {
        interface OAuthClient {
            /**
             * Sets the OAuth server URL.
             * @param url
             */
            setUrl(url);

            /**
             * Sets the clientId for the authentication flow.
             * @param clientId
             */

            setClientId(clientId);

            /**
             * Sets the clientSecret for the authentication flow.
             * @param grantType
             */
            setGrantType(grantType);

            /**
             * Sets the grantType of the authentication flow.
             * @param clientSecret
             */
            setClientSecret(clientSecret);

            /**
             * Gets the JWT access token.
             */
            getToken(): object;
        }
        interface OAuthConfg{
            /**
             * The OAuth server URL.Default equals null.
             */
            url:string
            /**
             * The clientId for the authentication flow.Default equals null.
             */
            clientId:string
            /**
             * The clientSecret for the authentication flow.Default equals null.
             */
            clientSecret:string
            /**
             * (Optional) The grantType of the authentication flow.
             * Default: client_credentials
             */
            grantType:string
            /**
             * (Optional) Whether to add by default /oauth/token to the URL.
             * Default: false.
             */
            isAbsoluteUrl:boolean
        }

        /**
         * Returns the JWT object.
         */
        function getToken(): object;

        /**
         * Get given property by name from the JWT.
         * @param name
         */
        function get(name): string;

        /**
         * Returns the email JWT property.
         */
        function getEmail(): string;

        /**
         * Returns the user_name JWT property.
         */
        function getUsername(): string;

        /**
         * Returns the grant_type JWT property.
         */
        function getGrantType(): string;

        /**
         * Returns true if JWT token is valid.
         * @param token
         */
        function verify(token): boolean

        /**
         * Returns OAuthClient with the specified config.
         * @param config
         */
        function getClient(config:OAuthConfg): OAuthClient;
    }
    module user {
        /**
         * Returns the name of the currently logged in user, if any or null
         */
        function getName(): string;

        /**
         * Returns true if the user has a given role and false otherwise
         * @param role
         */
        function isInRole(role:string): string;

        /**
         * Returns Timeout.
         */
        function getTimeout(): number;

        /**
         * Returns AuthType.
         */
        function getAuthType(): string;

        /**
         * Returns SecurityToken
         */
        function getSecurityToken();

        /**
         * Returns InvocationCount
         */
        function getInvocationCount();

        /**
         * Get Language
         */
        function getLanguage();

    }
}
