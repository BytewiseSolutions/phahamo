package ls.co.bytewise.api.export;

import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.timetable.Timetable;
import ls.co.bytewise.api.timetable.TimetableRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
public class SchoolExportService {
    private final SchoolClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final TimetableRepository timetableRepository;
    private final SchoolRepository schoolRepository;
    private final TermRepository termRepository;
    private final SchoolExportRenderer renderer;

    public SchoolExportService(SchoolClassRepository classRepository, StudentRepository studentRepository,
                               TimetableRepository timetableRepository, SchoolRepository schoolRepository,
                               TermRepository termRepository, SchoolExportRenderer renderer) {
        this.classRepository = classRepository;
        this.studentRepository = studentRepository;
        this.timetableRepository = timetableRepository;
        this.schoolRepository = schoolRepository;
        this.termRepository = termRepository;
        this.renderer = renderer;
    }

    @Transactional(readOnly = true)
    public byte[] classList(User user, Long classId, boolean pdf) {
        SchoolClass schoolClass = getClass(classId);
        ensureClassAccess(user, schoolClass);
        List<Student> students = studentRepository.findAllBySchoolIdAndCurrentClassId(TenantContext.getSchoolId(), classId)
                .stream().sorted(Comparator.comparing(s -> s.getUser().getLastName(), String.CASE_INSENSITIVE_ORDER)).toList();
        if (pdf) return renderer.renderClassList(school(), schoolClass, students, termName());
        return excel(students);
    }

    @Transactional(readOnly = true)
    public byte[] classTimetable(User user, Long classId) {
        SchoolClass schoolClass = getClass(classId);
        ensureClassAccess(user, schoolClass);
        List<Timetable> slots = timetableRepository.findAllByAssignment_SchoolClass_IdAndSchoolId(classId, TenantContext.getSchoolId());
        return renderer.renderTimetable(school(), schoolClass.getName(), null, slots, termName());
    }

    @Transactional(readOnly = true)
    public byte[] teacherTimetable(User user, Long teacherId) {
        if (user.hasRole(UserRole.TEACHER) && !user.getId().equals(teacherId)) {
            throw new AccessDeniedException("Teachers may only export their own timetable");
        }
        List<Timetable> slots = timetableRepository.findAllByAssignment_Teacher_IdAndSchoolId(teacherId, TenantContext.getSchoolId());
        String teacherName = slots.stream().findFirst().map(s -> s.getAssignment().getTeacher().getFullName()).orElse("Teacher");
        return renderer.renderTimetable(school(), null, teacherName, slots, termName());
    }

    private SchoolClass getClass(Long id) {
        return classRepository.findByIdAndSchool_Id(id, TenantContext.getSchoolId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
    }

    private void ensureClassAccess(User user, SchoolClass schoolClass) {
        if (user.hasRole(UserRole.SCHOOL_ADMIN) || user.hasRole(UserRole.SYSTEM_ADMIN)) return;
        if (user.hasRole(UserRole.TEACHER) && timetableRepository
                .findAllByAssignment_SchoolClass_IdAndSchoolId(schoolClass.getId(), TenantContext.getSchoolId()).stream()
                .anyMatch(s -> s.getAssignment().getTeacher() != null && user.getId().equals(s.getAssignment().getTeacher().getId()))) return;
        throw new AccessDeniedException("You may only export your own classes");
    }

    private School school() { return schoolRepository.findById(TenantContext.getSchoolId()).orElseThrow(); }
    private String termName() { return termRepository.findBySchoolIdOrderByStartDateDesc(TenantContext.getSchoolId()).stream().findFirst().map(Term::getName).orElse("Current term"); }
    private byte[] excel(List<Student> students) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Class List");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Admission Number");
            for (int i = 0; i < students.size(); i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(students.get(i).getUser().getFullName());
                row.createCell(1).setCellValue(students.get(i).getAdmissionNumber());
            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create Excel class list", e);
        }
    }
}
