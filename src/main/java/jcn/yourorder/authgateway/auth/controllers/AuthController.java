package jcn.yourorder.authgateway.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jcn.yourorder.authgateway.auth.enitites.dtos.request.LoginRequestDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.request.RefreshRequestDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.request.RegisterRequestDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.response.LoginResponseDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.response.RefreshResponseDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.response.RegisterResponseDto;
import jcn.yourorder.authgateway.auth.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth operations")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register user",
            description = "Registration user and returns access + refresh tokens"
    )
    @PostMapping("/register")
    public Mono<RegisterResponseDto> registerUser(
            @Valid @RequestBody RegisterRequestDto request
    ) {
        return authService.registerUser(request);
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates user and returns access + refresh tokens"
    )
    @PostMapping("/login")
    public Mono<LoginResponseDto> loginUser(
            @Valid @RequestBody LoginRequestDto request
    ) {
        return authService.loginUser(request);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Refresh and returns access token based refresh token"
    )
    @PostMapping("/refresh")
    public Mono<RefreshResponseDto> refreshUserToken(
            @Valid @RequestBody RefreshRequestDto request
    ) {
        return authService.refreshUserToken(request);
    }

    @Operation(
            summary = "Logout user",
            description = "Revokes current refresh token"
    )
    @PostMapping("/logout")
    public Mono<Void> logoutUser(
            @Valid @RequestBody RefreshRequestDto request
    ) {
        return authService.logoutUser(request);
    }
}

