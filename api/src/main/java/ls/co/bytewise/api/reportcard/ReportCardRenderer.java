package ls.co.bytewise.api.reportcard;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Locale;

@Component
public class ReportCardRenderer {

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

    public static byte[] render(ReportCardDto dto) {
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("card", dto);

        if (dto.getSchoolLogo() != null && dto.getSchoolLogo().length > 0) {
            String b64 = Base64.getEncoder().encodeToString(dto.getSchoolLogo());
            ctx.setVariable("logoDataUri",
                    "data:" + dto.getSchoolLogoMimeType() + ";base64," + b64);
        }

        String html = TEMPLATE_ENGINE.process("report-card", ctx);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new ReportCardExceptions.ReportCardRenderException(
                    "Failed to render report card PDF", e);
        }
    }
}