package jcn.yourorder.authgateway.auth.services;

import jcn.yourorder.authgateway.auth.enitites.dtos.request.LoginRequestDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.request.RegisterRequestDto;
import jcn.yourorder.authgateway.auth.enitites.enums.UserRole;
import jcn.yourorder.authgateway.auth.enitites.models.User;
import jcn.yourorder.authgateway.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> register(RegisterRequestDto request) {

        log.debug("REGISTER REQUEST: {}", request);

        String username = normalize(request.username());
        String email = normalize(request.email());
        String rawPassword = request.password();

        log.debug("NORMALIZED: username={}, email={}", username, email);

        return validateUnique(username, email)
                .doOnSubscribe(sub -> log.debug("START UNIQUE CHECK"))
                .doOnSuccess(v -> log.debug("UNIQUE CHECK PASSED"))
                .then(hashPassword(rawPassword))
                .doOnNext(encoded -> log.debug("PASSWORD HASHED: {}", encoded))
                .flatMap(encoded -> saveUser(username, email, encoded))
                .doOnNext(user -> log.debug("USER SAVED: {}", user))
                .doOnError(err -> log.error("REGISTER ERROR", err));
    }

    public Mono<User> login(LoginRequestDto request) {

        log.debug("LOGIN REQUEST: {}", request);

        String login = normalize(request.login());
        String rawPassword = request.password();

        log.debug("LOGIN ATTEMPT: {}", login);

        return userRepository.findByLogin(login)
                .doOnSubscribe(sub -> log.debug("SEARCHING USER IN DB"))
                .doOnNext(user -> {
                    log.debug("USER FOUND:");
                    log.debug(" - username: {}", user.getUsername());
                    log.debug(" - email: {}", user.getEmail());
                    log.debug(" - password(hash): {}", user.getPassword());
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("USER NOT FOUND: {}", login);
                    return Mono.error(new RuntimeException("USER NOT FOUND"));
                }))
                .flatMap(user -> verifyPassword(rawPassword, user))
                .doOnError(err -> log.error("LOGIN ERROR", err));
    }

    /**
     * Check if username and email are unique. If not, return an error with a descriptive message.
     *
     * @param username normalized username to check for uniqueness
     * @param email    normalized email to check for uniqueness
     * @return Mono that completes if both username and email are unique, or emits an error if either is already take
     */
    private Mono<Void> validateUnique(String username, String email) {

        log.debug("CHECK UNIQUE: username={}, email={}", username, email);

        return Mono.zip(
                userRepository.existsByUsername(username)
                        .doOnNext(exists -> log.debug("USERNAME EXISTS: {}", exists)),
                userRepository.existsByEmail(email)
                        .doOnNext(exists -> log.debug("EMAIL EXISTS: {}", exists))
        ).flatMap(tuple -> {

            if (tuple.getT1()) {
                log.error("USERNAME ALREADY TAKEN: {}", username);
                return Mono.error(new RuntimeException("Username already taken"));
            }

            if (tuple.getT2()) {
                log.error("EMAIL ALREADY TAKEN: {}", email);
                return Mono.error(new RuntimeException("Email already taken"));
            }

            return Mono.empty();
        });
    }


    /**
     * Hash the raw password using the provided PasswordEncoder. This operation is performed on a bounded elastic scheduler to avoid blocking the main thread, as password hashing can be CPU-intensive.
     *
     * @param rawPassword the plain text password to hash. This is the original password provided by the user during registration.
     * @return A Mono that emits the hashed password as a String once the hashing operation is complete. If an error occurs during hashing, the Mono will emit an error signal.
     */
    private Mono<String> hashPassword(String rawPassword) {

        log.debug("HASHING PASSWORD");

        return Mono.fromCallable(() -> passwordEncoder.encode(rawPassword))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(encoded -> log.debug("HASH RESULT: {}", encoded))
                .doOnError(err -> log.error("ERROR HASHING PASSWORD", err));
    }

    private Mono<User> saveUser(String username, String email, String encodedPassword) {

        User user = User.builder()
                .username(username)
                .email(email)
                .password(encodedPassword)
                .role(UserRole.RETAILER)
                .createdAt(Instant.now())
                .build();

        log.debug("SAVING USER: {}", user);

        return userRepository.save(user)
                .doOnSubscribe(sub -> log.debug("DB SAVE STARTED"))
                .doOnNext(saved -> log.debug("USER SAVED IN DB: {}", saved))
                .doOnError(err -> log.error("ERROR SAVING USER", err));
    }

    /**
     * Normalize input by converting to lowercase and trimming whitespace. This helps ensure consistent handling of usernames and emails, preventing issues with case sensitivity and accidental spaces.
     *
     * @param value the input string to normalize (e.g., username or email)
     * @return The normalized string, which is lowercase and trimmed of leading/trailing whitespace
     */
    private String normalize(String value) {
        String normalized = value.toLowerCase().trim();
        log.debug("NORMALIZE: '{}' -> '{}'", value, normalized);
        return normalized;
    }


    /**
     * Verify that the raw password provided during login matches the hashed password stored in the database for the given user. The password comparison is performed using the PasswordEncoder's matches method, which is a CPU-intensive operation. To avoid blocking the main thread, this comparison is executed on a bounded elastic scheduler.
     *
     * @param rawPassword the plain text password provided by the user during login
     * @param user        the User entity retrieved from the database based on the login identifier (username or email). This user object contains the hashed password that we will compare against the raw password.
     * @return A Mono that emits the User object if the password matches, or emits an error if the password is invalid. The password comparison is performed on a bounded elastic scheduler to avoid blocking the main thread, as it can be CPU-intensive.
     */
    private Mono<User> verifyPassword(String rawPassword, User user) {

        log.debug("VERIFY PASSWORD FOR USER: {}", user.getUsername());

        return Mono.fromCallable(() -> {
                    boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());

                    log.debug("PASSWORD CHECK:");
                    log.debug(" - raw: {}", rawPassword);
                    log.debug(" - encoded: {}", user.getPassword());
                    log.debug(" - matches: {}", matches);

                    return matches;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("INVALID PASSWORD FOR USER: {}", user.getUsername());
                    return Mono.error(new RuntimeException("Invalid credentials"));
                }))
                .map(matches -> user);
    }
}
