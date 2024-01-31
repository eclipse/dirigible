import { client as httpClient} from "@dirigible/http"
import { url } from "@dirigible/utils"

export interface OAuthClientConfig {
    readonly url: string;
    readonly clientId: string;
    readonly clientSecret: string;
    readonly grantType?: string;
}

export class OAuthClient {
    private config: OAuthClientConfig;

    constructor(config: OAuthClientConfig) {
        this.config = config;
        if (!config.grantType) {
            // @ts-ignore
            config.grantType = "client_credentials";
        }
    }

    public getToken() {
        const oauthResponse = httpClient.post(this.config.url, {
            params: [{
                name: "grant_type",
                value: this.config.grantType
            }, {
                name: "client_id",
                value: url.encode(this.config.clientId)
            }, {
                name: "client_secret",
                value: url.encode(this.config.clientSecret)
            }],
            headers: [{
                name: "Content-Type",
                value: "application/x-www-form-urlencoded"
            }]
        });
        if (oauthResponse.statusCode !== 200) {
            const errorMessage = `Error occurred while retrieving OAuth token. Status code: [${oauthResponse.status}], text: [${oauthResponse.text}]`;
            console.error(errorMessage);
            throw new Error(errorMessage);
        }
        return JSON.parse(oauthResponse.text);
    }
}