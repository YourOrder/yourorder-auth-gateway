package jcn.yourorder.authgateway.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic companyCreatedTopic() {
        return new NewTopic("company-created", 1, (short) 1);
    }
}
