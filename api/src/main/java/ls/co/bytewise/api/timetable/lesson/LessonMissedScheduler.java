package ls.co.bytewise.api.timetable.lesson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class LessonMissedScheduler {

    private static final Logger log = LoggerFactory.getLogger(LessonMissedScheduler.class);

    private final LessonRepository lessonRepository;

    public LessonMissedScheduler(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    /**
     * Runs every 15 minutes. Finds SCHEDULED lessons whose period has ended
     * and marks them as MISSED.
     *
     * TODO: once Attendance and Homework are implemented, only mark MISSED
     * when neither has been recorded for the lesson (see markMissedLessons
     * body below for where those checks belong).
     */
    @Scheduled(fixedRate = 15 * 60 * 1000)
    @Transactional
    public void markMissedLessons() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<Lesson> candidates = lessonRepository
                .findAllByStatusAndDateLessThanEqual(LessonStatus.SCHEDULED, today);

        int missedCount = 0;

        for (Lesson lesson : candidates) {
            LocalDateTime lessonEnd = LocalDateTime.of(
                    lesson.getDate(),
                    lesson.getTimetable().getPeriod().getEndTime()
            );

            if (lessonEnd.isBefore(now)) {
                lesson.setStatus(LessonStatus.MISSED);
                lessonRepository.save(lesson);
                missedCount++;
            }
        }

        if (missedCount > 0) {
            log.info("Marked {} lesson(s) as MISSED", missedCount);
        }
    }
}