package jcn.yourorder.authgateway.auth.enitites.dtos.request;

import jcn.yourorder.authgateway.auth.enitites.enums.UserRole;

import java.util.UUID;

public record AdminUpdateUserRequest(
        String username,
        String email,
        UUID tenantId,
        UserRole role
) {
}
