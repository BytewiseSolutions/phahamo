package ls.co.bytewise.api.academicyear;

import ls.co.bytewise.api.audit.AuditAction;
import ls.co.bytewise.api.audit.AuditLogService;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final SchoolRepository schoolRepository;
    private final TermRepository termRepository;
    private final AuditLogService auditLogService;

    public AcademicYearService(AcademicYearRepository academicYearRepository,
                               SchoolRepository schoolRepository, TermRepository termRepository,
                               AuditLogService auditLogService) {
        this.academicYearRepository = academicYearRepository;
        this.schoolRepository = schoolRepository;
        this.termRepository = termRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AcademicYearDto createAcademicYear(User creator, AcademicYearDto dto) {
        Long schoolId = TenantContext.getSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("Active school not found"));

        AcademicYear academicYear = new AcademicYear();
        academicYear.setSchool(school);
        academicYear.setName(dto.getName());
        academicYear.setStartDate(dto.getStartDate());
        academicYear.setEndDate(dto.getEndDate());
        academicYear.setActive(dto.isActive());
        academicYear.setCreatedBy(creator);
        academicYear.setUpdatedBy(creator);

        if (dto.isActive()) {
            deactivateCurrentActiveYear(null);
        }

        AcademicYear saved = academicYearRepository.save(academicYear);
        auditLogService.record(school, creator, AuditAction.ACADEMIC_YEAR_CREATED,
                "ACADEMIC_YEAR", saved.getId(), "Created academic year " + saved.getName());
        return new AcademicYearDto(saved);
    }

    public List<AcademicYearDto> listAcademicYears() {
        return academicYearRepository.findAll().stream()
                .map(AcademicYearDto::new)
                .toList();
    }

    public AcademicYearDto getAcademicYear(Long id) {
        return new AcademicYearDto(findOrThrow(id));
    }

    @Transactional
    public AcademicYearDto updateAcademicYear(User editor, Long id, AcademicYearDto dto) {
        AcademicYear academicYear = findOrThrow(id);

        if (academicYear.isActive() && !dto.isActive()) {
            throw new AcademicYearExceptions.InvalidAcademicYearStateException(
                    "Cannot deactivate the only active academic year. Activate a different academic year instead.");
        }

        academicYear.setName(dto.getName());
        academicYear.setStartDate(dto.getStartDate());
        academicYear.setEndDate(dto.getEndDate());
        academicYear.setUpdatedBy(editor);

        if (dto.isActive() && !academicYear.isActive()) {
            deactivateCurrentActiveYear(academicYear.getId());
        }
        academicYear.setActive(dto.isActive());

        AcademicYear saved = academicYearRepository.save(academicYear);
        auditLogService.record(saved.getSchool(), editor, AuditAction.ACADEMIC_YEAR_UPDATED,
                "ACADEMIC_YEAR", saved.getId(), "Updated academic year " + saved.getName());
        return new AcademicYearDto(saved);
    }

    @Transactional
    public void deleteAcademicYear(User deleter, Long id) {
        AcademicYear academicYear = findOrThrow(id);

        if (termRepository.existsByAcademicYearId(id)) {
            throw new AcademicYearExceptions.LinkedDataExistsException(
                    "Academic year cannot be deleted because it has linked terms");
        }

        academicYearRepository.delete(academicYear);
        auditLogService.record(academicYear.getSchool(), deleter, AuditAction.ACADEMIC_YEAR_DELETED,
                "ACADEMIC_YEAR", academicYear.getId(), "Deleted academic year " + academicYear.getName());
    }

    private void deactivateCurrentActiveYear(Long excludeId) {
        Long schoolId = TenantContext.getSchoolId();

        academicYearRepository.findBySchoolIdAndActiveTrue(schoolId)
                .ifPresent(current -> {
                    if (excludeId == null || !current.getId().equals(excludeId)) {
                        current.setActive(false);
                        academicYearRepository.save(current);
                    }
                });
    }

    private AcademicYear findOrThrow(Long id) {
        return academicYearRepository.findById(id)
                .orElseThrow(() -> new AcademicYearExceptions.AcademicYearNotFoundException("Academic year not found"));
    }
}
