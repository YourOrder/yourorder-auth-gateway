package jcn.yourorder.authgateway.auth.services;

import jcn.yourorder.authgateway.auth.enitites.dtos.request.LoginRequestDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.request.RegisterRequestDto;
import jcn.yourorder.authgateway.auth.enitites.enums.UserRole;
import jcn.yourorder.authgateway.auth.enitites.models.User;
import jcn.yourorder.authgateway.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> register(RegisterRequestDto request) {

        String username = normalize(request.username());
        String email = normalize(request.email());

        String rawPassword = request.password();

        return validateUnique(username, email)
                .then(hashPassword(rawPassword))
                .flatMap(encoded -> saveUser(username, email, encoded));
    }

    public Mono<User> login(LoginRequestDto request) {

        String login = normalize(request.login());

        String rawPassword = request.password();

        return userRepository.findByLogin(login)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")))
                .flatMap(user -> verifyPassword(rawPassword, user));
    }

    /**
     * Check if username and email are unique. If not, return an error with a descriptive message.
     *
     * @param username normalized username to check for uniqueness
     * @param email normalized email to check for uniqueness
     * @return Mono that completes if both username and email are unique, or emits an error if either is already take
     */
    private Mono<Void> validateUnique(String username, String email) {

        return Mono.zip(
                userRepository.existsByUsername(username),
                userRepository.existsByEmail(email)
        ).flatMap(tuple -> {

            if (tuple.getT1()) {
                return Mono.error(new RuntimeException("Username already taken"));
            }

            if (tuple.getT2()) {
                return Mono.error(new RuntimeException("Email already taken"));
            }

            return Mono.empty();
        });
    }


    /**
     * Hash the raw password using the provided PasswordEncoder. This operation is performed on a bounded elastic scheduler to avoid blocking the main thread, as password hashing can be CPU-intensive.
     * @param rawPassword the plain text password to hash. This is the original password provided by the user during registration.
     * @return A Mono that emits the hashed password as a String once the hashing operation is complete. If an error occurs during hashing, the Mono will emit an error signal.
     */
    private Mono<String> hashPassword(String rawPassword) {
        return Mono.fromCallable(() -> passwordEncoder.encode(rawPassword))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<User> saveUser(String username, String email, String encodedPassword) {

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .email(email)
                .password(encodedPassword)
                .role(UserRole.RETAILER)
                .createdAt(Instant.now())
                .build();

        return userRepository.save(user);
    }

    /**
     * Normalize input by converting to lowercase and trimming whitespace. This helps ensure consistent handling of usernames and emails, preventing issues with case sensitivity and accidental spaces.
     * @param value the input string to normalize (e.g., username or email)
     * @return The normalized string, which is lowercase and trimmed of leading/trailing whitespace
     */
    private String normalize(String value) {
        return value.toLowerCase().trim();
    }


    /**
     * Verify that the raw password provided during login matches the hashed password stored in the database for the given user. The password comparison is performed using the PasswordEncoder's matches method, which is a CPU-intensive operation. To avoid blocking the main thread, this comparison is executed on a bounded elastic scheduler.
     * @param rawPassword the plain text password provided by the user during login
     * @param user the User entity retrieved from the database based on the login identifier (username or email). This user object contains the hashed password that we will compare against the raw password.
     * @return A Mono that emits the User object if the password matches, or emits an error if the password is invalid. The password comparison is performed on a bounded elastic scheduler to avoid blocking the main thread, as it can be CPU-intensive.
     */
    private Mono<User> verifyPassword(String rawPassword, User user) {

        return Mono.fromCallable(() -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .subscribeOn(Schedulers.boundedElastic())
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")))
                .map(matches -> user);
    }
}
