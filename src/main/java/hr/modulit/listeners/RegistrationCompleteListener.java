package hr.modulit.listeners;

import hr.modulit.events.OnRegistrationCompleteEvent;
import hr.modulit.persistence.models.Account;
import hr.modulit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

public class RegistrationCompleteListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final OnRegistrationCompleteEvent event) {
        final Account account = event.getAccount();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(account, token);

        final SimpleMailMessage email = constructEmailMessage(event, account, token);
        mailSender.send(email);
    }


    private SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final Account account, final String token) {
        final String recipientAddress = account.getEmail();
        final String subject = messages.getMessage("registration.confirmation", null, null);
        final String message = messages.getMessage("registration.confirmation.content", null, null);
        final String confirmationUrl = event.getConfirmationUrl() + "/registrationConfirm.html?token=" + token;

        return new SimpleMailMessage() {{
            setTo(recipientAddress);
            setSubject(subject);
            setText(message + " \r\n" + confirmationUrl);
            setFrom(env.getProperty("support.email"));
        }};
    }

}
