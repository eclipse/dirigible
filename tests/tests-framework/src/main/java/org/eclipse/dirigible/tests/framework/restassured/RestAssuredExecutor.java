package org.eclipse.dirigible.tests.framework.restassured;

import io.restassured.RestAssured;
import io.restassured.authentication.AuthenticationScheme;
import org.eclipse.dirigible.tests.framework.DirigibleTestTenant;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class RestAssuredExecutor {

    private final int port;

    public RestAssuredExecutor(@LocalServerPort int port) {
        this.port = port;
    }

    /**
     * Execute REST Assured validation in tenant scope.
     *
     * @param tenant tenant
     * @param callable rest assured validation
     */
    public void execute(DirigibleTestTenant tenant, CallableNoResultAndNoException callable) {
        String configuredBaseURI = RestAssured.baseURI;
        int configuredPort = RestAssured.port;
        AuthenticationScheme configuredAuthentication = RestAssured.authentication;
        try {
            RestAssured.baseURI = "http://" + tenant.getSubdomain() + ".localhost";
            RestAssured.port = port;

            RestAssured.authentication = RestAssured.preemptive()
                                                    .basic(tenant.getUsername(), tenant.getPassword());

            callable.call();
        } finally {
            RestAssured.baseURI = configuredBaseURI;
            RestAssured.port = configuredPort;
            RestAssured.authentication = configuredAuthentication;
        }
    }
}
