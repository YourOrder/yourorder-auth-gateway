package jcn.yourorder.authgateway.auth.enitites.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table("users")
public class User {

    @Id
    private UUID id;

    private String username;
    private String email;
    private String password;
    private String role;
    // todo Наверное можно обойтись 1 айди
    private String tenantId;
    private Instant createdAt;
}