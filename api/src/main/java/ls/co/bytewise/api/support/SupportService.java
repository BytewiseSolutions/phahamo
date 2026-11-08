package ls.co.bytewise.api.support;

import ls.co.bytewise.api.mail.MailService;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SupportService {

    private final MailService mailService;
    private final SchoolRepository schoolRepository;
    private final String supportEmail;

    public SupportService(MailService mailService,
                          SchoolRepository schoolRepository,
                          @Value("${support.email:${spring.mail.username}}") String supportEmail) {
        this.mailService = mailService;
        this.schoolRepository = schoolRepository;
        this.supportEmail = supportEmail;
    }

    public void contactSupport(User user, SupportRequest request) {
        School school = currentSchool(user);
        String schoolName = school == null ? "No school assigned" : school.getName();
        String role = user.getRoles().stream().findFirst().map(Enum::name).orElse("UNKNOWN");

        String details = "Name: " + user.getFullName() + "\n"
                + "Email: " + user.getEmail() + "\n"
                + "Role: " + role + "\n"
                + "School: " + schoolName + "\n\n"
                + "Question:\n" + request.getQuestion();

        mailService.send(supportEmail, "Phahamo support: " + request.getSubject(), details, school);
        mailService.send(user.getEmail(), "Your Phahamo support request was sent",
                "Hi " + user.getFirstName() + ",\n\n"
                        + "Your support question has been sent to Bytewise.\n\n"
                        + "Subject: " + request.getSubject() + "\n\n"
                        + "We will review it and respond to you as soon as possible.", school);
    }

    private School currentSchool(User user) {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId != null) {
            return schoolRepository.findById(schoolId).orElse(null);
        }
        return user.getSchools().stream().findFirst().orElse(null);
    }
}
