package ls.co.bytewise.api.audit;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void record(School school, User actor, AuditAction action,
                       String targetType, Long targetId, String summary) {
        AuditLog log = new AuditLog();
        log.setSchool(school);
        log.setActor(actor);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setSummary(summary);
        log.setCreatedBy(actor);
        log.setUpdatedBy(actor);
        repository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDto> search(Long actorId, AuditAction action,
                                    LocalDate fromDate, LocalDate toDate,
                                    Pageable pageable) {
        ZoneId zone = ZoneId.systemDefault();
        Instant from = fromDate == null ? null : fromDate.atStartOfDay(zone).toInstant();
        Instant to = toDate == null ? null : toDate.plusDays(1).atStartOfDay(zone).toInstant();
        return repository.search(TenantContext.getSchoolId(), actorId, action, from, to, pageable)
                .map(AuditLogDto::new);
    }
}
