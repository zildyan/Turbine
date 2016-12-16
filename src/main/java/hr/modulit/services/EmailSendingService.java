package hr.modulit.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSendingService {

    @Autowired
    private Environment env;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailMessage(final String recipientEmail, String messageResourcePrefix, String additionalContent) {
        final String subject = getMessage(messageResourcePrefix + ".subject");
        final String content = getMessage(messageResourcePrefix + ".content");

        SimpleMailMessage email = new SimpleMailMessage() {{
            setTo(recipientEmail);
            setSubject(subject);
            setText(content + additionalContent);
            setFrom(env.getProperty("support.email"));
        }};

        mailSender.send(email);
    }

    private String getMessage(String messageKey) {
        return messages.getMessage(messageKey, null, null);
    }
}
