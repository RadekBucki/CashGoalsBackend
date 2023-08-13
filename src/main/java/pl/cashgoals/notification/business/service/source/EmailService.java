package pl.cashgoals.notification.business.service.source;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import pl.cashgoals.notification.business.model.Notification;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements SendMessageService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.sender}")
    private String sender;

    public void send(Notification notification) {
        Context context = new Context(notification.getLocale());

        Map<String, Object> variables = new HashMap<>(notification.getVariables());
        variables.put("user", notification.getUser());
        context.setVariables(Collections.unmodifiableMap(variables));

        try {
            String body = templateEngine.process(notification.getTemplate().getName(), context);
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setSubject(getSubject(notification));
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(sender);
            helper.setTo(notification.getUser().getEmail());
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (RuntimeException | MessagingException e) {
            log.error("Error while sending email" + e.getMessage(), notification);
            throw new AmqpException(e);
        }
    }

    private static String getSubject(Notification notification) {
        String subject = ResourceBundle.getBundle("email/Subjects", notification.getLocale())
                .getString(notification.getTemplate().getName());
        for (Map.Entry<String, String> entry : notification.getVariables().entrySet()) {
            subject = subject.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return subject;
    }
}
