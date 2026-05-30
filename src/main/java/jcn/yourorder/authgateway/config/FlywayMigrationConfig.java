package jcn.yourorder.authgateway.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FlywayMigrationConfig {

    @Bean
    ApplicationRunner flywayMigrationRunner(Environment environment) {
        return args -> {
            boolean enabled = environment.getProperty("spring.flyway.enabled", Boolean.class, true);
            if (!enabled) {
                return;
            }

            String url = environment.getRequiredProperty("spring.flyway.url");
            String user = environment.getRequiredProperty("spring.flyway.user");
            String password = environment.getProperty("spring.flyway.password", "");
            String[] locations = environment.getProperty(
                    "spring.flyway.locations",
                    "classpath:db/migration"
            ).split(",");
            boolean baselineOnMigrate = environment.getProperty(
                    "spring.flyway.baseline-on-migrate",
                    Boolean.class,
                    false
            );

            Flyway.configure()
                    .dataSource(url, user, password)
                    .locations(locations)
                    .baselineOnMigrate(baselineOnMigrate)
                    .load()
                    .migrate();
        };
    }
}
