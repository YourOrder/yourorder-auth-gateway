package jcn.yourorder.authgateway.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import jcn.yourorder.authgateway.auth.repositories.UserRepository;
import jcn.yourorder.authgateway.kafka.event.CompanyCreatedEvent;
import jcn.yourorder.authgateway.kafka.event.CompanyUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyEventListener {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "company.created", groupId = "auth-service")
    public void handleCreated(String message) throws Exception {
        CompanyCreatedEvent event = objectMapper.readValue(message, CompanyCreatedEvent.class);

        log.info("📩 CompanyCreated: {}", event);

        updateTenant(event.ownerId(), event.companyId());
    }

    @KafkaListener(topics = "company.updated", groupId = "auth-service")
    public void handleUpdated(String message) throws Exception {
        CompanyUpdatedEvent event = objectMapper.readValue(message, CompanyUpdatedEvent.class);

        log.info("📩 CompanyUpdated: {}", event);

        updateTenant(event.ownerId(), event.companyId());
    }

    private void updateTenant(UUID userId, UUID tenantId) {

        userRepository.findById(userId)
                .flatMap(user -> {

                    if (tenantId.equals(user.getTenantID())) {
                        return Mono.empty();
                    }

                    user.setTenantID(tenantId);

                    return userRepository.save(user);
                })
                .doOnSuccess(u -> log.info("✅ tenantId updated for user {}", userId))
                .doOnError(e -> log.error("❌ Error updating tenant", e))
                .subscribe();
    }
}
