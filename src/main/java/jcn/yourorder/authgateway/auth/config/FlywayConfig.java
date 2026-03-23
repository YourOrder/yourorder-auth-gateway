package jcn.yourorder.authgateway.auth.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://localhost:5432/mydb",
                        "postgres",
                        "1234"
                )
                .locations("classpath:db/migration")
                .load();
    }
}
