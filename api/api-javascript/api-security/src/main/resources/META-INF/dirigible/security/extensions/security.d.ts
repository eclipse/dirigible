declare module "@dirigible/security" {
    module oauth {
        interface OAuthClient {
            setUrl(url);

            setClientId(clientId);

            setGrantType(grantType);

            setClientSecret(clientSecret);

            getToken(): JSON;
        }

        function getToken(): JSON;

        function get(name): JSON;

        function getEmail(): string;

        function getUsername(): string;

        function getGrantType(): string;

        function verify(token): boolean

        function getClient(config): OAuthClient;
    }
    module user {
        function getName(): string;

        function isInRole(role): string;

        function getTimeout(): number;

        function getAuthType(): string;

        function getSecurityToken();

        function getInvocationCount();

        function getLanguage();

    }
}