package ls.co.bytewise.api.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ls.co.bytewise.api.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "blacklisted_token")
public class BlacklistedToken extends BaseEntity {

    @Column(nullable = false, length = 512, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;
}