package jcn.yourorder.authgateway.auth.enitites.models;

import jcn.yourorder.authgateway.auth.enitites.enums.UserRole;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
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

    @Column("tenant_id")
    private UUID tenantID;

    private String username;
    private String email;
    private String password;
    private UserRole role;
    @Column("created_at")
    private Instant createdAt;
}