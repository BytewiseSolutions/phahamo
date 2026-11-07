package ls.co.bytewise.api.mail;

import jakarta.mail.internet.MimeMessage;
import ls.co.bytewise.api.school.School;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public MailService(JavaMailSender mailSender,
                       org.springframework.core.env.Environment env) {
        this.mailSender = mailSender;
        this.fromAddress = env.getProperty("spring.mail.username");
    }

    @Async
    public void send(String to, String subject, String body) {
        send(to, subject, body, SchoolEmailBranding.generic());
    }

    public void send(String to, String subject, String body, School school) {
        send(to, subject, body, brandingFor(school));
    }

    @Async
    protected void send(String to, String subject, String body, SchoolEmailBranding branding) {
        if (to == null || to.isBlank()) return;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            applyHeaders(helper, to, subject, branding);
            helper.setText(body, htmlBody(body, branding));
            addLogo(helper, branding);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    public void sendWithAttachmentBlocking(String to, String subject, String body,
                                           String attachmentFilename,
                                           byte[] attachmentBytes,
                                           String attachmentMimeType) {
        sendWithAttachmentBlocking(to, subject, body, attachmentFilename, attachmentBytes,
                attachmentMimeType, SchoolEmailBranding.generic());
    }

    public void sendWithAttachmentBlocking(String to, String subject, String body,
                                           String attachmentFilename,
                                           byte[] attachmentBytes,
                                           String attachmentMimeType,
                                           School school) {
        sendWithAttachmentBlocking(to, subject, body, attachmentFilename, attachmentBytes,
                attachmentMimeType, brandingFor(school));
    }

    private void sendWithAttachmentBlocking(String to, String subject, String body,
                                           String attachmentFilename,
                                           byte[] attachmentBytes,
                                           String attachmentMimeType,
                                           SchoolEmailBranding branding) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Recipient email is required");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            applyHeaders(helper, to, subject, branding);
            helper.setText(body, htmlBody(body, branding));
            addLogo(helper, branding);

            helper.addAttachment(
                    attachmentFilename,
                    new ByteArrayResource(attachmentBytes),
                    attachmentMimeType
            );

            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email to " + to, e);
        }
    }

    private SchoolEmailBranding brandingFor(School school) {
        if (school == null) return SchoolEmailBranding.generic();
        var logo = school.getLogo();
        return new SchoolEmailBranding(
                school.getName(),
                school.getContactEmail(),
                logo == null ? null : logo.getContents(),
                logo == null ? null : logo.getContentType()
        );
    }

    private void applyHeaders(MimeMessageHelper helper, String to, String subject,
                              SchoolEmailBranding branding) throws Exception {
        helper.setFrom(fromAddress, branding.name());
        helper.setTo(to);
        if (branding.contactEmail() != null && !branding.contactEmail().isBlank()) {
            helper.setReplyTo(branding.contactEmail());
        }
        helper.setSubject(subject);
    }

    private void addLogo(MimeMessageHelper helper, SchoolEmailBranding branding) throws Exception {
        if (branding.logoContents() != null && branding.logoContents().length > 0) {
            helper.addInline("school-logo", new ByteArrayResource(branding.logoContents()),
                    branding.logoContentType() == null ? "image/png" : branding.logoContentType());
        }
    }

    private String htmlBody(String body, SchoolEmailBranding branding) {
        String safeName = escapeHtml(branding.name());
        String logo = branding.logoContents() == null || branding.logoContents().length == 0
                ? ""
                : "<img src=\"cid:school-logo\" alt=\"" + safeName + " logo\" "
                + "style=\"display:block;max-width:180px;max-height:80px;width:auto;height:auto;margin:0 auto 12px;\">";
        String content = escapeHtml(body).replace("\n", "<br>");
        return "<!doctype html><html><body style=\"margin:0;background:#f4f6f8;font-family:Arial,sans-serif;color:#1f2937;\">"
                + "<div style=\"width:100%;padding:24px 12px;box-sizing:border-box;\"><div style=\"max-width:600px;margin:0 auto;background:#fff;border-radius:10px;overflow:hidden;\">"
                + "<div style=\"padding:24px 20px;text-align:center;border-bottom:1px solid #e5e7eb;\">" + logo
                + "<div style=\"font-size:20px;font-weight:700;\">" + safeName + "</div></div>"
                + "<div style=\"padding:24px 20px;font-size:15px;line-height:1.6;word-break:break-word;\">" + content + "</div>"
                + "</div></div></body></html>";
    }

    private String escapeHtml(String value) {
        return value == null ? "" : value.replace("&", "&amp;")
                .replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
