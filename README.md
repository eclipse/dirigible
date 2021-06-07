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


**Eclipse Dirigible** is a High-Productivity Application Platform as a Service (hpaPaaS). It provides an application server consisting of pre-selected execution engines and built-in development tools as WebIDE. It is suitable for rapid development of business applications by also leveraging the Low Code / No Code techniques.

<p align="center">
  <img src="https://github.com/eclipse/dirigible/blob/master/logo/dirigible-logo-symbol.png" width="60%" alt="dirigible logo"/>
</p>

> Enjoy Programming Like Never Before

From the end user's perspective (developer), Dirigible runs directly in the browser, therefore does not require any downloads or installations.

From the service provider's perspective (PaaS/SaaS), Dirigible packs all required components in a self-contained software bundle that can be deployed in any Java-based web server, such as Tomcat, Jetty, JBoss.

Dirigible supports access to RDBMS via JDBC. Currently supported versions for RDBMS are HANA, MaxDB, Sybase ASE, PostgreSQL, MySQL, H2, and Derby.

Dirigible promotes the In-System Programming development model, where you make dynamic alteration of the live system. To provide the self-contained bundle serving all the needed features for a business application, Dirigible packs various engines such as ActiveMQ, Quartz, Lucene, Flowable, Mylyn, Rhino, V8 and others.

The project started as an internal SAP initiative to address the extension and adoption use-cases related to SOA and Enterprise Services.

- [Try](#trial)
- [Get Started](#get-started)
	- [Download](#download)
	- [Build](#build)
	- [Run](#run)
		- [Standalone](#standalone)
		- [Docker](#docker)
- [Additional Information](#additional-information)
	- [License](#license)
	- [Contributors](#contributors)
	- [References](#references)

## Instant Trial

You can try the sandbox instance to have a quick look on the functionality you are interested [http://trial.dirigible.io](http://trial.dirigible.io).

## Get Started

### Download

The "fast-track" - you can download the precompiled binaries produced by the GitHub Actions from [http://download.dirigible.io/](http://download.dirigible.io/) and skip the build section.

Nevertheless, we highly recommend building the binaries from source in order to have all experimental features that are not available in the releases.

### Build

##### Prerequisites

- [Git](http://git-scm.com/)
- [Java JDK 11+](https://adoptopenjdk.net/)
- [Maven 3.5.x](http://maven.apache.org/docs/3.5.3/release-notes.html)


##### Steps

1. Clone the [project repository - master branch](https://github.com/eclipse/dirigible/tree/master) or [download the latest sources](https://github.com/eclipse/dirigible/archive/master.zip).
2. Go to the root folder.
3. Build the project with:

        mvn clean install

 - If you have a multi-core system, enable threads:

        mvn -T 1C clean install

 - If you don't need to run tests, you can add the following argument:

        mvn clean install -DskipTests

 - If you don't need to compile and run tests:

        mvn clean install -Dmaven.test.skip=true -DskipTests

 - If you want to do a fast build, with no tests and javadocs:

        mvn -T 1C clean install -Dmaven.test.skip=true -DskipTests -Dmaven.javadoc.skip=true

The build should pass successfully. The produced `ROOT.war` files are in `releng/<artifact-name>/target/` and are ready to be deployed. There are separate deployable artifacts (WAR files) depending on the usage type. If you are running it locally, then you need `releng/desktop-all/target/ROOT.war`. There is also an executable JAR file under the `releng/<artifact-name>/target` folder with a name like `ROOT.jar`.


### Run

#### Standalone

##### Prerequisites

- [Tomcat 8.5.6](https://archive.apache.org/dist/tomcat/tomcat-8/v8.5.6/bin/)

The Java Web Application Archive (WAR) files can be deployed on [Apache Tomcat](http://tomcat.apache.org/) web container. In this case the built-in H2 database is used.

More information about how to deploy on Apache Tomcat can be found [here](http://tomcat.apache.org/tomcat-8.0-doc/appdev/deployment.html).

**macOS:**

```
brew install ttyd
```

**Linux:**

Linux support is built-in

More info about **ttyd** can be found at: [ttyd](https://github.com/tsl0922/ttyd)

##### Steps

1. Go to Tomcats home directory.

- If you installed tomcat using brew, you can see the path assigned to `CATALINA_HOME` by executing:

        catalina home

2. Go to the `webapps` directory and delete its content.
3. Copy the `ROOT.war` file and paste it in `webapps`.
4. Go to the `conf` directory, open `tomcat-users.xml` and configure the users store:

        <tomcat-users>
                <role rolename="Developer"/>
                <role rolename="Operator"/>
                <role rolename="Everyone"/>
                <user username="dirigible" password="dirigible" roles="Developer,Operator,Everyone"/>
        </tomcat-users>

5. Run Tomcat.
- If you have installed tomcat using brew:

        catalina run

- If you downloaded Tomcat manually, return to the base directory and run:

        sh bin/catalina.sh run

6. Open a web browser and go to:

        http://localhost:8080

7. Login with dirigible/dirigible.


#### Docker

##### Prerequisites

- [Install Docker](https://docs.docker.com/engine/installation/)

##### Steps

1. Get the container

- Pull the official image from Docker Hub:

        docker pull dirigiblelabs/dirigible-trial

- Build it locally

        cd releng
        docker build -t dirigible-trail -f Dockerfile-tomcat .

2. Start the container

        docker run -p 8080:8080 -p 8081:8081 dirigiblelabs/dirigible-trial <&- &

3. Open a web browser and go to:

        http://localhost:8080/

4. Optionally you can enhance and customize the Dockerfile from [here](https://github.com/eclipse/dirigible/blob/master/org.eclipse.dirigible/org.eclipse.dirigible.parent/releng/docker/)

## Additional Information

### License

This project is copyrighted by [SAP SE](http://www.sap.com/) and is available under the [Eclipse Public License v 1.0](https://www.eclipse.org/legal/epl-v20.html). See [LICENSE.txt](LICENSE.txt) and [NOTICE.txt](NOTICE.txt) for further details.


[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Feclipse%2Fdirigible.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Feclipse%2Fdirigible?ref=badge_large)

### Contributors

If you like to contribute to Dirigible, please read the [Contributor's guide](CONTRIBUTING.md).

### References

- Project Home
[http://www.dirigible.io](http://www.dirigible.io)

- Help Portal
[http://help.dirigible.io](http://www.dirigible.io/help)

- Samples
[http://samples.dirigible.io](http://samples.dirigible.io)

- Trial Instance
[http://trial.dirigible.io](http://trial.dirigible.io) or [http://dirigible.eclipse.org](http://dirigible.eclipse.org)

- Mailing List
[https://dev.eclipse.org/mailman/listinfo/dirigible-dev](https://dev.eclipse.org/mailman/listinfo/dirigible-dev)

- Issues
[https://github.com/eclipse/dirigible/issues](https://github.com/eclipse/dirigible/issues)