package ls.co.bytewise.api.student;

import lombok.Getter;

import java.util.List;

@Getter
public class StudentClassFeePreviewDto {
    private final Long studentId;
    private final String currentClassName;
    private final String newClassName;
    private final boolean feesChanged;
    private final List<StudentClassFeePreviewLineDto> terms;

    public StudentClassFeePreviewDto(Long studentId,
                                     String currentClassName,
                                     String newClassName,
                                     boolean feesChanged,
                                     List<StudentClassFeePreviewLineDto> terms) {
        this.studentId = studentId;
        this.currentClassName = currentClassName;
        this.newClassName = newClassName;
        this.feesChanged = feesChanged;
        this.terms = terms;
    }
}
