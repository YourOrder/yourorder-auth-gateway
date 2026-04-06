package jcn.yourorder.authgateway.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jcn.yourorder.authgateway.auth.enitites.models.User;
import jcn.yourorder.authgateway.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/debug/users")
@RequiredArgsConstructor
@Profile({"dev", "docker"})
@Tag(name = "Debug users", description = "Debug-only user inspection endpoints")
public class DebugUserController {

    private final UserRepository userRepository;

    @Operation(
            summary = "Get all users",
            description = "Returns all users from the database. Available only in dev and docker profiles."
    )
    @GetMapping
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(
            summary = "Get current user",
            description = "Returns the currently authenticated user based on the security context. Available only in dev and docker profiles.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    public Mono<User> me() {

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(id -> userRepository.findById(UUID.fromString(id)));
    }
}
