package ls.co.bytewise.api.schoolclass;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SchoolClassDto extends BaseDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Grade level is required")
    private Integer gradeLevel;

    @NotNull(message = "Academic year is required")
    private Long academicYearId;

    private String academicYearName;

    private Long classTeacherId;

    private String classTeacherName;

    public SchoolClassDto(SchoolClass schoolClass) {
        super(schoolClass);
        this.name = schoolClass.getName();
        this.gradeLevel = schoolClass.getGradeLevel();
        this.academicYearId = schoolClass.getAcademicYear().getId();
        this.academicYearName = schoolClass.getAcademicYear().getName();

        if (schoolClass.getClassTeacher() != null) {
            this.classTeacherId = schoolClass.getClassTeacher().getId();
            this.classTeacherName = schoolClass.getClassTeacher().getFullName();
        }
    }
}