package jcn.yourorder.authgateway.kafka.event;

import java.util.UUID;

public record CompanyCreatedEvent(
        UUID companyId,
        UUID ownerId
) {}
