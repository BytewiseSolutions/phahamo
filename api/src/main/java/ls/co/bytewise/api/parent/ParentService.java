package ls.co.bytewise.api.parent;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParentService {

    private final ParentRepository parentRepository;
    private final SchoolRepository schoolRepository;

    public ParentService(ParentRepository parentRepository, SchoolRepository schoolRepository) {
        this.parentRepository = parentRepository;
        this.schoolRepository = schoolRepository;
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
        parentRepository.delete(parent);
    }

    private Parent findOrThrow(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return parentRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ParentExceptions.ParentNotFoundException("Parent not found"));
    }
}