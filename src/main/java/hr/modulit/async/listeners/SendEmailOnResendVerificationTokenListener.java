package hr.modulit.async.listeners;

import hr.modulit.async.events.SendEmailOnResendVerificationTokenEvent;
import hr.modulit.persistence.models.Account;
import hr.modulit.persistence.models.Token;
import hr.modulit.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class SendEmailOnResendVerificationTokenListener implements ApplicationListener<SendEmailOnResendVerificationTokenEvent> {

    @Autowired
    private Environment env;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(final SendEmailOnResendVerificationTokenEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final SendEmailOnResendVerificationTokenEvent event) {
        final Account account = event.getAccount();
        final Token newToken = accountService.generateNewVerificationToken(event.getExistingToken());

        final SimpleMailMessage email = constructEmailMessage(event.getAppUrl(), account.getEmail(), newToken.getToken());
        mailSender.send(email);
    }

    private SimpleMailMessage constructEmailMessage(final String appUrl, final String recipientEmail, final String token) {
        final String subject = messages.getMessage("mail.resendToken.subject", null, null);
        final String content = messages.getMessage("mail.resendToken.content", null, null);
        final String confirmationUrl = appUrl + "/registrationConfirm.html?token=" + token;

        return new SimpleMailMessage() {{
            setTo(recipientEmail);
            setSubject(subject);
            setText(content + " \r\n" + confirmationUrl);
            setFrom(env.getProperty("support.email"));
        }};
    }
}
