package jcn.yourorder.authgateway.auth.enitites.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email isn`t real")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {}
