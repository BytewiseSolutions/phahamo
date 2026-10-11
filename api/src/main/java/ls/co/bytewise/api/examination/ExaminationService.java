package ls.co.bytewise.api.examination;

import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.subject.Subject;
import ls.co.bytewise.api.subject.SubjectRepository;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final TermRepository termRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;

    public ExaminationService(ExaminationRepository examinationRepository,
                              TermRepository termRepository,
                              SubjectRepository subjectRepository,
                              SchoolClassRepository schoolClassRepository) {
        this.examinationRepository = examinationRepository;
        this.termRepository = termRepository;
        this.subjectRepository = subjectRepository;
        this.schoolClassRepository = schoolClassRepository;
    }

    @Transactional
    public ExaminationDto create(User currentUser, ExaminationDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        Term term = termRepository.findById(dto.getTermId())
                .orElseThrow(() -> new ExaminationExceptions.InvalidExaminationException("Term not found"));

        if (!term.getSchool().getId().equals(schoolId)) {
            throw new ExaminationExceptions.InvalidExaminationException("Term does not belong to the active school");
        }

        validateDatesAndLinks(dto, schoolId);

        List<Examination> overlapping = examinationRepository.findOverlapping(
                schoolId,
                term.getId(),
                dto.getStartDate(),
                dto.getEndDate(),
                -1L
        );

        if (!overlapping.isEmpty()) {
            throw new ExaminationExceptions.OverlappingExaminationException(
                    "An examination already exists within this date range: " + overlapping.get(0).getName());
        }

        Examination examination = new Examination();
        examination.setSchool(term.getSchool());
        examination.setTerm(term);
        examination.setName(dto.getName().trim());
        examination.setStartDate(dto.getStartDate());
        examination.setEndDate(dto.getEndDate());
        examination.setCreatedBy(currentUser);
        examination.setUpdatedBy(currentUser);

        for (ExaminationDto.SubjectClassLink link : dto.getSubjectClasses()) {
            examination.getSubjectClasses().add(buildLink(examination, link));
        }

        Examination saved = examinationRepository.save(examination);
        return new ExaminationDto(saved);
    }

    public List<ExaminationDto> listAll() {
        Long schoolId = TenantContext.getSchoolId();
        return examinationRepository.findAllBySchoolIdOrderByStartDateDesc(schoolId).stream()
                .map(ExaminationDto::new)
                .toList();
    }

    public List<ExaminationDto> listByTerm(Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        return examinationRepository.findAllByTermIdAndSchoolIdOrderByStartDateDesc(termId, schoolId).stream()
                .map(ExaminationDto::new)
                .toList();
    }

    public ExaminationDto get(Long id) {
        return new ExaminationDto(findOrThrow(id));
    }

    @Transactional
    public ExaminationDto update(User editor, Long id, ExaminationDto dto) {
        Long schoolId = TenantContext.getSchoolId();
        Examination examination = findOrThrow(id);

        Term term = termRepository.findById(dto.getTermId())
                .orElseThrow(() -> new ExaminationExceptions.InvalidExaminationException("Term not found"));

        if (!term.getSchool().getId().equals(schoolId)) {
            throw new ExaminationExceptions.InvalidExaminationException("Term does not belong to the active school");
        }

        validateDatesAndLinks(dto, schoolId);

        List<Examination> overlapping = examinationRepository.findOverlapping(
                schoolId,
                term.getId(),
                dto.getStartDate(),
                dto.getEndDate(),
                id
        );

        if (!overlapping.isEmpty()) {
            throw new ExaminationExceptions.OverlappingExaminationException(
                    "An examination already exists within this date range: " + overlapping.get(0).getName());
        }

        examination.setTerm(term);
        examination.setName(dto.getName().trim());
        examination.setStartDate(dto.getStartDate());
        examination.setEndDate(dto.getEndDate());
        examination.setUpdatedBy(editor);

        examination.getSubjectClasses().clear();
        for (ExaminationDto.SubjectClassLink link : dto.getSubjectClasses()) {
            examination.getSubjectClasses().add(buildLink(examination, link));
        }

        Examination saved = examinationRepository.save(examination);
        return new ExaminationDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        Examination examination = findOrThrow(id);
        examinationRepository.delete(examination);
    }

    private void validateDatesAndLinks(ExaminationDto dto, Long schoolId) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ExaminationExceptions.InvalidExaminationException("Name is required");
        }
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new ExaminationExceptions.InvalidExaminationException("Start and end dates are required");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new ExaminationExceptions.InvalidExaminationException("End date must be on or after start date");
        }
        if (dto.getSubjectClasses() == null || dto.getSubjectClasses().isEmpty()) {
            throw new ExaminationExceptions.InvalidExaminationException("At least one subject and class is required");
        }
    }

    private ExaminationSubjectClass buildLink(Examination examination, ExaminationDto.SubjectClassLink link) {
        Long schoolId = TenantContext.getSchoolId();

        Subject subject = subjectRepository.findById(link.getSubjectId())
                .orElseThrow(() -> new ExaminationExceptions.InvalidExaminationException("Subject not found"));

        if (!subject.getSchool().getId().equals(schoolId)) {
            throw new ExaminationExceptions.InvalidExaminationException("Subject does not belong to the active school");
        }

        SchoolClass schoolClass = schoolClassRepository.findById(link.getClassId())
                .orElseThrow(() -> new ExaminationExceptions.InvalidExaminationException("Class not found"));

        if (!schoolClass.getSchool().getId().equals(schoolId)) {
            throw new ExaminationExceptions.InvalidExaminationException("Class does not belong to the active school");
        }

        ExaminationSubjectClass entity = new ExaminationSubjectClass();
        entity.setExamination(examination);
        entity.setSubject(subject);
        entity.setSchoolClass(schoolClass);
        return entity;
    }

    private Examination findOrThrow(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return examinationRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ExaminationExceptions.ExaminationNotFoundException("Examination not found"));
    }
}