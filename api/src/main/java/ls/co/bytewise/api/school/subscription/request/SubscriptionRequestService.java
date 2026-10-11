package ls.co.bytewise.api.school.subscription.request;

import ls.co.bytewise.api.common.DataFile;
import ls.co.bytewise.api.common.DataFileRepository;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolExceptions;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.school.subscription.Subscription;
import ls.co.bytewise.api.school.subscription.SubscriptionPlan;
import ls.co.bytewise.api.school.subscription.SubscriptionRepository;
import ls.co.bytewise.api.school.subscription.SubscriptionStatus;
import ls.co.bytewise.api.school.subscription.payment.PaymentMethod;
import ls.co.bytewise.api.school.subscription.payment.PaymentMethodRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SubscriptionRequestService {

    private final SubscriptionRequestRepository requestRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SchoolRepository schoolRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final DataFileRepository dataFileRepository;

    public SubscriptionRequestService(SubscriptionRequestRepository requestRepository,
                                      SubscriptionRepository subscriptionRepository,
                                      SchoolRepository schoolRepository,
                                      PaymentMethodRepository paymentMethodRepository,
                                      DataFileRepository dataFileRepository) {
        this.requestRepository = requestRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.schoolRepository = schoolRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.dataFileRepository = dataFileRepository;
    }

    @Transactional
    public SubscriptionRequestDto create(User requester, SubscriptionRequestDto dto) {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new IllegalStateException("No active school context");
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new SchoolExceptions.SchoolNotFoundException("School not found"));

        if (requestRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionRequestStatus.PENDING)) {
            throw new IllegalStateException("You already have a pending subscription request");
        }

        if (dto.getPlan() == null || dto.getPlan() == SubscriptionPlan.FREE_TRIAL) {
            throw new IllegalArgumentException("Invalid plan");
        }

        if (dto.getMethodId() == null) {
            throw new IllegalArgumentException("Payment method is required");
        }

        PaymentMethod method = paymentMethodRepository.findById(dto.getMethodId())
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

        if (!method.isActive()) {
            throw new IllegalArgumentException("Payment method is not available");
        }

        if (dto.getProofFileId() == null) {
            throw new IllegalArgumentException("Proof of payment is required");
        }

        DataFile proof = dataFileRepository.findById(dto.getProofFileId())
                .orElseThrow(() -> new IllegalArgumentException("Proof file not found"));

        SubscriptionRequest request = new SubscriptionRequest();
        request.setSchool(school);
        request.setPlan(dto.getPlan());
        request.setAmount(dto.getAmount());
        request.setCurrency(dto.getCurrency());
        request.setMethod(method);
        request.setProofFile(proof);
        request.setNote(dto.getNote());
        request.setStatus(SubscriptionRequestStatus.PENDING);
        request.setCreatedBy(requester);
        request.setUpdatedBy(requester);

        SubscriptionRequest saved = requestRepository.save(request);
        return new SubscriptionRequestDto(saved);
    }

    public List<SubscriptionRequestDto> listForCurrentSchool() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new IllegalStateException("No active school context");
        }

        return requestRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId).stream()
                .map(SubscriptionRequestDto::new)
                .toList();
    }

    public List<SubscriptionRequestDto> listAll() {
        return requestRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(SubscriptionRequestDto::new)
                .toList();
    }

    public List<SubscriptionRequestDto> listPending() {
        return requestRepository.findAllByStatusOrderByCreatedAtDesc(SubscriptionRequestStatus.PENDING).stream()
                .map(SubscriptionRequestDto::new)
                .toList();
    }

    @Transactional
    public SubscriptionRequestDto approve(User reviewer, Long id, String reviewNote) {
        SubscriptionRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != SubscriptionRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved");
        }

        applyToSubscription(request, reviewer);

        request.setStatus(SubscriptionRequestStatus.APPROVED);
        request.setReviewNote(reviewNote);
        request.setUpdatedBy(reviewer);

        SubscriptionRequest saved = requestRepository.save(request);
        return new SubscriptionRequestDto(saved);
    }

    @Transactional
    public SubscriptionRequestDto reject(User reviewer, Long id, String reviewNote) {
        SubscriptionRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != SubscriptionRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be rejected");
        }

        if (reviewNote == null || reviewNote.isBlank()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }

        request.setStatus(SubscriptionRequestStatus.REJECTED);
        request.setReviewNote(reviewNote);
        request.setUpdatedBy(reviewer);

        SubscriptionRequest saved = requestRepository.save(request);
        return new SubscriptionRequestDto(saved);
    }

    @Transactional
    public SubscriptionRequestDto cancel(User requester, Long id) {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new IllegalStateException("No active school context");
        }

        SubscriptionRequest request = requestRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != SubscriptionRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be cancelled");
        }

        request.setStatus(SubscriptionRequestStatus.CANCELLED);
        request.setUpdatedBy(requester);

        SubscriptionRequest saved = requestRepository.save(request);
        return new SubscriptionRequestDto(saved);
    }

    private void applyToSubscription(SubscriptionRequest request, User reviewer) {
        Subscription subscription = subscriptionRepository.findBySchoolId(request.getSchool().getId())
                .orElseThrow(() -> new IllegalStateException("Subscription not found for school"));

        Instant now = Instant.now();
        subscription.setPlan(request.getPlan());

        if (request.getPlan() == SubscriptionPlan.ONE_OFF) {
            subscription.setEndsAt(null);
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        } else {
            Instant base = subscription.getEndsAt() != null && subscription.getEndsAt().isAfter(now)
                    ? subscription.getEndsAt()
                    : now;

            Instant newEnd = switch (request.getPlan()) {
                case MONTHLY -> base.atZone(ZoneOffset.UTC).plusMonths(1).toInstant();
                case ANNUAL  -> base.atZone(ZoneOffset.UTC).plusYears(1).toInstant();
                default -> base;
            };

            subscription.setEndsAt(newEnd);
            subscription.setStatus(SubscriptionStatus.ACTIVE);

            if (subscription.getStartedAt() == null) {
                subscription.setStartedAt(now);
            }
        }

        subscription.setUpdatedBy(reviewer);
        subscriptionRepository.save(subscription);
    }

    public List<SubscriptionRequestDto> listForSchool(Long schoolId) {
        return requestRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId).stream()
                .map(SubscriptionRequestDto::new)
                .toList();
    }
}