package jcn.yourorder.authgateway.auth.enitites.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDto(

        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}
