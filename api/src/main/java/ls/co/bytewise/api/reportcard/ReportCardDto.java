package ls.co.bytewise.api.reportcard;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReportCardDto {
    private String schoolName;
    private String schoolAddress;
    private String schoolPhone;
    private String schoolEmail;
    private byte[] schoolLogo;
    private String schoolLogoMimeType;

    private String studentFullName;
    private String admissionNumber;
    private String className;
    private String termName;
    private String academicYearName;

    private List<SubjectLineDto> subjects;
    private Double averageMark;
    private String overallGrade;
    private String overallRemark;

    private Integer attendancePresent;
    private Integer attendanceAbsent;
    private Integer attendanceLate;
    private Integer attendanceExcused;
    private Integer attendanceTotal;
    private Double attendancePercent;

    private String principalName;
    private LocalDate generatedDate;

    @Getter
    @Setter
    public static class SubjectLineDto {
        private String subjectName;
        private Double finalMark;
        private String grade;
        private String remark;
    }
}