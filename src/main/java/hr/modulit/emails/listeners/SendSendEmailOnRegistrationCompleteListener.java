package hr.modulit.emails.listeners;

import hr.modulit.emails.events.SendEmailOnRegistrationCompleteEvent;
import hr.modulit.persistence.models.Account;
import hr.modulit.services.AccountService;
import hr.modulit.services.EmailSendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.UUID;

public class SendSendEmailOnRegistrationCompleteListener implements ApplicationListener<SendEmailOnRegistrationCompleteEvent> {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailSendingService emailSendingService;

    @Override
    public void onApplicationEvent(final SendEmailOnRegistrationCompleteEvent event) {
        confirmRegistration(event);
    }

    private void confirmRegistration(final SendEmailOnRegistrationCompleteEvent event) {
        final Account account = event.getAccount();
        final String token = UUID.randomUUID().toString();
        accountService.createVerificationToken(account, token);
        final String additionalContent = " \r\n" + getConfirmationUrl(event.getAppUrl(), token);
        emailSendingService.sendEmailMessage(account.getEmail(), "registration", additionalContent);
    }

    private String getConfirmationUrl(String appUrl, String token) {
        return appUrl + "/registrationConfirm.html?token=" + token;
    }

}
