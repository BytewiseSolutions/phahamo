package ls.co.bytewise.api.report;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class ReportPdfRenderer {

    private static final SpringTemplateEngine TEMPLATE_ENGINE = new SpringTemplateEngine();
    private final SchoolRepository schoolRepository;

    static {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("XHTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        TEMPLATE_ENGINE.setTemplateResolver(resolver);
    }

    public ReportPdfRenderer(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public byte[] render(String templateName, Object data, String title, String termName) {
        Long schoolId = TenantContext.getSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("School not found"));

        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("rows", data);

        Map<String, String> schoolInfo = new HashMap<>();
        schoolInfo.put("name", school.getName());
        schoolInfo.put("address", school.getAddress());
        schoolInfo.put("phone", school.getContactPhone());
        schoolInfo.put("email", school.getContactEmail());
        ctx.setVariable("school", schoolInfo);

        ctx.setVariable("title", title);
        ctx.setVariable("termName", termName);

        if (school.getLogo() != null) {
            String b64 = Base64.getEncoder().encodeToString(school.getLogo().getContents());
            ctx.setVariable("logoDataUri", "data:" + school.getLogo().getType() + ";base64," + b64);
        }

        String html = TEMPLATE_ENGINE.process(templateName, ctx);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to render PDF", e);
        }
    }
}