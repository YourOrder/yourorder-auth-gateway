package jcn.yourorder.authgateway.auth.enitites.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("refresh_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    private UUID id;

    private UUID userId;

    private String token;

    private Instant expiresAt;

    private boolean revoked;
}
