package org.eclipse.dirigible.components.data.source.snowpark;

import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.manager.DataSourceInitializerContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class DataSourceInitializerContributorSnowpark implements DataSourceInitializerContributor {
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(DataSourceInitializerContributorSnowpark.class);

    @Override
    public void contribute(DataSource datasource, Properties properties) {
        if ("Snowflake".equalsIgnoreCase(datasource.getName())) {
            Map<String, String> env = System.getenv();
            try {
                String url = "jdbc:snowflake://" + env.getOrDefault("SNOWFLAKE_ACCOUNT", null) + ".snowflakecomputing.com/";
                if (Files.exists(Paths.get("/snowflake/session/token"))) {
                    properties.put("CLIENT_SESSION_KEEP_ALIVE", true);
                    properties.put("account", env.get("SNOWFLAKE_ACCOUNT"));
                    properties.put("authenticator", "OAUTH");
                    properties.put("token", new String(Files.readAllBytes(Paths.get("/snowflake/session/token"))));
                    properties.put("warehouse", env.getOrDefault("SNOWFLAKE_WAREHOUSE", null));
                    properties.put("db", env.get("SNOWFLAKE_DATABASE"));
                    properties.put("schema", env.get("SNOWFLAKE_SCHEMA"));
                    url = "jdbc:snowflake://" + env.get("SNOWFLAKE_HOST") + ":" + env.get("SNOWFLAKE_PORT");
                    properties.put("jdbcUrl", url);
                    properties.put("dataSource.url", url);
                } else {
                    properties.put("CLIENT_SESSION_KEEP_ALIVE", true);
                    properties.put("account", env.getOrDefault("SNOWFLAKE_ACCOUNT", null));
                    properties.put("user", env.getOrDefault("SNOWFLAKE_USER", null));
                    properties.put("password", env.getOrDefault("SNOWFLAKE_PASSWORD", null));
                    properties.put("warehouse", env.getOrDefault("SNOWFLAKE_WAREHOUSE", null));
                    properties.put("db", env.getOrDefault("SNOWFLAKE_DATABASE", null));
                    properties.put("schema", env.getOrDefault("SNOWFLAKE_SCHEMA", null));
                    properties.put("jdbcUrl", url);
                    properties.put("dataSource.url", url);
                }
            } catch (IOException ex) {
                logger.error("Invalid configuration for the datasource: [{}]", datasource.getName(), ex);
            }
        }
    }
}
