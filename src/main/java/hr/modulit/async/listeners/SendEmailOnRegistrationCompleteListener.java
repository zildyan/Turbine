package hr.modulit.async.listeners;

import hr.modulit.async.events.SendEmailOnRegistrationCompleteEvent;
import hr.modulit.persistence.models.Account;
import hr.modulit.services.AccountService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class SendEmailOnRegistrationCompleteListener implements ApplicationListener<SendEmailOnRegistrationCompleteEvent> {

    @Autowired
    private Environment env;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(final SendEmailOnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final SendEmailOnRegistrationCompleteEvent event) {
        final Account account = event.getAccount();
        final String token = UUID.randomUUID().toString();
        accountService.createVerificationTokenForAccount(account, token);

        final SimpleMailMessage email = constructEmailMessage(event.getAppUrl(), account.getEmail(), token);
        mailSender.send(email);
    }


    private SimpleMailMessage constructEmailMessage(final String appUrl, final String recipientEmail, final String token) {
        final String subject = messages.getMessage("registration.confirmation", null, null);
        final String message = messages.getMessage("registration.confirmation.content", null, null);
        final String confirmationUrl = appUrl + "/registrationConfirm.html?token=" + token;

        return new SimpleMailMessage() {{
            setTo(recipientEmail);
            setSubject(subject);
            setText(message + " \r\n" + confirmationUrl);
            setFrom(env.getProperty("support.email"));
        }};
    }

}
