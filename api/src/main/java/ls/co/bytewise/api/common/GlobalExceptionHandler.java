package ls.co.bytewise.api.common;

import ls.co.bytewise.api.academicyear.AcademicYearExceptions;
import ls.co.bytewise.api.assessment.AssessmentExceptions;
import ls.co.bytewise.api.parent.ParentExceptions;
import ls.co.bytewise.api.school.SchoolExceptions;
import ls.co.bytewise.api.schoolclass.SchoolClassExceptions;
import ls.co.bytewise.api.security.MissingTenantException;
import ls.co.bytewise.api.student.StudentExceptions;
import ls.co.bytewise.api.subject.SubjectExceptions;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignmentExceptions;
import ls.co.bytewise.api.teacher.TeacherExceptions;
import ls.co.bytewise.api.term.TermExceptions;
import ls.co.bytewise.api.timetable.TimetableExceptions;
import ls.co.bytewise.api.timetable.lesson.LessonExceptions;
import ls.co.bytewise.api.timetable.period.PeriodExceptions;
import ls.co.bytewise.api.user.UserExceptions;
import ls.co.bytewise.api.user.auth.InvalidTokenException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "The requested operation violates a data constraint"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", "Validation failed");
        body.put("errors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidToken(InvalidTokenException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MissingTenantException.class)
    public ResponseEntity<Map<String, Object>> handleMissingTenant(MissingTenantException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoResourceFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Endpoint not found");
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPassword(InvalidPasswordException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SchoolExceptions.SchoolNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSchoolNotFound(SchoolExceptions.SchoolNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SchoolExceptions.InvalidTimezoneException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTimezone(SchoolExceptions.InvalidTimezoneException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "You do not have access to this resource");
    }
    @ExceptionHandler(UserExceptions.UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserExceptions.UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserExceptions.InvalidRoleAssignmentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRoleAssignment(UserExceptions.InvalidRoleAssignmentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SchoolExceptions.InvalidSchoolProfileException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSchoolProfile(SchoolExceptions.InvalidSchoolProfileException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AcademicYearExceptions.AcademicYearNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAcademicYearNotFound(AcademicYearExceptions.AcademicYearNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AcademicYearExceptions.LinkedDataExistsException.class)
    public ResponseEntity<Map<String, Object>> handleLinkedDataExists(AcademicYearExceptions.LinkedDataExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(TermExceptions.TermNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTermNotFound(TermExceptions.TermNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TermExceptions.InvalidTermException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTerm(TermExceptions.InvalidTermException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AcademicYearExceptions.InvalidAcademicYearStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAcademicYearState(AcademicYearExceptions.InvalidAcademicYearStateException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SchoolClassExceptions.SchoolClassNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSchoolClassNotFound(SchoolClassExceptions.SchoolClassNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SchoolClassExceptions.InvalidSchoolClassException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSchoolClass(SchoolClassExceptions.InvalidSchoolClassException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SchoolClassExceptions.DuplicateClassNameException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateClassName(SchoolClassExceptions.DuplicateClassNameException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SubjectExceptions.SubjectNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSubjectNotFound(SubjectExceptions.SubjectNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SubjectExceptions.DuplicateSubjectCodeException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateSubjectCode(SubjectExceptions.DuplicateSubjectCodeException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SubjectClassAssignmentExceptions.AssignmentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAssignmentNotFound(SubjectClassAssignmentExceptions.AssignmentNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SubjectClassAssignmentExceptions.InvalidAssignmentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAssignment(SubjectClassAssignmentExceptions.InvalidAssignmentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SubjectClassAssignmentExceptions.DuplicateAssignmentException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateAssignment(SubjectClassAssignmentExceptions.DuplicateAssignmentException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SubjectExceptions.LinkedDataExistsException.class)
    public ResponseEntity<Map<String, Object>> handleSubjectLinkedDataExists(SubjectExceptions.LinkedDataExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(StudentExceptions.StudentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStudentNotFound(StudentExceptions.StudentNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(StudentExceptions.InvalidStudentOperationException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStudentOperation(StudentExceptions.InvalidStudentOperationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ParentExceptions.ParentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleParentNotFound(ParentExceptions.ParentNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(ParentExceptions.InvalidSearchException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSearch(ParentExceptions.InvalidSearchException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ParentExceptions.DuplicateLinkException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateLink(ParentExceptions.DuplicateLinkException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ParentExceptions.LinkNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleLinkNotFound(ParentExceptions.LinkNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TeacherExceptions.TeacherNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTeacherNotFound(
            TeacherExceptions.TeacherNotFoundException ex) {

        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TeacherExceptions.TeacherAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleTeacherAlreadyExists(
            TeacherExceptions.TeacherAlreadyExistsException ex) {

        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }
    @ExceptionHandler(PeriodExceptions.PeriodNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePeriodNotFound(PeriodExceptions.PeriodNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PeriodExceptions.InvalidPeriodException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPeriod(PeriodExceptions.InvalidPeriodException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TimetableExceptions.TimetableNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTimetableNotFound(TimetableExceptions.TimetableNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TimetableExceptions.TimetableConflictException.class)
    public ResponseEntity<Map<String, Object>> handleTimetableConflict(TimetableExceptions.TimetableConflictException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(LessonExceptions.LessonNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleLessonNotFound(LessonExceptions.LessonNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(LessonExceptions.InvalidLessonException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidLesson(LessonExceptions.InvalidLessonException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(LessonExceptions.ActiveSchoolNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleActiveSchoolNotFound(LessonExceptions.ActiveSchoolNotFoundException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AssessmentExceptions.AssessmentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAssessmentNotFound(AssessmentExceptions.AssessmentNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AssessmentExceptions.InvalidAssessmentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAssessment(AssessmentExceptions.InvalidAssessmentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}