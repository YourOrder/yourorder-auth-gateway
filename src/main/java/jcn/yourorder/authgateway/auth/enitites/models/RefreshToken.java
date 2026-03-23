package jcn.yourorder.authgateway.auth.enitites.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
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

    @Column("user_id")
    private UUID userId;

    private String token;

    @Column("expires_at")
    private Instant expiresAt;

    private boolean revoked;

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }
}
