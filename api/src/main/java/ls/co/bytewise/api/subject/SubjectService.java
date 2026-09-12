package ls.co.bytewise.api.subject;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignmentRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SchoolRepository schoolRepository;
    private final SubjectClassAssignmentRepository assignmentRepository;

    public SubjectService(SubjectRepository subjectRepository,
                          SchoolRepository schoolRepository,
                          SubjectClassAssignmentRepository assignmentRepository) {
        this.subjectRepository = subjectRepository;
        this.schoolRepository = schoolRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional
    public SubjectDto createSubject(User creator, SubjectDto dto) {
        if (subjectRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new SubjectExceptions.DuplicateSubjectCodeException("A subject with this code already exists");
        }

        Long schoolId = TenantContext.getSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("Active school not found"));

        Subject subject = new Subject();
        subject.setSchool(school);
        subject.setName(dto.getName());
        subject.setCode(dto.getCode());
        subject.setCreatedBy(creator);
        subject.setUpdatedBy(creator);

        Subject saved = subjectRepository.save(subject);
        return new SubjectDto(saved);
    }

    public List<SubjectDto> listSubjects() {
        return subjectRepository.findAll().stream()
                .map(SubjectDto::new)
                .toList();
    }

    public SubjectDto getSubject(Long id) {
        return new SubjectDto(findOrThrow(id));
    }

    @Transactional
    public SubjectDto updateSubject(User editor, Long id, SubjectDto dto) {
        Subject subject = findOrThrow(id);

        if (subjectRepository.existsByCodeIgnoreCaseAndIdNot(dto.getCode(), id)) {
            throw new SubjectExceptions.DuplicateSubjectCodeException("A subject with this code already exists");
        }

        subject.setName(dto.getName());
        subject.setCode(dto.getCode());
        subject.setUpdatedBy(editor);

        Subject saved = subjectRepository.save(subject);
        return new SubjectDto(saved);
    }

    @Transactional
    public void deleteSubject(Long id) {
        Subject subject = findOrThrow(id);

        if (assignmentRepository.existsBySubjectId(id)) {
            throw new SubjectExceptions.LinkedDataExistsException(
                    "Subject cannot be deleted because it is assigned to one or more classes");
        }

        subjectRepository.delete(subject);
    }

    private Subject findOrThrow(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new SubjectExceptions.SubjectNotFoundException("Subject not found"));
    }
}