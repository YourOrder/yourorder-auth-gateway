package jcn.yourorder.authgateway.auth.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;

public class KafkaTopicConfig {

    @Bean
    public NewTopic companyCreatedTopic() {
        return new NewTopic("company-created", 1, (short) 1);
    }
}
