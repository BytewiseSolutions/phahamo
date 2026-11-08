package ls.co.bytewise.api.export;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.timetable.Timetable;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
public class SchoolExportRenderer {
    private static final SpringTemplateEngine ENGINE = new SpringTemplateEngine();
    static {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/"); resolver.setSuffix(".html"); resolver.setTemplateMode("XHTML");
        resolver.setCharacterEncoding("UTF-8"); resolver.setCacheable(false); ENGINE.setTemplateResolver(resolver);
    }

    public byte[] renderClassList(School school, SchoolClass schoolClass, List<Student> students, String term) {
        Context context = base(school, term);
        context.setVariable("className", schoolClass.getName()); context.setVariable("students", students);
        return render("export-class-list", context);
    }

    public byte[] renderTimetable(School school, String className, String teacherName, List<Timetable> slots, String term) {
        Context context = base(school, term);
        context.setVariable("className", className); context.setVariable("teacherName", teacherName);
        context.setVariable("slots", slots.stream().sorted(Comparator.comparing(Timetable::getDayOfWeek)
                .thenComparing(s -> s.getPeriod().getStartTime())).toList());
        return render("export-timetable", context);
    }

    private Context base(School school, String term) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("schoolName", school.getName()); context.setVariable("termName", term);
        if (school.getLogo() != null && school.getLogo().getContents().length > 0) {
            context.setVariable("logoDataUri", "data:" + school.getLogo().getType() + ";base64,"
                    + Base64.getEncoder().encodeToString(school.getLogo().getContents()));
        }
        return context;
    }

    private byte[] render(String template, Context context) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer(); renderer.setDocumentFromString(ENGINE.process(template, context));
            renderer.layout(); renderer.createPDF(out); return out.toByteArray();
        } catch (Exception e) { throw new IllegalStateException("Failed to render export PDF", e); }
    }
}
