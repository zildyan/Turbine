package hr.modulit.emails.listeners;

import hr.modulit.emails.events.SendEmailOnResendVerificationTokenEvent;
import hr.modulit.persistence.models.Account;
import hr.modulit.persistence.models.Token;
import hr.modulit.services.AccountService;
import hr.modulit.services.EmailSendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class SendEmailOnResendVerificationTokenListener extends EmailSendingService implements ApplicationListener<SendEmailOnResendVerificationTokenEvent> {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailSendingService emailSendingService;

    @Override
    public void onApplicationEvent(final SendEmailOnResendVerificationTokenEvent event) {
        resendVerificationToken(event);
    }

    private void resendVerificationToken(final SendEmailOnResendVerificationTokenEvent event) {
        final Account account = event.getAccount();
        final Token newToken = accountService.generateNewVerificationToken(event.getExistingToken());
        final String additionalContent = " \r\n" + getConfirmationUrl(event.getAppUrl(), newToken.getToken());
        emailSendingService.sendEmailMessage(account.getEmail(), "resendVerificationToken", additionalContent);
    }

    private String getConfirmationUrl(String appUrl, String token) {
        return appUrl + "/registrationConfirm.html?token=" + token;
    }
}
