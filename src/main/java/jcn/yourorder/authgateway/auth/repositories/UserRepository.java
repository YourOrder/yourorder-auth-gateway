package jcn.yourorder.authgateway.auth.repositories;

import jcn.yourorder.authgateway.auth.enitites.models.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {

    Mono<User> findByUsername(String username);

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    @Query("""
    SELECT * FROM users 
    WHERE username = :login 
       OR email = :login
    """)
    Mono<User> findByLogin(String login);
}
