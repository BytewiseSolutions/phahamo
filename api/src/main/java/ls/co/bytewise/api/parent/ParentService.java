package ls.co.bytewise.api.parent;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
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
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;

    public ParentService(ParentRepository parentRepository,
                         SchoolRepository schoolRepository,
                         StudentRepository studentRepository) {
        this.parentRepository = parentRepository;
        this.schoolRepository = schoolRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public ParentDto createParent(User creator, ParentDto dto) {
        Long schoolId = TenantContext.getSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("Active school not found"));

        Parent parent = new Parent();
        parent.setSchool(school);
        parent.setFirstName(dto.getFirstName());
        parent.setLastName(dto.getLastName());
        parent.setEmail(dto.getEmail());
        parent.setPhone(dto.getPhone());
        parent.setCreatedBy(creator);
        parent.setUpdatedBy(creator);

        Parent saved = parentRepository.save(parent);
        return new ParentDto(saved);
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
        return studentRepository.findParentsByStudentId(studentId).stream()
                .map(StudentParentDto::new)
                .toList();
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
    }

    public List<ParentDto> listParents() {
        Long schoolId = TenantContext.getSchoolId();
        return parentRepository.findAllBySchoolId(schoolId).stream()
                .map(ParentDto::new)
                .toList();
    }

    public ParentDto getParent(Long id) {
        return new ParentDto(findOrThrow(id));
    }

    @Transactional
    public ParentDto updateParent(User editor, Long id, ParentDto dto) {
        Parent parent = findOrThrow(id);

        parent.setFirstName(dto.getFirstName());
        parent.setLastName(dto.getLastName());
        parent.setEmail(dto.getEmail());
        parent.setPhone(dto.getPhone());
        parent.setUpdatedBy(editor);

        Parent saved = parentRepository.save(parent);
        return new ParentDto(saved);
    }

    @Transactional
    public void deleteParent(Long id) {
        Parent parent = findOrThrow(id);

        if (studentRepository.existsAnyLinkForParent(id)) {
            throw new ParentExceptions.DuplicateLinkException(
                    "Parent cannot be deleted while linked to one or more students");
        }

        parentRepository.delete(parent);
    }

    private Parent findOrThrow(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return parentRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ParentExceptions.ParentNotFoundException("Parent not found"));
    }
}