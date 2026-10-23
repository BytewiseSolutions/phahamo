package ls.co.bytewise.api.imports;

import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.teacher.EmploymentType;
import ls.co.bytewise.api.teacher.TeacherDto;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserDto;
import ls.co.bytewise.api.user.UserRepository;
import ls.co.bytewise.api.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

@Service
public class TeacherImportService {

    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
    };

    private final XlsxRowReader rowReader;
    private final UserService userService;
    private final UserRepository userRepository;

    public TeacherImportService(XlsxRowReader rowReader,
                                UserService userService,
                                UserRepository userRepository) {
        this.rowReader = rowReader;
        this.userService = userService;
        this.userRepository = userRepository;
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

        for (int i = 0; i < rows.size(); i++) {
            int rowNumber = i + 2;
            String[] cols = rows.get(i);

            ImportRowResultDto rowResult = new ImportRowResultDto();
            rowResult.setRowNumber(rowNumber);
            rowResult.setFirstName(get(cols, 0));
            rowResult.setLastName(get(cols, 1));
            rowResult.setEmail(get(cols, 2));

            try {
                UserDto userDto = mapRow(cols);
                if (dryRun) {
                    validateOnly(userDto);
                    result.getValidRows().add(rowResult);
                } else {
                    UserDto created = userService.createUserInNewTransaction(creator, userDto);
                    rowResult.setGeneratedId(created.getTeacher() != null
                            ? created.getTeacher().getEmployeeNumber() : null);
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

    private UserDto mapRow(String[] cols) {
        String firstName = get(cols, 0);
        String lastName = get(cols, 1);
        String email = get(cols, 2);
        String phone = get(cols, 3);
        String qualifications = get(cols, 4);
        String dateJoinedRaw = get(cols, 5);
        String specialization = get(cols, 6);
        String employmentTypeRaw = get(cols, 7);

        if (firstName == null || firstName.isBlank()) throw new ImportExceptions.ImportValidationException("First name is required");
        if (lastName == null || lastName.isBlank()) throw new ImportExceptions.ImportValidationException("Last name is required");
        if (email == null || email.isBlank()) throw new ImportExceptions.ImportValidationException("Email is required");
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new ImportExceptions.ImportValidationException("Invalid email format");
        if (employmentTypeRaw == null || employmentTypeRaw.isBlank()) throw new ImportExceptions.ImportValidationException("Employment type is required");

        EmploymentType employmentType = parseEmploymentType(employmentTypeRaw);
        LocalDate dateJoined = parseDate(dateJoinedRaw);

        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setQualifications(qualifications);
        teacherDto.setDateJoined(dateJoined);
        teacherDto.setSpecialization(specialization);
        teacherDto.setEmploymentType(employmentType);

        UserDto dto = new UserDto();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setRoles(Set.of(UserRole.TEACHER));
        dto.setTeacher(teacherDto);

        return dto;
    }

    private void validateOnly(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ImportExceptions.ImportValidationException("Email already exists in system");
        }
    }

    private EmploymentType parseEmploymentType(String raw) {
        try {
            return EmploymentType.valueOf(raw.trim().toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException e) {
            throw new ImportExceptions.ImportValidationException(
                    "Invalid employment type '" + raw + "' (must be FULL_TIME, PART_TIME, or CONTRACT)");
        }
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(raw, fmt);
            } catch (DateTimeParseException ignored) { }
        }
        throw new ImportExceptions.ImportValidationException(
                "Invalid date '" + raw + "' (use YYYY-MM-DD)");
    }

    private String get(String[] cols, int idx) {
        return idx < cols.length ? cols[idx] : null;
    }
}