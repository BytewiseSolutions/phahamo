package ls.co.bytewise.api.term;

import ls.co.bytewise.api.academicyear.AcademicYear;
import ls.co.bytewise.api.academicyear.AcademicYearRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
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
    public TermDto createTerm(User creator, TermDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        AcademicYear academicYear = academicYearRepository.findById(dto.getAcademicYearId())
                .orElseThrow(() -> new TermExceptions.InvalidTermException("Academic year not found"));

        if (!academicYear.getSchool().getId().equals(schoolId)) {
            throw new TermExceptions.InvalidTermException("Academic year does not belong to the active school");
        }

        validateDatesWithinAcademicYear(dto, academicYear);
        validateNoOverlap(dto, academicYear.getId(), null);

        Term term = new Term();
        term.setSchool(academicYear.getSchool());
        term.setAcademicYear(academicYear);
        term.setName(dto.getName());
        term.setStartDate(dto.getStartDate());
        term.setEndDate(dto.getEndDate());
        term.setCreatedBy(creator);
        term.setUpdatedBy(creator);

        Term saved = termRepository.save(term);
        return new TermDto(saved);
    }

    public List<TermDto> listTerms() {
        return termRepository.findAll().stream()
                .map(TermDto::new)
                .toList();
    }

    public List<TermDto> getTermsByAcademicYear(Long academicYearId) {
        Long schoolId = TenantContext.getSchoolId();

        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new TermExceptions.InvalidTermException("Academic year not found"));

        if (!academicYear.getSchool().getId().equals(schoolId)) {
            throw new TermExceptions.InvalidTermException("Academic year does not belong to the active school");
        }

        return termRepository.findByAcademicYearId(academicYearId).stream()
                .map(TermDto::new)
                .toList();
    }

    public TermDto getTerm(Long id) {
        return new TermDto(findOrThrow(id));
    }

    @Transactional
    public TermDto updateTerm(User editor, Long id, TermDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        Term term = findOrThrow(id);

        if (!term.getSchool().getId().equals(schoolId)) {
            throw new TermExceptions.InvalidTermException("Term does not belong to the active school");
        }

        AcademicYear academicYear = academicYearRepository.findById(dto.getAcademicYearId())
                .orElseThrow(() -> new TermExceptions.InvalidTermException("Academic year not found"));

        if (!academicYear.getSchool().getId().equals(schoolId)) {
            throw new TermExceptions.InvalidTermException("Academic year does not belong to the active school");
        }

        validateDatesWithinAcademicYear(dto, academicYear);
        validateNoOverlap(dto, academicYear.getId(), term.getId());

        term.setAcademicYear(academicYear);
        term.setName(dto.getName());
        term.setStartDate(dto.getStartDate());
        term.setEndDate(dto.getEndDate());
        term.setUpdatedBy(editor);

        Term saved = termRepository.save(term);
        return new TermDto(saved);
    }

    @Transactional
    public void deleteTerm(Long id) {
        Long schoolId = TenantContext.getSchoolId();

        Term term = findOrThrow(id);

        if (!term.getSchool().getId().equals(schoolId)) {
            throw new TermExceptions.InvalidTermException("Term does not belong to the active school");
        }

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
            throw new TermExceptions.InvalidTermException(
                    "Term dates must fall within the academic year"
            );
        }
    }

    private void validateNoOverlap(TermDto dto, Long academicYearId, Long excludeTermId) {
        List<Term> existingTerms = termRepository.findByAcademicYearId(academicYearId);

        boolean overlaps = existingTerms.stream()
                .filter(t -> excludeTermId == null || !t.getId().equals(excludeTermId))
                .anyMatch(t -> dto.getStartDate().isBefore(t.getEndDate())
                        && dto.getEndDate().isAfter(t.getStartDate()));

        if (overlaps) {
            throw new TermExceptions.InvalidTermException("Term dates overlap with an existing term in this academic year");
        }
    }
}