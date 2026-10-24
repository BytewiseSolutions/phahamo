package ls.co.bytewise.api.imports;

import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Gender;
import ls.co.bytewise.api.student.StudentDto;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserDto;
import ls.co.bytewise.api.user.UserRepository;
import ls.co.bytewise.api.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

@Service
public class StudentImportService {

    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
    };

    private final XlsxRowReader rowReader;
    private final UserService userService;
    private final UserRepository userRepository;
    private final SchoolClassRepository schoolClassRepository;

    public StudentImportService(XlsxRowReader rowReader,
                                UserService userService,
                                UserRepository userRepository,
                                SchoolClassRepository schoolClassRepository) {
        this.rowReader = rowReader;
        this.userService = userService;
        this.userRepository = userRepository;
        this.schoolClassRepository = schoolClassRepository;
    }

    public ImportResultDto process(User creator, MultipartFile file, boolean dryRun) {
        List<String[]> rows;
        try {
            rows = rowReader.readRows(file);
        } catch (IOException e) {
            throw new ImportExceptions.InvalidFileException("Could not read file: " + e.getMessage());
        }

        ImportResultDto result = new ImportResultDto();
        result.getSummary().setTotalRows(rows.size());

        Long schoolId = TenantContext.getSchoolId();

        for (int i = 0; i < rows.size(); i++) {
            int rowNumber = i + 2;
            String[] cols = rows.get(i);

            ImportRowResultDto rowResult = new ImportRowResultDto();
            rowResult.setRowNumber(rowNumber);
            rowResult.setFirstName(get(cols, 0));
            rowResult.setLastName(get(cols, 1));
            rowResult.setEmail(get(cols, 2));

            try {
                UserDto userDto = mapRow(cols, schoolId);
                if (dryRun) {
                    validateOnly(userDto);
                    result.getValidRows().add(rowResult);
                } else {
                    UserDto created = userService.createUserInNewTransaction(creator, userDto);
                    rowResult.setGeneratedId(created.getStudent() != null
                            ? created.getStudent().getAdmissionNumber() : null);
                    result.getValidRows().add(rowResult);
                }
            } catch (Exception e) {
                rowResult.setReason(e.getMessage());
                result.getInvalidRows().add(rowResult);
            }
        }

        result.getSummary().setValidCount(result.getValidRows().size());
        result.getSummary().setInvalidCount(result.getInvalidRows().size());
        result.getSummary().setAddedCount(dryRun ? 0 : result.getValidRows().size());
        result.getSummary().setSkippedCount(result.getInvalidRows().size());

        return result;
    }

    private UserDto mapRow(String[] cols, Long schoolId) {
        String firstName = get(cols, 0);
        String lastName = get(cols, 1);
        String email = get(cols, 2);
        String phone = get(cols, 3);
        String dateOfBirthRaw = get(cols, 4);
        String genderRaw = get(cols, 5);
        String classNameRaw = get(cols, 6);

        if (firstName == null || firstName.isBlank()) throw new ImportExceptions.ImportValidationException("First name is required");
        if (lastName == null || lastName.isBlank()) throw new ImportExceptions.ImportValidationException("Last name is required");
        if (email == null || email.isBlank()) throw new ImportExceptions.ImportValidationException("Email is required");
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new ImportExceptions.ImportValidationException("Invalid email format");
        if (dateOfBirthRaw == null || dateOfBirthRaw.isBlank()) throw new ImportExceptions.ImportValidationException("Date of birth is required");
        if (genderRaw == null || genderRaw.isBlank()) throw new ImportExceptions.ImportValidationException("Gender is required");

        Instant dateOfBirth = parseDate(dateOfBirthRaw);
        Gender gender = parseGender(genderRaw);
        SchoolClass schoolClass = resolveClass(classNameRaw, schoolId);

        StudentDto studentDto = new StudentDto();
        studentDto.setDateOfBirth(dateOfBirth);
        studentDto.setGender(gender);
        studentDto.setSchoolClassId(schoolClass != null ? schoolClass.getId() : null);

        UserDto dto = new UserDto();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setRoles(Set.of(UserRole.STUDENT));
        dto.setStudent(studentDto);

        return dto;
    }

    private void validateOnly(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ImportExceptions.ImportValidationException("Email already exists in system");
        }
    }

    private SchoolClass resolveClass(String className, Long schoolId) {
        if (className == null || className.isBlank()) return null;
        return schoolClassRepository.findByNameAndSchool_Id(className.trim(), schoolId)
                .orElseThrow(() -> new ImportExceptions.ImportValidationException(
                        "Class '" + className + "' not found"));
    }

    private Instant parseDate(String raw) {
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                LocalDate ld = LocalDate.parse(raw, fmt);
                return ld.atStartOfDay(ZoneOffset.UTC).toInstant();
            } catch (DateTimeParseException ignored) { }
        }
        throw new ImportExceptions.ImportValidationException(
                "Invalid date '" + raw + "' (use YYYY-MM-DD)");
    }

    private Gender parseGender(String raw) {
        try {
            return Gender.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ImportExceptions.ImportValidationException(
                    "Invalid gender '" + raw + "' (must be MALE or FEMALE)");
        }
    }

    private String get(String[] cols, int idx) {
        return idx < cols.length ? cols[idx] : null;
    }
}