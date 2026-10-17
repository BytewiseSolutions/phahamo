package ls.co.bytewise.api.fee;

import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FeeStructureDto extends BaseDto {

    private Long classId;
    private String className;
    private Long termId;
    private String termName;
    private String academicYearName;

    private List<FeeItemDto> items;
    private Double total;
    private Double mandatoryTotal;

    public FeeStructureDto(FeeStructure structure) {
        super(structure);
        this.classId = structure.getSchoolClass().getId();
        this.className = structure.getSchoolClass().getName();
        this.termId = structure.getTerm().getId();
        this.termName = structure.getTerm().getName();

        if (structure.getTerm().getAcademicYear() != null) {
            this.academicYearName = structure.getTerm().getAcademicYear().getName();
        }

        this.items = structure.getItems().stream()
                .map(FeeItemDto::new)
                .toList();

        this.total = structure.getItems().stream()
                .mapToDouble(FeeItem::getAmount)
                .sum();

        this.mandatoryTotal = structure.getItems().stream()
                .filter(FeeItem::getMandatory)
                .mapToDouble(FeeItem::getAmount)
                .sum();
    }
}