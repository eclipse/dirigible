package org.eclipse.dirigible.components.engine.ftp.config;

import org.apache.ftpserver.ftplet.UserManager;
import org.eclipse.dirigible.components.engine.ftp.domain.FtpUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
class FtpServerApplicationTest {

	@Autowired
	UserManager userManager;

	@Test
	void contextLoads() throws Exception {
		this.userManager.save(new FtpUser("foo", "pass", true, Collections.emptyList(), -1, null, true));
	}
	
	/**
     * The Class TestConfiguration.
     */
    @SpringBootApplication
    static class TestConfiguration {
    }

}
