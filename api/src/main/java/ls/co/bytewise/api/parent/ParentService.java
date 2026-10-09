package ls.co.bytewise.api.parent;

import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentExceptions;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParentService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private static final int MAX_SEARCH_RESULTS = 10;

    public ParentService(ParentRepository parentRepository,
                         StudentRepository studentRepository) {
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public ParentDto createProfileFor(User creator, User user, ParentDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        Parent parent = new Parent();
        parent.setUser(user);
        parent.setSchool(user.getSchools().stream()
                .filter(s -> s.getId().equals(schoolId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User is not attached to active school")));
        parent.setRelationship(dto.getRelationship());
        parent.setCreatedBy(creator);
        parent.setUpdatedBy(creator);

        Parent saved = parentRepository.save(parent);
        return new ParentDto(saved);
    }

    @Transactional
    public StudentParentDto linkExistingParent(User creator, Long studentId, Long parentId) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new StudentExceptions.StudentNotFoundException("Student not found"));

        Parent parent = findOrThrow(parentId);

        if (studentRepository.existsParentLink(studentId, parentId)) {
            throw new ParentExceptions.DuplicateLinkException("This parent is already linked to this student");
        }

        student.getParents().add(parent);
        student.setUpdatedBy(creator);
        studentRepository.save(student);

        return new StudentParentDto(parent);
    }

    public List<StudentParentDto> listParentsForStudent(Long studentId) {
        Long schoolId = TenantContext.getSchoolId();
        if (!studentRepository.existsByIdAndSchoolId(studentId, schoolId)) {
            throw new StudentExceptions.StudentNotFoundException("Student not found");
        }
        return studentRepository.findParentsByStudentId(studentId).stream()
                .map(StudentParentDto::new)
                .toList();
    }

    @Transactional
    public ParentDto updateParent(User editor, Long parentId, ParentDto dto) {
        Parent parent = findOrThrow(parentId);

        parent.setRelationship(dto.getRelationship());
        parent.setUpdatedBy(editor);

        Parent saved = parentRepository.save(parent);
        return new ParentDto(saved);
    }

    @Transactional
    public void unlinkParent(Long studentId, Long parentId) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new StudentExceptions.StudentNotFoundException("Student not found"));

        if (!studentRepository.existsParentLink(studentId, parentId)) {
            throw new ParentExceptions.LinkNotFoundException("Link not found");
        }

        Parent parent = findOrThrow(parentId);

        student.getParents().remove(parent);
        studentRepository.saveAndFlush(student);

        if (!studentRepository.existsAnyLinkForParent(parentId)) {
            parentRepository.delete(parent);
        }
    }

    private Parent findOrThrow(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return parentRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ParentExceptions.ParentNotFoundException("Parent not found"));
    }

    public List<StudentParentDto> searchParents(String email, String phone) {
        Long schoolId = TenantContext.getSchoolId();

        boolean hasEmail = email != null && !email.isBlank();
        boolean hasPhone = phone != null && !phone.isBlank();

        if (hasEmail == hasPhone) {
            throw new ParentExceptions.InvalidSearchException(
                    "Provide exactly one of email or phone");
        }

        List<Parent> results = hasEmail
                ? parentRepository.searchByEmail(schoolId, email, org.springframework.data.domain.PageRequest.of(0, MAX_SEARCH_RESULTS))
                : parentRepository.searchByPhone(schoolId, phone, org.springframework.data.domain.PageRequest.of(0, MAX_SEARCH_RESULTS));

        return results.stream().map(StudentParentDto::new).toList();
    }

}