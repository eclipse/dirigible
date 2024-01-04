# Eclipse Dirigible™

[![Build Status](https://github.com/eclipse/dirigible/workflows/Build/badge.svg)](https://github.com/eclipse/dirigible/actions?query=workflow%3ABuild)
[![Eclipse License](https://img.shields.io/badge/License-EPL%202.0-brightgreen.svg)](https://github.com/eclipse/dirigible/blob/master/LICENSE)
[![Download Dirigible](https://img.shields.io/badge/download-releases-green.svg)](http://download.dirigible.io/)
[![Artifact HUB](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/eclipse-dirigible)](https://artifacthub.io/packages/search?org=dirigiblelabs)
[![Maven Central](https://img.shields.io/maven-central/v/org.eclipse.dirigible/dirigible-server-all.svg)](https://search.maven.org/#search%7Cga%7C1%7Corg.eclipse.dirigible)
[![GitHub contributors](https://img.shields.io/github/contributors/eclipse/dirigible.svg)](https://github.com/eclipse/dirigible/graphs/contributors)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Feclipse%2Fdirigible.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Feclipse%2Fdirigible?ref=badge_shield)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/1967/badge)](https://bestpractices.coreinfrastructure.org/projects/1967)
[![REUSE status](https://api.reuse.software/badge/github.com/eclipse/dirigible)](https://api.reuse.software/info/github.com/eclipse/dirigible)


**Eclipse Dirigible** is a High-Productivity Application Platform as a Service (hpaPaaS). It provides an application server consisting of pre-selected execution engines and built-in web development tools. It is suitable for rapid development of business applications by also leveraging the Low Code / No Code techniques.

<p align="center">
  <img src="https://github.com/eclipse/dirigible/blob/master/logo/dirigible-logo-2Kx2K.png" width="40%" alt="dirigible logo"/>
</p>

> Enjoy Programming Like Never Before

From the end user's perspective (developer), Dirigible runs directly in the browser, therefore does not require any downloads or installations.

From the service provider's perspective (PaaS/SaaS), Dirigible packs all required components in a self-contained software bundle that can be deployed on a VM or Docker capable environment such as Kubernetes.

Dirigible supports access to RDBMS via JDBC. Currently supported versions for RDBMS are PostgreSQL, HANA, Sybase ASE, MySQL, H2, and Derby as well as NoSQL like MongoDB or pure OLAP like Snowflake.

Dirigible promotes the In-System Programming development model, where you make dynamic alteration of the live system. To provide the self-contained bundle serving all the needed features for a business application, Dirigible packs various engines such as ActiveMQ, Quartz, Lucene, Flowable, Mylyn, GraalJS and others.

The project started as an internal SAP initiative to address the extension and adoption use-cases related to SOA and Enterprise Services.

- [Try](#trial)
- [Get Started](#get-started)
	- [Download](#download)
	- [Build](#build)
	- [Run](#run)
		- [Standalone](#standalone)
		- [Docker](#docker)
                - [Native image](#native-image)
  	- [Code formatting](#code-formatting)	 
- [Additional Information](#additional-information)
	- [License](#license)
	- [Contributors](#contributors)
	- [References](#references)

## Instant Trial

You can try the sandbox instance to have a quick look on the functionality you are interested [https://trial.dirigible.io](https://trial.dirigible.io).

## Contact Us

Join the Eclipse Dirigible Slack Workspace to chat with the community: [https://slack.dirigible.io](https://slack.dirigible.io)

## Get Started

### Download

The "fast-track" - you can download the precompiled binaries produced by the GitHub Actions from [http://download.dirigible.io/](http://download.dirigible.io/) and skip the build section.

Nevertheless, we highly recommend building the binaries from source in order to have all experimental features that are not available in the releases.

### Build

##### Prerequisites

- [Git](http://git-scm.com/)
- [Java JDK 11+](https://adoptopenjdk.net/)
- [Maven 3.5.x](http://maven.apache.org/docs/3.5.3/release-notes.html)
- [esbuild](https://esbuild.github.io/getting-started/#install-esbuild)  - `npm i -g esbuild`
- [tsc](https://www.npmjs.com/package/typescript) - `npm i -g typescript`


##### Steps

1. Clone the [project repository - master branch](https://github.com/eclipse/dirigible/tree/master) or [download the latest sources](https://github.com/eclipse/dirigible/archive/master.zip).

  In case there is an issue with 'Filename too long in Git for Windows' then add the fllowing git confoguration
```
git config --system core.longpaths true
```

3. Go to the root folder.
4. Build the project with:

        mvn clean install

   > If you are using Windows, make sure that you open the terminal as Administrator otherwise the tests will fail

 - Quick build with tests:

        mvn -T 1C clean install -D maven.javadoc.skip=true -D license.skip=true

 - If you don't want to trigger license updates:

        mvn clean install -D license.skip=true

 - If you have a multi-core system, enable threads:

        mvn -T 1C clean install

 - If you don't need to run tests, you can add the following argument:

        mvn clean install -D skipTests

 - If you don't need to compile and run tests:

        mvn clean install -D maven.test.skip=true -D skipTests

 - If you want to do a fast build, with no tests, javadocs and license updates:

        mvn -T 1C clean install -D maven.test.skip=true -D skipTests -D maven.javadoc.skip=true -D license.skip=true

> The build should pass successfully.

The produced `dirigible-application-XXX-executable.jar` file is in `build/application/target/` and is ready to be deployed. It is Spring Boot application, so it can be executed locally right away.

### Run

#### Standalone

##### Prerequisites

**macOS:**

        brew install ttyd

**Linux:**

Linux support is built-in

More info about **ttyd** can be found at: [ttyd](https://github.com/tsl0922/ttyd)

##### Steps

1. From the project root directory run command:

        java -jar build/application/target/dirigible-application-*-executable.jar

   > for Windows
   
        java -jar build/application/target/$((Get-ChildItem dirigible-application-*-executable.jar -recurse -File | Sort-Object LastWriteTime | Select -Last 1).BaseName).jar

3. In case you want to debug the application run:

        java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 -jar build/application/target/dirigible-application-*-executable.jar

4. Open a web browser and go to: [http://localhost:8080](http://localhost:8080 "http://localhost:8080")
5. Login with user: `admin` and password `admin`
6. REST API description in an OpenAPI format can be found at: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html "http://localhost:8080/swagger-ui/index.html")

#### Docker

##### Prerequisites

- [Install Docker](https://docs.docker.com/engine/installation/)

##### Steps

1. Get the container

- Pull the official image from Docker Hub:

        docker pull dirigiblelabs/dirigible:latest

- Build it locally

        cd build/application
        docker build -t dirigiblelabs/dirigible:latest -f Dockerfile .
   > prerequisite: build the project as described in step 4 [here](https://github.com/eclipse/dirigible/blob/master/README.md#steps)

2. Start the container

        docker run --name dirigible --rm -p 8080:8080 -p 8081:8081 dirigiblelabs/dirigible:latest

3. Open a web browser and go to: [http://localhost:8080](http://localhost:8080 "http://localhost:8080")

4. Optionally you can enhance and customize the Dockerfile from [here](https://github.com/eclipse/dirigible/blob/master/build/application/Dockerfile)

#### Native image
##### Prerequisites
- [Install SDKMAN](https://sdkman.io)
- Install GraalVM: `sdk install java 21.0.1-graal`
- Make sure that `JAVA_HOME` and `PATH` env variables point to your graalvm jdk from the previous step

##### Steps
- Execute the following on your github repo root folder
```
# build dirigible fat jar
mvn -T 1C clean install -D maven.test.skip=true -D skipTests -D maven.javadoc.skip=true -D license.skip=true

rm -rf dirigible

# build native image
native-image -jar 'build/application/target/dirigible-application-10.0.0-SNAPSHOT-executable.jar' -o dirigible

# start dirigible
./dirigible
```

#### PostgreSQL

##### Steps

1. Install PostgreSQL e.g. for MacOS:

        brew install postgresql

   Alternatively you can use docker image

        docker run -itd -e POSTGRES_DB=postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres  -p 5432:5432 --name postgresql postgres
    
2. The run it:

        brew services start postgresql
    
3. Create a default user:

        createuser -s postgres
    
4. And expose the following environment variables:

        export DIRIGIBLE_DATASOURCE_DEFAULT_DRIVER=org.postgresql.Driver
        export DIRIGIBLE_DATASOURCE_DEFAULT_URL=jdbc:postgresql://localhost:5432/postgres
        export DIRIGIBLE_DATASOURCE_DEFAULT_USERNAME=postgres
        export DIRIGIBLE_DATASOURCE_DEFAULT_PASSWORD=postgres

   for Windows execute the following in the terminal with admin privileges
	```
	[System.Environment]::SetEnvironmentVariable('DIRIGIBLE_DATASOURCE_DEFAULT_DRIVER','org.postgresql.Driver', 'Machine')
	[System.Environment]::SetEnvironmentVariable('DIRIGIBLE_DATASOURCE_DEFAULT_URL','jdbc:postgresql://localhost:5432/postgres', 'Machine')
	[System.Environment]::SetEnvironmentVariable('DIRIGIBLE_DATASOURCE_DEFAULT_USERNAME','postgres', 'Machine')
	[System.Environment]::SetEnvironmentVariable('DIRIGIBLE_DATASOURCE_DEFAULT_PASSWORD','postgres', 'Machine')
	```

5. Then you can run Dirigible with PostgreSQL default database (DefaultDB).
   
   > If you have started the Dirigible before, make sure to execute `mvn clean` before starting the Dirigible with the PostgreSQL

### Code formatting
In order to contribute to the project, you need to configure your java code formatter.
Please follow the steps bellow
#### Eclipse
1. Window -> Preferences -> Java -> Code Style -> Formatter -> Import -> Select [this](https://github.com/eclipse/dirigible/blob/master/dirigible-formatter.xml) formatter
![image](https://github.com/eclipse/dirigible/assets/5058839/275463e4-5795-423c-bc29-e2cfdae42630)

2. Window -> Preferences -> Java -> Editor -> Save Actions -> Check `Perform the selected actions on save` -> Check `Format source code` -> Select -> `Format all lines`
#### IntelliJ
1. File (or IntelliJ IDEA if on MacOS) -> Settings -> Editor -> Code Style -> Java -> Scheme -> Import Scheme (from the settings button) -> Eclipse XML Profile -> Select [this](https://github.com/eclipse/dirigible/blob/master/dirigible-formatter.xml) formatter -> Copy to Project (from the settings button)
![image](https://github.com/eclipse/dirigible/assets/5058839/bed1ab0a-b572-47e5-9e79-31d2644c4380)

2.  File -> Settings -> Tools -> Actions on Save -> Check `Reformat code` 
#### Visual Studio Code
1. Install the extension [Language Support for Java(TM) by Red Hat](https://marketplace.visualstudio.com/items?itemName=redhat.java)
2. File -> Preferences -> Settings -> java.format.settings.url: Set URL (or local file path) pointing to Eclipse Formatter Profile file.

#### Maven
To format the code using Maven execute the following in the root dir of the project

	mvn formatter:format

### CMS with AWS S3

Eclipse Dirigible's Document perspective can be used with [AWS S3](https://aws.amazon.com/s3/), providing you CMS with S3's cloud storage.

#### Setup:
- AWS account with generated credentials (AWS Acces Key, AWS Secret Key)
- Set up the environment variables regarding CMS/CMS S3 exposed by Eclipse Dirigible. Full list can be found [here](https://www.dirigible.io/help/setup/setup-environment-variables/#cms).

#### Usage:
- Navigate to the `Document` perspective in your Eclipse Dirigible instance
- Upload a file and you should see the file in the perspective as well as in the S3 bucket

#### Test environment with LocalStack
If you prefer working with a test environment you can use [LocalStack](https://www.localstack.cloud/).

#### Setup:
- Install LocalStack on your machine using the installation guide [here](https://docs.localstack.cloud/getting-started/installation/) or the following commands:
  - MacOS - `brew install localstack/tap/localstack-cli`
  - Linux -
  
      For `x86-64`:
      ```
       curl -Lo localstack-cli-3.0.2-linux-amd64-onefile.tar.gz \ 
        https://github.com/localstack/localstack-cli/releases/download/v3.0.2/localstack-cli-3.0.2-linux-amd64-onefile.tar.gz
      ```
      For `ARM64`:
      ```
      curl -Lo localstack-cli-3.0.2-linux-arm64-onefile.tar.gz \
        https://github.com/localstack/localstack-cli/releases/download/v3.0.2/localstack-cli-3.0.2-linux-arm64-onefile.tar.gz
      ```
    
    Then extract the LocalStack CLI from the terminal:
    
    ```
    sudo tar xvzf localstack-cli-3.0.2-linux-*-onefile.tar.gz -C /usr/local/bin
    ```
- Start `LocalStack` using - `localstack start -d`
- Set up the environment variables, mainly `DIRIGIBLE_S3_PROVIDER=localstack`, exposed by Eclipse Dirigible


## Additional Information

### License

This project is copyrighted by [SAP SE](http://www.sap.com/) or an SAP affiliate company and Eclipse Dirigible contributors and is available under the [Eclipse Public License v 2.0](https://www.eclipse.org/legal/epl-v20.html). See [LICENSE.txt](LICENSE.txt) and [NOTICE.txt](NOTICE.txt) for further details.


[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Feclipse%2Fdirigible.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Feclipse%2Fdirigible?ref=badge_large)

### Contributors

If you like to contribute to Dirigible, please read the [Contributor's guide](CONTRIBUTING.md).

### Attribution links

Unicons by IconScout: [https://github.com/Iconscout/unicons](https://github.com/Iconscout/unicons)

### References

- Project Home: [https://www.dirigible.io](https://www.dirigible.io)
- Help Portal: [https://help.dirigible.io](https://www.dirigible.io/help)
- Samples: [https://samples.dirigible.io](https://samples.dirigible.io)
- Trial Instance: [https://trial.dirigible.io](https://trial.dirigible.io)
- Slack: [https://slack.dirigible.io](https://slack.dirigible.io)
- Mailing List: [https://dev.eclipse.org/mailman/listinfo/dirigible-dev](https://dev.eclipse.org/mailman/listinfo/dirigible-dev)
- Issues: [https://github.com/eclipse/dirigible/issues](https://github.com/eclipse/dirigible/issues)
- Eclipse Foundation Help Desk: https://gitlab.eclipse.org/eclipsefdn/helpdesk

