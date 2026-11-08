package ls.co.bytewise.api.dataexport;

import ls.co.bytewise.api.attendance.Attendance;
import ls.co.bytewise.api.attendance.AttendanceRepository;
import ls.co.bytewise.api.common.DataFile;
import ls.co.bytewise.api.common.DataFileRepository;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.parent.Parent;
import ls.co.bytewise.api.parent.ParentRepository;
import ls.co.bytewise.api.result.TermResult;
import ls.co.bytewise.api.result.TermResultRepository;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.studentfee.StudentFeeAccount;
import ls.co.bytewise.api.studentfee.StudentFeeAccountRepository;
import ls.co.bytewise.api.studentfee.StudentFeePayment;
import ls.co.bytewise.api.studentfee.StudentFeePaymentRepository;
import ls.co.bytewise.api.teacher.Teacher;
import ls.co.bytewise.api.teacher.TeacherRepository;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.mail.MailService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SchoolDataExportService {
    private final SchoolDataExportRepository exportRepository;
    private final DataFileRepository dataFileRepository;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolClassRepository classRepository;
    private final AttendanceRepository attendanceRepository;
    private final TermResultRepository resultRepository;
    private final StudentFeeAccountRepository feeAccountRepository;
    private final StudentFeePaymentRepository paymentRepository;
    private final MailService mailService;
    private final String publicApiUrl;

    public SchoolDataExportService(SchoolDataExportRepository exportRepository, DataFileRepository dataFileRepository,
                                   SchoolRepository schoolRepository, StudentRepository studentRepository,
                                   ParentRepository parentRepository, TeacherRepository teacherRepository,
                                   SchoolClassRepository classRepository, AttendanceRepository attendanceRepository,
                                   TermResultRepository resultRepository, StudentFeeAccountRepository feeAccountRepository,
                                   StudentFeePaymentRepository paymentRepository, MailService mailService,
                                   @Value("${app.public-api-url:http://localhost:8080/api}") String publicApiUrl) {
        this.exportRepository = exportRepository; this.dataFileRepository = dataFileRepository;
        this.schoolRepository = schoolRepository; this.studentRepository = studentRepository;
        this.parentRepository = parentRepository; this.teacherRepository = teacherRepository;
        this.classRepository = classRepository; this.attendanceRepository = attendanceRepository;
        this.resultRepository = resultRepository; this.feeAccountRepository = feeAccountRepository;
        this.paymentRepository = paymentRepository; this.mailService = mailService;
        this.publicApiUrl = publicApiUrl.replaceAll("/$", "");
    }

    @Transactional
    public SchoolDataExport request(User requester) {
        if (!requester.hasRole(UserRole.SCHOOL_ADMIN)) {
            throw new AccessDeniedException("Only school administrators can export school data");
        }
        School school = schoolRepository.findById(TenantContext.getSchoolId()).orElseThrow();
        SchoolDataExport export = new SchoolDataExport();
        export.setToken(UUID.randomUUID().toString());
        export.setSchool(school); export.setRequestedBy(requester);
        export.setStatus(SchoolDataExportStatus.PROCESSING);
        export.setCreatedAt(Instant.now()); export.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        return exportRepository.save(export);
    }

    @Transactional
    public void generate(Long exportId) {
        SchoolDataExport export = exportRepository.findById(exportId).orElseThrow();
        try {
            byte[] zip = buildZip(export.getSchool().getId());
            DataFile file = DataFile.builder().name("school-data-export.zip").type("application/zip")
                    .contents(zip).fileSize((long) zip.length).build();
            file.setCreatedBy(export.getRequestedBy());
            export.setFile(dataFileRepository.save(file));
            export.setStatus(SchoolDataExportStatus.READY); export.setCompletedAt(Instant.now());
            exportRepository.save(export);
            mailService.send(export.getRequestedBy().getEmail(), "Your school data export is ready",
                    "Your school data export is ready. Download it within 24 hours:\n\n"
                            + publicApiUrl + "/data-exports/download/" + export.getToken(), export.getSchool());
        } catch (Exception e) {
            export.setStatus(SchoolDataExportStatus.FAILED); exportRepository.save(export);
            throw new IllegalStateException("Failed to generate school data export", e);
        }
    }

    @Transactional(readOnly = true)
    public DataFile download(String token) {
        SchoolDataExport export = exportRepository.findByToken(token).orElseThrow();
        if (export.getStatus() != SchoolDataExportStatus.READY || export.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("This download link has expired or is not ready");
        }
        return export.getFile();
    }

    private byte[] buildZip(Long schoolId) throws IOException {
        List<Student> students = studentRepository.findAllBySchoolId(schoolId);
        List<Parent> parents = parentRepository.findAllBySchoolId(schoolId);
        List<Teacher> teachers = teacherRepository.findAllBySchoolId(schoolId);
        List<SchoolClass> classes = classRepository.findAllBySchool_Id(schoolId);
        List<Attendance> attendance = attendanceRepository.findAllBySchoolId(schoolId);
        List<TermResult> results = resultRepository.findAllBySchoolId(schoolId);
        List<StudentFeeAccount> fees = feeAccountRepository.findAllBySchoolId(schoolId);
        List<StudentFeePayment> payments = paymentRepository.findAllBySchoolId(schoolId);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream(); ZipOutputStream zip = new ZipOutputStream(output)) {
            add(zip, "students.xlsx", sheet -> {
                header(sheet, "Name", "Email", "Admission Number", "Date of Birth", "Gender", "Class", "Status");
                students.forEach(s -> row(sheet, s.getUser().getFullName(), s.getUser().getEmail(), s.getAdmissionNumber(), s.getDateOfBirth(), s.getGender(), s.getCurrentClass() == null ? "" : s.getCurrentClass().getName(), s.getStatus()));
            });
            add(zip, "parents.xlsx", sheet -> { header(sheet, "Name", "Email", "Phone", "Relationship", "Login Enabled"); parents.forEach(p -> row(sheet, p.getUser().getFullName(), p.getUser().getEmail(), p.getUser().getPhone(), p.getRelationship(), p.isLoginEnabled())); });
            add(zip, "teachers.xlsx", sheet -> { header(sheet, "Name", "Email", "Employee Number", "Qualifications", "Date Joined", "Specialization", "Employment Type"); teachers.forEach(t -> row(sheet, t.getUser().getFullName(), t.getUser().getEmail(), t.getEmployeeNumber(), t.getQualifications(), t.getDateJoined(), t.getSpecialization(), t.getEmploymentType())); });
            add(zip, "classes.xlsx", sheet -> { header(sheet, "Name", "Grade Level", "Academic Year", "Class Teacher"); classes.forEach(c -> row(sheet, c.getName(), c.getGradeLevel(), c.getAcademicYear().getName(), c.getClassTeacher() == null ? "" : c.getClassTeacher().getFullName())); });
            add(zip, "attendance.xlsx", sheet -> { header(sheet, "Date", "Student", "Class", "Status", "Marked By"); attendance.forEach(a -> row(sheet, a.getDate(), a.getStudent().getUser().getFullName(), a.getSchoolClass().getName(), a.getStatus(), a.getMarkedBy() == null ? "" : a.getMarkedBy().getFullName())); });
            add(zip, "results.xlsx", sheet -> { header(sheet, "Student", "Class", "Subject", "Term", "Final Mark", "Grade", "Remark", "Status"); results.forEach(r -> row(sheet, r.getStudent().getUser().getFullName(), r.getSchoolClass().getName(), r.getSubject().getName(), r.getTerm().getName(), r.getFinalMark(), r.getGrade(), r.getRemark(), r.getStatus())); });
            add(zip, "fees.xlsx", sheet -> { header(sheet, "Student", "Class", "Term", "Total Fees", "Adjustments", "Amount Paid", "Balance"); fees.forEach(f -> row(sheet, f.getStudent().getUser().getFullName(), f.getSchoolClass().getName(), f.getTerm().getName(), f.getTotalFees(), f.getTotalAdjustments(), f.getAmountPaid(), f.getBalance())); });
            add(zip, "payments.xlsx", sheet -> { header(sheet, "Student", "Amount", "Method", "Reference", "Paid At", "Status", "Received By"); payments.forEach(p -> row(sheet, p.getAccount().getStudent().getUser().getFullName(), p.getAmount(), p.getMethod(), p.getReference(), p.getPaidAt(), p.getStatus(), p.getReceivedBy() == null ? "" : p.getReceivedBy().getFullName())); });
            zip.finish(); return output.toByteArray();
        }
    }

    private void add(ZipOutputStream zip, String name, Consumer<Sheet> writer) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Data"); writer.accept(sheet); sheet.createFreezePane(0, 1);
            workbook.write(output); zip.putNextEntry(new ZipEntry(name)); zip.write(output.toByteArray()); zip.closeEntry();
        }
    }
    private void header(Sheet sheet, String... values) { row(sheet, (Object[]) values); }
    private void row(Sheet sheet, Object... values) {
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i] == null ? "" : String.valueOf(values[i]));
        }
    }
}
