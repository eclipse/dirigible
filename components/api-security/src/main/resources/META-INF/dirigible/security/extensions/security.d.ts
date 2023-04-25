declare module "@dirigible/security" {
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
