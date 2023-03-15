## Spring Boot based Server

This is stil an experimental feature to replace older frameworks to recent Spring Boot components.

#### Build

	cd components
	mvn clean install

If you want to do a fast build (for testing purposes):

	mvn -T 1C clean install -Dmaven.test.skip=true -DskipTests -Dmaven.javadoc.skip=true

#### Run

	java -jar build/application/target/dirigible-application-8.0.0-SNAPSHOT.jar

#### Debug

	java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 -jar build/application/target/dirigible-application-8.0.0-SNAPSHOT.jar
	
#### REST API

	http://localhost:8080/swagger-ui/index.html
	
#### Terminal

**macOS:**

```
brew install ttyd
```

**Linux:**

Linux support is built-in

More info about **ttyd** can be found at: [ttyd](https://github.com/tsl0922/ttyd)
