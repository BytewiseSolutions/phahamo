package ls.co.bytewise.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import ls.co.bytewise.api.common.BaseEntity;
import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.school.School;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User extends BaseEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean mustChangePassword = false;

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(nullable = false)
    private String phone;

    @Column(name = "date_of_birth")
    private Instant dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "terms_accepted_at")
    private Instant termsAcceptedAt;

    @Column(name = "terms_version", length = 32)
    private String termsVersion;

    @Column(name = "privacy_accepted_at")
    private Instant privacyAcceptedAt;

    @Column(name = "privacy_version", length = 32)
    private String privacyVersion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<UserRole> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_school",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "school_id")
    )
    private Set<School> schools = new HashSet<>();

    public boolean hasRole(UserRole role) {
        return roles.contains(role);
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
    public void add(School school) {
        schools.add(school);
    }
}
