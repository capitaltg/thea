Certificate Checker
=================

Configuring certificate and certificate chains for secure communications can be challenging.  This is true for static-content web servers, Java-based REST micro-services, and numerous applications that leverage TLS for secure client communications.  Applications include reverse proxies, Jetty and Tomcat, Apache Kafka, Solr and Elasticsearch, Hadoop clusters, Splunk, Nexus or Artifactory repositories, Jenkins, load balancers, and many, many more.  Configuring clients of these services can often also be complicated because it is difficult to understand what certificates the service presents and which certificates should be added to the client's trust store (if the service uses a CA that isn't included in the OS, JRE, or runtime platform).

Certificate Checker provides an easy-to-use solution to check certificates, certificate chains, and TLS configurations.  To run Certificate Checker for publicly-accessible web sites you can go to: https://certchecker.app and enter in there a URL to check.

Users can easily run Certificate Checker in an internal network to validate or troubleshoot their TLS configuration. To run it on a local network you can run the Docker image as described below.  You can also build the application and deploy it on an existing server.

## Developing Certificate Checker

Certificate Checker is written using Groovy and Spring Boot for the back-end API and Angular 7 for the front end.

### Developing locally
After cloning the repository, you can run `./gradlew eclipse` and then import it as an eclipse project.  Before importing the `thea-ui` folder into an editor (Atom and VSCode are two good options), run `npm install` from that folder to install the necessary JavaScript libraries.

### Running Thea locally
You can run the backend Thea API service locally by running `TheaServer.java` in Eclipse or by running `./gradlew bootRun`.  To run the front-end application locally, run `ng serve --proxy-config proxy.conf.json`.  That will start a local development server and will also proxy all requests made to `/api` to the backend service, which should now be running on port 7070.

### Design
UI design components use [Bootstrap](https://getbootstrap.com) with the Flatly theme found [here](https://bootswatch.com/flatly/).

## With Docker
To get Certificate Checker up and running quickly with [Docker](https://www.docker.com/), you can run:

```
docker pull capitaltg/thea
docker run -it -p 7070:7070 capitaltg/thea
```

Then, point your browser to http://localhost:7070. This command will run Certificate Checker using an embedded h2 database.  You can also run Certificate Checker with a real database connection. For a MySQL database, you can use the following syntax: 

```
docker run -d -p 7070:7070 --env-file thea.env capitaltg/thea
```

where thea.env contains:

```
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
SPRING_DATASOURCE_URL=jdbc:mysql://some-database.us-east-1.rds.amazonaws.com:3306/thea
SPRING_DATASOURCE_USERNAME=<db-username>
SPRING_DATASOURCE_PASSWORD=<db-password>
```

Certificate Checker comes bundled with the MySQL JDBC driver.  You can add another JDBC driver to the lib folder and adjust the environment variables file accordingly.

To add additional trusted anchor CAs, you can create a new Docker image with the following Dockerfile:

```
FROM capitaltg/thea:latest
COPY some-certificate.cer /tmp/some-certificate.cer
USER root
RUN keytool -import -trustcacerts -cacerts \
     -noprompt -storepass changeit \
     -file /tmp/some-certificate.cer -alias some-alias
USER ctg
```

## Enabling OAuth Login
Certificate Checker can be configured to allow users to login via OAuth.  Users can then setup watches
on certificate chains and be alerted when they change and/or are near expiration.  To do this, set
`THEA_UI_SHOW_LOGIN=true` and configure the Spring Boot OAuth settings.  The default OAuth configuration
uses GitHub and you can enable that by setting values for:
```
SECURITY_OAUTH2_CLIENT_CLIENT_ID=<clientId>
SECURITY_OAUTH2_CLIENT_CLIENT_SECRET=<clientSecret>
```

### Auth0
You can configure Spring Boot to use Auth0 by adding these configurations in addition to the client ID
and secret for a domain called domainX:
```
SECURITY_OAUTH2_CLIENT_ACCESS_TOKEN_URI=https://domainX.auth0.com/oauth/token
SECURITY_OAUTH2_CLIENT_USER_AUTHORIZATION_URI=https://domainX.auth0.com/authorize
SECURITY_OAUTH2_CLIENT_SCOPE=openid profile email
SECURITY_OAUTH2_RESOURCE_USER_INFO_URI=https://domainX.auth0.com/userinfo
```

### Google
You can configure Spring Boot to use Google by adding these configurations in addition to the client ID
and secret:
```
SECURITY_OAUTH2_CLIENT_ACCESS_TOKEN_URI=https://www.googleapis.com/oauth2/v3/token
SECURITY_OAUTH2_CLIENT_USER_AUTHORIZATION_URI=https://accounts.google.com/o/oauth2/auth
SECURITY_OAUTH2_CLIENT_SCOPE=email
SECURITY_OAUTH2_RESOURCE_USER_INFO_URI=https://www.googleapis.com/userinfo/v2/me
```

License
-------
Certificate Checker is made available under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).
