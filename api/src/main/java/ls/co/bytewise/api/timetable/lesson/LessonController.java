package ls.co.bytewise.api.timetable.lesson;

import ls.co.bytewise.api.user.User;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/class/{classId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<LessonDto> getLessonsForClass(@AuthenticationPrincipal User currentUser,
                                              @PathVariable Long classId,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return lessonService.getOrCreateLessonsForClassAndDate(currentUser, classId, date);
    }

    @GetMapping("/teacher/{teacherId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<LessonDto> getLessonsForTeacher(@AuthenticationPrincipal User currentUser,
                                                @PathVariable Long teacherId,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return lessonService.getOrCreateLessonsForTeacherAndDate(currentUser, teacherId, date);
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public LessonDto getLesson(@PathVariable Long id) {
        return lessonService.getLesson(id);
    }

    @PatchMapping("/{id}/status")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_TEACHER"})
    public LessonDto updateStatus(@AuthenticationPrincipal User currentUser,
                                  @PathVariable Long id,
                                  @RequestParam LessonStatus status) {
        return lessonService.updateStatus(currentUser, id, status);
    }
}