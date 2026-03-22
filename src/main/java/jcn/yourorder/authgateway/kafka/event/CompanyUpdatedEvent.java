package jcn.yourorder.authgateway.kafka.event;

import java.util.UUID;

public record CompanyUpdatedEvent(
        UUID companyId,
        UUID ownerId
) {}
