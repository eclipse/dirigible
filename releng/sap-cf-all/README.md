## Docker build:

1. **dirigible-base-platform-sap-cf**
    ```
    docker build -t dirigible-base-platform-sap-cf -f Dockerfile-base .
    docker tag dirigible-base-platform-sap-cf dirigiblelabs/dirigible-base-platform-sap-cf
    docker push dirigiblelabs/dirigible-base-platform-sap-cf
    ```

1. **dirigible-sap-cf**
    ```
    docker build -t dirigible-sap-cf .
    docker tag dirigible-sap-cf dirigiblelabs/dirigible-sap-cf
    docker push dirigiblelabs/dirigible-sap-cf
    ```

# TODO: Delete me
https://developers.sap.com/tutorials/s4sdk-secure-cloudfoundry.html

```
$ cf push dirigible --docker-image dirigiblelabs/dirigible-anonymous -m 1G
$ cf restart dirigible
$ cf delete dirigible

$ cf create-service xsuaa application dirigible-xsuaa -c xs-security.json
$ cf delete-service dirigible-xsuaa

$ cf bind-service approuter dirigible-xsuaa
$ cf restart approuter
$ cf unbind-service approuter dirigible-xsuaa

$ cf bind-service dirigible dirigible-xsuaa
$ cf restart dirigible
$ cf unbind-service dirigible dirigible-xsuaa
```


```
.factory('httpRequestInterceptor', function () {
	var csrfToken = null;
	return {
		request: function (config) {
			config.headers['X-Requested-With'] = 'Fetch';
			config.headers['X-CSRF-Token'] = csrfToken ? csrfToken : 'Fetch';
			return config;
		},
		response: function(response) {
			var token = response.headers()['x-csrf-token']
			if (token) {
				csrfToken = token;
			}
			return response;
		}
	};
})
```