package monokai.config;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.flyway.autoconfigure.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

	@Bean
	public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
		return configuration -> configuration
				.locations("classpath:db/migration")
				.encoding(StandardCharsets.UTF_8)
				.table("flyway_schema_history")
				.validateOnMigrate(true)
				.baselineOnMigrate(false)
				.cleanDisabled(true);
	}
}
