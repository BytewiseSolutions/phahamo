package ls.co.bytewise.api.term;

import ls.co.bytewise.api.academicyear.AcademicYear;
import ls.co.bytewise.api.academicyear.AcademicYearRepository;
import ls.co.bytewise.api.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TermService {

    private final TermRepository termRepository;
    private final AcademicYearRepository academicYearRepository;

    public TermService(TermRepository termRepository,
                       AcademicYearRepository academicYearRepository) {
        this.termRepository = termRepository;
        this.academicYearRepository = academicYearRepository;
    }

    @Transactional
    public TermDto createTerm(TermDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        AcademicYear academicYear = academicYearRepository.findById(dto.getAcademicYearId())
                .orElseThrow(() -> new IllegalStateException("Academic year not found"));

        if (!academicYear.getSchool().getId().equals(schoolId)) {
            throw new IllegalStateException("Academic year does not belong to the active school");
        }

        validateDatesWithinAcademicYear(dto, academicYear);

        Term term = new Term();
        term.setSchool(academicYear.getSchool());
        term.setAcademicYear(academicYear);
        term.setName(dto.getName());
        term.setStartDate(dto.getStartDate());
        term.setEndDate(dto.getEndDate());

        Term saved = termRepository.save(term);
        return new TermDto(saved);
    }

    public List<TermDto> listTerms() {
        return termRepository.findAll().stream()
                .map(TermDto::new)
                .toList();
    }

    public TermDto getTerm(Long id) {
        return new TermDto(findOrThrow(id));
    }

    @Transactional
    public TermDto updateTerm(Long id, TermDto dto) {
        Term term = findOrThrow(id);

        AcademicYear academicYear = academicYearRepository.findById(dto.getAcademicYearId())
                .orElseThrow(() -> new IllegalStateException("Academic year not found"));

        validateDatesWithinAcademicYear(dto, academicYear);

        term.setAcademicYear(academicYear);
        term.setName(dto.getName());
        term.setStartDate(dto.getStartDate());
        term.setEndDate(dto.getEndDate());

        Term saved = termRepository.save(term);
        return new TermDto(saved);
    }

    @Transactional
    public void deleteTerm(Long id) {
        Term term = findOrThrow(id);
        termRepository.delete(term);
    }

    private Term findOrThrow(Long id) {
        return termRepository.findById(id)
                .orElseThrow(() ->
                        new TermExceptions.TermNotFoundException("Term not found"));
    }

    private void validateDatesWithinAcademicYear(TermDto dto, AcademicYear academicYear) {
        if (dto.getStartDate().isBefore(academicYear.getStartDate())
                || dto.getEndDate().isAfter(academicYear.getEndDate())) {
            throw new IllegalArgumentException(
                    "Term dates must fall within the academic year"
            );
        }
    }
}