package sumdu.edu.ua.web.mail;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import sumdu.edu.ua.core.domain.Book;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JavaMailSender mailSender;
    private final EmailTemplateProcessor templateProcessor;

    @Value("${app.mail.recipient:your_email@gmail.com}")
    private String recipientEmail;

    @Value("${app.mail.from:bookapp@erlkonig.sumdu.edu.ua}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    public MailService(JavaMailSender mailSender,
                       EmailTemplateProcessor templateProcessor) {
        this.mailSender = mailSender;
        this.templateProcessor = templateProcessor;
    }

    public String renderNewBookHtml(Book book) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", book.getTitle());
        model.put("author", book.getAuthor());
        model.put("year", book.getYear());
        model.put("added", LocalDateTime.now().format(DATE_FORMATTER));

        return templateProcessor.process("new_book.ftl", model);
    }

    public void sendNewBookEmail(Book book) {
        String html = renderNewBookHtml(book);
        log.info("Rendered FreeMarker HTML email for book '{}' (year={}, rare={})",
                book.getTitle(), book.getYear(), book.getYear() != null && book.getYear() < 2000);

        if (!mailEnabled) {
            log.info("SMTP sending disabled (app.mail.enabled=false). Preview rendered HTML length={} chars", html.length());
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("Нова книга в каталозі: " + book.getTitle());
            helper.setText(html, true);
            helper.setFrom(fromEmail);

            mailSender.send(message);
            log.info("HTML email successfully sent via Gmail SMTP to '{}' for book '{}'", recipientEmail, book.getTitle());
        } catch (Exception e) {
            log.error("SMTP email sending failed for book '{}' (recipient='{}'): {}",
                    book.getTitle(), recipientEmail, e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
