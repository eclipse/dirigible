## Spring Boot based Server

This is stil an experimental feature to replace older frameworks to recent Spring Boot components.

#### Build

	cd components
	mvn clean install
	
#### Run

	java -jar app-all/target/dirigible-application-all-8.0.0-SNAPSHOT.jar

#### Debug

	java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 -jar app-all/target/dirigible-application-all-8.0.0-SNAPSHOT.jar
	
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
