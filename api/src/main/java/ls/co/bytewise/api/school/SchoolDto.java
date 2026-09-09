package ls.co.bytewise.api.school;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SchoolDto extends BaseDto {

    @NotBlank(message = "School name is required")
    private String schoolName;

    @NotBlank(message = "School address is required")
    private String schoolAddress;

    @NotBlank(message = "School contact email is required")
    @Email(message = "School contact email must be valid")
    private String schoolContactEmail;

    @NotBlank(message = "School contact phone is required")
    private String schoolContactPhone;

    @NotNull(message = "Term structure is required")
    private TermStructure schoolTermStructure;

    @NotNull(message = "Currency is required")
    private Currency schoolCurrency;

    @NotBlank(message = "Timezone is required")
    private String schoolTimezone;

    private Long schoolLogoId;

    public SchoolDto(School school) {
        super(school);

        this.schoolName = school.getName();
        this.schoolAddress = school.getAddress();
        this.schoolContactEmail = school.getContactEmail();
        this.schoolContactPhone = school.getContactPhone();
        this.schoolTermStructure = school.getTermStructure();
        this.schoolCurrency = school.getCurrency();
        this.schoolTimezone = school.getTimezone();

        if (school.getLogo() != null) {
            this.schoolLogoId = school.getLogo().getId();
        }
    }
}