package ls.co.bytewise.api.parent;

import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentExceptions;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParentService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;

    public ParentService(ParentRepository parentRepository,
                         StudentRepository studentRepository) {
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public StudentParentDto createAndLinkParent(User creator, Long studentId, ParentDto dto) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new StudentExceptions.StudentNotFoundException("Student not found"));

        Parent parent = new Parent();
        parent.setSchool(student.getSchool());
        parent.setFirstName(dto.getFirstName());
        parent.setLastName(dto.getLastName());
        parent.setEmail(dto.getEmail());
        parent.setPhone(dto.getPhone());
        parent.setCreatedBy(creator);
        parent.setUpdatedBy(creator);
        Parent savedParent = parentRepository.save(parent);

        student.getParents().add(savedParent);
        studentRepository.save(student);

        return new StudentParentDto(savedParent);
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
    public StudentParentDto updateParent(
            User editor,
            Long studentId,
            Long parentId,
            ParentDto dto
    ) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() ->
                        new StudentExceptions.StudentNotFoundException("Student not found"));

        Parent parent = findOrThrow(parentId);

        if (!studentRepository.existsParentLink(studentId, parentId)) {
            throw new ParentExceptions.LinkNotFoundException(
                    "This parent is not linked to this student"
            );
        }

        parent.setFirstName(dto.getFirstName());
        parent.setLastName(dto.getLastName());
        parent.setEmail(dto.getEmail());
        parent.setPhone(dto.getPhone());
        parent.setUpdatedBy(editor);

        Parent saved = parentRepository.save(parent);

        return new StudentParentDto(saved);
    }

    @Transactional
    public void unlinkParent(Long studentId, Long parentId) {
        if (!studentRepository.existsParentLink(studentId, parentId)) {
            throw new ParentExceptions.LinkNotFoundException("Link not found");
        }

        Long schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new StudentExceptions.StudentNotFoundException("Student not found"));

        Parent parent = findOrThrow(parentId);

        student.getParents().remove(parent);
        studentRepository.save(student);

        if (!studentRepository.existsAnyLinkForParent(parentId)) {
            parentRepository.delete(parent);
        }
    }

    private Parent findOrThrow(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return parentRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ParentExceptions.ParentNotFoundException("Parent not found"));
    }
}