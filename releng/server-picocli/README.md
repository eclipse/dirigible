## Eclipse Dirigible CLI

To build the CLI module, use:

    mvn clean install

Then you can immediately test the sample javascript module 

```javascript
console.log(`Hello ${__context.get('first_name')} ${__context.get('last_name')} from Eclipse Dirigible CLI!`);
```

by executing the following command line:

    java -jar ./target/dirigible.jar -w workspace -f hello.js -c first_name=John+last_name=Smith -e
    
Usage:

    java -jar dirigible.jar [-w=<workspace>] [-f=<file>] [-c=<context>] [-t=<type>] [-e]
    Executes a Script Module
      -w, --workspace=<workspace>
      -f, --file=<file>
      -t, --type=<type>
      -c, --context=<context>
      -e, --exit
      
> All the logs are suppressed. To enable them use the configuration files `application.properties` and `logback.xml`

This CLI module contain the minimal set of Dirigible modules needed for script execution, repository, database and background jobs.