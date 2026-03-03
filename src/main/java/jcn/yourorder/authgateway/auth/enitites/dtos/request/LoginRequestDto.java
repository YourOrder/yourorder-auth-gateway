package jcn.yourorder.authgateway.auth.enitites.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(

        @NotBlank(message = "Login is required")
        String login,

        @NotBlank(message = "Password is required")
        String password
) {}
