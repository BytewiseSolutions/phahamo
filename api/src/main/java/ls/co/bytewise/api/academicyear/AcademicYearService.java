package ls.co.bytewise.api.academicyear;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.term.TermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final SchoolRepository schoolRepository;
    private final TermRepository termRepository;

    public AcademicYearService(AcademicYearRepository academicYearRepository,
                               SchoolRepository schoolRepository, TermRepository termRepository) {
        this.academicYearRepository = academicYearRepository;
        this.schoolRepository = schoolRepository;
        this.termRepository = termRepository;
    }

    @Transactional
    public AcademicYearDto createAcademicYear(AcademicYearDto dto) {
        Long schoolId = TenantContext.getSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("Active school not found"));

        AcademicYear academicYear = new AcademicYear();
        academicYear.setSchool(school);
        academicYear.setName(dto.getName());
        academicYear.setStartDate(dto.getStartDate());
        academicYear.setEndDate(dto.getEndDate());
        academicYear.setActive(dto.isActive());

        if (dto.isActive()) {
            deactivateCurrentActiveYear(null);
        }

        AcademicYear saved = academicYearRepository.save(academicYear);
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
    public AcademicYearDto updateAcademicYear(Long id, AcademicYearDto dto) {
        AcademicYear academicYear = findOrThrow(id);

        academicYear.setName(dto.getName());
        academicYear.setStartDate(dto.getStartDate());
        academicYear.setEndDate(dto.getEndDate());

        if (dto.isActive() && !academicYear.isActive()) {
            deactivateCurrentActiveYear(academicYear.getId());
        }
        academicYear.setActive(dto.isActive());

        AcademicYear saved = academicYearRepository.save(academicYear);
        return new AcademicYearDto(saved);
    }

    @Transactional
    public void deleteAcademicYear(Long id) {
        AcademicYear academicYear = findOrThrow(id);

        if (termRepository.existsByAcademicYearId(id)) {
            throw new IllegalStateException(
                    "Academic year cannot be deleted because it has linked terms"
            );
        }

        academicYearRepository.delete(academicYear);
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