package ls.co.bytewise.api.school;

import ls.co.bytewise.api.common.*;
import ls.co.bytewise.api.legal.LegalPolicy;
import ls.co.bytewise.api.subscription.Subscription;
import ls.co.bytewise.api.subscription.SubscriptionPlan;
import ls.co.bytewise.api.subscription.SubscriptionRepository;
import ls.co.bytewise.api.subscription.SubscriptionStatus;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserDto;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.*;
import java.util.Set;

@Service
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionRepository subscriptionRepository;
    private final DataFileRepository dataFileRepository;

    public SchoolService(SchoolRepository schoolRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         SubscriptionRepository subscriptionRepository,
                         DataFileRepository dataFileRepository) {
        this.schoolRepository = schoolRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionRepository = subscriptionRepository;
        this.dataFileRepository = dataFileRepository;
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
        if (!Boolean.TRUE.equals(admin.getTermsAccepted())
                || !Boolean.TRUE.equals(admin.getPrivacyAccepted())) {
            throw new IllegalArgumentException("You must accept the Terms of Use and Privacy Policy");
        }
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
        Instant acceptedAt = Instant.now();
        newAdmin.setTermsAcceptedAt(acceptedAt);
        newAdmin.setTermsVersion(LegalPolicy.TERMS_VERSION);
        newAdmin.setPrivacyAcceptedAt(acceptedAt);
        newAdmin.setPrivacyVersion(LegalPolicy.PRIVACY_VERSION);
        newAdmin.setRoles(Set.of(UserRole.SCHOOL_ADMIN));

        newAdmin.add(savedSchool);

        userRepository.save(newAdmin);

        return savedSchool;
    }

    public SchoolDto getSchoolProfile(Long id) {
        School school = findSchoolOrThrow(id);
        assertTenantAccess(id);
        return new SchoolDto(school);
    }

    @Transactional
    public SchoolDto updateSchoolProfile(Long id, SchoolDto dto) {
        School school = findSchoolOrThrow(id);
        assertTenantAccess(id);

        if (dto.getSchoolTermStructure() == null) {
            throw new SchoolExceptions.InvalidSchoolProfileException("Term structure is required");
        }
        if (dto.getSchoolCurrency() == null) {
            throw new SchoolExceptions.InvalidSchoolProfileException("Currency is required");
        }
        if (dto.getSchoolTimezone() == null || dto.getSchoolTimezone().isBlank()) {
            throw new SchoolExceptions.InvalidSchoolProfileException("Timezone is required");
        }

        validateTimezone(dto.getSchoolTimezone());

        school.setName(dto.getSchoolName());
        school.setAddress(dto.getSchoolAddress());
        school.setContactEmail(dto.getSchoolContactEmail());
        school.setContactPhone(dto.getSchoolContactPhone());
        school.setTermStructure(dto.getSchoolTermStructure());
        school.setCurrency(dto.getSchoolCurrency());
        school.setTimezone(dto.getSchoolTimezone());

        if (dto.getAbsenceAlertsEnabled() != null) {
            school.setAbsenceAlertsEnabled(dto.getAbsenceAlertsEnabled());
        }

        School saved = schoolRepository.save(school);
        return new SchoolDto(saved);
    }

    @Transactional
    public SchoolDto updateSchoolLogo(Long id, MultipartFile file) throws IOException {
        School school = findSchoolOrThrow(id);
        assertTenantAccess(id);

        DataFile logo = DataFile.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .contents(file.getBytes())
                .fileSize(file.getSize())
                .build();
        logo = dataFileRepository.save(logo);

        school.setLogo(logo);
        School saved = schoolRepository.save(school);
        return new SchoolDto(saved);
    }
    public DataFile getSchoolLogoFile(Long id) {
        School school = findSchoolOrThrow(id);
        if (school.getLogo() == null) {
            throw new SchoolExceptions.SchoolNotFoundException("School has no logo uploaded");
        }
        return school.getLogo();
    }
    private School findSchoolOrThrow(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new SchoolExceptions.SchoolNotFoundException("School not found with id: " + id));
    }
    private void assertTenantAccess(Long schoolId) {
        Long tenantSchoolId = TenantContext.getSchoolId();
        if (tenantSchoolId != null && !tenantSchoolId.equals(schoolId)) {
            throw new AccessDeniedException("You do not have access to this school");
        }
    }
    private void validateTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
        } catch (DateTimeException e) {
            throw new SchoolExceptions.InvalidTimezoneException("'" + timezone + "' is not a valid timezone");
        }
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
