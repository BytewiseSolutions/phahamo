package ls.co.bytewise.api.examination;

import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExaminationDto extends BaseDto {

    private Long termId;
    private String termName;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<SubjectClassLink> subjectClasses = new ArrayList<>();

    public ExaminationDto(Examination examination) {
        super(examination);
        this.termId = examination.getTerm().getId();
        this.termName = examination.getTerm().getName();
        this.name = examination.getName();
        this.startDate = examination.getStartDate();
        this.endDate = examination.getEndDate();

        for (ExaminationSubjectClass link : examination.getSubjectClasses()) {
            this.subjectClasses.add(new SubjectClassLink(
                    link.getSubject().getId(),
                    link.getSubject().getName(),
                    link.getSchoolClass().getId(),
                    link.getSchoolClass().getName()
            ));
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SubjectClassLink {
        private Long subjectId;
        private String subjectName;
        private Long classId;
        private String className;

        public SubjectClassLink(Long subjectId, String subjectName, Long classId, String className) {
            this.subjectId = subjectId;
            this.subjectName = subjectName;
            this.classId = classId;
            this.className = className;
        }
    }
}