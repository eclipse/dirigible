## Eclipse Dirigible integration with Eclipse Vert.x

To build the Vert.x based distribution, use:

    mvn clean install
    
Then run the Vert.x server by the command:

    java -jar ./target/dirigible-server-vertx-<VERSION>-fat.jar

> You can set the environment variables `DIRIGIBLE_VERTX_PORT` and `DIRIGIBLE_REGISTRY_IMPORT_WORKSPACE` to [configure](https://www.dirigible.io/help/setup/setup-environment-variables/) the server.

The sample that you can find within the project itself:

```javascript
let request = __context.get('vertx.request');
let response = __context.get('vertx.response');

let message = `Hello from Eclipse Dirigible's Eclipse Vert.x server called by HTTP method ${request.method()}!`;

// write to response stream
response.write(message);

// write to system output
console.log(message);
```

Can be tested by typing the following URL in the browser:

    http://localhost:8888/services/v4/js/hello.js

> All the logs are suppressed. To enable them use the configuration file `logback.xml`

This Vert.x module contain the minimal set of Dirigible modules needed for script execution, repository, database and background jobs.