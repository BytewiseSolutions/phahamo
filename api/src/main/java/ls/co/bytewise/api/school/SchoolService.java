package ls.co.bytewise.api.school;

import ls.co.bytewise.api.common.InvalidPasswordException;
import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.school.subscription.Subscription;
import ls.co.bytewise.api.school.subscription.SubscriptionPlan;
import ls.co.bytewise.api.school.subscription.SubscriptionRepository;
import ls.co.bytewise.api.school.subscription.SubscriptionStatus;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserDto;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionRepository subscriptionRepository;

    public SchoolService(SchoolRepository schoolRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         SubscriptionRepository subscriptionRepository) {
        this.schoolRepository = schoolRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public School registerSchool(UserDto admin) {

        if (admin.getPassword() == null || admin.getPassword().isBlank()) {
            throw new InvalidPasswordException("Password is required");
        }

        if (admin.getPassword().length() < 8) {
            throw new InvalidPasswordException("Password must be at least 8 characters");
        }

        SchoolDto school = admin.getSchool();
        validateRegistration(admin.getEmail(), school.getSchoolContactEmail());

        School newSchool = new School();
        newSchool.setName(school.getSchoolName());
        newSchool.setAddress(school.getSchoolAddress());
        newSchool.setContactEmail(school.getSchoolContactEmail());
        newSchool.setContactPhone(school.getSchoolContactPhone());
        newSchool.setStatus(Status.ACTIVE);

        School savedSchool = schoolRepository.save(newSchool);

        Instant startedAt = Instant.now();
        Instant endsAt = ZonedDateTime.ofInstant(startedAt, ZoneOffset.UTC)
                .plusMonths(1)
                .toInstant();

        Subscription subscription = new Subscription();
        subscription.setSchool(savedSchool);
        subscription.setPlan(SubscriptionPlan.FREE_TRIAL);
        subscription.setStatus(SubscriptionStatus.TRIAL);
        subscription.setStartedAt(startedAt);
        subscription.setEndsAt(endsAt);

        subscriptionRepository.save(subscription);

        User newAdmin = new User();
        newAdmin.setFirstName(admin.getFirstName());
        newAdmin.setLastName(admin.getLastName());
        newAdmin.setEmail(admin.getEmail());
        newAdmin.setPhone(admin.getPhone());
        newAdmin.setPassword(passwordEncoder.encode(admin.getPassword()));
        newAdmin.setStatus(Status.ACTIVE);
        newAdmin.setRoles(Set.of(UserRole.SCHOOL_ADMIN));

        newAdmin.add(savedSchool);

        userRepository.save(newAdmin);

        return savedSchool;
    }

    private void validateRegistration(String email, String schoolContactEmail) {

        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException(
                    "A user with this email already exists"
            );
        }

        if (schoolRepository.existsByContactEmail(schoolContactEmail)) {
            throw new DataIntegrityViolationException(
                    "A school with this contact email already exists"
            );
        }
    }
}