package hr.modulit.emails.listeners;

import hr.modulit.emails.events.SendEmailOnForgottenPasswordEvent;
import hr.modulit.persistence.models.Account;
import hr.modulit.services.AccountService;
import hr.modulit.services.EmailSendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.UUID;

public class SendEmailOnForgottenPasswordListener implements ApplicationListener<SendEmailOnForgottenPasswordEvent> {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailSendingService emailSendingService;

    @Override
    public void onApplicationEvent(SendEmailOnForgottenPasswordEvent event) {
        resendForgottenPassword(event);
    }

    private void resendForgottenPassword(SendEmailOnForgottenPasswordEvent event) {
        final Account account = event.getAccount();
        final String token = UUID.randomUUID().toString();
        accountService.createPasswordToken(account, token);
        final String additionalContent =  " \r\n" + getConfirmationUrl(event.getAppUrl(), account.getId().toString(), token);
        emailSendingService.sendEmailMessage(account.getEmail(), "forgottenPassword", additionalContent);
    }

    private String getConfirmationUrl(String appUrl, String id, String token) {
        return appUrl + "/forgottenPassword.html?id=" + id + "&token=" + token;
    }
}
