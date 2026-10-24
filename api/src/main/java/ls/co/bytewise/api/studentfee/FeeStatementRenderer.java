package ls.co.bytewise.api.studentfee;

import ls.co.bytewise.api.school.School;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Locale;

@Component
public class FeeStatementRenderer {

    private static final SpringTemplateEngine TEMPLATE_ENGINE = new SpringTemplateEngine();

    static {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("XHTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        TEMPLATE_ENGINE.setTemplateResolver(resolver);
    }

    public static byte[] render(StudentFeeAccountDto account, School school, LocalDate generatedDate) {
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("account", account);
        ctx.setVariable("school", school);
        ctx.setVariable("generatedDate", generatedDate);

        if (school.getLogo() != null && school.getLogo().getContents() != null) {
            String b64 = Base64.getEncoder().encodeToString(school.getLogo().getContents());
            ctx.setVariable("logoDataUri",
                    "data:" + school.getLogo().getType() + ";base64," + b64);
        }

        String html = TEMPLATE_ENGINE.process("fee-statement", ctx);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new StudentFeeExceptions.InvalidFeeOperationException(
                    "Failed to render fee statement: " + e.getMessage());
        }
    }
}