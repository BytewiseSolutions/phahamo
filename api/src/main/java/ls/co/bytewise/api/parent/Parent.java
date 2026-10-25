package ls.co.bytewise.api.parent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "parent")
public class Parent extends SchoolScopedEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "login_enabled", nullable = false, columnDefinition = "boolean default true")
    private boolean loginEnabled = true;

    @Column(name = "invited_at")
    private Instant invitedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParentRelationship relationship = ParentRelationship.GUARDIAN;
}
